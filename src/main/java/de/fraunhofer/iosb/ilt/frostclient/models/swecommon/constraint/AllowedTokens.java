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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * SWE Class AllowedTokens constraint implementation.
 */
public class AllowedTokens extends AbstractConstraint<AllowedTokens> {

    /**
     * Value
     *
     * The values that the user can choose from.
     */
    private List<String> values;

    /**
     * Pattern
     *
     * The regex(?) pattern that the values must match.
     */
    private String pattern;

    public AllowedTokens() {
    }

    public AllowedTokens(String... tokens) {
        this.values = Arrays.asList(tokens);
    }

    public AllowedTokens(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.values);
        hash = 37 * hash + Objects.hashCode(this.pattern);
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
        final AllowedTokens other = (AllowedTokens) obj;
        if (!Objects.equals(this.pattern, other.pattern)) {
            return false;
        }
        if (!Objects.equals(this.values, other.values)) {
            return false;
        }
        return true;
    }

    public String getPattern() {
        return pattern;
    }

    public AllowedTokens setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public List<String> getValues() {
        return values;
    }

    public AllowedTokens setValues(List<String> values) {
        this.values = values;
        return this;
    }

    public boolean isValid(String input) {
        if (values != null) {
            for (String item : values) {
                if (item.equals(input)) {
                    return true;
                }
            }
        }
        if (pattern != null && !pattern.isEmpty()) {
            Pattern compiled = Pattern.compile(pattern);
            if (compiled.matcher(input).matches()) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected AllowedTokens self() {
        return this;
    }

    @Override
    public void addToSchema(ObjectNode schema) {
        if (pattern != null) {
            schema.put("pattern", pattern);
        } else if (values != null) {
            final ArrayNode children = new ArrayNode(JsonNodeFactory.instance);
            values.stream().forEach(t -> children.add(t));
            schema.set("enum", children);
        }
    }

}
