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

import static de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive.EDM_DATETIMEOFFSET;
import static de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive.EDM_STRING;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_DEFINITION;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_DESCRIPTION;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_ENCODINGTYPE;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_ID;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_NAME;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_PROPERTIES;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_DATASTREAM;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_DATASTREAMS;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_FEATUREOFINTEREST;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_FEATURESOFINTEREST;
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
import static de.fraunhofer.iosb.ilt.frostclient.utils.TypeReferencesHelper.TYPE_REFERENCE_UOM;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.PkValue;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInterval;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.UnitOfMeasurement;
import de.fraunhofer.iosb.ilt.frostclient.utils.Constants;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import java.time.ZonedDateTime;
import java.util.Map;
import org.geojson.GeoJsonObject;

/**
 * The core SensorThings v1.1 Sensing data model.
 */
public class SensorThingsV11Sensing implements DataModel {

    public static final String MQTT_BASE_PATH = "v1.1/";

    public static final String NAME_DEFINITION = CommonProperties.NAME_EP_DEFINITION;
    public static final String NAME_NAME = CommonProperties.NAME_EP_NAME;
    public static final String NAME_SYMBOL = "symbol";

    public static final String NAME_EP_FEATURE = "feature";
    public static final String NAME_EP_LOCATION = "location";
    public static final String NAME_EP_METADATA = "metadata";
    public static final String NAME_EP_OBSERVATIONTYPE = "observationType";
    public static final String NAME_EP_OBSERVEDAREA = "observedArea";
    public static final String NAME_EP_PARAMETERS = "parameters";
    public static final String NAME_EP_PHENOMENONTIME = "phenomenonTime";
    public static final String NAME_EP_RESULT = "result";
    public static final String NAME_EP_RESULTTIME = "resultTime";
    public static final String NAME_EP_RESULTQUALITY = "resultQuality";
    public static final String NAME_EP_SYMBOL = "symbol";
    public static final String NAME_EP_TIME = "time";
    public static final String NAME_EP_UNITOFMEASUREMENT = "unitOfMeasurement";
    public static final String NAME_EP_VALIDTIME = "validTime";

    public static final EntityPropertyMain<Object> EP_FEATURE = new EntityPropertyMain<>(NAME_EP_FEATURE, TypePrimitive.EDM_GEOMETRY);
    public static final EntityPropertyMain<Object> EP_LOCATION = new EntityPropertyMain<>(NAME_EP_LOCATION, TypePrimitive.EDM_GEOMETRY);
    public static final EntityPropertyMain<String> EP_METADATA = new EntityPropertyMain<>(NAME_EP_METADATA, EDM_STRING);
    public static final EntityPropertyMain<String> EP_OBSERVATIONTYPE = new EntityPropertyMain<>(NAME_EP_OBSERVATIONTYPE, EDM_STRING);
    public static final EntityPropertyMain<GeoJsonObject> EP_OBSERVEDAREA = new EntityPropertyMain<GeoJsonObject>(NAME_EP_OBSERVEDAREA, TypePrimitive.EDM_GEOMETRY).setReadOnly(true);
    public static final EntityPropertyMain<TimeValue> EP_PHENOMENONTIME = new EntityPropertyMain<>(NAME_EP_PHENOMENONTIME, TypeComplex.STA_TIMEVALUE);
    public static final EntityPropertyMain<TimeInterval> EP_PHENOMENONTIMEDS = new EntityPropertyMain<TimeInterval>(NAME_EP_PHENOMENONTIME, TypeComplex.STA_TIMEINTERVAL).setReadOnly(true);
    public static final EntityPropertyMain<Map<String, Object>> EP_PARAMETERS = new EntityPropertyMain<>(NAME_EP_PARAMETERS, TypeComplex.STA_MAP);
    public static final EntityPropertyMain<Object> EP_RESULT = new EntityPropertyMain<>(NAME_EP_RESULT, TypePrimitive.EDM_UNTYPED, true);
    public static final EntityPropertyMain<TimeInstant> EP_RESULTTIME = new EntityPropertyMain<>(NAME_EP_RESULTTIME, EDM_DATETIMEOFFSET);
    public static final EntityPropertyMain<TimeInterval> EP_RESULTTIMEDS = new EntityPropertyMain<TimeInterval>(NAME_EP_RESULTTIME, TypeComplex.STA_TIMEINTERVAL).setReadOnly(true);
    public static final EntityPropertyMain<Object> EP_RESULTQUALITY = new EntityPropertyMain<>(NAME_EP_RESULTQUALITY, TypeComplex.STA_OBJECT);
    public static final EntityPropertyMain<String> EP_SYMBOL = new EntityPropertyMain<>(NAME_EP_SYMBOL, EDM_STRING);
    public static final EntityPropertyMain<TimeInstant> EP_TIME = new EntityPropertyMain<>(NAME_EP_TIME, EDM_DATETIMEOFFSET);
    public static final EntityPropertyMain<TimeInterval> EP_VALIDTIME = new EntityPropertyMain<>(NAME_EP_VALIDTIME, TypeComplex.STA_TIMEINTERVAL);

