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
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_DATASTREAMS;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_OBSERVEDPROPERTIES;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_OBSERVEDPROPERTY;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_SENSOR;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_SENSORS;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_THING;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_THINGS;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core.NAMESPACE;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.Exceptions;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.Builder;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.BuilderId;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.BuilderIdNameDefDesProp;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeValue;
import net.time4j.Moment;

/**
 * The Data Model implements the SensorThings V2.0 Tasking extension.
 */
public class SensorThingsV20Om implements DataModel {

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

    public static final String NAME_DEPLOYMENT = "Deployment";
    public static final String NAME_DEPLOYMENTS = "Deployments";
    public static final String NAME_LINKING_TIME = "LinkingTime";
    public static final String NAME_LINKING_TIMES = "LinkingTimes";
    public static final String NAME_MONITORING_ACTIVITY = "MonitoringActivity";
    public static final String NAME_MONITORING_ACTIVITIES = "MonitoringActivities";
    public static final String NAME_MONITORING_NETWORK = "MonitoringNetwork";
    public static final String NAME_MONITORING_NETWORKS = "MonitoringNetworks";
    public static final String NAME_MONITORING_PROGRAM = "MonitoringProgram";
    public static final String NAME_MONITORING_PROGRAMS = "MonitoringPrograms";
    public static final String NAME_OBSERVING_PROCEDURE = "ObservingProcedure";
    public static final String NAME_OBSERVING_PROCEDURES = "ObservingProcedures";

    public static final String NAME_EP_POSITION = "position";
    public static final String NAME_EP_REASON = "reason";
    public static final String NAME_EP_TIME = "time";

    public static final EntityPropertyMain<Object> EP_POSITION = new EntityPropertyMain<>(NAME_EP_POSITION, TypePrimitive.EDM_GEOMETRY);
    public static final EntityPropertyMain<String> EP_REASON = new EntityPropertyMain<>(NAME_EP_REASON, EDM_STRING);
    public static final EntityPropertyMain<TimeValue> EP_TIME = new EntityPropertyMain<>(NAME_EP_TIME, TypeComplex.STA_TIMEVALUE);

    public final NavigationPropertyEntitySet npDatastreamDeployments = new NavigationPropertyEntitySet(NAME_DEPLOYMENTS);
    public final NavigationPropertyEntitySet npDatastreamObservingprocedure = new NavigationPropertyEntitySet(NAME_OBSERVING_PROCEDURE);

    public final NavigationPropertyEntitySet npDeploymentDatastreams = new NavigationPropertyEntitySet(NAME_DATASTREAMS, npDatastreamDeployments);
    public final NavigationPropertyEntity npDeploymentSensor = new NavigationPropertyEntity(NAME_SENSOR);
    public final NavigationPropertyEntity npDeploymentThing = new NavigationPropertyEntity(NAME_THING);

    public final NavigationPropertyEntity npLinkingtimeThing = new NavigationPropertyEntity(NAME_THING);
    public final NavigationPropertyEntity npLinkingtimeNetwork = new NavigationPropertyEntity(NAME_MONITORING_NETWORK);

    public final NavigationPropertyEntitySet npMonitoringactivityPrograms = new NavigationPropertyEntitySet(NAME_MONITORING_PROGRAMS);
    public final NavigationPropertyEntitySet npMonitoringactivityNetworks = new NavigationPropertyEntitySet(NAME_MONITORING_NETWORKS);

    public final NavigationPropertyEntitySet npMonitoringnetworkActivities = new NavigationPropertyEntitySet(NAME_MONITORING_ACTIVITIES, npMonitoringactivityNetworks);
    public final NavigationPropertyEntitySet npMonitoringnetworkLinkingtimes = new NavigationPropertyEntitySet(NAME_LINKING_TIMES, npLinkingtimeNetwork);
    public final NavigationPropertyEntitySet npMonitoringnetworkThings = new NavigationPropertyEntitySet(NAME_THINGS);

    public final NavigationPropertyEntitySet npMonitoringprogramActivities = new NavigationPropertyEntitySet(NAME_MONITORING_ACTIVITIES, npMonitoringactivityPrograms);

