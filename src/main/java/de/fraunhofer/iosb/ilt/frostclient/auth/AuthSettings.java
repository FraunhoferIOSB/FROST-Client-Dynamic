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
import de.fraunhofer.iosb.ilt.frostclient.utils.Utils;

/**
 * Settings holder for Auth* settings.
 */
public class AuthSettings extends ConfigProvider<AuthSettings> {

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
        AuthMethod authProvider = Utils.instantiateClass(authProviderClassName, AuthMethod.class);
        if (authProvider != null) {
            authProvider.setAuth(service);
        }
    }

    @Override
    public AuthSettings getThis() {
        return this;
    }

}
