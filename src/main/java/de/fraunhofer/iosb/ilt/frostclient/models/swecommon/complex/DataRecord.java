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
package de.fraunhofer.iosb.ilt.frostclient.models.swecommon.complex;

import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.AbstractDataComponent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * SWE Class DataRecord.
 */
public class DataRecord extends AbstractDataComponent<DataRecord, Map<String, Object>> {

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
    protected DataRecord self() {
        return this;
    }

}
