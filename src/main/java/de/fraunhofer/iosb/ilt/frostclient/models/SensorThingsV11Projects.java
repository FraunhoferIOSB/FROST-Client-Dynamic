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
package de.fraunhofer.iosb.ilt.frostclient.models;

import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_DESCRIPTION;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_ID;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_NAME;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_PROPERTIES;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_DATASTREAM;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_FEATUREOFINTEREST;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_FEATURESOFINTEREST;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_LOCATION;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_LOCATIONS;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_SENSORS;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_THING;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.NAME_THINGS;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.PkValue;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.Builder;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.BuilderId;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.BuilderIdNameDesProp;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import de.fraunhofer.iosb.ilt.frostclient.query.Query;
import java.util.Map;

/**
 * The Data Model implements the SensorThings Tasking extension.
 */
public class SensorThingsV11Projects implements DataModel {

    public static final String NAME_USER = "User";
    public static final String NAME_USERS = "Users";
    public static final String NAME_ROLE = "Role";
    public static final String NAME_ROLES = "Roles";
    public static final String NAME_PROJECT = "Project";
    public static final String NAME_PROJECTS = "Projects";
    public static final String NAME_USERPROJECTROLE = "UserProjectRole";
    public static final String NAME_USERPROJECTROLES = "UserProjectRoles";

    public static final String NAME_EP_PUBLIC = "public";
    public static final String NAME_EP_RESTRICTED = "restricted";
    public static final String NAME_EP_ROLENAME = "rolename";
    public static final String NAME_EP_USERNAME = "username";
    public static final String NAME_EP_USERPASS = "userpass";

    public static final String NAME_NP_GENERATEDFEATURE = "GeneratedFeature";
    public static final String NAME_NP_GENERATEDFORLOCATIONS = "GeneratedForLocations";

    public static final EntityPropertyMain<Boolean> EP_PUBLIC = new EntityPropertyMain<>(NAME_EP_PUBLIC, TypePrimitive.EDM_BOOLEAN);
    public static final EntityPropertyMain<Boolean> EP_RESTRICTED = new EntityPropertyMain<>(NAME_EP_RESTRICTED, TypePrimitive.EDM_BOOLEAN);
    public static final EntityPropertyMain<String> EP_ROLENAME = new EntityPropertyMain<>(NAME_EP_ROLENAME, TypePrimitive.EDM_STRING);
    public static final EntityPropertyMain<String> EP_USERNAME = new EntityPropertyMain<>(NAME_EP_USERNAME, TypePrimitive.EDM_STRING);
    public static final EntityPropertyMain<String> EP_USERPASS = new EntityPropertyMain<>(NAME_EP_USERPASS, TypePrimitive.EDM_STRING);

    public final NavigationPropertyEntitySet npFeatureOfInterestGeneratedForLocations = new NavigationPropertyEntitySet(NAME_NP_GENERATEDFORLOCATIONS);
    public final NavigationPropertyEntitySet npFeatureOfInterestProjects = new NavigationPropertyEntitySet(NAME_PROJECTS);

    public final NavigationPropertyEntitySet npLocationGeneratedFeature = new NavigationPropertyEntitySet(NAME_NP_GENERATEDFEATURE, npFeatureOfInterestGeneratedForLocations);
    public final NavigationPropertyEntitySet npLocationProjects = new NavigationPropertyEntitySet(NAME_PROJECTS);

    public final NavigationPropertyEntitySet npProjectFeaturesOfInterest = new NavigationPropertyEntitySet(NAME_FEATURESOFINTEREST, npFeatureOfInterestProjects);
    public final NavigationPropertyEntitySet npProjectLocations = new NavigationPropertyEntitySet(NAME_LOCATIONS, npLocationProjects);
    public final NavigationPropertyEntitySet npProjectSensors = new NavigationPropertyEntitySet(NAME_SENSORS);
    public final NavigationPropertyEntitySet npProjectThings = new NavigationPropertyEntitySet(NAME_THINGS);
    public final NavigationPropertyEntitySet npProjectUserProjectRoles = new NavigationPropertyEntitySet(NAME_USERPROJECTROLES);

    public final NavigationPropertyEntitySet npRoleUserProjectRoles = new NavigationPropertyEntitySet(NAME_USERPROJECTROLES);
    public final NavigationPropertyEntitySet npRoleUsers = new NavigationPropertyEntitySet(NAME_USERS);

