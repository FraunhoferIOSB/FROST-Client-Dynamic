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

import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.constraint.AllowedValues;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.util.UnitOfMeasurement;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * SWE Quantity class.
 */
public class Quantity extends AbstractSimpleComponent<Quantity, BigDecimal> {

    /**
     * Value
     *
     * A real value that is within one of the constraint intervals or exactly
     * one of the enumerated values, and most importantly is expressed in the
     * unit specified.
     */
    private BigDecimal value;

    /**
     * Constraint
     *
     * A limited list of possible values.
     */
    private AllowedValues constraint;

    /**
     * UoM
     *
     * The units of the value of this Quantity.
     */
    private UnitOfMeasurement uom;

    public Quantity setConstraint(AllowedValues constraint) {
        this.constraint = constraint;
        return this;
    }

    public Quantity setUom(UnitOfMeasurement uom) {
        this.uom = uom;
        return this;
    }

    @Override
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public Quantity setValue(BigDecimal value) {
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

    public UnitOfMeasurement getUom() {
        return uom;
    }

    public AllowedValues getConstraint() {
        return constraint;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.value);
        hash = 53 * hash + Objects.hashCode(this.constraint);
        hash = 53 * hash + Objects.hashCode(this.uom);
        hash = 53 * hash + super.hashCode();
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
        final Quantity other = (Quantity) obj;
        if (!Objects.equals(this.uom, other.uom)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.constraint, other.constraint)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    protected Quantity self() {
        return this;
    }

}
