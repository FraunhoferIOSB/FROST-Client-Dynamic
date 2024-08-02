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
package de.fraunhofer.iosb.ilt.frostclient;

import de.fraunhofer.iosb.ilt.frostclient.models.DataModel;
import de.fraunhofer.iosb.ilt.frostclient.settings.CachedSettings;
import de.fraunhofer.iosb.ilt.frostclient.settings.ConfigProvider;
import de.fraunhofer.iosb.ilt.frostclient.settings.Settings;
import de.fraunhofer.iosb.ilt.frostclient.settings.annotation.DefaultValue;
import de.fraunhofer.iosb.ilt.frostclient.settings.annotation.DefaultValueInt;
import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * The holder for all settings the SensorThingsService uses, and their default
 * values.
 */
public class ServiceSettings extends ConfigProvider {

    public static final String PREFIX_AUTH = "auth";

    @DefaultValue("")
    public static final String TAG_SERVICE_BASE_URL = "baseUrl";

    @DefaultValue("")
    public static final String TAG_SERVICE_MQTT_URL = "mqttUrl";

    @DefaultValue("")
    public static final String TAG_SERVICE_MODEL_CLASS_LIST = "modelClasses";

    @DefaultValueInt(120000)
    public static final String TAG_SERVICE_HTTP_REQUEST_TIMEOUT = "requestTimeoutMs";

    final AuthSettings authSettings;

    public ServiceSettings() {
        super(new CachedSettings());
        authSettings = new AuthSettings(new CachedSettings(getSettings(), PREFIX_AUTH));
    }

    public ServiceSettings(Settings settings) {
        super(settings);
        authSettings = new AuthSettings(new CachedSettings(settings, PREFIX_AUTH));
    }

    public String getBaseUrl() {
        return get(TAG_SERVICE_BASE_URL);
    }

    public String getMqttUrl() {
        return get(TAG_SERVICE_MQTT_URL);
    }

    public int getRequestTimeoutMs() {
        return getInt(TAG_SERVICE_HTTP_REQUEST_TIMEOUT);
    }

    public List<DataModel> getModels() {
        List<DataModel> result = new ArrayList<>();
        String classes = get(TAG_SERVICE_MODEL_CLASS_LIST);
        if (StringHelper.isNullOrEmpty(classes)) {
            return result;
        }
        String[] split = classes.split(",");
        for (String name : split) {
            try {
                Class<?> clazz = Class.forName(name, false, getClass().getClassLoader());
                if (DataModel.class.isAssignableFrom(clazz)) {
                    Class<DataModel> dataModelClass = (Class<DataModel>) clazz;
                    DataModel dataModel = dataModelClass.getDeclaredConstructor().newInstance();
                    result.add(dataModel);
                } else {
                    throw new IllegalArgumentException("Class " + name + " does not implement the interface DataModel");
                }
            } catch (ReflectiveOperationException ex) {
                throw new IllegalArgumentException("Failed to instantiate " + name + ".", ex);
            }

        }
        return result;
    }

    public AuthSettings getAuthSettings() {
        return authSettings;
    }

    public static class AuthSettings extends ConfigProvider {

        public AuthSettings(Settings settings) {
            super(settings);
        }
    }

}
