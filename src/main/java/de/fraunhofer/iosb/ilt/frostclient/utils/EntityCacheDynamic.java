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
package de.fraunhofer.iosb.ilt.frostclient.utils;

import de.fraunhofer.iosb.ilt.frostclient.dao.BaseDao;
import de.fraunhofer.iosb.ilt.frostclient.dao.Dao;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.query.Query;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <U> The type of the localId.
 */
public class EntityCacheDynamic<U> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityCacheDynamic.class.getName());

    private final Map<U, Entity> entitiesByLocalId = new LinkedHashMap<>();

    private PropertyExtractor<U, Entity> localIdExtractor;

    private PropertyExtractor<String, U> filterFromlocalId;

    private String expand;

    private int maxSize = Integer.MAX_VALUE;

    private final EntityType entityType;

    private Dao dao;

    public EntityCacheDynamic(EntityType et) {
        this.entityType = dao.getEntityType();
    }

    public EntityCacheDynamic(Dao dao) {
        this.dao = dao;
        this.entityType = dao.getEntityType();
    }

    public Entity get(U localId) {
        return entitiesByLocalId.get(localId);
    }

    public Entity get(Entity nonCached) {
        U localId = localIdExtractor.extractFrom(nonCached);
        return entitiesByLocalId.get(localId);
    }

    public Entity getOrLoad(Entity nonCached) throws ServiceFailureException {
        U localId = localIdExtractor.extractFrom(nonCached);
        return getOrLoad(localId);
    }

    /**
     * Get from the cache.If not in the cache, load using the given filter.
     *
     * @param localId The localId to get.
     * @return The requested entity, if it exists.
     * @throws ServiceFailureException if loading fails.
     */
    public Entity getOrLoad(U localId) throws ServiceFailureException {
        Entity entity = entitiesByLocalId.get(localId);
        if (entity != null) {
            return entity;
        }

        if (filterFromlocalId != null) {
            final String filter = filterFromlocalId.extractFrom(localId);
            Query query = dao.query()
                    .filter(filter);
            if (!StringHelper.isNullOrEmpty(expand)) {
                // TODO: clean up once new version is released
                query = query.expand(expand);
            }
            final List<Entity> entities = query.list()
                    .toList();
            if (entities.size() > 1) {
                throw new IllegalStateException("More than one " + entityType.entityName + " matches filter " + filter);
            }
            if (entities.size() == 1) {
                entity = entities.get(0);
            }
            if (entity != null) {
                put(localId, entity);
            }
        }
        return entity;
    }

    public boolean containsId(U localId) {
        return entitiesByLocalId.containsKey(localId);
    }

    public void put(Entity entity) {
        U localId = localIdExtractor.extractFrom(entity);
        put(localId, entity);
    }

    public void put(U localId, Entity entity) {
        if (entitiesByLocalId.size() >= maxSize) {
            clear();
        }

        entitiesByLocalId.put(localId, entity);
    }

    public void clear() {
        entitiesByLocalId.clear();
    }

    public boolean isEmpty() {
        return entitiesByLocalId.isEmpty();
    }

    public int load(BaseDao dao, String filter) throws ServiceFailureException {
        return load(dao, filter, "", "");
    }

    public int load(BaseDao dao, String filter, String select, String expand) throws ServiceFailureException {
        final Query query = dao.query();
        if (!select.isEmpty()) {
            query.select(select);
        }
        if (!expand.isEmpty()) {
            query.expand(expand);
        }
        if (!StringHelper.isNullOrEmpty(filter)) {
            query.filter(filter);
        }
        final EntitySet entities = query.top(1000).orderBy("id asc").list();
        final Iterator<Entity> iterator = entities.iterator();
        int count = 0;
        while (iterator.hasNext() && count < maxSize) {
            final Entity entity = iterator.next();
            try {
                final U localId = localIdExtractor.extractFrom(entity);
                if (localId != null) {
                    entitiesByLocalId.put(localId, entity);
                    count++;
                }
            } catch (RuntimeException ex) {
                LOGGER.debug("Failed to extract localId", ex);
            }
        }
        return count;
    }

    public int size() {
        return entitiesByLocalId.size();
    }

    public Collection<Entity> values() {
        return entitiesByLocalId.values();
    }

    public U localIdFor(Entity entity) {
        return localIdExtractor.extractFrom(entity);
    }

    public String localIdFilterFor(U localId) {
        return filterFromlocalId.extractFrom(localId);
    }

    public String localIdFilterFor(Entity entity) {
        return filterFromlocalId.extractFrom(localIdExtractor.extractFrom(entity));
    }

    public int getMaxSize() {
        return maxSize;
    }

    public EntityCacheDynamic<U> setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public EntityCacheDynamic<U> setDao(Dao dao) {
        this.dao = dao;
        return this;
    }

    public EntityCacheDynamic<U> setLocalIdExtractor(PropertyExtractor<U, Entity> localIdExtractor) {
        this.localIdExtractor = localIdExtractor;
        return this;
    }

    public EntityCacheDynamic<U> setFilterFromlocalId(PropertyExtractor<String, U> filterFromlocalId) {
        this.filterFromlocalId = filterFromlocalId;
        return this;
    }

    public EntityCacheDynamic<U> setExpand(String expand) {
        this.expand = expand;
        return this;
    }

    public static interface PropertyExtractor<U, T> {

        public U extractFrom(T input);
    }

}
