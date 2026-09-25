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
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_ENCODINGTYPE;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_THING;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_METADATA;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
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
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.BuilderId;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.BuilderIdNameDefDesProp;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.AbstractDataComponent;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.complex.DataRecord;
import de.fraunhofer.iosb.ilt.frostclient.query.Query;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import java.util.Map;
import net.time4j.Moment;
import tools.jackson.core.type.TypeReference;

/**
 * The Data Model implements the SensorThings Tasking extension.
 */
public class SensorThingsV11Tasking implements DataModel {

    public static final String NAME_ACTUATOR = "Actuator";
    public static final String NAME_ACTUATORS = "Actuators";
    public static final String NAME_TASK = "Task";
    public static final String NAME_TASKS = "Tasks";
    public static final String NAME_TASKING_CAPABILITY = "TaskingCapability";
    public static final String NAME_TASKING_CAPABILITIES = "TaskingCapabilities";

    public static final String NAME_EP_TASKINGPARAMETERS = "taskingParameters";
    public static final String NAME_EP_CREATIONTIME = "creationTime";

    public static final EntityPropertyMain<TimeInstant> EP_CREATIONTIME = new EntityPropertyMain<>(NAME_EP_CREATIONTIME, EDM_DATETIMEOFFSET);

    public static final TypeReference<DataRecord> TYPE_REFERENCE_DATARECORD = new TypeReference<DataRecord>() {
        // Empty on purpose.
    };
    public static final TypeComplex PT_DATA_RECORD = new TypeComplex("DataRecord", "A DataRecord", true,
            null,
            pt -> ParserUtils.getDataRecordDeserializer(),
            pt -> ParserUtils.getDefaultSerializer());
    public static final EntityPropertyMain<DataRecord> EP_TASKINGPARAMETERS_TC = new EntityPropertyMain<>(NAME_EP_TASKINGPARAMETERS, PT_DATA_RECORD);
    public static final EntityPropertyMain<MapValue> EP_TASKINGPARAMETERS_T = new EntityPropertyMain<>(NAME_EP_TASKINGPARAMETERS, TypeComplex.STA_MAP);

    public final NavigationPropertyEntity npTaskcapActuator = new NavigationPropertyEntity(NAME_ACTUATOR);
    public final NavigationPropertyEntity npTaskcapThing = new NavigationPropertyEntity(NAME_THING);
    public final NavigationPropertyEntitySet npTaskcapTasks = new NavigationPropertyEntitySet(NAME_TASKS);

    public final NavigationPropertyEntity npTaskTaskingcapability = new NavigationPropertyEntity(NAME_TASKING_CAPABILITY, npTaskcapTasks);
    public final NavigationPropertyEntitySet npActuatorTaskingcapabilities = new NavigationPropertyEntitySet(NAME_TASKING_CAPABILITIES, npTaskcapActuator);
    public final NavigationPropertyEntitySet npThingTaskingcapabilities = new NavigationPropertyEntitySet(NAME_TASKING_CAPABILITIES, npTaskcapThing);

    public final EntityType etActuator = new EntityType(NAME_ACTUATOR, NAME_ACTUATORS);
    public final EntityType etTask = new EntityType(NAME_TASK, NAME_TASKS);
    public final EntityType etTaskingCapability = new EntityType(NAME_TASKING_CAPABILITY, NAME_TASKING_CAPABILITIES);

    private ModelRegistry mr;

    public SensorThingsV11Tasking() {
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
                .registerProperty(CommonProperties.EP_ENCODINGTYPE)
                .registerProperty(SensorThingsV11Sensing.EP_METADATA)
                .registerProperty(CommonProperties.EP_PROPERTIES)
                .registerProperty(npActuatorTaskingcapabilities);

        etTask
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(EP_CREATIONTIME)
                .registerProperty(EP_TASKINGPARAMETERS_T)
                .registerProperty(npTaskTaskingcapability);

        etTaskingCapability
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(CommonProperties.EP_NAME)
                .registerProperty(CommonProperties.EP_DESCRIPTION)
                .registerProperty(CommonProperties.EP_PROPERTIES)
                .registerProperty(EP_TASKINGPARAMETERS_TC)
                .registerProperty(npTaskcapActuator)
                .registerProperty(npTaskcapTasks)
                .registerProperty(npTaskcapThing);

        mr.getEntityTypeForName(NAME_THING).registerProperty(npThingTaskingcapabilities);
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

        SensorThingsV11Tasking mdlTsk;

        public ActuatorBuilder(SensorThingsV11Tasking mdlTsk) {
            super(new Entity(mdlTsk.etActuator));
            this.mdlTsk = mdlTsk;
        }

        public ActuatorBuilder(SensorThingsV11Tasking mdlCore, Entity entity) {
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
            return entity.query(mdlTsk.npActuatorTaskingcapabilities);
        }

        public EntitySet getTaskingCapabilities() {
            return entity.getProperty(mdlTsk.npActuatorTaskingcapabilities);
        }

        public ActuatorBuilder addTaskingCapability(Entity ds) {
            entity.addNavigationEntity(mdlTsk.npActuatorTaskingcapabilities, ds);
            return getThis();
        }
    }

    public static class TaskBuilder extends BuilderId<TaskBuilder> {

        SensorThingsV11Tasking mdlTsk;

        public TaskBuilder(SensorThingsV11Tasking mdlTsk) {
            super(new Entity(mdlTsk.etTask));
            this.mdlTsk = mdlTsk;
        }

        public TaskBuilder(SensorThingsV11Tasking mdlCore, Entity entity) {
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
    }

    public static class TaskingCapabilityBuilder extends BuilderIdNameDefDesProp<TaskingCapabilityBuilder> {

        SensorThingsV11Tasking mdlTsk;

        public TaskingCapabilityBuilder(SensorThingsV11Tasking mdlTsk) {
            super(new Entity(mdlTsk.etTask));
            this.mdlTsk = mdlTsk;
        }

        public TaskingCapabilityBuilder(SensorThingsV11Tasking mdlCore, Entity entity) {
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

        public Query queryTasks() {
            return entity.query(mdlTsk.npTaskcapTasks);
        }

        public EntitySet getTasks() {
            return entity.getProperty(mdlTsk.npTaskcapTasks);
        }

        public TaskingCapabilityBuilder addTask(Entity task) {
            entity.addNavigationEntity(mdlTsk.npTaskcapTasks, task);
            return getThis();
        }

        public Entity getActuator() throws ServiceFailureException {
            return entity.getProperty(mdlTsk.npTaskcapActuator);
        }

        public TaskingCapabilityBuilder setActuator(Entity actuator) {
            entity.setProperty(mdlTsk.npTaskcapActuator, actuator);
            return getThis();
        }

        public Entity getThing() throws ServiceFailureException {
            return entity.getProperty(mdlTsk.npTaskcapThing);
        }

        public TaskingCapabilityBuilder setThing(Entity thing) {
            entity.setProperty(mdlTsk.npTaskcapThing, thing);
            return getThis();
        }
    }

    public static class ThingExtensionBuilder extends CommonProperties.Builder<ThingExtensionBuilder> {

        SensorThingsV11Tasking mdlTsk;

        public ThingExtensionBuilder(SensorThingsV11Tasking mdlTsk) {
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
