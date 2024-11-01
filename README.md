# Frost-Client [![Build Status](https://github.com/FraunhoferIOSB/FROST-Client/workflows/Maven%20Build/badge.svg)](https://github.com/FraunhoferIOSB/FROST-Client/actions) [![codecov](https://codecov.io/gh/FraunhoferIOSB/FROST-Client/branch/master/graph/badge.svg)](https://codecov.io/gh/FraunhoferIOSB/FROST-Client) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/e99823ab3a7541b085a9c9c48461d39f)](https://www.codacy.com/gh/FraunhoferIOSB/FROST-Client?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=FraunhoferIOSB/FROST-Client&amp;utm_campaign=Badge_Grade)

![FROST-Client Logo](https://raw.githubusercontent.com/hylkevds/FROST-Client-Dynamic/main/images/FROST-Client-darkgrey.png)

The **FR**aunhofer **O**pensource **S**ensor**T**hings-Client-Dynamic is a Java-based client
library for the [SensorThingsAPI](https://github.com/opengeospatial/sensorthings) and
other data models.
It aims to simplify development of SensorThings enabled client applications.

## Features

* CRUD operations
* Queries on entity sets
* Loading of referenced entities
* MultiDatastreams
* Tasking
* STA plus

## Unsupported

* Batch requests
* dataArray (for requesting observations)
* MQTT

## Using with maven

Add the dependency:
```xml
<dependency>
    <groupId>de.fraunhofer.iosb.ilt</groupId>
    <artifactId>FROST-Client-Dynamic</artifactId>
    <version>2.21</version>
</dependency>

```

## Using with gradle

Add the dependency:
```gradle
compile 'de.fraunhofer.iosb.ilt:FROST-Client-Dynamic:2.21'
```

## API

The `SensorThingsService` class is central to the library. An instance of it represents a SensorThings service and is identified by an URI.
This class needs to be initialised with a data model, but if initialised without a model it will try to figure out the model automatically.
Data models for the SensorThings API exist, but you can also create your own data models.

### CRUD operations

The source code below demonstrates the CRUD operations for Thing objects. Operations for other entities work similarly.

```java
SensorThingsV11Sensing modelSensing = new SensorThingsV11Sensing();
SensorThingsV11Tasking modelTasking = new SensorThingsV11Tasking();
URL serviceEndpoint = new URL("http://example.org/v1.1/");
SensorThingsService service = new SensorThingsService(modelSensing, modelTasking)
    .setBaseUrl(serviceEndpoint)
    .init();
```

```java
Entity thing = new Entity(modelSensing.etThing)
    .setProperty(SensorThingsV11Sensing.EP_NAME, "Thingything")
    .setProperty(SensorThingsV11Sensing.EP_DESCRIPTION, "I'm a thing!");
service.create(thing);

// get Thing with numeric id 1234
thing = service.dao(modelSensing.etThing).find(1234l);
// get Thing with String id ab12cd
thing = service.dao(modelSensing.etThing).find("ab12cd");

thing.setDescription("Things change...");
service.update(thing);

service.delete(thing);
```

### Entity Sets

Entity Sets are represented by instances of `EntityList<>`. The query parameters specified by the SensorThingsAPI standard can be applied to queries.

```java
EntitySet things = service.query(modelSensing.etThing)
                            .count()
                            .orderBy("description")
                            .select("name","id","description")
                            .filter("")
                            .expand()
                            .skip(5)
                            .top(10)
                            .list();

for (Entity thing : things) {
    System.out.println("So many things!");
}
```

Entity sets only load so many entities at a time, but the iterator will automatically
load more entities when more entities exist on the server. To get only the currently
loaded entities, use the `toList()` method to get the List<Entity> of currently
loaded entites.

```java
List<Entity> observations = service.query(modelSensing.etObservation)
                            .count()
                            .top(1000)
                            .list()
                            .toList();

for (Entity obs : observations) {
    // Only the loaded Observations...
    System.out.println("Observation " + obs.getId() + " has result " + obs.getResult());
}
```

Related entity sets can also be queried.
```java
// Get the thing with ID 1
Entity thing = service.dao(modelSensing.etThing).find(1l);

// Get the Datastreams of this Thing
EntitySet dataStreams = thing.query(modelSensing.npThingDatastreams).list();
for (Entity dataStream : dataStreams) {
    Entity sensor = dataStream.getProperty(modelSensing.npDatastreamSensor);
    System.out.println("dataStream " + dataStream.getId() + " has Sensor " + sensor.getId());
}

```


### Loading referenced objects

Loading referenced objects in one operation (and therefore in one request) is supported. The *$expand* option of the SensorThingsAPI standard is used internally.

```java
EntitySet things = service.query(modelSensing.etThing)
        .expand("Locations($select=name,encodingType,location)")
        .list();
for (entity Thing : things) {
    EntitySet locations = thing.getProperty(modelSensing.npThingLocations);
}
```


## Contributing

Contributions are welcome!

1.  Fork this repository
2.  Commit your changes
3.  Create a pull request

## License

The code and the documentation of this work is available under the MIT license.
