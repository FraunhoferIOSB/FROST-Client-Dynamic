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
package de.fraunhofer.iosb.ilt.frostclient.model.property;

import de.fraunhofer.iosb.ilt.frostclient.model.Property;
import de.fraunhofer.iosb.ilt.frostclient.model.PropertyType;
import de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public abstract class PropertyAbstract<P> implements Property<P> {

    private String name;
    private PropertyType type;

    /**
     * Flag indicating the property is system generated and can not be edited by
     * the user.
     */
    protected boolean readOnly;
    protected boolean keyPart;
    protected boolean nullable;

    protected List<Annotation> annotations = new ArrayList<>();

    public PropertyAbstract(String name, PropertyType type, boolean readOnly) {
        this(name, type, readOnly, true);
    }

    public PropertyAbstract(String name, PropertyType type, boolean readOnly, boolean nullable) {
        if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }
        this.name = name;
        this.type = type;
        this.readOnly = readOnly;
        this.nullable = nullable;
    }

    @Override
    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    @Override
    public String getJsonName() {
        return getName();
    }

    @Override
    public PropertyType getType() {
        return type;
    }

    protected void setType(PropertyType type) {
        this.type = type;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public boolean isKeyPart() {
        return keyPart;
    }

    public void setKeyPart(boolean keyPart) {
        this.keyPart = keyPart;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public PropertyAbstract<P> setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
        return this;
    }

    public PropertyAbstract<P> addAnnotation(Annotation annotation) {
        annotations.add(annotation);
        return this;
    }

}
