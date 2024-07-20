/*
 * Copyright (C) 2023 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
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

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import de.fraunhofer.iosb.ilt.frostclient.json.SimpleJsonMapper;
import de.fraunhofer.iosb.ilt.frostclient.model.ComplexValue;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.Property;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationProperty;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import java.io.IOException;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.geojson.GeoJsonObject;

public class ParserUtils {

    private ParserUtils() {
        // Utility class.
    }

    public static String entityPath(EntityType entityType, Object... primaryKeyValues) {
        return String.format("%s(%s)", entityType.mainSet, formatKeyValuesForUrl(primaryKeyValues));
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
        if (!parent.getEntityType().getNavigationProperties().contains(relation)) {
            throw new IllegalArgumentException("Entity of type " + parent.getEntityType() + " has no relation of type " + relation + ".");
        }

        return String.format("%s(%s)/%s", parent.getEntityType().mainSet, formatKeyValuesForUrl(parent.getPrimaryKeyValues()), relation.getName());
    }

    public static String formatKeyValuesForUrl(Entity entity) {
        return formatKeyValuesForUrl(entity.getPrimaryKeyValues());
    }

    public static String formatKeyValuesForUrl(Object... pkeyValues) {
        if (pkeyValues.length == 1) {
            if (pkeyValues[0] == null) {
                throw new IllegalArgumentException("Primary key value must be non-null");
            }
            return StringHelper.quoteForUrl(pkeyValues[0]);
        } else {
            throw new NotImplementedException("Multi-valued primary keys are not supported yet.");
        }
    }

    public static Object[] tryToParse(String input) {
        if (input.startsWith("'")) {
            return new Object[]{StringUtils.replace(input.substring(1, input.length() - 1), "''", "'")};
        }
        try {
            return new Object[]{Long.valueOf(input)};
        } catch (NumberFormatException exc) {
            // not a long.
        }
        return new Object[]{input};
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

    public static <T> JsonDeserializer<T> getDefaultDeserializer(TypeReference<T> tr) {
        return new JsonDeserializer<T>() {
            @Override
            public T deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JacksonException {
                return jp.readValueAs(tr);
            }
        };
    }

    public static JsonDeserializer<Object> getLocationDeserializer() {
        return new JsonDeserializer<Object>() {
            @Override
            public Object deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
                final ObjectMapper simpleObjectMapper = SimpleJsonMapper.getSimpleObjectMapper();
                final TreeNode valueTree = jp.readValueAsTree();
                try {
                    return simpleObjectMapper.treeToValue(valueTree, GeoJsonObject.class);
                } catch (JsonProcessingException ex) {
                    // Not GeoJSON
                }
                return simpleObjectMapper.treeToValue(valueTree, String.class);
            }
        };
    }

    public static JsonDeserializer<ComplexValue> getComplexTypeDeserializer(TypeComplex type) {
        return new ComplexTypeDeserializer(type);
    }

    public static JsonSerializer<Object> getDefaultSerializer() {
        return new JsonSerializer<Object>() {
            @Override
            public void serialize(Object t, JsonGenerator jg, SerializerProvider sp) throws IOException {
                jg.writePOJO(t);
            }
        };
    }

    private static class ComplexTypeDeserializer extends JsonDeserializer<ComplexValue> {

        private final TypeComplex type;

        public ComplexTypeDeserializer(TypeComplex type) {
            this.type = type;
        }

        @Override
        public ComplexValue deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
            ComplexValue result = type.instantiate();
            JsonToken currentToken = parser.currentToken();
            if (currentToken == JsonToken.VALUE_NULL) {
                return null;
            }
            currentToken = parser.nextToken();
            while (currentToken == JsonToken.FIELD_NAME) {
                String fieldName = parser.getCurrentName();
                parser.nextValue();
                Property property = type.getProperty(fieldName);
                if (property == null) {
                    if (!type.isOpenType()) {
                        final String message = "Unknown field: " + fieldName + " on " + type.getName() + " expected one of: " + type.getPropertiesByName().keySet();
                        throw new UnrecognizedPropertyException(parser, message, parser.getCurrentLocation(), TypeComplex.class, fieldName, null);
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

        private void deserializeProperty(JsonParser parser, DeserializationContext ctxt, Property property, ComplexValue result) throws IOException {
            if (property instanceof EntityPropertyMain epm) {
                deserializeEntityProperty(parser, ctxt, epm, result);
            } else if (property instanceof NavigationProperty) {
                throw new IllegalArgumentException("NavigationProperties not supported on ComplexTypes.");
            }
        }

        private void deserializeEntityProperty(JsonParser parser, DeserializationContext ctxt, EntityPropertyMain property, ComplexValue result) throws IOException {
            final JsonDeserializer deserializer = property.getType().getDeserializer();
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
