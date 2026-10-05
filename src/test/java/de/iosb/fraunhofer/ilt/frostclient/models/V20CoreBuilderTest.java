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
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_PROPERTIES;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core.EP_FEATURE;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core.EP_LOCATION;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core.EP_METADATA;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core.EP_PHENOMENONTIME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core.EP_RESULT;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core.EP_RESULTTIME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core.EP_RESULTTYPE;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core.EP_TIME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core.EP_VALIDTIME;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV20Core;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInterval;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeValue;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.AbstractDataComponent;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.simple.Quantity;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.util.UnitOfMeasurement;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.geojson.GeoJsonObject;
import org.geojson.Point;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class V20CoreBuilderTest extends BuilderTest {

    protected final SensorThingsV20Core model = new SensorThingsV20Core();

    private Entity datastreamEntity;
    private Entity featureEntity;
    private Entity featureTypeEntity;
    private Entity historicalLocationEntity;
    private Entity locationEntity;
    private Entity observationEntity;
    private Entity observedPropertyEntity;
    private Entity sensorEntity;
    private Entity thingEntity;

    @BeforeEach
    public void setUp() throws Exception {
        SensorThingsService service = new SensorThingsService(model)
                .setBaseUrl(SensorThingsService.NULL_URL_V20)
                .init();
        datastreamEntity = model.buildDatastream().build();
        featureEntity = model.buildFeature().build();
        featureTypeEntity = model.buildFeatureType().build();
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
    public void testBuildFeature() {
        Assertions.assertEquals(model.etFeature, featureEntity.getType());
    }

    @Test
    public void testBuildFeatureType() {
        Assertions.assertEquals(model.etFeatureType, featureTypeEntity.getType());
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
    public void testDatastreamSetResultType() {
        UnitOfMeasurement uom = new UnitOfMeasurement()
                .setCode("http://www.opengis.net/def/uom/UCUM/0/cel")
                .setSymbol("degC")
                .setLabel("Celsius");
        Quantity resultType = new Quantity()
                .setDefinition("http://www.opengis.net/def/property/OGC/0/Temperature")
                .setUom(uom);
        Entity entity = model.buildDatastream()
                .setResultType(resultType)
                .build();
        Assertions.assertSame(resultType, entity.getProperty(EP_RESULTTYPE));
    }

    @Test
    public void testDatastreamWithQuantity() {
        UnitOfMeasurement uom = new UnitOfMeasurement()
                .setCode("http://www.opengis.net/def/uom/UCUM/0/m")
                .setSymbol("m")
                .setLabel("Meter");
        Entity entity = model.buildDatastream()
                .withQuantity("http://www.opengis.net/def/property/OGC/0/Length", uom)
                .build();
        AbstractDataComponent resultType = entity.getProperty(EP_RESULTTYPE);
        Assertions.assertNotNull(resultType);
        Assertions.assertEquals("http://www.opengis.net/def/property/OGC/0/Length", resultType.getDefinition());
    }

    @Test
    public void testDatastreamSetProximateFoi() throws ServiceFailureException {
        Entity foi = model.buildFeature()
                .setId(1L)
                .build();
        Entity entity = model.buildDatastream()
                .setProximateFoi(foi)
                .build();
        Assertions.assertSame(foi, entity.getProperty(model.npDatastreamProximateFoi, false));
    }

    @Test
    public void testDatastreamAddUltimateFoi() {
        Entity foi1 = model.buildFeature().build();
        Entity foi2 = model.buildFeature().build();
        Entity entity = model.buildDatastream()
                .addUltimateFoi(foi1)
                .addUltimateFoi(foi2)
                .build();
        EntitySet ultimateFois = entity.getProperty(model.npDatastreamUltimateFois);
        Assertions.assertNotNull(ultimateFois);
        Assertions.assertEquals(2, ultimateFois.size());
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
    public void testDatastreamAddObservations() {
        Entity obs1 = model.buildObservation().build();
        Entity obs2 = model.buildObservation().build();
        Entity entity = model.buildDatastream()
                .addObservations(obs1)
                .addObservations(obs2)
                .build();
        EntitySet observations = entity.getProperty(model.npDatastreamObservations);
        Assertions.assertNotNull(observations);
        Assertions.assertEquals(2, observations.size());
    }

    @Test
    public void testFeatureSetEncodingType() {
        String encodingType = "application/geo+json";
        Entity entity = model.buildFeature()
                .setEncodingType(encodingType)
                .build();
        Assertions.assertEquals(encodingType, entity.getProperty(EP_ENCODINGTYPE));
    }

    @Test
    public void testFeatureUsingGeoJson() {
        Entity entity = model.buildFeature()
                .usingGeoJson()
                .build();
        Assertions.assertEquals("application/geo+json", entity.getProperty(EP_ENCODINGTYPE));
    }

    @Test
    public void testFeatureSetFeature() {
        GeoJsonObject feature = new Point(-112.153407, 36.054487);
        Entity entity = model.buildFeature()
                .setFeature(feature)
                .build();
        Assertions.assertSame(feature, entity.getProperty(EP_FEATURE));
    }

    @Test
    public void testFeatureAddDatastreamProximate() {
        Entity ds1 = model.buildDatastream().build();
        Entity ds2 = model.buildDatastream().build();
        Entity entity = model.buildFeature()
                .addDatastreamProximate(ds1)
                .addDatastreamProximate(ds2)
                .build();
        EntitySet datastreamsProximate = entity.getProperty(model.npFeatureDatastreamsProximate);
        Assertions.assertNotNull(datastreamsProximate);
        Assertions.assertEquals(2, datastreamsProximate.size());
    }

    @Test
    public void testFeatureAddDatastreamUltimate() {
        Entity ds1 = model.buildDatastream().build();
        Entity ds2 = model.buildDatastream().build();
        Entity entity = model.buildFeature()
                .addDatastreamUltimate(ds1)
                .addDatastreamUltimate(ds2)
                .build();
        EntitySet datastreamsUltimate = entity.getProperty(model.npFeatureDatastreamsUltimate);
        Assertions.assertNotNull(datastreamsUltimate);
        Assertions.assertEquals(2, datastreamsUltimate.size());
    }

    @Test
    public void testFeatureAddFeatureType() {
        Entity ft1 = model.buildFeatureType().build();
        Entity ft2 = model.buildFeatureType().build();
        Entity entity = model.buildFeature()
                .addFeatureType(ft1)
                .addFeatureType(ft2)
                .build();
        EntitySet featureTypes = entity.getProperty(model.npFeatureFeatureTypes);
        Assertions.assertNotNull(featureTypes);
        Assertions.assertEquals(2, featureTypes.size());
    }

    @Test
    public void testFeatureAddObservation() {
        Entity obs1 = model.buildObservation().build();
        Entity obs2 = model.buildObservation().build();
        Entity entity = model.buildFeature()
                .addObservation(obs1)
                .addObservation(obs2)
                .build();
        EntitySet observations = entity.getProperty(model.npFeatureObservations);
        Assertions.assertNotNull(observations);
        Assertions.assertEquals(2, observations.size());
    }

    @Test
    public void testFeatureTypeAddFeature() {
        Entity feature1 = model.buildFeature().build();
        Entity feature2 = model.buildFeature().build();
        Entity entity = model.buildFeatureType()
                .addFeature(feature1)
                .addFeature(feature2)
                .build();
        EntitySet features = entity.getProperty(model.npFeatureTypeFeatures);
        Assertions.assertNotNull(features);
        Assertions.assertEquals(2, features.size());
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
    public void testHistoricalLocationSetTimeWithInstant() {
        TimeInstant time = TimeInstant.create(Instant.parse("2016-02-15T09:30:00Z"));
        Entity entity = model.buildHistoricalLocation()
                .setTime(time)
                .build();
        Assertions.assertSame(time, entity.getProperty(EP_TIME));
    }

    @Test
    public void testHistoricalLocationAddLocation() {
        Entity loc1 = model.buildLocation().build();
        Entity loc2 = model.buildLocation().build();
        Entity entity = model.buildHistoricalLocation()
                .addLocation(loc1)
                .addLocation(loc2)
                .build();
        EntitySet locations = entity.getProperty(model.npHistlocLocations);
        Assertions.assertNotNull(locations);
        Assertions.assertEquals(2, locations.size());
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
    public void testLocationAddHistoricalLocation() {
        Entity hl1 = model.buildHistoricalLocation().build();
        Entity hl2 = model.buildHistoricalLocation().build();
        Entity entity = model.buildLocation()
                .addHistoricalLocation(hl1)
                .addHistoricalLocation(hl2)
                .build();
        EntitySet historicalLocations = entity.getProperty(model.npLocationHistoricallocations);
        Assertions.assertNotNull(historicalLocations);
        Assertions.assertEquals(2, historicalLocations.size());
    }

    @Test
    public void testLocationAddThing() {
        Entity thing1 = model.buildThing().build();
        Entity thing2 = model.buildThing().build();
        Entity entity = model.buildLocation()
                .addThing(thing1)
                .addThing(thing2)
                .build();
        EntitySet things = entity.getProperty(model.npLocationThings);
        Assertions.assertNotNull(things);
        Assertions.assertEquals(2, things.size());
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
    public void testObservationSetProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("key1", "value1");
        MapValue properties = new MapValue(TypeComplex.STA_MAP, props);
        Entity entity = model.buildObservation()
                .setProperties(properties)
                .build();
        Assertions.assertSame(properties, entity.getProperty(EP_PROPERTIES));
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
    public void testObservationSetProximateFoi() throws ServiceFailureException {
        Entity foi = model.buildFeature()
                .setId(1L)
                .build();
        Entity entity = model.buildObservation()
                .setProximateFoi(foi)
                .build();
        Assertions.assertSame(foi, entity.getProperty(model.npObservationProximateFoi, false));
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
    public void testThingSetDefinition() {
        String definition = "http://www.opengis.net/def/thing/Type/Building";
        Entity entity = model.buildThing()
                .setDefinition(definition)
                .build();
        Assertions.assertEquals(definition, entity.getProperty(EP_DEFINITION));
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

    @Test
    public void testDatastreamQueryObservationsThrowsWithoutService() {
        Entity entity = model.buildDatastream().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npDatastreamObservations));
    }

    @Test
    public void testDatastreamQueryUltimateFoisThrowsWithoutService() {
        Entity entity = model.buildDatastream().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npDatastreamUltimateFois));
    }

    @Test
    public void testFeatureQueryDatastreamsProximateThrowsWithoutService() {
        Entity entity = model.buildFeature().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npFeatureDatastreamsProximate));
    }

    @Test
    public void testFeatureQueryDatastreamsUltimateThrowsWithoutService() {
        Entity entity = model.buildFeature().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npFeatureDatastreamsUltimate));
    }

    @Test
    public void testFeatureQueryFeatureTypesThrowsWithoutService() {
        Entity entity = model.buildFeature().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npFeatureFeatureTypes));
    }

    @Test
    public void testFeatureQueryObservationsThrowsWithoutService() {
        Entity entity = model.buildFeature().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npFeatureObservations));
    }

    @Test
    public void testFeatureTypeQueryFeaturesThrowsWithoutService() {
        Entity entity = model.buildFeatureType().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npFeatureTypeFeatures));
    }

    @Test
    public void testHistoricalLocationQueryLocationsThrowsWithoutService() {
        Entity entity = model.buildHistoricalLocation().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npHistlocLocations));
    }

    @Test
    public void testLocationQueryHistoricalLocationsThrowsWithoutService() {
        Entity entity = model.buildLocation().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npLocationHistoricallocations));
    }

    @Test
    public void testLocationQueryThingsThrowsWithoutService() {
        Entity entity = model.buildLocation().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npLocationThings));
    }

    @Test
    public void testObservedPropertyQueryDatastreamsThrowsWithoutService() {
        Entity entity = model.buildObservedProperty().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npObspropDatastreams));
    }

    @Test
    public void testSensorQueryDatastreamsThrowsWithoutService() {
        Entity entity = model.buildSensor().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npSensorDatastreams));
    }

    @Test
    public void testThingQueryDatastreamsThrowsWithoutService() {
        Entity entity = model.buildThing().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npThingDatastreams));
    }

    @Test
    public void testThingQueryHistoricalLocationsThrowsWithoutService() {
        Entity entity = model.buildThing().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npThingHistoricallocations));
    }

    @Test
    public void testThingQueryLocationsThrowsWithoutService() {
        Entity entity = model.buildThing().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npThingLocations));
    }
}
