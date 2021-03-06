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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.ExtensibleObject;
import javax.xml.registry.infomodel.Slot;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ExtensibleObjectImpl implements ExtensibleObject
{
    private Map<String,Slot> slots = new HashMap<String,Slot>();

    public void addSlot(Slot slot) throws JAXRException
    {
        slots.put(slot.getName(), slot);
    }

    public void addSlots(Collection slots) throws JAXRException
    {
        for (Object slot : slots) {
            addSlot((Slot) slot);
        }
    }

    public Slot getSlot(String slotName)
    {
        return (Slot) slots.get(slotName);
    }

    public Collection<Slot> getSlots()
    {
        return slots.values();
    }

    public void removeSlot(String slotName)
    {
        slots.remove(slotName);
    }

    public void removeSlots(Collection slotNames)
    {
        slots.keySet().removeAll(slotNames);
    }
}
