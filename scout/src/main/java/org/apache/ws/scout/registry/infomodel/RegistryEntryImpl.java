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

import java.util.Date;

import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.UnsupportedCapabilityException;
import javax.xml.registry.infomodel.RegistryEntry;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class RegistryEntryImpl extends RegistryObjectImpl implements RegistryEntry
{
    //private Date expiry = null;
    //private int major = 1;
    //private int minor = 0;
    //private int stability = 1;
    //private int status = 1;

    //private String userversion = new String();

    /**
     * Creates a new instance of RegistryEntryImpl
     */
    public RegistryEntryImpl(LifeCycleManager lifeCycleManager)
    {
        super(lifeCycleManager);
    }

    public Date getExpiration() throws JAXRException
    {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public int getMajorVersion() throws JAXRException
    {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public int getMinorVersion() throws JAXRException
    {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public int getStability() throws JAXRException
    {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public int getStatus() throws JAXRException
    {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public String getUserVersion() throws JAXRException
    {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void setExpiration(Date date) throws JAXRException
    {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void setMajorVersion(int param) throws JAXRException
    {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void setMinorVersion(int param) throws JAXRException
    {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void setStability(int param) throws JAXRException
    {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void setUserVersion(String str) throws JAXRException
    {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

}
