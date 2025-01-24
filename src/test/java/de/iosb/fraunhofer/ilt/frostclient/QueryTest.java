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
package de.iosb.fraunhofer.ilt.frostclient;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.fraunhofer.iosb.ilt.frostclient.SensorThingsService;
import de.fraunhofer.iosb.ilt.frostclient.Version;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing;
import de.fraunhofer.iosb.ilt.frostclient.query.Expand.ExpandItem;
import de.fraunhofer.iosb.ilt.frostclient.query.Query;
import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import java.net.MalformedURLException;
import org.junit.jupiter.api.Test;

public class QueryTest {

    public QueryTest() {
    }

    @Test
    public void testFullQuery() throws MalformedURLException, ServiceFailureException {
        final SensorThingsV11Sensing mdlSensing = new SensorThingsV11Sensing();
        SensorThingsService service = new SensorThingsService(mdlSensing)
                .setBaseUrl(SensorThingsService.NULL_URL_V11)
                .setVersion(Version.V_1_1);
        Query q = new Query(service, mdlSensing.etThing)
                .top(5)
                .addExpandItem(new ExpandItem(mdlSensing.npThingDatastreams)
                        .top(5)
                        .filter("startswith(name,'A')")
                        .addExpandItem(new ExpandItem(mdlSensing.npDatastreamObservations)
                                .top(1)
                                .orderBy("phenomenonTime desc")
                                .select("result", "phenomenonTime")));
        final String expectedQuery = StringHelper.urlEncode("$top") + "=5&"
                + StringHelper.urlEncode("$expand") + "="
                + StringHelper.urlEncode("Datastreams($top=5;$filter=startswith(name,'A');$expand=Observations($top=1;$select=result,phenomenonTime;$orderby=phenomenonTime desc))");
        assertEquals(SensorThingsService.NULL_URL_V11 + "Things?" + expectedQuery, q.buildUrl().toString());
    }

    @Test
    public void testExpand() throws MalformedURLException, ServiceFailureException {
        final SensorThingsV11Sensing mdlSensing = new SensorThingsV11Sensing();
        ExpandItem expandItem = new ExpandItem(mdlSensing.npThingDatastreams)
                .top(5)
                .filter("startswith(name,'A')")
                .addExpandItem(new ExpandItem(mdlSensing.npDatastreamObservations)
                        .top(1)
                        .orderBy("phenomenonTime desc")
                        .select("result", "phenomenonTime"));
        final String expectedQuery1 = "Datastreams($top=5;$filter=startswith(name,'A');$expand=Observations($top=1;$select=result,phenomenonTime;$orderby=phenomenonTime desc))";
        assertEquals(expectedQuery1, expandItem.toUrl());

        final String expectedQuery2 = "$top=5&$filter=startswith(name,'A')&$expand=Observations($top=1;$select=result,phenomenonTime;$orderby=phenomenonTime desc)";
        assertEquals(expectedQuery2, expandItem.toUrlAsQuery());
    }

}
