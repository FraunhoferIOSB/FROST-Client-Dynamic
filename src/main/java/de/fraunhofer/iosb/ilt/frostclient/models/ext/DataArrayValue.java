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
package de.fraunhofer.iosb.ilt.frostclient.models.ext;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iosb.ilt.frostclient.exception.ServiceFailureException;
import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties;
import de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsV11Sensing;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The "value" in a data array response or request.
 */
@JsonIgnoreProperties("dataArray@iot.count")
public class DataArrayValue {

    /**
     * The observation properties that can appear in a DataArray.
     */
    public static enum DaArProperty {
        Id("id"),
        PhenomenonTime("phenomenonTime"),
        Result("result"),
        ResultTime("resultTime"),
        ResultQuality("resultQuality"),
        ValidTime("validTime"),
        Parameters("parameters"),
        FeatureOfInterest("FeatureOfInterest/id");

        public final String name;

        private DaArProperty(String name) {
            this.name = name;
        }

    }

    /**
     * The set of properties used in a specific dataArray.
     */
    public static class VisibleProperties {

        public final boolean id;
        public final boolean phenomenonTime;
        public final boolean result;
        public final boolean resultTime;
        public final boolean resultQuality;
        public final boolean validTime;
        public final boolean parameters;
        public final boolean featureOfInterest;

        public VisibleProperties() {
            this(false);
        }

        public VisibleProperties(boolean allValue) {
            id = allValue;
            phenomenonTime = allValue;
            result = allValue;
            resultTime = allValue;
            resultQuality = allValue;
            validTime = allValue;
            parameters = allValue;
            featureOfInterest = allValue;
        }

        public VisibleProperties(Set<DaArProperty> select) {
            id = select.contains(DaArProperty.Id);
            phenomenonTime = select.contains(DaArProperty.PhenomenonTime);
            result = select.contains(DaArProperty.Result);
            resultTime = select.contains(DaArProperty.ResultTime);
            resultQuality = select.contains(DaArProperty.ResultQuality);
            validTime = select.contains(DaArProperty.ValidTime);
            parameters = select.contains(DaArProperty.Parameters);
            featureOfInterest = select.contains(DaArProperty.FeatureOfInterest);
        }

        public List<String> getComponents() {
            List<String> components = new ArrayList<>();
            if (id) {
                components.add(DaArProperty.Id.name);
            }
            if (phenomenonTime) {
                components.add(DaArProperty.PhenomenonTime.name);
            }
            if (result) {
                components.add(DaArProperty.Result.name);
            }
            if (resultTime) {
                components.add(DaArProperty.ResultTime.name);
            }
            if (resultQuality) {
                components.add(DaArProperty.ResultQuality.name);
            }
            if (validTime) {
                components.add(DaArProperty.ValidTime.name);
            }
            if (parameters) {
                components.add(DaArProperty.Parameters.name);
            }
            if (featureOfInterest) {
                components.add(DaArProperty.FeatureOfInterest.name);
            }
            return components;
        }

        public List<Object> fromObservation(Entity o) {
            List<Object> value = new ArrayList<>();
            if (id) {
                value.add(o.getProperty(CommonProperties.EP_ID));
            }
            if (phenomenonTime) {
                value.add(o.getProperty(SensorThingsV11Sensing.EP_PHENOMENONTIME));
            }
            if (result) {
                value.add(o.getProperty(SensorThingsV11Sensing.EP_RESULT));
            }
            if (resultTime) {
                value.add(o.getProperty(SensorThingsV11Sensing.EP_RESULTTIME));
            }
            if (resultQuality) {
                value.add(o.getProperty(SensorThingsV11Sensing.EP_RESULTQUALITY));
            }
            if (validTime) {
                value.add(o.getProperty(SensorThingsV11Sensing.EP_VALIDTIME));
            }
            if (parameters) {
                value.add(o.getProperty(SensorThingsV11Sensing.EP_PARAMETERS));
            }
            if (featureOfInterest) {
                try {
                    NavigationPropertyEntity npFoI = o.getType().getNavigationPropertyEntity("FeatureOfInterest");
                    value.add(o.getProperty(npFoI).getPrimaryKeyValues().get(0));
                } catch (ServiceFailureException ex) {
                    value.add(null);
                }
            }
            return value;
        }
    }

    @JsonProperty("Datastream")
    private Entity datastream;

