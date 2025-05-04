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
import de.fraunhofer.iosb.ilt.configurable.editor.EditorBoolean;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorPassword;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorString;
import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.utils.ServerInfo;
import de.fraunhofer.iosb.ilt.settings.ConfigDefaults;
import de.fraunhofer.iosb.ilt.settings.annotation.DefaultValue;
import de.fraunhofer.iosb.ilt.settings.annotation.DefaultValueBoolean;
import de.fraunhofer.iosb.ilt.settings.annotation.SensitiveValue;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.LoggerFactory;

/**
 * Authentication using HTTP Basic Auth.
 */
public class AuthBasic implements AnnotatedConfigurable<Void, Void>, AuthMethod, ConfigDefaults {

    /**
     * The logger for this class.
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AuthBasic.class);

    @DefaultValue("")
    public static final String NAME_VAR_USERNAME = "username";
    @DefaultValue("")
    @SensitiveValue
    public static final String NAME_VAR_PASSWORD = "password";
    @DefaultValueBoolean(false)
    public static final String NAME_VAR_IGNORE_SSL_ERRORS = "ignoreSslErrors";

    @ConfigurableField(editor = EditorString.class,
            label = "Username",
            description = "The username to use for authentication")
    @EditorString.EdOptsString()
    private String username;

    @ConfigurableField(editor = EditorPassword.class,
            label = "Password",
            description = "The password to use for authentication")
    @EditorPassword.EdOptsPassword()
    private String password;

    @ConfigurableField(editor = EditorBoolean.class,
            label = "IgnoreSslErrors",
            description = "Ignore SSL certificate errors. This is a bad idea unless you know what you are doing.")
    @EditorBoolean.EdOptsBool()
    private boolean ignoreSslErrors;

    @Override
    public void setAuth(SensorThingsService service) {
        AuthSettings authSettings = service.getSettings().getAuthSettings();
        if (username == null) {
            username = authSettings.getSettings().get(NAME_VAR_USERNAME, getClass());
            password = authSettings.getSettings().get(NAME_VAR_PASSWORD, getClass());
            ignoreSslErrors = authSettings.getSettings().getBoolean(NAME_VAR_IGNORE_SSL_ERRORS, getClass());
        }

        try {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            ServerInfo serverInfo = service.getServerInfo();
            service.getOrCreateMqttConfig().setAuth(username, password);
            URL url = serverInfo.getBaseUrl();
            credsProvider.setCredentials(
                    new AuthScope(url.getHost(), url.getPort()),
                    new UsernamePasswordCredentials(username, password));

            HttpClientBuilder clientBuilder = service.getClientBuilder()
                    .setDefaultCredentialsProvider(credsProvider);

            if (ignoreSslErrors) {
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial((X509Certificate[] chain, String authType) -> true).build());
                clientBuilder.setSSLSocketFactory(sslsf);
            }

            service.rebuildHttpClient();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException ex) {
            LOGGER.error("Failed to initialise basic auth.", ex);
        }
    }

}
