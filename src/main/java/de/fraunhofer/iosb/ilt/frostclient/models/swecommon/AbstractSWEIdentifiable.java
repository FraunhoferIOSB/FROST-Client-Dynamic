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
package de.fraunhofer.iosb.ilt.frostclient.models.swecommon;

import java.util.Objects;

/**
 * SWE abstract Class AbstractSWEIdentifiable.
 *
 * @param <T> The type of the extending class.
 */
public abstract class AbstractSWEIdentifiable<T extends AbstractSWEIdentifiable<T>> extends AbstractSWE {

    /**
     * Identifier
     *
     * A unique identifier.
     */
    private String identifier;

    /**
     * Label
     *
     * A short descriptive name.
     */
    private String label;

    /**
     * Description
     *
     * A longer description.
     */
    private String description;

    /**
     * The type of the component.
     */
    private String type;

    public String getDescription() {
        return description;
    }

    public T setDescription(String description) {
        this.description = description;
        return self();
    }

    public String getIdentifier() {
        return identifier;
    }

    public T setIdentifier(String identifier) {
        this.identifier = identifier;
        return self();
    }

    public String getLabel() {
        return label;
    }

    public T setLabel(String label) {
        this.label = label;
        return self();
    }

    public String getType() {
        return type;
    }

    public T setType(String type) {
        this.type = type;
        return self();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.identifier);
        hash = 67 * hash + Objects.hashCode(this.label);
        hash = 67 * hash + Objects.hashCode(this.description);
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
        final AbstractSWEIdentifiable other = (AbstractSWEIdentifiable) obj;
        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        return Objects.equals(this.description, other.description);
    }

    protected abstract T self();

}
