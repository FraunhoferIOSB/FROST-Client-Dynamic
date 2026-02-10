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

import de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation.Annotatable;
import de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation.Annotation;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import java.util.ArrayList;
import java.util.List;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

public abstract class PropertyType implements Annotatable {

    private final String name;
    private final String description;
    private ValueDeserializer deserializer;
    private ValueSerializer serializer;
    protected List<Annotation> annotations = new ArrayList<>();

    protected PropertyType(String name, String description, ValueDeserializer deserializer, ValueSerializer serializer) {
        this.name = name;
        this.description = description;
        this.deserializer = deserializer;
        this.serializer = serializer;
    }

    protected PropertyType(String name, String description, ValueDeserializer deserializer) {
        this(name, description, deserializer, ParserUtils.getDefaultSerializer());
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ValueDeserializer getDeserializer() {
        return deserializer;
    }

    public PropertyType setDeserializer(ValueDeserializer deserializer) {
        this.deserializer = deserializer;
        return this;
    }

    public ValueSerializer getSerializer() {
        return serializer;
    }

    public PropertyType setSerializer(ValueSerializer serializer) {
        this.serializer = serializer;
        return this;
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
