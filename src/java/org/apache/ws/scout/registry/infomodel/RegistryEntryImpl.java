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
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.infomodel.RegistryEntry;
import java.util.Date;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class RegistryEntryImpl extends RegistryObjectImpl implements RegistryEntry {
    private Date expiry = null;
    private int major = 1;
    private int minor = 0;
    private int stability = 1;
    private int status = 1;

    private String userversion = new String();

    /**
     * Creates a new instance of RegistryEntryImpl
     */
    public RegistryEntryImpl(LifeCycleManager lifeCycleManager) {
        super(lifeCycleManager);
    }

    public Date getExpiration() throws JAXRException {
        return expiry;
    }

    public int getMajorVersion() throws JAXRException {
        return major;
    }

    public int getMinorVersion() throws JAXRException {
        return minor;
    }

    public int getStability() throws JAXRException {
        return stability;
    }

    public int getStatus() throws JAXRException {
        return status;
    }

    public String getUserVersion() throws JAXRException {
        return userversion;
    }

    public void setExpiration(Date date) throws JAXRException {
        expiry = date;
    }

    public void setMajorVersion(int param) throws JAXRException {
        major = param;
    }

    public void setMinorVersion(int param) throws JAXRException {
        minor = param;
    }

    public void setStability(int param) throws JAXRException {
        stability = param;
    }

    public void setUserVersion(String str) throws JAXRException {
        userversion = str;
    }

}
