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
package de.fraunhofer.iosb.ilt.frostclient.settings;

import de.fraunhofer.iosb.ilt.frostclient.settings.annotation.DefaultValue;
import de.fraunhofer.iosb.ilt.frostclient.settings.annotation.DefaultValueBoolean;
import de.fraunhofer.iosb.ilt.frostclient.settings.annotation.DefaultValueInt;
import java.util.Map;
import java.util.Set;

/**
 * Interface defining default methods for working with classes with fields
 * annotated with {@link DefaultValue} or {@link DefaultValueInt}.
 *
 * @author Brian Miles, Scf
 */
public interface ConfigDefaults {

    default boolean isSensitive(String fieldValue) {
        return ConfigUtils.isSensitive(getClass(), fieldValue);
    }

    /**
     * Returns the default value of a field annotated with either
     * {@link DefaultValue} or {@link DefaultValueInt}.
     *
     * @param fieldValue The value of the annotated field
     * @return The default value of the annotated field. If there is no such a
     * field, an IllegalArgumentException is thrown.
     */
    default String defaultValue(String fieldValue) {
        return ConfigUtils.getDefaultValue(getClass(), fieldValue);
    }

    /**
     * Returns the default value of a field annotated with
     * {@link DefaultValueInt}.
     *
     * @param fieldValue The value of the annotated field
     * @return The default value of the annotated field. If there is no such a
     * field, an IllegalArgumentException is thrown.
     */
    default int defaultValueInt(String fieldValue) {
        return ConfigUtils.getDefaultValueInt(getClass(), fieldValue);
    }

    /**
     * Returns the default value of a field annotated with
     * {@link DefaultValueBoolean}.
     *
     * @param fieldValue The value of the annotated field
     * @return The default value of the annotated field. If there is no such a
     * field, an IllegalArgumentException is thrown.
     */
    default boolean defaultValueBoolean(String fieldValue) {
        return ConfigUtils.getDefaultValueBoolean(getClass(), fieldValue);
    }

    /**
     * Return a list of field names that were annotated with either
     * {@link DefaultValue} or {@link DefaultValueInt}.
     *
     * @return The list of field names so annotated.
     */
    default Set<String> configTags() {
        return ConfigUtils.getConfigTags(getClass());
    }

    /**
     * Return a mapping of config tag value and default value for any field
     * annotated with either {@link DefaultValue} or {@link DefaultValueInt}.
     *
     * @return Mapping of config tag value and default value
     */
    default Map<String, String> configDefaults() {
        return ConfigUtils.getConfigDefaults(getClass());
    }

    /**
     * Return a mapping of config tag value and default value for any field
     * annotated with {@link DefaultValueInt}.
     *
     * @return Mapping of config tag value and default value
     */
    default Map<String, Integer> configDefaultsInt() {
        return ConfigUtils.getConfigDefaultsInt(getClass());
    }
}