    public static final TypeComplex ept_Uom = new TypeComplex("UnitOfMeasurement", "The Unit Of Measurement Type", false, UnitOfMeasurement::new, ParserUtils.getDefaultDeserializer(TYPE_REFERENCE_UOM), ParserUtils.getDefaultSerializer())
            .registerProperty(EP_NAME)
            .registerProperty(EP_SYMBOL)
            .registerProperty(EP_DEFINITION);
    public static final EntityPropertyMain<UnitOfMeasurement> EP_UNITOFMEASUREMENT = new EntityPropertyMain<>(NAME_EP_UNITOFMEASUREMENT, ept_Uom);

    public final NavigationPropertyEntity npObservationDatastream = new NavigationPropertyEntity(NAME_DATASTREAM);
    public final NavigationPropertyEntity npObservationFeatureofinterest = new NavigationPropertyEntity(NAME_FEATUREOFINTEREST);

    public final NavigationPropertyEntitySet npSensorDatastreams = new NavigationPropertyEntitySet(NAME_DATASTREAMS);

    public final NavigationPropertyEntitySet npObspropDatastreams = new NavigationPropertyEntitySet(NAME_DATASTREAMS);

    public final NavigationPropertyEntitySet npThingHistoricallocations = new NavigationPropertyEntitySet(NAME_HISTORICALLOCATIONS);
    public final NavigationPropertyEntitySet npThingDatastreams = new NavigationPropertyEntitySet(NAME_DATASTREAMS);
    public final NavigationPropertyEntitySet npThingLocations = new NavigationPropertyEntitySet(NAME_LOCATIONS);

    public final NavigationPropertyEntitySet npDatastreamObservations = new NavigationPropertyEntitySet(NAME_OBSERVATIONS, npObservationDatastream);
    public final NavigationPropertyEntity npDatastreamObservedproperty = new NavigationPropertyEntity(NAME_OBSERVEDPROPERTY, npObspropDatastreams);
    public final NavigationPropertyEntity npDatastreamSensor = new NavigationPropertyEntity(NAME_SENSOR, npSensorDatastreams);
    public final NavigationPropertyEntity npDatastreamThing = new NavigationPropertyEntity(NAME_THING, npThingDatastreams);

    public final NavigationPropertyEntitySet npFeatureObservations = new NavigationPropertyEntitySet(NAME_OBSERVATIONS, npObservationFeatureofinterest);

    public final NavigationPropertyEntitySet npLocationHistoricallocations = new NavigationPropertyEntitySet(NAME_HISTORICALLOCATIONS);
    public final NavigationPropertyEntitySet npLocationThings = new NavigationPropertyEntitySet(NAME_THINGS, npThingLocations);

    public final NavigationPropertyEntitySet npHistlocLocations = new NavigationPropertyEntitySet(NAME_LOCATIONS, npLocationHistoricallocations);
    public final NavigationPropertyEntity npHistlocThing = new NavigationPropertyEntity(NAME_THING, npThingHistoricallocations);

