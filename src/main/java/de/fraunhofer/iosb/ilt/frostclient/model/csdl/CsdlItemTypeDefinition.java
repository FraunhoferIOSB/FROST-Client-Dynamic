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

import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.PropertyType;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeSimple;
import java.io.IOException;
import java.io.Writer;

public class CsdlItemTypeDefinition extends CsdlSchemaItemAbstract {

    public static final String NAME_KIND_TYPEDEFINITION = "TypeDefinition";

    @JsonProperty("$UnderlyingType")
    public String underlyingType;

    @JsonProperty("@Core.Description")
    public String description;

    public CsdlItemTypeDefinition() {
        super(NAME_KIND_TYPEDEFINITION);
    }

    public CsdlItemTypeDefinition fillFrom(TypeSimple tc) {
        underlyingType = tc.getUnderlyingType().getName();
        description = tc.getDescription();
        return this;
    }

    public void applyTo(ModelRegistry mr, String name) {
        final PropertyType ut = mr.getPropertyType(underlyingType);
        if (ut == null) {
            throw new IllegalArgumentException("UnderlyingType of TypeDefinition " + name + " not found: " + underlyingType);
        }
        if (ut instanceof TypePrimitive pt) {
            mr.registerPropertyType(new TypeSimple(name, description, pt));
        } else {
            throw new IllegalArgumentException("UnderlyingType of TypeDefinition MUST be a PrimitiveType!");
        }
    }

    @Override
    public void writeXml(String nameSpace, String name, Writer writer) throws IOException {
        writer.write("<TypeDefinition Name=\"" + name + "\" UnderlyingType=\"" + underlyingType + "\" />");
    }

    public static CsdlItemTypeDefinition of(CsdlDocument doc, String nameSpace, TypeSimple ts) {
        return new CsdlItemTypeDefinition().fillFrom(ts);
    }
}
