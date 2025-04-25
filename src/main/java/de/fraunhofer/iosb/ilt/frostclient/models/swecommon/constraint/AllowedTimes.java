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

import static de.fraunhofer.iosb.ilt.frostclient.models.swecommon.util.JsonSchema.JSON_SCHEMA_KEY_ENUM;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.util.UnitOfMeasurement;
import java.util.List;
import java.util.Objects;

/**
 * SWE Class AllowedTimes constraint implementation.
 */
public class AllowedTimes extends AbstractConstraint<AllowedTimes> {

    /**
     * Value
     *
     * The values that the user can choose from.
     */
    private List<String> values;

    /**
     * Intervals
     *
     * The intervals that the values must fall in.
     */
    private List<List<String>> intervals;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.values);
        hash = 11 * hash + Objects.hashCode(this.intervals);
        hash = 11 * hash + Objects.hashCode(this.significantFigures);
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
        final AllowedTimes other = (AllowedTimes) obj;
        if (!Objects.equals(this.values, other.values)) {
            return false;
        }
        if (!Objects.equals(this.intervals, other.intervals)) {
            return false;
        }
        if (!Objects.equals(this.significantFigures, other.significantFigures)) {
            return false;
        }
        return true;
    }

    /**
     * Significant Figures
     *
     * The number of significant figures.
     */
    private Integer significantFigures;

    public List<String> getValues() {
        return values;
    }

    public AllowedTimes setValues(List<String> values) {
        this.values = values;
        return this;
    }

    public List<List<String>> getIntervals() {
        return intervals;
    }

    public AllowedTimes setIntervals(List<List<String>> intervals) {
        this.intervals = intervals;
        return this;
    }

    public Integer getSignificantFigures() {
        return significantFigures;
    }

    public AllowedTimes setSignificantFigures(Integer significantFigures) {
        this.significantFigures = significantFigures;
        return this;
    }

    public boolean isValid(String input, UnitOfMeasurement uom) {
        return true;
    }

    @Override
    protected AllowedTimes self() {
        return this;
    }

    @Override
    public void addToSchema(ObjectNode schema) {
        if (values != null) {
            final ArrayNode children = new ArrayNode(JsonNodeFactory.instance);
            values.stream().forEach(t -> children.add(t));
            schema.set(JSON_SCHEMA_KEY_ENUM, children);
        }
    }

}
