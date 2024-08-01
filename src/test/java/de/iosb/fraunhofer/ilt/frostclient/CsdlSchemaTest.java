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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.ilt.frostclient.json.SimpleJsonMapper;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.csdl.CsdlDocument;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the CSDL Schema generation and parsing.
 */
public class CsdlSchemaTest {

    @Test
    public void testSchemaExplicit() throws IOException {
        final ObjectMapper objectMapper = SimpleJsonMapper.getSimpleObjectMapper();
        final String schemaData = IOUtils.resourceToString("model/csdl_sensorthings_explicit.json", StandardCharsets.UTF_8, getClass().getClassLoader());
        CsdlDocument csdlDocument = objectMapper.readValue(schemaData, CsdlDocument.class);
        String schemaDataRebuild = objectMapper.writeValueAsString(csdlDocument);
        Assertions.assertTrue(jsonEquals(schemaData, schemaDataRebuild));
    }

    @Test
    public void testSchemaImplicit() throws IOException {
        final ObjectMapper objectMapper = SimpleJsonMapper.getSimpleObjectMapper();
        final String schemaDataExplicit = IOUtils.resourceToString("model/csdl_sensorthings_explicit.json", StandardCharsets.UTF_8, getClass().getClassLoader());
        final String schemaData = IOUtils.resourceToString("model/csdl_sensorthings.json", StandardCharsets.UTF_8, getClass().getClassLoader());
        CsdlDocument csdlDocument = objectMapper.readValue(schemaData, CsdlDocument.class);
        String schemaDataRebuild1 = objectMapper.writeValueAsString(csdlDocument);
        Assertions.assertTrue(jsonEquals(schemaDataExplicit, schemaDataRebuild1));
    }

    /**
     * Fully loads a CSDL Schema into a ModelRegistry, and writes the
     * ModelRegistry back to the CSDL Schema.
     *
     * @throws IOException if JSON reading/writing fails.
     */
    @Test
    public void testLoadSchemaExplicit() throws IOException {
        final ObjectMapper objectMapper = SimpleJsonMapper.getSimpleObjectMapper();
        final String schemaData = IOUtils.resourceToString("model/csdl_sensorthings_explicit.json", StandardCharsets.UTF_8, getClass().getClassLoader());
        CsdlDocument csdlDocument = objectMapper.readValue(schemaData, CsdlDocument.class);
        ModelRegistry mr = new ModelRegistry();
        csdlDocument.applyTo(mr);

        CsdlDocument csdlDocument2 = CsdlDocument.of(mr);
        String schemaDataRebuild = objectMapper.writeValueAsString(csdlDocument2);

        Assertions.assertTrue(jsonEquals(schemaData, schemaDataRebuild));
    }

    public boolean jsonEquals(String expectedJson, String actualJson) throws JsonProcessingException {
        final ObjectMapper objectMapper = SimpleJsonMapper.getSimpleObjectMapper();
        final JsonNode tree1 = objectMapper.readTree(expectedJson);
        final JsonNode tree2 = objectMapper.readTree(actualJson);
        String json1 = objectMapper.writeValueAsString(tree1);
        String json2 = objectMapper.writeValueAsString(tree2);
        return tree1.equals(tree2);
    }
}
