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
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SWE Time class.
 */
public class Time extends AbstractSimpleComponent<Time, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Time.class.getName());
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
     *
     * Default: http://www.opengis.net/def/uom/ISO‐8601/0/Gregorian
     */
    private UnitOfMeasurement uom;

    /**
     * Constraint.
     *
     * The “constraint” attribute allows further restricting the range of
     * possible time values.
     */
    private AllowedTimes constraint;

    /**
     * Value
     *
     * The “value” attribute (or the corresponding value in out-of-band data) is
     * of type “TimePosition” and must match the constraint.
     */
    private String value;

    public String getReferenceTime() {
        return referenceTime;
    }

    public Time setReferenceTime(String referenceTime) {
        this.referenceTime = referenceTime;
        return this;
    }

    public String getLocalFrame() {
        return localFrame;
    }

    public Time setLocalFrame(String localFrame) {
        this.localFrame = localFrame;
        return this;
    }

    public UnitOfMeasurement getUom() {
        return uom;
    }

    public Time setUom(UnitOfMeasurement uom) {
        this.uom = uom;
        return this;
    }

    public AllowedTimes getConstraint() {
        return constraint;
    }

    public Time setConstraint(AllowedTimes constraint) {
        this.constraint = constraint;
        return this;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Time setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean valueIsValid() {
        return validate(value);
    }

    @Override
    public boolean validate(Object input) {
        if (input == null) {
            return isOptional();
        }
        if (input instanceof JsonNode j) {
            return validate(j);
        }
        if (input instanceof String s) {
            return validate(s);
        }
        LOGGER.debug("Non-String value {} for Text.", input);
        return false;
    }

    @Override
    public boolean validate(JsonNode input) {
        if (input == null) {
            return isOptional();
        }
        if (!input.isTextual()) {
            LOGGER.debug("Non-Text value {} for Text.", input);
            return false;
        }
        return validate(input.asText());
    }

    public boolean validate(String input) {
        if (input == null) {
            return isOptional();
        }
        if (constraint == null) {
            return true;
        }
        return constraint.isValid(input, getUom());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.referenceTime);
        hash = 19 * hash + Objects.hashCode(this.localFrame);
        hash = 19 * hash + Objects.hashCode(this.uom);
        hash = 19 * hash + Objects.hashCode(this.constraint);
        hash = 19 * hash + Objects.hashCode(this.value);
        hash = 19 * hash + super.hashCode();
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
        final Time other = (Time) obj;
        if (!Objects.equals(this.referenceTime, other.referenceTime)) {
            return false;
        }
        if (!Objects.equals(this.localFrame, other.localFrame)) {
            return false;
        }
        if (!Objects.equals(this.uom, other.uom)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.constraint, other.constraint)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    protected Time self() {
        return this;
    }

}
