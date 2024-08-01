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
package de.fraunhofer.iosb.ilt.frostclient.models.swecommon.simple;

import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.AbstractDataComponent;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.util.NillValue;
import java.util.List;
import java.util.Objects;

/**
 * SWE AbstractSimpleComponent class.
 *
 * @param <T> The type of the extending class.
 * @param <V> The type of the Value field.
 */
public abstract class AbstractSimpleComponent<T extends AbstractSimpleComponent<T, V>, V> extends AbstractDataComponent<T, V> {

    /**
     * Axis ID
     *
     * A string that uniquely identifies one of the reference frame’s axes along
     * which the coordinate value is given.
     */
    private String axisID;

    /**
     * Reference Frame
     *
     * The reference frame relative to which the coordinate value is given.
     * Commonly an EPSG identifier.
     */
    private String referenceFrame;

    /**
     * NilValues
     *
     * a list (i.e. one or more) of NIL values.
     */
    private List<NillValue> nilValues;

    // TODO
    private Object quality;

    public String getReferenceFrame() {
        return referenceFrame;
    }

    public T setReferenceFrame(String referenceFrame) {
        this.referenceFrame = referenceFrame;
        return self();
    }

    public String getAxisID() {
        return axisID;
    }

    public T setAxisID(String axisID) {
        this.axisID = axisID;
        return self();
    }

    public List<NillValue> getNilValues() {
        return nilValues;
    }

    public T setNilValues(List<NillValue> nilValues) {
        this.nilValues = nilValues;
        return self();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.axisID);
        hash = 47 * hash + Objects.hashCode(this.referenceFrame);
        hash = 47 * hash + Objects.hashCode(this.nilValues);
        hash = 47 * hash + Objects.hashCode(this.quality);
        hash = 47 * hash + super.hashCode();
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
        final AbstractSimpleComponent other = (AbstractSimpleComponent) obj;
        if (!Objects.equals(this.axisID, other.axisID)) {
            return false;
        }
        if (!Objects.equals(this.referenceFrame, other.referenceFrame)) {
            return false;
        }
        if (!Objects.equals(this.nilValues, other.nilValues)) {
            return false;
        }
        if (!Objects.equals(this.quality, other.quality)) {
            return false;
        }
        return super.equals(obj);
    }

}
