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
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_RESULT;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing.EP_RESULTTIME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Tasking.EP_TASKINGPARAMETERS_TC;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Tasking.taskingParametersBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Tasking;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.constraint.AllowedValues;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.simple.Count;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.simple.Text;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EntityReaderTest {

    private SensorThingsV11Sensing modelSensing;
    private SensorThingsV11Tasking modelTasking;
    private SensorThingsService service;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        modelSensing = new SensorThingsV11Sensing();
        modelTasking = new SensorThingsV11Tasking();
        service = new SensorThingsService(modelSensing, modelTasking)
                .setBaseUrl(SensorThingsService.NULL_URL_V11)
                .init();
    }

    @Test
    public void readEntity() throws IOException {
        String json = """
                {
                    "phenomenonTime": "2016-01-07T02:00:00.000Z",
                    "resultTime": null,
                    "result": 0.15,
                    "Datastream@iot.navigationLink": "https://server.de/SensorThingsService/v1.0/Observations(7179373)/Datastream",
                    "FeatureOfInterest@iot.navigationLink": "https://server.de/SensorThingsService/v1.0/Observations(7179373)/FeatureOfInterest",
                    "@iot.id": 7179373,
                    "@iot.selfLink": "https://server.de/SensorThingsService/v1.0/Observations(7179373)"
                }""";

        Entity observation = service.getJsonReader().parseEntity(modelSensing.etObservation, json);

        Entity expected = modelSensing.newObservation(BigDecimal.valueOf(0.15), ZonedDateTime.parse("2016-01-07T02:00:00.000Z"))
                .setProperty(EP_ID, 7179373L)
                .setProperty(EP_RESULTTIME, null)
                .setSelfLink("https://server.de/SensorThingsService/v1.0/Observations(7179373)");

        assertEquals(expected, observation);
    }

    @Test
    public void readEntityList() throws IOException {
        String json = """
                {
                    "@iot.nextLink" : "https://server.de/SensorThingsService/v1.0/Things?$top=2&$skip=14&$expand=Datastreams%28%24top%3D2%3B%24count%3Dtrue%29",
                    "value" : [
                        {
                            "name" : "Recoaro 1000",
                            "description" : "Weather station Recoaro 1000",
                            "Datastreams@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/Things(19)/Datastreams",
                            "Datastreams@iot.count" : 6,
                            "Datastreams" : [
                                {
                                    "name" : "Air Temperature Recoaro 1000",
                                    "description" : "The Air Temperature at Recoaro 1000",
                                    "observationType" : "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement",
                                    "phenomenonTime" : "2010-01-01T00:00:00.000Z/2019-01-13T06:00:00.000Z",
                                    "unitOfMeasurement" : {
                                        "name" : "degree celcius",
                                        "symbol" : "\u00b0C",
                                        "definition" : "ucum:Cel"
                                    },
                                    "Observations" : [
                                        {"result": 0.0}
                                    ],
                                    "@iot.id" : 66,
                                    "@iot.selfLink" : "https://server.de/SensorThingsService/v1.0/Datastreams(66)"
                                }, {
                                    "name" : "Precipitation Recoaro 1000",
                                    "description" : "The Precipitation at Recoaro 1000",
                                    "observationType" : "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement",
                                    "phenomenonTime" : "2010-01-01T00:00:00.000Z/2019-01-13T06:00:00.000Z",
                                    "unitOfMeasurement" : {
                                        "name" : "mm/h",
                                        "symbol" : "mm/h",
                                        "definition" : "ucum:mm/h"
                                    },
                                    "@iot.id" : 130,
                                    "@iot.selfLink" : "https://server.de/SensorThingsService/v1.0/Datastreams(130)"
                                }
                            ],
                            "Datastreams@iot.nextLink" : "https://server.de/SensorThingsService/v1.0/Things(19)/Datastreams?$top=2&$skip=2&$count=true",
                            "MultiDatastreams@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/Things(19)/MultiDatastreams",
                            "Locations@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/Things(19)/Locations",
                            "HistoricalLocations@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/Things(19)/HistoricalLocations",
                            "@iot.id" : 19,
                            "@iot.selfLink" : "https://server.de/SensorThingsService/v1.0/Things(19)"
                        }, {
                            "name" : "Valdagno",
                            "description" : "Weather station Valdagno",
                            "Datastreams@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/Things(20)/Datastreams",
                            "Datastreams" : [],
                            "Datastreams@iot.count" : 7,
                            "Datastreams@iot.nextLink" : "https://server.de/SensorThingsService/v1.0/Things(20)/Datastreams?$top=2&$skip=2&$count=true",
                            "MultiDatastreams@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/Things(20)/MultiDatastreams",
                            "Locations@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/Things(20)/Locations",
                            "HistoricalLocations@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/Things(20)/HistoricalLocations",
                            "@iot.id" : 20,
                            "@iot.selfLink" : "https://server.de/SensorThingsService/v1.0/Things(20)"
                        }
                    ]
                }""";

        EntitySet things = service.getJsonReader().parseEntitySet(modelSensing.etThing, json);

        assertEquals("https://server.de/SensorThingsService/v1.0/Things?$top=2&$skip=14&$expand=Datastreams%28%24top%3D2%3B%24count%3Dtrue%29", things.getNextLink());
        List<Entity> thingList = things.toList();
        Entity thing = thingList.get(0);
        assertEquals(19L, thing.getProperty(EP_ID));
        assertEquals("Recoaro 1000", thing.getProperty(EP_NAME));
        assertEquals("Weather station Recoaro 1000", thing.getProperty(EP_DESCRIPTION));

        EntitySet datastreams = thing.getProperty(modelSensing.npThingDatastreams);
        assertEquals("https://server.de/SensorThingsService/v1.0/Things(19)/Datastreams?$top=2&$skip=2&$count=true", datastreams.getNextLink());
        assertEquals(6L, datastreams.getCount());

        List<Entity> dsList = datastreams.toList();
        Entity datastream = dsList.get(0);
        assertEquals(66L, datastream.getPrimaryKeyValues()[0]);
        assertEquals("Air Temperature Recoaro 1000", datastream.getProperty(EP_NAME));
        assertEquals("The Air Temperature at Recoaro 1000", datastream.getProperty(EP_DESCRIPTION));

        Object result = datastream.getProperty(modelSensing.npDatastreamObservations).toList().get(0).getProperty(EP_RESULT);
        assertEquals(new BigDecimal("0.0"), result);

        datastream = dsList.get(1);
        assertEquals(130L, datastream.getProperty(EP_ID));
        assertEquals("Precipitation Recoaro 1000", datastream.getProperty(EP_NAME));
        assertEquals("The Precipitation at Recoaro 1000", datastream.getProperty(EP_DESCRIPTION));

        thing = thingList.get(1);
        assertEquals(20L, thing.getProperty(EP_ID));
        assertEquals("Valdagno", thing.getProperty(EP_NAME));
        assertEquals("Weather station Valdagno", thing.getProperty(EP_DESCRIPTION));

        datastreams = thing.getProperty(modelSensing.npThingDatastreams);
        assertEquals("https://server.de/SensorThingsService/v1.0/Things(20)/Datastreams?$top=2&$skip=2&$count=true", datastreams.getNextLink());
        assertEquals(7L, datastreams.getCount());

    }

    @Test
    public void readEmptyEntityList() throws IOException {
        String json = """
                {
                    "value" : [ ]
                }""";

        EntitySet things = service.getJsonReader().parseEntitySet(modelSensing.etThing, json);
        assertEquals(null, things.getNextLink());
        List<Entity> thingList = things.toList();

        assertTrue(thingList.isEmpty());
    }

    @Test
    public void readTaskingCapabilities() throws IOException {
        String json = """
                {
                    "name" : "createNewVA",
                    "description" : "Virtual Actuator Server, starts new Virtual Actuators",
                    "taskingParameters" : {
                        "type" : "DataRecord",
                        "field" : [
                            {
                                "type" : "Text",
                                "label" : "Aktor-Name",
                                "description" : "Name des neuen virtuellen Aktors",
                                "name" : "vaName"
                            }, {
                                "type" : "Text",
                                "label" : "Aktor-Beschreibung",
                                "description" : "Beschreibung des neuen virtuellen Aktors",
                                "name" : "vaDescription"
                            }
                        ]
                    },
                    "Actuator@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/Actuator",
                    "Thing@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/TaskingCapabilities(1)/Thing",
                    "Tasks@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/TaskingCapabilities(1)/Tasks",
                    "@iot.id" : 1,
                    "@iot.selfLink" : "https://server.de/SensorThingsService/v1.0/TaskingCapabilities(1)"
                }""";

        Entity taskingCap = service.getJsonReader().parseEntity(modelTasking.etTaskingCapability, json);

        Text vaName = new Text();
        vaName.setLabel("Aktor-Name");
        vaName.setDescription("Name des neuen virtuellen Aktors");

        Text vaDescription = new Text();
        vaDescription.setLabel("Aktor-Beschreibung");
        vaDescription.setDescription("Beschreibung des neuen virtuellen Aktors");

        Entity expected = modelTasking.newTaskingCapability()
                .setProperty(EP_NAME, "createNewVA")
                .setProperty(EP_DESCRIPTION, "Virtual Actuator Server, starts new Virtual Actuators")
                .setProperty(EP_TASKINGPARAMETERS_TC, taskingParametersBuilder()
                        .taskingParameter("vaName", vaName)
                        .taskingParameter("vaDescription", vaDescription)
                        .build())
                .setProperty(EP_ID, 1L);
        expected.setSelfLink("https://server.de/SensorThingsService/v1.0/TaskingCapabilities(1)");

        assertEquals(expected, taskingCap);
    }

    @Test
    public void readTaskingCapabilitiesWithConstraint() throws IOException {
        String json = """
                {
                    "name": "DatastreamCopierCapability",
                    "description": "Copies Observations from one Datastream to another",
                    "taskingParameters": {
                        "type": "DataRecord",
                        "fields": [
                            {
                                "type": "Count",
                                "name": "sourceDatastream",
                                "label": "Source Datastream",
                                "description": "ID of the datastream from which the observations should be taken.",
                                "constraint": {
                                      "type": "AllowedValues",
                                      "interval": [[0, 10000]]
                                }
                            },
                            {
                                "type": "Count",
                                "name": "destinationDatastream",
                                "label": "Destination Datastream",
                                "description": "ID of the datastream to which the observations should be copied.",
                                "constraint": {
                                    "type": "AllowedValues",
                                    "interval": [[0, 10000]]
                                }
                            }
                        ]
                    },
                    "Actuator@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/Actuator",
                    "Thing@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/TaskingCapabilities(1)/Thing",
                    "Tasks@iot.navigationLink" : "https://server.de/SensorThingsService/v1.0/TaskingCapabilities(1)/Tasks",
                    "@iot.id" : 1,
                    "@iot.selfLink" : "https://server.de/SensorThingsService/v1.0/TaskingCapabilities(1)"
                }""";

        Entity taskingCap = service.getJsonReader().parseEntity(modelTasking.etTaskingCapability, json);

        List<List<BigDecimal>> interval = new ArrayList<>();
        List<BigDecimal> intervallItem = new ArrayList<>();
        intervallItem.add(new BigDecimal(0));
        intervallItem.add(new BigDecimal(10000));
        interval.add(intervallItem);
        AllowedValues allowedValues = new AllowedValues()
                .setInterval(interval);

        Count sourceDS = new Count();
        sourceDS.setLabel("Source Datastream");
        sourceDS.setDescription("ID of the datastream from which the observations should be taken.");
        sourceDS.setConstraint(allowedValues);

        Count destDS = new Count();
        destDS.setLabel("Destination Datastream");
        destDS.setDescription("ID of the datastream to which the observations should be copied.");
        destDS.setConstraint(allowedValues);

        Entity expected = modelTasking.newTaskingCapability()
                .setProperty(EP_NAME, "DatastreamCopierCapability")
                .setProperty(EP_DESCRIPTION, "Copies Observations from one Datastream to another")
                .setProperty(EP_TASKINGPARAMETERS_TC, taskingParametersBuilder()
                        .taskingParameter("sourceDatastream", sourceDS)
                        .taskingParameter("destinationDatastream", destDS)
                        .build())
                .setProperty(EP_ID, 1L)
                .setSelfLink("https://server.de/SensorThingsService/v1.0/TaskingCapabilities(1)");

        assertEquals(expected, taskingCap);
    }
}
