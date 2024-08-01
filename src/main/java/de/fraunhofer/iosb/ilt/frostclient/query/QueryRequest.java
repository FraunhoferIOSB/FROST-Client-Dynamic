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
package de.fraunhofer.iosb.ilt.frostclient.query;

import de.fraunhofer.iosb.ilt.frostclient.exception.MqttException;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.utils.MqttSubscription;

/**
 * Request methods a query should support.
 *
 * @param <T> The exact type of QueryRequest.
 */
public interface QueryRequest<T extends QueryRequest> {

    /**
     * Get the first instance of an entity collection.
     *
     * @return the first instance
     * @throws ServiceFailureException the request failed
     */
    Entity first() throws ServiceFailureException;

    /**
     * Get an entity collection.
     *
     * @return the entity collection
     * @throws ServiceFailureException the request failed
     */
    EntitySet list() throws ServiceFailureException;

    /**
     * Subscribe to the topic defined by this request and initialise the given
     * MqttSubscription. The topic and returnType will be set on the given
     * MqttSubscription.
     *
     * @param sub The subscription without topic, but with a handler.
     * @return this.
     * @throws MqttException is subscription fails.
     */
    T subscribe(MqttSubscription sub) throws MqttException;

}
