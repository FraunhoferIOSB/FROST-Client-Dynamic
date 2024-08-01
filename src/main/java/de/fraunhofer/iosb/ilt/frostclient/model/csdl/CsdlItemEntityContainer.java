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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationProperty;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CsdlItemEntityContainer extends CsdlSchemaItemAbstract {

    public static final String NAME_KIND_ENTITYCONTAINER = "EntityContainer";

    @JsonAnyGetter
    @JsonAnySetter
    public Map<String, ContainerItem> containers = new LinkedHashMap<>();

    public CsdlItemEntityContainer() {
        super(NAME_KIND_ENTITYCONTAINER);
    }

    public CsdlItemEntityContainer fillFrom(String nameSpace, ModelRegistry registry) {
        for (var container : registry.getContainers().entrySet()) {
            containers.put(container.getKey(), ContainerItem.of(nameSpace, container.getValue()));
        }
        return this;
    }

    public void applyTo(ModelRegistry mr, String name) {
        for (var entry : containers.entrySet()) {
            entry.getValue().applyTo(mr, entry.getKey());
        }
    }

    @Override
    public void writeXml(String nameSpace, String name, Writer writer) throws IOException {
        writer.write("<EntityContainer Name=\"" + name + "\">");
        for (Map.Entry<String, ContainerItem> entry : containers.entrySet()) {
            String propName = entry.getKey();
            ContainerItem property = entry.getValue();
            property.writeXml(propName, writer);
        }

        writer.write("</EntityContainer>");
    }

    public static class ContainerItem {

        @JsonProperty("$Collection")
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public Boolean collection;

        @JsonProperty("$Type")
        public String type;

        @JsonProperty("$NavigationPropertyBinding")
        public Map<String, String> navPropBinding = new HashMap<>();

        public ContainerItem fillFrom(String nameSpace, EntityType et) {
            collection = true;
            type = et.entityName;
            for (NavigationProperty np : et.getNavigationProperties()) {
                navPropBinding.put(np.getName(), np.getEntityType().mainSet);
            }
            return this;
        }

        public void applyTo(ModelRegistry mr, String name) {
            EntityType entityType = mr.getEntityTypeForName(type);
            entityType.setMainContainer(name);
            mr.registerEntityContainer(name, entityType);
        }

        public void writeXml(String name, Writer writer) throws IOException {
            writer.write("<EntitySet Name=\"" + name + "\" EntityType=\"" + type + "\">");
            for (Map.Entry<String, String> entry : navPropBinding.entrySet()) {
                writer.write("<NavigationPropertyBinding Path=\"" + entry.getKey() + "\" Target=\"" + entry.getValue() + "\" />");
            }
            writer.write("</EntitySet>");
        }

        public static ContainerItem of(String nameSpace, EntityType et) {
            return new ContainerItem().fillFrom(nameSpace, et);
        }
    }

    public static CsdlItemEntityContainer of(String nameSpace, ModelRegistry registry) {
        return new CsdlItemEntityContainer().fillFrom(nameSpace, registry);
    }
}
