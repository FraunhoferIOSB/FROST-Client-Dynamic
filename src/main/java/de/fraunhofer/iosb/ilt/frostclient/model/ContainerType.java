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
package de.fraunhofer.iosb.ilt.frostclient.model;

import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import java.util.Map;
import java.util.Set;

/**
 * Interface for Complex Types and Entity Types that contain sub-properties.
 *
 * @param <T> The type of the implementing class, for method chaining.
 */
public interface ContainerType<T extends ContainerType<T>> {

    /**
     * Register a new Property on this container type.
     *
     * @param property The property to register.
     * @return this.
     */
    public T registerProperty(Property property);

    /**
     * Check if this type allows user-defined properties.
     *
     * @return true if this is an open type, false otherwise.
     */
    public boolean isOpenType();

    /**
     * Get all the entity properties registered on this container type.
     *
     * @return all the properties.
     */
    public Set<EntityPropertyMain> getEntityProperties();

    /**
     * Get the entity property with the given name.
     *
     * @param name The name of the property.
     * @return The property with the given name, or null.
     */
    public EntityPropertyMain getEntityProperty(String name);

    /**
     * Get the map of all properties by their name.
     *
     * @return The name-property map.
     */
    public Map<String, Property> getPropertiesByName();

}
