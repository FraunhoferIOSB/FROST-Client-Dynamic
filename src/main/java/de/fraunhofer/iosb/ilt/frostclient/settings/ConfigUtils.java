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
import de.fraunhofer.iosb.ilt.frostclient.settings.annotation.DefaultValueDouble;
import de.fraunhofer.iosb.ilt.frostclient.settings.annotation.DefaultValueInt;
import de.fraunhofer.iosb.ilt.frostclient.settings.annotation.SensitiveValue;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility classes for the ConfigDefaults interface.
 *
 * @author Brian Miles, Scf
 */
public class ConfigUtils {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtils.class);
    private static final String UNABLE_TO_ACCESS_FIELD_ON_OBJECT = "Unable to access field '{}' on object: {}.";

    private ConfigUtils() {
        // Utility class.
    }

    /**
     * Return a list of field names, of the given Object, that were annotated
     * with either {@link DefaultValue} or {@link DefaultValueInt}.
     *
     * @param <T> The class that extends ConfigDefaults.
     * @param target The class to get the config field names for.
     * @return The list of field names so annotated.
     */
    public static <T extends ConfigDefaults> Set<String> getConfigTags(Class<T> target) {
        Set<String> configTags = new HashSet<>();
        for (Field f : target.getFields()) {
            try {
                if (f.isAnnotationPresent(DefaultValue.class)
                        || f.isAnnotationPresent(DefaultValueInt.class)
                        || f.isAnnotationPresent(DefaultValueBoolean.class)) {
                    configTags.add(f.get(target).toString());
                }
            } catch (IllegalAccessException e) {
                LOGGER.warn(UNABLE_TO_ACCESS_FIELD_ON_OBJECT, f.getName(), target);
            }
        }
        return configTags;
    }

    /**
     * Return a mapping of config tag value and default value for any field, of
     * the given Object, annotated with either {@link DefaultValue} or
     * {@link DefaultValueInt}.
     *
     * @param <T> The class that extends ConfigDefaults.
     * @param target The class to get the config fields for.
     * @return Mapping of config tag value and default value
     */
    public static <T extends ConfigDefaults> Map<String, String> getConfigDefaults(Class<T> target) {
        Map<String, String> configDefaults = new HashMap<>();
        for (Field f : target.getFields()) {
            String defaultValue = null;
            if (f.isAnnotationPresent(DefaultValue.class)) {
                defaultValue = f.getAnnotation(DefaultValue.class).value();
            } else if (f.isAnnotationPresent(DefaultValueInt.class)) {
                defaultValue = Integer.toString(f.getAnnotation(DefaultValueInt.class).value());
            } else if (f.isAnnotationPresent(DefaultValueBoolean.class)) {
                defaultValue = Boolean.toString(f.getAnnotation(DefaultValueBoolean.class).value());
            }
            try {
                if (defaultValue != null) {
                    String key = f.get(target).toString();
                    configDefaults.put(key, defaultValue);
                }
            } catch (IllegalAccessException exc) {
                LOGGER.warn(UNABLE_TO_ACCESS_FIELD_ON_OBJECT, f.getName(), target);
            }
        }
        return configDefaults;
    }

    /**
     * Return a mapping of config tag value and default value for any field, of
     * the given Object, annotated with {@link DefaultValueInt}.
     *
     * @param <T> The class that extends ConfigDefaults.
     * @param target The class to get the config fields for.
     * @return Mapping of config tag value and default value
     */
    public static <T extends ConfigDefaults> Map<String, Integer> getConfigDefaultsInt(Class<T> target) {
        Map<String, Integer> configDefaults = new HashMap<>();
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(target, DefaultValueInt.class);
        for (Field f : fields) {
            try {
                configDefaults.put(
                        f.get(target).toString(),
                        f.getAnnotation(DefaultValueInt.class).value());
            } catch (IllegalAccessException exc) {
                LOGGER.warn(UNABLE_TO_ACCESS_FIELD_ON_OBJECT, f.getName(), target);
            }
        }
        return configDefaults;
    }

    /**
     * Return a mapping of config tag value and default value for any field, of
     * the given Object, annotated with {@link DefaultValueBoolean}.
     *
     * @param <T> The class that extends ConfigDefaults.
     * @param target The class to get the config fields for.
     * @return Mapping of config tag value and default value
     */
    public static <T extends ConfigDefaults> Map<String, Boolean> getConfigDefaultsBoolean(Class<T> target) {
        Map<String, Boolean> configDefaults = new HashMap<>();
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(target, DefaultValueBoolean.class);
        for (Field f : fields) {
            try {
                configDefaults.put(
                        f.get(target).toString(),
                        f.getAnnotation(DefaultValueBoolean.class).value());
            } catch (IllegalAccessException exc) {
                LOGGER.warn(UNABLE_TO_ACCESS_FIELD_ON_OBJECT, f.getName(), target);
            }
        }
        return configDefaults;
    }

    /**
     * Returns true if the given class has a field with the given value that is
     * annotated with the {@link SensitiveValue} annotation.
     *
     * @param <T> The class that extends ConfigDefaults.
     * @param target The class to get the config default for.
     * @param fieldValue The value of the annotated field.
     * @return true if the field is annotated with the SensitiveValue
     * annotation.
     */
    public static <T extends ConfigDefaults> boolean isSensitive(Class<T> target, String fieldValue) {
        for (final Field f : target.getFields()) {
            try {
                if (f.isAnnotationPresent(SensitiveValue.class) && f.get(target).toString().equals(fieldValue)) {
                    return true;
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                LOGGER.warn(UNABLE_TO_ACCESS_FIELD_ON_OBJECT, f.getName(), target);
            }
        }
        return false;
    }

    /**
     * Returns the default value of a field annotated with any of
     * {@link DefaultValue}, {@link DefaultValueInt} or
     * {@link DefaultValueBoolean}. If there is no such a field, an
     * IllegalArgumentException is thrown.
     *
     * @param <T> The class that extends ConfigDefaults.
     * @param target The class to get the config default for.
     * @param fieldValue The value of the annotated field.
     * @return The default value of the annotated field. If there is no such a
     * field, an IllegalArgumentException is thrown.
     */
    public static <T extends ConfigDefaults> String getDefaultValue(Class<T> target, String fieldValue) {
        for (final Field f : target.getFields()) {
            try {
                if (!fieldValue.equals(f.get(target).toString())) {
                    continue;
                }
                if (f.isAnnotationPresent(DefaultValue.class)) {
                    return f.getAnnotation(DefaultValue.class).value();

                } else if (f.isAnnotationPresent(DefaultValueInt.class)) {
                    return Integer.toString(f.getAnnotation(DefaultValueInt.class).value());

                } else if (f.isAnnotationPresent(DefaultValueBoolean.class)) {
                    return Boolean.toString(f.getAnnotation(DefaultValueBoolean.class).value());

                } else if (f.isAnnotationPresent(DefaultValueDouble.class)) {
                    return Double.toString(f.getAnnotation(DefaultValueDouble.class).value());
                }
            } catch (IllegalAccessException e) {
                LOGGER.warn(UNABLE_TO_ACCESS_FIELD_ON_OBJECT, f.getName(), target);
            }
        }
        throw new IllegalArgumentException(target.getName() + " has no default-annotated field " + fieldValue);
    }

    /**
     * Returns the default value of a field annotated with
     * {@link DefaultValueInt}.
     *
     * @param <T> The class that extends ConfigDefaults.
     * @param target The class to get the config default for.
     * @param fieldValue The value of the annotated field
     * @return The default value of the annotated field. If there is no such a
     * field, an IllegalArgumentException is thrown.
     */
    public static <T extends ConfigDefaults> int getDefaultValueInt(Class<T> target, String fieldValue) {
        for (final Field f : target.getFields()) {
            if (f.isAnnotationPresent(DefaultValueInt.class)) {
                try {
                    if (f.get(target).toString().equals(fieldValue)) {
                        return f.getAnnotation(DefaultValueInt.class).value();
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.warn(UNABLE_TO_ACCESS_FIELD_ON_OBJECT, f.getName(), target);
                }
            }
        }
        throw new IllegalArgumentException(target.getName() + " has no integer-default-annotated field " + fieldValue);
    }

    /**
     * Returns the default value of a field annotated with
     * {@link DefaultValueDouble}.
     *
     * @param <T> The class that extends ConfigDefaults.
     * @param target The class to get the config default for.
     * @param fieldValue The value of the annotated field
     * @return The default value of the annotated field. If there is no such a
     * field, an IllegalArgumentException is thrown.
     */
    public static <T extends ConfigDefaults> double getDefaultValueDouble(Class<T> target, String fieldValue) {
        for (final Field f : target.getFields()) {
            if (f.isAnnotationPresent(DefaultValueDouble.class)) {
                try {
                    if (f.get(target).toString().equals(fieldValue)) {
                        return f.getAnnotation(DefaultValueDouble.class).value();
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.warn(UNABLE_TO_ACCESS_FIELD_ON_OBJECT, f.getName(), target);
                }
            }
        }
        throw new IllegalArgumentException(target.getName() + " has no double-default-annotated field " + fieldValue);
    }

    /**
     * Returns the default value of a field annotated with
     * {@link DefaultValueBoolean}.
     *
     * @param <T> The class that extends ConfigDefaults.
     * @param target The class to get the config default for.
     * @param fieldValue The value of the annotated field
     * @return The default value of the annotated field. If there is no such a
     * field, an IllegalArgumentException is thrown.
     */
    public static <T extends ConfigDefaults> boolean getDefaultValueBoolean(Class<T> target, String fieldValue) {
        for (final Field f : target.getFields()) {
            if (f.isAnnotationPresent(DefaultValueBoolean.class)) {
                try {
                    if (f.get(target).toString().equals(fieldValue)) {
                        return f.getAnnotation(DefaultValueBoolean.class).value();
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.warn(UNABLE_TO_ACCESS_FIELD_ON_OBJECT, f.getName(), target);
                }
            }
        }
        throw new IllegalArgumentException(target.getName() + " has no boolean-default-annotated field " + fieldValue);
    }
}