    public final NavigationPropertyEntitySet npObservedpropertyObservingprocedure = new NavigationPropertyEntitySet(NAME_OBSERVING_PROCEDURES);

    public final NavigationPropertyEntitySet npObservingprocedureDatastreams = new NavigationPropertyEntitySet(NAME_DATASTREAMS, npDatastreamObservingprocedure);
    public final NavigationPropertyEntitySet npObservingprocedureObservedproperties = new NavigationPropertyEntitySet(NAME_OBSERVEDPROPERTIES, npObservedpropertyObservingprocedure);
    public final NavigationPropertyEntitySet npObservingprocedureSensors = new NavigationPropertyEntitySet(NAME_SENSORS);

    public final NavigationPropertyEntitySet npSensorDeployments = new NavigationPropertyEntitySet(NAME_DEPLOYMENTS, npDeploymentSensor);
    public final NavigationPropertyEntitySet npSensorObservingprocedures = new NavigationPropertyEntitySet(NAME_OBSERVING_PROCEDURES, npObservingprocedureSensors);

    public final NavigationPropertyEntitySet npThingDeployments = new NavigationPropertyEntitySet(NAME_DEPLOYMENTS, npDeploymentThing);
    public final NavigationPropertyEntitySet npThingLinkingTimes = new NavigationPropertyEntitySet(NAME_LINKING_TIMES, npLinkingtimeThing);
    public final NavigationPropertyEntitySet npThingMonitoringnetworks = new NavigationPropertyEntitySet(NAME_MONITORING_NETWORKS, npMonitoringnetworkThings);

    public final EntityType etDeployment = new EntityType(NAME_DEPLOYMENT, NAME_DEPLOYMENTS).setNamespace(NAMESPACE);
    public final EntityType etLinkingTime = new EntityType(NAME_LINKING_TIME, NAME_LINKING_TIMES).setNamespace(NAMESPACE);
    public final EntityType etMonitoringActivity = new EntityType(NAME_MONITORING_ACTIVITY, NAME_MONITORING_ACTIVITIES).setNamespace(NAMESPACE);
    public final EntityType etMonitoringNetwork = new EntityType(NAME_MONITORING_NETWORK, NAME_MONITORING_NETWORKS).setNamespace(NAMESPACE);
    public final EntityType etMonitoringProgram = new EntityType(NAME_MONITORING_PROGRAM, NAME_MONITORING_PROGRAMS).setNamespace(NAMESPACE);
    public final EntityType etObservingProcedure = new EntityType(NAME_OBSERVING_PROCEDURE, NAME_OBSERVING_PROCEDURES).setNamespace(NAMESPACE);

    private ModelRegistry mr;

    public SensorThingsV20Om() {
    }

