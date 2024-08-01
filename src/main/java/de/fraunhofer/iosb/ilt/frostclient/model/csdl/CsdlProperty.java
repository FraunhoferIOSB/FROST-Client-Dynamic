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

import static de.fraunhofer.iosb.ilt.frostclient.model.csdl.CsdlPropertyEntity.NAME_KIND_ENTITYPROPERTY;
import static de.fraunhofer.iosb.ilt.frostclient.model.csdl.CsdlPropertyNavigation.NAME_KIND_NAVIGATIONPROPERTY;
import static de.fraunhofer.iosb.ilt.frostclient.utils.ParserUtils.objectToBoolean;

import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive;
import java.util.Map;
import java.util.Objects;

/**
 * A property in an entity type.
 */
public abstract class CsdlProperty extends CsdlSchemaItemAbstract {

    public CsdlProperty(String kind) {
        super(kind);
    }

    public abstract void applyTo(ModelRegistry mr, EntityType entityType, String name);

    public abstract void applyTo(ModelRegistry mr, TypeComplex complexType, String name);

    public static CsdlProperty of(String name, Map data) {
        String type = Objects.toString(data.get("$Type"), TypePrimitive.EDM_STRING_NAME);
        String kind = Objects.toString(data.get("$Kind"), NAME_KIND_ENTITYPROPERTY);
        boolean nullable = objectToBoolean(data.get("$Nullable"), false);
        boolean collection = objectToBoolean(data.get("$Collection"), false);

        switch (kind) {
            case NAME_KIND_ENTITYPROPERTY:
                return new CsdlPropertyEntity()
                        .setType(type)
                        .setNullable(nullable)
                        .setCollection(collection);

            case NAME_KIND_NAVIGATIONPROPERTY:
                String partner = Objects.toString(data.get("$Partner"));
                return new CsdlPropertyNavigation()
                        .setType(type)
                        .setCollection(collection)
                        .setNullable(nullable)
                        .setPartner(partner);
        }
        return null;
    }
}
