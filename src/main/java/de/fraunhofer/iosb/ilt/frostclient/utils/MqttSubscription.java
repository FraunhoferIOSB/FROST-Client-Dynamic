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

import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * An MQTT topic with a handler.
 */
public class MqttSubscription {

    private EntityType returnType;
    private String topic;
    private Predicate<Entity> filter;
    private Consumer<Entity> handler;

    public MqttSubscription(EntityType returnType) {
        this.returnType = returnType;
    }

    public MqttSubscription(String topic, EntityType returnType) {
        this.topic = topic;
        this.returnType = returnType;
    }

    public String getTopic() {
        return topic;
    }

    public MqttSubscription setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public EntityType getReturnType() {
        return returnType;
    }

    public MqttSubscription setReturnType(EntityType returnType) {
        this.returnType = returnType;
        return this;
    }

    public Predicate<Entity> getFilter() {
        return filter;
    }

    public MqttSubscription setFilter(Predicate<Entity> filter) {
        this.filter = filter;
        return this;
    }

    public Consumer<Entity> getHandler() {
        return handler;
    }

    public MqttSubscription setHandler(Consumer<Entity> handler) {
        this.handler = handler;
        return this;
    }

}