    @Override
    public final void init(SensorThingsService service, ModelRegistry modelRegistry) {
        if (this.mr != null) {
            throw new IllegalArgumentException("Already initialised.");
        }
        this.mr = modelRegistry;
        mr.addDataModel(this)
                .registerEntityType(etDeployment)
                .registerEntityType(etLinkingTime)
                .registerEntityType(etMonitoringActivity)
                .registerEntityType(etMonitoringNetwork)
                .registerEntityType(etMonitoringProgram)
                .registerEntityType(etObservingProcedure);

        etDeployment
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(CommonProperties.EP_NAME)
                .registerProperty(CommonProperties.EP_DEFINITION)
                .registerProperty(CommonProperties.EP_DESCRIPTION)
                .registerProperty(CommonProperties.EP_PROPERTIES)
                .registerProperty(EP_REASON)
                .registerProperty(CommonProperties.EP_ENCODINGTYPE)
                .registerProperty(EP_POSITION)
                .registerProperty(EP_TIME)
                .registerProperty(npDeploymentDatastreams)
                .registerProperty(npDeploymentSensor)
                .registerProperty(npDeploymentThing);

        etLinkingTime
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(EP_TIME)
                .registerProperty(npLinkingtimeNetwork)
                .registerProperty(npLinkingtimeThing);

        etMonitoringActivity
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(CommonProperties.EP_NAME)
                .registerProperty(CommonProperties.EP_DEFINITION)
                .registerProperty(CommonProperties.EP_DESCRIPTION)
                .registerProperty(CommonProperties.EP_PROPERTIES)
                .registerProperty(npMonitoringactivityNetworks)
                .registerProperty(npMonitoringactivityPrograms);

        etMonitoringNetwork
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(CommonProperties.EP_NAME)
                .registerProperty(CommonProperties.EP_DEFINITION)
                .registerProperty(CommonProperties.EP_DESCRIPTION)
                .registerProperty(CommonProperties.EP_PROPERTIES)
                .registerProperty(npMonitoringnetworkActivities)
                .registerProperty(npMonitoringnetworkLinkingtimes)
                .registerProperty(npMonitoringnetworkThings);

        etMonitoringProgram
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(CommonProperties.EP_NAME)
                .registerProperty(CommonProperties.EP_DEFINITION)
                .registerProperty(CommonProperties.EP_DESCRIPTION)
                .registerProperty(CommonProperties.EP_PROPERTIES)
                .registerProperty(npMonitoringprogramActivities);

        etObservingProcedure
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(CommonProperties.EP_NAME)
                .registerProperty(CommonProperties.EP_DEFINITION)
                .registerProperty(CommonProperties.EP_DESCRIPTION)
                .registerProperty(CommonProperties.EP_PROPERTIES)
                .registerProperty(npObservingprocedureDatastreams)
                .registerProperty(npObservingprocedureObservedproperties)
                .registerProperty(npObservingprocedureSensors);

        mr.getEntityTypeForName(NAME_THING)
                .registerProperty(npThingDeployments)
                .registerProperty(npThingLinkingTimes)
                .registerProperty(npThingMonitoringnetworks);
        mr.getEntityTypeForName(NAME_OBSERVEDPROPERTY)
                .registerProperty(npObservedpropertyObservingprocedure);
        mr.getEntityTypeForName(NAME_DEPLOYMENT)
                .registerProperty(npDeploymentDatastreams)
                .registerProperty(npDeploymentSensor)
                .registerProperty(npDeploymentThing);
    }

    @Override
    public boolean isInitialised() {
        return mr != null;
    }

    public ModelRegistry getModelRegistry() {
        return mr;
    }

    public DeploymentBuilder buildDeployment() {
        return new DeploymentBuilder(this);
    }

    public DeploymentBuilder editDeployment(Entity entity) {
        return new DeploymentBuilder(this, entity);
    }

    public LinkingTimeBuilder buildLinkingTime() {
        return new LinkingTimeBuilder(this);
    }

    public LinkingTimeBuilder editLinkingTime(Entity entity) {
        return new LinkingTimeBuilder(this, entity);

    }

    public MonitoringActivityBuilder buildMonitoringActivity() {
        return new MonitoringActivityBuilder(this);
    }

    public MonitoringActivityBuilder editMonitoringActivity(Entity entity) {
        return new MonitoringActivityBuilder(this, entity);
    }

    public MonitoringNetworkBuilder buildMonitoringNetwork() {
        return new MonitoringNetworkBuilder(this);
    }

    public MonitoringNetworkBuilder editMonitoringNetwork(Entity entity) {
        return new MonitoringNetworkBuilder(this, entity);
    }

    public MonitoringProgramBuilder buildMonitoringProgram() {
        return new MonitoringProgramBuilder(this);
    }

    public MonitoringProgramBuilder editMonitoringProgram(Entity entity) {
        return new MonitoringProgramBuilder(this, entity);
    }

    public ObservingProcedureBuilder buildObservingProcedure() {
        return new ObservingProcedureBuilder(this);
    }

    public ObservingProcedureBuilder editObservingProcedure(Entity entity) {
        return new ObservingProcedureBuilder(this, entity);

    }

    public ThingExtensionBuilder thingExtender() {
        return new ThingExtensionBuilder(this);
    }

