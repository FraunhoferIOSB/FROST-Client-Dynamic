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
package de.fraunhofer.iosb.ilt.frostclient.utils;

/**
 * Constants for special names in STA.
 */
public final class SpecialNames {

    public static final String IOT_COUNT = "iot.count";
    public static final String AT_IOT_COUNT = '@' + IOT_COUNT;

    public static final String IOT_ID = "iot.id";
    public static final String AT_IOT_ID = '@' + IOT_ID;

    public static final String IOT_NAVIGATION_LINK = "iot.navigationLink";
    public static final String AT_IOT_NAVIGATION_LINK = '@' + IOT_NAVIGATION_LINK;

    public static final String IOT_NEXT_LINK = "iot.nextLink";
    public static final String AT_IOT_NEXT_LINK = '@' + IOT_NEXT_LINK;

    public static final String IOT_SELF_LINK = "iot.selfLink";
    public static final String AT_IOT_SELF_LINK = '@' + IOT_SELF_LINK;

    public static final String AT_CONTEXT = "@context";
    public static final String AT_COUNT = "@count";
    public static final String AT_ID = "@id";
    public static final String AT_NAVIGATION_LINK = "@navigationLink";
    public static final String AT_NEXT_LINK = "@nextLink";

    private SpecialNames() {
        // Utility class.
    }

}
