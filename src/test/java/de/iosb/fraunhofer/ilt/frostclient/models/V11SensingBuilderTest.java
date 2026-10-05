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

import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_DEFINITION;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_ENCODINGTYPE;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_FEATURE;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_LOCATION;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_METADATA;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_OBSERVATIONTYPE;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_PARAMETERS;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_PHENOMENONTIME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_RESULT;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_RESULTQUALITY;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_RESULTTIME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_TIME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_UNITOFMEASUREMENT;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_VALIDTIME;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInterval;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.UnitOfMeasurement;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.geojson.GeoJsonObject;
import org.geojson.Point;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class V11SensingBuilderTest extends BuilderTest {

    private SensorThingsV11Sensing model;
    private Entity datastreamEntity;
    private Entity featureOfInterestEntity;
    private Entity historicalLocationEntity;
    private Entity locationEntity;
    private Entity observationEntity;
    private Entity observedPropertyEntity;
    private Entity sensorEntity;
    private Entity thingEntity;

    @BeforeEach
    public void setUp() throws Exception {
        model = new SensorThingsV11Sensing();
        SensorThingsService service = new SensorThingsService(model)
                .setBaseUrl(SensorThingsService.NULL_URL_V11)
                .init();
        datastreamEntity = model.buildDatastream().build();
        featureOfInterestEntity = model.buildFeature().build();
        historicalLocationEntity = model.buildHistoricalLocation().build();
        locationEntity = model.buildLocation().build();
        observationEntity = model.buildObservation().build();
        observedPropertyEntity = model.buildObservedProperty().build();
        sensorEntity = model.buildSensor().build();
        thingEntity = model.buildThing().build();
    }

    @Test
    public void testBuildDatastream() {
        Assertions.assertEquals(model.etDatastream, datastreamEntity.getType());
    }

    @Test
    public void testBuildFeatureOfInterest() {
        Assertions.assertEquals(model.etFeatureOfInterest, featureOfInterestEntity.getType());
    }

    @Test
    public void testBuildHistoricalLocation() {
        Assertions.assertEquals(model.etHistoricalLocation, historicalLocationEntity.getType());
    }

    @Test
    public void testBuildLocation() {
        Assertions.assertEquals(model.etLocation, locationEntity.getType());
    }

    @Test
    public void testBuildObservation() {
        Assertions.assertEquals(model.etObservation, observationEntity.getType());
    }

    @Test
    public void testBuildObservedProperty() {
        Assertions.assertEquals(model.etObservedProperty, observedPropertyEntity.getType());
    }

    @Test
    public void testBuildSensor() {
        Assertions.assertEquals(model.etSensor, sensorEntity.getType());
    }

    @Test
    public void testBuildThing() {
        Assertions.assertEquals(model.etThing, thingEntity.getType());
    }

    @Test
    public void testDatastreamSetObservationType() {
        String obsType = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_PointObservation";
        Entity entity = model.buildDatastream()
                .setObservationType(obsType)
                .build();
        Assertions.assertEquals(obsType, entity.getProperty(EP_OBSERVATIONTYPE));
    }

    @Test
    public void testDatastreamSetUnitOfMeasurement() {
        UnitOfMeasurement uom = new UnitOfMeasurement("Meter", "m", "http://www.opengis.net/def/uom/UCUM/0/m");
        Entity entity = model.buildDatastream()
                .setUnitOfMeasurement(uom)
                .build();
        Assertions.assertSame(uom, entity.getProperty(EP_UNITOFMEASUREMENT));
    }

    @Test
    public void testDatastreamSetObservedProperty() throws ServiceFailureException {
        Entity op = model.buildObservedProperty()
                .setId(1L)
                .build();
        Entity entity = model.buildDatastream()
                .setObservedProperty(op)
                .build();
        Assertions.assertSame(op, entity.getProperty(model.npDatastreamObservedproperty, false));
    }

    @Test
    public void testDatastreamSetSensor() throws ServiceFailureException {
        Entity sensor = model.buildSensor()
                .setId(1L)
                .build();
        Entity entity = model.buildDatastream()
                .setSensor(sensor)
                .build();
        Assertions.assertSame(sensor, entity.getProperty(model.npDatastreamSensor, false));
    }

    @Test
    public void testDatastreamSetThing() throws ServiceFailureException {
        Entity thing = model.buildThing()
                .setId(1L)
                .build();
        Entity entity = model.buildDatastream()
                .setThing(thing)
                .build();
        Assertions.assertSame(thing, entity.getProperty(model.npDatastreamThing, false));
    }

    @Test
    public void testDatastreamQueryObservationsThrowsWithoutService() {
        Entity entity = model.buildDatastream().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npDatastreamObservations));
    }

    @Test
    public void testDatastreamAddObservation() {
        Entity obs1 = model.buildObservation().build();
        Entity obs2 = model.buildObservation().build();
        Entity entity = model.buildDatastream()
                .addObservation(obs1)
                .addObservation(obs2)
                .build();
        EntitySet observations = entity.getProperty(model.npDatastreamObservations);
        Assertions.assertNotNull(observations);
        Assertions.assertEquals(2, observations.size());
    }

    @Test
    public void testFeatureOfInterestSetEncodingType() {
        String encodingType = "application/geo+json";
        Entity entity = model.buildFeature()
                .setEncodingType(encodingType)
                .build();
        Assertions.assertEquals(encodingType, entity.getProperty(EP_ENCODINGTYPE));
    }

    @Test
    public void testFeatureOfInterestUsingGeoJson() {
        Entity entity = model.buildFeature()
                .usingGeoJson()
                .build();
        Assertions.assertEquals("application/geo+json", entity.getProperty(EP_ENCODINGTYPE));
    }

    @Test
    public void testFeatureOfInterestSetFeature() {
        GeoJsonObject feature = new Point(-112.153407, 36.054487);
        Entity entity = model.buildFeature()
                .setFeature(feature)
                .build();
        Assertions.assertSame(feature, entity.getProperty(EP_FEATURE));
    }

    @Test
    public void testFeatureOfInterestSetFeatureOfInterest() {
        GeoJsonObject feature = new Point(-112.153407, 36.054487);
        Entity entity = model.buildFeature()
                .setFeature(feature)
                .build();
        Assertions.assertSame(feature, entity.getProperty(EP_FEATURE));
    }

    @Test
    public void testHistoricalLocationSetTimeWithMoment() {
        TimeInstant time = TimeInstant.create(Instant.parse("2016-02-15T09:30:00Z"));
        Entity entity = model.buildHistoricalLocation()
                .setTime(time)
                .build();
        Assertions.assertSame(time, entity.getProperty(EP_TIME));
    }

    @Test
    public void testHistoricalLocationSetTimeWithTimeInstant() {
        TimeInstant time = TimeInstant.create(Instant.parse("2016-02-15T09:30:00Z"));
        Entity entity = model.buildHistoricalLocation()
                .setTime(time)
                .build();
        Assertions.assertSame(time, entity.getProperty(EP_TIME));
    }

    @Test
    public void testHistoricalLocationSetThing() throws ServiceFailureException {
        Entity thing = model.buildThing()
                .setId(1L)
                .build();
        Entity entity = model.buildHistoricalLocation()
                .setThing(thing)
                .build();
        Assertions.assertSame(thing, entity.getProperty(model.npHistlocThing, false));
    }

    @Test
    public void testLocationSetEncodingType() {
        String encodingType = "application/geo+json";
        Entity entity = model.buildLocation()
                .setEncodingType(encodingType)
                .build();
        Assertions.assertEquals(encodingType, entity.getProperty(EP_ENCODINGTYPE));
    }

    @Test
    public void testLocationUsingGeoJson() {
        Entity entity = model.buildLocation()
                .usingGeoJson()
                .build();
        Assertions.assertEquals("application/geo+json", entity.getProperty(EP_ENCODINGTYPE));
    }

    @Test
    public void testLocationSetLocation() {
        GeoJsonObject location = new Point(-112.153407, 36.054487);
        Entity entity = model.buildLocation()
                .setLocation(location)
                .build();
        Assertions.assertSame(location, entity.getProperty(EP_LOCATION));
    }

    @Test
    public void testObservationSetResult() {
        Object result = 42.5;
        Entity entity = model.buildObservation()
                .setResult(result)
                .build();
        Assertions.assertSame(result, entity.getProperty(EP_RESULT));
    }

    @Test
    public void testObservationSetResultQuality() {
        Map<String, Object> qualityMap = new HashMap<>();
        qualityMap.put("precision", 0.5);
        MapValue resultQuality = new MapValue(TypeComplex.STA_OBJECT, qualityMap);
        Entity entity = model.buildObservation()
                .setResultQuality(resultQuality)
                .build();
        Assertions.assertSame(resultQuality, entity.getProperty(EP_RESULTQUALITY));
    }

    @Test
    public void testObservationSetPhenomenonTime() {
        TimeValue phenTime = TimeValue.create(Instant.parse("2016-02-15T09:30:00Z"));
        Entity entity = model.buildObservation()
                .setPhenomenonTime(phenTime)
                .build();
        Assertions.assertSame(phenTime, entity.getProperty(EP_PHENOMENONTIME));
    }

    @Test
    public void testObservationSetResultTime() {
        TimeInstant resultTime = TimeInstant.create(Instant.parse("2016-02-15T09:31:00Z"));
        Entity entity = model.buildObservation()
                .setResultTime(resultTime)
                .build();
        Assertions.assertSame(resultTime, entity.getProperty(EP_RESULTTIME));
    }

    @Test
    public void testObservationSetValidTime() {
        Instant start = Instant.parse("2016-02-15T09:30:00Z");
        Instant end = Instant.parse("2016-02-15T09:35:00Z");
        TimeInterval validTime = TimeInterval.create(start, end);
        Entity entity = model.buildObservation()
                .setValidTime(validTime)
                .build();
        Assertions.assertSame(validTime, entity.getProperty(EP_VALIDTIME));
    }

    @Test
    public void testObservationSetParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("key1", "value1");
        MapValue parameters = new MapValue(TypeComplex.STA_MAP, params);
        Entity entity = model.buildObservation()
                .setParameters(parameters)
                .build();
        Assertions.assertSame(parameters, entity.getProperty(EP_PARAMETERS));
    }

    @Test
    public void testObservationSetDatastream() throws ServiceFailureException {
        Entity ds = model.buildDatastream()
                .setId(1L)
                .build();
        Entity entity = model.buildObservation()
                .setDatastream(ds)
                .build();
        Assertions.assertSame(ds, entity.getProperty(model.npObservationDatastream, false));
    }

    @Test
    public void testObservationSetFeatureOfInterest() throws ServiceFailureException {
        Entity foi = model.buildFeature()
                .setId(1L)
                .build();
        Entity entity = model.buildObservation()
                .setFeatureOfInterest(foi)
                .build();
        Assertions.assertSame(foi, entity.getProperty(model.npObservationFeatureofinterest, false));
    }

    @Test
    public void testObservedPropertySetDefinition() {
        String definition = "http://www.opengis.net/def/property/OGC/0/Temperature";
        Entity entity = model.buildObservedProperty()
                .setDefinition(definition)
                .build();
        Assertions.assertEquals(definition, entity.getProperty(EP_DEFINITION));
    }

    @Test
    public void testObservedPropertyAddDatastream() {
        Entity ds1 = model.buildDatastream().build();
        Entity ds2 = model.buildDatastream().build();
        Entity entity = model.buildObservedProperty()
                .addDatastream(ds1)
                .addDatastream(ds2)
                .build();
        EntitySet datastreams = entity.getProperty(model.npObspropDatastreams);
        Assertions.assertNotNull(datastreams);
        Assertions.assertEquals(2, datastreams.size());
    }

    @Test
    public void testSensorSetEncodingType() {
        String encodingType = "application/pdf";
        Entity entity = model.buildSensor()
                .setEncodingType(encodingType)
                .build();
        Assertions.assertEquals(encodingType, entity.getProperty(EP_ENCODINGTYPE));
    }

    @Test
    public void testSensorSetMetadata() {
        String metadata = "http://sensor.example.com/metadata.xml";
        Entity entity = model.buildSensor()
                .setMetadata(metadata)
                .build();
        Assertions.assertEquals(metadata, entity.getProperty(EP_METADATA));
    }

    @Test
    public void testSensorAddDatastream() {
        Entity ds1 = model.buildDatastream().build();
        Entity ds2 = model.buildDatastream().build();
        Entity entity = model.buildSensor()
                .addDatastream(ds1)
                .addDatastream(ds2)
                .build();
        EntitySet datastreams = entity.getProperty(model.npSensorDatastreams);
        Assertions.assertNotNull(datastreams);
        Assertions.assertEquals(2, datastreams.size());
    }

    @Test
    public void testThingAddDatastream() {
        Entity ds1 = model.buildDatastream().build();
        Entity ds2 = model.buildDatastream().build();
        Entity entity = model.buildThing()
                .addDatastream(ds1)
                .addDatastream(ds2)
                .build();
        EntitySet datastreams = entity.getProperty(model.npThingDatastreams);
        Assertions.assertNotNull(datastreams);
        Assertions.assertEquals(2, datastreams.size());
    }

    @Test
    public void testThingAddHistoricalLocation() {
        Entity hl1 = model.buildHistoricalLocation().build();
        Entity hl2 = model.buildHistoricalLocation().build();
        Entity entity = model.buildThing()
                .addHistoricalLocation(hl1)
                .addHistoricalLocation(hl2)
                .build();
        EntitySet historicalLocations = entity.getProperty(model.npThingHistoricallocations);
        Assertions.assertNotNull(historicalLocations);
        Assertions.assertEquals(2, historicalLocations.size());
    }

    @Test
    public void testThingAddLocation() {
        Entity loc1 = model.buildLocation().build();
        Entity loc2 = model.buildLocation().build();
        Entity entity = model.buildThing()
                .addLocation(loc1)
                .addLocation(loc2)
                .build();
        EntitySet locations = entity.getProperty(model.npThingLocations);
        Assertions.assertNotNull(locations);
        Assertions.assertEquals(2, locations.size());
    }
}
