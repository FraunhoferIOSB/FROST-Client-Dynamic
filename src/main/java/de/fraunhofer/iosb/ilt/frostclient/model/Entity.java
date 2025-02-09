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

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.dao.BaseDao;
import de.fraunhofer.iosb.ilt.frostclient.dao.Dao;
import de.fraunhofer.iosb.ilt.frostclient.exception.MqttException;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.exception.StatusCodeException;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationProperty;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.query.Expand;
import de.fraunhofer.iosb.ilt.frostclient.query.Expand.ExpandItem;
import de.fraunhofer.iosb.ilt.frostclient.query.Query;
import de.fraunhofer.iosb.ilt.frostclient.utils.MqttSubscription;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import org.slf4j.LoggerFactory;

/**
 * The Entity model element.
 */
public class Entity implements ComplexValue<Entity> {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Entity.class.getName());

    private EntityType entityType;
    private final Map<EntityPropertyMain, Object> entityProperties = new HashMap<>();
    private final Map<NavigationProperty, Object> navProperties = new HashMap<>();
    private final Set<Property> setProperties = new HashSet<>();

    /**
     * The STA service this entity is loaded from.
     */
    private SensorThingsService service;
    /**
     * The expand that was applied to this entity when loading.
     */
    private Expand expand;

    /**
     * The selfLink or @id of this entity.
     */
    private String selfLink;

    public Entity(EntityType entityType) {
        this.entityType = entityType;
    }

    /**
     * Get the primary key definition of the (EntityType of the) Entity. This is
     * a shorthand for getEntityType().getPrimaryKey();
     *
     * @return The primary key definition of the Entity.
     */
    public final PrimaryKey getPrimaryKey() {
        return entityType.getPrimaryKey();
    }

    /**
     * Key the values of the primary key fields for this Entity.
     *
     * @return the primary key values.
     */
    public final PkValue getPrimaryKeyValues() {
        List<EntityPropertyMain> keyProperties = entityType.getPrimaryKey().getKeyProperties();
        PkValue pkValue = new PkValue(keyProperties.size());
        int idx = 0;
        for (EntityPropertyMain keyProperty : keyProperties) {
            pkValue.set(idx, getProperty(keyProperty));
            idx++;
        }
        return pkValue;
    }

    public boolean primaryKeyFullySet() {
        List<EntityPropertyMain> keyProperties = entityType.getPrimaryKey().getKeyProperties();
        for (EntityPropertyMain keyProperty : keyProperties) {
            Object value = getProperty(keyProperty);
            if (value == null) {
                return false;
            }
        }
        return true;
    }

    public final Entity setPrimaryKeyValues(PkValue values) {
        int idx = 0;
        for (EntityPropertyMain keyProperty : entityType.getPrimaryKey().getKeyProperties()) {
            if (idx >= values.size()) {
                throw new IllegalArgumentException("No value given for keyProperty " + idx);
            }
            setProperty(keyProperty, values.get(idx));
            idx++;
        }
        return this;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public Entity setSelfLink(String selfLink) {
        this.selfLink = selfLink;
        return this;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Entity setEntityType(EntityType entityType) {
        if (this.entityType != null) {
            throw new IllegalArgumentException("the type of this entity is alread yet to " + this.entityType.entityName);
        }
        this.entityType = entityType;
        return this;
    }

    public boolean isSetProperty(Property property) {
        if (property == ModelRegistry.EP_SELFLINK) {
            return true;
        }
        return setProperties.contains(property);
    }

    @Override
    public <P> P getProperty(Property<P> property) {
        return getProperty(property, true);
    }

    public Entity getProperty(NavigationPropertyEntity property) throws ServiceFailureException {
        return getProperty(property, true);
    }

    public Entity getProperty(NavigationPropertyEntity npe, boolean autoLoad) throws ServiceFailureException {
        Entity entity = (Entity) navProperties.get(npe);
        if (entity == null && autoLoad && service != null) {
            try {
                entity = service.dao(npe.getEntityType()).find(this, npe);
                setProperty(npe, entity);
            } catch (StatusCodeException ex) {
                final int statusCode = ex.getStatusCode();
                if (statusCode == 404 || statusCode == 204) {
                    // The entity doesn't have this navLink (sta 404, OData 204), all is fine.
                    return null;
                }
                // Something else went wrong, re-throw.
                throw ex;
            }
        } else if (entity != null && service != null && entity.getService() == null) {
            entity.setService(service);
        }
        return entity;
    }

    public <P> P getProperty(Property<P> property, boolean autoLoad) {
        if (property == null) {
            return null;
        }
        if (property == ModelRegistry.EP_SELFLINK) {
            return (P) getSelfLink();
        }
        if (!entityType.hasProperty(property)) {
            throw new IllegalArgumentException(entityType.entityName + " has no property " + property.getName());
        }
        if (property instanceof EntityPropertyMain epm) {
            return (P) entityProperties.get(epm);
        }
        if (property instanceof NavigationPropertyEntity npe) {
            try {
                return (P) getProperty(npe, autoLoad);
            } catch (ServiceFailureException ex) {
                LOGGER.error("Failed to load linked entity {}", npe, ex.getMessage());
                throw new RuntimeException(ex);
            }
        }
        if (property instanceof NavigationPropertyEntitySet npes) {
            EntitySet entitySet = (EntitySet) navProperties.get(npes);
            if (entitySet == null && autoLoad) {
                entitySet = new EntitySet(this, npes);
                if (autoLoad && service != null) {
                    String startLink = service.getFullPathString(this, npes);
                    entitySet.setInitialLink(startLink);
                    entitySet.setNextLink(startLink);
                    entitySet.setExpandItem(getExpandItemFor(npes));
                }
                setProperty(npes, entitySet);
            }
            if (entitySet != null && service != null && entitySet.getService() == null) {
                entitySet.setService(service);
            }
            return (P) entitySet;
        }
        return null;
    }

    @Override
    public Object getProperty(String name) {
        throw new IllegalArgumentException("Can not get custom properties from Entity " + entityType);
    }

    @Override
    public Entity setProperty(String name, Object value) {
        throw new IllegalArgumentException("Can not set custom properties on Entity " + entityType);
    }

    @Override
    public <P> Entity setProperty(Property<P> property, P value) {
        if (property == ModelRegistry.EP_SELFLINK) {
            setSelfLink(String.valueOf(value));
            return this;
        }
        if (!entityType.hasProperty(property)) {
            throw new IllegalArgumentException(entityType.entityName + " has no property " + property.getName());
        }
        if (property instanceof EntityPropertyMain epm) {
            entityProperties.put(epm, value);
            setProperties.add(property);
        } else if (property instanceof NavigationProperty np) {
            navProperties.put(np, value);
            if (value == null) {
                setProperties.remove(property);
            } else {
                setProperties.add(property);
            }
        }
        return this;
    }

    public Entity unsetProperty(Property property) {
        if (property instanceof EntityPropertyMain epm) {
            entityProperties.remove(epm);
        } else if (property instanceof NavigationProperty np) {
            navProperties.remove(np);
        }
        setProperties.remove(property);
        return this;
    }

    public Entity addNavigationEntity(NavigationPropertyEntitySet navProperty, Entity linkedEntity) {
        EntitySet entitySet = getProperty(navProperty);
        if (entitySet == null) {
            entitySet = new EntitySet(this, navProperty);
            setProperty(navProperty, entitySet);
        }
        entitySet.add(linkedEntity);
        return this;
    }

    public Entity addNavigationEntity(NavigationPropertyEntitySet navProperty, List<Entity> linkedEntities) {
        for (Entity linkedEntity : linkedEntities) {
            addNavigationEntity(navProperty, linkedEntity);
        }
        return this;
    }

    public Entity addNavigationEntity(NavigationPropertyEntitySet navProperty, Entity... linkedEntities) {
        for (Entity linkedEntity : linkedEntities) {
            addNavigationEntity(navProperty, linkedEntity);
        }
        return this;
    }

    /**
     * Check if the entity is associated with a service or not.
     *
     * @return true if the entity is associated with a service.
     */
    public boolean hasService() {
        return service != null;
    }

    public SensorThingsService getService() {
        return service;
    }

    public void setService(SensorThingsService service) {
        this.service = service;
    }

    public void ensureService() throws IllegalArgumentException {
        if (service == null) {
            throw new IllegalArgumentException("Can not subscribe, entity not sent to service yet.");
        }
    }

    public Expand getExpand() {
        return expand;
    }

    public ExpandItem getExpandItemFor(NavigationProperty navProp) {
        if (expand == null) {
            return null;
        }
        return expand.getItemFor(navProp);
    }

    public void setExpand(Expand expand) {
        this.expand = expand;
    }

    /**
     * Creates a copy of the entity, with only the Primary Key field(s) set.
     * Useful when creating a new entity that links to this entity.
     *
     * @return a copy with only the Primary Key fields set.
     */
    public Entity withOnlyPk() {
        Entity copy = new Entity(entityType);
        List<EntityPropertyMain> pkProps = getPrimaryKey().getKeyProperties();
        for (EntityPropertyMain pkProp : pkProps) {
            copy.setProperty(pkProp, getProperty(pkProp));
        }
        copy.setService(service);
        return copy;
    }

    public Query query(NavigationPropertyEntitySet navigationPropery) {
        if (service == null) {
            throw new IllegalArgumentException("Can not query from an entity not associated with a service.");
        }
        return new Query(service, this, navigationPropery);
    }

    public Dao dao(NavigationPropertyEntitySet navigationPropery) {
        if (service == null) {
            throw new IllegalArgumentException("Can not query from an entity not associated with a service.");
        }
        return new BaseDao(service, this, navigationPropery);
    }

    public MqttSubscription subscribe(Consumer<Entity> handler) throws MqttException {
        ensureService();
        String topic = service.getServerInfo().getMqttBasePath() + ParserUtils.entityPath(entityType, getPrimaryKeyValues());
        return service.subscribe(topic, handler, entityType);
    }

    public MqttSubscription subscribe(NavigationProperty np, Consumer<Entity> handler) throws MqttException {
        ensureService();
        String topic = service.getServerInfo().getMqttBasePath() + ParserUtils.relationPath(this, np);
        return service.subscribe(topic, handler, np.getEntityType());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entity other = (Entity) obj;
        if (!Objects.equals(this.entityType, other.entityType)) {
            return false;
        }
        return Objects.equals(this.getPrimaryKeyValues(), other.getPrimaryKeyValues());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.entityType);
        hash = 97 * hash + getPrimaryKeyValues().hashCode();
        return hash;
    }

    /**
     * Returns a string that represents the Entity, like a name.
     *
     * @return A string that represents the Entity, like a name.
     */
    public String display() {
        return entityType.display(this);
    }

    @Override
    public String toString() {
        return entityType.toString(this);
    }
}
