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

import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_ENCODINGTYPE;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core.EP_METADATA;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Tasking.EP_CREATIONTIME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Tasking.EP_RUNLOG;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Tasking.EP_RUNTIME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Tasking.EP_STATUS;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Tasking.EP_TASKINGPARAMETERS_T;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Tasking;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeValue;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.complex.DataRecord;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class V20TaskingBuilderTest extends BuilderTest {

    protected SensorThingsV20Tasking model;

    private Entity actuatorEntity;
    private Entity taskingCapabilityEntity;
    private Entity taskEntity;
    private SensorThingsV20Core modelCore;

    @BeforeEach
    public void setUp() throws Exception {
        model = new SensorThingsV20Tasking();
        modelCore = new SensorThingsV20Core();
        SensorThingsService service = new SensorThingsService(modelCore, model)
                .setBaseUrl(SensorThingsService.NULL_URL_V20)
                .init();
        actuatorEntity = model.buildActuator().build();
        taskingCapabilityEntity = model.buildTaskingCapability().build();
        taskEntity = model.buildTask().build();
    }

    @Test
    public void testBuildActuator() {
        Assertions.assertEquals(model.etActuator, actuatorEntity.getType());
    }

    @Test
    public void testBuildTask() {
        Assertions.assertEquals(model.etTask, taskEntity.getType());
    }

    @Test
    public void testBuildTaskingCapability() {
        Assertions.assertEquals(model.etTaskingCapability, taskingCapabilityEntity.getType());
    }

    @Test
    public void testActuatorSetEncodingType() {
        String encodingType = "application/pdf";
        Entity entity = model.buildActuator()
                .setEncodingType(encodingType)
                .build();
        Assertions.assertEquals(encodingType, entity.getProperty(EP_ENCODINGTYPE));
    }

    @Test
    public void testActuatorSetMetadata() {
        String metadata = "http://sensor.example.com/metadata.xml";
        Entity entity = model.buildActuator()
                .setMetadata(metadata)
                .build();
        Assertions.assertEquals(metadata, entity.getProperty(EP_METADATA));
    }

    @Test
    public void testActuatorAddTaskingCapability() {
        Entity tc1 = model.buildTaskingCapability().build();
        Entity tc2 = model.buildTaskingCapability().build();
        Entity entity = model.buildActuator()
                .addTaskingCapability(tc1)
                .addTaskingCapability(tc2)
                .build();
        EntitySet taskingCapabilities = entity.getProperty(model.npActuatorTaskingcaps);
        Assertions.assertNotNull(taskingCapabilities);
        Assertions.assertEquals(2, taskingCapabilities.size());
    }

    @Test
    public void testTaskSetCreationTime() {
        TimeInstant creationTime = TimeInstant.create(Instant.parse("2024-01-15T10:30:00Z"));
        Entity entity = model.buildTask()
                .setCreationTime(creationTime)
                .build();
        Assertions.assertSame(creationTime, entity.getProperty(EP_CREATIONTIME));
    }

    @Test
    public void testTaskSetRunTime() {
        TimeValue runTime = TimeValue.create(Instant.parse("2024-01-15T10:30:00Z"));
        Entity entity = model.buildTask()
                .setRunTime(runTime)
                .build();
        Assertions.assertSame(runTime, entity.getProperty(EP_RUNTIME));
    }

    @Test
    public void testTaskSetRunTimeStart() throws ServiceFailureException {
        TimeInstant creationTime = TimeInstant.create(Instant.parse("2024-01-15T10:30:00Z"));
        Entity entity = model.buildTask()
                .setRunTimeStart(creationTime.getDateTime())
                .build();
        TimeValue runTime = entity.getProperty(EP_RUNTIME);
        Assertions.assertNotNull(runTime);
    }

    @Test
    public void testTaskSetRunTimeEnd() throws ServiceFailureException {
        TimeInstant start = TimeInstant.create(Instant.parse("2024-01-15T10:30:00Z"));
        TimeInstant end = TimeInstant.create(Instant.parse("2024-01-15T11:30:00Z"));
        Entity entity = model.buildTask()
                .setRunTimeStart(start.getDateTime())
                .setRunTimeEnd(end.getDateTime())
                .build();
        TimeValue runTime = entity.getProperty(EP_RUNTIME);
        Assertions.assertNotNull(runTime);
    }

    @Test
    public void testTaskSetRunLog() {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("key1", "value1");
        MapValue runLog = new MapValue(TypeComplex.STA_MAP, logMap);
        Entity entity = model.buildTask()
                .setRunLog(runLog)
                .build();
        Assertions.assertSame(runLog, entity.getProperty(EP_RUNLOG));
    }

    @Test
    public void testTaskSetStatus() {
        SensorThingsV20Tasking.Status status = SensorThingsV20Tasking.Status.CREATED;
        Entity entity = model.buildTask()
                .setStatus(status)
                .build();
        Assertions.assertSame(status, entity.getProperty(EP_STATUS));
    }

    @Test
    public void testTaskSetTaskingParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("key1", "value1");
        MapValue taskingParameters = new MapValue(TypeComplex.STA_MAP, params);
        Entity entity = model.buildTask()
                .setTaskingParameters(taskingParameters)
                .build();
        Assertions.assertSame(taskingParameters, entity.getProperty(EP_TASKINGPARAMETERS_T));
    }

    @Test
    public void testTaskSetTaskingCapability() throws ServiceFailureException {
        Entity tc = model.buildTaskingCapability()
                .setId(1L)
                .build();
        Entity entity = model.buildTask()
                .setTaskingCapability(tc)
                .build();
        Assertions.assertSame(tc, entity.getProperty(model.npTaskTaskingcapability, false));
    }

    @Test
    public void testTaskSetProximateFeature() throws ServiceFailureException {
        Entity feature = modelCore.buildFeature()
                .setId(1L)
                .build();
        Entity entity = model.buildTask()
                .setProximateFeature(feature)
                .build();
        Assertions.assertSame(feature, entity.getProperty(model.npTaskProximateFeature, false));
    }

    @Test
    public void testTaskingCapabilitySetTaskingParameters() {
        DataRecord taskingParameters = new DataRecord();
        Entity entity = model.buildTaskingCapability()
                .setTaskingParameters(taskingParameters)
                .build();
        Assertions.assertSame(taskingParameters, entity.getProperty(SensorThingsV20Tasking.EP_TASKINGPARAMETERS_TC));
    }

    @Test
    public void testTaskingCapabilityAddActuatableProperty() {
        Entity prop1 = modelCore.buildObservedProperty()
                .build();
        Entity prop2 = modelCore.buildObservedProperty()
                .build();
        Entity entity = model.buildTaskingCapability()
                .addActuatableProperty(prop1)
                .addActuatableProperty(prop2)
                .build();
        EntitySet actuatableProperties = entity.getProperty(model.npTaskingcapActuatableProperties);
        Assertions.assertNotNull(actuatableProperties);
        Assertions.assertEquals(2, actuatableProperties.size());
    }

    @Test
    public void testTaskingCapabilityAddTask() {
        Entity task1 = model.buildTask().build();
        Entity task2 = model.buildTask().build();
        Entity entity = model.buildTaskingCapability()
                .addTask(task1)
                .addTask(task2)
                .build();
        EntitySet tasks = entity.getProperty(model.npTaskingcapTasks);
        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(2, tasks.size());
    }

    @Test
    public void testTaskingCapabilityAddUltimateFeature() {
        Entity feature1 = modelCore.buildFeature()
                .setId(1L)
                .build();
        Entity feature2 = modelCore.buildFeature()
                .setId(2L)
                .build();
        Entity entity = model.buildTaskingCapability()
                .addUltimateFeature(feature1)
                .addUltimateFeature(feature2)
                .build();
        EntitySet ultimateFeatures = entity.getProperty(model.npTaskingcapUltimateFeatures);
        Assertions.assertNotNull(ultimateFeatures);
        Assertions.assertEquals(2, ultimateFeatures.size());
    }

    @Test
    public void testTaskingCapabilitySetActuator() throws ServiceFailureException {
        Entity actuator = model.buildActuator()
                .build();
        Entity entity = model.buildTaskingCapability()
                .setActuator(actuator)
                .build();
        Assertions.assertSame(actuator, entity.getProperty(model.npTaskingcapActuator, false));
    }

    @Test
    public void testTaskingCapabilitySetThing() throws ServiceFailureException {
        Entity thing = modelCore.buildThing()
                .build();
        Entity entity = model.buildTaskingCapability()
                .setThing(thing)
                .build();
        Assertions.assertSame(thing, entity.getProperty(model.npTaskingcapThing, false));
    }

    @Test
    public void testActuatorQueryTaskingCapabilitiesThrowsWithoutService() {
        Entity entity = model.buildActuator().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npActuatorTaskingcaps));
    }

    @Test
    public void testTaskingCapabilityQueryTasksThrowsWithoutService() {
        Entity entity = model.buildTaskingCapability().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npTaskingcapTasks));
    }

}
