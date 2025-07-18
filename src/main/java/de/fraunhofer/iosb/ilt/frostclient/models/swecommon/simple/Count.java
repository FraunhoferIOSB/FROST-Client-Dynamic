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

import static de.fraunhofer.iosb.ilt.frostclient.models.swecommon.util.JsonSchema.JSON_SCHEMA_KEY_TYPE;
import static de.fraunhofer.iosb.ilt.frostclient.models.swecommon.util.JsonSchema.JSON_SCHEMA_TYPE_INTEGER;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.constraint.AllowedValues;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SWE Count class.
 */
public class Count extends AbstractSimpleComponent<Count, Number> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Count.class.getName());

    /**
     * Value
     *
     * an integer that must be within one of the constraint intervals or exactly
     * one of the enumerated values.
     */
    private Number value;

    /**
     * Constraint
     *
     * A list of inclusive intervals and/or single values.
     */
    private AllowedValues constraint;

    public AllowedValues getConstraint() {
        return constraint;
    }

    public void setConstraint(AllowedValues constraint) {
        this.constraint = constraint;
    }

    @Override
    public Number getValue() {
        return value;
    }

    @Override
    public Count setValue(Number value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean valueIsValid() {
        return validate(value);
    }

    @Override
    public boolean validate(Object input) {
        if (input == null) {
            return isOptional() || isSecret();
        }
        if (input instanceof JsonNode j) {
            return validate(j);
        }
        if (input instanceof Number n) {
            return validate(n);
        }
        LOGGER.debug("Non-integral value {} for Count.", input);
        return false;
    }

    @Override
    public boolean validate(JsonNode input) {
        if (input == null) {
            return isOptional() || isSecret();
        }
        if (!input.isIntegralNumber()) {
            LOGGER.debug("Non-integral value {} for Count.", input);
            return false;
        }
        return validate(input.bigIntegerValue());

    }

    public boolean validate(Number input) {
        if (input == null) {
            return isOptional() || isSecret();
        }
        if (input instanceof Double || input instanceof Float || input instanceof BigDecimal) {
            if (input.doubleValue() != input.longValue()) {
                LOGGER.debug("Non-integer value {} for Count!", input);
                return false;
            }
        }
        if (constraint == null) {
            return true;
        }
        if (input instanceof BigInteger bi) {
            return constraint.isValid(new BigDecimal(bi));
        } else if (input instanceof BigDecimal bd) {
            return constraint.isValid(bd);
        } else {
            return constraint.isValid(new BigDecimal(input.longValue()));
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.value);
        hash = 79 * hash + Objects.hashCode(this.constraint);
        hash = 79 * hash + super.hashCode();
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
        final Count other = (Count) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.constraint, other.constraint)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    protected Count self() {
        return this;
    }

    @Override
    public ObjectNode asJsonSchema() {
        ObjectNode schema = super.asJsonSchema()
                .put(JSON_SCHEMA_KEY_TYPE, JSON_SCHEMA_TYPE_INTEGER);
        if (constraint != null) {
            constraint.addToSchema(schema);
        }
        return schema;
    }

}
