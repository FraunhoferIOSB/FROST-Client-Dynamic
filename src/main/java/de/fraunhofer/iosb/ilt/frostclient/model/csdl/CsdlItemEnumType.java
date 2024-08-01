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
package de.fraunhofer.iosb.ilt.frostclient.model.csdl;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeEnumeration;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class CsdlItemEnumType extends CsdlSchemaItemAbstract {

    public static final String NAME_KIND_ENUMTYPE = "EnumType";

    @JsonProperty("@Core.Description")
    public String description;

    @JsonAnyGetter
    @JsonAnySetter
    public Map<String, Number> values = new LinkedHashMap<>();

    public CsdlItemEnumType() {
        super(NAME_KIND_ENUMTYPE);
    }

    public CsdlItemEnumType fillFrom(TypeEnumeration<?> te) {
        description = te.getDescription();
        for (Map.Entry<String, Number> entry : te.getValues().entrySet()) {
            final String name = entry.getKey();
            final Number value = entry.getValue();
            values.put(name, value);
        }
        return this;
    }

    @Override
    public void writeXml(String nameSpace, String name, Writer writer) throws IOException {
        writer.write("<EnumType Name=\"" + name + "\" UnderlyingType=\"Edm.Int32\">");
        for (Map.Entry<String, Number> entry : values.entrySet()) {
            String memberName = entry.getKey();
            Number memberValue = entry.getValue();
            writer.write("<Member Name=\"" + memberName + "\"   Value=\"" + memberValue + "\" />");
        }
        writer.write("</EnumType>");
    }

}
