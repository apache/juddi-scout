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

import java.util.Locale;

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.LocalizedString;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class LocalizedStringTest extends TestCase {
    public void testEquals() {
        LocalizedString ls1 = new LocalizedStringImpl(Locale.US, "Equal", "UTF-8");
        LocalizedString ls2 = new LocalizedStringImpl(Locale.US, "Equal", "UTF-8");
        assertTrue(ls1.equals(ls2));
        assertTrue(ls2.equals(ls1));
        ls2 = new LocalizedStringImpl(Locale.US, "NotEqual", "UTF-8");
        assertFalse(ls1.equals(ls2));
        assertFalse(ls2.equals(ls1));
        ls2 = new LocalizedStringImpl(Locale.US, "Equal", "US-ASCII");
        assertFalse(ls1.equals(ls2));
        assertFalse(ls2.equals(ls1));
    }

    public void testNullEquals() {
        LocalizedString ls1 = new LocalizedStringImpl(Locale.US, null, "UTF-8");
        LocalizedString ls2 = new LocalizedStringImpl(Locale.US, null, "UTF-8");
        assertTrue(ls1.equals(ls2));
        assertTrue(ls2.equals(ls1));
        ls2 = new LocalizedStringImpl(Locale.US, "NotEqual", "UTF-8");
        assertFalse(ls1.equals(ls2));
        assertFalse(ls2.equals(ls1));
        ls2 = new LocalizedStringImpl(Locale.US, null, "US-ASCII");
        assertFalse(ls1.equals(ls2));
        assertFalse(ls2.equals(ls1));
    }

    public void testSetCharsetName() throws JAXRException {
        LocalizedString ls1 = new LocalizedStringImpl(Locale.US, "USA", "UTF-8");
        assertEquals("UTF-8", ls1.getCharsetName());
        ls1.setCharsetName("US-ASCII");
        assertEquals("US-ASCII", ls1.getCharsetName());
        try {
            ls1.setCharsetName(null);
            fail("expected IllegalArgumentException for null charsetName");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    public void testSetLocale() throws JAXRException {
        LocalizedString ls1 = new LocalizedStringImpl(Locale.US, "USA", "UTF-8");
        assertEquals(Locale.US, ls1.getLocale());
        ls1.setLocale(Locale.CANADA);
        assertEquals(Locale.CANADA, ls1.getLocale());
        try {
            ls1.setLocale(null);
            fail("expected IllegalArgumentException for null locale");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    public void testSetValue() throws JAXRException {
        LocalizedString ls1 = new LocalizedStringImpl(Locale.US, "USA", "UTF-8");
        assertEquals("USA", ls1.getValue());
        ls1.setValue("Foo");
        assertEquals("Foo", ls1.getValue());
        ls1.setValue(null);
        assertNull(ls1.getValue());
    }
}
