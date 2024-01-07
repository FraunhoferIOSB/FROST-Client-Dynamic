/*
 * Copyright (C) 2023 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
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

import de.fraunhofer.iosb.ilt.frostclient.model.ComplexValue;
import de.fraunhofer.iosb.ilt.frostclient.model.Property;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;
import net.time4j.Moment;

/**
 * Common interface for time values. Needed as STA sometimes does not specify
 * wether an instant or an interval will be passed.
 */
public class TimeValue implements TimeObject, ComplexValue<TimeValue> {

    public static EntityPropertyMain<TimeInstant> EP_START_TIME = TypeComplex.EP_START_TIME;
    public static EntityPropertyMain<TimeInstant> EP_END_TIME = TypeComplex.EP_END_TIME;

    private TimeInstant instant;
    private TimeInterval interval;

    public TimeValue() {
        this.instant = null;
        this.interval = null;
    }

    public TimeValue(TimeInstant timeInstant) {
        this.instant = timeInstant;
        this.interval = null;
    }

    public TimeValue(TimeInterval timeInterval) {
        this.instant = null;
        this.interval = timeInterval;
    }

    public static TimeValue create(TimeInstant instant) {
        return new TimeValue(instant);
    }

    public static TimeValue create(TimeInterval timeInterval) {
        return new TimeValue(timeInterval);
    }

    public static TimeValue create(Moment start, Moment end) {
        return new TimeValue(TimeInterval.create(start, end));
    }

    public static TimeValue create(Instant start, Instant end) {
        return new TimeValue(TimeInterval.create(start, end));
    }

    public static TimeValue create(ZonedDateTime start, ZonedDateTime end) {
        return new TimeValue(TimeInterval.create(start, end));
    }

    public static TimeValue create(Moment instant) {
        return new TimeValue(new TimeInstant(instant));
    }

    public static TimeValue create(Instant instant) {
        return new TimeValue(TimeInstant.create(instant));
    }

    public static TimeValue create(ZonedDateTime zdt) {
        return new TimeValue(TimeInstant.create(zdt));
    }

    public boolean isInstant() {
        return instant != null;
    }

    public TimeInstant getInstant() {
        return instant;
    }

    public boolean isInterval() {
        return interval != null;
    }

    public TimeInterval getInterval() {
        return interval;
    }

    @Override
    public String asISO8601() {
        return instant == null ? interval.asISO8601() : instant.asISO8601();
    }

    @Override
    public boolean isEmpty() {
        if (instant != null) {
            return instant.isEmpty();
        }
        if (interval != null) {
            return interval.isEmpty();
        }
        return true;
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
        final TimeValue other = (TimeValue) obj;
        if (!Objects.equals(this.instant, other.instant)) {
            return false;
        }
        return Objects.equals(this.interval, other.interval);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.instant);
        hash = 67 * hash + Objects.hashCode(this.interval);
        return hash;
    }

    @Override
    public <P> P getProperty(Property<P> property) {
        if (property != EP_START_TIME && property != EP_END_TIME) {
            throw new IllegalArgumentException("Unknown sub-property: " + property);
        }
        if (isInterval()) {
            return interval.getProperty(property);
        } else {
            return (P) instant;
        }
    }

    @Override
    public <P> TimeValue setProperty(Property<P> property, P value) {
        Moment moment;
        if (value == null) {
            moment = null;
        } else if (value instanceof Moment m) {
            moment = m;
        } else if (value instanceof Instant i) {
            moment = Moment.from(i);
        } else {
            throw new IllegalArgumentException("TimeInterval only accepts Moment or Instant, not " + value.getClass().getName());
        }
        if (property == EP_START_TIME) {
            if (moment == null) {
                return this;
            }
            if (instant != null) {
                instant = new TimeInstant(moment);
            } else {
                interval.setProperty(property, moment);
            }
            return this;
        }
        if (property == EP_END_TIME) {
            if (instant != null) {
                if (moment == null) {
                    return this;
                }
                // setting end on instant, convert to interval.
                interval = TimeInterval.create(instant.getDateTime(), moment);
                instant = null;
            } else {
                if (moment == null) {
                    // Removing end from interval, convert to instant
                    instant = TimeInstant.create(interval.getStart());
                    interval = null;
                } else {
                    interval.setProperty(property, moment);
                }
            }
        }
        throw new IllegalArgumentException("Unknown sub-property: " + property);

    }

    @Override
    public Object getProperty(String name) {
        throw new IllegalArgumentException("Can not get custom properties from TimeValue");
    }

    @Override
    public TimeValue setProperty(String name, Object value) {
        throw new IllegalArgumentException("Can not set custom properties on TimeValue");
    }

}
