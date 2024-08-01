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
package de.fraunhofer.iosb.ilt.frostclient.model;

import static de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper.cleanForLogging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.StatusCodeException;
import de.fraunhofer.iosb.ilt.frostclient.json.deserialize.JsonReader;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import de.fraunhofer.iosb.ilt.frostclient.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.http.Consts;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of EntitySet interface.
 */
public class EntitySetImpl implements EntitySet {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntitySetImpl.class.getName());

    protected List<Entity> data;
    protected long count = -1;
    protected String nextLink;

    @JsonIgnore
    private final EntityType type;
    @JsonIgnore
    private NavigationPropertyEntitySet navigationProperty;
    @JsonIgnore
    private SensorThingsService service;

    public EntitySetImpl(EntityType type) {
        this.data = new ArrayList<>();
        this.type = type;
    }

    public EntitySetImpl(NavigationPropertyEntitySet navigationProperty) {
        this.data = new ArrayList<>();
        this.type = navigationProperty.getEntityType();
        this.navigationProperty = navigationProperty;
    }

    @Override
    public List<Entity> toList() {
        return data;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public Iterator<Entity> iterator() {
        return new Iterator<Entity>() {
            private Iterator<Entity> currentIterator = data.iterator();

            private void fetchNextList() {
                if (nextLink == null) {
                    currentIterator = null;
                    return;
                }
                fetchNext();
                currentIterator = data.iterator();
            }

            @Override
            public boolean hasNext() {
                if (currentIterator == null) {
                    return false;
                }
                if (currentIterator.hasNext()) {
                    return true;
                }
                fetchNextList();
                return hasNext();
            }

            @Override
            public Entity next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return currentIterator.next();
            }
        };
    }

    @Override
    public void fetchNext() {
        if (nextLink == null) {
            data = Collections.emptyList();
            return;
        }
        HttpGet httpGet = new HttpGet(nextLink);
        httpGet.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        LOGGER.debug("Fetching: {}", httpGet.getURI());
        try (CloseableHttpResponse response = service.execute(httpGet)) {
            Utils.throwIfNotOk(httpGet, response);
            String json = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            JsonReader reader = new JsonReader(service.getModelRegistry());
            EntitySet nextSet = reader.parseEntitySet(type, json);
            nextSet.setService(service);
            data = nextSet.toList();
            nextLink = nextSet.getNextLink();
        } catch (IOException | ParseException exc) {
            LOGGER.error("Failed deserializing collection.", exc);
            nextLink = null;
            data = new ArrayList<>();
        } catch (StatusCodeException exc) {
            LOGGER.error("Failed follow nextlink: {} - '{}' - {}", exc.getStatusCode(), nextLink, cleanForLogging(exc.getReturnedContent(), 100));
            LOGGER.debug("Response: {}", exc.getReturnedContent());
            nextLink = null;
            data = new ArrayList<>();
        }
    }

    @Override
    public void add(Entity e) {
        data.add(e);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, navigationProperty);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EntitySetImpl other = (EntitySetImpl) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return Objects.equals(this.navigationProperty, other.navigationProperty);
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public boolean hasNextLink() {
        return !StringHelper.isNullOrEmpty(nextLink);
    }

    @Override
    public String getNextLink() {
        return nextLink;
    }

    @Override
    public void setNextLink(String nextLink) {
        this.nextLink = nextLink;
    }

    @Override
    public EntityType getEntityType() {
        return type;
    }

    @Override
    public NavigationPropertyEntitySet getNavigationProperty() {
        return navigationProperty;
    }

    public EntitySetImpl setNavigationProperty(NavigationPropertyEntitySet navigationProperty) {
        this.navigationProperty = navigationProperty;
        return this;
    }

    @Override
    public SensorThingsService getService() {
        return service;
    }

    @Override
    public void setService(SensorThingsService service) {
        this.service = service;
        for (Entity entity : data) {
            entity.setService(service);
        }
    }
}
