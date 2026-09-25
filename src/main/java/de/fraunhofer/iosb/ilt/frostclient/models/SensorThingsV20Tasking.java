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

import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_ENCODINGTYPE;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_FEATUREOFINTEREST;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_OBSERVEDPROPERTY;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_THING;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core.EP_METADATA;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core.NAMESPACE;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.Exceptions;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.PkValue;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeEnumeration;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.BuilderId;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.BuilderIdNameDefDesProp;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeValue;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.complex.DataRecord;
import de.fraunhofer.iosb.ilt.frostclient.query.Query;
import java.util.Map;
import net.time4j.Moment;

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
    public static final String NAME_EP_RUNLOG = "runLog";
    public static final String NAME_EP_STATUS = "status";
    public static final String NAME_EP_CREATIONTIME = SensorThingsV11Tasking.NAME_EP_CREATIONTIME;
    public static final String NAME_NP_ACTUATABLEPROPERTIES = "actuatableProperties";

    public static final TypeEnumeration PT_STATUS = new TypeEnumeration("TaskStatus", "Task Status Enumeration", Status.class);

    public static final EntityPropertyMain<TimeInstant> EP_CREATIONTIME = SensorThingsV11Tasking.EP_CREATIONTIME;
    public static final EntityPropertyMain<TimeValue> EP_RUNTIME = new EntityPropertyMain<>(NAME_EP_RUNTIME, TypeComplex.STA_TIMEVALUE);
    public static final EntityPropertyMain<MapValue> EP_RUNLOG = new EntityPropertyMain<>(NAME_EP_RUNLOG, TypeComplex.STA_MAP);
    public static final EntityPropertyMain<Status> EP_STATUS = new EntityPropertyMain<>(NAME_EP_STATUS, PT_STATUS);
    public static final EntityPropertyMain<DataRecord> EP_TASKINGPARAMETERS_TC = SensorThingsV11Tasking.EP_TASKINGPARAMETERS_TC;
    public static final EntityPropertyMain<MapValue> EP_TASKINGPARAMETERS_T = SensorThingsV11Tasking.EP_TASKINGPARAMETERS_T;

    public final NavigationPropertyEntity npTaskingcapActuator = new NavigationPropertyEntity(NAME_ACTUATOR);
    public final NavigationPropertyEntity npTaskingcapThing = new NavigationPropertyEntity(NAME_THING);
    public final NavigationPropertyEntitySet npTaskingcapUltimateFeatures = new NavigationPropertyEntitySet(SensorThingsV20Core.NAME_NP_ULTIMATEFOIS);
    public final NavigationPropertyEntitySet npTaskingcapActuatableProperties = new NavigationPropertyEntitySet(NAME_NP_ACTUATABLEPROPERTIES);
    public final NavigationPropertyEntitySet npTaskingcapTasks = new NavigationPropertyEntitySet(NAME_TASKS);

    public final NavigationPropertyEntity npTaskTaskingcapability = new NavigationPropertyEntity(NAME_TASKING_CAPABILITY, npTaskingcapTasks);
    public final NavigationPropertyEntity npTaskProximateFeature = new NavigationPropertyEntity(SensorThingsV20Core.NAME_NP_PROXIMATEFOI);

    public final NavigationPropertyEntitySet npActuatorTaskingcaps = new NavigationPropertyEntitySet(NAME_TASKING_CAPABILITIES, npTaskingcapActuator);
    public final NavigationPropertyEntitySet npFeatureTasks = new NavigationPropertyEntitySet(NAME_TASKS, npTaskProximateFeature);
    public final NavigationPropertyEntitySet npFeatureTaskingcaps = new NavigationPropertyEntitySet(NAME_TASKING_CAPABILITIES, npTaskingcapUltimateFeatures);
    public final NavigationPropertyEntitySet npObspropTaskingcaps = new NavigationPropertyEntitySet(NAME_TASKING_CAPABILITIES, npTaskingcapActuatableProperties);
    public final NavigationPropertyEntitySet npThingTaskingcapabilities = new NavigationPropertyEntitySet(NAME_TASKING_CAPABILITIES, npTaskingcapThing);

    public final EntityType etActuator = new EntityType(NAME_ACTUATOR, NAME_ACTUATORS).setNamespace(NAMESPACE);
    public final EntityType etTask = new EntityType(NAME_TASK, NAME_TASKS).setNamespace(NAMESPACE);
    public final EntityType etTaskingCapability = new EntityType(NAME_TASKING_CAPABILITY, NAME_TASKING_CAPABILITIES).setNamespace(NAMESPACE);

    private ModelRegistry mr;

    public SensorThingsV20Tasking() {
    }

    @Override
    public final void init(SensorThingsService service, ModelRegistry modelRegistry) {
        if (this.mr != null) {
            throw new IllegalArgumentException("Already initialised.");
        }
        this.mr = modelRegistry;
        mr.addDataModel(this)
                .registerPropertyType(PT_STATUS.setNamespace(NAMESPACE))
                .registerEntityType(etActuator)
                .registerEntityType(etTask)
                .registerEntityType(etTaskingCapability);

        etActuator
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(CommonProperties.EP_NAME)
                .registerProperty(CommonProperties.EP_DEFINITION)
                .registerProperty(CommonProperties.EP_DESCRIPTION)
                .registerProperty(CommonProperties.EP_ENCODINGTYPE)
                .registerProperty(SensorThingsV11Sensing.EP_METADATA)
                .registerProperty(CommonProperties.EP_PROPERTIES)
                .registerProperty(npActuatorTaskingcaps);

        etTask
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(EP_CREATIONTIME)
                .registerProperty(EP_RUNTIME)
                .registerProperty(EP_RUNLOG)
                .registerProperty(EP_STATUS)
                .registerProperty(EP_TASKINGPARAMETERS_T)
                .registerProperty(npTaskProximateFeature)
                .registerProperty(npTaskTaskingcapability);

        etTaskingCapability
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(CommonProperties.EP_NAME)
                .registerProperty(CommonProperties.EP_DEFINITION)
                .registerProperty(CommonProperties.EP_DESCRIPTION)
                .registerProperty(CommonProperties.EP_PROPERTIES)
                .registerProperty(EP_TASKINGPARAMETERS_TC)
                .registerProperty(npTaskingcapActuatableProperties)
                .registerProperty(npTaskingcapActuator)
                .registerProperty(npTaskingcapTasks)
                .registerProperty(npTaskingcapThing)
                .registerProperty(npTaskingcapUltimateFeatures);

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

    public ActuatorBuilder buildActuator() {
        return new ActuatorBuilder(this);
    }

    public ActuatorBuilder editActuator(Entity entity) {
        return new ActuatorBuilder(this, entity);
    }

    public TaskBuilder buildTask() {
        return new TaskBuilder(this);
    }

    public TaskBuilder editTask(Entity entity) {
        return new TaskBuilder(this, entity);
    }

    public TaskingCapabilityBuilder buildTaskingCapability() {
        return new TaskingCapabilityBuilder(this);
    }

    public TaskingCapabilityBuilder editTaskingCapability(Entity entity) {
        return new TaskingCapabilityBuilder(this, entity);
    }

    public ThingExtensionBuilder thingExtender() {
        return new ThingExtensionBuilder(this);
    }

    public static class ActuatorBuilder extends BuilderIdNameDefDesProp<ActuatorBuilder> {

        SensorThingsV20Tasking mdlTsk;

        public ActuatorBuilder(SensorThingsV20Tasking mdlTsk) {
            super(new Entity(mdlTsk.etActuator));
            this.mdlTsk = mdlTsk;
        }

        public ActuatorBuilder(SensorThingsV20Tasking mdlCore, Entity entity) {
            super(entity);
            this.mdlTsk = mdlCore;
        }

        public String getEncodingType() {
            return entity.getProperty(EP_ENCODINGTYPE);
        }

        public ActuatorBuilder setEncodingType(String encodingType) {
            entity.setProperty(EP_ENCODINGTYPE, encodingType);
            return getThis();
        }

        public Object getMetadata() {
            return entity.getProperty(EP_METADATA);
        }

        public ActuatorBuilder setMetadata(Object metadata) {
            entity.setProperty(EP_METADATA, metadata);
            return getThis();
        }

        public Query queryTaskingCapabilities() {
            return entity.query(mdlTsk.npActuatorTaskingcaps);
        }

        public EntitySet getTaskingCapabilities() {
            return entity.getProperty(mdlTsk.npActuatorTaskingcaps);
        }

        public ActuatorBuilder addTaskingCapability(Entity ds) {
            entity.addNavigationEntity(mdlTsk.npActuatorTaskingcaps, ds);
            return getThis();
        }
    }

    public static class TaskBuilder extends BuilderId<TaskBuilder> {

        SensorThingsV20Tasking mdlTsk;

        public TaskBuilder(SensorThingsV20Tasking mdlTsk) {
            super(new Entity(mdlTsk.etTask));
            this.mdlTsk = mdlTsk;
        }

        public TaskBuilder(SensorThingsV20Tasking mdlCore, Entity entity) {
            super(entity);
            this.mdlTsk = mdlCore;
        }

        public TimeInstant getCreationTime() {
            return entity.getProperty(EP_CREATIONTIME);
        }

        public TaskBuilder setCreationTime(TimeInstant cTime) {
            entity.setProperty(EP_CREATIONTIME, cTime);
            return getThis();
        }

        public TaskBuilder setCreationTime(Moment cTime) {
            return setCreationTime(TimeInstant.create(cTime));
        }

        public TimeValue getRunTime() {
            return entity.getProperty(EP_RUNTIME);
        }

        public TaskBuilder setRunTime(TimeValue phenTime) {
            entity.setProperty(EP_RUNTIME, phenTime);
            return getThis();
        }

        public TaskBuilder setRunTimeStart(Moment start) {
            TimeValue time = entity.getProperty(EP_RUNTIME);
            if (time == null) {
                time = TimeValue.create(start);
                entity.setProperty(EP_RUNTIME, time);
            } else {
                time.setProperty(TimeValue.EP_START_TIME, TimeInstant.create(start));
            }
            return getThis();
        }

        public TaskBuilder setRunTimeEnd(Moment end) {
            TimeValue time = entity.getProperty(EP_RUNTIME);
            Exceptions.illegalArgumentIf(time == null, "Set the start time first.");
            time.setProperty(TimeValue.EP_END_TIME, TimeInstant.create(end));
            return getThis();
        }

        public MapValue getRunLog() {
            return entity.getProperty(EP_RUNLOG);
        }

        public TaskBuilder setRunLog(MapValue runLog) {
            entity.setProperty(EP_RUNLOG, runLog);
            return getThis();
        }

        public Status getStatus() {
            return entity.getProperty(EP_STATUS);
        }

        public TaskBuilder setStatus(Status status) {
            entity.setProperty(EP_STATUS, status);
            return getThis();
        }

        public MapValue getTaskingParameters() {
            return entity.getProperty(EP_TASKINGPARAMETERS_T);
        }

        public TaskBuilder setTaskingParameters(MapValue tp) {
            entity.setProperty(EP_TASKINGPARAMETERS_T, tp);
            return getThis();
        }

        public Entity getTaskingCapability() throws ServiceFailureException {
            return entity.getProperty(mdlTsk.npTaskTaskingcapability);
        }

        public TaskBuilder setTaskingCapability(Entity tc) {
            entity.setProperty(mdlTsk.npTaskTaskingcapability, tc);
            return getThis();
        }

        public Entity getProximateFeature() throws ServiceFailureException {
            return entity.getProperty(mdlTsk.npTaskProximateFeature);
        }

        public TaskBuilder setProximateFeature(Entity feature) {
            entity.setProperty(mdlTsk.npTaskProximateFeature, feature);
            return getThis();
        }
    }

    public static class TaskingCapabilityBuilder extends BuilderIdNameDefDesProp<TaskingCapabilityBuilder> {

        SensorThingsV20Tasking mdlTsk;

        public TaskingCapabilityBuilder(SensorThingsV20Tasking mdlTsk) {
            super(new Entity(mdlTsk.etTask));
            this.mdlTsk = mdlTsk;
        }

        public TaskingCapabilityBuilder(SensorThingsV20Tasking mdlCore, Entity entity) {
            super(entity);
            this.mdlTsk = mdlCore;
        }

        public DataRecord getTaskingParameters() {
            return entity.getProperty(EP_TASKINGPARAMETERS_TC);
        }

        public TaskingCapabilityBuilder setTaskingParameters(DataRecord dr) {
            entity.setProperty(EP_TASKINGPARAMETERS_TC, dr);
            return getThis();
        }

        public Query queryActuatableProperties() {
            return entity.query(mdlTsk.npTaskingcapActuatableProperties);
        }

        public EntitySet getActuatableProperties() {
            return entity.getProperty(mdlTsk.npTaskingcapActuatableProperties);
        }

        public TaskingCapabilityBuilder addActuatableProperty(Entity prop) {
            entity.addNavigationEntity(mdlTsk.npTaskingcapActuatableProperties, prop);
            return getThis();
        }

        public Query queryTasks() {
            return entity.query(mdlTsk.npTaskingcapTasks);
        }

        public EntitySet getTasks() {
            return entity.getProperty(mdlTsk.npTaskingcapTasks);
        }

        public TaskingCapabilityBuilder addTask(Entity task) {
            entity.addNavigationEntity(mdlTsk.npTaskingcapTasks, task);
            return getThis();
        }

        public Query queryUltimateFeatures() {
            return entity.query(mdlTsk.npTaskingcapUltimateFeatures);
        }

        public EntitySet getUltimateFeatures() {
            return entity.getProperty(mdlTsk.npTaskingcapUltimateFeatures);
        }

        public TaskingCapabilityBuilder addUltimateFeature(Entity feature) {
            entity.addNavigationEntity(mdlTsk.npTaskingcapUltimateFeatures, feature);
            return getThis();
        }

        public Entity getActuator() throws ServiceFailureException {
            return entity.getProperty(mdlTsk.npTaskingcapActuator);
        }

        public TaskingCapabilityBuilder setActuator(Entity actuator) {
            entity.setProperty(mdlTsk.npTaskingcapActuator, actuator);
            return getThis();
        }

        public Entity getThing() throws ServiceFailureException {
            return entity.getProperty(mdlTsk.npTaskingcapThing);
        }

        public TaskingCapabilityBuilder setThing(Entity thing) {
            entity.setProperty(mdlTsk.npTaskingcapThing, thing);
            return getThis();
        }
    }

    public static class ThingExtensionBuilder extends CommonProperties.Builder<ThingExtensionBuilder> {

        SensorThingsV20Tasking mdlTsk;

        public ThingExtensionBuilder(SensorThingsV20Tasking mdlTsk) {
            this.mdlTsk = mdlTsk;
        }

        public Query queryTaskingcapabilities(Entity tc) {
            return entity.query(mdlTsk.npThingTaskingcapabilities);
        }

        public EntitySet getTaskingcapabilities(Entity tc) {
            return entity.getProperty(mdlTsk.npThingTaskingcapabilities);
        }

        public ThingExtensionBuilder addTaskingcapability(Entity tc) {
            entity.addNavigationEntity(mdlTsk.npThingTaskingcapabilities, tc);
            return getThis();
        }
    }

    @Deprecated
    public Entity newTaskingCapability() {
        return new Entity(etTaskingCapability);
    }

    @Deprecated
    public Entity newTaskingCapability(Object id) {
        return new Entity(etTaskingCapability)
                .setPrimaryKeyValues(PkValue.of(id));
    }

    @Deprecated
    public Entity newTaskingCapability(String name, String description) {
        return newTaskingCapability()
                .setProperty(CommonProperties.EP_NAME, name)
                .setProperty(CommonProperties.EP_DESCRIPTION, description);
    }

    @Deprecated
    public Entity newTaskingCapability(String name, String description, Map<String, Object> properties) {
        return newTaskingCapability(name, description, new MapValue(TypeComplex.STA_MAP, properties));
    }

    @Deprecated
    public Entity newTaskingCapability(String name, String description, MapValue properties) {
        return newTaskingCapability(name, description)
                .setProperty(CommonProperties.EP_PROPERTIES, properties);
    }

    public static SensorThingsV11Tasking.TaskingParametersBuilder taskingParametersBuilder() {
        return SensorThingsV11Tasking.taskingParametersBuilder();
    }

}
