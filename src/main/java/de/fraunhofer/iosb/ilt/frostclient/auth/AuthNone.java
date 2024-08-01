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

import com.google.gson.JsonElement;
import de.fraunhofer.iosb.ilt.configurable.AnnotatedConfigurable;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorBoolean;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorNull;
import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authentication type for no authentication. Does add the option to ignore SSL
 * certificate errors, for when using self-signed certificates for testing
 * purposes.
 */
public class AuthNone implements AnnotatedConfigurable<Void, Void>, AuthMethod {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthNone.class);

    @ConfigurableField(editor = EditorBoolean.class,
            label = "IgnoreSslErrors",
            description = "Ignore SSL certificate errors. This is a bad idea unless you know what you are doing.")
    @EditorBoolean.EdOptsBool()
    private boolean ignoreSslErrors;

    @Override
    public void configure(JsonElement config, Void context, Void edtCtx, ConfigEditor<?> ce) {
        // Nothing to configure
    }

    @Override
    public ConfigEditor<?> getConfigEditor(Void context, Void edtCtx) {
        return new EditorNull();
    }

    @Override
    public void setAuth(SensorThingsService service) {
        try {
            HttpClientBuilder clientBuilder = service.getClientBuilder();

            if (ignoreSslErrors) {
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        new SSLContextBuilder()
                                .loadTrustMaterial((X509Certificate[] chain, String authType) -> true)
                                .build());
                clientBuilder.setSSLSocketFactory(sslsf);
            }

            service.rebuildHttpClient();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException ex) {
            LOGGER.error("Failed to initialise basic auth.", ex);
        }

    }

}
