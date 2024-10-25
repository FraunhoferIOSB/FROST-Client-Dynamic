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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySetImpl;
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
        ObjectMapper mapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .enable(DeserializationFeature.USE_LONG_FOR_INTS)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();

        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(EntitySet.class, EntitySetImpl.class);
        for (EntityType entityType : modelRegistry.getEntityTypes()) {
            EntityDeserializer.getInstance(modelRegistry, entityType);
        }
        module.addDeserializer(TimeInstant.class, new TimeInstantDeserializer());
        module.addDeserializer(TimeInterval.class, new TimeIntervalDeserializer());
        module.addDeserializer(TimeValue.class, new TimeValueDeserializer());
        module.addDeserializer(Moment.class, new MomentDeserializer());

        mapper.registerModule(module);
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
        DefaultDeserializationContext dsc = (DefaultDeserializationContext) mapper.getDeserializationContext();
        dsc = dsc.createInstance(mapper.getDeserializationConfig(), parser, mapper.getInjectableValues());
        return EntityDeserializer.getInstance(modelRegistry, entityType)
                .deserializeFull(parser, dsc);
    }

    public EntitySet parseEntitySet(EntityType entityType, String value) throws IOException {
        try (final JsonParser parser = mapper.createParser(value)) {
            DefaultDeserializationContext dsc = (DefaultDeserializationContext) mapper.getDeserializationContext();
            dsc = dsc.createInstance(mapper.getDeserializationConfig(), parser, mapper.getInjectableValues());
            return EntitySetDeserializer.getInstance(modelRegistry, entityType)
                    .deserializeFull(parser, dsc);
        } catch (StackOverflowError err) {
            throw new IOException("Json is too deeply nested.");
        }
    }

    public EntitySet parseEntitySet(EntityType entityType, Reader value) throws IOException {
        try (final JsonParser parser = mapper.createParser(value)) {
            DefaultDeserializationContext dsc = (DefaultDeserializationContext) mapper.getDeserializationContext();
            dsc = dsc.createInstance(mapper.getDeserializationConfig(), parser, mapper.getInjectableValues());
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
