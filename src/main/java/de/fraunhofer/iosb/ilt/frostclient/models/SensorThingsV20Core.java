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

import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_DEFINITION;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_DESCRIPTION;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_ENCODINGTYPE;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_ID;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_NAME;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_PROPERTIES;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_DATASTREAM;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_DATASTREAMS;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_DATASTREAMS_PROXIMATE;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_DATASTREAMS_ULTIMATE;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_FEATURE;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_FEATURES;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_FEATURE_TYPE;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_FEATURE_TYPES;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_HISTORICALLOCATION;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_HISTORICALLOCATIONS;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_LOCATION;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_LOCATIONS;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_OBSERVATION;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_OBSERVATIONS;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_OBSERVEDPROPERTIES;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_OBSERVEDPROPERTY;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_SENSOR;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_SENSORS;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_THING;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_THINGS;
import static de.fraunhofer.iosb.ilt.frostclient.utils.Constants.CONTENT_TYPE_APPLICATION_GEOJSON;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.Exceptions;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.PkValue;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.BuilderId;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.BuilderIdNameDefDesProp;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInterval;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeValue;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.AbstractDataComponent;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.simple.Quantity;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.util.UnitOfMeasurement;
import java.time.ZonedDateTime;
import java.util.Map;
import net.time4j.Moment;
import org.geojson.GeoJsonObject;

/**
 * The core SensorThings v2.0 Sensing data model.
 */
public class SensorThingsV20Core implements DataModel {

    public static final String NAMESPACE = "org.OGC.STA";

    public static final String MQTT_BASE_PATH = "v2.0/";

    public static final String NAME_EP_RESULTTYPE = "resultType";

    public static final String NAME_NP_PROXIMATEFOI = "ProximateFeatureOfInterest";
    public static final String NAME_NP_ULTIMATEFOIS = "UltimateFeaturesOfInterest";

    public static final EntityPropertyMain<Object> EP_FEATURE = SensorThingsV11Sensing.EP_FEATURE;
    public static final EntityPropertyMain<Object> EP_LOCATION = SensorThingsV11Sensing.EP_LOCATION;
    public static final EntityPropertyMain<Object> EP_METADATA = SensorThingsV11Sensing.EP_METADATA;
    public static final EntityPropertyMain<GeoJsonObject> EP_OBSERVEDAREA = SensorThingsV11Sensing.EP_OBSERVEDAREA;
    public static final EntityPropertyMain<TimeValue> EP_PHENOMENONTIME = SensorThingsV11Sensing.EP_PHENOMENONTIME;
    public static final EntityPropertyMain<TimeInterval> EP_PHENOMENONTIMEDS = SensorThingsV11Sensing.EP_PHENOMENONTIMEDS;
    public static final EntityPropertyMain<Object> EP_RESULT = SensorThingsV11Sensing.EP_RESULT;
    public static final EntityPropertyMain<TimeInstant> EP_RESULTTIME = SensorThingsV11Sensing.EP_RESULTTIME;
    public static final EntityPropertyMain<TimeInterval> EP_RESULTTIMEDS = SensorThingsV11Sensing.EP_RESULTTIMEDS;
    public static final EntityPropertyMain<AbstractDataComponent> EP_RESULTTYPE = new EntityPropertyMain<>(NAME_EP_RESULTTYPE, TypeComplex.STA_ABSTRACT_DATA_COMPONENT);
    public static final EntityPropertyMain<TimeInstant> EP_TIME = SensorThingsV11Sensing.EP_TIME;
    public static final EntityPropertyMain<TimeInterval> EP_VALIDTIME = SensorThingsV11Sensing.EP_VALIDTIME;

    public final NavigationPropertyEntity npObservationDatastream = new NavigationPropertyEntity(NAME_DATASTREAM);
    public final NavigationPropertyEntity npObservationProximateFoi = new NavigationPropertyEntity(NAME_NP_PROXIMATEFOI);

