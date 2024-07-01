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

import de.fraunhofer.iosb.ilt.frostclient.model.PropertyType;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OData enumeration property type.
 *
 * @param <K> The enum this Type extends.
 */
public class TypeEnumeration<K extends Enum<K>> extends PropertyType {

    private final Class<K> enumClass;

    public TypeEnumeration(String name, String description, Class<K> enumClass) {
        super(name, description, null, null);
        this.enumClass = enumClass;
    }

    public Class<K> getEnumClass() {
        return enumClass;
    }

    public Map<String, Number> getValues() {
        Map<String, Number> members = new LinkedHashMap<>();
        for (K member : enumClass.getEnumConstants()) {
            members.put(member.toString(), member.ordinal());
        }
        return members;
    }
}
