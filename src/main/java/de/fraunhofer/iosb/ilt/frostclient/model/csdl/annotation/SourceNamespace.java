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

/**
 * A holder for the combination of an Annotation Source and Annotation
 * Namespace.
 *
 * @author hylke
 */
public class SourceNamespace {

    /**
     * The base URL defining an annotation, without file-extension.
     */
    private final String sourceUrlBase;

    /**
     * The Namespace of an annotation.
     */
    private final String nameSpace;

    public SourceNamespace(String sourceUrlBase, String nameSpace) {
        this.sourceUrlBase = sourceUrlBase;
        this.nameSpace = nameSpace;
    }

    /**
     * The base URL defining an annotation, without file-extension.
     *
     * @return the base URL defining an annotation, without file-extension.
     */
    public String getSourceUrlBase() {
        return sourceUrlBase;
    }

    /**
     * The Namespace of an annotation.
     *
     * @return the Namespace of an annotation.
     */
    public String getNameSpace() {
        return nameSpace;
    }

}
