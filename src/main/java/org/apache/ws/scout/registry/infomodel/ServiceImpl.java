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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.RegistryService;
import javax.xml.registry.UnexpectedObjectException;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ServiceImpl extends RegistryEntryImpl implements Service
{

    private Organization org = null;
    private Collection<ServiceBinding> serviceBindings = new ArrayList<ServiceBinding>();
    private String orgKey = null;
    
    /**
     * Creates a new instance of ServiceImpl
     */
    public ServiceImpl(LifeCycleManager lifeCycleManager)
    {
        super(lifeCycleManager);
    }

    public void addServiceBinding(ServiceBinding sb)
            throws JAXRException
    {
        serviceBindings.add(sb);
        ((ServiceBindingImpl)sb).setService(this);
    }

    public void addServiceBindings(Collection col)
            throws JAXRException
    {
       try{
        Iterator iter = col.iterator();
        while(iter.hasNext())
        {
            addServiceBinding((ServiceBinding)iter.next());
        }
       }catch(ClassCastException ce)
       {
           throw new UnexpectedObjectException(ce.getLocalizedMessage());
       }
    }

    public Organization getProvidingOrganization()
            throws JAXRException
    {
        if (org == null) {
        	if (super.getSubmittingOrganization() != null) {
        		return super.getSubmittingOrganization();
        	} else {
        		RegistryService rs = super.getLifeCycleManager().getRegistryService();
        		BusinessQueryManager bqm = rs.getBusinessQueryManager();
                Organization o = (Organization) bqm.getRegistryObject(orgKey,
                        LifeCycleManager.ORGANIZATION);
                setProvidingOrganization(o);	
                return o;
        	}
        }
        return org;
    }

    public Collection getServiceBindings() throws JAXRException
    {
        return serviceBindings;
    }

    public void removeServiceBinding(ServiceBinding serviceBinding)
            throws JAXRException
    {
        serviceBindings.remove(serviceBinding);
    }

    public void removeServiceBindings(Collection collection)
            throws JAXRException
    {
        serviceBindings.removeAll(collection);
    }

    public void setProvidingOrganization(Organization organization)
            throws JAXRException
    {
        this.org = organization;
    }
    
    public void setSubmittingOrganizationKey(String key) {
    	orgKey = key;
    }
    
    public String getSubmittingOrganizationKey() {
    	return orgKey;
    }   
}
