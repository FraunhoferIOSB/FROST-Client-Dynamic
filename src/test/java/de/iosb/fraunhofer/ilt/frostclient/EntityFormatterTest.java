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
package de.iosb.fraunhofer.ilt.frostclient;

import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_DESCRIPTION;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_ID;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_NAME;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_PROPERTIES;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_PHENOMENONTIME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_RESULT;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Tasking.EP_TASKINGPARAMETERS_TC;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Tasking.taskingParametersBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.json.serialize.JsonWriter;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Tasking;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInterval;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.UnitOfMeasurement;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.constraint.AllowedTokens;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.simple.Category;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.simple.Text;
import de.fraunhofer.iosb.ilt.frostclient.utils.CollectionsHelper;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geojson.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

public class EntityFormatterTest {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EntityFormatterTest.class.getName());

    private SensorThingsV11Sensing modelSensing;
    private SensorThingsV11Tasking modelTasking;
    private SensorThingsService service;

    @BeforeEach
    public void setUp() {
        modelSensing = new SensorThingsV11Sensing();
        modelTasking = new SensorThingsV11Tasking();
        try {
            service = new SensorThingsService(modelSensing, modelTasking)
                    .setBaseUrl(SensorThingsService.NULL_URL_V11)
                    .init();
        } catch (MalformedURLException ex) {
            LOGGER.error("Failed to set up.", ex);
        }
    }

    @Test
    public void writeThing_Basic_Success() throws IOException {
        String expResult = """
                {
                  "@iot.id": 1,
                  "name": "This thing is an oven.",
                  "description": "This thing is an oven.",
                  "properties": {
                    "owner": "John Doe",
                    "color": "Silver"
                  }
                }""";
        Entity entity = new Entity(modelSensing.etThing);
        entity.setProperty(EP_ID, 1L);
        entity.setProperty(EP_NAME, "This thing is an oven.");
        entity.setProperty(EP_DESCRIPTION, "This thing is an oven.");
        entity.setSelfLink("http://example.org/Observations/1");

        MapValue properties = CollectionsHelper.propertiesBuilder()
                .addItem("owner", "John Doe")
                .addItem("color", "Silver")
                .build();
        entity.setProperty(EP_PROPERTIES, properties);

        String json = JsonWriter.writeEntity(entity);
        assertTrue(jsonEqual(expResult, json));
    }

    @Test
    public void writeThing_Basic_StringId_Success() throws IOException {
        String expResult = """
                {
                  "@iot.id": "aStringAsId",
                  "name": "This thing is an oven.",
                  "description": "This thing is an oven.",
                  "properties": {
                    "owner": "John Doe",
                    "color": "Silver"
                  }
                }""";

        MapValue properties = CollectionsHelper.propertiesBuilder()
                .addItem("owner", "John Doe")
                .addItem("color", "Silver")
                .build();
        Entity entity = modelSensing.newThing("This thing is an oven.", "This thing is an oven.", properties);
        entity.setProperty(EP_ID, "aStringAsId");

        String json = JsonWriter.writeEntity(entity);
        assertTrue(jsonEqual(expResult, json));
    }

    @Test
    public void writeThing_CompletelyEmpty_Success() throws IOException {
        String expResult = "{}";
        Entity entity = new Entity(modelSensing.etThing);
        String json = JsonWriter.writeEntity(entity);
        assert (jsonEqual(expResult, json));
    }

    @Test
    public void writeThingWithLocation() throws IOException {
        String expResult = """
                {
                  "@iot.id": 1,
                  "name": "This thing is an oven.",
                  "description": "This thing is an oven.",
                  "properties": {},
                  "Locations": [
                    { "@iot.id": 1 }
                  ]
                }""";
        Entity entity = modelSensing.newThing("This thing is an oven.", "This thing is an oven.", new HashMap<>());
        entity.setProperty(EP_ID, 1L);

        Entity location = new Entity(modelSensing.etLocation)
                .setPrimaryKeyValues(1L);
        entity.getProperty(modelSensing.npThingLocations).add(location);

        String json = JsonWriter.writeEntity(entity);
        assertTrue(jsonEqual(expResult, json));
    }

    @Test
    public void testNestedLocation() throws IOException {
        LOGGER.info("  testNestedLocation");
        String expResult = """
                {
                  "name": "TestThing",
                  "description": "A Thing for testing.",
                  "Locations": [
                    {
                      "name": "TestLocation",
                      "description": "The location of the TestThing",
                      "encodingType": "application/vnd.geo+json",
                      "location": {
                        "type": "Point",
                        "coordinates": [8.8, 49.9]
                      }
                    }
                  ]
                }""";

        Entity entity = modelSensing.newThing("TestThing", "A Thing for testing.");
        Entity location = modelSensing.newLocation("TestLocation", "The location of the TestThing", "application/vnd.geo+json", new Point(8.8, 49.9));
        entity.getProperty(modelSensing.npThingLocations).add(location);

        String json = JsonWriter.writeEntity(entity);
        assertTrue(jsonEqual(expResult, json));
    }

    @Test
    public void writeLocation_GeoJson() throws Exception {
        String expResult = """
                {
                    "@iot.id": 1,
                    "name": "OvenLocation",
                    "description": "The location of an oven.",
                    "encodingType": "application/geo+json",
                    "location": {
                        "type": "Point",
                        "coordinates": [-114.05, 51.05]
                    }
                }""";
        Entity entity = modelSensing.newLocation("OvenLocation", "The location of an oven.", new Point(-114.05, 51.05))
                .setProperty(EP_ID, 1L);

        String json = JsonWriter.writeEntity(entity);
        assertTrue(jsonEqual(expResult, json));

        Entity parsed = service.getJsonReader().parseEntity(modelSensing.etLocation, expResult);
        assertEquals(entity, parsed);
    }

    @Test
    public void writeLocation_String() throws Exception {
        String expResult = """
                {
                    "@iot.id": 1,
                    "name": "OvenLocation",
                    "description": "The location of an oven.",
                    "encodingType": "text/plain",
                    "location": "Third house on the left."
                }""";
        Entity entity = modelSensing.newLocation("OvenLocation", "The location of an oven.", "text/plain", "Third house on the left.")
                .setProperty(EP_ID, 1L);

        String json = JsonWriter.writeEntity(entity);
        assertTrue(jsonEqual(expResult, json));

        Entity parsed = service.getJsonReader().parseEntity(modelSensing.etLocation, expResult);
        assertEquals(entity, parsed);
    }

    @Test
    public void writeEverything() throws Exception {
        String expResult = """
                {
                    "description": "thing 1",
                    "name": "thing name 1",
                    "properties": {
                        "reference": "first"
                    },
                    "Locations": [
                        {
                            "description": "location 1",
                            "name": "location name 1",
                            "location": {
                                "type": "Point",
                                "coordinates": [-117.05, 51.05]
                            },
                            "encodingType": "application/geo+json"
                        }
                    ],
                    "Datastreams": [
                        {
                            "unitOfMeasurement": {
                                "name": "Lumen",
                                "symbol": "lm",
                                "definition": "http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html/Lumen"
                            },
                            "description": "datastream 1",
                            "name": "datastream name 1",
                            "observationType": "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement",
                            "ObservedProperty": {
                                "name": "Luminous Flux",
                                "definition": "http://www.qudt.org/qudt/owl/1.0.0/quantity/Instances.html/LuminousFlux",
                                "description": "observedProperty 1"
                            },
                            "Sensor": {
                                "description": "sensor 1",
                                "name": "sensor name 1",
                                "encodingType": "application/text",
                                "metadata": "Light flux sensor"
                            },
                            "Observations": [
                                {
                                    "phenomenonTime": "2015-03-03T00:00:00Z",
                                    "result": 3
                                },
                                {
                                    "phenomenonTime": "2015-03-04T00:00:00Z",
                                    "result": 4
                                }
                            ]
                        },
                        {
                            "unitOfMeasurement": {
                                "name": "Centigrade",
                                "symbol": "C",
                                "definition": "http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html/Lumen"
                            },
                            "description": "datastream 2",
                            "name": "datastream name 2",
                            "observationType": "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement",
                            "ObservedProperty": {
                                "name": "Tempretaure",
                                "definition": "http://www.qudt.org/qudt/owl/1.0.0/quantity/Instances.html/Tempreture",
                                "description": "observedProperty 2"
                            },
                            "Sensor": {
                                "description": "sensor 2",
                                "name": "sensor name 2",
                                "encodingType": "application/text",
                                "metadata": "Tempreture sensor"
                            },
                            "Observations": [
                                {
                                    "phenomenonTime": "2015-03-05T00:00:00Z",
                                    "result": 5
                                },
                                {
                                    "phenomenonTime": "2015-03-06T00:00:00Z",
                                    "result": 6
                                }
                            ]
                        }
                    ]
                }""";
        Entity thing = modelSensing.newThing("thing name 1", "thing 1");
        MapValue properties = CollectionsHelper.propertiesBuilder()
                .addItem("reference", "first")
                .build();
        thing.setProperty(EP_PROPERTIES, properties);

        Entity location = modelSensing.newLocation("location name 1", "location 1", new Point(-117.05, 51.05));
        thing.getProperty(modelSensing.npThingLocations).add(location);

        UnitOfMeasurement um1 = new UnitOfMeasurement("Lumen", "lm", "http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html/Lumen");
        Entity ds1 = modelSensing.newDatastream("datastream name 1", "datastream 1", um1);
        ds1.setProperty(modelSensing.npDatastreamObservedproperty, modelSensing.newObservedProperty("Luminous Flux", "http://www.qudt.org/qudt/owl/1.0.0/quantity/Instances.html/LuminousFlux", "observedProperty 1"));
        ds1.setProperty(modelSensing.npDatastreamSensor, modelSensing.newSensor("sensor name 1", "sensor 1", "application/text", "Light flux sensor"));
        ds1.getProperty(modelSensing.npDatastreamObservations).add(modelSensing.newObservation(3L, ZonedDateTime.parse("2015-03-03T00:00:00Z")));
        ds1.getProperty(modelSensing.npDatastreamObservations).add(modelSensing.newObservation(4L, ZonedDateTime.parse("2015-03-04T00:00:00Z")));
        thing.getProperty(modelSensing.npThingDatastreams).add(ds1);

        UnitOfMeasurement um2 = new UnitOfMeasurement("Centigrade", "C", "http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html/Lumen");
        Entity ds2 = modelSensing.newDatastream("datastream name 2", "datastream 2", um2);
        ds2.setProperty(modelSensing.npDatastreamObservedproperty, modelSensing.newObservedProperty("Tempretaure", "http://www.qudt.org/qudt/owl/1.0.0/quantity/Instances.html/Tempreture", "observedProperty 2"));
        ds2.setProperty(modelSensing.npDatastreamSensor, modelSensing.newSensor("sensor name 2", "sensor 2", "application/text", "Tempreture sensor"));
        ds2.getProperty(modelSensing.npDatastreamObservations).add(modelSensing.newObservation(5L, ZonedDateTime.parse("2015-03-05T00:00:00Z")));
        ds2.getProperty(modelSensing.npDatastreamObservations).add(modelSensing.newObservation(6L, ZonedDateTime.parse("2015-03-06T00:00:00Z")));
        thing.getProperty(modelSensing.npThingDatastreams).add(ds2);

        String json = JsonWriter.writeEntity(thing);
        assertTrue(jsonEqual(expResult, json));

        Entity parsed = service.getJsonReader().parseEntity(modelSensing.etThing, expResult);
        assertEquals(thing, parsed);
    }

    @Test
    public void writeObservationDateTime() throws IOException {
        String expResult = """
                {
                    "@iot.id": 1,
                    "phenomenonTime": "2014-12-31T11:59:59Z",
                    "result": 70.40
                }""";
        Entity entity = modelSensing.newObservation(new BigDecimal("70.40"), ZonedDateTime.parse("2014-12-31T11:59:59Z"))
                .setProperty(EP_ID, 1L);

        String json = JsonWriter.writeEntity(entity);
        assertTrue(jsonEqual(expResult, json));

        Entity parsed = service.getJsonReader().parseEntity(modelSensing.etObservation, expResult);
        assertEquals(entity, parsed);

        String json2 = JsonWriter.writeEntity(parsed);
        assertTrue(jsonEqual(expResult, json2));
    }

    @Test
    public void writeObservationInterval() throws IOException {
        String expResult = """
                {
                    "@iot.id": 1,
                    "phenomenonTime": "2014-12-31T11:59:59Z/2014-12-31T12:01:01Z",
                    "result": 70.40
                }""";
        Entity entity = modelSensing.newObservation(new BigDecimal("70.40"), TimeInterval.parse("2014-12-31T11:59:59Z/2014-12-31T12:01:01Z"))
                .setProperty(EP_ID, 1L)
                .setSelfLink("http://example.org/Observations/1");

        String json = JsonWriter.writeEntity(entity);
        assertTrue(jsonEqual(expResult, json));

        Entity parsed = service.getJsonReader().parseEntity(modelSensing.etObservation, expResult);
        String json2 = JsonWriter.writeEntity(parsed);
        assertTrue(jsonEqual(expResult, json2));
    }

    @Test
    public void writeObservationNull() throws IOException {
        String expResult = """
                {
                    "@iot.id": 1,
                    "phenomenonTime": "2014-12-31T11:59:59Z",
                    "result": null
                }""";
        Entity entity = modelSensing.newObservation()
                .setProperty(EP_ID, 1L)
                .setProperty(EP_RESULT, null)
                .setProperty(EP_PHENOMENONTIME, TimeValue.create(ZonedDateTime.parse("2014-12-31T11:59:59Z")));

        String json = JsonWriter.writeEntity(entity);
        assertTrue(jsonEqual(expResult, json));

        Entity parsed = service.getJsonReader().parseEntity(modelSensing.etObservation, expResult);
        String json2 = JsonWriter.writeEntity(parsed);
        assertTrue(jsonEqual(expResult, json2));
    }

    @Test
    public void writeObservationNoResult() throws IOException {
        String expResult = """
                {
                    "phenomenonTime": "2014-12-31T11:59:59Z"
                }""";
        Entity entity = modelSensing.newObservation()
                .setProperty(EP_PHENOMENONTIME, TimeValue.create(ZonedDateTime.parse("2014-12-31T11:59:59Z")));

        String json = JsonWriter.writeEntity(entity);
        assertTrue(jsonEqual(expResult, json));

        Entity parsed = service.getJsonReader().parseEntity(modelSensing.etObservation, expResult);
        String json2 = JsonWriter.writeEntity(parsed);
        assertTrue(jsonEqual(expResult, json2));
    }

    @Test
    public void writeObservationZero() throws IOException {
        String expResult = """
                {
                    "result": 0.0
                }""";
        Entity entity = modelSensing.newObservation()
                .setProperty(EP_RESULT, new BigDecimal("0.0"));

        String json = JsonWriter.writeEntity(entity);
        assertTrue(jsonEqual(expResult, json));

        Entity parsed = service.getJsonReader().parseEntity(modelSensing.etObservation, expResult);
        assertEquals(entity.getProperty(EP_RESULT), parsed.getProperty(EP_RESULT));

        String json2 = JsonWriter.writeEntity(parsed);
        assertTrue(jsonEqual(expResult, json2));
    }

    private boolean jsonEqual(String string1, String string2) {
        ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        try {
            JsonNode json1 = mapper.readTree(string1);
            JsonNode json2 = mapper.readTree(string2);
            return json1.equals(json2);
        } catch (IOException ex) {
            Logger.getLogger(EntityFormatterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Test
    public void writeTaskingParameter() throws IOException {
        String expResult = """
                {
                    "@iot.id": 1,
                    "name": "Control Light",
                    "description": "Turn the light on and off, as well as specifying light color.",
                    "taskingParameters": {
                        "type": "DataRecord",
                        "fields": [
                            {
                                "name": "status",
                                "label": "On/Off status",
                                "description": "Specifies turning the light On or Off",
                                "type": "Category",
                                "constraint": {
                                    "type": "AllowedTokens",
                                    "value": [
                                        "on", "off"
                                    ]
                                }
                            },
                            {
                                "name": "color",
                                "label": "Light Color",
                                "description": "Specifies the light color in RGB HEX format. Example: #FF11A0",
                                "type": "Text",
                                "constraint": {
                                    "type": "AllowedTokens",
                                    "pattern": "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"
                                }
                            }
                        ]
                    }
                }""";
        Entity entity = modelTasking.newTaskingCapability("Control Light", "Turn the light on and off, as well as specifying light color.")
                .setProperty(EP_ID, 1L)
                .setProperty(EP_TASKINGPARAMETERS_TC, taskingParametersBuilder()
                        .taskingParameter(
                                "status",
                                new Category()
                                        .setLabel("On/Off status")
                                        .setDescription("Specifies turning the light On or Off")
                                        .setConstraint(new AllowedTokens("on", "off")))
                        .taskingParameter(
                                "color",
                                new Text()
                                        .setLabel("Light Color")
                                        .setDescription("Specifies the light color in RGB HEX format. Example: #FF11A0")
                                        .setConstraint(new AllowedTokens("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")))
                        .build());

        String json = JsonWriter.writeEntity(entity);
        assertTrue(jsonEqual(expResult, json));

        Entity parsed = service.getJsonReader().parseEntity(modelTasking.etTaskingCapability, expResult);
        assertEquals(entity, parsed);

        String json2 = JsonWriter.writeEntity(parsed);
        assertTrue(jsonEqual(expResult, json2));
    }

}
