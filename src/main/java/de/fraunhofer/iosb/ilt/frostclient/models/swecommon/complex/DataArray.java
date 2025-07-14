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
package de.fraunhofer.iosb.ilt.frostclient.models.swecommon.complex;

import com.fasterxml.jackson.databind.JsonNode;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.AbstractDataComponent;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SWE Class DataArray.
 */
public class DataArray extends AbstractDataComponent<DataArray, List<Object>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataArray.class.getName());

    private AbstractDataComponent elementType;
    private List<Object> values;

    public AbstractDataComponent getElementType() {
        return elementType;
    }

    public DataArray setElementType(AbstractDataComponent elementType) {
        this.elementType = elementType;
        return self();
    }

    @Override
    public List<Object> getValue() {
        return values;
    }

    @Override
    protected DataArray self() {
        return this;
    }

    @Override
    public DataArray setValue(List<Object> value) {
        this.values = value;
        return self();
    }

    @Override
    public boolean validate(Object input) {
        if (input instanceof List list) {
            return validate(list);
        } else if (input instanceof JsonNode jn) {
            return validate(jn);
        } else {
            LOGGER.error("Input is not a List or JsonArray");
            return false;
        }
    }

    public boolean validate(List input) {
        if (elementType == null) {
            LOGGER.error("ElementType is not set.");
            return false;
        }
        for (var item : input) {
            if (!elementType.validate(item)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean validate(JsonNode input) {
        if (input.isArray()) {
            for (var it = input.values(); it.hasNext();) {
                JsonNode item = it.next();
                if (!elementType.validate(item)) {
                    return false;
                }
            }
            return true;
        } else {
            LOGGER.error("Input is not a JsonArray");
            return false;
        }
    }

    @Override
    public boolean valueIsValid() {
        return validate(values);
    }

}