    public final EntityType etThing = new EntityType(NAME_THING, NAME_THINGS);
    public final EntityType etSensor = new EntityType(NAME_SENSOR, NAME_SENSORS);
    public final EntityType etObservedProperty = new EntityType(NAME_OBSERVEDPROPERTY, NAME_OBSERVEDPROPERTIES);
    public final EntityType etObservation = new EntityType(NAME_OBSERVATION, NAME_OBSERVATIONS)
            .setToStringMethod(e -> e.getEntityType().toString() + ": " + e.getPrimaryKeyValues() + " " + e.getProperty(EP_PHENOMENONTIME) + " " + e.getProperty(EP_RESULT));
    public final EntityType etLocation = new EntityType(NAME_LOCATION, NAME_LOCATIONS);
    public final EntityType etHistoricalLocation = new EntityType(NAME_HISTORICALLOCATION, NAME_HISTORICALLOCATIONS);
    public final EntityType etFeatureOfInterest = new EntityType(NAME_FEATUREOFINTEREST, NAME_FEATURESOFINTEREST);
    public final EntityType etDatastream = new EntityType(NAME_DATASTREAM, NAME_DATASTREAMS);

    private ModelRegistry mr;

    public SensorThingsV11Sensing() {
    }

    @Override
    public final void init(SensorThingsService service, ModelRegistry modelRegistry) {
        if (this.mr != null) {
            throw new IllegalArgumentException("Already initialised.");
        }
        this.mr = modelRegistry;
        mr.addDataModel(this);

        mr.registerPropertyType(ept_Uom)
                .registerPropertyType(TypeComplex.STA_OBJECT)
                .registerPropertyType(TypeComplex.STA_MAP)
                .registerPropertyType(TypeComplex.STA_TIMEINTERVAL)
                .registerPropertyType(TypeComplex.STA_TIMEVALUE)
                .registerEntityType(etThing)
                .registerEntityType(etSensor)
                .registerEntityType(etLocation)
                .registerEntityType(etDatastream)
                .registerEntityType(etObservation)
                .registerEntityType(etObservedProperty)
                .registerEntityType(etFeatureOfInterest)
                .registerEntityType(etHistoricalLocation);

        etDatastream
                .registerProperty(EP_ID)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_OBSERVATIONTYPE)
                .registerProperty(EP_UNITOFMEASUREMENT)
                .registerProperty(EP_OBSERVEDAREA)
                .registerProperty(EP_PHENOMENONTIMEDS)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(EP_RESULTTIMEDS)
                .registerProperty(npDatastreamObservedproperty)
                .registerProperty(npDatastreamSensor)
                .registerProperty(npDatastreamThing)
                .registerProperty(npDatastreamObservations);

