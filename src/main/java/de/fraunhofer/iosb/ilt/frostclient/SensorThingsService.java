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

import static de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper.isNullOrEmpty;

import com.github.fge.jsonpatch.JsonPatchOperation;
import de.fraunhofer.iosb.ilt.frostclient.auth.AuthMethod;
import de.fraunhofer.iosb.ilt.frostclient.dao.BaseDao;
import de.fraunhofer.iosb.ilt.frostclient.dao.Dao;
import de.fraunhofer.iosb.ilt.frostclient.exception.MqttException;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.json.deserialize.JsonReader;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationProperty;
import de.fraunhofer.iosb.ilt.frostclient.models.DataModel;
import de.fraunhofer.iosb.ilt.frostclient.query.Query;
import de.fraunhofer.iosb.ilt.frostclient.settings.Settings;
import de.fraunhofer.iosb.ilt.frostclient.utils.MqttConfig;
import de.fraunhofer.iosb.ilt.frostclient.utils.MqttSubscription;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import de.fraunhofer.iosb.ilt.frostclient.utils.ServerInfo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.LoggerFactory;

/**
 * A SensorThingsService represents the service endpoint of a server.
 */
public class SensorThingsService {

    /**
     * The logger for this class.
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SensorThingsService.class);

    public static final URL NULL_URL_V11;

    // Static initialiser because all URL Constructors can throw.
    static {
        URL tempUrl = null;
        try {
            tempUrl = new URL("http://example.org/v1.1/");
        } catch (MalformedURLException ex) {
        }
        NULL_URL_V11 = tempUrl;
    }

    private final ModelRegistry modelRegistry;
    private final ServerInfo serverInfo = new ServerInfo();
    private JsonReader jsonReader;
    private String urlReplace;

    private HttpClientBuilder clientBuilder;
    private CloseableHttpClient httpClient;
    private MqttConfig mqttConfig;
    private MqttClient mqttClient;
    private final MqttCallback mqttCallback = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            LOGGER.info("MQTT connection established");
        }

        @Override
        public void connectionLost(Throwable cause) {
            LOGGER.warn("MQTT connection lost, details in debug");
            LOGGER.debug("MQTT connection lost", cause);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            handleMessage(topic, message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            LOGGER.debug("Publish completed.");
        }
    };

    private final Map<String, Set<MqttSubscription>> mqttSubscriptions = new HashMap<>();

    private ServiceSettings settings;
    private TokenManager tokenManager;

    /**
     * The request timeout in MS.
     */
    private int requestTimeoutMs = 120000;

    private boolean initialised = false;

    /**
     * Create a new SensorThingsService using the setting from the given
     * ServiceSettings object.
     *
     * @param settings The settings provider.
     */
    public SensorThingsService(Settings settings) {
        this.settings = ServiceSettings.of(settings);
        this.modelRegistry = new ModelRegistry();
    }

    /**
     * Creates a new SensorThingsService without a base url set. The base url
     * MUST be set before the service can be used. The models will be
     * initialised in the order they are passed.
     *
     * @param models The data models to use.
     */
    public SensorThingsService(DataModel... models) {
        this(Arrays.asList(models));
    }

