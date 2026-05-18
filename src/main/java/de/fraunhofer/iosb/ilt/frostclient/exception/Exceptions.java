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
package de.fraunhofer.iosb.ilt.frostclient.exception;

import java.util.function.Supplier;
import org.apache.commons.lang3.Strings;

/**
 * A collection of helper methods for throwing exceptions.
 */
public class Exceptions {

    public static final void illegalArgumentIf(boolean predicate, String message, Object param1) {
        if (predicate) {
            throw new IllegalArgumentException(replacePlaceholders(message, new Object[]{param1}));
        }
    }

    public static final void illegalArgumentIf(boolean predicate, String message, Object param1, Object param2) {
        if (predicate) {
            throw new IllegalArgumentException(replacePlaceholders(message, new Object[]{param1, param2}));
        }
    }

    public static final void illegalArgumentIf(boolean predicate, String message, Object... params) {
        if (predicate) {
            throw new IllegalArgumentException(replacePlaceholders(message, params));
        }
    }

    public static final void illegalArgumentIf(boolean predicate, Supplier<String> message) {
        if (predicate) {
            throw new IllegalArgumentException(message == null ? "Expected false." : message.get());
        }
    }

    public static final String replacePlaceholders(String line, Object... params) {
        StringBuilder replaced = new StringBuilder();
        int idx = 0;
        for (var param : params) {
            int found = Strings.CS.indexOf(line, "{}", idx);
            if (found == -1) {
                break;
            }
            replaced.append(line.substring(idx, found))
                    .append(param);
            idx = found + 2;
        }
        if (idx < line.length()) {
            replaced.append(line.substring(idx));
        }
        return replaced.toString();
    }
}
