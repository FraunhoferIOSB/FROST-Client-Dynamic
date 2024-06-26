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
package de.fraunhofer.iosb.ilt.frostclient;

import com.github.fge.jsonpatch.JsonPatchOperation;
import de.fraunhofer.iosb.ilt.frostclient.dao.BaseDao;
import de.fraunhofer.iosb.ilt.frostclient.dao.Dao;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.json.deserialize.JsonReader;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationProperty;
import de.fraunhofer.iosb.ilt.frostclient.models.DataModel;
import de.fraunhofer.iosb.ilt.frostclient.query.Query;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import de.fraunhofer.iosb.ilt.frostclient.utils.TokenManager;
import de.fraunhofer.iosb.ilt.frostclient.utils.Utils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;

/**
 * A SensorThingsService represents the service endpoint of a server.
 */
public class SensorThingsService {

    /**
     * The logger for this class.
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SensorThingsService.class);

    private final ModelRegistry modelRegistry;
    private final List<DataModel> models = new ArrayList<>();
    private JsonReader jsonReader;
    private URL endpoint;
    private String urlReplace;
    private HttpClientBuilder clientBuilder;
    private CloseableHttpClient httpClient;
    private TokenManager tokenManager;
    private Version version;
    /**
     * The request timeout in MS.
     */
    private int requestTimeoutMs = 120000;

    /**
     * Creates a new SensorThingsService without an endpoint url set. The
     * endpoint url MUST be set before the service can be used. The models will
     * be initialised in the order they are returned by the list.
     *
     * @param models The data models to use.
     */
    public SensorThingsService(List<DataModel> models) {
        modelRegistry = new ModelRegistry();
        this.models.addAll(models);
    }

    /**
     * Creates a new SensorThingsService without an endpoint url set. The
     * endpoint url MUST be set before the service can be used.
     *
     * @param modelRegistry the data model to use.
     */
    public SensorThingsService(ModelRegistry modelRegistry) {
        this.modelRegistry = modelRegistry;
        modelRegistry.initFinalise();
        jsonReader = new JsonReader(modelRegistry);
    }

    /**
     * Creates a new SensorThingsService without an endpoint url set. The
     * endpoint url MUST be set before the service can be used. The models will
     * be initialised in the order they are passed.
     *
     * @param models The data models to use.
     */
    public SensorThingsService(DataModel... models) {
        this(Arrays.asList(models));
    }

    /**
     * Creates a new SensorThingsService with the given endpoint URI. The models
     * will be initialised in the order they have.
     *
     * @param endpoint the base URI of the SensorThings service
     * @param models The data models to use.
     * @throws MalformedURLException when building the final URL fails.
     */
    public SensorThingsService(URI endpoint, List<DataModel> models) throws MalformedURLException {
        this(models);
        setEndpoint(endpoint);
    }

    /**
     * Creates a new SensorThingsService with the given endpoint URI. The models
     * will be initialised in the order they are passed.
     *
     * @param endpoint the base URI of the SensorThings service
     * @param models The data models to use.
     * @throws MalformedURLException when building the final URL fails.
     */
    public SensorThingsService(URI endpoint, DataModel... models) throws MalformedURLException {
        this(endpoint, Arrays.asList(models));
    }

    /**
     * Creates a new SensorThingsService with the given endpoint URL. The models
     * will be initialised in the order they are passed.
     *
     * @param endpoint the base URL of the SensorThings service
     * @param models The data models to use.
     * @throws MalformedURLException when building the final URL fails.
     */
    public SensorThingsService(URL endpoint, List<DataModel> models) throws MalformedURLException {
        this(models);
        setEndpoint(endpoint);
    }

    /**
     * Creates a new SensorThingsService with the given endpoint URL. The models
     * will be initialised in the order they are passed.
     *
     * @param endpoint the base URL of the SensorThings service
     * @param models The data models to use.
     * @throws MalformedURLException when building the final URL fails.
     */
    public SensorThingsService(URL endpoint, DataModel... models) throws MalformedURLException {
        this(endpoint, Arrays.asList(models));
    }

    public ModelRegistry getModelRegistry() {
        return modelRegistry;
    }

    private void initModels() {
        for (DataModel model : models) {
            model.init(this, modelRegistry);
        }
        modelRegistry.initFinalise();
        jsonReader = new JsonReader(modelRegistry);
    }

    public <T extends DataModel> T getModel(Class<T> clazz) {
        return modelRegistry.getModel(clazz);
    }

    public <T extends DataModel> boolean hasModel(Class<T> clazz) {
        return modelRegistry.hasModel(clazz);
    }

    public JsonReader getJsonReader() {
        return jsonReader;
    }

