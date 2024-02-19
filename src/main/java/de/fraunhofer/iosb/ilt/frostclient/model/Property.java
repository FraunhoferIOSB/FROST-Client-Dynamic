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
package de.fraunhofer.iosb.ilt.frostclient.model;

import de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation.Annotatable;

/**
 * @param <P> The type of the value of the property.
 */
public interface Property<P> extends Comparable<Property<?>>, Annotatable {

    /**
     * The name of this property as used in URLs.
     *
     * @return The name of this property as used in URLs.
     */
    public String getName();

    /**
     * The name of this property as used in JSON.
     *
     * @return The name of this property as used in JSON.
     */
    public String getJsonName();

    /**
     * The class of the type of the value of this property.
     *
     * @return The class of the type of the value of this property.
     */
    public PropertyType getType();

    /**
     * Flag indicating the property is system generated and can not be edited by
     * the user.
     *
     * @return the readOnly flag.
     */
    public boolean isReadOnly();

    /**
     * Flag indicating the property can be left out, or explicitly be set to
     * null.
     *
     * @return The nullable flag.
     */
    public boolean isNullable();

    /**
     * Flag indicating if this Property is part of the primary key of the
     * Entity.
     *
     * @return true if this Property is part of the primary key of the Entity.
     */
    public boolean isKeyPart();

    @Override
    public default int compareTo(Property o) {
        return getName().compareTo(o.getName());
    }

}
