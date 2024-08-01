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
package de.fraunhofer.iosb.ilt.frostclient.model.property;

import de.fraunhofer.iosb.ilt.frostclient.model.PropertyType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * @param <P> The type of the value of the property.
 */
public class EntityPropertyMain<P> extends PropertyAbstract<P> implements EntityProperty<P> {

    /**
     * Flag indicating a null value should not be ignored, but serialised as
     * Json NULL.
     */
    public final boolean serialiseNull;

    private final Collection<String> aliases;

    public EntityPropertyMain(String name, PropertyType type) {
        this(name, type, false);
    }

    public EntityPropertyMain(String name, PropertyType type, boolean serialiseNull) {
        super(name, type, false);

        this.aliases = new ArrayList<>();
        this.aliases.add(name);
        this.serialiseNull = serialiseNull;
    }

    public Collection<String> getAliases() {
        return aliases;
    }

    public EntityPropertyMain<P> setAliases(String... aliases) {
        if (this.aliases.size() != 1) {
            throw new IllegalStateException("Aliases already set for " + getName());
        }
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    public EntityPropertyMain<P> setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
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
        final EntityPropertyMain<?> other = (EntityPropertyMain<?>) obj;
        return Objects.equals(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

}
