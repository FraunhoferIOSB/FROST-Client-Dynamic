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
package de.fraunhofer.iosb.ilt.frostclient.auth;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.settings.ConfigProvider;
import de.fraunhofer.iosb.ilt.frostclient.settings.Settings;
import de.fraunhofer.iosb.ilt.frostclient.settings.annotation.DefaultValue;
import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import org.slf4j.LoggerFactory;

/**
 * Settings holder for Auth* settings.
 */
public class AuthSettings extends ConfigProvider {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AuthSettings.class.getName());
    @DefaultValue("")
    public static final String TAG_AUTH_PROVIDER_CLASS = "providerClass";

    public AuthSettings(Settings settings) {
        super(settings);
    }

    public void load(SensorThingsService service) {
        String authProviderClassName = get(TAG_AUTH_PROVIDER_CLASS);
        if (StringHelper.isNullOrEmpty(authProviderClassName)) {
            return;
        }
        Class<AuthMethod> authClass;
        try {
            Class<?> clazz = Class.forName(authProviderClassName, false, getClass().getClassLoader());
            if (AuthMethod.class.isAssignableFrom(clazz)) {
                authClass = (Class<AuthMethod>) clazz;
            } else {
                throw new IllegalArgumentException("Class " + authProviderClassName + " does not implement the interface AuthMethod");
            }
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Class '" + authProviderClassName + "' could not be found", ex);
        }
        try {
            AuthMethod authProvider = authClass.getDeclaredConstructor().newInstance();
            authProvider.setAuth(service);
        } catch (ReflectiveOperationException | SecurityException | IllegalArgumentException ex) {
            LOGGER.error("Class '{}' could not be instantiated", authProviderClassName, ex);
        }

    }

}
