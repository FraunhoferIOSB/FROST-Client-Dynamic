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

import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_FEATUREOFINTEREST;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_OBSERVEDPROPERTY;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_THING;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.PkValue;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeEnumeration;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeValue;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.AbstractDataComponent;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.complex.DataRecord;
import java.util.Map;

/**
 * The Data Model implements the SensorThings V2.0 Tasking extension.
 */
public class SensorThingsV20Tasking implements DataModel {

    public static enum Status {
        CREATED("Created"),
        RUNNING("Running"),
        COMPLETED("Completed"),
        REJECTED("Rejected"),
        FAILED("Failed");

        public final String name;

        private Status(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return getName();
        }

    }

    public static final String NAME_ACTUATOR = SensorThingsV11Tasking.NAME_ACTUATOR;
    public static final String NAME_ACTUATORS = SensorThingsV11Tasking.NAME_ACTUATORS;
    public static final String NAME_TASK = SensorThingsV11Tasking.NAME_TASK;
    public static final String NAME_TASKS = SensorThingsV11Tasking.NAME_TASKS;
    public static final String NAME_TASKING_CAPABILITY = SensorThingsV11Tasking.NAME_TASKING_CAPABILITY;
    public static final String NAME_TASKING_CAPABILITIES = SensorThingsV11Tasking.NAME_TASKING_CAPABILITIES;

    public static final String NAME_EP_TASKINGPARAMETERS = SensorThingsV11Tasking.NAME_EP_TASKINGPARAMETERS;
    public static final String NAME_EP_RUNTIME = "runTime";
    public static final String NAME_EP_STATUS = "status";
    public static final String NAME_EP_CREATIONTIME = SensorThingsV11Tasking.NAME_EP_CREATIONTIME;
    public static final String NAME_NP_ACTUATABLEPROPERTIES = "actuatableProperties";

    public static final TypeEnumeration PT_STATUS = new TypeEnumeration("TaskStatus", "Task Status Enumeration", Status.class);

    public static final EntityPropertyMain<TimeInstant> EP_CREATIONTIME = SensorThingsV11Tasking.EP_CREATIONTIME;
    public static final EntityPropertyMain<TimeValue> EP_RUNTIME = new EntityPropertyMain<>(NAME_EP_RUNTIME, TypeComplex.STA_TIMEVALUE);
    public static final EntityPropertyMain<Status> EP_STATUS = new EntityPropertyMain<>(NAME_EP_STATUS, PT_STATUS);
    public static final EntityPropertyMain<DataRecord> EP_TASKINGPARAMETERS_TC = SensorThingsV11Tasking.EP_TASKINGPARAMETERS_TC;
    public static final EntityPropertyMain<MapValue> EP_TASKINGPARAMETERS_T = SensorThingsV11Tasking.EP_TASKINGPARAMETERS_T;

    public final NavigationPropertyEntity npTaskingcapActuator = new NavigationPropertyEntity(NAME_ACTUATOR);
    public final NavigationPropertyEntity npTaskingcapThing = new NavigationPropertyEntity(NAME_THING);
    public final NavigationPropertyEntity npTaskingcapUltimateFeature = new NavigationPropertyEntity(SensorThingsV20Core.NAME_NP_ULTIMATEFOI);
    public final NavigationPropertyEntitySet npTaskingcapActuatableProperties = new NavigationPropertyEntitySet(NAME_NP_ACTUATABLEPROPERTIES);
    public final NavigationPropertyEntitySet npTaskingcapTasks = new NavigationPropertyEntitySet(NAME_TASKS);

    public final NavigationPropertyEntity npTaskTaskingcapability = new NavigationPropertyEntity(NAME_TASKING_CAPABILITY, npTaskingcapTasks);
    public final NavigationPropertyEntity npTaskProximateFeature = new NavigationPropertyEntity(SensorThingsV20Core.NAME_NP_PROXIMATEFOI);

    public final NavigationPropertyEntitySet npActuatorTaskingcaps = new NavigationPropertyEntitySet(NAME_TASKING_CAPABILITIES, npTaskingcapActuator);
    public final NavigationPropertyEntitySet npFeatureTasks = new NavigationPropertyEntitySet(NAME_TASKS, npTaskProximateFeature);
    public final NavigationPropertyEntitySet npFeatureTaskingcaps = new NavigationPropertyEntitySet(NAME_TASKING_CAPABILITIES, npTaskingcapUltimateFeature);
    public final NavigationPropertyEntitySet npObspropTaskingcaps = new NavigationPropertyEntitySet(NAME_TASKING_CAPABILITIES, npTaskingcapActuatableProperties);
    public final NavigationPropertyEntitySet npThingTaskingcapabilities = new NavigationPropertyEntitySet(NAME_TASKING_CAPABILITIES, npTaskingcapThing);

