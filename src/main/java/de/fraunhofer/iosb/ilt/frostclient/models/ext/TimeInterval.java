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

import de.fraunhofer.iosb.ilt.frostclient.model.ComplexValue;
import de.fraunhofer.iosb.ilt.frostclient.model.Property;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex;
import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;
import net.time4j.Moment;
import net.time4j.range.MomentInterval;

/**
 * Represent an ISO8601 time interval.
 */
public class TimeInterval implements TimeObject, ComplexValue<TimeInterval> {

    public static EntityPropertyMain<TimeInstant> EP_START_TIME = TypeComplex.EP_START_TIME;
    public static EntityPropertyMain<TimeInstant> EP_END_TIME = TypeComplex.EP_END_TIME;

    private MomentInterval interval;

    public TimeInterval() {
        this.interval = null;
    }

    public TimeInterval(MomentInterval interval) {
        this.interval = interval;
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval);
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
        final TimeInterval other = (TimeInterval) obj;
        return Objects.equals(this.interval, other.interval);
    }

    public static TimeInterval create(Moment start, Moment end) {
        return new TimeInterval(MomentInterval.between(start, end));
    }

    public static TimeInterval create(Instant start, Instant end) {
        return new TimeInterval(MomentInterval.between(start, end));
    }

    public static TimeInterval create(ZonedDateTime start, ZonedDateTime end) {
        return new TimeInterval(MomentInterval.between(start.toInstant(), end.toInstant()));
    }

    public static TimeInterval parse(String value) {
        try {
            return new TimeInterval(MomentInterval.parseISO(value));
        } catch (ParseException ex) {
            throw new IllegalArgumentException("Failed to parse TimeInterval " + StringHelper.cleanForLogging(value), ex);
        }
    }

    public MomentInterval getInterval() {
        return interval;
    }

    @Override
    public boolean isEmpty() {
        return interval == null;
    }

    @Override
    public String asISO8601() {
        return StringHelper.FORMAT_INTERVAL.print(interval);
    }

    @Override
    public String toString() {
        return asISO8601();
    }

    @Override
    public <P> P getProperty(Property<P> property) {
        if (interval == null) {
            return null;
        }
        if (property == EP_START_TIME) {
            return (P) interval.getStartAsMoment();
        }
        if (property == EP_END_TIME) {
            return (P) interval.getEndAsMoment();
        }
        throw new IllegalArgumentException("Unknown sub-property: " + property);
    }

    @Override
    public TimeInterval setProperty(Property property, Object value) {
        if (value == null) {
            return this;
        }
        Moment moment;
        if (value instanceof Moment m) {
            moment = m;
        } else if (value instanceof Instant i) {
            moment = Moment.from(i);
        } else if (value instanceof TimeInstant ti) {
            moment = ti.getDateTime();
        } else {
            throw new IllegalArgumentException("TimeInterval only accepts Moment, Instant or TimeInstant, not " + value.getClass().getName());
        }
        if (property == EP_START_TIME) {
            if (interval == null) {
                interval = MomentInterval.since(moment);
            } else {
                interval = interval.withStart(moment);
            }
            return this;
        }
        if (property == EP_END_TIME) {
            if (interval == null) {
                interval = MomentInterval.until(moment).withOpenEnd();
            } else {
                interval = interval.withEnd(moment).withOpenEnd();
            }
            return this;
        }
        throw new IllegalArgumentException("Unknown sub-property: " + property);
    }

    public Moment getStart() {
        if (interval == null) {
            return null;
        }
        return interval.getStartAsMoment();
    }

    public Moment getEnd() {
        if (interval == null) {
            return null;
        }
        return interval.getEndAsMoment();
    }

    @Override
    public Object getProperty(String name) {
        throw new IllegalArgumentException("Can not get custom properties from TimeInterval");
    }

    @Override
    public TimeInterval setProperty(String name, Object value) {
        throw new IllegalArgumentException("Can not set custom properties on TimeInterval");
    }
}
