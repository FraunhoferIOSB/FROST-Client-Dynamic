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
package de.iosb.fraunhofer.ilt.frostclient.models;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Om;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeValue;
import java.time.Instant;
import org.geojson.Point;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class V20OmBuilderTest extends BuilderTest {

    protected final SensorThingsV20Om model = new SensorThingsV20Om();
    protected final SensorThingsV20Core modelCore = new SensorThingsV20Core();

    private SensorThingsV20Core modelSensing;

    @BeforeEach
    public void setUp() throws Exception {
        modelSensing = new SensorThingsV20Core();
        SensorThingsService service = new SensorThingsService(modelSensing, model)
                .setBaseUrl(SensorThingsService.NULL_URL_V20)
                .init();
    }

    @Test
    public void testBuildDeployment() {
        Entity entity = model.buildDeployment().build();
        Assertions.assertEquals(model.etDeployment, entity.getType());
    }

    @Test
    public void testBuildLinkingTime() {
        Entity entity = model.buildLinkingTime().build();
        Assertions.assertEquals(model.etLinkingTime, entity.getType());
    }

    @Test
    public void testBuildMonitoringActivity() {
        Entity entity = model.buildMonitoringActivity().build();
        Assertions.assertEquals(model.etMonitoringActivity, entity.getType());
    }

    @Test
    public void testBuildMonitoringNetwork() {
        Entity entity = model.buildMonitoringNetwork().build();
        Assertions.assertEquals(model.etMonitoringNetwork, entity.getType());
    }

    @Test
    public void testBuildMonitoringProgram() {
        Entity entity = model.buildMonitoringProgram().build();
        Assertions.assertEquals(model.etMonitoringProgram, entity.getType());
    }

    @Test
    public void testBuildObservingProcedure() {
        Entity entity = model.buildObservingProcedure().build();
        Assertions.assertEquals(model.etObservingProcedure, entity.getType());
    }

    @Test
    public void testDeploymentSetReason() {
        String reason = "Deployment for maintenance";
        Entity entity = model.buildDeployment()
                .setReason(reason)
                .build();
        Assertions.assertEquals(reason, entity.getProperty(SensorThingsV20Om.EP_REASON));
    }

    @Test
    public void testDeploymentSetEncodingType() {
        String encodingType = "application/pdf";
        Entity entity = model.buildDeployment()
                .setEncodingType(encodingType)
                .build();
        Assertions.assertEquals(encodingType, entity.getProperty(CommonProperties.EP_ENCODINGTYPE));
    }

    @Test
    public void testDeploymentSetPosition() {
        Object position = new Point(-112.153407, 36.054487);
        Entity entity = model.buildDeployment()
                .setPosition(position)
                .build();
        Assertions.assertSame(position, entity.getProperty(SensorThingsV20Om.EP_POSITION));
    }

    @Test
    public void testDeploymentAddDatastream() {
        Entity ds1 = modelCore.buildDatastream().build();
        Entity ds2 = modelCore.buildDatastream().build();
        Entity entity = model.buildDeployment()
                .addDatastream(ds1)
                .addDatastream(ds2)
                .build();
        EntitySet datastreams = entity.getProperty(model.npDeploymentDatastreams);
        Assertions.assertNotNull(datastreams);
        Assertions.assertEquals(2, datastreams.size());
    }

    @Test
    public void testDeploymentSetSensor() throws ServiceFailureException {
        Entity sensor = modelCore.buildSensor().build();
        Entity entity = model.buildDeployment()
                .setSensor(sensor)
                .build();
        Assertions.assertSame(sensor, entity.getProperty(model.npDeploymentSensor));
    }

    @Test
    public void testDeploymentSetThing() throws ServiceFailureException {
        Entity thing = modelCore.buildThing().build();
        Entity entity = model.buildDeployment()
                .setThing(thing)
                .build();
        Assertions.assertSame(thing, entity.getProperty(model.npDeploymentThing));
    }

    @Test
    public void testDeploymentQueryDatastreamsThrowsWithoutService() {
        Entity entity = model.buildDeployment().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npDeploymentDatastreams));
    }

    @Test
    public void testLinkingTimeSetTime() {
        TimeValue time = TimeValue.create(Instant.parse("2016-02-15T09:30:00Z"));
        Entity entity = model.buildLinkingTime()
                .setTime(time)
                .build();
        Assertions.assertSame(time, entity.getProperty(SensorThingsV20Om.EP_TIME));
    }

    @Test
    public void testLinkingTimeSetTimeStart() {
        TimeInstant instant = TimeInstant.parse("2016-02-15T09:30:00Z");
        TimeValue time = model.buildLinkingTime()
                .setTimeStart(instant.getDateTime())
                .getTime();
        Assertions.assertNotNull(time);
        Assertions.assertNotNull(time.getProperty(TypeComplex.EP_START_TIME));
    }

    @Test
    public void testLinkingTimeSetTimeEnd() {
        TimeInstant instantStart = TimeInstant.parse("2016-02-15T09:30:00Z");
        TimeInstant instantEnd = TimeInstant.parse("2016-02-15T09:35:00Z");
        TimeValue time = model.buildLinkingTime()
                .setTimeStart(instantStart.getDateTime())
                .setTimeEnd(instantEnd.getDateTime())
                .getTime();
        Assertions.assertNotNull(time);
        Assertions.assertNotNull(time.getProperty(TypeComplex.EP_END_TIME));
    }

    @Test
    public void testLinkingTimeSetTimeEndWithoutStartThrows() {
        TimeInstant instantEnd = TimeInstant.parse("2016-02-15T09:35:00Z");
        assertThrowsIllegalArgumentException(() -> model.buildLinkingTime().setTimeEnd(instantEnd.getDateTime()));
    }

    @Test
    public void testLinkingTimeAddMonitoringNetwork() throws ServiceFailureException {
        Entity monNet = model.buildMonitoringNetwork().build();
        Entity entity = model.buildLinkingTime()
                .addMonitoringNetwork(monNet)
                .build();
        Assertions.assertSame(monNet, entity.getProperty(model.npLinkingtimeNetwork));
    }

    @Test
    public void testLinkingTimeSetThing() throws ServiceFailureException {
        Entity thing = modelCore.buildThing().build();
        Entity entity = model.buildLinkingTime()
                .setThing(thing)
                .build();
        Assertions.assertSame(thing, entity.getProperty(model.npLinkingtimeThing));
    }

    @Test
    public void testMonitoringActivityAddMonitoringNetwork() {
        Entity monNet1 = model.buildMonitoringNetwork().build();
        Entity monNet2 = model.buildMonitoringNetwork().build();
        Entity entity = model.buildMonitoringActivity()
                .addMonitoringNetwork(monNet1)
                .addMonitoringNetwork(monNet2)
                .build();
        EntitySet networks = entity.getProperty(model.npMonitoringactivityNetworks);
        Assertions.assertNotNull(networks);
        Assertions.assertEquals(2, networks.size());
    }

    @Test
    public void testMonitoringActivityAddMonitoringProgram() {
        Entity monProg1 = model.buildMonitoringProgram().build();
        Entity monProg2 = model.buildMonitoringProgram().build();
        Entity entity = model.buildMonitoringActivity()
                .addMonitoringProgram(monProg1)
                .addMonitoringProgram(monProg2)
                .build();
        EntitySet programs = entity.getProperty(model.npMonitoringactivityPrograms);
        Assertions.assertNotNull(programs);
        Assertions.assertEquals(2, programs.size());
    }

    @Test
    public void testMonitoringActivityQueryNetworksThrowsWithoutService() {
        Entity entity = model.buildMonitoringActivity().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npMonitoringactivityNetworks));
    }

    @Test
    public void testMonitoringActivityQueryProgramsThrowsWithoutService() {
        Entity entity = model.buildMonitoringActivity().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npMonitoringactivityPrograms));
    }

    @Test
    public void testMonitoringNetworkAddMonitoringActivity() {
        Entity monAct1 = model.buildMonitoringActivity().build();
        Entity monAct2 = model.buildMonitoringActivity().build();
        Entity entity = model.buildMonitoringNetwork()
                .addMonitoringActivity(monAct1)
                .addMonitoringActivity(monAct2)
                .build();
        EntitySet activities = entity.getProperty(model.npMonitoringnetworkActivities);
        Assertions.assertNotNull(activities);
        Assertions.assertEquals(2, activities.size());
    }

    @Test
    public void testMonitoringNetworkAddLinkingTime() {
        Entity lt1 = model.buildLinkingTime().build();
        Entity lt2 = model.buildLinkingTime().build();
        Entity entity = model.buildMonitoringNetwork()
                .addLinkingTime(lt1)
                .addLinkingTime(lt2)
                .build();
        EntitySet linkingTimes = entity.getProperty(model.npMonitoringnetworkLinkingtimes);
        Assertions.assertNotNull(linkingTimes);
        Assertions.assertEquals(2, linkingTimes.size());
    }

    @Test
    public void testMonitoringNetworkAddThing() {
        Entity thing1 = modelCore.buildThing().build();
        Entity thing2 = modelCore.buildThing().build();
        Entity entity = model.buildMonitoringNetwork()
                .addThing(thing1)
                .addThing(thing2)
                .build();
        EntitySet things = entity.getProperty(model.npMonitoringnetworkThings);
        Assertions.assertNotNull(things);
        Assertions.assertEquals(2, things.size());
    }

    @Test
    public void testMonitoringNetworkQueryActivitiesThrowsWithoutService() {
        Entity entity = model.buildMonitoringNetwork().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npMonitoringnetworkActivities));
    }

    @Test
    public void testMonitoringNetworkQueryLinkingTimesThrowsWithoutService() {
        Entity entity = model.buildMonitoringNetwork().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npMonitoringnetworkLinkingtimes));
    }

    @Test
    public void testMonitoringNetworkQueryThingsThrowsWithoutService() {
        Entity entity = model.buildMonitoringNetwork().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npMonitoringnetworkThings));
    }

    @Test
    public void testMonitoringProgramAddMonitoringActivity() {
        Entity monAct1 = model.buildMonitoringActivity().build();
        Entity monAct2 = model.buildMonitoringActivity().build();
        Entity entity = model.buildMonitoringProgram()
                .addMonitoringActivity(monAct1)
                .addMonitoringActivity(monAct2)
                .build();
        EntitySet activities = entity.getProperty(model.npMonitoringprogramActivities);
        Assertions.assertNotNull(activities);
        Assertions.assertEquals(2, activities.size());
    }

    @Test
    public void testMonitoringProgramQueryActivitiesThrowsWithoutService() {
        Entity entity = model.buildMonitoringProgram().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npMonitoringprogramActivities));
    }

    @Test
    public void testObservingProcedureAddDatastream() {
        Entity ds1 = modelCore.buildDatastream().build();
        Entity ds2 = modelCore.buildDatastream().build();
        Entity entity = model.buildObservingProcedure()
                .addDatastream(ds1)
                .addDatastream(ds2)
                .build();
        EntitySet datastreams = entity.getProperty(model.npObservingprocedureDatastreams);
        Assertions.assertNotNull(datastreams);
        Assertions.assertEquals(2, datastreams.size());
    }

    @Test
    public void testObservingProcedureAddObservedProperty() {
        Entity op1 = modelCore.buildObservedProperty().build();
        Entity op2 = modelCore.buildObservedProperty().build();
        Entity entity = model.buildObservingProcedure()
                .addObservedProperty(op1)
                .addObservedProperty(op2)
                .build();
        EntitySet observedProperties = entity.getProperty(model.npObservingprocedureObservedproperties);
        Assertions.assertNotNull(observedProperties);
        Assertions.assertEquals(2, observedProperties.size());
    }

    @Test
    public void testObservingProcedureAddSensor() {
        Entity sensor1 = modelCore.buildSensor().build();
        Entity sensor2 = modelCore.buildSensor().build();
        Entity entity = model.buildObservingProcedure()
                .addSensor(sensor1)
                .addSensor(sensor2)
                .build();
        EntitySet sensors = entity.getProperty(model.npObservingprocedureSensors);
        Assertions.assertNotNull(sensors);
        Assertions.assertEquals(2, sensors.size());
    }

    @Test
    public void testObservingProcedureQueryDatastreamsThrowsWithoutService() {
        Entity entity = model.buildObservingProcedure().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npObservingprocedureDatastreams));
    }

    @Test
    public void testObservingProcedureQueryObservedPropertiesThrowsWithoutService() {
        Entity entity = model.buildObservingProcedure().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npObservingprocedureObservedproperties));
    }

    @Test
    public void testObservingProcedureQuerySensorsThrowsWithoutService() {
        Entity entity = model.buildObservingProcedure().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npObservingprocedureSensors));
    }

    @Test
    public void testThingExtensionAddDeployment() {
        Entity dep1 = model.buildDeployment().build();
        Entity dep2 = model.buildDeployment().build();
        Entity entity = modelSensing.buildThing()
                .extend(model.thingExtender())
                .addDeployment(dep1)
                .addDeployment(dep2)
                .build();
        EntitySet deployments = entity.getProperty(model.npThingDeployments);
        Assertions.assertNotNull(deployments);
        Assertions.assertEquals(2, deployments.size());
    }

    @Test
    public void testThingExtensionAddLinkingTime() {
        Entity lt1 = model.buildLinkingTime().build();
        Entity lt2 = model.buildLinkingTime().build();
        Entity entity = modelSensing.buildThing()
                .extend(model.thingExtender())
                .addLinkingTime(lt1)
                .addLinkingTime(lt2)
                .build();
        EntitySet linkingTimes = entity.getProperty(model.npThingLinkingTimes);
        Assertions.assertNotNull(linkingTimes);
        Assertions.assertEquals(2, linkingTimes.size());
    }

    @Test
    public void testThingExtensionAddMonitoringNetwork() {
        Entity monNet1 = model.buildMonitoringNetwork().build();
        Entity monNet2 = model.buildMonitoringNetwork().build();
        Entity entity = modelSensing.buildThing()
                .extend(model.thingExtender())
                .addMonitoringNetwork(monNet1)
                .addMonitoringNetwork(monNet2)
                .build();
        EntitySet networks = entity.getProperty(model.npThingMonitoringnetworks);
        Assertions.assertNotNull(networks);
        Assertions.assertEquals(2, networks.size());
    }

    @Test
    public void testThingExtensionQueryDeploymentsThrowsWithoutService() {
        Entity entity = modelSensing.buildThing()
                .extend(model.thingExtender())
                .build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npThingDeployments));
    }

    @Test
    public void testThingExtensionQueryLinkingTimesThrowsWithoutService() {
        Entity entity = modelSensing.buildThing()
                .extend(model.thingExtender())
                .build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npThingLinkingTimes));
    }

    @Test
    public void testThingExtensionQueryMonitoringNetworksThrowsWithoutService() {
        Entity entity = modelSensing.buildThing()
                .extend(model.thingExtender())
                .build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npThingMonitoringnetworks));
    }
}
