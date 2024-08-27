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
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.UnitOfMeasurement;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for creating entities, if they don't exist yet, and returning
 * the existing entity if they do.
 */
public final class FrostUtils {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FrostUtils.class);

    private static final int DEFAULT_MAX_PROPERTIES_DEPTH = 5;

    public static final ZoneId ZONE_ID_Z = ZoneId.of("Z");

    /**
     * The NULL unit to use for "empty" units.
     */
    public static final UnitOfMeasurement NULL_UNIT = new UnitOfMeasurement(null, null, null);

    /**
     * The encoding type for GeoJSON.
     */
    public static final String ENCODING_GEOJSON = "application/geo+json";

    /**
     * The content type for GeoJSON.
     */
    public static final String CONTENT_TYPE_GEOJSON = ENCODING_GEOJSON;

    private final SensorThingsService service;

    private boolean dryRun;

    private int countInsert;
    private int countUpdate;

    public FrostUtils(final SensorThingsService service) {
        this.service = service;
    }

    public FrostUtils setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;
        return this;
    }

    public SensorThingsService getService() {
        return service;
    }

    public void update(final Entity entity) throws ServiceFailureException {
        if (dryRun) {
            LOGGER.info("Dry Run: Not updating entity {}", entity);
        } else {
            service.update(entity);
            countUpdate++;
        }
    }

    public void create(final Entity entity) throws ServiceFailureException {
        if (dryRun) {
            LOGGER.info("Dry Run: Not creating entity {}", entity);
        } else {
            service.create(entity);
            countInsert++;
        }
    }

    public int getCountInsert() {
        return countInsert;
    }

    public int getCountUpdate() {
        return countUpdate;
    }

    public void resetCounts() {
        countInsert = 0;
        countUpdate = 0;
    }

    public Entity findOrCreate(final Entity newEntity, EntityCacheDynamic cache) throws ServiceFailureException {
        Entity cachedEntity = cache.getOrLoad(newEntity);
        if (cachedEntity == null) {
            create(newEntity);
            cache.put(newEntity);
            LOGGER.info("Created {}: {}", newEntity.getEntityType(), cache.localIdFor(newEntity));
            return newEntity;
        }

        // TODO: Check if the entity is correct.
        return cachedEntity;
    }

    /**
     * Checks if all entries in source exist in target, with the same value. If
     * not, target is updated and true is returned. Sub-maps are recursed.
     *
     * @param target the target map to update
     * @param source the source map to get values from
     * @param maxDepth The maximum depth to recurse.
     * @return true if target was updated, false if not.
     */
    public static boolean addProperties(final Map<String, Object> target, final Map<String, Object> source, final int maxDepth) {
        if (target == null) {
            return false;
        }

        boolean updated = false;
        for (final Map.Entry<String, Object> entry : source.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if ((value == null || String.valueOf(value).isEmpty()) && !target.containsKey(key)) {
                continue;
            }
            if (!target.containsKey(key)) {
                target.put(key, value);
                updated = true;
            } else {
                final Object tValue = target.get(key);
                if (value instanceof final Map valueMap) {
                    if (maxDepth > 0) {
                        if (tValue instanceof final Map tValueMap) {
                            updated = updated || addProperties(tValueMap, valueMap, maxDepth - 1);
                        } else {
                            target.put(key, value);
                            updated = true;
                        }
                    }
                } else if (!resultCompare(value, tValue)) {
                    target.put(key, value);
                    updated = true;
                }

            }
        }
        return updated;
    }

    private static boolean resultCompare(final Object one, final Object two) {
        if (one == null) {
            return two == null;
        }
        if (two == null) {
            return false;
        }
        if (one.equals(two)) {
            return true;
        }

        try {
            if (one instanceof final Long longOne && two instanceof final Integer intTwo) {
                return longOne.equals(Long.valueOf(intTwo));
            }
            if (two instanceof final Long longTwo && one instanceof final Integer intOne) {
                return longTwo.equals(Long.valueOf(intOne));
            }
            if (one instanceof final BigDecimal decOne) {
                return decOne.compareTo(new BigDecimal(two.toString())) == 0;
            }
            if (two instanceof final BigDecimal decTwo) {
                return decTwo.compareTo(new BigDecimal(one.toString())) == 0;
            }
            if (one instanceof final BigInteger bigIntOne) {
                return bigIntOne.equals(new BigInteger(two.toString()));
            }
            if (two instanceof final BigInteger bigIntTwo) {
                return bigIntTwo.equals(new BigInteger(one.toString()));
            }
            if (one instanceof final Collection cOne && two instanceof final Collection cTwo) {
                final Iterator iTwo = cTwo.iterator();
                for (final Object itemOne : cOne) {
                    if (!iTwo.hasNext() || !resultCompare(itemOne, iTwo.next())) {
                        // Collection one is longer than two
                        return false;
                    }
                }
                return !iTwo.hasNext();
            }
        } catch (final NumberFormatException exc) {
            LOGGER.trace("Not both bigdecimal.", exc);
            // not both bigDecimal.
        }
        return false;
    }

}
