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
package de.fraunhofer.iosb.ilt.frostclient.json.deserialize;

import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.Property;
import de.fraunhofer.iosb.ilt.frostclient.model.PropertyType;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationProperty;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

/**
 * Handles deserialization of Entity objects.
 */
public class EntityDeserializer extends ValueDeserializer<Entity> {

    private static final Map<ModelRegistry, Map<EntityType, EntityDeserializer>> instancePerModelAndType = new HashMap<>();

    public static EntityDeserializer getInstance(final ModelRegistry modelRegistry, final EntityType entityType) {
        return instancePerModelAndType
                .computeIfAbsent(
                        modelRegistry,
                        t -> new HashMap<>())
                .computeIfAbsent(entityType,
                        t -> new EntityDeserializer(modelRegistry, t));
    }

    private final EntityType entityType;
    private final ModelRegistry modelRegistry;
    private final Map<String, PropertyData> propertyByName = new HashMap<>();

    public EntityDeserializer(ModelRegistry modelRegistry, EntityType entityType) {
        this.modelRegistry = modelRegistry;
        this.entityType = entityType;
        final Set<Property> propertySet;
        propertySet = entityType.getPropertySet();

        for (Property property : propertySet) {
            if (property instanceof EntityPropertyMain) {
                final PropertyType type = property.getType();
                final PropertyData propertyData = new PropertyData(
                        property,
                        false,
                        type.getDeserializer(),
                        type.getSerializer());
                for (String alias : ((EntityPropertyMain<?>) property).getAliases()) {
                    propertyByName.put(alias, propertyData);
                }
            } else if (property instanceof NavigationProperty np) {
                propertyByName.put(
                        property.getJsonName(),
                        new PropertyData(
                                property,
                                np.isEntitySet()));
            }
        }
    }

    /**
     * Deserialises an Entity, consuming the Object start and end tokens.
     *
     * @param parser The parser to fetch tokens from.
     * @param ctxt The context to fetch settings from.
     * @return The deserialised Entity.
     * @throws JacksonException If deserialisation fails.
     */
    public Entity deserializeFull(JsonParser parser, DeserializationContext ctxt) throws JacksonException {
        parser.nextToken();
        Entity result = deserialize(parser, ctxt);
        parser.nextToken();
        return result;
    }

    @Override
    public Entity deserialize(JsonParser parser, DeserializationContext ctxt) throws JacksonException {
        Entity result = new Entity(entityType);

        boolean failOnUnknown = ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        JsonToken currentToken = parser.nextToken();
        while (currentToken == JsonToken.PROPERTY_NAME) {
            String fieldName = parser.currentName();
            parser.nextValue();
            if (fieldName.endsWith("@iot.count")) {
                deserialiseEntitySetCount(parser, fieldName, result);
            } else if (fieldName.endsWith("@iot.nextLink")) {
                deserialiseEntitySetNextLink(parser, fieldName, result);
            } else {
                PropertyData propertyData = propertyByName.get(fieldName);
                if (propertyData == null) {
                    if (failOnUnknown) {
                        final String message = "Unknown field: " + fieldName + " on " + entityType.entityName + " expected one of: " + propertyByName.keySet();
                        throw new UnrecognizedPropertyException(parser, message, parser.currentLocation(), Entity.class, fieldName, null);
                    } else {
                        parser.readValueAsTree();
                    }
                } else {
                    deserializeProperty(parser, ctxt, result, propertyData);
                }
            }
            currentToken = parser.nextToken();
        }

        return result;
    }

    private void deserializeProperty(JsonParser parser, DeserializationContext ctxt, Entity result, PropertyData propertyData) throws JacksonException {
        if (propertyData.property instanceof EntityPropertyMain) {
            deserializeEntityProperty(parser, ctxt, propertyData, result);
        } else if (propertyData.property instanceof NavigationProperty) {
            deserializeNavigationProperty(propertyData, result, parser, ctxt);
        }
    }

    private void deserializeNavigationProperty(PropertyData propertyData, Entity result, JsonParser parser, DeserializationContext ctxt) throws JacksonException {
        NavigationProperty navPropertyMain = (NavigationProperty) propertyData.property;
        if (propertyData.isEntitySet) {
            deserialiseEntitySet(parser, ctxt, (NavigationPropertyEntitySet) navPropertyMain, result);
        } else {
            final EntityType targetEntityType = navPropertyMain.getEntityType();
            Object value = getInstance(modelRegistry, targetEntityType)
                    .deserialize(parser, ctxt);
            result.setProperty(navPropertyMain, value);
        }
    }

    private void deserializeEntityProperty(JsonParser parser, DeserializationContext ctxt, PropertyData propertyData, Entity result) throws JacksonException {
        EntityPropertyMain entityPropertyMain = (EntityPropertyMain) propertyData.property;
        if (propertyData.deserializer == null) {
            Object value = parser.readValueAs(Object.class);
            result.setProperty(entityPropertyMain, value);
        } else {
            Object value = propertyData.deserializer.deserialize(parser, ctxt);
            result.setProperty(entityPropertyMain, value);
        }
    }

    private void deserialiseEntitySetCount(JsonParser parser, String fieldName, Entity result) throws JacksonException {
        PropertyData propertyData = propertyByName.get(fieldName.substring(0, fieldName.indexOf('@')));
        if (propertyData == null) {
            return;
        }
        if (propertyData.property instanceof NavigationPropertyEntitySet npes) {
            EntitySet entitySet = result.getProperty(npes);
            if (entitySet == null) {
                entitySet = new EntitySet(result, npes);
                result.setProperty(npes, entitySet);
            }
            entitySet.setCount(parser.getLongValue());
        }
    }

    private void deserialiseEntitySetNextLink(JsonParser parser, String fieldName, Entity result) throws JacksonException {
        PropertyData propertyData = propertyByName.get(fieldName.substring(0, fieldName.indexOf('@')));
        if (propertyData == null) {
            return;
        }
        if (propertyData.property instanceof NavigationPropertyEntitySet npes) {
            EntitySet entitySet = result.getProperty(npes);
            if (entitySet == null) {
                entitySet = new EntitySet(result, npes);
                result.setProperty(npes, entitySet);
            }
            entitySet.setNextLink(parser.getValueAsString());
        }
    }

    private void deserialiseEntitySet(JsonParser parser, DeserializationContext ctxt, NavigationPropertyEntitySet navProperty, Entity result) throws JacksonException {
        final EntityType setType = navProperty.getEntityType();
        EntitySet entitySet = result.getProperty(navProperty);
        if (entitySet == null) {
            entitySet = new EntitySet(result, navProperty);
            result.setProperty(navProperty, entitySet);
        }
        EntityDeserializer setEntityDeser = getInstance(modelRegistry, setType);
        JsonToken curToken = parser.nextToken();
        while (curToken != null && curToken != JsonToken.END_ARRAY) {
            entitySet.add(setEntityDeser.deserialize(parser, ctxt));
            curToken = parser.nextToken();
        }
    }

    private static class PropertyData {

        final Property property;
        final boolean isEntitySet;
        final ValueDeserializer deserializer;
        final ValueSerializer serializer;

        public PropertyData(Property property, boolean isEntitySet) {
            this(property, isEntitySet, null, null);
        }

        public PropertyData(Property property, boolean isEntitySet, ValueDeserializer deserializer, ValueSerializer serializer) {
            this.property = property;
            this.isEntitySet = isEntitySet;
            this.deserializer = deserializer;
            this.serializer = serializer;
        }

    }
}
