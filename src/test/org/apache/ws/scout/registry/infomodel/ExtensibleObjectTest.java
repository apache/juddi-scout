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
import javax.xml.registry.infomodel.ExtensibleObject;
import javax.xml.registry.infomodel.Slot;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @version $Revision$ $Date$
 */
public class ExtensibleObjectTest extends TestCase {
    private ExtensibleObject eo;
    private Slot slot;
    private Slot slot2;

    public void testAddSlot() throws JAXRException {
        eo.addSlot(slot2);
        assertEquals(2, eo.getSlots().size());
        assertTrue(eo.getSlots().contains(slot));
        assertTrue(eo.getSlots().contains(slot2));
    }

    public void testAddSlots() throws JAXRException {
        Set slots = new HashSet();
        slots.add(slot);
        slots.add(slot2);
        eo.addSlots(slots);
        assertEquals(slots, new HashSet(eo.getSlots()));
    }

    public void testGetSlot() throws JAXRException {
        assertEquals(slot, eo.getSlot("MockSlot"));
    }

    public void testGetSlots() throws JAXRException {
        Collection slots = eo.getSlots();
        assertEquals(1, slots.size());
        assertTrue(slots.contains(slot));
    }

    public void testRemoveSlot() throws JAXRException {
        eo.removeSlot("MockSlot");
        assertTrue(eo.getSlots().isEmpty());
    }

    public void testRemoveSlotWithOneRemaining() throws JAXRException {
        eo.addSlot(slot2);
        eo.removeSlot("MockSlot");
        Collection slots = eo.getSlots();
        assertEquals(1, slots.size());
        assertFalse(slots.contains(slot));
        assertTrue(slots.contains(slot2));
    }

    public void testRemoveSlots() throws JAXRException {
        MockSlot slot3 = new MockSlot("MockSlot3");
        eo.addSlot(slot2);
        eo.addSlot(slot3);
        Collection slots = new HashSet();
        slots.add(slot2.getName());
        slots.add("MockSlot4");
        eo.removeSlots(slots);
        slots = eo.getSlots();
        assertEquals(2, slots.size());
        assertTrue(slots.contains(slot));
        assertFalse(slots.contains(slot2));
        assertTrue(slots.contains(slot3));
    }

    public void setUp() throws Exception {
        super.setUp();
        eo = new TestObject();
        slot = new MockSlot("MockSlot");
        eo.addSlot(slot);
        slot2 = new MockSlot("MockSlot2");
    }

    private static class TestObject extends ExtensibleObjectImpl {
    }

    private class MockSlot implements Slot {
        private String name;

        public MockSlot(String name) {
            this.name = name;
        }

        public String getName() throws JAXRException {
            return name;
        }

        public String getSlotType() throws JAXRException {
            return null;
        }

        public Collection getValues() throws JAXRException {
            return null;
        }

        public void setName(String name) throws JAXRException {
        }

        public void setSlotType(String slotType) throws JAXRException {
        }

        public void setValues(Collection values) throws JAXRException {
        }
    }
}
