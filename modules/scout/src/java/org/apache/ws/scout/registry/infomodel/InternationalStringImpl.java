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

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.LocalizedString;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class InternationalStringImpl implements InternationalString {
    /**
     * Maintains an Hashmap of locale to string value
     */
    private final Map map = new HashMap();

    public InternationalStringImpl() {
    }

    public InternationalStringImpl(String str)
    {
        Locale locale = Locale.getDefault();
        map.put(new MapKey(locale, LocalizedString.DEFAULT_CHARSET_NAME), new LocalizedStringImpl(locale, str, LocalizedString.DEFAULT_CHARSET_NAME));

    }

    public InternationalStringImpl(Locale locale, String str, String charsetName) {
        MapKey mapKey = new MapKey(locale, charsetName);
        map.put(mapKey, new LocalizedStringImpl(locale, str, charsetName));
    }

    public void addLocalizedString(LocalizedString localizedString) throws JAXRException {
        MapKey mapKey = new MapKey(localizedString);
        map.put(mapKey, localizedString);
    }

    public void addLocalizedStrings(Collection collection) throws JAXRException {
        for (Iterator i = collection.iterator(); i.hasNext();) {
            LocalizedString localizedString = (LocalizedString) i.next();
            map.put(new MapKey(localizedString), localizedString);
        }
    }

    public Collection getLocalizedStrings() throws JAXRException {
        return Collections.unmodifiableCollection(map.values());
    }

    public String getValue() throws JAXRException {
        return getValue(Locale.getDefault());
    }

    public void setValue(String str) throws JAXRException {
        setValue(Locale.getDefault(), str);
    }

    public String getValue(Locale locale) throws JAXRException {
        LocalizedString localizedString = (LocalizedString) map.get(new MapKey(locale, LocalizedString.DEFAULT_CHARSET_NAME));
        return localizedString != null ? localizedString.getValue() : null;
    }

    public void setValue(Locale locale, String value) throws JAXRException {
        map.put(new MapKey(locale, LocalizedString.DEFAULT_CHARSET_NAME), new LocalizedStringImpl(locale, value, LocalizedString.DEFAULT_CHARSET_NAME));
    }

    public void removeLocalizedString(LocalizedString localizedString) throws JAXRException {
        map.remove(new MapKey(localizedString));
    }

    public void removeLocalizedStrings(Collection collection) throws JAXRException {
        for (Iterator i = collection.iterator(); i.hasNext();) {
            removeLocalizedString((LocalizedString) i.next());
        }
    }

    public LocalizedString getLocalizedString(Locale locale, String charset) throws JAXRException {
        return (LocalizedString) map.get(new MapKey(locale, charset));
    }

    private static class MapKey {
        private final Locale locale;
        private final String charsetName;

        public MapKey(Locale locale, String charsetName) {
            this.locale = locale;
            this.charsetName = charsetName;
        }

        public MapKey(LocalizedString localizedString) throws JAXRException {
            this.locale = localizedString.getLocale();
            this.charsetName = localizedString.getCharsetName();
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MapKey)) return false;
            final MapKey mapKey = (MapKey) o;
            if (!charsetName.equals(mapKey.charsetName)) return false;
            if (!locale.equals(mapKey.locale)) return false;
            return true;
        }

        public int hashCode() {
            int result;
            result = locale.hashCode();
            result = 29 * result + charsetName.hashCode();
            return result;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer(32);
            buf.append('[').append(locale).append(',').append(charsetName).append(']');
            return buf.toString();
        }
    }
}
