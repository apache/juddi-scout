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

package org.apache.ws.scout.registry;

import java.io.Serializable;
import java.net.URI;
import java.util.Properties;
import java.util.Set;

import javax.xml.registry.Connection;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;

/**
 * Apache Scout Implementation of a JAXR Connection.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ConnectionImpl implements Connection, Serializable
{
    private static final long serialVersionUID = 1L;
    private boolean closed = false;
    private boolean synchronous = true;
    private Set credentials;
    private final RegistryImpl registry;
    private final String postalScheme;
    private final int maxRows;

    public ConnectionImpl(URI queryManagerURI, URI lifeCycleManagerURI, String transportClass, String postalScheme, int maxRows)
    {
        Properties prop = new Properties();
        /**
         * If you want to override any of the properties
         * juddi RegistryProxy uses, set the System property
         * accordingly.
         */
        if (transportClass!=null) {
    		prop.setProperty(RegistryImpl.TRANSPORT_CLASS_PROPERTY_NAME, transportClass);
    	} else {
    		String transport = System.getProperty(RegistryImpl.TRANSPORT_CLASS_PROPERTY_NAME);
    		if (transport != null) {
    			prop.setProperty(RegistryImpl.TRANSPORT_CLASS_PROPERTY_NAME, transport);
    		}
        }
        /**
         * Even if the properties passed contains no values,
         * juddi takes default values
         */
        registry = new RegistryImpl(prop);   
        registry.setInquiryURI(queryManagerURI);
        registry.setPublishURI(lifeCycleManagerURI);
        this.postalScheme = postalScheme;
        this.maxRows = maxRows;

    }

    public RegistryService getRegistryService() throws JAXRException
    {
        RegistryServiceImpl reg = new RegistryServiceImpl(registry, postalScheme, maxRows);
        reg.setConnection(this);
        return reg;
    }

    public void close()
    {
        closed = true;
    }

    public boolean isClosed()
    {
        return closed;
    }

    public Set getCredentials()
    {
        return credentials;
    }

    public void setCredentials(Set credentials)
    {
        this.credentials = credentials;
    }

    public boolean isSynchronous()
    {
        return synchronous;
    }

    public void setSynchronous(boolean synchronous)
    {
        this.synchronous = synchronous;
    }
}
