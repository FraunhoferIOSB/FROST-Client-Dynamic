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
package de.fraunhofer.iosb.ilt.frostclient.models.ext;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.json.SimpleJsonMapper;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import de.fraunhofer.iosb.ilt.frostclient.utils.Utils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * A document sent or received using the DataArrays extension of STA 1.1.
 */
public class DataArrayDocument {

    public static final TypeReference<List<String>> LIST_OF_STRING = new TypeReference<List<String>>() {
        // Empty by design.
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(DataArrayDocument.class.getName());

    private long count = -1;
    private String nextLink;
    private final List<DataArrayValue> value = new ArrayList<>();

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getNextLink() {
        return nextLink;
    }

    public void setNextLink(String nextLink) {
        this.nextLink = nextLink;
    }

    public void addDataArrayValue(DataArrayValue dav) {
        value.add(dav);
    }

    public List<DataArrayValue> getValue() {
        return value;
    }

    /**
     * All observations in all Datastreams in this DataArrayDocument.
     *
     * @return All observations in all Datastreams in this DataArrayDocument.
     */
    public List<Entity> getObservations() {
        List<Entity> retval = new ArrayList<>();
        for (DataArrayValue dav : value) {
            retval.addAll(dav.getObservations());
        }
        return retval;
    }

    /**
     * Send the DataArrayDocument to the given service.
     *
     * @param service The service to create the observations on.
     * @return The response of the service.
     * @throws ServiceFailureException in case the server rejects the POST.
     */
    public List<String> create(SensorThingsService service) throws ServiceFailureException {
        List<String> result = new ArrayList<>();
        final ObjectMapper mapper = SimpleJsonMapper.getSimpleObjectMapper();
        URIBuilder uriBuilder;
        HttpPost httpPost;
        String json;
        try {
            json = mapper.writeValueAsString(this.getValue());
            uriBuilder = new URIBuilder(service.getBaseUrl() + "CreateObservations");
            httpPost = new HttpPost(uriBuilder.build());
        } catch (JacksonException | URISyntaxException ex) {
            throw new ServiceFailureException("Failed to create Observations.", ex);
        }

        LOGGER.debug("Posting to: {}", httpPost.getURI());
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = service.execute(httpPost)) {

            Utils.throwIfNotOk(httpPost, response);

            String jsonResponse = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            result = mapper.readValue(jsonResponse, LIST_OF_STRING);
            List<Entity> observations = this.getObservations();
            if (observations.size() != result.size()) {
                LOGGER.error("Size of returned location list ({}) is not equal to number of sent Observations ({})!", result.size(), observations.size());
            }
            int i = 0;
            for (Entity o : observations) {
                String newLocation = result.get(i);
                if (newLocation.startsWith("error")) {
                    LOGGER.warn("Failed to insert Observation. Error: {}.", newLocation);
                } else {
                    int pos1 = newLocation.indexOf('(') + 1;
                    int pos2 = newLocation.indexOf(')', pos1);
                    String stringPkValue = newLocation.substring(pos1, pos2);
                    o.setPrimaryKeyValues(ParserUtils.tryToParse(stringPkValue));
                    o.setService(service);
                }
                i++;
            }

        } catch (IOException exc) {
            throw new ServiceFailureException("Failed to create Observations.", exc);
        }
        return result;
    }
}
