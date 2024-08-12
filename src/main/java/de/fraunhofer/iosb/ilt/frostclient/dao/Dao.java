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
package de.fraunhofer.iosb.ilt.frostclient.dao;

import com.github.fge.jsonpatch.JsonPatchOperation;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.PkValue;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.query.Query;
import java.net.URI;
import java.util.List;

/**
 * CRUD operations for Entity Types.
 */
public interface Dao {

    /**
     * Get the EntityType this Dao operates on.
     *
     * @return the EntityType this Dao operates on.
     */
    public EntityType getEntityType();

    /**
     * Create a new entity.
     *
     * @param entity the entity to create
     * @throws ServiceFailureException the operation failed
     */
    void create(Entity entity) throws ServiceFailureException;

    /**
     * Find an entity.
     *
     * @param primaryKeyValues the values for the entity's unique id
     * @return the entity
     * @throws ServiceFailureException the operation failed
     */
    Entity find(PkValue primaryKeyValues) throws ServiceFailureException;

    /**
     * Find the entity related to the given parent, like the Thing for a
     * Datastream.
     *
     * @param parent The parent to find the singular entity for.
     * @param npe The navigation property to load.
     * @return the singular entity linked from the parent.
     * @throws ServiceFailureException the operation failed
     */
    public Entity find(Entity parent, NavigationPropertyEntity npe) throws ServiceFailureException;

    /**
     * Find an entity.
     *
     * @param uri the entity's URI
     * @return the entity
     * @throws ServiceFailureException the operation failed
     */
    Entity find(URI uri) throws ServiceFailureException;

    /**
     * Update an entity.
     *
     * @param entity the entity to update
     * @throws ServiceFailureException the operation failed
     */
    void update(Entity entity) throws ServiceFailureException;

    /**
     * Update the given entity with the given patch. Does not update the entity
     * object itself. To see the result, fetch it anew from the server.
     *
     * @param entity The entity to update on the server.
     * @param patch The patch to apply to the entity.
     * @throws ServiceFailureException the operation failed
     */
    void patch(Entity entity, List<JsonPatchOperation> patch) throws ServiceFailureException;

    /**
     * Delete an entity.
     *
     * @param entity the entity to delete
     * @throws ServiceFailureException the operation failed
     */
    void delete(Entity entity) throws ServiceFailureException;

    /**
     * Start a query to find an entity collection.
     *
     * @return the query
     */
    Query query();

}
