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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fraunhofer.iosb.ilt.frostclient.model.ComplexValue;
import de.fraunhofer.iosb.ilt.frostclient.model.Property;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The complex value for an open type.
 */
public class MapValue implements ComplexValue<MapValue> {

    private final Map<String, Object> content;

    public MapValue() {
        this.content = new LinkedHashMap<>();
    }

    public MapValue(Map<String, Object> content) {
        this.content = content;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return content.isEmpty();
    }

    @JsonAnyGetter
    public Map<String, Object> getContent() {
        return content;
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return content.entrySet();
    }

    @JsonIgnore
    public Object get(String name) {
        return content.get(name);
    }

    public boolean containsKey(String name) {
        return content.containsKey(name);
    }

    @JsonAnySetter
    public MapValue put(String name, Object value) {
        content.put(name, value);
        return this;
    }

    @JsonIgnore
    @Override
    public <P> P getProperty(Property<P> property) {
        return (P) content.get(property.getJsonName());
    }

    @JsonIgnore
    @Override
    public <P> MapValue setProperty(Property<P> property, P value) {
        content.put(property.getJsonName(), value);
        return this;
    }

    @Override
    public Object getProperty(String name) {
        return content.get(name);
    }

    @Override
    public MapValue setProperty(String name, Object value) {
        content.put(name, value);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MapValue o) {
            return content.equals(o.content);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return 79 * hash + Objects.hashCode(content);
    }

}
