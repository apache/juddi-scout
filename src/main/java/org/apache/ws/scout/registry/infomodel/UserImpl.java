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
import javax.xml.registry.UnsupportedCapabilityException;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.PersonName;
import javax.xml.registry.infomodel.User;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class UserImpl extends RegistryObjectImpl implements User
{
    private PersonName personName = null;

    private Collection postalAddresses = new ArrayList();
    private Collection emailAddresses = new ArrayList();
    private Collection telnumbers = new ArrayList();

    private String type = "";

    private Organization org = null;

    /**
     * Creates a new instance of UserImpl
     */
    public UserImpl(LifeCycleManager lifeCycleManager)
    {
        super(lifeCycleManager);
    }

    public Organization getOrganization() throws JAXRException
    {
        return org;
    }

    public PersonName getPersonName() throws JAXRException
    {
        return personName;
    }

    public Collection getPostalAddresses() throws JAXRException
    {
        return postalAddresses;
    }

    public Collection getTelephoneNumbers(String str)
            throws JAXRException
    {
        return telnumbers;
    }

    public String getType() throws JAXRException
    {
        return type;
    }

    public URL getUrl() throws JAXRException
    {
        throw new UnsupportedCapabilityException();
    }

    public void setEmailAddresses(Collection collection)
            throws JAXRException
    {
        emailAddresses = collection;
    }

    public void setPersonName(PersonName pname) throws JAXRException
    {
        personName = pname;
    }

    public void setPostalAddresses(Collection collection)
            throws JAXRException
    {
        postalAddresses = collection;
    }

    public void setTelephoneNumbers(Collection collection)
            throws JAXRException
    {
        telnumbers = collection;
    }

    public void setType(String str) throws JAXRException
    {
        type = str;
    }

    public void setUrl(URL uRL) throws JAXRException
    {
        throw new UnsupportedCapabilityException();
    }

    public Collection getEmailAddresses() throws JAXRException
    {
        return emailAddresses;
    }

    //Specific API
    public void setOrganization(Organization o)
    {
        org = o;
    }
}