    public final NavigationPropertyEntitySet npSensorDatastreams = new NavigationPropertyEntitySet(NAME_DATASTREAMS);

    public final NavigationPropertyEntitySet npObspropDatastreams = new NavigationPropertyEntitySet(NAME_DATASTREAMS);

    public final NavigationPropertyEntitySet npThingHistoricallocations = new NavigationPropertyEntitySet(NAME_HISTORICALLOCATIONS);
    public final NavigationPropertyEntitySet npThingDatastreams = new NavigationPropertyEntitySet(NAME_DATASTREAMS);
    public final NavigationPropertyEntitySet npThingLocations = new NavigationPropertyEntitySet(NAME_LOCATIONS);

    public final NavigationPropertyEntitySet npDatastreamObservations = new NavigationPropertyEntitySet(NAME_OBSERVATIONS, npObservationDatastream);
    public final NavigationPropertyEntitySet npDatastreamObservedproperties = new NavigationPropertyEntitySet(NAME_OBSERVEDPROPERTIES, npObspropDatastreams);
    public final NavigationPropertyEntity npDatastreamSensor = new NavigationPropertyEntity(NAME_SENSOR, npSensorDatastreams);
    public final NavigationPropertyEntity npDatastreamThing = new NavigationPropertyEntity(NAME_THING, npThingDatastreams);
    public final NavigationPropertyEntitySet npDatastreamUltimateFois = new NavigationPropertyEntitySet(NAME_NP_ULTIMATEFOIS);
    public final NavigationPropertyEntity npDatastreamProximateFoi = new NavigationPropertyEntity(NAME_NP_PROXIMATEFOI);

    public final NavigationPropertyEntitySet npFeatureFeatureTypes = new NavigationPropertyEntitySet(NAME_FEATURE_TYPES);
    public final NavigationPropertyEntitySet npFeatureObservations = new NavigationPropertyEntitySet(NAME_OBSERVATIONS, npObservationProximateFoi);
    public final NavigationPropertyEntitySet npFeatureDatastreamsProximate = new NavigationPropertyEntitySet(NAME_DATASTREAMS_PROXIMATE, npDatastreamProximateFoi);
    public final NavigationPropertyEntitySet npFeatureDatastreamsUltimate = new NavigationPropertyEntitySet(NAME_DATASTREAMS_ULTIMATE, npDatastreamUltimateFois);

    public final NavigationPropertyEntitySet npFeatureTypeFeatures = new NavigationPropertyEntitySet(NAME_FEATURES, npFeatureFeatureTypes);

    public final NavigationPropertyEntitySet npLocationHistoricallocations = new NavigationPropertyEntitySet(NAME_HISTORICALLOCATIONS);
    public final NavigationPropertyEntitySet npLocationThings = new NavigationPropertyEntitySet(NAME_THINGS, npThingLocations);

    public final NavigationPropertyEntitySet npHistlocLocations = new NavigationPropertyEntitySet(NAME_LOCATIONS, npLocationHistoricallocations);
    public final NavigationPropertyEntity npHistlocThing = new NavigationPropertyEntity(NAME_THING, npThingHistoricallocations);

