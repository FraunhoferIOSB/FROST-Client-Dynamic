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
package de.fraunhofer.iosb.ilt.frostclient.models.swecommon.simple;

import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.constraint.AllowedTokens;
import java.util.Objects;

/**
 * SWE Text class.
 */
public class Text extends AbstractSimpleComponent<Text, String> {

    /**
     * Value
     *
     * The value of this field.
     */
    private String value;

    /**
     * Constraint
     *
     * The constraints put on the value of this component.
     */
    private AllowedTokens constraint;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.value);
        hash = 59 * hash + Objects.hashCode(this.constraint);
        hash = 59 * hash + super.hashCode();
        return hash;
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
        final Text other = (Text) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.constraint, other.constraint)) {
            return false;
        }
        return super.equals(obj);
    }

    public AllowedTokens getConstraint() {
        return constraint;
    }

    public Text setConstraint(AllowedTokens constraint) {
        this.constraint = constraint;
        return this;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Text setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean valueIsValid() {
        if (value == null) {
            return false;
        }
        if (constraint == null) {
            return true;
        }
        return constraint.isValid(value);
    }

    @Override
    protected Text self() {
        return this;
    }

}
