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
package de.fraunhofer.iosb.ilt.frostclient.utils;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.NotAuthorizedException;
import de.fraunhofer.iosb.ilt.frostclient.exception.NotFoundException;
import de.fraunhofer.iosb.ilt.frostclient.exception.StatusCodeException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various utilities.
 */
public class Utils {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    /**
     * Throws a StatusCodeException if the given response did not have status
     * code 2xx
     *
     * @param request The request that generated the response.
     * @param response The response to check the status code of.
     * @throws StatusCodeException If the response was not 2xx.
     */
    public static void throwIfNotOk(HttpRequestBase request, CloseableHttpResponse response) throws StatusCodeException {
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 204) {
            throw new StatusCodeException(request.getURI().toString(), statusCode, response.getStatusLine().getReasonPhrase(), "");
        }
        if (statusCode < 200 || statusCode >= 300 || statusCode == 204) {
            String returnContent = null;
            try {
                returnContent = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            } catch (IOException exc) {
                LOGGER.warn("Failed to get content from error response.", exc);
            }
            if (statusCode == 401 || statusCode == 403) {
                request.getURI();
                throw new NotAuthorizedException(statusCode, request.getURI().toString(), response.getStatusLine().getReasonPhrase(), returnContent);
            }
            if (statusCode == 404) {
                throw new NotFoundException(request.getURI().toString(), response.getStatusLine().getReasonPhrase(), returnContent);
            }
            throw new StatusCodeException(request.getURI().toString(), statusCode, response.getStatusLine().getReasonPhrase(), returnContent);
        }
    }

    public static void createInsecureHttpClient(SensorThingsService service) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustSelfSignedStrategy())
                .build();
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
        service.getClientBuilder().setSSLSocketFactory(connectionFactory);
        service.rebuildHttpClient();
    }
}