    public final EntityType etDatastream = new EntityType(NAME_DATASTREAM, NAME_DATASTREAMS).setNamespace(NAMESPACE);
    public final EntityType etFeature = new EntityType(NAME_FEATURE, NAME_FEATURES).setNamespace(NAMESPACE);
    public final EntityType etFeatureType = new EntityType(NAME_FEATURE_TYPE, NAME_FEATURE_TYPES).setNamespace(NAMESPACE);
    public final EntityType etHistoricalLocation = new EntityType(NAME_HISTORICALLOCATION, NAME_HISTORICALLOCATIONS).setNamespace(NAMESPACE);
    public final EntityType etLocation = new EntityType(NAME_LOCATION, NAME_LOCATIONS).setNamespace(NAMESPACE);
    public final EntityType etObservedProperty = new EntityType(NAME_OBSERVEDPROPERTY, NAME_OBSERVEDPROPERTIES).setNamespace(NAMESPACE);
    public final EntityType etObservation = new EntityType(NAME_OBSERVATION, NAME_OBSERVATIONS)
            .setToStringMethod(e -> e.getPrimaryKeyValues() + " " + e.getProperty(EP_PHENOMENONTIME) + " " + e.getProperty(EP_RESULT))
            .setNamespace(NAMESPACE);
    public final EntityType etSensor = new EntityType(NAME_SENSOR, NAME_SENSORS).setNamespace(NAMESPACE);
    public final EntityType etThing = new EntityType(NAME_THING, NAME_THINGS).setNamespace(NAMESPACE);

    private ModelRegistry mr;

    public SensorThingsV20Core() {
    }

