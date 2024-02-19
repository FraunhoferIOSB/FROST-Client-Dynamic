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

import static de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation.Annotation.DocType.JSON;

import de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation.Annotation;
import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hylke
 */
public class CsdlAnnotation {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsdlAnnotation.class.getName());

    private String name;
    private String namespace;
    private Object value;

    public CsdlAnnotation fillFrom(CsdlDocument doc, Annotation annotation) {
        this.name = annotation.getName();
        this.namespace = annotation.getNameSpace();
        this.value = annotation.getValue();
        doc.registerAnnotation(annotation.getSourceUrl(JSON), this);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getQualifiedName() {
        return namespace + "." + name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void writeXml(Writer writer) throws IOException {
        if (value instanceof Boolean boolValue) {
            if (boolValue) {
                writer.write("<Annotation Term=\"" + getQualifiedName() + "\" />");
            }
            return;
        }
        writer.write("<Annotation Term=\"" + getQualifiedName() + "\"");
        if (value instanceof String string) {
            writer.write(" String=\"" + StringEscapeUtils.escapeXml11(string) + "\" />");
            return;
        }
        LOGGER.error("Unknown annotation value type: {}", value.getClass().getName());
    }

    public static CsdlAnnotation of(CsdlDocument doc, Annotation annotation) {
        return new CsdlAnnotation().fillFrom(doc, annotation);
    }
}
