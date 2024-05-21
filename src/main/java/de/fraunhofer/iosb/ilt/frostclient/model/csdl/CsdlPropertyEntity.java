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
package de.fraunhofer.iosb.ilt.frostclient.model.csdl;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.PropertyType;
import de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation.Annotation;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeCollection;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The CSDL representation of an EntityProperty.
 */
public class CsdlPropertyEntity extends CsdlProperty {

    public static final String NAME_KIND_ENTITYPROPERTY = "Property";

    private static final String TYPE_DEFAULT = "Edm.String";

    @JsonProperty("$Type")
    public String type;

    @JsonProperty("$Nullable")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public boolean nullable;

    @JsonProperty("$Collection")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public boolean collection;

    @JsonIgnore
    private final List<CsdlAnnotation> annotations = new ArrayList<>();

    public CsdlPropertyEntity() {
        super(NAME_KIND_ENTITYPROPERTY);
    }

    public CsdlPropertyEntity fillFrom(CsdlDocument doc, String nameSpace, EntityType et, EntityPropertyMain<?> ep) {
        final PropertyType propertyType = ep.getType();
        type = propertyType.getName();
        collection = propertyType.isCollection();
        if (et.getPrimaryKey() != ep) {
            nullable = ep.isNullable();
        }
        for (Annotation an : ep.getAnnotations()) {
            annotations.add(CsdlAnnotation.of(doc, an));
        }
        return this;
    }

    public CsdlPropertyEntity fillFrom(CsdlDocument doc, String nameSpace, PropertyType propertyType, boolean nullable) {
        type = propertyType.getName();
        this.nullable = nullable;
        collection = propertyType.isCollection();
        for (Annotation an : propertyType.getAnnotations()) {
            annotations.add(CsdlAnnotation.of(doc, an));
        }
        return this;
    }

    @Override
    public void applyTo(ModelRegistry mr, EntityType entityType, String name) {
        EntityPropertyMain ep = createProperty(mr, name);
        entityType.registerProperty(ep);
    }

    @Override
    public void applyTo(ModelRegistry mr, TypeComplex tc, String name) {
        EntityPropertyMain ep = createProperty(mr, name);
        tc.registerProperty(ep);
    }

    private EntityPropertyMain createProperty(ModelRegistry mr, String name) throws IllegalArgumentException {
        PropertyType propertyType = mr.getPropertyType(type);
        if (collection) {
            if (propertyType instanceof TypePrimitive ptp) {
                propertyType = new TypeCollection(ptp, null);
            } else if (propertyType instanceof TypeComplex ptc) {
                propertyType = new TypeCollection(ptc, null);
            } else {
                throw new IllegalArgumentException("Can't create Type for Set of " + propertyType.getName());
            }
        }
        EntityPropertyMain ep = new EntityPropertyMain(name, propertyType);
        ep.setNullable(nullable);
        return ep;
    }

    @JsonAnyGetter
    public Map<String, Object> otherProperties() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (CsdlAnnotation annotation : annotations) {
            result.put('@' + annotation.getQualifiedName(), annotation.getValue());
        }
        return result;
    }

    @Override
    public void writeXml(String nameSpace, String name, Writer writer) throws IOException {
        String typeString = type == null ? TYPE_DEFAULT : type;
        if (collection) {
            typeString = "Collection(" + typeString + ")";
        }
        String nullableString = (nullable) ? " Nullable=\"" + Boolean.toString(nullable) + "\"" : "";
        writer.write("<Property Name=\"" + name + "\" Type=\"" + typeString + "\"" + nullableString);
        if (annotations.isEmpty()) {
            writer.write(" />");
        } else {
            writer.write(">");
            for (CsdlAnnotation an : annotations) {
                an.writeXml(writer);
            }
            writer.write("</Property>");
        }
    }

    public CsdlPropertyEntity setType(String type) {
        this.type = type;
        return this;
    }

    public CsdlPropertyEntity setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public CsdlPropertyEntity setCollection(boolean collection) {
        this.collection = collection;
        return this;
    }

    public static CsdlPropertyEntity of(CsdlDocument doc, String nameSpace, EntityType et, EntityPropertyMain<?> ep) {
        return new CsdlPropertyEntity().fillFrom(doc, nameSpace, et, ep);
    }

    public static CsdlPropertyEntity of(CsdlDocument doc, String nameSpace, PropertyType type, boolean nullable) {
        return new CsdlPropertyEntity().fillFrom(doc, nameSpace, type, nullable);
    }
}
