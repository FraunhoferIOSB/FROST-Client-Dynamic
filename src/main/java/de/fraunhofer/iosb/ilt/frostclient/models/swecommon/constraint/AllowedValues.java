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
package de.fraunhofer.iosb.ilt.frostclient.models.swecommon.constraint;

import static de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper.isNullOrEmpty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * SWE Class AllowedValues constraint implementation.
 */
public class AllowedValues extends AbstractConstraint<AllowedValues> {

    /**
     * Values
     *
     * The values that the user can choose from.
     */
    private List<BigDecimal> value;

    /**
     * Intervals
     *
     * The intervals that the value must fall in.
     */
    private List<List<BigDecimal>> interval;

    /**
     * Significant Figures
     *
     * The number of significant figures.
     */
    private Integer significantFigures;

    public List<BigDecimal> getValue() {
        return value;
    }

    public List<List<BigDecimal>> getInterval() {
        return interval;
    }

    public Integer getSignificantFigures() {
        return significantFigures;
    }

    public boolean isValid(BigDecimal input) {
        if (isNullOrEmpty(value) && isNullOrEmpty(interval) && significantFigures == 0) {
            // This constraint is empty
            return true;
        }
        if (value != null) {
            for (BigDecimal item : value) {
                if (item.compareTo(input) == 0) {
                    return true;
                }
            }
        }
        if (interval != null) {
            for (List<BigDecimal> range : interval) {
                if (range.get(0).compareTo(input) < 0 && range.get(1).compareTo(input) > 0) {
                    return true;
                }
            }
        }
        // TODO: validate significantFigues
        return false;
    }

    public AllowedValues setValue(List<BigDecimal> value) {
        this.value = value;
        return this;
    }

    public AllowedValues setInterval(List<List<BigDecimal>> interval) {
        this.interval = interval;
        return this;
    }

    public AllowedValues setSignificantFigures(Integer significantFigures) {
        this.significantFigures = significantFigures;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.value);
        hash = 17 * hash + Objects.hashCode(this.interval);
        hash = 17 * hash + Objects.hashCode(this.significantFigures);
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
        final AllowedValues other = (AllowedValues) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.interval, other.interval)) {
            return false;
        }
        return Objects.equals(this.significantFigures, other.significantFigures);
    }

    @Override
    protected AllowedValues self() {
        return this;
    }

}
