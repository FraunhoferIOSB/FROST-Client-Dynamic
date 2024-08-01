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

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import java.util.Iterator;
import java.util.List;

/**
 * The EntitySet model element.
 */
public interface EntitySet extends Iterable<Entity> {

    public void add(Entity entity);

    /**
     * Get The total number of entities in the Set that exist on the server.
     * Returns -1 if the count is not loaded.
     *
     * @return The total number of entities in the Set that exist on the server.
     */
    public long getCount();

    public void setCount(long count);

    /**
     * Get an iterator that iterates over all entities, following nextLinks if
     * needed.
     *
     * @return An iterator that iterates over all entities, following nextLinks
     * if needed.
     */
    @Override
    public Iterator<Entity> iterator();

    /**
     * Check if there is a nextLink.
     *
     * @return true if there is a nextLink to fetch more Entities.
     */
    boolean hasNextLink();

    /**
     * Get the nextLink, if it exists. NULL otherwise.
     *
     * @return the nextLink, if it exists.
     */
    public String getNextLink();

    public void setNextLink(String nextLink);

    /**
     * Use the nextLink to fetch more Entities.
     *
     * @throws ServiceFailureException If there is a problem following the
     * nextLink.
     */
    void fetchNext() throws ServiceFailureException;

    /**
     * Get the number of currently loaded Entities in the Set.
     *
     * @return The number of currently loaded Entities in the Set.
     */
    public int size();

    /**
     * Check if there are entities currently loaded in the set.
     *
     * @return true if there are no Entities currently loaded in the set.
     */
    public boolean isEmpty();

    /**
     * Get the currently loaded entities as a {@link List<Entity>}. This returns
     * the internal ArrayList.
     *
     * @return the list of <i>currently loaded</i> entities.
     */
    public List<Entity> toList();

    @JsonIgnore
    public EntityType getEntityType();

    /**
     * Get the navigationProperty that manages this EntitySet. Can be null, for
     * top-level entity sets.
     *
     * @return the navigationProperty that manages this EntitySet.
     */
    @JsonIgnore
    public NavigationPropertyEntitySet getNavigationProperty();

    /**
     * Returns the service for the set.
     *
     * @return the service for the set.
     */
    public SensorThingsService getService();

    /**
     * Sets the service for the set and all Entities in the set.
     *
     * @param service the service to set.
     */
    public void setService(SensorThingsService service);
}
