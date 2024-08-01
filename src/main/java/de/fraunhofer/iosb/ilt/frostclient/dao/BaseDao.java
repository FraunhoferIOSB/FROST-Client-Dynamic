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
package de.fraunhofer.iosb.ilt.frostclient.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchOperation;
import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.json.serialize.JsonWriter;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.query.Query;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import de.fraunhofer.iosb.ilt.frostclient.utils.Utils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The implementation of a data access object.
 */
public class BaseDao implements Dao {

    public static final ContentType APPLICATION_JSON_PATCH = ContentType.create("application/json-patch+json", Consts.UTF_8);
    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDao.class);
    private final SensorThingsService service;
    private final EntityType entityType;
    private final Entity parent;
    private final NavigationPropertyEntitySet navigationLink;

    /**
     * Constructor.
     *
     * @param service the service to operate on
     * @param entityType the type of entity to deal with.
     */
    public BaseDao(SensorThingsService service, EntityType entityType) {
        this.service = service;
        this.entityType = entityType;
        this.parent = null;
        this.navigationLink = null;
    }

    public BaseDao(SensorThingsService service, Entity parent, NavigationPropertyEntitySet navigationLink) {
        this.service = service;
        this.entityType = navigationLink.getEntityType();
        this.parent = parent;
        this.navigationLink = navigationLink;
        if (!parent.getEntityType().getNavigationSets().contains(navigationLink)) {
            throw new IllegalArgumentException("Entities of type " + entityType + " don't have a navigationProperty " + navigationLink);
        }
    }

    private URL getSetPath() throws ServiceFailureException {
        if (parent == null) {
            return service.getFullPath(entityType);
        }
        return service.getFullPath(parent, navigationLink);
    }

    @Override
    public void create(Entity entity) throws ServiceFailureException {
        URIBuilder uriBuilder;
        String json;
        HttpPost httpPost;
        try {
            uriBuilder = new URIBuilder(getSetPath().toURI());
            json = JsonWriter.writeEntity(entity);
            httpPost = new HttpPost(uriBuilder.build());
        } catch (URISyntaxException | JsonProcessingException ex) {
            throw new ServiceFailureException("Failed to create entity.", ex);
        }

        LOGGER.debug("Posting to: {}", httpPost.getURI());
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = service.execute(httpPost)) {
            Utils.throwIfNotOk(httpPost, response);
            Header locationHeader = response.getLastHeader("location");
            EntityUtils.consumeQuietly(response.getEntity());
            if (locationHeader == null) {
                throw new IllegalStateException("Server did not send a location header for the new entitiy.");
            }
            String newLocation = locationHeader.getValue();
            int pos1 = newLocation.indexOf('(') + 1;
            int pos2 = newLocation.indexOf(')', pos1);
            String stringPkValue = newLocation.substring(pos1, pos2);
            entity.setPrimaryKeyValues(ParserUtils.tryToParse(stringPkValue));
            entity.setService(service);
        } catch (IOException exc) {
            throw new ServiceFailureException("Failed to create entity.", exc);
        }

    }

    @Override
    public Entity find(Object... pkValues) throws ServiceFailureException {
        try {
            URI uri = buildUri(pkValues);
            return find(uri);
        } catch (URISyntaxException ex) {
            throw new ServiceFailureException(ex);
        }
    }

    @Override
    public Entity find(Entity parent, NavigationPropertyEntity npe) throws ServiceFailureException {
        if (!parent.getEntityType().getNavigationEntities().contains(npe)) {
            throw new IllegalArgumentException("Entities of type " + parent + " don't have nav prop " + npe);
        }
        try {
            return find(service.getFullPath(parent, npe).toURI());
        } catch (URISyntaxException ex) {
            throw new ServiceFailureException(ex);
        }
    }

    @Override
    public Entity find(URI uri) throws ServiceFailureException {
        HttpGet httpGet = new HttpGet(uri);
        LOGGER.debug("Fetching: {}", uri);
        httpGet.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());

        try (CloseableHttpResponse response = service.execute(httpGet)) {
            Utils.throwIfNotOk(httpGet, response);
            String returnContent = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            Entity entity = service.getJsonReader().parseEntity(entityType, returnContent);
            entity.setService(service);
            return entity;
        } catch (IOException | ParseException ex) {
            throw new ServiceFailureException(ex);
        }
    }

    @Override
    public void update(Entity entity) throws ServiceFailureException {
        HttpPatch httpPatch;
        String json;
        try {
            final URI uri = buildUri(entity.getPrimaryKeyValues());
            json = JsonWriter.writeEntity(entity);
            httpPatch = new HttpPatch(uri);
        } catch (JsonProcessingException | URISyntaxException ex) {
            throw new ServiceFailureException(ex);
        }

        LOGGER.debug("Patching: {}", httpPatch.getURI());
        httpPatch.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = service.execute(httpPatch)) {
            Utils.throwIfNotOk(httpPatch, response);
            EntityUtils.consumeQuietly(response.getEntity());
        } catch (IOException ex) {
            throw new ServiceFailureException(ex);
        }
    }

    @Override
    public void patch(Entity entity, List<JsonPatchOperation> patch) throws ServiceFailureException {
        HttpPatch httpPatch;
        String json;
        try {
            final URI uri = buildUri(entity.getPrimaryKeyValues());
            json = JsonWriter.writeObject(patch);
            httpPatch = new HttpPatch(uri);
        } catch (URISyntaxException | JsonProcessingException ex) {
            throw new ServiceFailureException(ex);
        }

        LOGGER.debug("Patching: {} with patch {}", httpPatch.getURI(), patch);
        httpPatch.setEntity(new StringEntity(json, APPLICATION_JSON_PATCH));

        try (CloseableHttpResponse response = service.execute(httpPatch)) {
            Utils.throwIfNotOk(httpPatch, response);
            EntityUtils.consumeQuietly(response.getEntity());
        } catch (IOException ex) {
            throw new ServiceFailureException(ex);
        }
    }

    @Override
    public void delete(Entity entity) throws ServiceFailureException {
        HttpDelete httpDelete;
        try {
            final URI uri = buildUri(entity.getPrimaryKeyValues());
            httpDelete = new HttpDelete(uri);
        } catch (URISyntaxException ex) {
            throw new ServiceFailureException(ex);
        }
        LOGGER.debug("Deleting: {}", httpDelete.getURI());

        try (CloseableHttpResponse response = service.execute(httpDelete)) {
            Utils.throwIfNotOk(httpDelete, response);
            EntityUtils.consumeQuietly(response.getEntity());
        } catch (IOException ex) {
            throw new ServiceFailureException(ex);
        }
    }

    @Override
    public Query query() {
        if (parent == null) {
            return new Query(service, entityType);
        }
        return new Query(service, parent, navigationLink);
    }

    private URI buildUri(Object[] pkValues) throws NotImplementedException, URISyntaxException, ServiceFailureException {
        URIBuilder uriBuilder;
        if (pkValues.length == 1) {
            uriBuilder = new URIBuilder(getSetPath() + "(" + ParserUtils.formatKeyValuesForUrl(pkValues[0]) + ")");
        } else {
            throw new NotImplementedException("Multi-valued primary keys are not supported yet.");
        }
        final URI uri = uriBuilder.build();
        return uri;
    }

    protected SensorThingsService getService() {
        return service;
    }

}
