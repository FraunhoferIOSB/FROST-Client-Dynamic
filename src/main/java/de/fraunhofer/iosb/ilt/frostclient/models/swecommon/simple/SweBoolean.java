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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Swe-Common Boolean.
 */
public class SweBoolean extends AbstractSimpleComponent<SweBoolean, Boolean> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SweBoolean.class.getName());

    public static final String SWE_NAME = "Boolean";

    /**
     * The value of this Boolean Component.
     */
    private Boolean value;

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public SweBoolean setValue(Boolean value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean valueIsValid() {
        return true;
    }

    @Override
    public boolean validate(Object input) {
        if (input == null) {
            return isOptional() || isSecret();
        }
        if (input instanceof JsonNode j) {
            return validate(j);
        }
        if (input instanceof Boolean b) {
            return validate(b);
        }
        LOGGER.debug("Non-boolean value {} for Count.", input);
        return false;
    }

    @Override
    public boolean validate(JsonNode input) {
        if (input == null) {
            return isOptional() || isSecret();
        }
        if (!input.isBoolean()) {
            LOGGER.debug("Non-boolean value {} for Count.", input);
            return false;
        }
        return true;
    }

    public boolean validate(Boolean value) {
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.value);
        hash = 71 * hash + super.hashCode();
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
        final SweBoolean other = (SweBoolean) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    protected SweBoolean self() {
        return this;
    }

    @Override
    public ObjectNode asJsonSchema() {
        ObjectNode schema = super.asJsonSchema()
                .put("type", "boolean");
        return schema;
    }

}
