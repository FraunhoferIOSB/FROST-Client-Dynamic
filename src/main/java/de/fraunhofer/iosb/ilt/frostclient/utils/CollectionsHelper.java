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

import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods for dealing with Maps.
 */
public class CollectionsHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionsHelper.class);

    private CollectionsHelper() {
        // Utility class
    }

    public static void setOn(final Map<String, Object> map, final String path, final Object value) {
        setOn(map, Arrays.asList(StringUtils.split(path, '/')), value);
    }

    public static void setOn(final Map<String, Object> map, final List<String> path, final Object value) {
        setOn(map, path, 0, value);
    }

    public static void setOn(final Map<String, Object> map, final List<String> path, final int idx, final Object value) {
        final String key = path.get(idx);
        if (idx == path.size() - 1) {
            map.put(key, value);
            return;
        }
        Object subEntry = map.get(key);
        if (subEntry == null) {
            Map<String, Object> subMap = new HashMap<>();
            map.put(key, subMap);
            setOn(subMap, path, idx + 1, value);
            return;
        }
        if (subEntry instanceof Map) {
            setOn((Map) subEntry, path, idx + 1, value);
            return;
        }
        if (subEntry instanceof List) {
            throw new IllegalArgumentException("Item at path element " + key + " is a list.");
        }
        throw new IllegalArgumentException("Element at path index " + idx + " is not a map or list.");
    }

    public static Object getFrom(final List<Object> list, final List<String> path) {
        return getFrom((Object) list, path);
    }

    public static Object getFrom(final Map<String, Object> map, final String... path) {
        return getFrom((Object) map, Arrays.asList(path));
    }

    public static Object getFrom(final Map<String, Object> map, final List<String> path) {
        return getFrom((Object) map, path);
    }

    private static Object getFrom(final Object mapOrList, final List<String> path) {
        Object currentEntry = mapOrList;
        int last = path.size();
        for (int idx = 0; idx < last; idx++) {
            String key = path.get(idx);
            if (currentEntry instanceof Map) {
                currentEntry = ((Map) currentEntry).get(key);
            } else if (currentEntry instanceof List) {
                try {
                    currentEntry = ((List) currentEntry).get(Integer.parseInt(key));
                } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                    LOGGER.warn("Failed to get {} from {}.", key, currentEntry, ex);
                    return null;
                }
            }
        }
        return currentEntry;
    }

    public static PropertyBuilder propertiesBuilder() {
        return new PropertyBuilder();
    }

    public static class PropertyBuilder {

        Map<String, Object> properties = new HashMap<>();

        public PropertyBuilder addItem(final String key, final Object value) {
            properties.put(key, value);
            return this;
        }

        public PropertyBuilder addPath(final String path, final Object value) {
            setOn(properties, path, value);
            return this;
        }

        public MapValue build() {
            return new MapValue(properties);
        }

        public Map<String, Object> buildMap() {
            return properties;
        }
    }
}
