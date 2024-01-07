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
package de.fraunhofer.iosb.ilt.frostclient.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ComplexValueImpl implements ComplexValue<ComplexValueImpl> {

    public static final TypeReference<ComplexValueImpl> TYPE_REFERENCE = new TypeReference<ComplexValueImpl>() {
        // Empty by design.
    };

    private final TypeComplex type;
    private final Map<String, Object> properties = new LinkedHashMap<>();

    public ComplexValueImpl(TypeComplex type) {
        this.type = type;
    }

    @JsonIgnore
    public TypeComplex getType() {
        return type;
    }

    @Override
    public <P> P getProperty(Property<P> property) {
        return (P) properties.get(property.getJsonName());
    }

    @Override
    public <P> ComplexValueImpl setProperty(Property<P> property, P value) {
        properties.put(property.getJsonName(), value);
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAllProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @JsonAnySetter
    public void setAnyProperty(String name, Object value) {
        properties.put(name, value);
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public ComplexValueImpl setProperty(String name, Object value) {
        if (!type.isOpenType()) {
            throw new IllegalArgumentException("Can not set custom properties on non-openType " + type);
        }
        properties.put(name, value);
        return this;
    }

    public static TypeComplex.Instantiator createFor(TypeComplex type) {
        return () -> new ComplexValueImpl(type);
    }

}