    public final EntityType etActuator = new EntityType(NAME_ACTUATOR, NAME_ACTUATORS);
    public final EntityType etTask = new EntityType(NAME_TASK, NAME_TASKS);
    public final EntityType etTaskingCapability = new EntityType(NAME_TASKING_CAPABILITY, NAME_TASKING_CAPABILITIES);

    private ModelRegistry mr;

    public SensorThingsV20Tasking() {
    }

    @Override
    public final void init(SensorThingsService service, ModelRegistry modelRegistry) {
        if (this.mr != null) {
            throw new IllegalArgumentException("Already initialised.");
        }
        this.mr = modelRegistry;
        mr.addDataModel(this);

        mr.registerEntityType(etActuator);
        mr.registerEntityType(etTask);
        mr.registerEntityType(etTaskingCapability);

        etActuator
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(CommonProperties.EP_NAME)
                .registerProperty(CommonProperties.EP_DESCRIPTION)
                .registerProperty(SensorThingsV11Sensing.EP_ENCODINGTYPE)
                .registerProperty(SensorThingsV11Sensing.EP_METADATA)
                .registerProperty(CommonProperties.EP_PROPERTIES)
                .registerProperty(npActuatorTaskingcaps);

        etTask
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(EP_CREATIONTIME)
                .registerProperty(EP_RUNTIME)
                .registerProperty(EP_STATUS)
                .registerProperty(EP_TASKINGPARAMETERS_T)
                .registerProperty(npTaskProximateFeature)
                .registerProperty(npTaskTaskingcapability);

        etTaskingCapability
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(CommonProperties.EP_NAME)
                .registerProperty(CommonProperties.EP_DESCRIPTION)
                .registerProperty(CommonProperties.EP_PROPERTIES)
                .registerProperty(EP_TASKINGPARAMETERS_TC)
                .registerProperty(npTaskingcapActuatableProperties)
                .registerProperty(npTaskingcapActuator)
                .registerProperty(npTaskingcapTasks)
                .registerProperty(npTaskingcapThing)
                .registerProperty(npTaskingcapUltimateFeature);

        mr.getEntityTypeForName(NAME_THING)
                .registerProperty(npThingTaskingcapabilities);
        mr.getEntityTypeForName(NAME_OBSERVEDPROPERTY)
                .registerProperty(npObspropTaskingcaps);
        mr.getEntityTypeForName(NAME_FEATUREOFINTEREST)
                .registerProperty(npFeatureTaskingcaps)
                .registerProperty(npFeatureTasks);
    }

    @Override
    public boolean isInitialised() {
        return mr != null;
    }

    public ModelRegistry getModelRegistry() {
        return mr;
    }

    public Entity newTaskingCapability() {
        return new Entity(etTaskingCapability);
    }

    public Entity newTaskingCapability(Object id) {
        return new Entity(etTaskingCapability)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    public Entity newTaskingCapability(String name, String description) {
        return newTaskingCapability()
                .setProperty(CommonProperties.EP_NAME, name)
                .setProperty(CommonProperties.EP_DESCRIPTION, description);
    }

    public Entity newTaskingCapability(String name, String description, Map<String, Object> properties) {
        return newTaskingCapability(name, description, new MapValue(properties));
    }

    public Entity newTaskingCapability(String name, String description, MapValue properties) {
        return newTaskingCapability(name, description)
                .setProperty(CommonProperties.EP_PROPERTIES, properties);
    }

    public static TaskingParametersBuilder taskingParametersBuilder() {
        return new TaskingParametersBuilder();
    }

    public static class TaskingParametersBuilder {

        private final DataRecord taskingParameters = new DataRecord();

        public TaskingParametersBuilder taskingParameter(AbstractDataComponent field) {
            taskingParameters.getFields().add(field);
            return this;
        }

        public TaskingParametersBuilder taskingParameter(String name, AbstractDataComponent taskingParameter) {
            if (!name.equals(taskingParameter.getName())) {
                taskingParameter.setName(name);
            }
            return taskingParameter(taskingParameter);
        }

        public DataRecord build() {
            return taskingParameters;
        }
    }

}
