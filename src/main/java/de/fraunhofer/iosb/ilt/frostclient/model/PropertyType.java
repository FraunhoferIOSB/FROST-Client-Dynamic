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

import static de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry.DEFAULT_NAMESPACE;

import de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation.Annotatable;
import de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation.Annotation;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

public abstract class PropertyType implements Annotatable {

    private final String name;
    private final String description;
    private String namespace = DEFAULT_NAMESPACE;
    private CreatorDeserializer creatorDeserializer;
    private ValueDeserializer deserializer;
    private CreatorSerializer creatorSerializer;
    private ValueSerializer serializer;
    protected List<Annotation> annotations = new ArrayList<>();

    protected PropertyType(String name, String description, CreatorDeserializer cd, CreatorSerializer cs) {
        this.name = name;
        this.description = description;
        this.creatorDeserializer = cd;
        this.creatorSerializer = cs;
    }

    protected PropertyType(String name, String description, ValueDeserializer deserializer) {
        this(name, description, deserializer, ParserUtils.getDefaultSerializer());
    }

    protected PropertyType(String name, String description, ValueDeserializer deserializer, ValueSerializer serializer) {
        if (name.startsWith("Edm.")) {
            namespace = "Edm";
            name = name.substring(4);
        }
        this.name = name;
        this.description = description;
        this.deserializer = deserializer;
        this.serializer = serializer;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return ModelRegistry.fullName(namespace, name);
    }

    public String getNamespace() {
        return namespace;
    }

    public PropertyType setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ValueDeserializer getDeserializer() {
        if (deserializer == null && creatorDeserializer != null) {
            deserializer = creatorDeserializer.create(this);
        }
        return deserializer;
    }

    public ValueSerializer getSerializer() {
        if (serializer == null && creatorSerializer != null) {
            serializer = creatorSerializer.create(this);
        }
        return serializer;
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

    public static interface CreatorDeserializer {

        public ValueDeserializer create(PropertyType type);
    }

    public static interface CreatorSerializer {

        public ValueSerializer create(PropertyType type);
    }

    @Override
    public String toString() {
        return getFullName();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.namespace);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PropertyType other = (PropertyType) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.namespace, other.namespace);
    }

}
