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
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInterval;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeValue;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import net.time4j.Moment;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

/**
 * Allows parsing of STA entities from JSON.
 */
public class JsonReader {

    /**
     * The mappers to use for normal users.
     */
    private static final Map<ModelRegistry, ObjectMapper> mappers = new HashMap<>();

    /**
     * Get an object mapper for the given id Class. If the id class is the same
     * as for the first call, the cached mapper is returned.
     *
     * @param modelRegistry The modelRegistry holding the data model to get a
     * mapper for.
     * @return The cached or created object mapper.
     */
    private static ObjectMapper getObjectMapper(ModelRegistry modelRegistry) {
        return mappers.computeIfAbsent(modelRegistry, mr -> createObjectMapper(mr));
    }

    /**
     * Create a new object mapper for the given model Registry.
     *
     * @param modelRegistry The modelRegistry holding the data model to create a
     * mapper for.
     * @return The created object mapper.
     */
    private static ObjectMapper createObjectMapper(ModelRegistry modelRegistry) {
        for (EntityType entityType : modelRegistry.getEntityTypes()) {
            EntityDeserializer.getInstance(modelRegistry, entityType);
        }
        SimpleModule module = new SimpleModule()
                .addDeserializer(TimeInstant.class, new TimeInstantDeserializer())
                .addDeserializer(TimeInterval.class, new TimeIntervalDeserializer())
                .addDeserializer(TimeValue.class, new TimeValueDeserializer())
                .addDeserializer(Moment.class, new MomentDeserializer());
        ObjectMapper mapper = JsonMapper.builder()
                .disable(EnumFeature.READ_ENUMS_USING_TO_STRING)
                .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .enable(DeserializationFeature.USE_LONG_FOR_INTS)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .addModule(module)
                .build();

        return mapper;
    }

    /**
     * The objectMapper for this instance of EntityParser.
     */
    private final ObjectMapper mapper;
    private final ModelRegistry modelRegistry;

    /**
     * Create a JsonReader.
     *
     * @param modelRegistry the model registry to create the json reader for.
     */
    public JsonReader(ModelRegistry modelRegistry) {
        this.modelRegistry = modelRegistry;
        mapper = getObjectMapper(modelRegistry);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public Entity parseEntity(EntityType entityType, byte[] value) throws IOException {
        try (final JsonParser parser = mapper.createParser(value)) {
            return parseEntity(parser, entityType);
        } catch (StackOverflowError err) {
            throw new IOException("Json is too deeply nested.");
        }
    }

    public Entity parseEntity(EntityType entityType, String value) throws IOException {
        try (final JsonParser parser = mapper.createParser(value)) {
            return parseEntity(parser, entityType);
        } catch (StackOverflowError err) {
            throw new IOException("Json is too deeply nested.");
        }
    }

    public Entity parseEntity(EntityType entityType, Reader value) throws IOException {
        try (final JsonParser parser = mapper.createParser(value)) {
            return parseEntity(parser, entityType);
        } catch (StackOverflowError err) {
            throw new IOException("Json is too deeply nested.");
        }
    }

    private Entity parseEntity(final JsonParser parser, EntityType entityType) throws IOException {
        DeserializationContext dsc = mapper._deserializationContext();
        return EntityDeserializer.getInstance(modelRegistry, entityType)
                .deserializeFull(parser, dsc);
    }

    public EntitySet parseEntitySet(EntityType entityType, String value) throws IOException {
        try (final JsonParser parser = mapper.createParser(value)) {
            DeserializationContext dsc = mapper._deserializationContext();
            return EntitySetDeserializer.getInstance(modelRegistry, entityType)
                    .deserializeFull(parser, dsc);
        } catch (StackOverflowError err) {
            throw new IOException("Json is too deeply nested.");
        }
    }

    public EntitySet parseEntitySet(EntityType entityType, Reader value) throws IOException {
        try (final JsonParser parser = mapper.createParser(value)) {
            DeserializationContext dsc = mapper._deserializationContext();
            return EntitySetDeserializer.getInstance(modelRegistry, entityType)
                    .deserializeFull(parser, dsc);
        } catch (StackOverflowError err) {
            throw new IOException("Json is too deeply nested.");
        }
    }

    public <T> T parseObject(Class<T> clazz, String value) throws IOException {
        return mapper.readValue(value, clazz);
    }

    public <T> T parseObject(Class<T> clazz, Reader value) throws IOException {
        return mapper.readValue(value, clazz);
    }

    public <T> T parseObject(TypeReference<T> typeReference, String value) throws IOException {
        return mapper.readValue(value, typeReference);
    }

    public <T> T parseObject(TypeReference<T> typeReference, Reader value) throws IOException {
        return mapper.readValue(value, typeReference);
    }

}
