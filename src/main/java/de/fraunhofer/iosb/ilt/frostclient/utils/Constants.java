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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various String constants.
 */
public class Constants {

    public static final String OM_MEASUREMENT = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement";
    public static final String OM_COMPLEXOBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ComplexObservation";

    public static final String CONFORMANCE_STA_11_MQTT_CREATE = "http://www.opengis.net/spec/iot_sensing/1.1/req/create-observations-via-mqtt/observations-creation";
    public static final String CONFORMANCE_STA_11_MQTT_READ = "http://www.opengis.net/spec/iot_sensing/1.1/req/receive-updates-via-mqtt/receive-updates";

    public static final String CONTENT_TYPE = "content-type";
    public static final String CONTENT_TYPE_APPLICATION_GEOJSON = "application/geo+json";
    public static final String CONTENT_TYPE_APPLICATION_HTTP = "application/http";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE_APPLICATION_JSONPATCH = "application/json-patch+json";
    public static final String CONTENT_TYPE_APPLICATION_XML = "application/xml";
    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    public static final String CHARSET_UTF8 = "charset=UTF-8";

    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_LOCATION = "Location";
    public static final String HEADER_PREFER = "Prefer";

    public static final String TAG_PREFER_RETURN = "return";

    public static final String REQUEST_PARAM_FORMAT = "$format";

    public static final String VALUE_ID_TYPE_LONG = "LONG";
    public static final String VALUE_ID_TYPE_STRING = "STRING";
    public static final String VALUE_ID_TYPE_UUID = "UUID";
    public static final String VALUE_RETURN_REPRESENTATION = "representation";
    public static final String VALUE_RETURN_MINIMAL = "minimal";

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.class.getName());

    private Constants() {
        // Utility class, not to be instantiated.
    }

    /**
     * Throws an IllegalArgumentException if entity is not an instance of
     * targetClass, or if entity is null. Returns the entity so calls can be
     * chained.
     *
     * @param <T> The type of the entity (auto detected)
     * @param entity The entity to check the class of.
     * @param targetClass The class to check the entity against.
     * @return The entity to check.
     */
    public static <T> T throwIfNullOrTypeNot(Object entity, Class<T> targetClass) {
        if (entity == null || !targetClass.isAssignableFrom(entity.getClass())) {
            throw new IllegalArgumentException("Expected " + targetClass + " got " + entity);
        }
        return (T) entity;
    }

    /**
     * Throws an IllegalArgumentException if entity is not an instance of
     * targetClass. Does not throw if entity is null. Returns the entity so
     * calls can be chained.
     *
     * @param <T> The type of the entity (auto detected)
     * @param entity The entity to check the class of.
     * @param targetClass The class to check the entity against.
     * @return The entity to check.
     */
    public static <T> T throwIfTypeNot(Object entity, Class<T> targetClass) {
        if (entity != null && !targetClass.isAssignableFrom(entity.getClass())) {
            LOGGER.error("Expected {}, but got {}", targetClass, entity);
            throw new IllegalArgumentException("Expected " + targetClass + " got " + entity);
        }
        return (T) entity;
    }
}
