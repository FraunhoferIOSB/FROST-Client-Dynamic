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

import de.fraunhofer.iosb.ilt.frostclient.models.DataModel;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The information needed to access a server.
 */
public class ServerInfo {

    private URL baseUrl;
    private String mqttUrl;
    private String mqttBasePath = "";

    private boolean mqttExpandAllowed = false;
    private boolean mqttFilterAllowed = false;

    private final List<DataModel> models = new ArrayList<>();

    public URL getBaseUrl() {
        return baseUrl;
    }

    public ServerInfo setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public String getMqttUrl() {
        return mqttUrl;
    }

    public ServerInfo setMqttUrl(String mqttUrl) {
        this.mqttUrl = mqttUrl;
        return this;
    }

    public String getMqttBasePath() {
        return mqttBasePath;
    }

    public ServerInfo setMqttBasePath(String mqttBasePath) {
        this.mqttBasePath = mqttBasePath;
        return this;
    }

    public List<DataModel> getModels() {
        return models;
    }

    public ServerInfo addModel(DataModel modelToAdd) {
        models.add(modelToAdd);
        if (StringHelper.isNullOrEmpty(mqttBasePath)) {
            mqttBasePath = modelToAdd.getMqttBasePath();
        }
        return this;
    }

    public ServerInfo addModels(Collection<DataModel> modelsToAdd) {
        for (var model : modelsToAdd) {
            addModel(model);
        }
        return this;
    }

    public boolean isMqttExpandAllowed() {
        return mqttExpandAllowed;
    }

    public void setMqttExpandAllowed(boolean mqttExpandAllowed) {
        this.mqttExpandAllowed = mqttExpandAllowed;
    }

    public boolean isMqttFilterAllowed() {
        return mqttFilterAllowed;
    }

    public void setMqttFilterAllowed(boolean mqttFilterAllowed) {
        this.mqttFilterAllowed = mqttFilterAllowed;
    }

}
