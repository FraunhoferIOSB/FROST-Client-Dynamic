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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationProperty;
import java.io.IOException;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles serialization of Entity objects.
 */
public class EntitySerializer extends JsonSerializer<Entity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntitySerializer.class.getName());

    @Override
    public void serialize(Entity entity, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        try {
            writeContent(entity, gen);
        } catch (IOException | RuntimeException exc) {
            LOGGER.error("Failed to serialise entity.", exc);
            throw new IOException("could not serialize Entity");
        } finally {
            gen.writeEndObject();
        }
    }

    public void writeContent(Entity entity, JsonGenerator gen) throws IOException {
        Set<EntityPropertyMain> entityProps = entity.getEntityType().getEntityProperties();
        Set<NavigationProperty> navigationProps = entity.getEntityType().getNavigationProperties();
        for (EntityPropertyMain ep : entityProps) {
            writeEntityProp(ep, entity, gen);
        }
        for (NavigationProperty np : navigationProps) {
            writeNavProp(entity, np, gen);
        }
    }

    private void writeEntityProp(EntityPropertyMain ep, Entity entity, JsonGenerator gen) throws IOException {
        if (ep.isReadOnly() && !ep.isKeyPart()) {
            return;
        }
        if (ep == ModelRegistry.EP_SELFLINK) {
            return;
        }
        final Object value = entity.getProperty(ep, false);
        if (value != null || (entity.isSetProperty(ep))) {
            final String name = ep.getName();
            gen.writeObjectField(name, value);
        }
    }

    private void writeNavProp(Entity entity, NavigationProperty np, JsonGenerator gen) throws IOException {
        Object entityOrSet = entity.getProperty(np, false);
        if (entityOrSet instanceof EntitySet entitySet) {
            writeEntitySet(np, entitySet, gen);
        } else if (entityOrSet instanceof Entity expandedEntity) {
            if (expandedEntity.hasService()) {
                gen.writeObjectField(np.getJsonName(), expandedEntity.withOnlyPk());
            } else {
                gen.writeObjectField(np.getJsonName(), expandedEntity);
            }
        }
    }

    private void writeEntitySet(NavigationProperty np, EntitySet entitySet, JsonGenerator gen) throws IOException {
        if (entitySet == null || entitySet.isEmpty()) {
            return;
        }
        String jsonName = np.getJsonName();
        gen.writeArrayFieldStart(jsonName);
        for (Object child : entitySet) {
            gen.writeObject(child);
        }
        gen.writeEndArray();
    }

}
