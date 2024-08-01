# Changelog

## Development Version 2.13

**Updates**
* Renamed may classes and moved constants around.
* Pulled SWE-Common classes internally.
* Improved methods of query() to allow null parameters.
* Added MQTT support.
* Improved DataRecord / TaskingParameter deserialisation.
* Bumped dependency versions.


## Release Version 2.12

**Updates**
* Renamed TypeSimpleSet to TypeCollection
* Always serialised null values if they are explicitly set
* Allow times to be null
* Bumped dependency versions.


## Release Version 2.11

**Updates**
* Fixed Entity hashCode method, allowing entities to be keys in Maps
* Bumped dependency versions.


## Release Version 2.10

**Updates**
* Fixed return type on Entity.getProperty for Entity navigation Properties.
* Bumped dependency versions.


## Release Version 2.9

**Updates**
* Fixed model initialisation when using external ModelRegistry.


## Release Version 2.8

**Updates**
* Fixed namespace not being set on EntityTypes.
* Fixed exception trying to auto load navlinks on unconnected entities.


## Release Version 2.7

**Updates**
* Added automatic model sniffing when creating a SensorThingsService without model.


## Release Version 2.6

**Updates**
* Bumped dependency versions.
* Implemented loading models from OData 4.01 CSDL ($metadata).


## Release Version 2.5

**Updates**
* Bumped dependency versions.
* Updated for the published STAplus standard.


## Release Version 2.4

**Updates**
* Fixed incorrect URL generation for sub DOAs like Datastream(x)/Observations.
* Simplified equals methods on Entity and EntitySet.


## Release Version 2.3

**Updates**
* Allow Service to be initialised with an existing ModelRegistry.


## Release Version 2.2

**Updates**
* Reworked PropertyTypes.
* Updated the model handling, service will initialise models.
* Added STAplus data model.
* Added checks when setting or getting properties, throw IllegalArgumentException
  when the EntityType does not have the property.


## Release Version 2.1

**Updates**
* Replaced the concept of Id with the concept of PrimaryKey.
  Primary keys point to one or more properties that can have any name.
* Fixed 403 being returned as 401.
* Bumped dependency versions.


## Release Version 2.0

**Updates**
* Complete redesign of FROST-Client to become data model agnostic.

