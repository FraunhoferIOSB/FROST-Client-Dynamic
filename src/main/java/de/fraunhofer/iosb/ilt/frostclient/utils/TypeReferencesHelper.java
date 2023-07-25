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
package de.fraunhofer.iosb.ilt.frostclient.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.model.ext.TimeInterval;
import de.fraunhofer.iosb.ilt.frostclient.model.ext.TimeValue;
import de.fraunhofer.iosb.ilt.frostclient.model.ext.UnitOfMeasurement;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.geojson.GeoJsonObject;
import org.slf4j.LoggerFactory;

/**
 * Various re-usable TypeReference definitions.
 */
public class TypeReferencesHelper {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TypeReferencesHelper.class.getName());

    public static final TypeReference<BigDecimal> TYPE_REFERENCE_BIGDECIMAL = new TypeReference<BigDecimal>() {
        // Empty on purpose.
    };
    public static final TypeReference<Boolean> TYPE_REFERENCE_BOOLEAN = new TypeReference<Boolean>() {
        // Empty on purpose.
    };
    public static final TypeReference<Date> TYPE_REFERENCE_DATE = new TypeReference<Date>() {
        // Empty on purpose.
    };
    public static final TypeReference<Duration> TYPE_REFERENCE_DURATION = new TypeReference<Duration>() {
        // Empty on purpose.
    };
    public static final TypeReference<Entity> TYPE_REFERENCE_ENTITY = new TypeReference<Entity>() {
        // Empty on purpose.
    };
    public static final TypeReference<EntitySet> TYPE_REFERENCE_ENTITYSET = new TypeReference<EntitySet>() {
        // Empty on purpose.
    };
    public static final TypeReference<GeoJsonObject> TYPE_REFERENCE_GEOJSONOBJECT = new TypeReference<GeoJsonObject>() {
        // Empty on purpose.
    };
    public static final TypeReference<Integer> TYPE_REFERENCE_INTEGER = new TypeReference<Integer>() {
        // Empty on purpose.
    };
    public static final TypeReference<List<String>> TYPE_REFERENCE_LIST_STRING = new TypeReference<List<String>>() {
        // Empty on purpose.
    };
    public static final TypeReference<Long> TYPE_REFERENCE_LONG = new TypeReference<Long>() {
        // Empty on purpose.
    };
    public static final TypeReference<List<UnitOfMeasurement>> TYPE_REFERENCE_LIST_UOM = new TypeReference<List<UnitOfMeasurement>>() {
        // Empty on purpose.
    };
    public static final TypeReference<Map<String, Object>> TYPE_REFERENCE_MAP = new TypeReference<Map<String, Object>>() {
        // Empty on purpose.
    };
    public static final TypeReference<MapValue> TYPE_REFERENCE_MAPVALUE = new TypeReference<MapValue>() {
        // Empty on purpose.
    };
    public static final TypeReference<Number> TYPE_REFERENCE_NUMBER = new TypeReference<Number>() {
        // Empty on purpose.
    };
    public static final TypeReference<Object> TYPE_REFERENCE_OBJECT = new TypeReference<Object>() {
        // Empty on purpose.
    };
    public static final TypeReference<String> TYPE_REFERENCE_STRING = new TypeReference<String>() {
        // Empty on purpose.
    };
    public static final TypeReference<TimeInstant> TYPE_REFERENCE_TIMEINSTANT = new TypeReference<TimeInstant>() {
        // Empty on purpose.
    };
    public static final TypeReference<TimeInterval> TYPE_REFERENCE_TIMEINTERVAL = new TypeReference<TimeInterval>() {
        // Empty on purpose.
    };
    public static final TypeReference<TimeValue> TYPE_REFERENCE_TIMEVALUE = new TypeReference<TimeValue>() {
        // Empty on purpose.
    };
    public static final TypeReference<UnitOfMeasurement> TYPE_REFERENCE_UOM = new TypeReference<UnitOfMeasurement>() {
        // Empty on purpose.
    };
    public static final TypeReference<UUID> TYPE_REFERENCE_UUID = new TypeReference<UUID>() {
        // Empty on purpose.
    };

    private static final Map<String, TypeReference> REFERENCES = new HashMap<>();

    static {
        for (Field field : FieldUtils.getAllFields(TypeReferencesHelper.class)) {
            try {
                TypeReference referenceValue = (TypeReference) FieldUtils.readStaticField(field, false);
                final String referenceName = field.getName().substring(15).toLowerCase();
                REFERENCES.put(referenceName, referenceValue);
                LOGGER.debug("Registered type: {}", referenceName);
            } catch (IllegalArgumentException ex) {
                LOGGER.error("Failed to initialise: {}", field, ex);
            } catch (IllegalAccessException ex) {
                LOGGER.trace("Failed to initialise: {}", field, ex);
            }
        }
    }

    private TypeReferencesHelper() {
        // Utility class
    }

    public static TypeReference getTypeReference(String name) {
        return REFERENCES.get(name.toLowerCase());
    }
}
