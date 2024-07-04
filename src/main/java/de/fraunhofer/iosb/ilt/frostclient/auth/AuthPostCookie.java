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
package de.fraunhofer.iosb.ilt.frostclient.auth;

import de.fraunhofer.iosb.ilt.configurable.AnnotatedConfigurable;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorPassword;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorString;
import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import java.io.IOException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.LoggerFactory;

/**
 * Authentication using Cookies.
 */
public class AuthPostCookie implements AnnotatedConfigurable<Void, Void>, AuthMethod {

    public static final String HTTPREQUEST_HEADER_ACCEPT = "Accept";
    public static final String HTTPREQUEST_HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HTTPREQUEST_TYPE_JSON = "application/json";
    /**
     * The logger for this class.
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AuthPostCookie.class);

    @ConfigurableField(editor = EditorString.class,
            label = "PostUrl",
            description = "The url to post to, use placeholders {username} and {password} for username and password.",
            optional = false)
    @EditorString.EdOptsString(dflt = "https://example.org/servlet/is/rest/login?user={username}&key={password}")
    private String postUrl;

    @ConfigurableField(editor = EditorString.class,
            label = "Username",
            description = "The username to use for authentication",
            optional = false)
    @EditorString.EdOptsString()
    private String username;

    @ConfigurableField(editor = EditorPassword.class,
            label = "Password",
            description = "The password to use for authentication",
            optional = false)
    @EditorPassword.EdOptsPassword()
    private String password;

    @Override
    public void setAuth(SensorThingsService service) {
        String finalUrl = postUrl.replace("{username}", username);
        finalUrl = finalUrl.replace("{password}", password);
        CloseableHttpClient client = service.getHttpClient();
        final HttpPost loginPost = new HttpPost(finalUrl);
        loginPost.setHeader(HTTPREQUEST_HEADER_ACCEPT, HTTPREQUEST_TYPE_JSON);
        try {
            client.execute(loginPost);
        } catch (IOException ex) {
            LOGGER.error("Failed to login.", ex);
        }
    }

}
