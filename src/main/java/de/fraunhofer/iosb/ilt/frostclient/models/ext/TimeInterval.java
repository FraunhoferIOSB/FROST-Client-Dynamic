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
        this.interval = MomentInterval.between(Moment.nowInSystemTime(), Moment.nowInSystemTime());
    }

    public TimeInterval(MomentInterval interval) {
        if (interval == null) {
            throw new IllegalArgumentException("Interval must be non-null");
        }
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
        if (property == EP_START_TIME) {
            return (P) interval.getStartAsMoment();
        }
        if (property == EP_END_TIME) {
            return (P) interval.getStartAsMoment();
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
        } else {
            throw new IllegalArgumentException("TimeInterval only accepts Moment or Instant, not " + value.getClass().getName());
        }
        if (property == EP_START_TIME) {
            interval = interval.withStart(moment);
            return this;
        }
        if (property == EP_END_TIME) {
            interval = interval.withEnd(moment).withOpenEnd();
            return this;
        }
        throw new IllegalArgumentException("Unknown sub-property: " + property);
    }

    public Moment getStart() {
        return interval.getStartAsMoment();
    }

    public Moment getEnd() {
        return interval.getEndAsMoment();
    }
}