    /**
     * Sets the endpoint URL/URI. Once the endpoint URL/URI is set it can not be
     * changed. The endpoint url MUST be set before the service can be used.
     *
     * @param endpoint The URI of the endpoint.
     * @return this.
     * @throws java.net.MalformedURLException when building the final url fails.
     */
    public final SensorThingsService setEndpoint(URI endpoint) throws MalformedURLException {
        return setEndpoint(endpoint.toURL());
    }

    /**
     * Sets the endpoint URL/URI. Once the endpoint URL/URI is set it can not be
     * changed. The endpoint url MUST be set before the service can be used.
     *
     * @param endpoint The URL of the endpoint.
     * @return this.
     * @throws MalformedURLException when building the final URL fails.
     */
    public final SensorThingsService setEndpoint(URL endpoint) throws MalformedURLException {
        if (this.endpoint != null) {
            throw new IllegalStateException("endpoint URL already set.");
        }
        // Temporarily set the given url directly as endpoint
        String url = StringUtils.removeEnd(endpoint.toString(), "/");
        this.endpoint = new URL(url + "/");
        if (models.isEmpty() && !modelRegistry.isInitialised()) {
            models.addAll(Utils.detectModels(this, endpoint));
        }
        String lastSegment = url.substring(url.lastIndexOf('/') + 1);
        Version detectedVersion = Version.findVersion(lastSegment);
        if (detectedVersion == null) {
            if (getVersion() == null) {
                throw new MalformedURLException("endpoint URL does not contain version (e.g. http://example.org/v1.0/) nor version information explicitely provided");
            }
            if (!url.endsWith(getVersion().getUrlPart())) {
                url += "/" + getVersion().getUrlPart();
                this.endpoint = new URL(url + "/");
            }
        } else {
            version = detectedVersion;
        }

        initModels();
        return this;
    }

    /**
     * In some cases the server generates URLs using a different base URL. For
     * instance when the server has a different external and internal address.
     * This option will replace the start part of each URL generated by the
     * server that matches the given string, with the service URL.
     *
     * @param urlReplace the endpoint url the server uses, that needs to be
     * replaced.
     * @return this.
     */
    public final SensorThingsService setUrlReplace(String urlReplace) {
        this.urlReplace = urlReplace;
        return this;
    }

    /**
     * Gets the endpoint URL for the service. Throws an IllegalStateException if
     * the endpoint is not set.
     *
     * @return the endpoint URL for the service.
     */
    public URL getEndpoint() {
        if (this.endpoint == null) {
            throw new IllegalStateException("endpoint URL not set.");
        }
        return this.endpoint;
    }

    /**
     * Check if the endpoint is set.
     *
     * @return true if the endpoint is set, false otherwise.
     */
    public boolean isEndpointSet() {
        return endpoint != null;
    }

    /**
     * The full path to the entity or collection.
     *
     * @param parent The entity holding the relation, can be null.
     * @param relation The relation or collection to get.
     * @return the full path to the entity or collection.
     * @throws ServiceFailureException If generating the path fails.
     */
    public URL getFullPath(Entity parent, NavigationProperty relation) throws ServiceFailureException {
        try {
            return new URL(getEndpoint().toString() + ParserUtils.relationPath(parent, relation));
        } catch (MalformedURLException exc) {
            LOGGER.error("Failed to generate URL.", exc);
            throw new ServiceFailureException(exc);
        }
    }

    /**
     * The full path to the entity or collection.
     *
     * @param entityType entity type to get the path for.
     * @return the full path to the entity or collection.
     * @throws ServiceFailureException If generating the path fails.
     */
    public URL getFullPath(EntityType entityType) throws ServiceFailureException {
        try {
            return new URL(getEndpoint().toString() + entityType.mainContainer);
        } catch (MalformedURLException exc) {
            LOGGER.error("Failed to generate URL.", exc);
            throw new ServiceFailureException(exc);
        }
    }

    /**
     * Execute the given request, adding a token header if needed.
     *
     * @param request The request to execute.
     * @return the response.
     * @throws IOException in case of problems.
     */
    public CloseableHttpResponse execute(HttpRequestBase request) throws IOException {
        final String urlString = request.getURI().toString();
        if (urlReplace != null && urlString.startsWith(urlReplace)) {
            final String newUrlString = endpoint.toString() + urlString.substring(urlReplace.length());
            LOGGER.debug("   Fixed: {}", newUrlString);
            try {
                request.setURI(new URI(newUrlString));
            } catch (URISyntaxException ex) {
                throw new IOException("Failed to replace start of URL", ex);
            }
        }
        final CloseableHttpClient client = getHttpClient();
        setTimeouts(request);
        if (tokenManager != null) {
            tokenManager.addAuthHeader(request);
        }
        return client.execute(request);
    }