    public static class BuilderIdNameDefDesPropTime<T extends BuilderIdNameDefDesPropTime<T>> extends BuilderIdNameDefDesProp<T> {

        public BuilderIdNameDefDesPropTime(Entity entity) {
            super(entity);
        }

        public T setTime(TimeValue time) {
            entity.setProperty(EP_TIME, time);
            return getThis();
        }

        public T setTimeStart(Moment start) {
            TimeValue time = entity.getProperty(EP_TIME);
            if (time == null) {
                time = TimeValue.create(start);
                entity.setProperty(EP_TIME, time);
            } else {
                time.setProperty(TimeValue.EP_START_TIME, TimeInstant.create(start));
            }
            return getThis();
        }

        public T setTimeEnd(Moment end) {
            TimeValue time = entity.getProperty(EP_TIME);
            Exceptions.illegalArgumentIf(time == null, "Set the start time first.");
            time.setProperty(TimeValue.EP_END_TIME, TimeInstant.create(end));
            return getThis();
        }
    }

    public static class DeploymentBuilder extends BuilderIdNameDefDesPropTime<DeploymentBuilder> {

        SensorThingsV20Om mdlOm;

        public DeploymentBuilder(SensorThingsV20Om mdlOm) {
            super(new Entity(mdlOm.etDeployment));
            this.mdlOm = mdlOm;
        }

        public DeploymentBuilder(SensorThingsV20Om mdlOm, Entity entity) {
            super(entity);
            this.mdlOm = mdlOm;
        }

        public DeploymentBuilder setReason(String reason) {
            entity.setProperty(EP_REASON, reason);
            return getThis();
        }

        public DeploymentBuilder setEncodingType(String encodingType) {
            entity.setProperty(CommonProperties.EP_ENCODINGTYPE, encodingType);
            return getThis();
        }

        public DeploymentBuilder setPosition(Object position) {
            entity.setProperty(EP_POSITION, position);
            return getThis();
        }

        public DeploymentBuilder addDatastream(Entity ds) {
            entity.addNavigationEntity(mdlOm.npDeploymentDatastreams, ds);
            return getThis();
        }

        public DeploymentBuilder setSensor(Entity sensor) {
            entity.setProperty(mdlOm.npDeploymentSensor, sensor);
            return getThis();
        }

        public DeploymentBuilder setThing(Entity thing) {
            entity.setProperty(mdlOm.npDeploymentThing, thing);
            return getThis();
        }
    }

    public static class LinkingTimeBuilder extends BuilderId<LinkingTimeBuilder> {

        SensorThingsV20Om mdlOm;

        public LinkingTimeBuilder(SensorThingsV20Om mdlOm) {
            super(new Entity(mdlOm.etLinkingTime));
            this.mdlOm = mdlOm;
        }

        public LinkingTimeBuilder(SensorThingsV20Om mdlOm, Entity entity) {
            super(entity);
            this.mdlOm = mdlOm;
        }

        public LinkingTimeBuilder addMonitoringNetwork(Entity monNet) {
            entity.setProperty(mdlOm.npLinkingtimeNetwork, monNet);
            return getThis();
        }

        public LinkingTimeBuilder addThing(Entity thing) {
            entity.setProperty(mdlOm.npLinkingtimeThing, thing);
            return getThis();
        }

    }

    public static class MonitoringActivityBuilder extends BuilderIdNameDefDesPropTime<MonitoringActivityBuilder> {

        SensorThingsV20Om mdlOm;

        public MonitoringActivityBuilder(SensorThingsV20Om mdlOm) {
            super(new Entity(mdlOm.etMonitoringActivity));
            this.mdlOm = mdlOm;
        }

        public MonitoringActivityBuilder(SensorThingsV20Om mdlOm, Entity entity) {
            super(entity);
            this.mdlOm = mdlOm;
        }

        public MonitoringActivityBuilder addMonitoringNetwork(Entity monNet) {
            entity.addNavigationEntity(mdlOm.npMonitoringactivityNetworks, monNet);
            return getThis();
        }

