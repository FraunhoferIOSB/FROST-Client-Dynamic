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
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.PkValue;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
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
                .registerProperty(CommonProperties.EP_ID)
                .registerProperty(CommonProperties.EP_NAME)
                .registerProperty(CommonProperties.EP_DESCRIPTION)
                .registerProperty(EP_PUBLIC)
                .registerProperty(CommonProperties.EP_PROPERTIES)
                .registerProperty(npProjectFeaturesOfInterest)
                .registerProperty(npProjectLocations)
                .registerProperty(npProjectSensors)
                .registerProperty(npProjectThings)
                .registerProperty(npProjectUserProjectRoles);

        etRole
                .registerProperty(EP_ROLENAME)
                .registerProperty(CommonProperties.EP_DESCRIPTION)
                .registerProperty(CommonProperties.EP_PROPERTIES)
                .registerProperty(npRoleUserProjectRoles)
                .registerProperty(npRoleUsers);

        etUser
                .registerProperty(EP_USERNAME)
                .registerProperty(EP_USERPASS)
                .registerProperty(npUserRoles)
                .registerProperty(npUserUserProjectRoles);

        etUserProjectRole
                .registerProperty(CommonProperties.EP_ID)
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

}
