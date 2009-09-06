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
package org.apache.ws.scout.registry;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.CapabilityProfile;
import javax.xml.registry.DeclarativeQueryManager;
import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;
import javax.xml.registry.UnsupportedCapabilityException;
import javax.xml.registry.infomodel.ClassificationScheme;

import org.apache.ws.scout.registry.infomodel.ClassificationSchemeImpl;
import org.apache.ws.scout.registry.infomodel.KeyImpl;
import org.apache.ws.scout.transport.TransportException;

/**
 * Scout implementation of javax.xml.registry.RegistryService
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 * @author Jeremy Boynes <jboynes@apache.org>
 * @author Tom Cunningham <tcunning@apache.org>
 */
public class RegistryServiceImpl implements RegistryService
{
    private final IRegistryBase registry;
    private final BusinessQueryManager queryManager;
    private final BusinessLifeCycleManager lifeCycleManager;
    
    private final ClassificationSchemeImpl postalScheme;
    private final int maxRows;
    private final String uddiVersion;

    private ConnectionImpl connection;

    public RegistryServiceImpl(IRegistryBase registry, String postalScheme, int maxRows, String uddiVersion)
    {
        this.registry = registry;
        this.maxRows = maxRows;
        this.uddiVersion = uddiVersion;
        if ("3.0".equals(uddiVersion)) {
        	queryManager = new BusinessQueryManagerV3Impl(this);
        	lifeCycleManager = new BusinessLifeCycleManagerV3Impl(this);
        } else {
        	queryManager = new BusinessQueryManagerImpl(this);
        	lifeCycleManager = new BusinessLifeCycleManagerImpl(this);
        }
        if (postalScheme == null)
        {
            this.postalScheme = null;
        } else
        {
            this.postalScheme = new ClassificationSchemeImpl(lifeCycleManager);
            this.postalScheme.setKey(new KeyImpl(postalScheme));
        }
    }
 
    IRegistryBase getRegistry()
    {
        return registry;
    }

    BusinessLifeCycleManager getLifeCycleManagerImpl()
    {
        return lifeCycleManager;
    }

    int getMaxRows()
    {
        return maxRows;
    }

    public CapabilityProfile getCapabilityProfile()
    {
        return new CapabilityProfileImpl();
    }

    public String getUddiVersion() {
    	return uddiVersion;
    }
    
    public BusinessQueryManager getBusinessQueryManager() throws JAXRException
    {
        return queryManager;
    }

    public BusinessLifeCycleManager getBusinessLifeCycleManager() throws JAXRException
    {
        return lifeCycleManager;
    }

    public BulkResponse getBulkResponse(String s) throws JAXRException, InvalidRequestException
    {
        if (s == "" || s == null)
            throw new InvalidRequestException();
        return null;
    }

    public DeclarativeQueryManager getDeclarativeQueryManager() throws JAXRException, UnsupportedCapabilityException
    {
        throw new UnsupportedCapabilityException();
    }

    public ClassificationScheme getDefaultPostalScheme() throws JAXRException
    {
        return postalScheme;
    }

    public String makeRegistrySpecificRequest(String s) throws JAXRException
    {
       String inquiry = "INQUIRY";
       String publish = "PUBLISH";
       String type = "";

       //TODO: Need a better way to do this
       String snippet = s.substring(0,20);
       if( snippet.indexOf("save") > -1) type = publish;
       else
       type = inquiry;

       try {
    	   return registry.execute(s,type);
	   } catch (TransportException e) {
		   throw new JAXRException(e.getLocalizedMessage());
	   } 
     }

    public ConnectionImpl getConnection()
    {
        return connection;
    }

    public void setConnection(ConnectionImpl connection)
    {
        this.connection = connection;
    }

}
