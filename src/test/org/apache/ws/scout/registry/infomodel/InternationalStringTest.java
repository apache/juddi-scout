/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.ws.scout.registry.infomodel;

import junit.framework.TestCase;

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.LocalizedString;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * @version $Revision$ $Date$
 */
public class InternationalStringTest extends TestCase {
    private Locale defaultLocale;
    private LocalizedString defaultString;
    private LocalizedString usString;
    private LocalizedString ukString;
    private LocalizedString caString;

    public void testAddLocalizedString() throws JAXRException {
        InternationalString is = new InternationalStringImpl(Locale.US, "USA", LocalizedString.DEFAULT_CHARSET_NAME);
        Collection localizedStrings = is.getLocalizedStrings();
        assertEquals(1, localizedStrings.size());
        assertTrue(localizedStrings.contains(usString));

        is.addLocalizedString(ukString);
        localizedStrings = is.getLocalizedStrings();
        assertEquals(2, localizedStrings.size());
        assertTrue(localizedStrings.contains(usString));
        assertTrue(localizedStrings.contains(ukString));

        // add again and see that we overwrite
        is.addLocalizedString(ukString);
        localizedStrings = is.getLocalizedStrings();
        assertEquals(2, localizedStrings.size());
        assertTrue(localizedStrings.contains(usString));
        assertTrue(localizedStrings.contains(ukString));
    }

    public void testAddLocalizedStrings() throws JAXRException {
        InternationalString is = new InternationalStringImpl(Locale.US, "USA", LocalizedString.DEFAULT_CHARSET_NAME);
        Collection localizedStrings = is.getLocalizedStrings();
        assertEquals(1, localizedStrings.size());
        assertTrue(localizedStrings.contains(usString));

        ArrayList strings = new ArrayList(2);
        strings.add(ukString);
        strings.add(caString);
        is.addLocalizedStrings(strings);

        localizedStrings = is.getLocalizedStrings();
        assertEquals(3, localizedStrings.size());
        assertTrue(localizedStrings.contains(usString));
        assertTrue(localizedStrings.contains(caString));
        assertTrue(localizedStrings.contains(ukString));
    }

    public void testGetValue() throws JAXRException {
        InternationalString is = new InternationalStringImpl();
        is.addLocalizedString(usString);
        is.addLocalizedString(defaultString);
        is.addLocalizedString(ukString);
        assertEquals("default", is.getValue());
    }

    public void testSetValue() throws JAXRException {
        InternationalString is = new InternationalStringImpl();
        is.addLocalizedString(defaultString);
        is.setValue("foo");
        assertEquals("foo", is.getValue());
    }

    public void testGetValueWithLocale() throws JAXRException {
        InternationalString is = new InternationalStringImpl();
        is.addLocalizedString(usString);
        is.addLocalizedString(ukString);
        assertEquals("USA", is.getValue(Locale.US));
    }

    public void testGetValueWithUndefinedLocale() throws JAXRException {
        InternationalString is = new InternationalStringImpl();
        is.addLocalizedString(usString);
        assertNull(is.getValue(Locale.UK));
    }

    public void testSetValueWithLocale() throws JAXRException {
        InternationalString is = new InternationalStringImpl();
        is.addLocalizedString(usString);
        is.addLocalizedString(ukString);
        is.setValue(Locale.US, "foo");
        assertEquals("foo", is.getValue(Locale.US));
        assertEquals("England", is.getValue(Locale.UK));
        assertEquals(2, is.getLocalizedStrings().size());
    }

    public void testRemoveLocalizedString() throws JAXRException {
        InternationalString is = new InternationalStringImpl();
        is.addLocalizedString(usString);
        is.addLocalizedString(ukString);
        is.addLocalizedString(caString);
        assertEquals(3, is.getLocalizedStrings().size());

        is.removeLocalizedString(ukString);
        Collection localizedStrings = is.getLocalizedStrings();
        assertEquals(2, localizedStrings.size());
        assertTrue(localizedStrings.contains(usString));
        assertFalse(localizedStrings.contains(ukString));
        assertTrue(localizedStrings.contains(caString));
    }

    public void testRemoveLocalizedStrings() throws JAXRException {
        InternationalString is = new InternationalStringImpl();
        is.addLocalizedString(usString);
        is.addLocalizedString(ukString);
        is.addLocalizedString(caString);
        assertEquals(3, is.getLocalizedStrings().size());

        Collection localizedStrings = new ArrayList();
        localizedStrings.add(usString);
        localizedStrings.add(caString);
        is.removeLocalizedStrings(localizedStrings);
        localizedStrings = is.getLocalizedStrings();
        assertEquals(1, localizedStrings.size());
        assertFalse(localizedStrings.contains(usString));
        assertTrue(localizedStrings.contains(ukString));
        assertFalse(localizedStrings.contains(caString));
    }

    public void testGetLocalizedString() throws JAXRException {
        InternationalString is = new InternationalStringImpl();
        is.addLocalizedString(usString);
        assertEquals(usString, is.getLocalizedString(Locale.US, LocalizedString.DEFAULT_CHARSET_NAME));
        assertNull(is.getLocalizedString(Locale.US, "US-ASCII"));
    }

    protected void setUp() throws Exception {
        super.setUp();
        defaultLocale = Locale.getDefault();
        defaultString = new LocalizedStringImpl(defaultLocale, "default", LocalizedString.DEFAULT_CHARSET_NAME);
        usString = new LocalizedStringImpl(Locale.US, "USA", LocalizedString.DEFAULT_CHARSET_NAME);
        ukString = new LocalizedStringImpl(Locale.UK, "England", LocalizedString.DEFAULT_CHARSET_NAME);
        caString = new LocalizedStringImpl(Locale.CANADA, "Canada", LocalizedString.DEFAULT_CHARSET_NAME);
    }
}
