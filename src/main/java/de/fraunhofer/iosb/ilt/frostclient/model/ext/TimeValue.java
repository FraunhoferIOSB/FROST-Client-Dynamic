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
package de.fraunhofer.iosb.ilt.frostclient.model.ext;

import de.fraunhofer.iosb.ilt.frostclient.model.property.type.ComplexValue;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;
import net.time4j.Moment;

/**
 * Common interface for time values. Needed as STA sometimes does not specify
 * wether an instant or an interval will be passed.
 */
public class TimeValue implements TimeObject, ComplexValue {

    private final TimeInstant instant;
    private final TimeInterval interval;

    public TimeValue(TimeInstant timeInstant) {
        this.instant = timeInstant;
        this.interval = null;
    }

    public TimeValue(TimeInterval timeInterval) {
        this.instant = null;
        this.interval = timeInterval;
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
    public Object get(String name) {
        if (isInterval()) {
            return interval.get(name);
        } else {
            return instant;
        }
    }

}
