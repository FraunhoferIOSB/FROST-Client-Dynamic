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
package de.fraunhofer.iosb.ilt.frostclient.models.swecommon.constraint;

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
    private List<String> value;

    /**
     * Pattern
     *
     * The regex(?) pattern that the value must match.
     */
    private String pattern;

    public AllowedTokens() {
    }

    public AllowedTokens(String... tokens) {
        this.value = Arrays.asList(tokens);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.value);
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
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    public AllowedTokens(String pattern) {
        this.pattern = pattern;
    }

    public List<String> getValue() {
        return value;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean isValid(String input) {
        if (value != null) {
            for (String item : value) {
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

    public AllowedTokens setValue(List<String> value) {
        this.value = value;
        return this;
    }

    public AllowedTokens setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    @Override
    protected AllowedTokens self() {
        return this;
    }

}
