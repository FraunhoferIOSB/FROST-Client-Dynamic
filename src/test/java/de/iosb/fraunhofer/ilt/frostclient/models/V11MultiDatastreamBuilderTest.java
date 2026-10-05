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
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11MultiDatastream;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.UnitOfMeasurement;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class V11MultiDatastreamBuilderTest extends BuilderTest {

    protected final SensorThingsV11MultiDatastream model = new SensorThingsV11MultiDatastream();

    private Entity multiDatastreamEntity;
    private Entity observationEntity;
    private Entity observedPropertyEntity;
    private Entity sensorEntity;
    private Entity thingEntity;

    private final SensorThingsV11Sensing modelSensing = new SensorThingsV11Sensing();

    @BeforeEach
    public void setUp() throws Exception {
        SensorThingsService service = new SensorThingsService(modelSensing, model)
                .setBaseUrl(SensorThingsService.NULL_URL_V11)
                .init();
        multiDatastreamEntity = model.buildMultiDatastream().build();
        observationEntity = model.observationExtender().build();
        observedPropertyEntity = modelSensing.buildObservedProperty().build();
        sensorEntity = modelSensing.buildSensor().build();
        thingEntity = modelSensing.buildThing().build();
    }

    @Test
    public void testBuildMultiDatastream() {
        Assertions.assertEquals(model.etMultiDatastream, multiDatastreamEntity.getType());
    }

    @Test
    public void testMultiDatastreamSetMultiObservationDataTypes() {
        List<String> obsTypes = Arrays.asList("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement", "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation");
        Entity entity = model.buildMultiDatastream()
                .setMultiObservationDataTypes(obsTypes)
                .build();
        Assertions.assertEquals(obsTypes, entity.getProperty(SensorThingsV11MultiDatastream.EP_MULTIOBSERVATIONDATATYPES));
    }

    @Test
    public void testMultiDatastreamCreateMultiObservationDataType() {
        List<UnitOfMeasurement> uoms = Arrays.asList(new UnitOfMeasurement("Meter", "m", "http://www.opengis.net/def/uom/UCUM/0/m"));
        Entity entity = model.buildMultiDatastream()
                .setUnitOfMeasurements(uoms)
                .createMultiObservationDataType()
                .build();
        List<String> obsTypes = entity.getProperty(SensorThingsV11MultiDatastream.EP_MULTIOBSERVATIONDATATYPES);
        Assertions.assertEquals(1, obsTypes.size());
        Assertions.assertEquals("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement", obsTypes.get(0));
    }

    @Test
    public void testMultiDatastreamSetUnitOfMeasurements() {
        List<UnitOfMeasurement> uoms = Arrays.asList(new UnitOfMeasurement("Meter", "m", "http://www.opengis.net/def/uom/UCUM/0/m"));
        Entity entity = model.buildMultiDatastream()
                .setUnitOfMeasurements(uoms)
                .build();
        Assertions.assertEquals(uoms, entity.getProperty(SensorThingsV11MultiDatastream.EP_UNITOFMEASUREMENTS));
    }

    @Test
    public void testMultiDatastreamSetUnitOfMeasurementsVarargs() {
        UnitOfMeasurement uom1 = new UnitOfMeasurement("Meter", "m", "http://www.opengis.net/def/uom/UCUM/0/m");
        UnitOfMeasurement uom2 = new UnitOfMeasurement("Second", "s", "http://www.opengis.net/def/uom/UCUM/0/s");
        Entity entity = model.buildMultiDatastream()
                .setUnitOfMeasurements(uom1, uom2)
                .build();
        List<UnitOfMeasurement> uoms = entity.getProperty(SensorThingsV11MultiDatastream.EP_UNITOFMEASUREMENTS);
        Assertions.assertEquals(2, uoms.size());
    }

    @Test
    public void testMultiDatastreamSetObservedProperty() throws ServiceFailureException {
        Entity op = modelSensing.buildObservedProperty()
                .setId(1L)
                .build();
        Entity entity = model.buildMultiDatastream()
                .addObservedProperty(op)
                .build();
        EntitySet observedProperties = entity.getProperty(model.npMultidatastreamObservedproperties);
        Assertions.assertNotNull(observedProperties);
        Assertions.assertEquals(1, observedProperties.size());
    }

    @Test
    public void testMultiDatastreamSetSensor() throws ServiceFailureException {
        Entity sensor = modelSensing.buildSensor()
                .setId(1L)
                .build();
        Entity entity = model.buildMultiDatastream()
                .setSensor(sensor)
                .build();
        Assertions.assertSame(sensor, entity.getProperty(model.npMultidatastreamSensor, false));
    }

    @Test
    public void testMultiDatastreamSetThing() throws ServiceFailureException {
        Entity thing = modelSensing.buildThing()
                .setId(1L)
                .build();
        Entity entity = model.buildMultiDatastream()
                .setThing(thing)
                .build();
        Assertions.assertSame(thing, entity.getProperty(model.npMultidatastreamThing, false));
    }

    @Test
    public void testMultiDatastreamQueryObservationsThrowsWithoutService() {
        Entity entity = model.buildMultiDatastream().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npMultidatastreamObservations));
    }

    @Test
    public void testMultiDatastreamQueryObservedpropertiesThrowsWithoutService() {
        Entity entity = model.buildMultiDatastream().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npMultidatastreamObservedproperties));
    }

    @Test
    public void testMultiDatastreamAddObservation() {
        Entity obs1 = modelSensing.buildObservation().build();
        Entity obs2 = modelSensing.buildObservation().build();
        Entity entity = model.buildMultiDatastream()
                .addObservation(obs1)
                .addObservation(obs2)
                .build();
        EntitySet observations = entity.getProperty(model.npMultidatastreamObservations);
        Assertions.assertNotNull(observations);
        Assertions.assertEquals(2, observations.size());
    }

    @Test
    public void testObservationExtensionBuilder() throws ServiceFailureException {
        Entity multiDatastream = model.buildMultiDatastream()
                .setId(1L)
                .build();
        Entity entity = modelSensing.buildObservation()
                .extend(model.observationExtender())
                .setMultiDatastream(multiDatastream)
                .build();
        Assertions.assertSame(multiDatastream, entity.getProperty(model.npObservationMultidatastream, false));
    }

    @Test
    public void testObservationExtensionBuilderReturnsSelf() {
        modelSensing.buildObservation()
                .extend(model.observationExtender())
                .getThis();
    }
}
