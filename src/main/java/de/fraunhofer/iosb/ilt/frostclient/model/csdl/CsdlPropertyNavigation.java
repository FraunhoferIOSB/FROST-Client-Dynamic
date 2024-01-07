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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.csdl.annotation.Annotation;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationProperty;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyAbstract;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsdlPropertyNavigation extends CsdlProperty {

    public static final String NAME_KIND_NAVIGATIONPROPERTY = "NavigationProperty";

    @JsonProperty("$Collection")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Boolean collection;

    @JsonProperty("$Partner")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public String partner;

    @JsonProperty("$Type")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public String type;

    @JsonProperty("$Nullable")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public boolean nullable;

    @JsonIgnore
    private final List<CsdlAnnotation> annotations = new ArrayList<>();

    public CsdlPropertyNavigation() {
        super(NAME_KIND_NAVIGATIONPROPERTY);
    }

    public CsdlPropertyNavigation fillFrom(CsdlDocument doc, String nameSpace, EntityType et, NavigationProperty np) {
        type = np.getType().getName();
        final NavigationProperty inverse = np.getInverse();
        if (inverse != null) {
            partner = inverse.getName();
        }
        if (np.isEntitySet()) {
            collection = true;
        }
        nullable = np.isNullable();
        for (Annotation an : np.getAnnotations()) {
            annotations.add(CsdlAnnotation.of(doc, an));
        }
        return this;
    }

    @Override
    public void applyTo(ModelRegistry mr, EntityType entityType, String name) {
        NavigationPropertyAbstract np = createProperty(mr, name);
        entityType.registerProperty(np);
    }

    @Override
    public void applyTo(ModelRegistry mr, TypeComplex ct, String name) {
        NavigationPropertyAbstract np = createProperty(mr, name);
        ct.registerProperty(np);
    }

    private NavigationPropertyAbstract createProperty(ModelRegistry mr, String name) {
        var targetType = mr.getEntityTypeForName(type);
        NavigationPropertyAbstract np;
        if (collection) {
            var npc = new NavigationPropertyEntitySet(name);
            np = npc;
        } else {
            var npe = new NavigationPropertyEntity(name);

            np = npe;
        }
        np.setEntityType(targetType);
        np.setNullable(nullable);
        NavigationPropertyAbstract inverse = targetType.getNavigationProperty(partner);
        if (inverse != null) {
            np.setInverses(inverse);
        }
        return np;
    }

    @JsonAnyGetter
    public Map<String, Object> otherProperties() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (CsdlAnnotation annotation : annotations) {
            result.put('@' + annotation.getQualifiedName(), annotation.getValue());
        }
        return result;
    }

    @Override
    public void writeXml(String nameSpace, String name, Writer writer) throws IOException {
        String finalType = type;
        if (collection != null && collection) {
            finalType = "Collection(" + type + ")";
        }
        String nullableString = "";
        if (nullable) {
            nullableString = " Nullable=\"" + Boolean.toString(nullable) + "\"";
        }
        String partnerString = "";
        if (!StringHelper.isNullOrEmpty(partner)) {
            partnerString = " Partner=\"" + partner + "\"";
        }
        writer.write("<NavigationProperty Name=\"" + name + "\" Type=\"" + finalType + "\"" + nullableString + partnerString);
        if (annotations.isEmpty()) {
            writer.write(" />");
        } else {
            writer.write(">");
            for (CsdlAnnotation an : annotations) {
                an.writeXml(writer);
            }
            writer.write("</NavigationProperty>");
        }
    }

    public CsdlPropertyNavigation setType(String type) {
        this.type = type;
        return this;
    }

    public CsdlPropertyNavigation setPartner(String partner) {
        this.partner = partner;
        return this;
    }

    public CsdlPropertyNavigation setNullable(Boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public CsdlPropertyNavigation setCollection(Boolean collection) {
        this.collection = collection;
        return this;
    }

    public static CsdlPropertyNavigation of(CsdlDocument doc, String nameSpace, EntityType et, NavigationProperty np) {
        return new CsdlPropertyNavigation().fillFrom(doc, nameSpace, et, np);
    }
}
