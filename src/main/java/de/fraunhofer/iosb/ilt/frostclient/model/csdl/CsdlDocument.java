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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

/**
 * OData CSDL Document.
 */
public class CsdlDocument {

    public static final String VERSION_ODATA_40 = "4.0";
    public static final String VERSION_ODATA_401 = "4.01";

    @JsonProperty("$Version")
    public String version;

    @JsonProperty("$EntityContainer")
    public String entityContainer;

    @JsonAnyGetter
    public Map<String, CsdlSchema> nameSpaces = new LinkedHashMap<>();

    @JsonProperty("$Reference")
    private final Map<String, ReferencedDoc> referencedDocs = new TreeMap<>();

    private static class ReferencedDoc {

        @JsonProperty("$Include")
        private final List<ReferencedNamespace> includes = new ArrayList<>();

        @JsonIgnore
        private final String url;

        @JsonIgnore
        private final Map<String, ReferencedNamespace> namespaces = new TreeMap<>();

        @JsonIgnore
        private final Set<String> aliases = new TreeSet<>();

        public ReferencedDoc(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public ReferencedDoc addAnnotation(CsdlAnnotation annotation) {
            ReferencedNamespace refNs = namespaces.computeIfAbsent(annotation.getNamespace(), this::createReferencedNamespace);
            refNs.addAnnotation(annotation);
            return this;
        }

        public List<ReferencedNamespace> getIncludes() {
            return includes;
        }

        private ReferencedNamespace createReferencedNamespace(String namespace) {
            String[] parts = StringUtils.split(namespace, '.');
            String baseAlias = parts[parts.length - 2];
            int i = 1;
            String alias = baseAlias;
            while (aliases.contains(alias)) {
                alias = baseAlias + (i++);
            }
            final ReferencedNamespace refNs = new ReferencedNamespace(namespace).setAlias(alias);
            aliases.add(alias);
            includes.add(refNs);
            return refNs;
        }

        public void writeXml(Writer writer) throws IOException {
            String xmlUrl = url.replace(".json", ".xml");
            writer.write("<edmx:Reference Uri=\"" + xmlUrl + "\">");
            for (ReferencedNamespace include : includes) {
                include.writeXml(writer);
            }
            writer.write("</edmx:Reference>");
        }
    }

    private static class ReferencedNamespace {

        @JsonProperty("$Namespace")
        private final String namespace;
        @JsonProperty("$Alias")
        private String alias;

        @JsonIgnore
        private final List<CsdlAnnotation> annotations = new ArrayList<>();

        public ReferencedNamespace(String name) {
            this.namespace = name;
        }

        public String getNamespace() {
            return namespace;
        }

        public String getAlias() {
            return alias;
        }

        public ReferencedNamespace setAlias(String alias) {
            this.alias = alias;
            return this;
        }

        public List<CsdlAnnotation> getAnnotations() {
            return annotations;
        }

        public ReferencedNamespace addAnnotation(CsdlAnnotation annotation) {
            annotations.add(annotation);
            annotation.setNamespace(alias);
            return this;
        }

        public void writeXml(Writer writer) throws IOException {
            writer.write("<edmx:Include Alias=\"" + alias + "\" Namespace=\"" + namespace + "\" />");
        }
    }

    @JsonAnySetter
    public void addSchemaItem(String name, CsdlSchema schema) {
        schema.setDocument(this);
        schema.setNamespace(name);
        nameSpaces.put(name, schema);
    }

    /**
     * Fill the document using the given Settings, and return itself.
     *
     * @param mr the ModelRegistry to create the document from.
     * @return this.
     */
    public CsdlDocument fillFrom(ModelRegistry mr) {
        this.version = "4.01";
        String nameSpace = "de.FROST";
        String schemaName = "FrostService";
        entityContainer = nameSpace + '.' + schemaName;
        nameSpaces.put(nameSpace, CsdlSchema.of(this, nameSpace, schemaName, mr));
        return this;
    }

    public void applyTo(ModelRegistry mr) {
        for (CsdlSchema schema : nameSpaces.values()) {
            schema.applyTo(mr);
        }
        mr.initFinalise();
    }

    public void registerAnnotation(String baseUrl, CsdlAnnotation annotation) {
        referencedDocs.computeIfAbsent(baseUrl, ReferencedDoc::new).addAnnotation(annotation);
    }

    public void writeXml(Writer writer) throws IOException {
        if (VERSION_ODATA_40.equals(version)) {
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">");
        } else {
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"4.01\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">");
        }
        for (ReferencedDoc refDoc : referencedDocs.values()) {
            refDoc.writeXml(writer);
        }
        writer.write("<edmx:DataServices>");
        for (Entry<String, CsdlSchema> entry : nameSpaces.entrySet()) {
            String name = entry.getKey();
            CsdlSchema schema = entry.getValue();
            schema.writeXml(name, writer);
        }

        writer.write("</edmx:DataServices></edmx:Edmx>");
    }

    public static CsdlDocument of(ModelRegistry mr) {
        return new CsdlDocument().fillFrom(mr);
    }
}
