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
package de.fraunhofer.iosb.ilt.frostclient.json.serialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.fraunhofer.iosb.ilt.frostclient.json.deserialize.mixins.AbstractConstraintMixin;
import de.fraunhofer.iosb.ilt.frostclient.json.deserialize.mixins.AbstractDataComponentMixin;
import de.fraunhofer.iosb.ilt.frostclient.json.deserialize.mixins.AbstractSWEIdentifiableMixin;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeObject;
import de.fraunhofer.iosb.ilt.swe.common.AbstractDataComponent;
import de.fraunhofer.iosb.ilt.swe.common.AbstractSWEIdentifiable;
import de.fraunhofer.iosb.ilt.swe.common.constraint.AbstractConstraint;
import java.io.IOException;
import java.io.Writer;
import net.time4j.Moment;

/**
 * Enables serialization of entities as JSON.
 */
public class JsonWriter {

    private static ObjectMapper objectMapperInstance;

    public static ObjectMapper getObjectMapper() {
        if (objectMapperInstance == null) {
            initObjectMapper();
        }
        return objectMapperInstance;
    }

    private static synchronized void initObjectMapper() {
        if (objectMapperInstance == null) {
            objectMapperInstance = createObjectMapper();
        }
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)
                .addMixIn(AbstractDataComponent.class, AbstractDataComponentMixin.class)
                .addMixIn(AbstractSWEIdentifiable.class, AbstractSWEIdentifiableMixin.class)
                .addMixIn(AbstractConstraint.class, AbstractConstraintMixin.class);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Entity.class, new EntitySerializer());
        module.addSerializer(TimeObject.class, new TimeObjectSerializer());
        module.addSerializer(Moment.class, new MomentSerializer());
        mapper.registerModule(module);
        return mapper;
    }

    private JsonWriter() {
    }

    public static void writeEntity(Writer writer, Entity entity) throws IOException {
        getObjectMapper().writeValue(writer, entity);
    }

    public static String writeEntity(Entity entity) throws JsonProcessingException {
        return getObjectMapper().writeValueAsString(entity);
    }

    public static byte[] writeBytes(Entity entity) throws JsonProcessingException {
        return getObjectMapper().writeValueAsBytes(entity);
    }

    public static void writeObject(Writer writer, Object object) throws IOException {
        getObjectMapper().writeValue(writer, object);
    }

    public static String writeObject(Object object) throws JsonProcessingException {
        return getObjectMapper().writeValueAsString(object);
    }
}
