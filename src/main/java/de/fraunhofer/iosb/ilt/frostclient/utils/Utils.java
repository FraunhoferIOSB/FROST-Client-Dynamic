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
package de.fraunhofer.iosb.ilt.frostclient.utils;

import static de.fraunhofer.iosb.ilt.frostclient.SensorThingsService.NULL_URL_V11;
import static de.fraunhofer.iosb.ilt.frostclient.utils.Constants.CONFORMANCE_STA_11_MQTT_CREATE;
import static de.fraunhofer.iosb.ilt.frostclient.utils.Constants.CONFORMANCE_STA_11_MQTT_READ;

import com.fasterxml.jackson.databind.JsonNode;
import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.NotAuthorizedException;
import de.fraunhofer.iosb.ilt.frostclient.exception.NotFoundException;
import de.fraunhofer.iosb.ilt.frostclient.exception.StatusCodeException;
import de.fraunhofer.iosb.ilt.frostclient.json.SimpleJsonMapper;
import de.fraunhofer.iosb.ilt.frostclient.models.CSDLModel;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsPlus;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11MultiDatastream;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Tasking;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various utilities.
 */
public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private static final String NAME_NAME = "name";
    private static final String NAME_VALUE = "value";
    private static final String NAME_HEADER_ACCEPT = "Accept";
    private static final String NAME_HEADER_ODATA_VERSION = "OData-Version";
    private static final String NAME_CONFORMANCE = "conformance";
    private static final String NAME_SERVER_SETTINGS = "serverSettings";
    private static final String NAME_ENDPOINTS = "endpoints";

    private enum KnownModels {
        STA_SENSING,
        STA_MULTIDATASTREAM,
        STA_TASKING,
        STA_PLUS
    }

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

    public static ServerInfo detectServerInfo(SensorThingsService service) {
        ServerInfo serverInfo = service.getServerInfo();
        boolean modelsPreSet = !serverInfo.getModels().isEmpty();
        HttpGet httpGet;
        try {
            httpGet = new HttpGet(serverInfo.getBaseUrl().toURI());
            LOGGER.debug("Fetching: {}", httpGet.getURI());
            httpGet.addHeader(NAME_HEADER_ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Could not generate context url.", ex);
        }
        if (service.getBaseUrl().equals(NULL_URL_V11)) {
            return serverInfo;
        }
        try (CloseableHttpResponse response = service.execute(httpGet)) {
            Utils.throwIfNotOk(httpGet, response);
            String json = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            JsonNode tree = SimpleJsonMapper.getSimpleObjectMapper().readTree(json);
            Header[] odataVersion = response.getHeaders(NAME_HEADER_ODATA_VERSION);
            if (tree.has("@context") && odataVersion.length > 0) {
                // Assume OData 4.01
                LOGGER.info("Detected OData 4.01.");
                if (!modelsPreSet) {
                    serverInfo.addModel(new CSDLModel());
                }
                findMqttEndpoint(tree.get(NAME_SERVER_SETTINGS), serverInfo);
                return serverInfo;
            }
            Set<KnownModels> foundModels = new HashSet<>();
            if (tree.has(NAME_SERVER_SETTINGS)) {
                // SensorThings 1.1 or 1.0
                JsonNode serverSettings = tree.get(NAME_SERVER_SETTINGS);
                JsonNode conformance = serverSettings.get(NAME_CONFORMANCE);
                for (var entries = conformance.elements(); entries.hasNext();) {
                    String confClass = entries.next().textValue();
                    if (confClass.startsWith("http://www.opengis.net/spec/iot_sensing/1.1/req/datamodel")) {
                        foundModels.add(KnownModels.STA_SENSING);
                    } else if (confClass.startsWith("http://www.opengis.net/spec/iot_sensing/1.1/req/multi-datastream")) {
                        foundModels.add(KnownModels.STA_MULTIDATASTREAM);
                    } else if (confClass.startsWith("http://www.opengis.net/spec/iot_sensing/1.1/req/actuator")) {
                        foundModels.add(KnownModels.STA_TASKING);
                    } else if (confClass.startsWith("http://www.opengis.net/spec/sensorthings-staplus/1.0/conf/core")) {
                        foundModels.add(KnownModels.STA_PLUS);
                    } else {
                        switch (confClass) {
                            case "https://fraunhoferiosb.github.io/FROST-Server/extensions/MqttExpand.html":
                                serverInfo.setMqttExpandAllowed(true);
                                break;

                            case "https://fraunhoferiosb.github.io/FROST-Server/extensions/MqttFilter.html":
                                serverInfo.setMqttFilterAllowed(true);
                                break;
                        }
                    }
                }
                findMqttEndpoint(serverSettings, serverInfo);
            } else if (tree.has(NAME_VALUE)) {
                JsonNode entities = tree.get(NAME_VALUE);
                for (var it = entities.elements(); it.hasNext();) {
                    String entityName = it.next().get(NAME_NAME).textValue();
                    switch (entityName) {
                        case "Things":
                            foundModels.add(KnownModels.STA_SENSING);
                            break;

                        case "MultiDatastreams":
                            foundModels.add(KnownModels.STA_MULTIDATASTREAM);
                            break;

                        case "Tasks":
                            foundModels.add(KnownModels.STA_TASKING);
                            break;

                        case "Parties":
                            foundModels.add(KnownModels.STA_PLUS);
                            break;
                    }
                }
            }
            if (!modelsPreSet) {
                if (foundModels.contains(KnownModels.STA_SENSING)) {
                    LOGGER.info("Detected STA Sensing.");
                    serverInfo.addModel(new SensorThingsV11Sensing());
                }
                if (foundModels.contains(KnownModels.STA_MULTIDATASTREAM)) {
                    LOGGER.info("Detected STA MultiDatastream.");
                    serverInfo.addModel(new SensorThingsV11MultiDatastream());
                }
                if (foundModels.contains(KnownModels.STA_TASKING)) {
                    LOGGER.info("Detected STA Tasking.");
                    serverInfo.addModel(new SensorThingsV11Tasking());
                }
                if (foundModels.contains(KnownModels.STA_PLUS)) {
                    LOGGER.info("Detected STAplus.");
                    serverInfo.addModel(new SensorThingsPlus());
                }
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to parse metadata", ex);
        } catch (StatusCodeException ex) {
            LOGGER.error("Failed to request metadata", ex);
        }

        return serverInfo;
    }

    public static void findMqttEndpoint(JsonNode serverSettings, ServerInfo result) {
        if (serverSettings == null) {
            return;
        }
        JsonNode mqttCreate = serverSettings.get(CONFORMANCE_STA_11_MQTT_CREATE);
        JsonNode mqttRead = serverSettings.get(CONFORMANCE_STA_11_MQTT_READ);
        String bestEndpoint;
        bestEndpoint = findBestEndpoint(mqttCreate);
        if (StringHelper.isNullOrEmpty(bestEndpoint)) {
            bestEndpoint = findBestEndpoint(mqttRead);
        }
        result.setMqttUrl(bestEndpoint);
    }

    private static String findBestEndpoint(JsonNode mqtt) {
        if (mqtt == null) {
            return null;
        }
        JsonNode endpoints = mqtt.get(NAME_ENDPOINTS);
        if (endpoints == null || !endpoints.isArray()) {
            return null;
        }
        String best = null;
        for (var endpoint : endpoints) {
            String url = endpoint.asText();
            if (best == null || url.startsWith("ws")) {
                // We prefer WebSocket URLs.
                best = url;
            }
        }
        return best;
    }
}
