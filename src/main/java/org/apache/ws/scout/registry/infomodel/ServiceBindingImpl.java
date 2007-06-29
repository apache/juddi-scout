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
import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.UnexpectedObjectException;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.SpecificationLink;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ServiceBindingImpl extends RegistryObjectImpl implements ServiceBinding
{
    private Collection links = null;
    private String accessuri = null;
    private Service service = null;
    private ServiceBinding targetbinding = null;
    private boolean validateuri = false;

    public ServiceBindingImpl(LifeCycleManager lifeCycleManager)
    {
        super(lifeCycleManager);
    }

    public ServiceBindingImpl(LifeCycleManager lifeCycleManager, InternationalString n)
    {
        super(lifeCycleManager, n);
    }

    public void addSpecificationLink(SpecificationLink sl) throws JAXRException
    {
        if(links == null) links = new ArrayList();
        links.add(sl);
        ((SpecificationLinkImpl)sl).setServiceBinding(this);
    }

    public void addSpecificationLinks(Collection col) throws JAXRException
    {
        try
        {
            if(links == null) links = new ArrayList();
            Iterator iter = col.iterator();
            while(iter.hasNext())
            {
               addSpecificationLink((SpecificationLink)iter.next());
            }
        } catch (ClassCastException e)
        {
            throw new UnexpectedObjectException();
        }
    }

    public String getAccessURI() throws JAXRException
    {
        return accessuri;
    }

    public Service getService() throws JAXRException
    {
        return service;
    }

    public Collection getSpecificationLinks() throws JAXRException
    {
        return links;
    }

    public ServiceBinding getTargetBinding() throws JAXRException
    {
        return targetbinding;
    }

    public void removeSpecificationLink(SpecificationLink link) throws JAXRException
    {
        if(links == null) links = new ArrayList();
        links.remove(link);
    }

    public void removeSpecificationLinks(Collection col) throws JAXRException
    {
        if(links == null) links = new ArrayList();
        links.removeAll(col);
    }

    public void setAccessURI(String s) throws JAXRException
    {
        if(targetbinding != null)
        throw new InvalidRequestException("There is already a Target Binding defined");
        accessuri = s;
    }

    public void setTargetBinding(ServiceBinding sb) throws JAXRException
    {
        if(accessuri != null)
                throw new InvalidRequestException("There is already an Access URI defined");

        targetbinding = sb;
    }

    public boolean getValidateURI() throws JAXRException
    {
        return validateuri;
    }

    public void setValidateURI(boolean b) throws JAXRException
    {
        validateuri = b;
    }

    //Specific API
    public void setService(Service s)
    {
        service =s;
    }
}
