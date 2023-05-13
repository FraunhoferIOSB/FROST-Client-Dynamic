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
    <version>2.0-SNAPSHOT</version>
</dependency>

```

## Using with gradle

Add the dependency:
```gradle
compile 'de.fraunhofer.iosb.ilt:FROST-Client-Dynamic:2.0-SNAPSHOT'
```

## API

The `SensorThingsService` class is central to the library. An instance of it represents a SensorThings service and is identified by an URI.
This class needs to be initialised with a data model.
Data models for the SensorThings API exist, but you can also create your own data models.

### CRUD operations

The source code below demonstrates the CRUD operations for Thing objects. Operations for other entities work similarly.

```java
SensorThingsSensingV11 modelSensing = new SensorThingsSensingV11();
SensorThingsTaskingV11 modelTasking = new SensorThingsTaskingV11(modelSensing);
URL serviceEndpoint = new URL("http://example.org/v1.0/");
SensorThingsService service = new SensorThingsService(modelTasking.getModelRegistry(), serviceEndpoint);
```

```java
Entity thing = new Entity(modelSensing.etThing)
    .setProperty(SensorThingsSensingV11.EP_NAME, "Thingything")
    .setProperty(SensorThingsSensingV11.EP_DESCRIPTION, "I'm a thing!")
service.create(thing);

// get Thing with numeric id 1234
thing = service.things().find(1234l);
// get Thing with String id ab12cd
thing = service.things().find("ab12cd");

thing.setDescription("Things change...");
service.update(thing);

service.delete(thing);
```

### Entity Sets

Entity Sets are represented by instances of `EntityList<>`. The query parameters specified by the SensorThingsAPI standard can be applied to queries.

```java
EntityList<Thing> things = service.things()
                            .query()
                            .count()
                            .orderBy("description")
                            .select("name","id","description")
                            .filter("")
                            .expand()
                            .skip(5)
                            .top(10)
                            .list();

for (Thing thing : things) {
    System.out.println("So many things!");
}
```

Entity sets only load so many entities at a time. If you want to get *all* entities,
and there are more entities than the $top parameter allows you get in one request, you can
use the `EntityList.fullIterator();` Iterator.

```java
EntityList<Observations> observations = service.observations()
                            .query()
                            .count()
                            .top(1000)
                            .list();

Iterator<Observation> i = observations.fullIterator();
while (i.hasNext()) {
    Observation obs = i.next();
    System.out.println("Observation " + obs.getId() + " has result " + obs.getResult());
}
```

Related entity sets can also be queried.
```java
// Get the thing with ID 1
thing = service.things().find(1l);

// Get the Datastreams of this Thing
EntityList<Datastream> dataStreams = thing.datastreams().query().list();
for (Datastream dataStream : dataStreams) {
    Sensor sensor = dataStream.getSensor();
    System.out.println("dataStream " + dataStream.getId() + " has Sensor " + sensor.getId());
}

```


### Loading referenced objects

Loading referenced objects in one operation (and therefore in one request) is supported. The *$expand* option of the SensorThingsAPI standard is used internally.

```java
Thing thing = service.things().find(1l,
                Expansion.of(EntityType.THING)
                .with(ExpandedEntity.from(EntityType.LOCATIONS)));
EntityList<Location> locations = thing.getLocations();
```

Or using a simple string to define the expand:

```java
EntityList<Thing> things = service.things().query()
        .expand("Locations($select=name,encodingType,location)")
        .list();
for (Iterator<Thing> it = things.fullIterator(); it.hasNext();) {
    Thing thing = it.next();
    EntityList<Location> locations = thing.getLocations();
}
```


### DataArray for Observation creation

Using DataArrays for creating Observations is more efficient, since only one http request
 is done, and the observations are more efficiently encoded in this request, so the request
 is smaller than the sum of the separate, normal requests.

```java
Set<DataArrayValue.Property> properties = new HashSet<>();
properties.add(DataArrayValue.Property.Result);
properties.add(DataArrayValue.Property.PhenomenonTime);

DataArrayValue dav1 = new DataArrayValue(datastream1, properties);
dav1.addObservation(observation1);
dav1.addObservation(observation2);
dav1.addObservation(observation3);

DataArrayValue dav2 = new DataArrayValue(multiDatastream1, properties);
dav2.addObservation(observation4);
dav2.addObservation(observation5);
dav2.addObservation(observation6);

DataArrayDocument dad = new DataArrayDocument();
dad.addDataArrayValue(dav1);
dad.addDataArrayValue(dav2);

service.create(dad);

```

### Subscription via MQTT

To be notified about changes to entities or entity sets you can use MQTT subscriptions.

```java
// subscribe directly to an entity, topic: [version]/Datastreams(1)
MqttSubscription datastreamDirectSubscription = DatastreamBuilder
		.builder()
		.service(service)
		.id(new IdLong(1L))
		.build()
		.subscribe(x -> System.out.println(x.getId()));
service.unsubscribe(datastreamDirectSubscription);

// subscribe to an entity relative to another, topic: [version]/Datastreams(1)/Thing
MqttSubscription thingViaDatastreamSubscription = DatastreamBuilder
		.builder()
		.service(service)
		.id(new IdLong(1L))
		.build()
		.<Thing>subscribeRelative(x -> System.out.println(x.getId()), EntityType.THING);
service.unsubscribe(thingViaDatastreamSubscription);

// subscribe directly to an entity set, topic: [version]/Observations
MqttSubscription observationsDirectSubscription = service
		.observations()
		.subscribe(x -> System.out.println(x.getId()));
service.unsubscribe(observationsDirectSubscription);

// subscribe directly to an entity set including only selected properties in the response,
// topic: [version]/Observations?$select=result,resultTime
MqttSubscription observationsDirectWithSelectSubscription = service
		.observations()
		.subscribe(x -> System.out.println(x.getId()), EntityProperty.RESULT, EntityProperty.RESULTTIME);
service.unsubscribe(observationsDirectWithSelectSubscription);

// subscribe directly to an entity set but locally filter incoming notifications before calling the handler function
// here: only fire handler if observation was man since yesterday
MqttSubscription observationsDirectWithFilterSubscription = service
		.observations()
		.subscribe(x -> x.getResultTime().isAfter(ZonedDateTime.now().minusDays(1)),
				x -> System.out.println(x.getId()),
				EntityProperty.RESULT, EntityProperty.RESULTTIME);
service.unsubscribe(observationsDirectWithFilterSubscription);

// subscribe to an entity relative to another, topic: [version]/Datastreams(1)/Observations
MqttSubscription observationsViaDatastreamSubscription = DatastreamBuilder
		.builder()
		.service(service)
		.id(new IdLong(1L))
		.build()
		.<Observation>subscribeRelative(x -> System.out.println(x.getId()), EntityType.OBSERVATIONS);
service.unsubscribe(observationsViaDatastreamSubscription);
```


## Contributing

Contributions are welcome!

1.  Fork this repository
2.  Commit your changes
3.  Create a pull request

## License

The code and the documentation of this work is available under the MIT license.
