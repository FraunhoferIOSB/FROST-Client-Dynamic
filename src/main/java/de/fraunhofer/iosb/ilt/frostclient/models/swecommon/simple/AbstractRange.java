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
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for ranges.
 *
 * @param <T> The type of the extending class.
 * @param <V> The type of the Value field.
 */
public abstract class AbstractRange<T extends AbstractSimpleComponent<T, List<V>>, V> extends AbstractSimpleComponent<T, List<V>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRange.class.getName());

    @Override
    public final boolean validate(Object input) {
        if (input == null) {
            return isOptional() || isSecret();
        }
        if (input instanceof JsonNode j) {
            return validate(j);
        }
        if (input instanceof List list) {
            return validate(list);
        }
        if (input instanceof Object[] arr) {
            return validate(Arrays.asList(arr));
        }
        LOGGER.debug("Input not a list/array: {}", input);
        return false;
    }

    @Override
    public final boolean validate(JsonNode input) {
        if (input == null) {
            return isOptional() || isSecret();
        }
        if (input.isArray()) {
            return validateArray(input);
        }
        LOGGER.debug("Input not a list/array: {}", input);
        return false;
    }

    protected abstract boolean validateArray(JsonNode input);

    public abstract boolean validate(List<V> input);
}
