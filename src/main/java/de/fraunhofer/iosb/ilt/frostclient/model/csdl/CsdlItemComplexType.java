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
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.Property;
import de.fraunhofer.iosb.ilt.frostclient.model.PropertyType;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CsdlItemComplexType extends CsdlSchemaItemAbstract {

    public static final String NAME_KIND_COMPLEXTYPE = "ComplexType";

    @JsonProperty("$Abstract")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public boolean isAbstract;

    @JsonProperty("$BaseType")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public String baseType;

    @JsonProperty("$OpenType")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public boolean openType;

    @JsonProperty("@Core.Description")
    public String description;

    @JsonAnyGetter
    public Map<String, CsdlProperty> properties = new LinkedHashMap<>();

    public CsdlItemComplexType() {
        super(NAME_KIND_COMPLEXTYPE);
    }

    public CsdlItemComplexType fillFrom(CsdlDocument doc, String nameSpace, TypeComplex tc) {
        description = tc.getDescription();
        openType = tc.isOpenType();
        for (Property property : tc.getProperties()) {
            final String name = property.getName();
            final PropertyType type = property.getType();
            final boolean nullable = property.isNullable();
            properties.put(name, CsdlPropertyEntity.of(doc, nameSpace, type, nullable));
        }
        return this;
    }

    public void applyTo(ModelRegistry mr, String name) {
        TypeComplex type = new TypeComplex(name, description, openType);
        type.setDeserializer(ParserUtils.getComplexTypeDeserializer(type));
        type.setSerializer(ParserUtils.getDefaultSerializer());
        for (var propEntry : properties.entrySet()) {
            String propName = propEntry.getKey();
            CsdlProperty property = propEntry.getValue();
            property.applyTo(mr, type, propName);
        }
        mr.registerPropertyType(type);
    }

    public CsdlItemComplexType setOpenType(boolean openType) {
        this.openType = openType;
        return this;
    }

    @JsonAnySetter
    public void addProperty(String name, Object data) {
        if (name.startsWith("@")) {

        } else if (data instanceof Map map) {
            properties.put(name, CsdlProperty.of(name, map));
        }
    }

    @Override
    public void writeXml(String nameSpace, String name, Writer writer) throws IOException {
        writer.write("<ComplexType Name=\"" + name + "\" OpenType=\"" + Boolean.toString(openType) + "\">");
        for (Entry<String, CsdlProperty> entry : properties.entrySet()) {
            String propName = entry.getKey();
            CsdlProperty property = entry.getValue();
            property.writeXml(nameSpace, propName, writer);
        }
        writer.write("</ComplexType>");
    }

    public static CsdlItemComplexType of(CsdlDocument doc, String nameSpace, TypeComplex tc) {
        return new CsdlItemComplexType().fillFrom(doc, nameSpace, tc);
    }
}
