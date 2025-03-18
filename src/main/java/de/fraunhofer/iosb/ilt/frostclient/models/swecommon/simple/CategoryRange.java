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
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.constraint.AllowedTokens;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SWE Class CategoryRange.
 */
public class CategoryRange extends AbstractRange<CategoryRange, String> {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryRange.class);

    /**
     * Value
     *
     * The starting end ending values of this CategoryRange.
     */
    private List<String> value = new ArrayList<>();

    /**
     * Allowed Tokens
     *
     * A limited list of possible values.
     */
    private AllowedTokens constraint;

    //TODO
    private Map<String, String> codeSpace;

    public AllowedTokens getConstraint() {
        return constraint;
    }

    public CategoryRange setConstraint(AllowedTokens constraint) {
        this.constraint = constraint;
        return this;
    }

    @Override
    public List<String> getValue() {
        return value;
    }

    @Override
    public CategoryRange setValue(List<String> value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean valueIsValid() {
        return validate(value);
    }

    @Override
    protected boolean validateArray(JsonNode input) {
        if (constraint == null) {
            return true;
        }
        for (JsonNode item : input) {
            if (!item.isTextual()) {
                LOGGER.debug("Non-text item {} in array", item);
                return false;
            }
            if (!constraint.isValid(item.asText())) {
                LOGGER.error("Item '{}' does not fit the constraint", item);
                return false;
            }
        }
        return true;

    }

    @Override
    public boolean validate(List<String> input) {
        if (input == null) {
            return isOptional() || isSecret();
        }
        final int size = input.size();
        if (size != 2) {
            LOGGER.debug("Range must have 2 items, found: {}", size);
            return false;
        }
        if (constraint == null) {
            return true;
        }
        for (String item : input) {
            if (!constraint.isValid(item)) {
                LOGGER.error("Item '{}' does not fit the constraint", item);
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.value);
        hash = 59 * hash + Objects.hashCode(this.constraint);
        hash = 59 * hash + Objects.hashCode(this.codeSpace);
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
        final CategoryRange other = (CategoryRange) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.constraint, other.constraint)) {
            return false;
        }
        if (!Objects.equals(this.codeSpace, other.codeSpace)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    protected CategoryRange self() {
        return this;
    }

}
