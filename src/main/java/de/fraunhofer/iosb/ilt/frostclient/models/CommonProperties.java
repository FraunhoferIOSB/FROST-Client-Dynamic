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
package de.fraunhofer.iosb.ilt.frostclient.models;

import static de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive.EDM_STRING;
import static de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive.EDM_UNTYPED;
import static de.fraunhofer.iosb.ilt.frostclient.utils.SpecialNames.AT_IOT_ID;

import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.PkValue;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;

/**
 * Common properties and names to make switching between data models easier.
 */
public class CommonProperties {

    // Common-Common
    public static final EntityPropertyMain<Object> EP_ID = new EntityPropertyMain<>("id", EDM_UNTYPED).setAliases(AT_IOT_ID);

    // STA-Common
    public static final String NAME_DATASTREAM = "Datastream";
    public static final String NAME_DATASTREAMS = "Datastreams";
    public static final String NAME_DATASTREAMS_PROXIMATE = "DatastreamsProximate";
    public static final String NAME_DATASTREAMS_ULTIMATE = "DatastreamsUltimate";
    public static final String NAME_FEATURE = "Feature";
    public static final String NAME_FEATURES = "Features";
    public static final String NAME_FEATURE_TYPE = "FeatureType";
    public static final String NAME_FEATURE_TYPES = "FeatureTypes";
    public static final String NAME_FEATUREOFINTEREST = "FeatureOfInterest";
    public static final String NAME_FEATURESOFINTEREST = "FeaturesOfInterest";
    public static final String NAME_HISTORICALLOCATION = "HistoricalLocation";
    public static final String NAME_HISTORICALLOCATIONS = "HistoricalLocations";
    public static final String NAME_LOCATION = "Location";
    public static final String NAME_LOCATIONS = "Locations";
    public static final String NAME_OBSERVATION = "Observation";
    public static final String NAME_OBSERVATIONS = "Observations";
    public static final String NAME_OBSERVEDPROPERTY = "ObservedProperty";
    public static final String NAME_OBSERVEDPROPERTIES = "ObservedProperties";
    public static final String NAME_SENSOR = "Sensor";
    public static final String NAME_SENSORS = "Sensors";
    public static final String NAME_THING = "Thing";
    public static final String NAME_THINGS = "Things";

    public static final String NAME_EP_DESCRIPTION = "description";
    public static final String NAME_EP_DEFINITION = "definition";
    public static final String NAME_EP_ENCODINGTYPE = "encodingType";
    public static final String NAME_EP_NAME = "name";
    public static final String NAME_EP_PROPERTIES = "properties";

    public static final EntityPropertyMain<String> EP_DESCRIPTION = new EntityPropertyMain<>(NAME_EP_DESCRIPTION, EDM_STRING);
    public static final EntityPropertyMain<String> EP_DEFINITION = new EntityPropertyMain<>(NAME_EP_DEFINITION, EDM_STRING);
    public static final EntityPropertyMain<String> EP_NAME = new EntityPropertyMain<>(NAME_EP_NAME, EDM_STRING);
    public static final EntityPropertyMain<MapValue> EP_PROPERTIES = new EntityPropertyMain<>(NAME_EP_PROPERTIES, TypeComplex.STA_MAP);
    public static final EntityPropertyMain<String> EP_ENCODINGTYPE = new EntityPropertyMain<>(NAME_EP_ENCODINGTYPE, EDM_STRING);

    public static class Builder<T extends Builder<T>> {

        protected Entity entity;

        public Builder() {
        }

        public Builder(Entity entity) {
            this.entity = entity;
        }

        protected void setEntity(Entity entity) {
            this.entity = entity;
        }

        public Entity build() {
            return entity;
        }

        public T getThis() {
            return (T) this;
        }

        public <U extends BuilderId<U>> U extend(U extension) {
            extension.setEntity(entity);
            return extension;
        }
    }

    public static class BuilderId<T extends BuilderId<T>> extends Builder<T> {

        public BuilderId(Entity entity) {
            super(entity);
        }

        public PkValue getPrimaryKeyValues() {
            return entity.getPrimaryKeyValues();
        }

        public T setId(Object... value) {
            entity.setPrimaryKeyValues(PkValue.of(value));
            return getThis();
        }

        public T setId(PkValue value) {
            entity.setPrimaryKeyValues(value);
            return getThis();
        }

    }

    public static class BuilderIdNameDesProp<T extends BuilderIdNameDesProp<T>> extends BuilderId<T> {

        public BuilderIdNameDesProp(Entity entity) {
            super(entity);
        }

        public String getName() {
            return entity.getProperty(EP_NAME);
        }

        public T setName(String value) {
            entity.setProperty(EP_NAME, value);
            return getThis();
        }

        public String getDescription() {
            return entity.getProperty(EP_DESCRIPTION);
        }

        public T setDescription(String value) {
            entity.setProperty(EP_DESCRIPTION, value);
            return getThis();
        }

        public MapValue getProperties() {
            return entity.getProperty(EP_PROPERTIES);
        }

        public T setProperties(MapValue properties) {
            entity.setProperty(EP_PROPERTIES, properties);
            return getThis();
        }
    }

    public static class BuilderIdNameDefDesProp<T extends BuilderIdNameDefDesProp<T>> extends BuilderIdNameDesProp<T> {

        public BuilderIdNameDefDesProp(Entity entity) {
            super(entity);
        }

        public String getDefinition() {
            return entity.getProperty(EP_DEFINITION);
        }

        public T setDefinition(String value) {
            entity.setProperty(EP_DEFINITION, value);
            return getThis();
        }

    }

}