    private SensorThingsService setTimeouts(HttpRequestBase request) {
        RequestConfig.Builder configBuilder;
        if (request.getConfig() == null) {
            configBuilder = RequestConfig.copy(RequestConfig.DEFAULT);
        } else {
            configBuilder = RequestConfig.copy(request.getConfig());
        }
        RequestConfig config = configBuilder
                .setSocketTimeout(requestTimeoutMs)
                .setConnectTimeout(requestTimeoutMs)
                .setConnectionRequestTimeout(requestTimeoutMs)
                .build();
        request.setConfig(config);
        return this;
    }

    /**
     * Query a main entity set.
     *
     * @param type the type to query.
     * @return a new Query for the given type.
     */
    public Query query(EntityType type) {
        return new Query(this, type);
    }

    public Dao dao(EntityType type) {
        return new BaseDao(this, type);
    }

    /**
     * Create the given entity in this service. Executes a POST to the
     * Collection of the entity type. The entity will be updated with the ID of
     * the entity in the Service and it will be linked to the Service.
     *
     * @param entity The entity to create in the service.
     * @throws ServiceFailureException in case the server rejects the POST.
     */
    public void create(Entity entity) throws ServiceFailureException {
        new BaseDao(this, entity.getEntityType()).create(entity);
    }

    /**
     * Patches the entity in the Service.
     *
     * @param entity The entity to update in the service.
     * @throws ServiceFailureException in case the server rejects the PATCH.
     */
    public void update(Entity entity) throws ServiceFailureException {
        new BaseDao(this, entity.getEntityType()).update(entity);
    }

    /**
     * Update the given entity with the given patch. Does not update the entity
     * object itself. To see the result, fetch it anew from the server.
     *
     * @param entity The entity to update on the server.
     * @param patch The patch to apply to the entity.
     * @throws ServiceFailureException in case the server rejects the PATCH.
     */
    public void patch(Entity entity, List<JsonPatchOperation> patch) throws ServiceFailureException {
        new BaseDao(this, entity.getEntityType()).patch(entity, patch);
    }

    /**
     * Deletes the given entity from the service.
     *
     * @param entity The entity to delete in the service.
     * @throws ServiceFailureException in case the server rejects the DELETE.
     */
    public void delete(Entity entity) throws ServiceFailureException {
        new BaseDao(this, entity.getEntityType()).delete(entity);
    }

    /**
     * Sets the TokenManager. Before each request is sent to the Service, the
     * TokenManager has the opportunity to modify the request and add any
     * headers required for Authentication and Authorisation.
     *
     * @param tokenManager The TokenManager to use, can be null.
     * @return This SensorThingsService.
     */
    public SensorThingsService setTokenManager(TokenManager tokenManager) {
        if (tokenManager != null && httpClient != null) {
            tokenManager.setHttpClient(httpClient);
        }
        this.tokenManager = tokenManager;
        return this;
    }

    /**
     * @return The current TokenManager.
     */
    public TokenManager getTokenManager() {
        return tokenManager;
    }

    /**
     * Get the httpclient used for requests.
     *
     * @return the client
     */
    public CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = getClientBuilder().build();
            if (tokenManager != null) {
                tokenManager.setHttpClient(httpClient);
            }
        }
        return httpClient;
    }

    /**
     * Get the Builder used to generate the httpClient. If changes are made to
     * the builder after the httpClient is already generated, call {@link #rebuildHttpClient()
     * } to trigger the httpClient to be built anew.
     *
     * The clientBuilder is initialised using: {@code HttpClients.custom().useSystemProperties()
     * }
     *
     * @return The client Builder used to generate the httpClient.
     */
    public HttpClientBuilder getClientBuilder() {
        if (clientBuilder == null) {
            clientBuilder = HttpClients.custom().useSystemProperties();
        }
        return clientBuilder;
    }

    /**
     * Triggers a rebuild of the httpClient, using the latest changes to the
     * clientBuilder.
     */
    public void rebuildHttpClient() {
        httpClient = null;
    }

    public List<DataModel> getModels() {
        return Collections.unmodifiableList(models);
    }

    public Version getVersion() {
        if (version == null) {
            for (DataModel model : models) {
                version = model.getVersion();
                if (version != null) {
                    return version;
                }
            }
        }
        return version;
    }

    /**
     * Explicitly set the version for servers that do not conform to the
     * standard and do not have the version number in the URL.
     *
     * @param version the version to use.
     */
    public void setVersion(Version version) {
        this.version = version;
    }

    public int getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public SensorThingsService setRequestTimeoutMs(int requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
        return this;
    }

}
