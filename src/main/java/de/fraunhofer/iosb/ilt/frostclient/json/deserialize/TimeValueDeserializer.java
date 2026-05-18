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
package de.fraunhofer.iosb.ilt.frostclient.json.deserialize;

import static de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex.NAME_INTERVAL_END;
import static de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex.NAME_INTERVAL_START;

import de.fraunhofer.iosb.ilt.frostclient.exception.Exceptions;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInterval;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeValue;
import net.time4j.Moment;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

/**
 * Helper for deserialization of TimeValue objects from JSON. May not work
 * properly in every case as deciding wether input is a TimeInstant or a
 * TimeInterval is based on exceptions while parsing
 */
public class TimeValueDeserializer extends StdDeserializer<TimeValue> {

    public TimeValueDeserializer() {
        super(TimeValue.class);
    }

    @Override
    public TimeValue deserialize(JsonParser jp, DeserializationContext dc) throws JacksonException {
        JsonToken curToken = jp.currentToken();
        if (curToken == JsonToken.VALUE_STRING) {
            return parseStringValue(jp);
        } else if (curToken == JsonToken.START_OBJECT) {
            return parseObjectValue(jp);
        } else {
            throw new IllegalArgumentException("Could not parse TimeValue, found a " + curToken.name());
        }
    }

    private TimeValue parseStringValue(JsonParser jp) throws JacksonException {
        String node = jp.getValueAsString();
        if (node == null) {
            return null;
        }
        try {
            return new TimeValue(TimeInstant.parse(node));
        } catch (IllegalArgumentException e) {
            return new TimeValue(TimeInterval.parse(node));
        }
    }

    private TimeValue parseObjectValue(JsonParser jp) throws JacksonException {
        Moment start = null;
        Moment end = null;
        JsonToken currentToken = jp.nextToken();
        while (currentToken == JsonToken.PROPERTY_NAME) {
            final String fieldName = jp.currentName();
            currentToken = jp.nextToken();
            Exceptions.illegalArgumentIf(currentToken != JsonToken.VALUE_STRING, "Found {} for {}, expected a string", currentToken, fieldName);
            final String valueAsString = jp.getValueAsString();
            switch (fieldName) {
                case NAME_INTERVAL_START:
                    start = TimeInstant.parseMoment(valueAsString);
                    break;

                case NAME_INTERVAL_END:
                    end = TimeInstant.parseMoment(valueAsString);
                    break;

                default:
                    throw new IllegalArgumentException("Found field " + fieldName + " expected one of: start, end");
            }
            currentToken = jp.nextToken();
        }

        if (start == null && end == null) {
            return null;
        }
        if (start != null && end == null) {
            return TimeValue.create(start);
        }
        if (end != null && start == null) {
            return TimeValue.create(end);
        }
        return TimeValue.create(start, end);
    }
}