    public final NavigationPropertyEntitySet npSensorProjects = new NavigationPropertyEntitySet(NAME_PROJECTS, npProjectSensors);

    public final NavigationPropertyEntitySet npThingProjects = new NavigationPropertyEntitySet(NAME_PROJECTS, npProjectThings);

    public final NavigationPropertyEntitySet npUserRoles = new NavigationPropertyEntitySet(NAME_ROLES, npRoleUsers);
    public final NavigationPropertyEntitySet npUserUserProjectRoles = new NavigationPropertyEntitySet(NAME_USERPROJECTROLES);

    public final NavigationPropertyEntity npUserProjectRoleProject = new NavigationPropertyEntity(NAME_PROJECT, npProjectUserProjectRoles);
    public final NavigationPropertyEntity npUserProjectRoleRole = new NavigationPropertyEntity(NAME_ROLE, npRoleUserProjectRoles);
    public final NavigationPropertyEntity npUserProjectRoleUser = new NavigationPropertyEntity(NAME_USER, npUserUserProjectRoles);

    public final EntityType etProject = new EntityType(NAME_PROJECT, NAME_PROJECTS);
    public final EntityType etRole = new EntityType(NAME_ROLE, NAME_ROLES);
    public final EntityType etUser = new EntityType(NAME_USER, NAME_USERS);
    public final EntityType etUserProjectRole = new EntityType(NAME_USERPROJECTROLE, NAME_USERPROJECTROLES);

    private ModelRegistry mr;

    public SensorThingsV11Projects() {
    }

    @Override
    public final void init(SensorThingsService service, ModelRegistry modelRegistry) {
        if (this.mr != null) {
            throw new IllegalArgumentException("Already initialised.");
        }
        this.mr = modelRegistry;
        mr.addDataModel(this);

        mr.registerEntityType(etProject);
        mr.registerEntityType(etRole);
        mr.registerEntityType(etUser);
        mr.registerEntityType(etUserProjectRole);

        etProject
                .registerProperty(EP_ID)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_PUBLIC)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(npProjectFeaturesOfInterest)
                .registerProperty(npProjectLocations)
                .registerProperty(npProjectSensors)
                .registerProperty(npProjectThings)
                .registerProperty(npProjectUserProjectRoles);

        etRole
                .registerProperty(EP_ROLENAME)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(npRoleUserProjectRoles)
                .registerProperty(npRoleUsers);

        etUser
                .registerProperty(EP_USERNAME)
                .registerProperty(EP_USERPASS)
                .registerProperty(npUserRoles)
                .registerProperty(npUserUserProjectRoles);

        etUserProjectRole
                .registerProperty(EP_ID)
                .registerProperty(npUserProjectRoleProject)
                .registerProperty(npUserProjectRoleRole)
                .registerProperty(npUserProjectRoleUser);

