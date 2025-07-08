# Changelog

## Version 2.30-SNAPSHOT

**Updates**
* Fixed entities received over MQTT not having a service set.
* Added resubscribe after reconnect.


## Version 2.29

**Updates**
* Added entity creation methods for Projects plugin.
* Fixed data model for Projects plugin.
* Made ModelRegistry.initFinalise fluent.


## Version 2.28

**Updates**
* Moved handling of environment variables to a separate library: [Settings](https://github.com/FraunhoferIOSB/Settings/).
* Fixed setting auth method from environment variables


## Version 2.27

**Updates**
* Bumped dependency versions.
* Added experimental feature to generate JSON-Schema from SWE-Common.


## Version 2.26

**Updates**
* Bumped dependency versions.
* Improved MQTT disconnect logic.
* Changed type of Sensor and Actuator metadata to Any (EDM.Untyped).


## Version 2.25

**Updates**
* Bumped dependency versions.
* Added custom attribute `secret` to AbstractDataComponent to flag to clients that values should be encrypted or stored securely.
  Secret fields are treated as optional.


## Version 2.24

**Updates**
* Bumped dependency versions.
* Set automaticReconnect on MQTT connect options.
* Added option to EntityCache to set how to deal with duplicates.


## Version 2.23

**Updates**
* Bumped dependency versions.
* Changed type of Obseration/parameters to MapValue.
* Made setVersion fluent.
* Added Expand API.
* Changed EntitySet iterator to be repeatable.


## Version 2.22

**Updates**
* Added `getService()` method to Dao interface.


## Version 2.21

**Updates**
* Fixed SWE-Common restrictions classes.
* Fixed null in log messages when logPrefix not set.
* Added isNullOrEmpty for Map.


## Version 2.20

**Updates**
* Added deserialisation support for Time4J Moment.
* Added validation method to SWE-Common implementation.


## Version 2.19

**Updates**
* Allow EntityType-specific custom toString methods for Entity.
* Allow EntityType-specific custom display methods for Entity, to get a string for displaying in a GUI.
* Fixed Auth method not initialised correctly. The AuthMethod can now be set on the service before calling init.
* Fixed relations in Projects data model.


## Version 2.18

**Updates**
* Added MQTT methods.
* Improved version detection for OData.
* Added data model for Projects extension: https://fraunhoferiosb.github.io/FROST-Server/extensions/DataModel-Projects.html
* Bumped dependency versions.


## Version 2.17

**Updates**
* Added convenience methods to MapValue
* Bumped dependency versions.


## Version 2.16

**Updates**
* Added ChangingStatusLogger for logging statistics.
* Improved EntityCache
* Bumped dependency versions.


## Version 2.15

**Updates**
* Introduced PkValue object to wrap Object[] for primary key values.
* Fixed enum deserialisation.
* Added FrostUtils and Cache classes.
* Made ConfigProvider more dynamic.
* Added utility for instantiating classes.
* Bumped dependency versions.


## Version 2.14

**Updates**
* Allow setting auth using environment variables (basic auth only for now).


## Version 2.13

**Updates**
* Renamed may classes and moved constants around.
* Pulled SWE-Common classes internally.
* Improved methods of query() to allow null parameters.
* Added MQTT support.
* Improved DataRecord / TaskingParameter deserialisation.
* Some settings can now be set using environment variables.
* The service BaseUrl must now be set using `setBaseUrl`.
* The service must now be initialised by calling `init()`.
* Bumped dependency versions.


## Version 2.12

**Updates**
* Renamed TypeSimpleSet to TypeCollection
* Always serialised null values if they are explicitly set
* Allow times to be null
* Bumped dependency versions.


## Version 2.11

**Updates**
* Fixed Entity hashCode method, allowing entities to be keys in Maps
* Bumped dependency versions.


## Version 2.10

**Updates**
* Fixed return type on Entity.getProperty for Entity navigation Properties.
* Bumped dependency versions.


## Version 2.9

**Updates**
* Fixed model initialisation when using external ModelRegistry.


## Version 2.8

**Updates**
* Fixed namespace not being set on EntityTypes.
* Fixed exception trying to auto load navlinks on unconnected entities.


## Version 2.7

**Updates**
* Added automatic model sniffing when creating a SensorThingsService without model.


## Version 2.6

**Updates**
* Bumped dependency versions.
* Implemented loading models from OData 4.01 CSDL ($metadata).


## Version 2.5

**Updates**
* Bumped dependency versions.
* Updated for the published STAplus standard.


## Version 2.4

**Updates**
* Fixed incorrect URL generation for sub DOAs like Datastream(x)/Observations.
* Simplified equals methods on Entity and EntitySet.


## Version 2.3

**Updates**
* Allow Service to be initialised with an existing ModelRegistry.


## Version 2.2

**Updates**
* Reworked PropertyTypes.
* Updated the model handling, service will initialise models.
* Added STAplus data model.
* Added checks when setting or getting properties, throw IllegalArgumentException
  when the EntityType does not have the property.


## Version 2.1

**Updates**
* Replaced the concept of Id with the concept of PrimaryKey.
  Primary keys point to one or more properties that can have any name.
* Fixed 403 being returned as 401.
* Bumped dependency versions.


## Version 2.0

**Updates**
* Complete redesign of FROST-Client to become data model agnostic.

