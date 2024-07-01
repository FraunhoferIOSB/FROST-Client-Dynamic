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
package de.fraunhofer.iosb.ilt.frostclient.model.property.type;

import static de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive.EDM_DATETIMEOFFSET;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import de.fraunhofer.iosb.ilt.frostclient.model.ComplexValue;
import de.fraunhofer.iosb.ilt.frostclient.model.ComplexValueImpl;
import de.fraunhofer.iosb.ilt.frostclient.model.Property;
import de.fraunhofer.iosb.ilt.frostclient.model.PropertyType;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInterval;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeValue;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.AbstractDataComponent;
import de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils;
import de.fraunhofer.iosb.ilt.frostclient.utils.TypeReferencesHelper;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The PropertyType of Complex Properties.
 */
public class TypeComplex extends PropertyType {

    public static final String STA_MAP_NAME = "Object";
    public static final String STA_OBJECT_NAME = "ANY";
    public static final String STA_TIMEINTERVAL_NAME = "TimeInterval";
    public static final String STA_TIMEVALUE_NAME = "TimeValue";
    public static final String NAME_INTERVAL_START = "start";
    public static final String NAME_INTERVAL_END = "end";

    public static final EntityPropertyMain<TimeInstant> EP_START_TIME = new EntityPropertyMain<>(NAME_INTERVAL_START, EDM_DATETIMEOFFSET);
    public static final EntityPropertyMain<TimeInstant> EP_END_TIME = new EntityPropertyMain<>(NAME_INTERVAL_END, EDM_DATETIMEOFFSET);

    public static final TypeComplex STA_MAP = new TypeComplex(STA_MAP_NAME, "A free object that can contain anything", true, MapValue::new, TypeReferencesHelper.TYPE_REFERENCE_MAPVALUE);
    public static final TypeComplex STA_OBJECT = new TypeComplex(STA_OBJECT_NAME, "A free type, can be anything", true, null, TypeReferencesHelper.TYPE_REFERENCE_OBJECT);
    public static final TypeComplex STA_OBJECT_UNTYPED = new TypeComplex(STA_OBJECT_NAME, "A free type, can be anything", true, null, null, null);

    public static final TypeComplex STA_TIMEINTERVAL = new TypeComplex(STA_TIMEINTERVAL_NAME, "An ISO time interval.", false, TimeInterval::new, TypeReferencesHelper.TYPE_REFERENCE_TIMEINTERVAL)
            .registerProperty(EP_START_TIME)
            .registerProperty(EP_END_TIME);
    public static final TypeComplex STA_TIMEVALUE = new TypeComplex(STA_TIMEVALUE_NAME, "An ISO time instant or time interval.", false, TimeValue::new, TypeReferencesHelper.TYPE_REFERENCE_TIMEVALUE)
            .registerProperty(EP_START_TIME)
            .registerProperty(EP_END_TIME);

    public static final JsonDeserializer<AbstractDataComponent> temp = ParserUtils.getDefaultDeserializer(TypeReferencesHelper.TYPE_REFERENCE_ABSTRACTDATACOMPONENT);
    public static final TypeComplex STA_ABSTRACT_DATA_COMPONENT = new TypeComplex("AbstractDataComponent", "An SWE-Common AbstractDataComponent", true)
            .setSerializer(ParserUtils.getDefaultSerializer())
            .setDeserializer(ParserUtils.getDefaultDeserializer(TypeReferencesHelper.TYPE_REFERENCE_ABSTRACTDATACOMPONENT));

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeComplex.class.getName());
    private static final Map<String, TypeComplex> TYPES = new HashMap<>();

    static {
        for (Field field : FieldUtils.getAllFields(TypeComplex.class)) {
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            try {
                final TypeComplex type = (TypeComplex) FieldUtils.readStaticField(field, false);
                final String name = type.getName();
                TYPES.put(name, type);
                LOGGER.debug("Registered type: {}", name);
            } catch (IllegalArgumentException ex) {
                LOGGER.error("Failed to initialise: {}", field, ex);
            } catch (IllegalAccessException ex) {
                LOGGER.trace("Failed to initialise: {}", field, ex);
            } catch (ClassCastException ex) {
                // It's not a TypeSimplePrimitive
            }
        }
    }

    @Deprecated
    public static TypeComplex getType(String name) {
        return TYPES.get(name);
    }

    /**
     * The Set of PROPERTIES that Elements of this type have.
     */
    private final Set<Property> properties = new LinkedHashSet<>();

    /**
     * The Set of PROPERTIES that Entities of this type have, mapped by their
     * name.
     */
    private final Map<String, Property> propertiesByName = new LinkedHashMap<>();
    private final boolean openType;
    private Instantiator instantiator;

    public TypeComplex(String name, String description, boolean openType) {
        super(name, description, null, null);
        this.openType = openType;
        this.instantiator = ComplexValueImpl.createFor(this);
    }

    public TypeComplex(String name, String description, boolean openType, Instantiator instantiator, TypeReference tr) {
        super(name, description, ParserUtils.getDefaultDeserializer(tr), ParserUtils.getDefaultSerializer());
        this.openType = openType;
        this.instantiator = instantiator;
    }

    public TypeComplex(String name, String description, boolean openType, Instantiator instantiator, JsonDeserializer jd, JsonSerializer js) {
        super(name, description, jd, js);
        this.openType = openType;
        this.instantiator = instantiator;
    }

    public boolean isOpenType() {
        return openType;
    }

    public Set<Property> getProperties() {
        return properties;
    }

    public Property getProperty(String name) {
        return propertiesByName.get(name);
    }

    public Map<String, Property> getPropertiesByName() {
        return propertiesByName;
    }

    public boolean hasProperty(String name) {
        return propertiesByName.containsKey(name);
    }

    public TypeComplex registerProperty(Property property) {
        properties.add(property);
        propertiesByName.put(property.getName(), property);
        return this;
    }

    public TypeComplex setInstantiator(Instantiator instantiator) {
        this.instantiator = instantiator;
        return this;
    }

    @Override
    public TypeComplex setSerializer(JsonSerializer serializer) {
        super.setSerializer(serializer);
        return this;
    }

    @Override
    public TypeComplex setDeserializer(JsonDeserializer deserializer) {
        super.setDeserializer(deserializer);
        return this;
    }

    public ComplexValue instantiate() {
        return instantiator.instantiate();
    }

    public static interface Instantiator {

        public ComplexValue instantiate();
    }

    @Override
    public String toString() {
        return "TypeComplex: " + getName();
    }

}
