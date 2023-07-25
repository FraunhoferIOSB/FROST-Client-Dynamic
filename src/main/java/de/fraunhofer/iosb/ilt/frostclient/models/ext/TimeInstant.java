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

import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;
import net.time4j.Moment;
import net.time4j.SystemClock;
import net.time4j.format.expert.Iso8601Format;

/**
 * Represents ISO8601 Instant.
 */
public class TimeInstant implements TimeObject {

    private final Moment dateTime;

    public TimeInstant(Moment dateTime) {
        this.dateTime = dateTime;
    }

    public static TimeInstant now() {
        return new TimeInstant(SystemClock.currentMoment());
    }

    public static TimeInstant create(Moment moment) {
        if (moment == null) {
            return null;
        }
        return new TimeInstant(moment);
    }

    public static TimeInstant create(Instant instant) {
        if (instant == null) {
            return null;
        }
        return new TimeInstant(Moment.from(instant));
    }

    public static TimeInstant create(ZonedDateTime zdt) {
        if (zdt == null) {
            return null;
        }
        return new TimeInstant(Moment.from(zdt.toInstant()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime);
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
        final TimeInstant other = (TimeInstant) obj;
        if (this.dateTime == null && other.dateTime == null) {
            return true;
        }
        if (this.dateTime == null || other.dateTime == null) {
            return false;
        }
        return this.dateTime.equals(other.dateTime);
    }

    public static TimeInstant parse(String value) {
        try {
            return new TimeInstant(Iso8601Format.EXTENDED_DATE_TIME_OFFSET.parse(value));
        } catch (ParseException ex) {
            throw new IllegalArgumentException("Failed to parse TimeInstant " + StringHelper.cleanForLogging(value), ex);
        }
    }

    public Moment getDateTime() {
        return dateTime;
    }

    @Override
    public boolean isEmpty() {
        return dateTime == null;
    }

    @Override
    public String asISO8601() {
        if (dateTime == null) {
            return "";
        }
        return StringHelper.FORMAT_MOMENT.print(dateTime);
    }

    @Override
    public String toString() {
        return asISO8601();
    }

}