        etFeatureOfInterest
                .registerProperty(EP_ID)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_ENCODINGTYPE)
                .registerProperty(EP_FEATURE)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(npFeatureObservations);

        etHistoricalLocation
                .registerProperty(EP_ID)
                .registerProperty(EP_TIME)
                .registerProperty(npHistlocThing)
                .registerProperty(npHistlocLocations);

        etLocation
                .registerProperty(EP_ID)
                .registerProperty(EP_NAME)
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
                .registerProperty(EP_RESULTQUALITY)
                .registerProperty(EP_VALIDTIME)
                .registerProperty(EP_PARAMETERS)
                .registerProperty(npObservationDatastream)
                .registerProperty(npObservationFeatureofinterest);

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
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_ENCODINGTYPE)
                .registerProperty(EP_METADATA)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(npSensorDatastreams);

        etThing
                .registerProperty(EP_ID)
                .registerProperty(EP_NAME)
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

    public Entity newThing() {
        return new Entity(etThing);
    }

    public Entity newThing(Object id) {
        return new Entity(etThing)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    public Entity newThing(String name, String description) {
        return newThing()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description);
    }

    public Entity newThing(String name, String description, Map<String, Object> properties) {
        return newThing(name, description, new MapValue(properties));
    }

    public Entity newThing(String name, String description, MapValue properties) {
        return newThing(name, description)
                .setProperty(EP_PROPERTIES, properties);
    }

    public Entity newLocation() {
        return new Entity(etLocation);
    }

    public Entity newLocation(Object id) {
        return new Entity(etLocation)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    public Entity newLocation(String name, String description, GeoJsonObject location) {
        return newLocation(name, description, CONTENT_TYPE_APPLICATION_GEOJSON, location);
    }

    public Entity newLocation(String name, String description, String encodingType, Object location) {
        return newLocation()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description)
                .setProperty(EP_ENCODINGTYPE, encodingType)
                .setProperty(EP_LOCATION, location);
    }

    public Entity newDatastream() {
        return new Entity(etDatastream);
    }

    public Entity newDatastream(Object id) {
        return new Entity(etDatastream)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    public Entity newDatastream(String name, String description, UnitOfMeasurement uom) {
        return newDatastream(name, description, Constants.OM_MEASUREMENT, uom);
    }

    public Entity newDatastream(String name, String description, String observationType, UnitOfMeasurement uom) {
        return newDatastream()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description)
                .setProperty(EP_OBSERVATIONTYPE, observationType)
                .setProperty(EP_UNITOFMEASUREMENT, uom);
    }

    public Entity newSensor() {
        return new Entity(etSensor);
    }

    public Entity newSensor(Object id) {
        return new Entity(etSensor)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    public Entity newSensor(String name, String description, String encodingType, String metaData) {
        return newSensor()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description)
                .setProperty(EP_ENCODINGTYPE, encodingType)
                .setProperty(EP_METADATA, metaData);
    }

    public Entity newObservedProperty() {
        return new Entity(etObservedProperty);
    }

    public Entity newObservedProperty(Object id) {
        return new Entity(etObservedProperty)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    public Entity newObservedProperty(String name, String definition, String desription) {
        return newObservedProperty()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DEFINITION, definition)
                .setProperty(EP_DESCRIPTION, desription);
    }

    public Entity newObservation() {
        return new Entity(etObservation);
    }

    public Entity newObservation(Object result) {
        return newObservation()
                .setProperty(EP_RESULT, result);
    }

    public Entity newObservation(Object result, Entity datastream) {
        if (!etDatastream.equals(datastream.getEntityType())) {
            throw new IllegalArgumentException("Datastream Entity must have entityType Datastream, not " + datastream.getEntityType());
        }
        return newObservation()
                .setProperty(EP_RESULT, result)
                .setProperty(npObservationDatastream, datastream);
    }

    public Entity newObservation(Object result, TimeValue phenomenonTime) {
        return newObservation(result)
                .setProperty(EP_PHENOMENONTIME, phenomenonTime);
    }

    public Entity newObservation(Object result, ZonedDateTime phenomenonTime) {
        return newObservation(result, TimeValue.create(phenomenonTime));
    }

    public Entity newObservation(Object result, TimeValue phenomenonTime, Entity datastream) {
        return newObservation(result, datastream)
                .setProperty(EP_PHENOMENONTIME, phenomenonTime);
    }

    public Entity newObservation(Object result, ZonedDateTime phenomenonTime, Entity datastream) {
        return newObservation(result, TimeValue.create(phenomenonTime), datastream);
    }

    public Entity newObservation(Object result, TimeInterval phenomenonTime) {
        return newObservation(result, new TimeValue(phenomenonTime));
    }

    public Entity newObservation(Object result, TimeInterval phenomenonTime, Entity datastream) {
        return newObservation(result, new TimeValue(phenomenonTime), datastream);
    }

    public Entity newHistoricalLocation() {
        return new Entity(etHistoricalLocation);
    }

    public Entity newHistoricalLocation(Object id) {
        return new Entity(etHistoricalLocation)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    public Entity newHistoricalLocation(ZonedDateTime time) {
        return newHistoricalLocation()
                .setProperty(EP_TIME, TimeInstant.create(time));
    }

    public Entity newHistoricalLocation(ZonedDateTime time, Entity thing, Entity... location) {
        return newHistoricalLocation()
                .setProperty(EP_TIME, TimeInstant.create(time))
                .setProperty(npHistlocThing, thing)
                .addNavigationEntity(npHistlocLocations, location);
    }

    public Entity newFeatureOfInterest() {
        return new Entity(etFeatureOfInterest);
    }

    public Entity newFeatureOfInterest(Object id) {
        return new Entity(etFeatureOfInterest)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    public Entity newFeatureOfInterest(String name, String description, GeoJsonObject location) {
        return newFeatureOfInterest(name, description, CONTENT_TYPE_APPLICATION_GEOJSON, location);
    }

    public Entity newFeatureOfInterest(String name, String description, String encodingType, Object location) {
        return newFeatureOfInterest()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description)
                .setProperty(EP_ENCODINGTYPE, encodingType)
                .setProperty(EP_FEATURE, location);
    }

}
