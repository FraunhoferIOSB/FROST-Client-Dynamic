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
package de.fraunhofer.iosb.ilt.frostclient;

import static de.fraunhofer.iosb.ilt.frostclient.utils.SpecialNames.AT_COUNT;
import static de.fraunhofer.iosb.ilt.frostclient.utils.SpecialNames.AT_IOT_COUNT;
import static de.fraunhofer.iosb.ilt.frostclient.utils.SpecialNames.AT_IOT_ID;
import static de.fraunhofer.iosb.ilt.frostclient.utils.SpecialNames.AT_IOT_NAVIGATION_LINK;
import static de.fraunhofer.iosb.ilt.frostclient.utils.SpecialNames.AT_IOT_NEXT_LINK;
import static de.fraunhofer.iosb.ilt.frostclient.utils.SpecialNames.AT_IOT_SELF_LINK;

import java.util.HashMap;
import java.util.Map;

/**
 * The versions that FROST supports.
 */
public class Version {

    public static final String VERSION_STA_V10_NAME = "v1.0";
    public static final String VERSION_STA_V11_NAME = "v1.1";
    public static final Version V_1_0 = new Version(VERSION_STA_V10_NAME, AT_IOT_COUNT, AT_IOT_ID, AT_IOT_SELF_LINK, AT_IOT_NEXT_LINK, AT_IOT_NAVIGATION_LINK);
    public static final Version V_1_1 = new Version(VERSION_STA_V11_NAME, AT_IOT_COUNT, AT_IOT_ID, AT_IOT_SELF_LINK, AT_IOT_NEXT_LINK, AT_IOT_NAVIGATION_LINK);
    public static final Version V_ODATA_4_01 = new Version("", AT_COUNT, "id", AT_IOT_SELF_LINK, AT_IOT_NEXT_LINK, AT_IOT_NAVIGATION_LINK);

    public final String urlPart;
    public final String countName;
    public final String idName;
    public final String navLinkName;
    public final String nextLinkName;
    public final String selfLinkName;

    public static final Map<String, Version> VERSIONS = new HashMap<>();

    public static void registerVersion(Version version) {
        VERSIONS.put(version.urlPart, version);
    }

    public static Version findVersion(String urlPart) {
        return VERSIONS.get(urlPart);
    }

    static {
        registerVersion(V_1_0);
        registerVersion(V_1_1);
    }

    public Version(String urlPart, String countName, String idName, String selfLinkName, String nextLinkName, String navLinkName) {
        this.urlPart = urlPart;
        this.countName = countName;
        this.idName = idName;
        this.selfLinkName = selfLinkName;
        this.nextLinkName = nextLinkName;
        this.navLinkName = navLinkName;
    }

    @Override
    public String toString() {
        return urlPart;
    }

    public String getCountName() {
        return countName;
    }

    public String getIdName() {
        return idName;
    }

    public String getNavLinkName() {
        return navLinkName;
    }

    public String getNextLinkName() {
        return nextLinkName;
    }

    public String getSelfLinkName() {
        return selfLinkName;
    }

    public String getUrlPart() {
        return urlPart;
    }

}
