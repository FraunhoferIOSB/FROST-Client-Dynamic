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

import de.fraunhofer.iosb.ilt.frostclient.Version;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityReference;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationProperty;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Handles serialization of Entity objects.
 */
public class EntitySerializerOdata extends ValueSerializer<Entity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntitySerializerOdata.class.getName());
    private final Version version;

    public EntitySerializerOdata(Version version) {
        this.version = version;
    }

    @Override
    public void serialize(Entity entity, JsonGenerator gen, SerializationContext serializers) {
        if (entity instanceof EntityReference) {
            writeEntityReference(gen, entity);
        } else {
            writeEntity(gen, entity);
        }
    }

    private void writeEntity(JsonGenerator gen, Entity entity) throws JacksonException {
        gen.writeStartObject();
        writeContent(entity, gen);
        gen.writeEndObject();
    }

    public void writeContent(Entity entity, JsonGenerator gen) {
        Set<EntityPropertyMain> entityProps = entity.getType().getEntityProperties();
        Set<NavigationProperty> navigationProps = entity.getType().getNavigationProperties();
        for (EntityPropertyMain ep : entityProps) {
            writeEntityProp(ep, entity, gen);
        }
        for (NavigationProperty np : navigationProps) {
            writeNavProp(entity, np, gen);
        }
    }

    private void writeEntityProp(EntityPropertyMain ep, Entity entity, JsonGenerator gen) {
        if (ep.isReadOnly() && !ep.isKeyPart()) {
            return;
        }
        if (ep == ModelRegistry.EP_SELFLINK) {
            return;
        }
        final Object value = entity.getProperty(ep, false);
        if (value != null || (entity.isSetProperty(ep))) {
            final String name = version.chooseAliasFor(ep);
            gen.writePOJOProperty(name, value);
        }
    }

    private void writeNavProp(Entity entity, NavigationProperty np, JsonGenerator gen) {
        Object entityOrSet = entity.getProperty(np, false);
        if (entityOrSet instanceof EntitySet entitySet) {
            writeEntitySet(np, entitySet, gen);
        } else if (entityOrSet instanceof Entity expandedEntity) {
            gen.writeName(np.getJsonName());
            writeExpandedEntity(gen, expandedEntity);
        }
    }

    private void writeExpandedEntity(JsonGenerator gen, Entity expandedEntity) {
        if (expandedEntity.hasService()) {
            Entity ref = version.getReferenceFor(expandedEntity);
            writeEntityReference(gen, ref);
        } else {
            gen.writePOJO(expandedEntity);
        }
    }

    private void writeEntityReference(JsonGenerator gen, Entity entity) {
        gen.writeStartObject();
        gen.writeStringProperty(version.getSelfLinkName(), entity.getSelfLink());
        gen.writeEndObject();
    }

    private void writeEntitySet(NavigationProperty np, EntitySet entitySet, JsonGenerator gen) {
        if (entitySet == null || entitySet.isEmpty()) {
            return;
        }
        String jsonName = np.getJsonName();
        gen.writeArrayPropertyStart(jsonName);
        for (Entity child : entitySet) {
            writeExpandedEntity(gen, child);
        }
        gen.writeEndArray();
    }

}
