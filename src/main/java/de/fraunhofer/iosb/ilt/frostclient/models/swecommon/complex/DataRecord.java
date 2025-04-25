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

import static de.fraunhofer.iosb.ilt.frostclient.models.swecommon.util.JsonSchema.JSON_SCHEMA_KEY_PROPERTIES;
import static de.fraunhofer.iosb.ilt.frostclient.models.swecommon.util.JsonSchema.JSON_SCHEMA_KEY_REQUIRED;
import static de.fraunhofer.iosb.ilt.frostclient.models.swecommon.util.JsonSchema.JSON_SCHEMA_KEY_TYPE;
import static de.fraunhofer.iosb.ilt.frostclient.models.swecommon.util.JsonSchema.JSON_SCHEMA_TYPE_OBJECT;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.AbstractDataComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SWE Class DataRecord.
 */
public class DataRecord extends AbstractDataComponent<DataRecord, Map<String, Object>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecord.class.getName());

    private List<AbstractDataComponent> fields;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.fields);
        hash = 29 * hash + super.hashCode();
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
        final DataRecord other = (DataRecord) obj;
        if (!Objects.equals(this.fields, other.fields)) {
            return false;
        }
        return super.equals(obj);
    }

    public List<AbstractDataComponent> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    /**
     * Alias for setFields, since some JSON has the fields in the field field.
     *
     * @param fields The fields to set on the DataRecord.
     * @return this.
     */
    public DataRecord setField(List<AbstractDataComponent> fields) {
        return setFields(fields);
    }

    public DataRecord setFields(List<AbstractDataComponent> fields) {
        this.fields = fields;
        return this;
    }

    public java.util.Optional<AbstractDataComponent> getFieldByName(String name) {
        return getFields().stream().filter(f -> f.getName().equals(name)).findFirst();
    }

    public DataRecord addDataComponent(String name, AbstractDataComponent field) {
        if (!name.equals(field.getName())) {
            field.setName(name);
        }
        return addDataComponent(field);
    }

    public DataRecord addDataComponent(AbstractDataComponent field) {
        String name = field.getName();
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Field must have a non-empty name");
        }
        if (getFieldByName(name).isPresent()) {
            throw new IllegalArgumentException("Field with name " + name + " is already present");
        }
        getFields().add(field);
        return this;
    }

    @Override
    public Map<String, Object> getValue() {
        Map<String, Object> value = new LinkedHashMap<>();
        for (AbstractDataComponent f : fields) {
            value.put(f.getName(), f.getValue());
        }
        return value;
    }

    @Override
    public DataRecord setValue(Map<String, Object> value) {
        if (fields == null) {
            return this;
        }
        for (AbstractDataComponent f : fields) {
            Object fieldValue = value.get(f.getName());
            if (fieldValue != null) {
                f.setValue(fieldValue);
            }
        }
        return this;
    }

    @Override
    public boolean valueIsValid() {
        if (fields == null) {
            return true;
        }
        for (AbstractDataComponent f : fields) {
            if (!f.valueIsValid()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean validate(Object input) {
        if (input instanceof Map mapValue) {
            return validate(mapValue);
        } else if (input instanceof List list) {
            return validate(list);
        } else if (input instanceof JsonNode jsonValue) {
            return validate(jsonValue);
        } else {
            LOGGER.debug("Value is not a Map or JsonNode: {}", input);
            return false;
        }
    }

    @Override
    public boolean validate(JsonNode input) {
        if (!input.isObject() && !input.isArray()) {
            LOGGER.debug("Value is not an Array or Object: {}", input);
            return false;
        }
        if (fields == null) {
            return true;
        }
        if (input.isObject()) {
            return validateObject(input);
        }
        return validateArray(input);
    }

    public boolean validateObject(JsonNode input) {
        for (AbstractDataComponent field : fields) {
            final String fieldName = field.getName();
            final JsonNode fieldValue = input.get(fieldName);
            if (fieldValue == null) {
                if (field.isOptional() || field.isSecret()) {
                    continue;
                } else {
                    LOGGER.debug("No value for non-optional field {}", fieldName);
                    return false;
                }
            }
            if (!field.validate(fieldValue)) {
                return false;
            }
        }
        return true;
    }

    public boolean validate(Object... input) {
        return validate(Arrays.asList(input));
    }

    public boolean validate(List<Object> input) {
        if (input.size() != fields.size()) {
            LOGGER.debug("Length of value list {} differs from fields array {}", input.size(), fields.size());
            return false;
        }
        for (int idx = 0; idx < fields.size(); idx++) {
            AbstractDataComponent field = fields.get(idx);
            Object fieldValue = input.get(idx);
            if (!field.validate(fieldValue)) {
                return false;
            }
        }
        return true;
    }

    public boolean validateArray(JsonNode input) {
        if (input.size() != fields.size()) {
            LOGGER.debug("Length of value array {} differs from fields array {}", input.size(), fields.size());
            return false;
        }
        for (int idx = 0; idx < fields.size(); idx++) {
            AbstractDataComponent field = fields.get(idx);
            JsonNode fieldValue = input.get(idx);
            if (!field.validate(fieldValue)) {
                return false;
            }
        }
        return true;
    }

    public boolean validate(Map<String, Object> input) {
        if (fields == null) {
            return true;
        }
        for (AbstractDataComponent field : fields) {
            final String fieldName = field.getName();
            final Object fieldValue = input.get(fieldName);
            if (fieldValue == null) {
                if (field.isOptional() || field.isSecret()) {
                    continue;
                } else {
                    LOGGER.debug("No value for non-optional field {}", fieldName);
                    return false;
                }
            }
            if (!field.validate(fieldValue)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected DataRecord self() {
        return this;
    }

    @Override
    public ObjectNode asJsonSchema() {
        ObjectNode properties = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode required = new ArrayNode(JsonNodeFactory.instance);
        for (var field : fields) {
            properties.set(field.getName(), field.asJsonSchema());
            if (!field.isOptional()) {
                required.add(field.getName());
            }
        }
        ObjectNode schema = super.asJsonSchema()
                .put(JSON_SCHEMA_KEY_TYPE, JSON_SCHEMA_TYPE_OBJECT)
                .set(JSON_SCHEMA_KEY_PROPERTIES, properties);
        if (!required.isEmpty()) {
            schema.set(JSON_SCHEMA_KEY_REQUIRED, required);
        }
        return schema;
    }

}