    @Override
    public final void init(SensorThingsService service, ModelRegistry modelRegistry) {
        if (this.mr != null) {
            throw new IllegalArgumentException("Already initialised.");
        }
        this.mr = modelRegistry;
        mr.addDataModel(this);

        mr.registerPropertyType(TypeComplex.STA_OBJECT)
                .registerPropertyType(TypeComplex.STA_MAP)
                .registerPropertyType(TypeComplex.STA_TIMEINTERVAL)
                .registerPropertyType(TypeComplex.STA_TIMEVALUE)
                .registerEntityType(etDatastream)
                .registerEntityType(etFeature)
                .registerEntityType(etFeatureType)
                .registerEntityType(etHistoricalLocation)
                .registerEntityType(etLocation)
                .registerEntityType(etObservation)
                .registerEntityType(etObservedProperty)
                .registerEntityType(etSensor)
                .registerEntityType(etThing);

        etDatastream
                .registerProperty(EP_ID)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DEFINITION)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_RESULTTYPE)
                .registerProperty(EP_OBSERVEDAREA)
                .registerProperty(EP_PHENOMENONTIMEDS)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(EP_RESULTTIMEDS)
                .registerProperty(npDatastreamSensor)
                .registerProperty(npDatastreamThing)
                .registerProperty(npDatastreamProximateFoi)
                .registerProperty(npDatastreamUltimateFois)
                .registerProperty(npDatastreamObservedproperties)
                .registerProperty(npDatastreamObservations);

        etFeature
                .registerProperty(EP_ID)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DEFINITION)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_ENCODINGTYPE)
                .registerProperty(EP_FEATURE)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(npFeatureFeatureTypes)
                .registerProperty(npFeatureDatastreamsProximate)
                .registerProperty(npFeatureDatastreamsUltimate)
                .registerProperty(npFeatureObservations);

        etFeatureType
                .registerProperty(EP_ID)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DEFINITION)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(npFeatureTypeFeatures);

        etHistoricalLocation
                .registerProperty(EP_ID)
                .registerProperty(EP_TIME)
                .registerProperty(npHistlocThing)
                .registerProperty(npHistlocLocations);

        etLocation
                .registerProperty(EP_ID)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DEFINITION)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_ENCODINGTYPE)
                .registerProperty(EP_LOCATION)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(npLocationHistoricallocations)
                .registerProperty(npLocationThings);

        etObservation
                .registerProperty(EP_ID)
                .registerProperty(EP_PHENOMENONTIME)
                .registerProperty(EP_RESULTTIME)
                .registerProperty(EP_RESULT)
                .registerProperty(EP_VALIDTIME)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(npObservationDatastream)
                .registerProperty(npObservationProximateFoi);

        etObservedProperty
                .registerProperty(EP_ID)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DEFINITION)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(npObspropDatastreams);

        etSensor
                .registerProperty(EP_ID)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DEFINITION)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_ENCODINGTYPE)
                .registerProperty(EP_METADATA)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(npSensorDatastreams);

        etThing
                .registerProperty(EP_ID)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DEFINITION)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(npThingLocations)
                .registerProperty(npThingHistoricallocations)
                .registerProperty(npThingDatastreams);
    }

    @Override
    public boolean isInitialised() {
        return mr != null;
    }

    @Override
    public String getMqttBasePath() {
        return MQTT_BASE_PATH;
    }

    public DatastreamBuilder buildDatastream() {
        return new DatastreamBuilder(this);
    }

    public DatastreamBuilder editDatastream(Entity entity) {
        return new DatastreamBuilder(this, entity);
    }

    public FeatureBuilder buildFeature() {
        return new FeatureBuilder(this);
    }

    public FeatureBuilder editFeature(Entity entity) {
        return new FeatureBuilder(this, entity);
    }

    public FeatureTypeBuilder buildFeatureType() {
        return new FeatureTypeBuilder(this);
    }

    public FeatureTypeBuilder editFeatureType(Entity entity) {
        return new FeatureTypeBuilder(this, entity);
    }

    public HistoricalLocationBuilder buildHistoricalLocation() {
        return new HistoricalLocationBuilder(this);
    }

    public HistoricalLocationBuilder editHistoricalLocation(Entity entity) {
        return new HistoricalLocationBuilder(this, entity);
    }

    public LocationBuilder buildLocation() {
        return new LocationBuilder(this);
    }

    public LocationBuilder editLocation(Entity entity) {
        return new LocationBuilder(this, entity);
    }

    public ObservationBuilder buildObservation() {
        return new ObservationBuilder(this);
    }

    public ObservationBuilder editObservation(Entity entity) {
        return new ObservationBuilder(this, entity);
    }

    public ObservedPropertyBuilder buildObservedProperty() {
        return new ObservedPropertyBuilder(this);
    }

    public ObservedPropertyBuilder editObservedProperty(Entity entity) {
        return new ObservedPropertyBuilder(this, entity);
    }

    public SensorBuilder buildSensor() {
        return new SensorBuilder(this);
    }

    public SensorBuilder editSensor(Entity entity) {
        return new SensorBuilder(this, entity);
    }

    public ThingBuilder buildThing() {
        return new ThingBuilder(this);
    }

    public ThingBuilder editThing(Entity entity) {
        return new ThingBuilder(this, entity);
    }

    public static class DatastreamBuilder extends BuilderIdNameDefDesProp<DatastreamBuilder> {

        SensorThingsV20Core mdlCore;

        public DatastreamBuilder(SensorThingsV20Core mdlCore) {
            super(new Entity(mdlCore.etDatastream));
            this.mdlCore = mdlCore;
        }

        public DatastreamBuilder(SensorThingsV20Core mdlCore, Entity entity) {
            super(entity);
            this.mdlCore = mdlCore;
        }

        public DatastreamBuilder setResultType(AbstractDataComponent resultType) {
            entity.setProperty(EP_RESULTTYPE, resultType);
            return getThis();
        }

        public DatastreamBuilder addObservations(Entity ds) {
            entity.addNavigationEntity(mdlCore.npDatastreamObservations, ds);
            return getThis();
        }

        public DatastreamBuilder addUltimateFoi(Entity uFoi) {
            entity.addNavigationEntity(mdlCore.npDatastreamUltimateFois, uFoi);
            return getThis();
        }

        public DatastreamBuilder setProximateFoi(Entity pFoi) {
            entity.setProperty(mdlCore.npDatastreamProximateFoi, pFoi);
            return getThis();
        }

        public DatastreamBuilder setSensor(Entity sensor) {
            entity.setProperty(mdlCore.npDatastreamSensor, sensor);
            return getThis();
        }

        public DatastreamBuilder setThing(Entity thing) {
            entity.setProperty(mdlCore.npDatastreamThing, thing);
            return getThis();
        }
    }

    public static class FeatureBuilder extends BuilderIdNameDefDesProp<FeatureBuilder> {

        SensorThingsV20Core mdlCore;

        public FeatureBuilder(SensorThingsV20Core mdlCore) {
            super(new Entity(mdlCore.etFeature));
            this.mdlCore = mdlCore;
        }

        public FeatureBuilder(SensorThingsV20Core mdlCore, Entity entity) {
            super(entity);
            this.mdlCore = mdlCore;
        }

        public FeatureBuilder addDatastreamProximate(Entity ds) {
            entity.addNavigationEntity(mdlCore.npFeatureDatastreamsProximate, ds);
            return getThis();
        }

        public FeatureBuilder addDatastreamUltimate(Entity ds) {
            entity.addNavigationEntity(mdlCore.npFeatureDatastreamsUltimate, ds);
            return getThis();
        }

        public FeatureBuilder addFeatureType(Entity ft) {
            entity.addNavigationEntity(mdlCore.npFeatureFeatureTypes, ft);
            return getThis();
        }

        public FeatureBuilder addObservation(Entity obs) {
            entity.addNavigationEntity(mdlCore.npFeatureObservations, obs);
            return getThis();
        }
    }

    public static class FeatureTypeBuilder extends BuilderIdNameDefDesProp<FeatureTypeBuilder> {

        SensorThingsV20Core mdlCore;

        public FeatureTypeBuilder(SensorThingsV20Core mdlCore) {
            super(new Entity(mdlCore.etFeatureType));
            this.mdlCore = mdlCore;
        }

        public FeatureTypeBuilder(SensorThingsV20Core mdlCore, Entity entity) {
            super(entity);
            this.mdlCore = mdlCore;
        }

        public FeatureTypeBuilder addFeature(Entity feature) {
            entity.addNavigationEntity(mdlCore.npFeatureTypeFeatures, feature);
            return getThis();
        }
    }

    public static class HistoricalLocationBuilder extends BuilderId<HistoricalLocationBuilder> {

        SensorThingsV20Core mdlCore;

        public HistoricalLocationBuilder(SensorThingsV20Core mdlCore) {
            super(new Entity(mdlCore.etHistoricalLocation));
            this.mdlCore = mdlCore;
        }

        public HistoricalLocationBuilder(SensorThingsV20Core mdlCore, Entity entity) {
            super(entity);
            this.mdlCore = mdlCore;
        }

        public HistoricalLocationBuilder setTime(TimeInstant time) {
            entity.setProperty(EP_TIME, time);
            return getThis();
        }

        public HistoricalLocationBuilder setTime(Moment time) {
            entity.setProperty(EP_TIME, TimeInstant.create(time));
            return getThis();
        }

        public HistoricalLocationBuilder addLocation(Entity location) {
            entity.addNavigationEntity(mdlCore.npHistlocLocations, location);
            return getThis();
        }

        public HistoricalLocationBuilder setThing(Entity thing) {
            entity.setProperty(mdlCore.npHistlocThing, thing);
            return getThis();
        }

    }

    public static class LocationBuilder extends BuilderIdNameDefDesProp<LocationBuilder> {

        SensorThingsV20Core mdlCore;

        public LocationBuilder(SensorThingsV20Core mdlCore) {
            super(new Entity(mdlCore.etLocation));
            this.mdlCore = mdlCore;
        }

        public LocationBuilder(SensorThingsV20Core mdlCore, Entity entity) {
            super(entity);
            this.mdlCore = mdlCore;
        }

        public LocationBuilder setEncodingType(String encodingType) {
            entity.setProperty(EP_ENCODINGTYPE, encodingType);
            return getThis();
        }

        public LocationBuilder setLocation(Object location) {
            entity.setProperty(EP_LOCATION, location);
            return getThis();
        }

        public LocationBuilder addHistoricalLocation(Entity hl) {
            entity.addNavigationEntity(mdlCore.npLocationHistoricallocations, hl);
            return getThis();
        }

        public LocationBuilder addThing(Entity thing) {
            entity.addNavigationEntity(mdlCore.npLocationThings, thing);
            return getThis();
        }
    }

    public static class ObservationBuilder extends BuilderId<ObservationBuilder> {

        SensorThingsV20Core mdlCore;

        public ObservationBuilder(SensorThingsV20Core mdlCore) {
            super(new Entity(mdlCore.etObservation));
            this.mdlCore = mdlCore;
        }

        public ObservationBuilder(SensorThingsV20Core mdlCore, Entity entity) {
            super(entity);
            this.mdlCore = mdlCore;
        }

        public ObservationBuilder setResult(Object result) {
            entity.setProperty(EP_RESULT, result);
            return getThis();
        }

        public ObservationBuilder setPhenomenonTime(TimeValue phenTime) {
            entity.setProperty(EP_PHENOMENONTIME, phenTime);
            return getThis();
        }

        public ObservationBuilder setPhenomenonTimeStart(Moment start) {
            TimeValue time = entity.getProperty(EP_PHENOMENONTIME);
            if (time == null) {
                time = TimeValue.create(start);
                entity.setProperty(EP_PHENOMENONTIME, time);
            } else {
                time.setProperty(TimeValue.EP_START_TIME, TimeInstant.create(start));
            }
            return getThis();
        }

        public ObservationBuilder setPhenomenonTimeEnd(Moment end) {
            TimeValue time = entity.getProperty(EP_PHENOMENONTIME);
            Exceptions.illegalArgumentIf(time == null, "Set the start time first.");
            time.setProperty(TimeValue.EP_END_TIME, TimeInstant.create(end));
            return getThis();
        }

        public ObservationBuilder setResultTime(TimeInstant resultTime) {
            entity.setProperty(EP_RESULTTIME, resultTime);
            return getThis();
        }

        public ObservationBuilder setResultTime(Moment resultTime) {
            entity.setProperty(EP_RESULTTIME, TimeInstant.create(resultTime));
            return getThis();
        }

        public ObservationBuilder setValidTime(TimeInterval validTime) {
            entity.setProperty(EP_VALIDTIME, validTime);
            return getThis();
        }

        public ObservationBuilder setProperties(MapValue properties) {
            entity.setProperty(EP_PROPERTIES, properties);
            return getThis();
        }

        public ObservationBuilder setDatastream(Entity ds) {
            entity.setProperty(mdlCore.npObservationDatastream, ds);
            return getThis();
        }

        public ObservationBuilder setProximateFoi(Entity pFoi) {
            entity.setProperty(mdlCore.npObservationProximateFoi, pFoi);
            return getThis();
        }
    }

    public static class ObservedPropertyBuilder extends BuilderIdNameDefDesProp<ObservedPropertyBuilder> {

        SensorThingsV20Core mdlCore;

        public ObservedPropertyBuilder(SensorThingsV20Core mdlCore) {
            super(new Entity(mdlCore.etObservedProperty));
            this.mdlCore = mdlCore;
        }

        public ObservedPropertyBuilder(SensorThingsV20Core mdlCore, Entity entity) {
            super(entity);
            this.mdlCore = mdlCore;
        }

        public ObservedPropertyBuilder addDatastream(Entity ds) {
            entity.addNavigationEntity(mdlCore.npObspropDatastreams, ds);
            return getThis();
        }
    }

    public static class SensorBuilder extends BuilderIdNameDefDesProp<SensorBuilder> {

        SensorThingsV20Core mdlCore;

        public SensorBuilder(SensorThingsV20Core mdlCore) {
            super(new Entity(mdlCore.etSensor));
            this.mdlCore = mdlCore;
        }

        public SensorBuilder(SensorThingsV20Core mdlCore, Entity entity) {
            super(entity);
            this.mdlCore = mdlCore;
        }

        public SensorBuilder setEncodingType(String encodingType) {
            entity.setProperty(EP_ENCODINGTYPE, encodingType);
            return getThis();
        }

        public SensorBuilder setMetadata(Object metadata) {
            entity.setProperty(EP_METADATA, metadata);
            return getThis();
        }

        public SensorBuilder addDatastream(Entity ds) {
            entity.addNavigationEntity(mdlCore.npSensorDatastreams, ds);
            return getThis();
        }
    }

    public static class ThingBuilder extends BuilderIdNameDefDesProp<ThingBuilder> {

        SensorThingsV20Core mdlCore;

        public ThingBuilder(SensorThingsV20Core mdlCore) {
            super(new Entity(mdlCore.etThing));
            this.mdlCore = mdlCore;
        }

        public ThingBuilder(SensorThingsV20Core mdlCore, Entity entity) {
            super(entity);
            this.mdlCore = mdlCore;
        }

        public ThingBuilder addDatastream(Entity ds) {
            entity.addNavigationEntity(mdlCore.npThingDatastreams, ds);
            return getThis();
        }

        public ThingBuilder addHistoricallocation(Entity hl) {
            entity.addNavigationEntity(mdlCore.npThingHistoricallocations, hl);
            return getThis();
        }

        public ThingBuilder addLocation(Entity location) {
            entity.addNavigationEntity(mdlCore.npThingLocations, location);
            return getThis();
        }
    }

    @Deprecated
    public Entity newThing() {
        return new Entity(etThing);
    }

    @Deprecated
    public Entity newThing(Object id) {
        return new Entity(etThing)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    @Deprecated
    public Entity newThing(String name, String description) {
        return newThing()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description);
    }

    @Deprecated
    public Entity newThing(String name, String description, Map<String, Object> properties) {
        return newThing(name, description, new MapValue(TypeComplex.STA_MAP, properties));
    }

    @Deprecated
    public Entity newThing(String name, String description, MapValue properties) {
        return newThing(name, description)
                .setProperty(EP_PROPERTIES, properties);
    }

    @Deprecated
    public Entity newLocation() {
        return new Entity(etLocation);
    }

    @Deprecated
    public Entity newLocation(Object id) {
        return new Entity(etLocation)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    @Deprecated
    public Entity newLocation(String name, String description, GeoJsonObject location) {
        return newLocation(name, description, CONTENT_TYPE_APPLICATION_GEOJSON, location);
    }

    @Deprecated
    public Entity newLocation(String name, String description, String encodingType, Object location) {
        return newLocation()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description)
                .setProperty(EP_ENCODINGTYPE, encodingType)
                .setProperty(EP_LOCATION, location);
    }

    @Deprecated
    public Entity newDatastream() {
        return new Entity(etDatastream);
    }

    @Deprecated
    public Entity newDatastream(Object id) {
        return new Entity(etDatastream)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    @Deprecated
    public Entity newDatastream(String name, String description, String definition, UnitOfMeasurement uom) {
        return newDatastream(name, description, new Quantity().setDefinition(definition).setUom(uom));
    }

    @Deprecated
    public Entity newDatastream(String name, String description, AbstractDataComponent resultType) {
        return newDatastream()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description)
                .setProperty(EP_RESULTTYPE, resultType);
    }

    @Deprecated
    public Entity newSensor() {
        return new Entity(etSensor);
    }

    @Deprecated
    public Entity newSensor(Object id) {
        return new Entity(etSensor)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    @Deprecated
    public Entity newSensor(String name, String description, String encodingType, String metaData) {
        return newSensor()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description)
                .setProperty(EP_ENCODINGTYPE, encodingType)
                .setProperty(EP_METADATA, metaData);
    }

    @Deprecated
    public Entity newObservedProperty() {
        return new Entity(etObservedProperty);
    }

    @Deprecated
    public Entity newObservedProperty(Object id) {
        return new Entity(etObservedProperty)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    @Deprecated
    public Entity newObservedProperty(String name, String definition, String desription) {
        return newObservedProperty()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DEFINITION, definition)
                .setProperty(EP_DESCRIPTION, desription);
    }

    @Deprecated
    public Entity newObservation() {
        return new Entity(etObservation);
    }

    @Deprecated
    public Entity newObservation(Object result) {
        return newObservation()
                .setProperty(EP_RESULT, result);
    }

    @Deprecated
    public Entity newObservation(Object result, Entity datastream) {
        if (!etDatastream.equals(datastream.getType())) {
            throw new IllegalArgumentException("Datastream Entity must have entityType Datastream, not " + datastream.getType());
        }
        return newObservation()
                .setProperty(EP_RESULT, result)
                .setProperty(npObservationDatastream, datastream);
    }

    @Deprecated
    public Entity newObservation(Object result, TimeValue phenomenonTime) {
        return newObservation(result)
                .setProperty(EP_PHENOMENONTIME, phenomenonTime);
    }

    @Deprecated
    public Entity newObservation(Object result, ZonedDateTime phenomenonTime) {
        return newObservation(result, TimeValue.create(phenomenonTime));
    }

    @Deprecated
    public Entity newObservation(Object result, TimeValue phenomenonTime, Entity datastream) {
        return newObservation(result, datastream)
                .setProperty(EP_PHENOMENONTIME, phenomenonTime);
    }

    @Deprecated
    public Entity newObservation(Object result, ZonedDateTime phenomenonTime, Entity datastream) {
        return newObservation(result, TimeValue.create(phenomenonTime), datastream);
    }

    @Deprecated
    public Entity newObservation(Object result, TimeInterval phenomenonTime) {
        return newObservation(result, new TimeValue(phenomenonTime));
    }

    @Deprecated
    public Entity newObservation(Object result, TimeInterval phenomenonTime, Entity datastream) {
        return newObservation(result, new TimeValue(phenomenonTime), datastream);
    }

    @Deprecated
    public Entity newHistoricalLocation() {
        return new Entity(etHistoricalLocation);
    }

    @Deprecated
    public Entity newHistoricalLocation(Object id) {
        return new Entity(etHistoricalLocation)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    @Deprecated
    public Entity newHistoricalLocation(ZonedDateTime time) {
        return newHistoricalLocation()
                .setProperty(EP_TIME, TimeInstant.create(time));
    }

    @Deprecated
    public Entity newHistoricalLocation(ZonedDateTime time, Entity thing, Entity... location) {
        return newHistoricalLocation()
                .setProperty(EP_TIME, TimeInstant.create(time))
                .setProperty(npHistlocThing, thing)
                .addNavigationEntity(npHistlocLocations, location);
    }

    @Deprecated
    public Entity newFeature() {
        return new Entity(etFeature);
    }

    @Deprecated
    public Entity newFeature(Object id) {
        return new Entity(etFeature)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    @Deprecated
    public Entity newFeature(String name, String description, GeoJsonObject location) {
        return newFeature(name, description, CONTENT_TYPE_APPLICATION_GEOJSON, location);
    }

    @Deprecated
    public Entity newFeature(String name, String description, String encodingType, Object location) {
        return newFeature()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description)
                .setProperty(EP_ENCODINGTYPE, encodingType)
                .setProperty(EP_FEATURE, location);
    }

    @Deprecated
    public Entity newFeatureType() {
        return new Entity(etFeatureType);
    }

    @Deprecated
    public Entity newFeatureType(Object id) {
        return new Entity(etFeatureType)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    @Deprecated
    public Entity newFeatureType(String name, String description) {
        return newFeatureType()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description);
    }

    @Deprecated
    public Entity newFeatureType(String name, String description, String definition) {
        return newFeatureType(name, description)
                .setProperty(EP_DEFINITION, definition);
    }

}
