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
package de.fraunhofer.iosb.ilt.frostclient.utils;

import static net.time4j.PlainTime.HOUR_FROM_0_TO_24;
import static net.time4j.PlainTime.MINUTE_OF_HOUR;
import static net.time4j.PlainTime.NANO_OF_SECOND;
import static net.time4j.PlainTime.SECOND_OF_MINUTE;
import static net.time4j.format.expert.IsoDateStyle.EXTENDED_CALENDAR_DATE;
import static net.time4j.format.expert.IsoDecimalStyle.DOT;
import static net.time4j.tz.ZonalOffset.UTC;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.engine.ChronoCondition;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.format.NumberSystem;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.ChronoPrinter;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.IsoDateStyle;
import net.time4j.format.expert.IsoDecimalStyle;
import net.time4j.range.MomentInterval;
import net.time4j.tz.ZonalOffset;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various String helper methods.
 */
public class StringHelper {

    public static final Charset UTF8 = StandardCharsets.UTF_8;

    private static final Logger LOGGER = LoggerFactory.getLogger(StringHelper.class);
    private static final String UTF8_NOT_SUPPORTED = "UTF-8 not supported?";
    private static final NonZeroCondition NON_ZERO_FRACTION = new NonZeroCondition(PlainTime.NANO_OF_SECOND);

    public static final ChronoPrinter<Moment> FORMAT_MOMENT = buildMomentFormatter();
    public static final ChronoPrinter<MomentInterval> FORMAT_INTERVAL = buildIntervalFormatter();

