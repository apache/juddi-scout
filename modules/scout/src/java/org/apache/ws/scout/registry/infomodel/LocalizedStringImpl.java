/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ws.scout.registry.infomodel;

import java.util.Locale;
import javax.xml.registry.infomodel.LocalizedString;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class LocalizedStringImpl implements LocalizedString {
    private String charsetName;
    private Locale locale;
    private String value;

    public LocalizedStringImpl() {
        this.locale = Locale.getDefault();
        this.charsetName = LocalizedString.DEFAULT_CHARSET_NAME;
    }

    /**
     * Constuctor for a LocalizedString.
     * @param locale the locale; must not be null
     * @param value the value; may be null
     * @param charsetName the charset; must not be null
     */
    public LocalizedStringImpl(Locale locale, String value, String charsetName) {
        if (locale == null) {
            throw new IllegalArgumentException("locale cannot be null");
        }
        if (charsetName == null) {
            throw new IllegalArgumentException("charsetName cannot be null");
        }
        this.locale = locale;
        this.value = value;
        this.charsetName = charsetName;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getValue() {
        return value;
    }

    public void setCharsetName(String charsetName) {
        if (charsetName == null) {
            throw new IllegalArgumentException("charsetName cannot be null");
        }
        this.charsetName = charsetName;
    }

    public void setLocale(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("locale cannot be null");
        }
        this.locale = locale;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * There is a spec ambiguity here as it does not define how equals is determined for LocalizedString
     * but they are intended to be used in Collections.
     * We define it as locale, charsetName and value being equal.
     * @param o the other object
     * @return true if they are equal
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalizedStringImpl)) return false;

        final LocalizedStringImpl localizedString = (LocalizedStringImpl) o;

        if (!charsetName.equals(localizedString.charsetName)) return false;
        if (!locale.equals(localizedString.locale)) return false;
        if (value != null ? !value.equals(localizedString.value) : localizedString.value != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = charsetName.hashCode();
        result = 29 * result + locale.hashCode();
        result = 29 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return value;
    }
}
