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
package de.fraunhofer.iosb.ilt.frostclient.models;

import de.fraunhofer.iosb.ilt.configurable.AnnotatedConfigurable;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableClass;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorString;
import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.Version;
import de.fraunhofer.iosb.ilt.frostclient.exception.StatusCodeException;
import de.fraunhofer.iosb.ilt.frostclient.json.SimpleJsonMapper;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.csdl.CsdlDocument;
import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import de.fraunhofer.iosb.ilt.frostclient.utils.Utils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

@ConfigurableClass
public class CSDLModel implements DataModel, AnnotatedConfigurable<Object, Object> {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CSDLModel.class.getName());
    private static final String DEFAULT_CONTEXT_PATH = "$metadata?$format=json";

    private ModelRegistry mr;

    @ConfigurableField(editor = EditorString.class, optional = true,
            label = "ContextPath", description = "The path to the context document, either absolute or relative to the service base url.")
    @EditorString.EdOptsString(dflt = DEFAULT_CONTEXT_PATH)
    private String contextPath;

    @Override
    public void init(SensorThingsService service, ModelRegistry mr) {
        if (StringHelper.isNullOrEmpty(contextPath)) {
            contextPath = DEFAULT_CONTEXT_PATH;
        }
        HttpGet httpGet;
        try {
            URL contextUrl = new URL(service.getEndpoint(), contextPath);
            httpGet = new HttpGet(contextUrl.toURI());
            LOGGER.debug("Fetching: {}", httpGet.getURI());
            httpGet.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        } catch (URISyntaxException | MalformedURLException ex) {
            throw new IllegalArgumentException("Could not generate context url.", ex);
        }

        try (CloseableHttpResponse response = service.execute(httpGet)) {
            Utils.throwIfNotOk(httpGet, response);
            String json = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            CsdlDocument csdlDocument = SimpleJsonMapper.getSimpleObjectMapper().readValue(json, CsdlDocument.class);
            csdlDocument.applyTo(mr);
        } catch (IOException ex) {
            LOGGER.error("Failed to parse metadata", ex);
        } catch (StatusCodeException ex) {
            LOGGER.error("Failed to request metadata", ex);
        }

    }

    public String getContextPath() {
        return contextPath;
    }

    public CSDLModel setContextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    @Override
    public boolean isInitialised() {
        return mr != null;
    }

    @Override
    public Version getVersion() {
        return Version.V_ODATA_4_01;
    }

}
