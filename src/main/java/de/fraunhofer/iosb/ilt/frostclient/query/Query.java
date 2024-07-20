/*
 * Copyright (C) 2023 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
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
import de.fraunhofer.iosb.ilt.frostclient.utils.MqttSubscription;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import de.fraunhofer.iosb.ilt.frostclient.utils.Utils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
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
public class Query implements QueryRequest<Query>, QueryParameter {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Query.class);
    private final SensorThingsService service;
    private final EntityType entityType;
    private final Entity parent;
    private final NavigationPropertyEntitySet navigationLink;
    private final List<NameValuePair> params = new ArrayList<>();

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

    private void removeAllParams(String key) {
        for (Iterator<NameValuePair> it = params.iterator(); it.hasNext();) {
            NameValuePair param = it.next();
            if (param.getName().equals(key)) {
                it.remove();
                break;
            }
        }
    }

    @Override
    public Query filter(String options) {
        removeAllParams("$filter");
        if (!StringHelper.isNullOrEmpty(options)) {
            params.add(new BasicNameValuePair("$filter", options));
        }
        return this;
    }

    @Override
    public Query top(int n) {
        removeAllParams("$top");
        params.add(new BasicNameValuePair("$top", Integer.toString(n)));
        return this;
    }

    @Override
    public Query orderBy(String clause) {
        removeAllParams("$orderby");
        if (!StringHelper.isNullOrEmpty(clause)) {
            params.add(new BasicNameValuePair("$orderby", clause));
        }
        return this;
    }

    @Override
    public Query skip(int n) {
        removeAllParams("$skip");
        if (n > 0) {
            params.add(new BasicNameValuePair("$skip", Integer.toString(n)));
        }
        return this;
    }

    @Override
    public Query count() {
        removeAllParams("$count");
        params.add(new BasicNameValuePair("$count", "true"));
        return this;
    }

    public Query expand(String expansion) {
        removeAllParams("$expand");
        if (!StringHelper.isNullOrEmpty(expansion)) {
            params.add(new BasicNameValuePair("$expand", expansion));
        }
        return this;
    }

    public Query select(String... fields) {
        removeAllParams("$select");
        if (fields == null) {
            return this;
        }
        StringBuilder selectValue = new StringBuilder();
        for (String field : fields) {
            selectValue.append(field).append(",");
        }
        if (selectValue.length() == 0) {
            return this;
        }
        String select = selectValue.substring(0, selectValue.length() - 1);
        if (select.isEmpty()) {
            return this;
        }
        params.add(new BasicNameValuePair("$select", select));
        return this;
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
        HttpGet httpGet;
        try {
            URIBuilder uriBuilder;
            if (parent == null) {
                uriBuilder = new URIBuilder(service.getFullPath(entityType).toURI());
            } else {
                uriBuilder = new URIBuilder(service.getFullPath(parent, navigationLink).toURI());
            }

            uriBuilder.addParameters(params);
            httpGet = new HttpGet(uriBuilder.build());
        } catch (URISyntaxException ex) {
            throw new ServiceFailureException("Failed to fetch entities from query.", ex);
        }

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
        for (NameValuePair param : params) {
            final String paramName = param.getName();
            switch (paramName) {
                case "$expand":
                    if (service.getServerInfo().isMqttExpandAllowed()) {
                        addParamToTopic(collectedParams, param);
                    }
                    break;

                case "$filter":
                    if (service.getServerInfo().isMqttFilterAllowed()) {
                        addParamToTopic(collectedParams, param);
                    }
                    break;

                case "$select":
                    addParamToTopic(collectedParams, param);
                    break;

                default:
                    LOGGER.debug("Ignoring parameter {} for MQTT", paramName);
            }
        }
        topic.append(collectedParams);
        sub.setTopic(topic.toString())
                .setReturnType(tt);
        service.subscribe(sub);
        return this;
    }

    public void addParamToTopic(StringBuilder collectedParam, NameValuePair param) {
        if (collectedParam.isEmpty()) {
            collectedParam.append('?').append(param.getName()).append('=').append(param.getValue());
        } else {
            collectedParam.append('&').append(param.getName()).append('=').append(param.getValue());
        }
    }

    public void delete() throws ServiceFailureException {
        removeAllParams("$top");
        removeAllParams("$skip");
        removeAllParams("$count");
        removeAllParams("$select");
        removeAllParams("$expand");

        HttpDelete httpDelete;
        try {
            URIBuilder uriBuilder;
            if (parent != null) {
                uriBuilder = new URIBuilder(service.getFullPath(parent, navigationLink).toURI());
            } else {
                uriBuilder = new URIBuilder(service.getFullPath(entityType).toURI());
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
