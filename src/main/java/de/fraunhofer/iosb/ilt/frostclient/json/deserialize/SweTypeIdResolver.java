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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.AbstractSWEIdentifiable;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.constraint.AbstractConstraint;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves Swe types based on their "type" property.
 */
public class SweTypeIdResolver implements TypeIdResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SweTypeIdResolver.class.getName());

    private static final Map<String, Class<?>> annnotatedClasses;

    static {
        final Reflections reflections = new Reflections("de.fraunhofer.iosb.ilt.frostclient.models.swecommon");
        annnotatedClasses = reflections
                .getSubTypesOf(AbstractSWEIdentifiable.class)
                .stream()
                .collect(Collectors.toMap(
                        x -> idFromClass(x),
                        x -> x));
        annnotatedClasses.putAll(
                reflections
                        .getSubTypesOf(AbstractConstraint.class)
                        .stream()
                        .collect(Collectors.toMap(
                                x -> idFromClass(x),
                                x -> x)));
    }

    private JavaType superType;

    @Override
    public void init(JavaType baseType) {
        superType = baseType;
    }

    @Override
    public String idFromValue(Object value) {
        return idFromClass(value.getClass());
    }

    public static String idFromClass(Class clazz) {
        final String className = clazz.getName();
        String name = className.substring(1 + className.lastIndexOf('.'));
        try {
            name = FieldUtils.readStaticField(clazz, "SWE_NAME").toString();
        } catch (NullPointerException | IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.trace("Class {} has no SWE_NAME field.", className);
        }
        LOGGER.trace("{} -> {}", clazz.getName(), name);
        return name;
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromClass(value.getClass());
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        if (!annnotatedClasses.containsKey(id)) {
            throw new RuntimeException(String.format("unkown type '%s'", id));
        }
        return context.constructSpecializedType(superType, annnotatedClasses.get(id));
    }

    @Override
    public String idFromBaseType() {
        return idFromClass(superType.getRawClass());
    }

    @Override
    public String getDescForKnownTypeIds() {
        return annnotatedClasses.toString();
    }

}
