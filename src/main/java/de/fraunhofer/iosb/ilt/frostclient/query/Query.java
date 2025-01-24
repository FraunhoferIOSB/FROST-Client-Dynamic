/*
 * Copyright (C) 2024 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.fraunhofer.iosb.ilt.frostclient.query;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.MqttException;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.query.Expand.ExpandItem;
import de.fraunhofer.iosb.ilt.frostclient.utils.MqttSubscription;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import de.fraunhofer.iosb.ilt.frostclient.utils.Utils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A query for reading operations.
 */
public class Query implements QueryRequest<Query>, QueryParameter<Query> {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Query.class);
    private final SensorThingsService service;
    private final EntityType entityType;
    private final Entity parent;
    private final NavigationPropertyEntitySet navigationLink;

    private Boolean count;
    private String[] select;
    private String filter;
    private String orderby;
    private int skip = -1;
    private int top = -1;
    private String expandString;
    private Expand expand;

    public Query(SensorThingsService service, EntityType entityType) {
        this.service = service;
        this.entityType = entityType;
        this.parent = null;
        this.navigationLink = null;
    }

    public Query(SensorThingsService service, Entity parent, NavigationPropertyEntitySet navigationLink) {
        this.service = service;
        this.entityType = navigationLink.getEntityType();
        if (!parent.getEntityType().getNavigationSets().contains(navigationLink)) {
            throw new IllegalArgumentException("Entity " + parent + " has no navigationProperty " + navigationLink);
        }
        this.navigationLink = navigationLink;
        this.parent = parent;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public SensorThingsService getService() {
        return service;
    }

    @Override
    public Query filter(String filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public Query top(int top) {
        this.top = top;
        return this;
    }

    @Override
    public Query orderBy(String orderby) {
        this.orderby = orderby;
        return this;
    }

    @Override
    public Query skip(int skip) {
        this.skip = skip;
        return this;
    }

    @Override
    public Query count(boolean count) {
        this.count = count;
        return this;
    }

    public Query expand(String expansion) {
        this.expandString = expansion;
        return this;
    }

    @Override
    public Query expand(Expand expand) {
        this.expand = expand;
        if (expand != null) {
            expand.setOnType(entityType);
        }
        return this;
    }

    @Override
    public Query addExpandItem(Expand.ExpandItem item) {
        if (expand == null) {
            expand = new Expand()
                    .setOnType(entityType);
        }
        expand.addItem(item);
        return this;
    }

    private ExpandItem createExpandItem() {
        return new ExpandItem(null)
                .count(count)
                .top(top)
                .skip(skip)
                .select(select)
                .filter(filter)
                .orderBy(orderby)
                .expand(expand);
    }

    @Override
    public Query select(String... fields) {
        this.select = fields;
        return this;
    }

    public URI buildUrl() throws ServiceFailureException {
        try {
            URIBuilder uriBuilder;
            if (parent == null) {
                uriBuilder = new URIBuilder(service.getFullPath(entityType).toURI());
            } else {
                uriBuilder = new URIBuilder(service.getFullPath(parent, navigationLink).toURI());
            }
            List<NameValuePair> params = new ArrayList<>();
            if (count != null) {
                params.add(new BasicNameValuePair("$count", "true"));
            }
            if (top >= 0) {
                params.add(new BasicNameValuePair("$top", Integer.toString(top)));
            }
            if (skip > 0) {
                params.add(new BasicNameValuePair("$skip", Integer.toString(skip)));
            }
            if (!StringHelper.isNullOrEmpty(select)) {
                params.add(new BasicNameValuePair("$select", String.join(",", select)));
            }
            if (!StringHelper.isNullOrEmpty(orderby)) {
                params.add(new BasicNameValuePair("$orderby", orderby));
            }
            if (!StringHelper.isNullOrEmpty(filter)) {
                params.add(new BasicNameValuePair("$filter", filter));
            }
            if (expand != null) {
                params.add(new BasicNameValuePair("$expand", expand.toUrl()));
            } else if (!StringHelper.isNullOrEmpty(expandString)) {
                params.add(new BasicNameValuePair("$expand", expandString));
            }

            uriBuilder.addParameters(params);
            return uriBuilder.build();
        } catch (URISyntaxException ex) {
            throw new ServiceFailureException("Failed to fetch entities from query.", ex);
        }
    }

    @Override
    public Entity first() throws ServiceFailureException {
        this.top(1);
        List<Entity> asList = this.list().toList();
        if (asList.isEmpty()) {
            return null;
        }
        return asList.get(0);
    }

    @Override
    public EntitySet list() throws ServiceFailureException {
        EntitySet list;
        HttpGet httpGet = new HttpGet(buildUrl());

        LOGGER.debug("Fetching: {}", httpGet.getURI());
        httpGet.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());

        try (CloseableHttpResponse response = service.execute(httpGet)) {
            Utils.throwIfNotOk(httpGet, response);
            String json = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            list = service.getJsonReader().parseEntitySet(entityType, json);
        } catch (IOException ex) {
            throw new ServiceFailureException("Failed to fetch entities from query.", ex);
        }

        list.setService(service);
        return list;
    }

    @Override
    public Query subscribe(MqttSubscription sub) throws MqttException {
        String mqttBasePath = service.getServerInfo().getMqttBasePath();
        StringBuilder topic = new StringBuilder(mqttBasePath);
        EntityType tt;
        if (parent == null) {
            topic.append(entityType.mainSet);
            tt = entityType;
        } else {
            topic.append(ParserUtils.relationPath(parent, navigationLink));
            tt = navigationLink.getEntityType();
        }
        StringBuilder collectedParams = new StringBuilder();

        if (service.getServerInfo().isMqttExpandAllowed()) {
            if (expand != null) {
                addParamToTopic(collectedParams, "$expand", expand.toUrl());
            } else if (!StringHelper.isNullOrEmpty(expandString)) {
                addParamToTopic(collectedParams, "$expand", expandString);
            }
        }
        if (service.getServerInfo().isMqttFilterAllowed() && !StringHelper.isNullOrEmpty(filter)) {
            addParamToTopic(collectedParams, "$filter", filter);
        }
        if (!StringHelper.isNullOrEmpty(select)) {
            addParamToTopic(collectedParams, "$select", String.join(",", select));
        }

        topic.append(collectedParams);
        sub.setTopic(topic.toString())
                .setReturnType(tt);
        service.subscribe(sub);
        return this;
    }

    public void addParamToTopic(StringBuilder collectedParam, String name, String value) {
        if (collectedParam.isEmpty()) {
            collectedParam.append('?').append(name).append('=').append(value);
        } else {
            collectedParam.append('&').append(name).append('=').append(value);
        }
    }

    public void delete() throws ServiceFailureException {
        HttpDelete httpDelete;
        try {
            URIBuilder uriBuilder;
            if (parent != null) {
                uriBuilder = new URIBuilder(service.getFullPath(parent, navigationLink).toURI());
            } else {
                uriBuilder = new URIBuilder(service.getFullPath(entityType).toURI());
            }
            List<NameValuePair> params = new ArrayList<>();
            if (!StringHelper.isNullOrEmpty(filter)) {
                params.add(new BasicNameValuePair("$filter", filter));
            }
            uriBuilder.addParameters(params);
            httpDelete = new HttpDelete(uriBuilder.build());
        } catch (URISyntaxException ex) {
            throw new ServiceFailureException("Failed to delete from query.", ex);
        }

        LOGGER.debug("Deleting: {}", httpDelete.getURI());
        httpDelete.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());

        try (CloseableHttpResponse response = service.execute(httpDelete)) {
            Utils.throwIfNotOk(httpDelete, response);
            EntityUtils.consumeQuietly(response.getEntity());
        } catch (IOException ex) {
            throw new ServiceFailureException("Failed to delete from query.", ex);
        }

    }
}
