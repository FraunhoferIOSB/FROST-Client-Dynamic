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
package de.fraunhofer.iosb.ilt.frostclient.models;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.Version;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;

/**
 * The interface for classes that implement a data model.
 */
public interface DataModel {

    /**
     * Initialise the data model in the given ModelRegistry.
     *
     * @param service The service to use for loading data needed for
     * initialisation.
     * @param mr The ModelRegistry to initialise the data model in.
     */
    public void init(SensorThingsService service, ModelRegistry mr);

    /**
     * Check if the model is initialised.
     *
     * @return true if initialised.
     */
    public boolean isInitialised();

    public default Version getVersion() {
        return null;
    }

    /**
     * Get the base MQTT path that the model defines. For instance "v1.1/" for
     * SensorThings API version 1.1. If this base path is not empty, it MUST end
     * in a slash.
     *
     * @return the mqtt base path, if the model defined any, or an empty string.
     */
    public default String getMqttBasePath() {
        return "";
    }
}