    private StringHelper() {
        // Utility class, not to be instantiated.
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String deCapitalize(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    /**
     * Returns true if the given string is null, or empty.
     *
     * @param string the string to check.
     * @return true if string == null || string.isEmpty()
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isNullOrEmpty(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    /**
     * Replaces characters that might break logging output. Currently: \n, \r
     * and \t
     *
     * @param string The string to clean.
     * @return The cleaned string.
     */
    public static String cleanForLogging(String string) {
        if (string == null) {
            return "null";
        }
        return StringUtils.replaceChars(string, "\n\r\t", "");
    }

    /**
     * Null-Save replaces characters that might break logging output. Currently:
     * \n, \r and \t
     *
     * @param object The Object to clean.
     * @return The cleaned string.
     */
    public static String cleanForLogging(Object object) {
        return cleanForLogging(Objects.toString(object));
    }

    /**
     * Replaces all ' in the string with ''.
     *
     * @param in The string to escape.
     * @return The escaped string.
     */
    public static String escapeForStringConstant(String in) {
        return in.replace("'", "''");
    }

    /**
     * Removes characters that might break logging output and truncates to a
     * maximum length.Currently: \n, \r and \t
     *
     * @param string The string to clean.
     * @param maxLength The maximum length of the returned String.
     * @return The cleaned string.
     */
    public static String cleanForLogging(String string, int maxLength) {
        return StringUtils.replaceChars(StringUtils.abbreviate(string, maxLength), "\n\r\t", "");
    }

    /**
     * Quote the given value for use in URLs.
     *
     * @param in The object to quote.
     * @return The quoted String.
     */
    public static String quoteForUrl(Object in) {
        if (in instanceof Number) {
            return in.toString();
        }
        return "'" + escapeForStringConstant(in.toString()) + "'";
    }

    /**
     * Urlencodes the given string, optionally not encoding forward slashes.
     *
     * In urls, forward slashes before the "?" must never be urlEncoded.
     * Urlencoding of slashes could otherwise be used to obfuscate phising URLs.
     *
     * @param string The string to urlEncode.
     * @param notSlashes If true, forward slashes are not encoded.
     * @return The urlEncoded string.
     */
    public static String urlEncode(String string, boolean notSlashes) {
        if (notSlashes) {
            return urlEncodeNotSlashes(string);
        }
        return urlEncode(string);
    }

    /**
     * Urlencodes the given string, except for the forward slashes.
     *
     * @param string The string to urlEncode.
     * @return The urlEncoded string.
     */
    public static String urlEncodeNotSlashes(String string) {
        String[] split = string.split("/");
        for (int i = 0; i < split.length; i++) {
            split[i] = urlEncode(split[i]);
        }
        return String.join("/", split);
    }

    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, UTF8.name());
        } catch (UnsupportedEncodingException exc) {
            // Should never happen, UTF-8 is build in.
            LOGGER.error(UTF8_NOT_SUPPORTED);
            throw new IllegalStateException(UTF8_NOT_SUPPORTED, exc);
        }
    }

    /**
     * Decode the given input using UTF-8 as character set.
     *
     * @param input The input to urlDecode.
     * @return The decoded input.
     */
    public static String urlDecode(String input) {
        try {
            return URLDecoder.decode(input, UTF8.name());
        } catch (UnsupportedEncodingException exc) {
            // Should never happen, UTF-8 is build in.
            LOGGER.error(UTF8_NOT_SUPPORTED);
            throw new IllegalStateException(UTF8_NOT_SUPPORTED, exc);
        }
    }

    private static ChronoPrinter<MomentInterval> buildIntervalFormatter() {
        return (formattable, buffer, attributes) -> {
            MomentInterval interval = formattable.toCanonical();
            if (interval.getStart().isInfinite()) {
                buffer.append("-");
            } else {
                FORMAT_MOMENT.print(interval.getStartAsMoment(), buffer);
            }
            buffer.append('/');
            if (interval.getEnd().isInfinite()) {
                buffer.append("-");
            } else {
                FORMAT_MOMENT.print(interval.getEndAsMoment(), buffer);
            }
            return Collections.emptySet();
        };
    }

    private static ChronoPrinter<Moment> buildMomentFormatter() {
        IsoDateStyle dateStyle = EXTENDED_CALENDAR_DATE;
        IsoDecimalStyle decimalStyle = DOT;
        ZonalOffset offset = UTC;
        ChronoFormatter.Builder<Moment> builder = ChronoFormatter.setUp(Moment.axis(), Locale.ROOT);
        builder.addCustomized(
                PlainDate.COMPONENT,
                Iso8601Format.ofDate(dateStyle),
                (text, status, attributes) -> null);
        builder.addLiteral('T');
        addWallTime(builder, dateStyle.isExtended(), decimalStyle);
        builder.addTimezoneOffset(FormatStyle.MEDIUM, dateStyle.isExtended(), Collections.singletonList("Z"));
        return builder.build().with(Leniency.STRICT).withTimezone(offset);

    }

    private static <T extends ChronoEntity<T>> void addWallTime(ChronoFormatter.Builder<T> builder, boolean extended, IsoDecimalStyle decimalStyle) {

        builder.startSection(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
        builder.startSection(Attributes.ZERO_DIGIT, '0');
        builder.addFixedInteger(HOUR_FROM_0_TO_24, 2);

        if (extended) {
            builder.addLiteral(':');
        }

        builder.addFixedInteger(MINUTE_OF_HOUR, 2);

        if (extended) {
            builder.addLiteral(':');
        }

        builder.addFixedInteger(SECOND_OF_MINUTE, 2);
        builder.startOptionalSection(NON_ZERO_FRACTION);

        switch (decimalStyle) {
            case COMMA:
                builder.addLiteral(',', '.');
                break;
            case DOT:
                builder.addLiteral('.', ',');
                break;
            default:
                throw new UnsupportedOperationException(decimalStyle.name());
        }

        builder.addFraction(NANO_OF_SECOND, 1, 9, false);

        for (int i = 0; i < 3; i++) {
            builder.endSection();
        }

    }

    private static class NonZeroCondition implements ChronoCondition<ChronoDisplay> {

        private final ChronoElement<Integer> element;

        NonZeroCondition(ChronoElement<Integer> element) {
            this.element = element;
        }

        @Override
        public boolean test(ChronoDisplay context) {
            return (context.getInt(this.element) > 0);
        }

        ChronoCondition<ChronoDisplay> or(final NonZeroCondition other) {
            return context -> (NonZeroCondition.this.test(context) || other.test(context));
        }
    }

}