        mr.getEntityTypeForName(NAME_DATASTREAM)
                .registerProperty(EP_RESTRICTED);
        mr.getEntityTypeForName(NAME_FEATUREOFINTEREST)
                .registerProperty(EP_RESTRICTED)
                .registerProperty(npFeatureOfInterestGeneratedForLocations)
                .registerProperty(npFeatureOfInterestProjects);
        mr.getEntityTypeForName(NAME_LOCATION)
                .registerProperty(EP_RESTRICTED)
                .registerProperty(npLocationGeneratedFeature);
        mr.getEntityTypeForName(NAME_THING)
                .registerProperty(EP_RESTRICTED)
                .registerProperty(npThingProjects);
    }

    @Override
    public boolean isInitialised() {
        return mr != null;
    }

    public ModelRegistry getModelRegistry() {
        return mr;
    }

    public ProjectBuilder buildProject() {
        return new ProjectBuilder(this);
    }

    public ProjectBuilder editProject(Entity entity) {
        return new ProjectBuilder(this, entity);
    }

    public static class ProjectBuilder extends BuilderIdNameDesProp<ProjectBuilder> {

        SensorThingsV11Projects mdlProjects;

        public ProjectBuilder(SensorThingsV11Projects mdlProjects) {
            super(new Entity(mdlProjects.etProject));
            this.mdlProjects = mdlProjects;
        }

        public ProjectBuilder(SensorThingsV11Projects mdlProjects, Entity entity) {
            super(entity);
            this.mdlProjects = mdlProjects;
        }

        public Query queryFeaturesOfInterest() {
            return entity.query(mdlProjects.npProjectFeaturesOfInterest);
        }

        public EntitySet getFeaturesOfInterest() {
            return entity.getProperty(mdlProjects.npProjectFeaturesOfInterest);
        }

        public ProjectBuilder addFeatureOfInterest(Entity foi) {
            entity.addNavigationEntity(mdlProjects.npProjectFeaturesOfInterest, foi);
            return getThis();
        }

        public Query queryLocations() {
            return entity.query(mdlProjects.npProjectLocations);
        }

        public EntitySet getLocations() {
            return entity.getProperty(mdlProjects.npProjectLocations);
        }

        public ProjectBuilder addLocation(Entity location) {
            entity.addNavigationEntity(mdlProjects.npProjectLocations, location);
            return getThis();
        }

        public Query queryHistoricalSensors() {
            return entity.query(mdlProjects.npProjectSensors);
        }

        public EntitySet getHistoricalSensors() {
            return entity.getProperty(mdlProjects.npProjectSensors);
        }

        public ProjectBuilder addSensor(Entity sensor) {
            entity.addNavigationEntity(mdlProjects.npProjectSensors, sensor);
            return getThis();
        }

        public Query queryThings() {
            return entity.query(mdlProjects.npProjectThings);
        }

        public EntitySet getThings() {
            return entity.getProperty(mdlProjects.npProjectThings);
        }

        public ProjectBuilder addThing(Entity thing) {
            entity.addNavigationEntity(mdlProjects.npProjectThings, thing);
            return getThis();
        }

        public Query queryUserProjectRoles() {
            return entity.query(mdlProjects.npProjectUserProjectRoles);
        }

        public EntitySet getUserProjectRoles() {
            return entity.getProperty(mdlProjects.npProjectUserProjectRoles);
        }

        public ProjectBuilder addUserProjectRole(Entity upr) {
            entity.addNavigationEntity(mdlProjects.npProjectUserProjectRoles, upr);
            return getThis();
        }
    }

    public static class RoleBuilder extends Builder<RoleBuilder> {

        SensorThingsV11Projects mdlProjects;

        public RoleBuilder(SensorThingsV11Projects mdlProjects) {
            super(new Entity(mdlProjects.etRole));
            this.mdlProjects = mdlProjects;
        }

        public RoleBuilder(SensorThingsV11Projects mdlProjects, Entity entity) {
            super(entity);
            this.mdlProjects = mdlProjects;
        }

        public String getRolename() {
            return entity.getProperty(EP_ROLENAME);
        }

        public RoleBuilder setRolename(String name) {
            entity.setProperty(EP_ROLENAME, name);
            return getThis();
        }

        public String getDescription() {
            return entity.getProperty(EP_DESCRIPTION);
        }

        public RoleBuilder setDescription(String desc) {
            entity.setProperty(EP_DESCRIPTION, desc);
            return getThis();
        }

        public MapValue getProperties() {
            return entity.getProperty(EP_PROPERTIES);
        }

        public RoleBuilder setProperties(MapValue properties) {
            entity.setProperty(EP_PROPERTIES, properties);
            return getThis();
        }

        public Query queryUsers() {
            return entity.query(mdlProjects.npRoleUsers);
        }

        public EntitySet getUsers() {
            return entity.getProperty(mdlProjects.npRoleUsers);
        }

        public RoleBuilder addUser(Entity user) {
            entity.addNavigationEntity(mdlProjects.npRoleUsers, user);
            return getThis();
        }

        public Query queryUserProjectRoles() {
            return entity.query(mdlProjects.npRoleUserProjectRoles);
        }

        public EntitySet getUserProjectRoles() {
            return entity.getProperty(mdlProjects.npRoleUserProjectRoles);
        }

        public RoleBuilder addUserProjectRole(Entity upr) {
            entity.addNavigationEntity(mdlProjects.npRoleUserProjectRoles, upr);
            return getThis();
        }
    }

    public static class UserBuilder extends Builder<UserBuilder> {

        SensorThingsV11Projects mdlProjects;

        public UserBuilder(SensorThingsV11Projects mdlProjects) {
            super(new Entity(mdlProjects.etUser));
            this.mdlProjects = mdlProjects;
        }

        public UserBuilder(SensorThingsV11Projects mdlProjects, Entity entity) {
            super(entity);
            this.mdlProjects = mdlProjects;
        }

        public String getUsername() {
            return entity.getProperty(EP_USERNAME);
        }

        public UserBuilder setUsername(String name) {
            entity.setProperty(EP_USERNAME, name);
            return getThis();
        }

        public UserBuilder setUserpass(String pass) {
            entity.setProperty(EP_USERPASS, pass);
            return getThis();
        }

        public Query queryRoles() {
            return entity.query(mdlProjects.npUserRoles);
        }

        public EntitySet getRoles() {
            return entity.getProperty(mdlProjects.npUserRoles);
        }

        public UserBuilder addRole(Entity upr) {
            entity.addNavigationEntity(mdlProjects.npUserRoles, upr);
            return getThis();
        }

        public Query queryUserProjectRoles() {
            return entity.query(mdlProjects.npUserUserProjectRoles);
        }

        public EntitySet getUserProjectRoles() {
            return entity.getProperty(mdlProjects.npUserUserProjectRoles);
        }

        public UserBuilder addUserProjectRole(Entity upr) {
            entity.addNavigationEntity(mdlProjects.npUserUserProjectRoles, upr);
            return getThis();
        }
    }

    public static class UserProjectRoleBuilder extends BuilderId<UserProjectRoleBuilder> {

        SensorThingsV11Projects mdlProjects;

        public UserProjectRoleBuilder(SensorThingsV11Projects mdlProjects) {
            super(new Entity(mdlProjects.etUserProjectRole));
            this.mdlProjects = mdlProjects;
        }

        public UserProjectRoleBuilder(SensorThingsV11Projects mdlProjects, Entity entity) {
            super(entity);
            this.mdlProjects = mdlProjects;
        }

        public Entity getProject() throws ServiceFailureException {
            return entity.getProperty(mdlProjects.npUserProjectRoleProject);
        }

        public UserProjectRoleBuilder setProject(Entity project) {
            entity.setProperty(mdlProjects.npUserProjectRoleProject, project);
            return getThis();
        }

        public Entity getRole() throws ServiceFailureException {
            return entity.getProperty(mdlProjects.npUserProjectRoleRole);
        }

        public UserProjectRoleBuilder setRole(Entity role) {
            entity.setProperty(mdlProjects.npUserProjectRoleRole, role);
            return getThis();
        }

        public Entity getUser() throws ServiceFailureException {
            return entity.getProperty(mdlProjects.npUserProjectRoleUser);
        }

        public UserProjectRoleBuilder setUser(Entity role) {
            entity.setProperty(mdlProjects.npUserProjectRoleUser, role);
            return getThis();
        }
    }

    public static class DatastreamExtensionBuilder extends ExtensionBuilderRestricted<DatastreamExtensionBuilder> {

        public DatastreamExtensionBuilder() {
        }

        public DatastreamExtensionBuilder(Entity entity) {
            super(entity);
        }
    }

    public static class FeatureOfInterestExtensionBuilder extends ExtensionBuilderProjectsRestricted<FeatureOfInterestExtensionBuilder> {

        public FeatureOfInterestExtensionBuilder(SensorThingsV11Projects mdlProjects) {
            super(mdlProjects.npFeatureOfInterestProjects);
        }

        public FeatureOfInterestExtensionBuilder(SensorThingsV11Projects mdlProjects, Entity entity) {
            super(mdlProjects.npFeatureOfInterestProjects, entity);
        }
    }

    public static class LocationExtensionBuilder extends ExtensionBuilderProjectsRestricted<LocationExtensionBuilder> {

        public LocationExtensionBuilder(SensorThingsV11Projects mdlProjects) {
            super(mdlProjects.npLocationProjects);
        }

        public LocationExtensionBuilder(SensorThingsV11Projects mdlProjects, Entity entity) {
            super(mdlProjects.npLocationProjects, entity);
        }
    }

    public static class SensorExtensionBuilder extends ExtensionBuilderProjectsRestricted<SensorExtensionBuilder> {

        public SensorExtensionBuilder(SensorThingsV11Projects mdlProjects) {
            super(mdlProjects.npSensorProjects);
        }

        public SensorExtensionBuilder(SensorThingsV11Projects mdlProjects, Entity entity) {
            super(mdlProjects.npSensorProjects, entity);
        }
    }

    public static class ThingExtensionBuilder extends ExtensionBuilderProjectsRestricted<ThingExtensionBuilder> {

        public ThingExtensionBuilder(SensorThingsV11Projects mdlProjects) {
            super(mdlProjects.npThingProjects);
        }

        public ThingExtensionBuilder(SensorThingsV11Projects mdlProjects, Entity entity) {
            super(mdlProjects.npThingProjects, entity);
        }
    }

    public static abstract class ExtensionBuilderProjectsRestricted<T extends ExtensionBuilderProjectsRestricted<T>> extends ExtensionBuilderRestricted<T> {

        private NavigationPropertyEntitySet npProjects;

        public ExtensionBuilderProjectsRestricted(NavigationPropertyEntitySet npProjects) {
            this.npProjects = npProjects;
        }

        public ExtensionBuilderProjectsRestricted(NavigationPropertyEntitySet npProjects, Entity entity) {
            super(entity);
            this.npProjects = npProjects;
        }

        public Query queryProjects(Entity tc) {
            return entity.query(npProjects);
        }

        public EntitySet getProjects(Entity tc) {
            return entity.getProperty(npProjects);
        }

        public T addProject(Entity project) {
            entity.addNavigationEntity(npProjects, project);
            return getThis();
        }
    }

    public static abstract class ExtensionBuilderRestricted<T extends ExtensionBuilderRestricted<T>> extends Builder<T> {

        public ExtensionBuilderRestricted() {
        }

        public ExtensionBuilderRestricted(Entity entity) {
            super(entity);
        }

        public Boolean getRestricted() {
            return entity.getProperty(EP_RESTRICTED);
        }

        public T setRestricted(boolean restricted) {
            entity.setProperty(EP_RESTRICTED, restricted);
            return getThis();
        }
    }

    @Deprecated
    public Entity newUser() {
        return new Entity(etUser);
    }

    @Deprecated
    public Entity newUser(String username, String password) {
        return newUser()
                .setProperty(EP_USERNAME, username)
                .setProperty(EP_USERPASS, password);
    }

    @Deprecated
    public Entity newRole() {
        return new Entity(etRole);
    }

    @Deprecated
    public Entity newRole(String rolename, String description) {
        return newRole()
                .setProperty(EP_ROLENAME, rolename)
                .setProperty(EP_DESCRIPTION, description);
    }

    @Deprecated
    public Entity newRole(String rolename, String description, MapValue properties) {
        return newRole(rolename, description)
                .setProperty(EP_PROPERTIES, properties);
    }

    @Deprecated
    public Entity newRole(String rolename, String description, Map<String, Object> properties) {
        return newRole(rolename, description, new MapValue(TypeComplex.STA_MAP, properties));
    }

    @Deprecated
    public Entity newProject() {
        return new Entity(etProject);
    }

    @Deprecated
    public Entity newProject(String projectname, String description) {
        return newProject()
                .setProperty(EP_NAME, projectname)
                .setProperty(EP_DESCRIPTION, description);
    }

    @Deprecated
    public Entity newProject(String rolename, String description, MapValue properties) {
        return newProject(rolename, description)
                .setProperty(EP_PROPERTIES, properties);
    }

    @Deprecated
    public Entity newProject(String rolename, String description, Map<String, Object> properties) {
        return newProject(rolename, description, new MapValue(TypeComplex.STA_MAP, properties));
    }

    @Deprecated
    public Entity newUserProjectRole() {
        return new Entity(etUserProjectRole);
    }

    @Deprecated
    public Entity newUserProjectRole(Object... pk) {
        return newUserProjectRole()
                .setPrimaryKeyValues(PkValue.of(pk));
    }

    @Deprecated
    public Entity newUserProjectRole(PkValue pk) {
        return newUserProjectRole()
                .setPrimaryKeyValues(pk);
    }

    @Deprecated
    public Entity newUserProjectRole(Entity user, Entity project, Entity role) {
        return newUserProjectRole()
                .setProperty(npUserProjectRoleUser, user)
                .setProperty(npUserProjectRoleProject, project)
                .setProperty(npUserProjectRoleRole, role);
    }

}
