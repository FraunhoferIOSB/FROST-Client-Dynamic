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
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_NAME;
import static de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.EP_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.PkValue;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.Builder;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.BuilderId;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.BuilderIdNameDesProp;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommonBuilderTest extends BuilderTest {

    private SensorThingsV11Sensing model;

    private Entity entity;

    @BeforeEach
    public void setUp() throws Exception {
        model = new SensorThingsV11Sensing();
        SensorThingsService service = new SensorThingsService(model)
                .setBaseUrl(SensorThingsService.NULL_URL_V11)
                .init();
        entity = new Entity(model.etThing);
    }

    @Test
    public void testDefaultConstructor() {
        Builder<?> builder = new Builder<>();
        assertNotNull(builder);
    }

    @Test
    public void testEntityConstructor() {
        Builder<?> builder = new Builder<>(entity);
        assertNotNull(builder);
    }

    @Test
    public void testBuild() {
        Builder<?> builder = new Builder<>(entity);
        Entity result = builder.build();
        assertSame(entity, result);
    }

    @Test
    public void testSetIdWithObjectArray() {
        BuilderId<?> builder = new BuilderId<>(entity);
        builder.setId("test-id");
        PkValue pkValue = entity.getPrimaryKeyValues();
        assertEquals(1, pkValue.size());
        assertEquals("test-id", pkValue.get(0));
    }

    @Test
    public void testSetIdWithPkValue() {
        BuilderId<?> builder = new BuilderId<>(entity);
        PkValue pkValue = new PkValue(new Object[]{"test-id"});
        builder.setId(pkValue);
        PkValue result = entity.getPrimaryKeyValues();
        assertEquals(pkValue.size(), result.size());
        assertEquals(pkValue.get(0), result.get(0));
    }

    @Test
    public void testGetPrimaryKeyValues() {
        entity.setPrimaryKeyValues(PkValue.of("test-id"));
        BuilderId<?> builder = new BuilderId<>(entity);
        PkValue pkValue = builder.getPrimaryKeyValues();
        assertNotNull(pkValue);
        assertEquals("test-id", pkValue.get(0));
    }

    @Test
    public void testSetName() {
        BuilderIdNameDesProp<?> builder = new BuilderIdNameDesProp<>(entity);
        builder.setName("test-name");
        assertEquals("test-name", entity.getProperty(EP_NAME));
    }

    @Test
    public void testGetName() {
        entity.setProperty(EP_NAME, "test-name");
        BuilderIdNameDesProp<?> builder = new BuilderIdNameDesProp<>(entity);
        assertEquals("test-name", builder.getName());
    }

    @Test
    public void testSetDescription() {
        BuilderIdNameDesProp<?> builder = new BuilderIdNameDesProp<>(entity);
        builder.setDescription("test-description");
        assertEquals("test-description", entity.getProperty(EP_DESCRIPTION));
    }

    @Test
    public void testGetDescription() {
        entity.setProperty(EP_DESCRIPTION, "test-description");
        BuilderIdNameDesProp<?> builder = new BuilderIdNameDesProp<>(entity);
        assertEquals("test-description", builder.getDescription());
    }

    @Test
    public void testSetProperties() {
        BuilderIdNameDesProp<?> builder = new BuilderIdNameDesProp<>(entity);
        MapValue properties = new MapValue(TypeComplex.STA_MAP);
        properties.put("key1", "value1");
        builder.setProperties(properties);
        assertSame(properties, entity.getProperty(EP_PROPERTIES));
    }

    @Test
    public void testGetProperties() {
        MapValue properties = new MapValue(TypeComplex.STA_MAP);
        properties.put("key1", "value1");
        entity.setProperty(EP_PROPERTIES, properties);
        BuilderIdNameDesProp<?> builder = new BuilderIdNameDesProp<>(entity);
        assertSame(properties, builder.getProperties());
    }

}
