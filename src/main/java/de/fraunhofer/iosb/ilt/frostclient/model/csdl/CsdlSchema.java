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
package de.fraunhofer.iosb.ilt.frostclient.model.csdl;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.PropertyType;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeSimple;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An OData CSDL Schema.
 */
public class CsdlSchema {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsdlSchema.class.getName());

    private String namespace;
    private String entityContainer;

    private CsdlDocument document;

    @JsonAnyGetter
    public Map<String, CsdlSchemaItem> schemaItems = new LinkedHashMap<>();

    @JsonIgnore
    public String getNamespace() {
        return namespace;
    }

    @JsonIgnore
    public String getEntityContainerName() {
        return entityContainer;
    }

    @JsonIgnore
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @JsonIgnore
    public Map<String, CsdlSchemaItem> getSchemaItems() {
        return schemaItems;
    }

    @JsonAnySetter
    public void addSchemaItem(String name, CsdlSchemaItem schemaItem) {
        schemaItem.setDocument(document);
        schemaItem.setSchema(this);
        schemaItems.put(name, schemaItem);
    }

    public void setDocument(CsdlDocument document) {
        this.document = document;
    }

    public CsdlSchema fillFrom(CsdlDocument doc, String namespace, String entityContainer, ModelRegistry mr) {
        this.document = doc;
        this.namespace = namespace;
        this.entityContainer = entityContainer;
        for (EntityType entityType : mr.getEntityTypes()) {
            final String fullName = entityType.entityName;
            final String shortName = removeNamespace(fullName);
            schemaItems.put(shortName, CsdlItemEntityType.of(doc, namespace, entityType));
        }
        for (Entry<String, PropertyType> entry : mr.getPropertyTypes().entrySet()) {
            String name = removeNamespace(entry.getKey());
            PropertyType value = entry.getValue();
            if (value instanceof TypeComplex tc) {
                schemaItems.put(name, CsdlItemComplexType.of(doc, namespace, tc));
            } else if (value instanceof TypeSimple ts) {
                schemaItems.put(name, CsdlItemTypeDefinition.of(doc, namespace, ts));
            } else {
                LOGGER.debug("Unknown PropertyType {}", value);
            }
        }
        schemaItems.put(entityContainer, CsdlItemEntityContainer.of(namespace, mr));

        return this;
    }

    private String removeNamespace(String name) {
        if (name.startsWith(namespace)) {
            return name.substring(namespace.length() + 1);
        }
        return name;
    }

    public void applyTo(ModelRegistry mr) {
        Map<String, CsdlItemEntityType> entityTypes = new LinkedHashMap<>();
        Map<String, CsdlItemTypeDefinition> typeDefs = new LinkedHashMap<>();
        Map<String, CsdlItemComplexType> complexTypes = new LinkedHashMap<>();
        Map<String, CsdlItemEntityContainer> itemContainers = new LinkedHashMap<>();
        for (var entry : schemaItems.entrySet()) {
            String name = entry.getKey();
            CsdlSchemaItem item = entry.getValue();
            if (item instanceof CsdlItemEntityType et) {
                entityTypes.put(name, et);
            } else if (item instanceof CsdlItemTypeDefinition td) {
                typeDefs.put(name, td);
            } else if (item instanceof CsdlItemComplexType ct) {
                complexTypes.put(name, ct);
            } else if (item instanceof CsdlItemEntityContainer ec) {
                itemContainers.put(name, ec);
            }
        }
        final String prefix = getNamespace() + '.';
        for (var entry : typeDefs.entrySet()) {
            entry.getValue().applyTo(mr, prefix + entry.getKey());
        }
        for (var entry : complexTypes.entrySet()) {
            entry.getValue().applyTo(mr, prefix + entry.getKey());
        }
        for (var entry : entityTypes.entrySet()) {
            final CsdlItemEntityType ciEt = entry.getValue();
            ciEt.setNamespace(namespace);
            ciEt.applyTo(mr, prefix + entry.getKey());
        }
        for (var entry : entityTypes.entrySet()) {
            entry.getValue().applyPropertiesTo(mr, prefix + entry.getKey());
        }
        for (var entry : itemContainers.entrySet()) {
            entry.getValue().applyTo(mr, prefix + entry.getKey());
        }

    }

    public void writeXml(String nameSpace, Writer writer) throws IOException {
        writer.write("<Schema Namespace=\"" + nameSpace + "\" xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">");
        for (Map.Entry<String, CsdlSchemaItem> entry : schemaItems.entrySet()) {
            String name = entry.getKey();
            CsdlSchemaItem item = entry.getValue();
            item.writeXml(nameSpace, name, writer);
        }
        writer.write("</Schema>");
    }

    public static CsdlSchema of(CsdlDocument doc, String nameSpace, String entityContainer, ModelRegistry mr) {
        return new CsdlSchema().fillFrom(doc, nameSpace, entityContainer, mr);
    }
}
