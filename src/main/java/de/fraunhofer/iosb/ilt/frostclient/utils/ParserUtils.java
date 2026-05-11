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
package de.fraunhofer.iosb.ilt.frostclient.utils;

import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Tasking.TYPE_REFERENCE_DATARECORD;

import de.fraunhofer.iosb.ilt.frostclient.json.SimpleJsonMapper;
import de.fraunhofer.iosb.ilt.frostclient.model.ComplexValue;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.PkValue;
import de.fraunhofer.iosb.ilt.frostclient.model.Property;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationProperty;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.complex.DataRecord;
import org.apache.commons.lang3.StringUtils;
import org.geojson.GeoJsonObject;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TreeNode;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

public class ParserUtils {

    private ParserUtils() {
        // Utility class.
    }

    public static String entityPath(EntityType entityType, PkValue primaryKeyValues) {
        return String.format("%s(%s)",
                entityType.mainSet,
                StringHelper.formatKeyValuesForUrl(entityType.getPrimaryKey(), primaryKeyValues));
    }

    /**
     * The local path to an entity or collection. e.g.:
     * <ul>
     * <li>Things(2)/Datastreams</li>
     * <li>Datastreams(5)/Thing</li>
     * </ul>
     *
     * @param parent The entity holding the relation, can be null.
     * @param relation The relation or collection to get.
     * @return The path to the entity collection.
     */
    public static String relationPath(Entity parent, NavigationProperty relation) {
        if (parent == null) {
            throw new IllegalArgumentException("Can't generate path for null entity.");
        }
        if (!parent.getType().getNavigationProperties().contains(relation)) {
            throw new IllegalArgumentException("Entity of type " + parent.getType() + " has no relation of type " + relation + ".");
        }

        return String.format("%s(%s)/%s",
                parent.getType().mainSet,
                StringHelper.formatKeyValuesForUrl(parent),
                relation.getName());
    }

    public static PkValue tryToParse(String input) {
        if (input.startsWith("'")) {
            final String value = StringUtils.replace(input.substring(1, input.length() - 1), "''", "'");
            return PkValue.of(value);
        }
        try {
            return PkValue.of(Long.valueOf(input));
        } catch (NumberFormatException exc) {
            // not a long.
        }
        return PkValue.of(input);
    }

    public static boolean objectToBoolean(Object data, boolean dflt) {
        if (data == null) {
            return dflt;
        }
        if (data instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(data.toString());
    }

    public static <T> ValueDeserializer<T> getDefaultDeserializer(TypeReference<T> tr) {
        return new ValueDeserializer<T>() {
            @Override
            public T deserialize(JsonParser jp, DeserializationContext dc) throws JacksonException {
                return jp.readValueAs(tr);
            }
        };
    }

    public static <T> ValueDeserializer<T> getDefaultDeserializer(Class<T> clazz) {
        return new ValueDeserializer<T>() {
            @Override
            public T deserialize(JsonParser jp, DeserializationContext dc) throws JacksonException {
                return jp.readValueAs(clazz);
            }
        };
    }

    public static ValueDeserializer<DataRecord> getDataRecordDeserializer() {
        return new ValueDeserializer<DataRecord>() {
            @Override
            public DataRecord deserialize(JsonParser jp, DeserializationContext dc) throws JacksonException {
                TreeNode tree = jp.readValueAsTree();
                if (tree.size() == 0) {
                    return null;
                }
                return SimpleJsonMapper.getSimpleObjectMapper().treeToValue(tree, TYPE_REFERENCE_DATARECORD);
            }
        };
    }

    public static ValueDeserializer<Object> getLocationDeserializer() {
        return new ValueDeserializer<Object>() {
            @Override
            public Object deserialize(JsonParser jp, DeserializationContext dc) throws JacksonException {
                final ObjectMapper simpleObjectMapper = SimpleJsonMapper.getSimpleObjectMapper();
                final TreeNode valueTree = jp.readValueAsTree();
                try {
                    return simpleObjectMapper.treeToValue(valueTree, GeoJsonObject.class);
                } catch (JacksonException ex) {
                    // Not GeoJSON
                }
                return simpleObjectMapper.treeToValue(valueTree, Object.class);
            }
        };
    }

    public static ValueDeserializer<ComplexValue> getComplexTypeDeserializer(TypeComplex type) {
        return new ComplexTypeDeserializer(type);
    }

    public static <V> ValueSerializer<V> getDefaultSerializer() {
        return new ValueSerializer<V>() {
            @Override
            public void serialize(Object t, JsonGenerator jg, SerializationContext ctx) throws JacksonException {
                jg.writePOJO(t);
            }
        };
    }

    public static class ComplexTypeDeserializer<V extends ComplexValue<V>> extends ValueDeserializer<V> {

        private final TypeComplex type;

        public ComplexTypeDeserializer(TypeComplex type) {
            if (type == null) {
                throw new IllegalArgumentException("Type must be non-null");
            }
            this.type = type;
        }

        @Override
        public V deserialize(JsonParser parser, DeserializationContext ctxt) throws JacksonException {
            V result = (V) type.instantiate();
            JsonToken currentToken = parser.currentToken();
            if (currentToken == JsonToken.VALUE_NULL) {
                return null;
            }
            currentToken = parser.nextToken();
            while (currentToken == JsonToken.PROPERTY_NAME) {
                String fieldName = parser.currentName();
                parser.nextValue();
                Property property = type.getEntityProperty(fieldName);
                if (property == null) {
                    if (!type.isOpenType()) {
                        final String message = "Unknown field: " + fieldName + " on " + type.getName() + " expected one of: " + type.getPropertiesByName().keySet();
                        throw new UnrecognizedPropertyException(parser, message, parser.currentLocation(), TypeComplex.class, fieldName, null);
                    } else {
                        result.setProperty(fieldName, parser.readValueAsTree());
                    }
                } else {
                    deserializeProperty(parser, ctxt, property, result);
                }
                currentToken = parser.nextToken();
            }

            return result;
        }

        private void deserializeProperty(JsonParser parser, DeserializationContext ctxt, Property property, ComplexValue result) throws JacksonException {
            if (property instanceof EntityPropertyMain epm) {
                deserializeEntityProperty(parser, ctxt, epm, result);
            } else if (property instanceof NavigationProperty) {
                throw new IllegalArgumentException("NavigationProperties not supported on ComplexTypes.");
            }
        }

        private void deserializeEntityProperty(JsonParser parser, DeserializationContext ctxt, EntityPropertyMain property, ComplexValue result) throws JacksonException {
            final ValueDeserializer deserializer = property.getType().getDeserializer();
            if (deserializer == null) {
                Object value = parser.readValueAs(Object.class);
                result.setProperty(property, value);
            } else {
                Object value = deserializer.deserialize(parser, ctxt);
                result.setProperty(property, value);
            }
        }
    }
}
