/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.apache.ws.scout.registry.infomodel;

import javax.xml.registry.infomodel.Slot;
import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import java.util.Collection;

/**
 * Implements Jaxr API
 *
 * @author <mailto:anil@apache.org>Anil Saldhana
 * @since Nov 20, 2004
 */
public class SlotImpl implements Slot
{
    private String slotType;
    private String name;
    private Collection values;
    private LifeCycleManager lcm;

    public SlotImpl(LifeCycleManager lifeCycleManager)
    {
        lcm = lifeCycleManager;
    }
    public String getName() throws JAXRException
    {
      return name;
    }

    public String getSlotType() throws JAXRException
    {
       return slotType;
    }

    public Collection getValues() throws JAXRException
    {
        return values;
    }

    public void setName(String s) throws JAXRException
    {
        name = s;
    }

    public void setSlotType(String s) throws JAXRException
    {
        slotType = s;
    }

    public void setValues(Collection collection) throws JAXRException
    {
        values = collection;
    }
}
