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
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_METADATA;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Tasking.EP_CREATIONTIME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Tasking.EP_TASKINGPARAMETERS_T;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Tasking.EP_TASKINGPARAMETERS_TC;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Tasking;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.AbstractDataComponent;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.complex.DataRecord;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.simple.SweBoolean;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.simple.Text;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class V11TaskingBuilderTest extends BuilderTest {

    protected final SensorThingsV11Tasking model = new SensorThingsV11Tasking();

    private Entity actuatorEntity;
    private Entity taskingCapabilityEntity;
    private Entity taskEntity;
    private SensorThingsV11Sensing modelSensing;

    @BeforeEach
    public void setUp() throws Exception {
        modelSensing = new SensorThingsV11Sensing();
        SensorThingsService service = new SensorThingsService(modelSensing, model)
                .setBaseUrl(SensorThingsService.NULL_URL_V11)
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
        EntitySet taskingCapabilities = entity.getProperty(model.npActuatorTaskingcapabilities);
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
    public void testTaskingCapabilitySetTaskingParameters() {
        DataRecord taskingParameters = new DataRecord();
        taskingParameters.getFields().add(new Text());
        Entity entity = model.buildTaskingCapability()
                .setTaskingParameters(taskingParameters)
                .build();
        Assertions.assertSame(taskingParameters, entity.getProperty(EP_TASKINGPARAMETERS_TC));
    }

    @Test
    public void testTaskingCapabilitySetActuator() throws ServiceFailureException {
        Entity actuator = model.buildActuator()
                .setId(1L)
                .build();
        Entity entity = model.buildTaskingCapability()
                .setActuator(actuator)
                .build();
        Assertions.assertSame(actuator, entity.getProperty(model.npTaskcapActuator, false));
    }

    @Test
    public void testTaskingCapabilitySetThing() throws ServiceFailureException {
        Entity thing = modelSensing.buildThing()
                .setId(1L)
                .build();
        Entity entity = model.buildTaskingCapability()
                .setThing(thing)
                .build();
        Assertions.assertSame(thing, entity.getProperty(model.npTaskcapThing, false));
    }

    @Test
    public void testTaskingCapabilityAddTask() {
        Entity task1 = model.buildTask().build();
        Entity task2 = model.buildTask().build();
        Entity entity = model.buildTaskingCapability()
                .addTask(task1)
                .addTask(task2)
                .build();
        EntitySet tasks = entity.getProperty(model.npTaskcapTasks);
        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(2, tasks.size());
    }

    @Test
    public void testTaskingParametersBuilder() {
        AbstractDataComponent field1 = new Text();
        AbstractDataComponent field2 = new SweBoolean();
        DataRecord result = SensorThingsV11Tasking.taskingParametersBuilder()
                .taskingParameter(field1)
                .taskingParameter("field2", field2)
                .build();
        Assertions.assertEquals(2, result.getFields().size());
    }

    @Test
    public void testActuatorQueryTaskingCapabilitiesThrowsWithoutService() {
        Entity entity = model.buildActuator().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npActuatorTaskingcapabilities));
    }

    @Test
    public void testTaskingCapabilityQueryTasksThrowsWithoutService() {
        Entity entity = model.buildTaskingCapability().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npTaskcapTasks));
    }

}
