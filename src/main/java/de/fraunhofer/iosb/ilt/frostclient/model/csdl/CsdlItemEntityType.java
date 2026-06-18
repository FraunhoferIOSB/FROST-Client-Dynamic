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
package de.fraunhofer.iosb.ilt.frostclient.model.csdl;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.PkSingle;
import de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation.Annotation;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationProperty;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsdlItemEntityType extends CsdlSchemaItemAbstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsdlItemEntityType.class.getName());

    public static final String NAME_KIND_ENTITYTYPE = "EntityType";

    @JsonProperty("$Key")
    public List<String> key = new ArrayList<>();

    @JsonIgnore
    public Map<String, CsdlProperty> _properties = new LinkedHashMap<>();
    @JsonIgnore
    private final List<CsdlAnnotation> _annotations = new ArrayList<>();
    @JsonIgnore
    private String _namespace;

    public CsdlItemEntityType() {
        super(NAME_KIND_ENTITYTYPE);
    }

    public CsdlItemEntityType fillFrom(CsdlDocument doc, String nameSpace, EntityType et) {
        this._namespace = nameSpace;
        for (EntityPropertyMain keyProp : et.getPrimaryKey().getKeyProperties()) {
            String keyName = keyProp.getJsonName();
            if ("@iot.id".equals(keyName)) {
                keyName = "id";
            }
            key.add(keyName);
        }

        for (EntityPropertyMain ep : et.getEntityProperties()) {
            if (ep == ModelRegistry.EP_SELFLINK) {
                continue;
            }
            String propertyName = ep.getJsonName();
            if ("@iot.id".equals(propertyName)) {
                propertyName = "id";
            }
            _properties.put(propertyName, CsdlPropertyEntity.of(doc, nameSpace, et, ep));
        }
        for (NavigationProperty np : et.getNavigationProperties()) {
            _properties.put(np.getJsonName(), CsdlPropertyNavigation.of(doc, nameSpace, et, np));
        }
        for (Annotation an : et.getAnnotations()) {
            _annotations.add(CsdlAnnotation.of(doc, an));
        }

        return this;
    }

    public void setNamespace(String namespace) {
        this._namespace = namespace;
    }

    public void applyTo(ModelRegistry mr, String name) {
        EntityType entityType = mr.getEntityTypeForName(_namespace, name);
        if (entityType == null) {
            entityType = new EntityType(name);
            entityType.setNamespace(_namespace);
            mr.registerEntityType(entityType);
        } else {
            LOGGER.info("    EntityType {}.{} already registered.", _namespace, name);
        }
        LOGGER.debug("    Applied {}", name);
    }

    public void applyPropertiesTo(ModelRegistry mr, String name) {
        final EntityType entityType = mr.getEntityTypeForName(_namespace, name);
        for (var entry : _properties.entrySet()) {
            entry.getValue().applyTo(mr, entityType, entry.getKey());
        }
        if (key.size() == 1) {
            final EntityPropertyMain keyProp = entityType.getEntityProperty(key.get(0));
            keyProp.setReadOnly(true);
            PkSingle pk = new PkSingle(keyProp);
            entityType.setPrimaryKey(pk);
        } else {
            throw new NotImplementedException("Multi-Keyed entity types are not supported yet.");
        }
        LOGGER.debug("    Applied {}", name);
    }

    @JsonAnyGetter
    public Map<String, Object> otherProperties() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Entry<String, CsdlProperty> entry : _properties.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        for (CsdlAnnotation annotation : _annotations) {
            result.put('@' + annotation.getQualifiedName(), annotation.getValue());
        }
        return result;
    }

    @JsonAnySetter
    public void addProperty(String name, Object data) {
        if (name.startsWith("@")) {

        } else if (data instanceof CsdlProperty p) {
            _properties.put(name, p);
        } else if (data instanceof Map map) {
            _properties.put(name, CsdlProperty.of(name, map));
        }
    }

    @Override
    public void writeXml(String nameSpace, String name, Writer writer) throws IOException {
        writer.write("<EntityType Name=\"" + name + "\">");
        writer.write("<Key>");
        for (String keyName : key) {
            writer.write("<PropertyRef Name=\"" + keyName + "\" />");
        }
        writer.write("</Key>");
        for (Entry<String, CsdlProperty> entry : _properties.entrySet()) {
            String propName = entry.getKey();
            CsdlProperty property = entry.getValue();
            property.writeXml(nameSpace, propName, writer);
        }
        for (CsdlAnnotation an : _annotations) {
            an.writeXml(writer);
        }
        writer.write("</EntityType>");
    }

    public static CsdlItemEntityType of(CsdlDocument doc, String nameSpace, EntityType et) {
        return new CsdlItemEntityType().fillFrom(doc, nameSpace, et);
    }
}
