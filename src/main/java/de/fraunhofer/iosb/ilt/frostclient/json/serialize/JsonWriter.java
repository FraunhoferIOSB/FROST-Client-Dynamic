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
package de.fraunhofer.iosb.ilt.frostclient.json.serialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeObject;
import java.io.IOException;
import java.io.Writer;
import net.time4j.Moment;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.datatype.jsonp.JSONPModule;

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
        SimpleModule module = new SimpleModule()
                .addSerializer(Entity.class, new EntitySerializer())
                .addSerializer(TimeObject.class, new TimeObjectSerializer())
                .addSerializer(Moment.class, new MomentSerializer());
        ObjectMapper mapper = JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_EMPTY))
                .disable(EnumFeature.READ_ENUMS_USING_TO_STRING)
                .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .addModule(module)
                .addModules(new JSONPModule())
                .build();

        return mapper;
    }

    private JsonWriter() {
    }

    public static void writeEntity(Writer writer, Entity entity) throws IOException {
        getObjectMapper().writeValue(writer, entity);
    }

    public static String writeEntity(Entity entity) throws JacksonException {
        return getObjectMapper().writeValueAsString(entity);
    }

    public static byte[] writeBytes(Entity entity) throws JacksonException {
        return getObjectMapper().writeValueAsBytes(entity);
    }

    public static void writeObject(Writer writer, Object object) throws JacksonException {
        getObjectMapper().writeValue(writer, object);
    }

    public static String writeObject(Object object) throws JacksonException {
        return getObjectMapper().writeValueAsString(object);
    }
}
