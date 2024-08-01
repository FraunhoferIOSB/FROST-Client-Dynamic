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
package de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation;

/**
 * Abstract base implementation for OData annotations.
 *
 * @param <T> The exact type of the annotation.
 */
public abstract class AbstractAnnotation<T extends AbstractAnnotation> implements Annotation {

    private String sourceUrlBase;
    private String nameSpace;
    private String name;

    protected AbstractAnnotation() {
        // Empty constructor
    }

    protected AbstractAnnotation(SourceNamespaceName snn) {
        this.sourceUrlBase = snn.getSourceUrlBase();
        this.nameSpace = snn.getNameSpace();
        this.name = snn.getName();
    }

    @Override
    public final String getSourceUrl(DocType docType) {
        return sourceUrlBase + "." + docType.getDefaultExtension();
    }

    public final T setSourceUrlBase(String sourceUrlBase) {
        this.sourceUrlBase = sourceUrlBase;
        return getThis();
    }

    @Override
    public final String getNameSpace() {
        return nameSpace;
    }

    public final T setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
        return getThis();
    }

    @Override
    public final String getName() {
        return name;
    }

    public final T setName(String name) {
        this.name = name;
        return getThis();
    }

    public abstract T getThis();
}
