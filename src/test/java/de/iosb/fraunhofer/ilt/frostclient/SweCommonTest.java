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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.ilt.frostclient.json.SimpleJsonMapper;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.complex.DataRecord;
import de.fraunhofer.iosb.ilt.frostclient.utils.CollectionsHelper;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SweCommonTest {

    private final static String DATA_RECORD = """
            {
              "type": "DataRecord",
              "label": "Weather Data Record",
              "fields": [
                {
                  "name": "bikeCount",
                  "type": "Count",
                  "definition": "http://mmisw.org/ont/cf/parameter/bikeCount",
                  "label": "Bike Count",
                    "constraint": {
                      "values": [5, 6, 7]
                    }
                },
                {
                  "name": "pressure",
                  "type": "Quantity",
                  "optional": true,
                  "definition": "http://mmisw.org/ont/cf/parameter/air_pressure_at_mean_sea_level",
                  "label": "Air Pressure",
                  "uom": { "code": "mbar" }
                },
                {
                  "name": "model",
                  "type": "Text",
                  "optional": true,
                  "definition": "http://sensorml.com/ont/swe/property/ModelNumber",
                  "label": "Model Number",
                  "constraint": {
                    "pattern": "^[A-Z]{3}[0-9]{2}S1$"
                  }
                }
              ]
            }""";

    @Test
    public void testDataRecord() throws JsonProcessingException {
        ObjectMapper om = SimpleJsonMapper.getSimpleObjectMapper();
        DataRecord dr = om.readValue(DATA_RECORD, DataRecord.class);
        Map<String, Object> value = CollectionsHelper.propertiesBuilder()
                .addItem("bikeCount", 5)
                .addItem("pressure", 1000.5)
                .buildMap();
        Assertions.assertTrue(dr.validate(value));

        value = CollectionsHelper.propertiesBuilder()
                .addItem("bikeCount", 5.0)
                .addItem("pressure", 1000.5)
                .buildMap();
        Assertions.assertTrue(dr.validate(value));

        value = CollectionsHelper.propertiesBuilder()
                .addItem("bikeCount", 5.1)
                .addItem("pressure", 1000.5)
                .buildMap();
        Assertions.assertFalse(dr.validate(value));

        value = CollectionsHelper.propertiesBuilder()
                .addItem("pressure", 1000.5)
                .buildMap();
        Assertions.assertFalse(dr.validate(value));

        value = CollectionsHelper.propertiesBuilder()
                .addItem("bikeCount", 5)
                .buildMap();
        Assertions.assertTrue(dr.validate(value));

        value = CollectionsHelper.propertiesBuilder()
                .addItem("bikeCount", 4)
                .buildMap();
        Assertions.assertFalse(dr.validate(value));

        value = CollectionsHelper.propertiesBuilder()
                .addItem("bikeCount", "5.1")
                .addItem("pressure", 1000.5)
                .buildMap();
        Assertions.assertFalse(dr.validate(value));

        value = CollectionsHelper.propertiesBuilder()
                .addItem("model", "ABC12S1")
                .addItem("bikeCount", 5)
                .buildMap();
        Assertions.assertTrue(dr.validate(value));

        value = CollectionsHelper.propertiesBuilder()
                .addItem("model", "ABC12")
                .addItem("bikeCount", 5)
                .buildMap();
        Assertions.assertFalse(dr.validate(value));

        value = CollectionsHelper.propertiesBuilder()
                .addItem("model", "AB12S1")
                .addItem("bikeCount", 5)
                .buildMap();
        Assertions.assertFalse(dr.validate(value));
    }

}
