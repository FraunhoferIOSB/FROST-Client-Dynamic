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
package de.fraunhofer.iosb.ilt.frostclient.query;

import de.fraunhofer.iosb.ilt.frostclient.query.Expand.ExpandItem;

/**
 * Request parameters a query should support.
 *
 * @param <T> The exact class implementing this interface.
 */
public interface QueryParameter<T extends QueryParameter<T>> {

    /**
     * Add the select parameter as specified by the SensorThingsAPI
     * specification.
     *
     * @param fields the fields to select.
     * @return this
     */
    public T select(String... fields);

    /**
     * Add the filter parameter as specified by the SensorThingsAPI
     * specification.
     *
     * @param filter the filter options as a string
     * @return this
     */
    public T filter(String filter);

    /**
     * Add the top parameter as specified by the SensorThingsAPI specification.
     *
     * @param top the limit
     * @return this
     */
    public T top(int top);

    /**
     * Add the orderBy parameter as specified by the SensorThingsAPI
     * specification.
     *
     * @param orderBy the order clause
     * @return this
     */
    public T orderBy(String orderBy);

    /**
     * Add the skip parameter as specified by the SensorThingsAPI specification.
     *
     * @param skip the number of entities to skip
     * @return this
     */
    public T skip(int skip);

    /**
     * Add the count parameter as specified by the SensorThingsAPI
     * specification.
     *
     * @param count the value for the count parameter.
     * @return this
     */
    public T count(boolean count);

    /**
     * Add an expand to the query.
     *
     * @param expand the expand.
     * @return this
     */
    public T expand(Expand expand);

    /**
     * Add an expand item, creating an Expand if required.
     *
     * @param item the expandItem to add.
     * @return this
     */
    public T addExpandItem(ExpandItem item);
}
