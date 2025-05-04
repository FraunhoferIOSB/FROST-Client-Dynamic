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

import de.fraunhofer.iosb.ilt.configurable.AnnotatedConfigurable;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorString;
import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.settings.ConfigDefaults;
import de.fraunhofer.iosb.ilt.frostclient.settings.annotation.DefaultValue;
import de.fraunhofer.iosb.ilt.frostclient.settings.annotation.SensitiveValue;
import de.fraunhofer.iosb.ilt.frostclient.utils.TokenManager;
import org.apache.http.HttpRequest;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Authentication using custom Headers added to each request.
 */
public class AuthHeader implements AnnotatedConfigurable<Void, Void>, AuthMethod, ConfigDefaults {

    @DefaultValue("")
    public static final String NAME_VAR_HEADERNAME = "headername";
    @DefaultValue("")
    @SensitiveValue
    public static final String NAME_VAR_HEADERVALUE = "headervalue";

    @ConfigurableField(editor = EditorString.class,
            label = "Header Name",
            description = "The name of the authentication header to use.")
    @EditorString.EdOptsString()
    private String headerName;

    @ConfigurableField(editor = EditorString.class,
            label = "Header Value",
            description = "The value of the authentication header to use.")
    @EditorString.EdOptsString()
    private String headerValue;

    @Override
    public void setAuth(SensorThingsService service) {
        AuthSettings authSettings = service.getSettings().getAuthSettings();
        if (headerName == null) {
            headerName = authSettings.getSettings().get(NAME_VAR_HEADERNAME, getClass());
            headerValue = authSettings.getSettings().get(NAME_VAR_HEADERVALUE, getClass());
        }
        service.setTokenManager(new TokenManager() {
            @Override
            public void addAuthHeader(HttpRequest hr) {
                hr.addHeader(headerName, headerValue);
            }

            @Override
            public TokenManager setHttpClient(CloseableHttpClient chc) {
                // We don't need a HTTPClient.
                return this;
            }

            @Override
            public CloseableHttpClient getHttpClient() {
                // We don't need a HTTPClient.
                return null;
            }
        });
    }

}
