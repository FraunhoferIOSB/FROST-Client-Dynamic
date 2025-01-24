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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Deserialises top-level entity sets. Nested sets are handled separately.
 */
public class EntitySetDeserializer extends JsonDeserializer<EntitySet> {

    private static final Map<ModelRegistry, Map<EntityType, EntitySetDeserializer>> instancePerModelAndType = new HashMap<>();

    public static EntitySetDeserializer getInstance(final ModelRegistry modelRegistry, final EntityType entityType) {
        return instancePerModelAndType
                .computeIfAbsent(
                        modelRegistry,
                        t -> new HashMap<>())
                .computeIfAbsent(entityType,
                        t -> new EntitySetDeserializer(modelRegistry, t));
    }

    private final EntityType entityType;
    private final ModelRegistry modelRegistry;

    public EntitySetDeserializer(ModelRegistry modelRegistry, EntityType entityType) {
        this.modelRegistry = modelRegistry;
        this.entityType = entityType;
    }

    /**
     * Deserialises an EntitySet result, consuming the Object start and end
     * tokens.
     *
     * @param parser The parser to fetch tokens from.
     * @param ctxt The context to fetch settings from.
     * @return The deserialised Entity.
     * @throws IOException If deserialisation fails.
     */
    public EntitySet deserializeFull(JsonParser parser, DeserializationContext ctxt) throws IOException {
        parser.nextToken();
        EntitySet result = deserialize(parser, ctxt);
        parser.nextToken();
        return result;
    }

    @Override
    public EntitySet deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        EntitySet result = new EntitySet(entityType);

        boolean failOnUnknown = ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        JsonToken currentToken = parser.nextToken();
        while (currentToken == JsonToken.FIELD_NAME) {
            String fieldName = parser.getCurrentName();
            parser.nextValue();
            if (fieldName.endsWith("count")) {
                result.setCount(parser.readValueAs(Long.class));
            } else if (fieldName.endsWith("nextLink")) {
                result.setNextLink(parser.readValueAs(String.class));
            } else if ("value".equals(fieldName)) {
                deserialiseEntitySet(parser, ctxt, result);
            } else if (failOnUnknown) {
                final String message = "Unknown field: " + fieldName + " on " + entityType.entityName + " set.";
                throw new UnrecognizedPropertyException(parser, message, parser.currentLocation(), EntitySet.class, fieldName, null);
            }
            currentToken = parser.nextToken();
        }

        return result;
    }

    private void deserialiseEntitySet(JsonParser parser, DeserializationContext ctxt, EntitySet targetSet) throws IOException {
        EntityDeserializer entityDeser = EntityDeserializer.getInstance(modelRegistry, targetSet.getEntityType());
        JsonToken curToken = parser.nextToken();
        while (curToken != null && curToken != JsonToken.END_ARRAY) {
            targetSet.add(entityDeser.deserialize(parser, ctxt));
            curToken = parser.nextToken();
        }
    }

}