        public MonitoringActivityBuilder addMonitoringProgram(Entity monProg) {
            entity.addNavigationEntity(mdlOm.npMonitoringactivityPrograms, monProg);
            return getThis();
        }
    }

    public static class MonitoringNetworkBuilder extends BuilderIdNameDefDesPropTime<MonitoringNetworkBuilder> {

        SensorThingsV20Om mdlOm;

        public MonitoringNetworkBuilder(SensorThingsV20Om mdlOm) {
            super(new Entity(mdlOm.etMonitoringNetwork));
            this.mdlOm = mdlOm;
        }

        public MonitoringNetworkBuilder(SensorThingsV20Om mdlOm, Entity entity) {
            super(entity);
            this.mdlOm = mdlOm;
        }

        public MonitoringNetworkBuilder addMonitoringActivity(Entity monAct) {
            entity.addNavigationEntity(mdlOm.npMonitoringnetworkActivities, monAct);
            return getThis();
        }

        public MonitoringNetworkBuilder addLinkingTime(Entity lt) {
            entity.addNavigationEntity(mdlOm.npMonitoringnetworkLinkingtimes, lt);
            return getThis();
        }

        public MonitoringNetworkBuilder addThing(Entity thing) {
            entity.addNavigationEntity(mdlOm.npMonitoringnetworkThings, thing);
            return getThis();
        }
    }

    public static class MonitoringProgramBuilder extends BuilderIdNameDefDesPropTime<MonitoringProgramBuilder> {

        SensorThingsV20Om mdlOm;

        public MonitoringProgramBuilder(SensorThingsV20Om mdlOm) {
            super(new Entity(mdlOm.etMonitoringProgram));
            this.mdlOm = mdlOm;
        }

        public MonitoringProgramBuilder(SensorThingsV20Om mdlOm, Entity entity) {
            super(entity);
            this.mdlOm = mdlOm;
        }

        public MonitoringProgramBuilder addMonitoringActivity(Entity monAct) {
            entity.addNavigationEntity(mdlOm.npMonitoringprogramActivities, monAct);
            return getThis();
        }
    }

    public static class ObservingProcedureBuilder extends BuilderIdNameDefDesProp<ObservingProcedureBuilder> {

        SensorThingsV20Om mdlOm;

        public ObservingProcedureBuilder(SensorThingsV20Om mdlOm) {
            super(new Entity(mdlOm.etObservingProcedure));
            this.mdlOm = mdlOm;
        }

        public ObservingProcedureBuilder(SensorThingsV20Om mdlOm, Entity entity) {
            super(entity);
            this.mdlOm = mdlOm;
        }

        public ObservingProcedureBuilder addDatastream(Entity ds) {
            entity.addNavigationEntity(mdlOm.npObservingprocedureDatastreams, ds);
            return getThis();
        }

        public ObservingProcedureBuilder addObservedProperty(Entity op) {
            entity.addNavigationEntity(mdlOm.npObservingprocedureObservedproperties, op);
            return getThis();
        }

        public ObservingProcedureBuilder addSensor(Entity sensor) {
            entity.addNavigationEntity(mdlOm.npObservingprocedureSensors, sensor);
            return getThis();
        }
    }

    public static class ThingExtensionBuilder extends Builder<ThingExtensionBuilder> {

        SensorThingsV20Om mdlOm;

        public ThingExtensionBuilder(SensorThingsV20Om mdlOm) {
            this.mdlOm = mdlOm;
        }

        public ThingExtensionBuilder addDeployment(Entity deployment) {
            entity.addNavigationEntity(mdlOm.npThingDeployments, deployment);
            return getThis();
        }

        public ThingExtensionBuilder addLinkingTime(Entity lt) {
            entity.addNavigationEntity(mdlOm.npThingLinkingTimes, lt);
            return getThis();
        }

        public ThingExtensionBuilder addMonitoringNetwork(Entity monNet) {
            entity.addNavigationEntity(mdlOm.npThingMonitoringnetworks, monNet);
            return getThis();
        }
    }
}
