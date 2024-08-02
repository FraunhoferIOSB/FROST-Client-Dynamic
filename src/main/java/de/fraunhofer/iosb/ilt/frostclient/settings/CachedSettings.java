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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A caching wrapper around a Settings instance.
 */
public class CachedSettings extends Settings {

    private final Map<String, String> valuesString = new HashMap<>();
    private final Map<String, Integer> valuesInt = new HashMap<>();
    private final Map<String, Long> valuesLong = new HashMap<>();
    private final Map<String, Boolean> valuesBoolean = new HashMap<>();
    private final Map<String, Double> valuesDouble = new HashMap<>();

    /**
     * Creates a new cached settings, with no prefix, containing only
     * environment variables, not logging sensitive data.
     */
    public CachedSettings() {
        super();
    }

    /**
     * Creates a new settings, containing the given properties, and environment
     * variables, with no prefix and not logging sensitive data.
     *
     * @param properties The properties to use. These can be overridden by
     * environment variables.
     */
    public CachedSettings(Properties properties) {
        super(properties);
    }

    /**
     *
     * @param parent
     * @param prefix
     */
    public CachedSettings(Settings parent, String prefix) {
        super(parent.getProperties(), prefix, false, parent.getLogSensitiveData());
    }

    /**
     * Creates a new settings, containing the given properties, and environment
     * variables, with the given prefix.
     *
     * @param properties The properties to use.
     * @param prefix The prefix to use.
     * @param wrapInEnvironment Flag indicating if environment variables can
     * override the given properties.
     * @param logSensitiveData Flag indicating things like passwords should be
     * logged completely, not hidden.
     */
    public CachedSettings(Properties properties, String prefix, boolean wrapInEnvironment, boolean logSensitiveData) {
        super(properties, prefix, wrapInEnvironment, logSensitiveData);
    }

    @Override
    public String get(String name) {
        if (valuesString.containsKey(name)) {
            return valuesString.get(name);
        }
        String value = super.get(name);
        valuesString.put(name, value);
        return value;
    }

    @Override
    public String getSensitive(String name) {
        if (valuesString.containsKey(name)) {
            return valuesString.get(name);
        }
        String value = super.getSensitive(name);
        valuesString.put(name, value);
        return value;
    }

    @Override
    public String get(String name, String defaultValue) {
        if (valuesString.containsKey(name)) {
            return valuesString.get(name);
        }
        String value = super.get(name, defaultValue);
        valuesString.put(name, value);
        return value;
    }

    @Override
    public String getSensitive(String name, String defaultValue) {
        if (valuesString.containsKey(name)) {
            return valuesString.get(name);
        }
        String value = super.getSensitive(name, defaultValue);
        valuesString.put(name, value);
        return value;
    }

    @Override
    public String get(String name, Class<? extends ConfigDefaults> defaultsProvider) {
        if (valuesString.containsKey(name)) {
            return valuesString.get(name);
        }
        String value = super.get(name, defaultsProvider);
        valuesString.put(name, value);
        return value;
    }

    @Override
    public void set(String name, String value) {
        valuesString.put(name, value);
    }

    @Override
    public void set(String name, boolean value) {
        valuesBoolean.put(name, value);
    }

    @Override
    public boolean getBoolean(String name) {
        if (valuesBoolean.containsKey(name)) {
            return valuesBoolean.get(name);
        }
        boolean value = super.getBoolean(name);
        valuesBoolean.put(name, value);
        return value;
    }

    @Override
    public boolean getBoolean(String name, boolean defaultValue) {
        if (valuesBoolean.containsKey(name)) {
            return valuesBoolean.get(name);
        }
        boolean value = super.getBoolean(name, defaultValue);
        valuesBoolean.put(name, value);
        return value;
    }

    @Override
    public boolean getBoolean(String name, Class<? extends ConfigDefaults> defaultsProvider) {
        if (valuesBoolean.containsKey(name)) {
            return valuesBoolean.get(name);
        }
        boolean value = super.getBoolean(name, defaultsProvider);
        valuesBoolean.put(name, value);
        return value;
    }

    @Override
    public int getInt(String name) {
        if (valuesInt.containsKey(name)) {
            return valuesInt.get(name);
        }
        int value = super.getInt(name);
        valuesInt.put(name, value);
        return value;
    }

    @Override
    public int getInt(String name, int defaultValue) {
        if (valuesInt.containsKey(name)) {
            return valuesInt.get(name);
        }
        int value = super.getInt(name, defaultValue);
        valuesInt.put(name, value);
        valuesString.put(name, Integer.toString(value));
        return value;
    }

    @Override
    public int getInt(String name, Class<? extends ConfigDefaults> defaultsProvider) {
        if (valuesInt.containsKey(name)) {
            return valuesInt.get(name);
        }
        int value = super.getInt(name, defaultsProvider);
        valuesInt.put(name, value);
        return value;
    }

    @Override
    public long getLong(String name) {
        if (valuesLong.containsKey(name)) {
            return valuesLong.get(name);
        }
        long value = super.getLong(name);
        valuesLong.put(name, value);
        return value;
    }

    @Override
    public long getLong(String name, long defaultValue) {
        if (valuesLong.containsKey(name)) {
            return valuesLong.get(name);
        }
        long value = super.getLong(name, defaultValue);
        valuesLong.put(name, value);
        return value;
    }

    @Override
    public long getLong(String name, Class<? extends ConfigDefaults> defaultsProvider) {
        if (valuesLong.containsKey(name)) {
            return valuesLong.get(name);
        }
        long value = super.getLong(name, defaultsProvider);
        valuesLong.put(name, value);
        return value;
    }

    @Override
    public double getDouble(String name) {
        if (valuesDouble.containsKey(name)) {
            return valuesDouble.get(name);
        }
        double value = super.getDouble(name);
        valuesDouble.put(name, value);
        return value;
    }

    @Override
    public double getDouble(String name, double defaultValue) {
        if (valuesDouble.containsKey(name)) {
            return valuesDouble.get(name);
        }
        double value = super.getDouble(name, defaultValue);
        valuesDouble.put(name, value);
        return value;
    }

    @Override
    public double getDouble(String name, Class<? extends ConfigDefaults> defaultsProvider) {
        if (valuesDouble.containsKey(name)) {
            return valuesDouble.get(name);
        }
        double value = super.getDouble(name, defaultsProvider);
        valuesDouble.put(name, value);
        return value;
    }

}
