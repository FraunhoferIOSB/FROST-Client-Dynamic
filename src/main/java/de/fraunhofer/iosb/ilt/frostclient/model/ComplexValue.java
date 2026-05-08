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
package de.fraunhofer.iosb.ilt.frostclient.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collections;
import java.util.Map;

/**
 * Interface that values of complex properties should implement to make it
 * easier to access sub-properties.
 *
 * @param <S> The type of the complex value (for fluent API)
 */
public interface ComplexValue<S extends ComplexValue<S>> {

    /**
     * Get the value of the given property.
     *
     * @param <P> The type of the property and value.
     * @param property The property to get the value of.
     * @return the value of the requested property.
     */
    public <P> P getProperty(Property<P> property);

    /**
     * Set the given property to the given value.
     *
     * @param <P> The type of the property.
     * @param property The property to set.
     * @param value The value to set the property to.
     * @return this.
     */
    public <P> S setProperty(Property<P> property, P value);

    /**
     * Get the custom property with the given name. Only valid for ComplexTypes
     * that are classed as openType, returns null for non-openTypes.
     *
     * @param name The name of the custom property to fetch.
     * @return The value of the custom property.
     */
    public Object getProperty(String name);

    /**
     * Set the custom property with the given name to the given value. Only
     * valid for ComplexTypes that are classed as openType.
     *
     * @param name The name of the custom property to set.
     * @param value The value of the custom property to set.
     * @return this.
     */
    public S setProperty(String name, Object value);

    /**
     * Get the extra (open) content of the type. This excludes the defined
     * properties of the type.
     *
     * @return The extra content.
     */
    public default Map<String, Object> getContent() {
        return Collections.emptyMap();
    }

    @JsonIgnore
    public ContainerType<?> getType();
}
