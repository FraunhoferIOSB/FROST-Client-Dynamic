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
package de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author hylke
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface Annotation {

    public enum DocType {
        JSON("json"),
        XML("xml");

        private final String defaultExtension;

        private DocType(String defaultExtension) {
            this.defaultExtension = defaultExtension;
        }

        public String getDefaultExtension() {
            return defaultExtension;
        }

    }

    /**
     * The URL of the document that defines the annotation.
     *
     * @param docType the
     * @return The URL of the document that defines the annotation.
     */
    public String getSourceUrl(DocType docType);

    /**
     * The namespace of the annotation Term.
     *
     * @return The name space of the annotation Term.
     */
    public String getNameSpace();

    /**
     * The name of the annotation Term.
     *
     * @return The name of the annotation Term.
     */
    public String getName();

    /**
     * The value of the annotation.
     *
     * @return The value of the annotation.
     */
    public Object getValue();

}
