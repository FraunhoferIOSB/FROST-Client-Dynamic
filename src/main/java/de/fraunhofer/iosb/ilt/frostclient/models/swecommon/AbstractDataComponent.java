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
package de.fraunhofer.iosb.ilt.frostclient.models.swecommon;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;

/**
 * Swe-Common AbstractDataComponent.
 *
 * @param <T> The type of the extending class.
 * @param <V> The type of the Value field.
 */
public abstract class AbstractDataComponent<T extends AbstractDataComponent<T, V>, V> extends AbstractSWEIdentifiable<T> {

    /**
     * The name of the component when used as a field in a DataRecord.
     */
    private String name;

    /**
     * A scoped name that maps to a controlled term defined in a (web
     * accessible) dictionary, registry or ontology.
     */
    private String definition;

    /**
     * A flag indicating if the component value can be omitted.
     */
    private boolean optional;

    /**
     * A flag indicating if the component value is fixed or can be updated.
     */
    private boolean updatable;

    public String getDefinition() {
        return definition;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.definition);
        hash = 29 * hash + (this.optional ? 1 : 0);
        hash = 29 * hash + (this.updatable ? 1 : 0);
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
        final AbstractDataComponent other = (AbstractDataComponent) obj;
        if (this.optional != other.optional) {
            return false;
        }
        if (this.updatable != other.updatable) {
            return false;
        }
        if (!Objects.equals(this.definition, other.definition)) {
            return false;
        }
        return super.equals(obj);
    }

    public String getName() {
        return name;
    }

    public T setName(String name) {
        this.name = name;
        return self();
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Boolean isOptional() {
        return optional;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Boolean isUpdatable() {
        return updatable;
    }

    /**
     * Checks if any set values are valid for any set constraints.
     *
     * @return true if the values are valid.
     */
    public abstract boolean valueIsValid();

    /**
     * Get the value of this DataComponent.
     *
     * @return The value of this DataComponent.
     */
    public abstract V getValue();

    public abstract T setValue(V value);

    /**
     * Validate the given value against this component.
     *
     * @param input the value to validate.
     * @return true if the value has the correct class and value.
     */
    public abstract boolean validate(Object input);

    /**
     * Validate the given value against this component.
     *
     * @param input the value to validate.
     * @return true if the value has the correct type and value.
     */
    public abstract boolean validate(JsonNode input);

    public T setDefinition(String definition) {
        this.definition = definition;
        return self();
    }

    public T setOptional(boolean optional) {
        this.optional = optional;
        return self();
    }

    public T setUpdatable(boolean updatable) {
        this.updatable = updatable;
        return self();
    }

}