    /**
     * Creates a new SensorThingsService without a base url set. The base url
     * MUST be set before the service can be used. The models will be
     * initialised in the order they are returned by the list.
     *
     * @param models The data models to use.
     */
    public SensorThingsService(List<DataModel> models) {
        modelRegistry = new ModelRegistry();
        serverInfo.addModels(models);
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

    public SensorThingsService setModels(List<DataModel> models) {
        serverInfo.addModels(models);
        return this;
    }

    public SensorThingsService setModels(DataModel... models) {
        serverInfo.addModels(Arrays.asList(models));
        return this;
    }

    public SensorThingsService init() throws MalformedURLException {
        if (initialised) {
            return this;
        }
        getSettings();
        requestTimeoutMs = settings.getRequestTimeoutMs();
        if (serverInfo.getModels().isEmpty()) {
            serverInfo.addModels(settings.getModels());
        }

        settings.getAuthSettings().load(this);

        if (!serverInfo.isBaseUrlSet()) {
            String baseUrl = settings.getBaseUrl();
            if (isNullOrEmpty(baseUrl)) {
                throw new IllegalArgumentException("Base URL must be set before init is called.");
            }
            setBaseUrl(URI.create(baseUrl));
        }
        if (!serverInfo.isMqttUrlSet()) {
            String mqttUrl = settings.getMqttUrl();
            if (!isNullOrEmpty(mqttUrl)) {
                serverInfo.setMqttUrl(mqttUrl);
            }
        }
        if (!modelRegistry.isInitialised()) {
            Utils.detectServerInfo(this);
        }

        initModels();
        initialised = true;
        return this;
    }

    public ServiceSettings getSettings() {
        if (settings == null) {
            settings = new ServiceSettings();
        }
        return settings;
    }

    public ModelRegistry getModelRegistry() {
        return modelRegistry;
    }

    private void initModels() {
        for (DataModel model : serverInfo.getModels()) {
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

    public SensorThingsService setAuthMethod(AuthMethod authMethod) {
        getSettings().getAuthSettings().setAuthMethod(authMethod);
        return this;
    }

    /**
     * Sets the endpoint URL/URI. Once the endpoint URL/URI is set it can not be
     * changed. The endpoint url MUST be set before the service can be used.
     *
     * @param endpoint The URI of the endpoint.
     * @return this.
     * @throws java.net.MalformedURLException when building the final url fails.
     */
    public final SensorThingsService setBaseUrl(URI endpoint) throws MalformedURLException {
        return setBaseUrl(endpoint.toURL());
    }

    /**
     * Sets the endpoint URL/URI. Once the endpoint URL/URI is set it can not be
     * changed. The endpoint url MUST be set before the service can be used.
     *
     * @param endpoint The URL of the endpoint.
     * @return this.
     * @throws MalformedURLException when building the final URL fails.
     */
    public final SensorThingsService setBaseUrl(URL endpoint) throws MalformedURLException {
        if (serverInfo.getBaseUrl() != null) {
            throw new IllegalStateException("endpoint URL already set.");
        }
        String url = StringUtils.removeEnd(endpoint.toString(), "/");
        serverInfo.setBaseUrl(URI.create(url + "/").toURL());
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
    public URL getBaseUrl() {
        if (serverInfo.getBaseUrl() == null) {
            throw new IllegalStateException("endpoint URL not set.");
        }
        return serverInfo.getBaseUrl();
    }

    /**
     * Check if the endpoint is set.
     *
     * @return true if the endpoint is set, false otherwise.
     */
    public boolean isBaseUrlSet() {
        return serverInfo.getBaseUrl() != null;
    }

    public String getFullPathString(Entity parent, NavigationProperty relation) {
        return getBaseUrl().toString() + ParserUtils.relationPath(parent, relation);
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
            return new URL(getBaseUrl().toString() + ParserUtils.relationPath(parent, relation));
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
            return new URL(getBaseUrl().toString() + entityType.mainSet);
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
        if (!isNullOrEmpty(urlReplace) && urlString.startsWith(urlReplace)) {
            final String newUrlString = serverInfo.getBaseUrl().toString() + urlString.substring(urlReplace.length());
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
        return Collections.unmodifiableList(serverInfo.getModels());
    }

    public Version getVersion() {
        return serverInfo.getVersion();
    }

    /**
     * Explicitly set the version for servers that do not conform to the
     * standard and do not have the version number in the URL.
     *
     * @param version the version to use.
     * @return this.
     */
    public SensorThingsService setVersion(Version version) {
        this.serverInfo.setVersion(version);
        return this;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public int getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public SensorThingsService setRequestTimeoutMs(int requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
        return this;
    }

    public MqttConfig getMqttConfig() {
        return mqttConfig;
    }

    public MqttConfig getOrCreateMqttConfig() {
        if (mqttConfig == null) {
            LOGGER.info("Using default MQTT configuration");
            mqttConfig = new MqttConfig();
        }
        return mqttConfig;
    }

    public void setMqttConfig(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    /**
     * Start a MQTT subscription
     *
     * @param topic The MQTT topic to subscribe
     * @param handler is called when a new notification happens (if result
     * satisfies the filter)
     * @param returnType type to cast the result to
     * @return A subscription object
     * @throws MqttException when subscription fails
     */
    public MqttSubscription subscribe(String topic, Consumer<Entity> handler, EntityType returnType) throws MqttException {
        var sub = new MqttSubscription(topic, returnType)
                .setHandler(handler);
        subscribe(sub);
        return sub;
    }

    /**
     * Start a MQTT subscription
     *
     * @param sub The details of the subscription.
     * @throws MqttException when subscription fails
     */
    public void subscribe(MqttSubscription sub) throws MqttException {
        ensureMqttConnected();
        synchronized (mqttSubscriptions) {
            String topic = sub.getTopic();
            Set<MqttSubscription> subSet = mqttSubscriptions.computeIfAbsent(topic, t -> new CopyOnWriteArraySet<>());
            if (subSet.add(sub) && subSet.size() == 1) {
                // First subscription for this topic.
                try {
                    mqttClient.subscribe(topic);
                } catch (org.eclipse.paho.client.mqttv3.MqttException exc) {
                    throw new MqttException(String.format("subscribing topic '%s' failed", topic), exc);
                }
            }
        }
    }

    public void unSubscribe(MqttSubscription sub) throws MqttException {
        final String topic = sub.getTopic();
        synchronized (mqttSubscriptions) {
            Set<MqttSubscription> subSet = mqttSubscriptions.get(topic);
            if (subSet == null) {
                LOGGER.info("No subscriptions found for topic {}", topic);
                return;
            }
            if (subSet.remove(sub)) {
                if (subSet.isEmpty()) {
                    // Last subscription for this topic removed.
                    mqttSubscriptions.remove(topic);
                    try {
                        mqttClient.unsubscribe(topic);
                    } catch (org.eclipse.paho.client.mqttv3.MqttException ex) {
                        throw new MqttException("Failed to unsubscribe", ex);
                    }
                }
            }
        }
    }

    /**
     * Removed all MqttSubscription for the given topic and unsubscribes.
     *
     * @param topic The topic to remove all subscriptions for.
     * @throws MqttException if unsubscribe fails.
     */
    public void unSubscribeAll(String topic) throws MqttException {
        mqttSubscriptions.remove(topic);
        if (mqttClient == null) {
            return;
        }
        try {
            mqttClient.unsubscribe(topic);
        } catch (org.eclipse.paho.client.mqttv3.MqttException ex) {
            throw new MqttException("Failed to unsubscribe", ex);
        }
    }

    private void handleMessage(String topic, MqttMessage message) {
        for (MqttSubscription sub : mqttSubscriptions.getOrDefault(topic, Collections.emptySet())) {
            try {
                Entity entity = jsonReader.parseEntity(sub.getReturnType(), message.toString());
                Predicate<Entity> filter = sub.getFilter();
                if (filter == null || filter.test(entity)) {
                    sub.getHandler().accept(entity);
                }
            } catch (RuntimeException | IOException ex) {
                LOGGER.error("Exception while handling message.", ex);
            }
        }
    }

    public boolean isMqttConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    public void mqttResubscribe() throws MqttException {
        ensureMqttConnected();
        synchronized (mqttSubscriptions) {
            List<String> topics = new ArrayList<>();
            int count = 0;
            for (String topic : mqttSubscriptions.keySet()) {
                topics.add(topic);
                count++;
                if (count == 100) {
                    sendSubscribe(topics);
                    count = 0;
                    topics.clear();
                }
            }
            if (count > 0) {
                sendSubscribe(topics);
                topics.clear();
            }
        }
    }

    private void sendSubscribe(List<String> topics) throws MqttException {
        try {
            mqttClient.subscribe(topics.toArray(String[]::new));
        } catch (org.eclipse.paho.client.mqttv3.MqttException ex) {
            throw new MqttException("Failed to resubscribe", ex);
        }
    }

    private void ensureMqttConnected() throws MqttException {
        ensureMqttConfigured();
        if (mqttClient.isConnected()) {
            return;
        }
        try {
            final MqttConnectOptions options = mqttConfig.getOptions();
            if (mqttConfig.isAuthSet()) {
                options.setUserName(mqttConfig.getUsername());
                options.setPassword(mqttConfig.getPassword().toCharArray());
                options.setAutomaticReconnect(true);
            }
            mqttClient.connect(options);
        } catch (org.eclipse.paho.client.mqttv3.MqttException exc) {
            throw new MqttException("MQTT connection failed", exc);
        }
    }

    private void ensureMqttConfigured() throws MqttException {
        if (mqttClient == null) {
            if (mqttConfig == null) {
                LOGGER.info("Using default MQTT configuration");
                mqttConfig = new MqttConfig();
            }
            try {
                mqttClient = new MqttClient(serverInfo.getMqttUrl(), mqttConfig.getClientId(), mqttConfig.getPersistence());
                mqttClient.setCallback(mqttCallback);
            } catch (org.eclipse.paho.client.mqttv3.MqttException exc) {
                throw new MqttException("could not create MQTT client", exc);
            }
        }
    }

    /**
     * Unsubscribes all topics and closes the connection.
     */
    public void cleanupMqtt() {
        // Copy the topics, since unsubscribing changes the mqttSubscriptions.
        List<String> topics = new ArrayList<>(mqttSubscriptions.keySet());
        topics.forEach((topic) -> {
            try {
                unSubscribeAll(topic);
            } catch (MqttException ex) {
                LOGGER.warn("error unsubscribing from MQTT", ex);
            }
        });
        if (mqttClient != null) {
            try {
                mqttClient.disconnect();
                mqttClient.close(true);
            } catch (org.eclipse.paho.client.mqttv3.MqttException ex) {
                LOGGER.warn("error closing MQTT conection", ex);
            }
        }
        mqttClient = null;
    }

}
