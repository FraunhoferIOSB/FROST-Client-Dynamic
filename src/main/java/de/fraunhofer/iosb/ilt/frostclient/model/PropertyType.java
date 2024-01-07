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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation.Annotatable;
import de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation.Annotation;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import java.util.ArrayList;
import java.util.List;

public class PropertyType implements Annotatable {

    private final String name;
    private final String description;
    private JsonDeserializer deserializer;
    private JsonSerializer serializer;
    protected List<Annotation> annotations = new ArrayList<>();

    public PropertyType(String name, String description, JsonDeserializer deserializer, JsonSerializer serializer) {
        this.name = name;
        this.description = description;
        this.deserializer = deserializer;
        this.serializer = serializer;
    }

    public PropertyType(String name, String description, JsonDeserializer deserializer) {
        this(name, description, deserializer, ParserUtils.getDefaultSerializer());
    }

    public PropertyType(String name, String description, TypeReference tr) {
        this(name, description, ParserUtils.getDefaultDeserializer(tr), ParserUtils.getDefaultSerializer());
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public JsonDeserializer getDeserializer() {
        return deserializer;
    }

    public void setDeserializer(JsonDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    public JsonSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(JsonSerializer serializer) {
        this.serializer = serializer;
    }

    public boolean isCollection() {
        return false;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public PropertyType setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
        return this;
    }

    public PropertyType addAnnotation(Annotation annotation) {
        annotations.add(annotation);
        return this;
    }

}
