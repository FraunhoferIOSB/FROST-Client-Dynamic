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
package de.fraunhofer.iosb.ilt.frostclient.model;

import static de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive.EDM_STRING;
import static de.fraunhofer.iosb.ilt.frostclient.utils.SpecialNames.AT_IOT_SELF_LINK;

import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive;
import de.fraunhofer.iosb.ilt.frostclient.models.DataModel;
import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The modelRegistry holds the registered EntityTypes and PropertyTypes.
 */
public class ModelRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelRegistry.class.getName());

    /**
     * The global EntityProperty SelfLink.
     */
    public static final EntityPropertyMain<String> EP_SELFLINK = new EntityPropertyMain<String>(AT_IOT_SELF_LINK, EDM_STRING).setAliases("selfLink");

    /**
     * All entity types, by their entityName (both singular and mainSet).
     */
    private final Map<String, EntityType> entityTypesByName = new TreeMap<>();

    /**
     * The EntityContainers, with their entity type.
     */
    private final Map<String, EntityType> containersByName = new TreeMap<>();

    /**
     * All entity types.
     */
    private final Set<EntityType> entityTypes = new LinkedHashSet<>();

    /**
     * All property types by their name.
     */
    private final Map<String, PropertyType> propertyTypes = new TreeMap<>();

    /**
     * The data models used in this ModelRegistry.
     */
    private Map<Class<? extends DataModel>, DataModel> dataModels = new HashMap<>();

    private boolean initialised;

    /**
     * Register a new entity type. Registering the same type twice is a no-op,
     * registering a new entity type with a name that already exists causes an
     * {@link IllegalArgumentException}.
     *
     * @param type The entity type to register.
     * @return this ModelRegistry.
     */
    public final ModelRegistry registerEntityType(EntityType type) {
        EntityType existing = entityTypesByName.get(type.entityName);
        if (existing == type) {
            LOGGER.info("Entity type {} already registered.", type.entityName);
            return this;
        }
        if (existing != null) {
            LOGGER.error("Duplicate entity type name: {}", type.entityName);
            throw new IllegalArgumentException("An entity type named " + type.entityName + " is already registered");
        }
        entityTypesByName.put(type.entityName, type);
        entityTypes.add(type);
        type.setModelRegistry(this);
        return this;
    }

    public final ModelRegistry registerEntityContainer(String name, EntityType type) {
        containersByName.put(name, type);
        return this;
    }

    /**
     * Get the entity type with the given name.
     *
     * @param typeName The name of the entity type to find.
     * @return the entity type with the given name, or null.
     */
    public final EntityType getEntityTypeForName(String typeName) {
        final EntityType type = entityTypesByName.get(typeName);
        return type;
    }

    public final Set<EntityType> getEntityTypes() {
        return entityTypes;
    }

    public Map<String, EntityType> getContainers() {
        return containersByName;
    }

    public EntityType getEntityTypeForContainer(String name) {
        return containersByName.get(name);
    }

    public ModelRegistry registerPropertyType(PropertyType type) {
        propertyTypes.put(type.getName(), type);
        return this;
    }

    public final PropertyType getPropertyType(String name) {
        PropertyType type = propertyTypes.get(name);
        if (type != null) {
            return type;
        }
        type = TypePrimitive.getType(name);
        if (type != null) {
            return type;
        }
        type = TypeComplex.getType(name);
        if (type != null) {
            return type;
        }
        throw new IllegalArgumentException("unknown property type: " + name);
    }

    public Map<String, PropertyType> getPropertyTypes() {
        return propertyTypes;
    }

    public <T extends DataModel> T getModel(Class<T> clazz) {
        return (T) dataModels.get(clazz);
    }

    public <T extends DataModel> boolean hasModel(Class<T> clazz) {
        return dataModels.containsKey(clazz);
    }

    public ModelRegistry addDataModel(DataModel model) {
        dataModels.put(model.getClass(), model);
        return this;
    }

    public synchronized ModelRegistry initFinalise() {
        if (initialised) {
            return this;
        }
        LOGGER.info("Finalising {} EntityTypes.", entityTypes.size());
        for (EntityType type : entityTypes) {
            type.init();
            if (!StringHelper.isNullOrEmpty(type.mainSet)) {
                containersByName.put(type.mainSet, type);
            }
        }
        initialised = true;
        return this;
    }

    public boolean isInitialised() {
        return initialised;
    }

}
