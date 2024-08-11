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
package de.fraunhofer.iosb.ilt.frostclient.model;

import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A wrapper for an Object array holding primary key values. This class
 * implements equals, which an array does not.
 */
public class PkValue implements Iterable<Object> {

    private final Object[] values;

    public PkValue(int size) {
        this.values = new Object[size];
    }

    public PkValue(Object[] value) {
        this.values = value;
    }

    public Object get(int idx) {
        return values[idx];
    }

    public PkValue set(int idx, Object value) {
        this.values[idx] = value;
        return this;
    }

    public String getUrl(PrimaryKey pk) {
        return StringHelper.formatKeyValuesForUrl(pk, this);
    }

    public boolean isFullySet() {
        for (var value : values) {
            if (value == null) {
                return false;
            }
        }
        return true;
    }

    public boolean isFullyUnSet() {
        for (var value : values) {
            if (value != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PkValue other = (PkValue) obj;
        return Arrays.equals(this.values, other.values);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return 41 * hash + Arrays.hashCode(values);
    }

    public int size() {
        return values.length;
    }

    @Override
    public Iterator<Object> iterator() {
        return Arrays.stream(values).iterator();
    }

    public static PkValue of(Object... value) {
        for (var item : value) {
            if (item instanceof PkValue) {
                throw new IllegalArgumentException("Wrapping a PkValue in a PkValue!");
            }
        }
        return new PkValue(value);
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }

}
