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

import com.fasterxml.jackson.databind.JsonNode;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.constraint.AllowedTimes;
import de.fraunhofer.iosb.ilt.frostclient.models.swecommon.util.UnitOfMeasurement;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SWE Class TimeRange.
 */
public class TimeRange extends AbstractRange<TimeRange, String> {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeRange.class);

    /**
     * Reference Time
     *
     * The “referenceTime” attribute is used to specify a different time origin
     * than the one sometimes implied by the “referenceFrame”. This is used to
     * express a time relative to an arbitrary epoch (i.e. different from the
     * origin of a well known reference frame). The new time origin specified by
     * “referenceTime” shall be expressed with respect to the reference frame
     * specified and is of type “DateTime”. This forces the definition of this
     * origin as a calendar date/time combination.
     */
    private String referenceTime;

    /**
     * Local Frame
     *
     * The optional “localFrame” attribute allows for the definition of a local
     * temporal frame of reference through the value of the component (i.e. we
     * are specifying a time origin), as opposed to the referenceFrame which
     * specifies that the value of the component is in reference to this frame.
     */
    private String localFrame;

    /**
     * Unit of Measurement
     *
     * The “uom” attribute is mandatory since time is a continuous property that
     * shall always be expressed in a well defined scale. The only units allowed
     * are obviously time units.
     */
    private UnitOfMeasurement uom;

    /**
     * Constraint
     *
     * The “constraint” attribute allows further restricting the range of
     * possible time values.
     */
    private AllowedTimes constraint;

    /**
     * Value
     *
     * The starting end ending values of this TimeRange.
     */
    private List<String> value;

    public String getReferenceTime() {
        return referenceTime;
    }

    public TimeRange setReferenceTime(String referenceTime) {
        this.referenceTime = referenceTime;
        return this;
    }

    public String getLocalFrame() {
        return localFrame;
    }

    public TimeRange setLocalFrame(String localFrame) {
        this.localFrame = localFrame;
        return this;
    }

    public UnitOfMeasurement getUom() {
        return uom;
    }

    public TimeRange setUom(UnitOfMeasurement uom) {
        this.uom = uom;
        return this;
    }

    public AllowedTimes getConstraint() {
        return constraint;
    }

    public TimeRange setConstraint(AllowedTimes constraint) {
        this.constraint = constraint;
        return this;
    }

    @Override
    public List<String> getValue() {
        return value;
    }

    @Override
    public TimeRange setValue(List<String> value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean valueIsValid() {
        return validate(value);
    }

    @Override
    protected boolean validateArray(JsonNode input) {
        if (constraint == null) {
            return true;
        }
        for (JsonNode item : input) {
            if (!item.isTextual()) {
                LOGGER.debug("Non-text value {} for TimeRange.", input);
                return false;
            }
            if (!constraint.isValid(item.asText(), uom)) {
                LOGGER.error("Item '{}' does not fit the constraint", item);
                return false;
            }
        }
        return true;

    }

    @Override
    public boolean validate(List<String> input) {
        if (input == null) {
            return isOptional();
        }
        final int size = input.size();
        if (size != 2) {
            LOGGER.debug("Range must have 2 items, found: {}", size);
            return false;
        }
        if (constraint == null) {
            return true;
        }
        for (String item : input) {
            if (!constraint.isValid(item, uom)) {
                LOGGER.error("Item '{}' does not fit the constraint", item);
                return false;
            }
        }
        return true;

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.referenceTime);
        hash = 97 * hash + Objects.hashCode(this.localFrame);
        hash = 97 * hash + Objects.hashCode(this.uom);
        hash = 97 * hash + Objects.hashCode(this.constraint);
        hash = 97 * hash + Objects.hashCode(this.value);
        hash = 97 * hash + super.hashCode();
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
        final TimeRange other = (TimeRange) obj;
        if (!Objects.equals(this.referenceTime, other.referenceTime)) {
            return false;
        }
        if (!Objects.equals(this.localFrame, other.localFrame)) {
            return false;
        }
        if (!Objects.equals(this.uom, other.uom)) {
            return false;
        }
        if (!Objects.equals(this.constraint, other.constraint)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    protected TimeRange self() {
        return this;
    }

}
