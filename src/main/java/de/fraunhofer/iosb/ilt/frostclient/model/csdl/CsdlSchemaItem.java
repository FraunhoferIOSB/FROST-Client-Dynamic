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

import static de.fraunhofer.iosb.ilt.frostclient.model.csdl.CsdlItemComplexType.NAME_KIND_COMPLEXTYPE;
import static de.fraunhofer.iosb.ilt.frostclient.model.csdl.CsdlItemEntityContainer.NAME_KIND_ENTITYCONTAINER;
import static de.fraunhofer.iosb.ilt.frostclient.model.csdl.CsdlItemEntityType.NAME_KIND_ENTITYTYPE;
import static de.fraunhofer.iosb.ilt.frostclient.model.csdl.CsdlItemTypeDefinition.NAME_KIND_TYPEDEFINITION;
import static de.fraunhofer.iosb.ilt.frostclient.model.csdl.CsdlPropertyEntity.NAME_KIND_ENTITYPROPERTY;
import static de.fraunhofer.iosb.ilt.frostclient.model.csdl.CsdlPropertyNavigation.NAME_KIND_NAVIGATIONPROPERTY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.io.Writer;

/**
 * Interface for schema items.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "$Kind")
@JsonTypeIdResolver(CsdlSchemaItem.SchemaItemIdResolver.class)
public interface CsdlSchemaItem {

    @JsonProperty("$Kind")
    public String getKind();

    public void writeXml(String nameSpace, String name, Writer writer) throws IOException;

    @JsonIgnore
    public CsdlDocument getDocument();

    @JsonIgnore
    public void setDocument(CsdlDocument document);

    @JsonIgnore
    public CsdlSchema getSchema();

    @JsonIgnore
    public void setSchema(CsdlSchema schema);

    public class SchemaItemIdResolver extends TypeIdResolverBase {

        private JavaType baseType;

        @Override
        public void init(JavaType bt) {
            baseType = bt;
            super.init(bt);
        }

        @Override
        public JavaType typeFromId(DatabindContext context, String id) throws IOException {
            if (id == null) {
                return TypeFactory.defaultInstance().constructSpecializedType(baseType, CsdlPropertyEntity.class);
            }

            switch (id) {
                case NAME_KIND_COMPLEXTYPE:
                    return TypeFactory.defaultInstance().constructSpecializedType(baseType, CsdlItemComplexType.class);
                case NAME_KIND_ENTITYCONTAINER:
                    return TypeFactory.defaultInstance().constructSpecializedType(baseType, CsdlItemEntityContainer.class);
                case NAME_KIND_ENTITYTYPE:
                    return TypeFactory.defaultInstance().constructSpecializedType(baseType, CsdlItemEntityType.class);
                case NAME_KIND_TYPEDEFINITION:
                    return TypeFactory.defaultInstance().constructSpecializedType(baseType, CsdlItemTypeDefinition.class);
                case NAME_KIND_NAVIGATIONPROPERTY:
                    return TypeFactory.defaultInstance().constructSpecializedType(baseType, CsdlPropertyNavigation.class);
                case NAME_KIND_ENTITYPROPERTY:
                case "":
                    return TypeFactory.defaultInstance().constructSpecializedType(baseType, CsdlPropertyEntity.class);
                default:
                    throw new IllegalArgumentException("Unknown $kind: " + id);
            }
        }

        @Override
        public String idFromValue(Object value) {
            if (value instanceof CsdlSchemaItem si) {
                return si.getKind();
            }
            return null;
        }

        @Override
        public String idFromValueAndType(Object value, Class<?> suggestedType) {
            if (value instanceof CsdlSchemaItem si) {
                return si.getKind();
            }
            return null;
        }

        @Override
        public JsonTypeInfo.Id getMechanism() {
            return JsonTypeInfo.Id.CUSTOM;
        }

    }
}
