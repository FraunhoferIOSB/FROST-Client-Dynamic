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
package de.fraunhofer.iosb.ilt.frostclient.model.ext;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.fraunhofer.iosb.ilt.frostclient.json.SimpleJsonMapper;
import de.fraunhofer.iosb.ilt.frostclient.model.ComplexValue;
import de.fraunhofer.iosb.ilt.frostclient.model.Property;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11;
import java.util.Objects;

/**
 * Model class for UnitOfMeasurement. This is a complex property in STA.
 */
public class UnitOfMeasurement implements ComplexValue<UnitOfMeasurement> {

    public static final EntityPropertyMain<String> EP_NAME = SensorThingsSensingV11.EP_NAME;
    public static final EntityPropertyMain<String> EP_DEFINITION = SensorThingsSensingV11.EP_DEFINITION;
    public static final EntityPropertyMain<String> EP_SYMBOL = SensorThingsSensingV11.EP_SYMBOL;

    private String name;
    private String symbol;
    private String definition;

    public UnitOfMeasurement() {
    }

    public UnitOfMeasurement(
            String name,
            String symbol,
            String definition) {
        this.name = name;
        this.symbol = symbol;
        this.definition = definition;
    }

    @Override
    public <P> P getProperty(Property<P> property) {
        if (property == EP_NAME) {
            return (P) getName();
        }
        if (property == EP_SYMBOL) {
            return (P) getSymbol();
        }
        if (property == EP_DEFINITION) {
            return (P) getDefinition();
        }
        throw new IllegalArgumentException("Unknown sub-property: " + property);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @return the definition
     */
    public String getDefinition() {
        return definition;
    }

    @Override
    public <P> UnitOfMeasurement setProperty(Property<P> property, P value) {
        if (property == EP_NAME) {
            return setName(Objects.toString(value));
        }
        if (property == EP_SYMBOL) {
            return setSymbol(Objects.toString(value));
        }
        if (property == EP_DEFINITION) {
            return setDefinition(Objects.toString(value));
        }
        throw new IllegalArgumentException("Unknown sub-property: " + property);
    }

    /**
     * @param name the name to set
     * @return this
     */
    public UnitOfMeasurement setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @param symbol the symbol to set
     * @return this
     */
    public UnitOfMeasurement setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    /**
     * @param definition the definition to set
     * @return this
     */
    public UnitOfMeasurement setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    @Override
    public String toString() {
        try {
            return SimpleJsonMapper.getSimpleObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            return super.toString();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, symbol, definition);
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
        final UnitOfMeasurement other = (UnitOfMeasurement) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.symbol, other.symbol)) {
            return false;
        }
        return Objects.equals(this.definition, other.definition);
    }
}
