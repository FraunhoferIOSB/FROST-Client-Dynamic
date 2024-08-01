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
package de.fraunhofer.iosb.ilt.frostclient.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.fraunhofer.iosb.ilt.frostclient.json.deserialize.mixins.AbstractConstraintMixin;
import de.fraunhofer.iosb.ilt.frostclient.json.deserialize.mixins.AbstractDataComponentMixin;
import de.fraunhofer.iosb.ilt.frostclient.json.deserialize.mixins.AbstractSWEIdentifiableMixin;
import de.fraunhofer.iosb.ilt.frostclient.json.serialize.MomentSerializer;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.AbstractDataComponent;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.AbstractSWEIdentifiable;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.constraint.AbstractConstraint;
import net.time4j.Moment;

/**
 * A mapper handler for simple json serialisations.
 */
public class SimpleJsonMapper {

    private static ObjectMapper simpleObjectMapper;

    private SimpleJsonMapper() {
        // Utility class.
    }

    /**
     * get an ObjectMapper for generic, non-STA use.
     *
     * @return an ObjectMapper for generic, non-STA use.
     */
    public static ObjectMapper getSimpleObjectMapper() {
        if (simpleObjectMapper == null) {
            SimpleModule module = new SimpleModule();
            module.addSerializer(Moment.class, new MomentSerializer());
            simpleObjectMapper = new ObjectMapper()
                    .setSerializationInclusion(JsonInclude.Include.ALWAYS)
                    .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                    .addMixIn(AbstractDataComponent.class, AbstractDataComponentMixin.class)
                    .addMixIn(AbstractSWEIdentifiable.class, AbstractSWEIdentifiableMixin.class)
                    .addMixIn(AbstractConstraint.class, AbstractConstraintMixin.class)
                    .registerModule(module);
        }
        return simpleObjectMapper;
    }
}
