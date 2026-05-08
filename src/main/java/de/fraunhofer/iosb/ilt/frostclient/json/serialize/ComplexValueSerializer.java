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
package de.fraunhofer.iosb.ilt.frostclient.json.serialize;

import de.fraunhofer.iosb.ilt.frostclient.model.ComplexValue;
import de.fraunhofer.iosb.ilt.frostclient.model.ContainerType;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import java.util.Map;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class ComplexValueSerializer extends ValueSerializer<ComplexValue> {

    @Override
    public void serialize(ComplexValue cv, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeStartObject();
        ContainerType<?> type = cv.getType();
        for (EntityPropertyMain p : type.getEntityProperties()) {
            Object pVal = cv.getProperty(p);
            if (pVal == null) {
                if (p.serialiseNull) {
                    gen.writeNullProperty(p.getJsonName());
                }
            } else {
                gen.writePOJOProperty(p.getJsonName(), pVal);
            }
        }
        if (type.isOpenType()) {
            Map<String, Object> content = cv.getContent();
            for (Map.Entry<String, Object> entry : content.entrySet()) {
                gen.writePOJOProperty(entry.getKey(), entry.getValue());
            }
        }
        gen.writeEndObject();

    }

}
