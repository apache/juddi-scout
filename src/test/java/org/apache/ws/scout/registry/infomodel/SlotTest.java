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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.Slot;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class SlotTest extends TestCase {
    private Slot slot;

    public void testEmptySlot() throws JAXRException {
        slot = new SlotImpl();
        assertNull(slot.getName());
        assertNull(slot.getSlotType());
        assertNotNull(slot.getValues()); // values may be empty but not null
        assertTrue(slot.getValues().isEmpty());
    }

    public void testName() throws JAXRException {
        slot.setName("Test Name");
        assertEquals("Test Name", slot.getName());
    }

    public void testType() throws JAXRException {
        slot.setSlotType("Test Type");
        assertEquals("Test Type", slot.getSlotType());
    }

    public void testValues() throws JAXRException {
        // check duplicate values are removed
        Collection<String> values = new ArrayList<String>();
        values.add("Value 1");
        values.add("Value 2");
        values.add("Value 2");
        slot.setValues(values);

        values = new HashSet<String>();
        values.add("Value 1");
        values.add("Value 2");
        assertEquals(values, slot.getValues());
    }

    public void testNullValues() throws JAXRException {
        try {
            slot.setValues(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    public void testEquals() throws JAXRException {
        Slot slot1 = new SlotImpl();
        Slot slot2 = new SlotImpl();
        assertTrue(slot1.equals(slot2));
        slot1.setName("test");
        assertFalse(slot1.equals(slot2));
        slot2.setName("test");
        assertTrue(slot1.equals(slot2));
        slot1.setName(null);
        assertFalse(slot1.equals(slot2));
    }

    protected void setUp() throws Exception {
        super.setUp();
        slot = new SlotImpl();
    }
}
