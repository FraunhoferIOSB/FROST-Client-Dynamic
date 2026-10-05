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
package de.iosb.fraunhofer.ilt.frostclient.models;

import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_DESCRIPTION;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_ID;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_NAME;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_PROPERTIES;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Projects.EP_ROLENAME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Projects.EP_USERNAME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Projects.EP_USERPASS;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Projects;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class V11ProjectsBuilderTest extends BuilderTest {

    protected final SensorThingsV11Projects model = new SensorThingsV11Projects();

    private Entity projectEntity;
    private Entity roleEntity;
    private Entity userEntity;
    private Entity userProjectRoleEntity;

    private SensorThingsV11Sensing modelSensing;

    @BeforeEach
    public void setUp() throws Exception {
        modelSensing = new SensorThingsV11Sensing();
        SensorThingsService service = new SensorThingsService(modelSensing, model)
                .setBaseUrl(SensorThingsService.NULL_URL_V11)
                .init();
        projectEntity = model.buildProject().build();
        roleEntity = model.buildRole().build();
        userEntity = model.buildUser().build();
        userProjectRoleEntity = model.buildUserProjectRole().build();
    }

    @Test
    public void testBuildProject() {
        Assertions.assertEquals(model.etProject, projectEntity.getType());
    }

    @Test
    public void testBuildRole() {
        Assertions.assertEquals(model.etRole, roleEntity.getType());
    }

    @Test
    public void testBuildUser() {
        Assertions.assertEquals(model.etUser, userEntity.getType());
    }

    @Test
    public void testBuildUserProjectRole() {
        Assertions.assertEquals(model.etUserProjectRole, userProjectRoleEntity.getType());
    }

    @Test
    public void testProjectSetId() {
        Entity entity = model.buildProject()
                .setId(1L)
                .build();
        Assertions.assertEquals(1L, entity.getProperty(EP_ID));
    }

    @Test
    public void testProjectSetName() {
        String name = "Test Project";
        Entity entity = model.buildProject()
                .setName(name)
                .build();
        Assertions.assertEquals(name, entity.getProperty(EP_NAME));
    }

    @Test
    public void testProjectSetDescription() {
        String description = "Test Project Description";
        Entity entity = model.buildProject()
                .setDescription(description)
                .build();
        Assertions.assertEquals(description, entity.getProperty(EP_DESCRIPTION));
    }

    @Test
    public void testProjectSetProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("key1", "value1");
        MapValue properties = new MapValue(TypeComplex.STA_MAP, props);
        Entity entity = model.buildProject()
                .setProperties(properties)
                .build();
        Assertions.assertSame(properties, entity.getProperty(EP_PROPERTIES));
    }

    @Test
    public void testProjectAddFeatureOfInterest() throws ServiceFailureException {
        Entity foi = modelSensing.buildFeature().build();
        Entity entity = model.buildProject()
                .addFeatureOfInterest(foi)
                .build();
        EntitySet fois = entity.getProperty(model.npProjectFeaturesOfInterest);
        Assertions.assertNotNull(fois);
        Assertions.assertEquals(1, fois.size());
    }

    @Test
    public void testProjectAddLocation() throws ServiceFailureException {
        Entity location = modelSensing.buildLocation().build();
        Entity entity = model.buildProject()
                .addLocation(location)
                .build();
        EntitySet locations = entity.getProperty(model.npProjectLocations);
        Assertions.assertNotNull(locations);
        Assertions.assertEquals(1, locations.size());
    }

    @Test
    public void testProjectAddSensor() throws ServiceFailureException {
        Entity sensor = modelSensing.buildSensor().build();
        Entity entity = model.buildProject()
                .addSensor(sensor)
                .build();
        EntitySet sensors = entity.getProperty(model.npProjectSensors);
        Assertions.assertNotNull(sensors);
        Assertions.assertEquals(1, sensors.size());
    }

    @Test
    public void testProjectAddThing() throws ServiceFailureException {
        Entity thing = modelSensing.buildThing().build();
        Entity entity = model.buildProject()
                .addThing(thing)
                .build();
        EntitySet things = entity.getProperty(model.npProjectThings);
        Assertions.assertNotNull(things);
        Assertions.assertEquals(1, things.size());
    }

    @Test
    public void testThingAddProject() throws ServiceFailureException {
        Entity thing = modelSensing.buildThing()
                .extend(model.thingExtender())
                .addProject(projectEntity)
                .build();
        EntitySet projects = thing.getProperty(model.npThingProjects);
        Assertions.assertNotNull(projects);
        Assertions.assertEquals(1, projects.size());
        Assertions.assertEquals(projectEntity, projects.toList().get(0));
    }

    @Test
    public void testProjectAddUserProjectRole() throws ServiceFailureException {
        Entity upr = model.buildUserProjectRole().setId(1L).build();
        Entity entity = model.buildProject()
                .addUserProjectRole(upr)
                .build();
        EntitySet uprs = entity.getProperty(model.npProjectUserProjectRoles);
        Assertions.assertNotNull(uprs);
        Assertions.assertEquals(1, uprs.size());
    }

    @Test
    public void testProjectQueryFeaturesOfInterestThrowsWithoutService() {
        Entity entity = model.buildProject().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npProjectFeaturesOfInterest));
    }

    @Test
    public void testProjectQueryLocationsThrowsWithoutService() {
        Entity entity = model.buildProject().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npProjectLocations));
    }

    @Test
    public void testProjectQuerySensorsThrowsWithoutService() {
        Entity entity = model.buildProject().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npProjectSensors));
    }

    @Test
    public void testProjectQueryThingsThrowsWithoutService() {
        Entity entity = model.buildProject().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npProjectThings));
    }

    @Test
    public void testProjectQueryUserProjectRolesThrowsWithoutService() {
        Entity entity = model.buildProject().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npProjectUserProjectRoles));
    }

    @Test
    public void testRoleSetRolename() {
        String rolename = "Administrator";
        Entity entity = model.buildRole()
                .setRolename(rolename)
                .build();
        Assertions.assertEquals(rolename, entity.getProperty(EP_ROLENAME));
    }

    @Test
    public void testRoleSetDescription() {
        String description = "Role Description";
        Entity entity = model.buildRole()
                .setDescription(description)
                .build();
        Assertions.assertEquals(description, entity.getProperty(EP_DESCRIPTION));
    }

    @Test
    public void testRoleSetProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("key1", "value1");
        MapValue properties = new MapValue(TypeComplex.STA_MAP, props);
        Entity entity = model.buildRole()
                .setProperties(properties)
                .build();
        Assertions.assertSame(properties, entity.getProperty(EP_PROPERTIES));
    }

    @Test
    public void testRoleAddUser() throws ServiceFailureException {
        Entity user = model.buildUser().build();
        Entity entity = model.buildRole()
                .addUser(user)
                .build();
        EntitySet users = entity.getProperty(model.npRoleUsers);
        Assertions.assertNotNull(users);
        Assertions.assertEquals(1, users.size());
    }

    @Test
    public void testRoleAddUserProjectRole() throws ServiceFailureException {
        Entity upr = model.buildUserProjectRole().build();
        Entity entity = model.buildRole()
                .addUserProjectRole(upr)
                .build();
        EntitySet uprs = entity.getProperty(model.npRoleUserProjectRoles);
        Assertions.assertNotNull(uprs);
        Assertions.assertEquals(1, uprs.size());
    }

    @Test
    public void testRoleQueryUsersThrowsWithoutService() {
        Entity entity = model.buildRole().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npRoleUsers));
    }

    @Test
    public void testRoleQueryUserProjectRolesThrowsWithoutService() {
        Entity entity = model.buildRole().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npRoleUserProjectRoles));
    }

    @Test
    public void testUserSetUsername() {
        String username = "testuser";
        Entity entity = model.buildUser()
                .setUsername(username)
                .build();
        Assertions.assertEquals(username, entity.getProperty(EP_USERNAME));
    }

    @Test
    public void testUserSetUserpass() {
        String userpass = "testpass";
        Entity entity = model.buildUser()
                .setUserpass(userpass)
                .build();
        Assertions.assertEquals(userpass, entity.getProperty(EP_USERPASS));
    }

    @Test
    public void testUserAddRole() throws ServiceFailureException {
        Entity role = model.buildRole().build();
        Entity entity = model.buildUser()
                .addRole(role)
                .build();
        EntitySet roles = entity.getProperty(model.npUserRoles);
        Assertions.assertNotNull(roles);
        Assertions.assertEquals(1, roles.size());
    }

    @Test
    public void testUserAddUserProjectRole() throws ServiceFailureException {
        Entity upr = model.buildUserProjectRole().build();
        Entity entity = model.buildUser()
                .addUserProjectRole(upr)
                .build();
        EntitySet uprs = entity.getProperty(model.npUserUserProjectRoles);
        Assertions.assertNotNull(uprs);
        Assertions.assertEquals(1, uprs.size());
    }

    @Test
    public void testUserQueryRolesThrowsWithoutService() {
        Entity entity = model.buildUser().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npUserRoles));
    }

    @Test
    public void testUserQueryUserProjectRolesThrowsWithoutService() {
        Entity entity = model.buildUser().build();
        assertThrowsIllegalArgumentException(() -> entity.query(model.npUserUserProjectRoles));
    }

    @Test
    public void testUserProjectRoleSetId() {
        Entity entity = model.buildUserProjectRole()
                .setId(1L)
                .build();
        Assertions.assertEquals(1L, entity.getProperty(EP_ID));
    }

    @Test
    public void testUserProjectRoleSetProject() throws ServiceFailureException {
        Entity project = model.buildProject().build();
        Entity entity = model.buildUserProjectRole()
                .setProject(project)
                .build();
        Assertions.assertSame(project, entity.getProperty(model.npUserProjectRoleProject, false));
    }

    @Test
    public void testUserProjectRoleSetRole() throws ServiceFailureException {
        Entity role = model.buildRole().build();
        Entity entity = model.buildUserProjectRole()
                .setRole(role)
                .build();
        Assertions.assertSame(role, entity.getProperty(model.npUserProjectRoleRole, false));
    }

    @Test
    public void testUserProjectRoleSetUser() throws ServiceFailureException {
        Entity user = model.buildUser().build();
        Entity entity = model.buildUserProjectRole()
                .setUser(user)
                .build();
        Assertions.assertSame(user, entity.getProperty(model.npUserProjectRoleUser, false));
    }

}
