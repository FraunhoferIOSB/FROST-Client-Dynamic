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

/**
 * Request parameters a query should support.
 */
public interface QueryParameter {

    /**
     * Add the filter parameter as specified by the SensorThingsAPI
     * specification.
     *
     * @param options the filter options as a string
     * @return the updated instance of the query
     */
    public Query filter(String options);

    /**
     * Add the top parameter as specified by the SensorThingsAPI specification.
     *
     * @param n the limit
     * @return the updated instance of the query
     */
    public Query top(int n);

    /**
     * Add the orderBy parameter as specified by the SensorThingsAPI
     * specification.
     *
     * @param clause the order clause
     * @return the updated instance of the query
     */
    public Query orderBy(String clause);

    /**
     * Add the skip parameter as specified by the SensorThingsAPI specification.
     *
     * @param n the number of entities to skip
     * @return the updated instance of the query
     */
    public Query skip(int n);

    /**
     * Add the count parameter as specified by the SensorThingsAPI
     * specification.
     *
     * @return the updated instance of the query
     */
    public Query count();
}
