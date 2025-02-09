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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import java.io.IOException;
import java.text.ParseException;
import net.time4j.Moment;
import net.time4j.format.expert.Iso8601Format;

/**
 * Helper for deserialization of TimeInstant objects from JSON.
 */
public class MomentDeserializer extends StdDeserializer<Moment> {

    public MomentDeserializer() {
        super(TimeInstant.class);
    }

    @Override
    public Moment deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
        final String valueAsString = jp.getValueAsString();
        if (valueAsString == null) {
            return null;
        }
        try {
            return Iso8601Format.EXTENDED_DATE_TIME_OFFSET.parse(valueAsString);
        } catch (ParseException ex) {
            throw new IllegalArgumentException("Failed to parse TimeInstant " + StringHelper.cleanForLogging(valueAsString), ex);
        }
    }

}
