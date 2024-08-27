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

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.Property;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties;
import de.fraunhofer.iosb.ilt.frostclient.utils.EntityCacheDynamic.PropertyExtractor;
import java.util.HashMap;
import java.util.Map;

/**
 * A collection of caches for different EntityTypes for the same service.
 */
public class CacheCollection {

    private final SensorThingsService service;
    private final Map<String, EntityCacheDynamic<String>> caches = new HashMap<>();
    private String defaultLocalIdKey = "localId";

    public CacheCollection(SensorThingsService service) {
        this.service = service;
    }

    public CacheCollection setDefaultLocalIdKey(String defaultLocalIdKey) {
        this.defaultLocalIdKey = defaultLocalIdKey;
        return this;
    }

    public CacheCollection createCache(EntityType et, PropertyExtractor<String, Entity> localIdExtractor, PropertyExtractor<String, String> filterFromlocalId) {
        var ec = new EntityCacheDynamic<String>(service.dao(et))
                .setLocalIdExtractor(localIdExtractor)
                .setFilterFromlocalId(filterFromlocalId);
        caches.put(et.getEntityName(), ec);
        return this;
    }

    public CacheCollection createLocalIdCache(final EntityType et) {
        if (caches.containsKey(et.getEntityName())) {
            throw new IllegalStateException("CacheCollection already contains a cache for " + et.getEntityName());
        }
        final PropertyExtractor<String, Entity> localIdExtractor = createLocalIdExtractor(et, defaultLocalIdKey);
        final PropertyExtractor<String, String> filterFromLocalId = createFilterFromLocalId();
        var ec = new EntityCacheDynamic<String>(service.dao(et))
                .setLocalIdExtractor(localIdExtractor)
                .setFilterFromlocalId(filterFromLocalId);
        caches.put(et.getEntityName(), ec);
        return this;
    }

    public CacheCollection createNameCache(final EntityType et) {
        if (caches.containsKey(et.getEntityName())) {
            throw new IllegalStateException("CacheCollection already contains a cache for " + et.getEntityName());
        }
        final PropertyExtractor<String, Entity> nameExtractor = createNameExtractor(et);
        final PropertyExtractor<String, String> filterFromName = createFilterFromName();
        var ec = new EntityCacheDynamic<String>(service.dao(et))
                .setLocalIdExtractor(nameExtractor)
                .setFilterFromlocalId(filterFromName);
        caches.put(et.getEntityName(), ec);
        return this;
    }

    private static PropertyExtractor<String, Entity> createLocalIdExtractor(final EntityType et, String localIdKey) {
        PropertyExtractor<String, Entity> localIdExtractor;
        final Property propProperties = et.getProperty("properties");
        if (propProperties == null) {
            throw new IllegalArgumentException("Entity Type has no properties: can't create a localIdExtractor!");
        }
        localIdExtractor = (entity) -> {
            return "" + CollectionsHelper.getFrom(entity.getProperty(propProperties), localIdKey);
        };
        return localIdExtractor;
    }

    public static PropertyExtractor<String, Entity> createNameExtractor(final EntityType et) {
        PropertyExtractor<String, Entity> nameExtractor;
        final Property<String> propName = et.getProperty(CommonProperties.NAME_EP_NAME);
        if (propName == null) {
            throw new IllegalArgumentException("EntityType " + et + " has no property 'name'");
        }
        nameExtractor = (entity) -> {
            return entity.getProperty(propName);
        };
        return nameExtractor;
    }

    private PropertyExtractor<String, String> createFilterFromLocalId() {
        return localId -> "properties/" + defaultLocalIdKey + " eq " + StringHelper.quoteForUrl(localId) + "";
    }

    private PropertyExtractor<String, String> createFilterFromName() {
        return name -> "name eq " + StringHelper.quoteForUrl(name) + "";
    }

    /**
     * Returns the configured cache for the given entity type.
     *
     * @param et The entity type to get the cache for.
     * @return the existing entity cache, or null.
     */
    public EntityCacheDynamic<String> getCache(final EntityType et) {
        return caches.get(et.getEntityName());
    }

    /**
     * Returns the configured cache for the given entity type, and creates a new
     * name-based cache if no cache exist yet.
     *
     * @param et The entity type to get the cache for.
     * @return the existing entity cache, or a new name-based cache.
     */
    public EntityCacheDynamic<String> getCacheNameBased(final EntityType et) {
        final EntityCacheDynamic<String> cache = caches.get(et.getEntityName());
        if (cache == null) {
            return createNameCache(et).getCache(et);
        }
        return cache;
    }

}
