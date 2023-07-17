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
package de.fraunhofer.iosb.ilt.frostclient.models;

import static de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeSimplePrimitive.EDM_STRING;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.EP_DESCRIPTION;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.EP_ID;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.EP_NAME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.EP_OBSERVATIONTYPE;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.EP_OBSERVEDAREA;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.EP_PHENOMENONTIME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.EP_PHENOMENONTIMEDS;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.EP_PROPERTIES;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.EP_RESULT;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.EP_RESULTTIMEDS;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.NAME_OBSERVATION;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.NAME_OBSERVATIONS;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.NAME_OBSERVEDPROPERTIES;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.NAME_OBSERVEDPROPERTY;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.NAME_SENSOR;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.NAME_THING;
import static de.fraunhofer.iosb.ilt.frostclient.utils.TypeReferencesHelper.TYPE_REFERENCE_LIST_STRING;
import static de.fraunhofer.iosb.ilt.frostclient.utils.TypeReferencesHelper.TYPE_REFERENCE_LIST_UOM;

import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.ext.TimeInterval;
import de.fraunhofer.iosb.ilt.frostclient.model.ext.TimeValue;
import de.fraunhofer.iosb.ilt.frostclient.model.ext.UnitOfMeasurement;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeSimpleSet;
import de.fraunhofer.iosb.ilt.frostclient.utils.Constants;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class SensorThingsMultiDatastreamV11 {

    public static final String NAME_MULTI_DATASTREAM = "MultiDatastream";
    public static final String NAME_MULTI_DATASTREAMS = "MultiDatastreams";
    public static final String NAME_MULTIOBSERVATIONDATATYPES = "multiObservationDataTypes";

    public static final EntityPropertyMain<List<String>> EP_MULTIOBSERVATIONDATATYPES = new EntityPropertyMain<>(NAME_MULTIOBSERVATIONDATATYPES, new TypeSimpleSet(EDM_STRING, TYPE_REFERENCE_LIST_STRING));
    public static final EntityPropertyMain<List<UnitOfMeasurement>> EP_UNITOFMEASUREMENTS = new EntityPropertyMain<>("unitOfMeasurements", new TypeSimpleSet(SensorThingsSensingV11.ept_Uom, TYPE_REFERENCE_LIST_UOM));

    public final NavigationPropertyEntity npObservationMultidatastream = new NavigationPropertyEntity(NAME_MULTI_DATASTREAM);
    public final NavigationPropertyEntitySet npMultidatastreamObservations = new NavigationPropertyEntitySet(NAME_OBSERVATIONS, npObservationMultidatastream);

    public final NavigationPropertyEntitySet npObspropMultidatastreams = new NavigationPropertyEntitySet(NAME_MULTI_DATASTREAMS);
    public final NavigationPropertyEntitySet npMultidatastreamObservedproperties = new NavigationPropertyEntitySet(NAME_OBSERVEDPROPERTIES, npObspropMultidatastreams);

    public final NavigationPropertyEntitySet npThingMultidatastreams = new NavigationPropertyEntitySet(NAME_MULTI_DATASTREAMS);
    public final NavigationPropertyEntity npMultidatastreamThing = new NavigationPropertyEntity(NAME_THING, npThingMultidatastreams);

    public final NavigationPropertyEntitySet npSensorMultidatastreams = new NavigationPropertyEntitySet(NAME_MULTI_DATASTREAMS);
    public final NavigationPropertyEntity npMultidatastreamSensor = new NavigationPropertyEntity(NAME_SENSOR, npSensorMultidatastreams);

    public final EntityType etMultiDatastream = new EntityType(NAME_MULTI_DATASTREAM, NAME_MULTI_DATASTREAMS);

    public final ModelRegistry mr;

    public SensorThingsMultiDatastreamV11(SensorThingsSensingV11 modelSensing) {
        this(modelSensing.getModelRegistry());
    }

    public SensorThingsMultiDatastreamV11(ModelRegistry mrSensing) {
        this.mr = mrSensing;
        mr.registerEntityType(etMultiDatastream);

        etMultiDatastream
                .registerProperty(EP_ID)
                .registerProperty(ModelRegistry.EP_SELFLINK)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_OBSERVATIONTYPE)
                .registerProperty(EP_MULTIOBSERVATIONDATATYPES)
                .registerProperty(EP_UNITOFMEASUREMENTS)
                .registerProperty(EP_OBSERVEDAREA)
                .registerProperty(EP_PHENOMENONTIMEDS)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(EP_RESULTTIMEDS)
                .registerProperty(npMultidatastreamObservedproperties)
                .registerProperty(npMultidatastreamSensor)
                .registerProperty(npMultidatastreamThing)
                .registerProperty(npMultidatastreamObservations);
        // Register multiDatastream on existing entities.
        mr.getEntityTypeForName(NAME_THING)
                .registerProperty(npThingMultidatastreams);
        mr.getEntityTypeForName(NAME_OBSERVEDPROPERTY)
                .registerProperty(npObspropMultidatastreams);
        mr.getEntityTypeForName(NAME_SENSOR)
                .registerProperty(npSensorMultidatastreams);
        mr.getEntityTypeForName(NAME_OBSERVATION)
                .registerProperty(npObservationMultidatastream);
    }

    public ModelRegistry getModelRegistry() {
        return mr;
    }

    public Entity newMultiDatastream() {
        return new Entity(etMultiDatastream);
    }

    public Entity newMultiDatastream(Object id) {
        return new Entity(etMultiDatastream)
                .setPrimaryKeyValues(id);
    }

    public Entity newMultiDatastream(String name, String description, UnitOfMeasurement... uoms) {
        List<String> obsTypes = new ArrayList<>();
        for (int i = 0; i < uoms.length; i++) {
            obsTypes.add(Constants.OM_MEASUREMENT);
        }
        return newMultiDatastream()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description)
                .setProperty(EP_OBSERVATIONTYPE, Constants.OM_COMPLEXOBSERVATION)
                .setProperty(EP_MULTIOBSERVATIONDATATYPES, obsTypes)
                .setProperty(EP_UNITOFMEASUREMENTS, Arrays.asList(uoms));
    }

    public Entity newObservation() {
        return new Entity(mr.getEntityTypeForName(SensorThingsSensingV11.NAME_OBSERVATION));
    }

    public Entity newObservation(Object result) {
        return newObservation()
                .setProperty(EP_RESULT, result);
    }

    public Entity newObservation(Object result, Entity datastream) {
        if (!etMultiDatastream.equals(datastream.getEntityType())) {
            throw new IllegalArgumentException("Datastream must have entityType Datastream, not " + datastream.getEntityType());
        }
        return newObservation()
                .setProperty(EP_RESULT, result)
                .setProperty(npObservationMultidatastream, datastream);
    }

    public Entity newObservation(Object result, ZonedDateTime phenomenonTime) {
        return newObservation(result)
                .setProperty(EP_PHENOMENONTIME, TimeValue.create(phenomenonTime));
    }

    public Entity newObservation(Object result, ZonedDateTime phenomenonTime, Entity datastream) {
        return newObservation(result, datastream)
                .setProperty(EP_PHENOMENONTIME, TimeValue.create(phenomenonTime));
    }

    public Entity newObservation(Object result, TimeInterval phenomenonTime) {
        return newObservation(result)
                .setProperty(EP_PHENOMENONTIME, new TimeValue(phenomenonTime));
    }

    public Entity newObservation(Object result, TimeInterval phenomenonTime, Entity datastream) {
        return newObservation(result, datastream)
                .setProperty(EP_PHENOMENONTIME, new TimeValue(phenomenonTime));
    }

}