    @JsonProperty("MultiDatastream")
    private Entity multiDatastream;

    @JsonProperty("components")
    private List<String> components;

    @JsonProperty("dataArray")
    private List<List<Object>> dataArray = new ArrayList<>();

    @JsonIgnore
    private VisibleProperties visibleProperties;

    @JsonIgnore
    private List<Entity> observations = new ArrayList<>();

    public DataArrayValue() {
        this.visibleProperties = new VisibleProperties(true);
        this.components = visibleProperties.getComponents();
    }

    public DataArrayValue(Entity datastream, Set<DaArProperty> properties) {
        if ("Datastream".equals(datastream.getType().name)) {
            this.datastream = datastream.withOnlyPk();
        } else if ("MultiDatastream".equals(datastream.getType().name)) {
            this.multiDatastream = datastream.withOnlyPk();
        }
        this.visibleProperties = new VisibleProperties(properties);
        this.components = visibleProperties.getComponents();
    }

    public Entity getDatastream() {
        if (datastream != null) {
            return datastream;
        }
        return multiDatastream;
    }

    public void setDatastream(Entity datastream) {
        if ("Datastream".equals(datastream.getType().name)) {
            if (this.multiDatastream != null) {
                throw new IllegalArgumentException("Can not have both a Datastream and a MultiDatastream.");
            }
            this.datastream = datastream.withOnlyPk();
            return;
        }
        if ("MultiDatastream".equals(datastream.getType().name)) {
            if (this.datastream != null) {
                throw new IllegalArgumentException("Can not have both a Datastream and a MultiDatastream.");
            }
            this.multiDatastream = datastream.withOnlyPk();
        }
    }

    public List<String> getComponents() {
        return components;
    }

    /**
     * Set the components to transmit. Throws IllegalStateException when
     * observations have already be added.
     *
     * @param properties The components to set.
     */
    public void setComponents(Set<DaArProperty> properties) {
        if (!dataArray.isEmpty()) {
            throw new IllegalStateException("Can not change components after adding Observations.");
        }
        visibleProperties = new VisibleProperties(properties);
        components = visibleProperties.getComponents();
    }

    /**
     * Add an observation to this DataArray. The Datastream or MultiDatastream
     * of the Observation is ignored.
     *
     * @param o The Observation to add.
     */
    public void addObservation(Entity o) {
        dataArray.add(visibleProperties.fromObservation(o));
        observations.add(o);
    }

    public List<Entity> getObservations() {
        return observations;
    }

    public List<List<Object>> getDataArray() {
        return dataArray;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.datastream);
        hash = 29 * hash + Objects.hashCode(this.multiDatastream);
        hash = 29 * hash + Objects.hashCode(this.components);
        hash = 29 * hash + Objects.hashCode(this.dataArray);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataArrayValue other = (DataArrayValue) obj;
        if (!Objects.equals(this.datastream, other.datastream)) {
            return false;
        }
        if (!Objects.equals(this.multiDatastream, other.multiDatastream)) {
            return false;
        }
        if (!Objects.equals(this.components, other.components)) {
            return false;
        }
        return Objects.equals(this.dataArray, other.dataArray);
    }

    /**
     * Helper for generating a key for a DataArray for a given Observation. The
     * Observation must have a (Multi)Datastream that already exists in the
     * server (has an id).
     *
     * @param observation the observation to generate a key for.
     * @return a key to use for the DataArray for the Observation.
     */
    public static String dataArrayKeyFor(Entity observation) {
        EntityType etObs = observation.getType();
        NavigationPropertyEntity npDatastream = etObs.getNavigationPropertyEntity("Datastream");

        Entity ds = null;
        try {
            ds = observation.getProperty(npDatastream);
        } catch (ServiceFailureException ex) {
            // No Datastream.
        }
        if (ds == null) {
            NavigationPropertyEntity npMultiDatastream = etObs.getNavigationPropertyEntity("MultiDatastream");
            Entity mds = null;
            try {
                mds = observation.getProperty(npMultiDatastream);
            } catch (ServiceFailureException ex) {
                // No Mds.
            }
            if (mds == null) {
                throw new IllegalArgumentException("Observation must have a Datastream or MultiDatastream.");
            }
            return "mds-" + mds.getPrimaryKeyValues().toString();
        }
        return "ds-" + ds.getPrimaryKeyValues().toString();
    }
}
