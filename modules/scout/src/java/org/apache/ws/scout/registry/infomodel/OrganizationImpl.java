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

import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.UnsupportedCapabilityException;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.PostalAddress;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.TelephoneNumber;
import javax.xml.registry.infomodel.User;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Organization Interface
 * * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class OrganizationImpl extends RegistryObjectImpl implements Organization {
    private User primaryContact;
    private Set users = new HashSet();
    private Set telephoneNumbers = new HashSet();
    private Set services = new HashSet();

    public OrganizationImpl(LifeCycleManager lifeCycleManager) {
        super(lifeCycleManager);
    }

    public User getPrimaryContact() throws JAXRException {
        if (primaryContact == null) {
            throw new IllegalStateException("primaryContact is null and the spec says we cannot return a null value");
        }
        return primaryContact;
    }

    public void setPrimaryContact(User user) throws JAXRException {
        if (user == null) {
            throw new IllegalArgumentException("primaryContact must not be null");
        }
        users.add(user);
        primaryContact = user;
    }

    public void addUser(User user) throws JAXRException {
        users.add(user);
    }

    public void addUsers(Collection collection) throws JAXRException {
        // do this by hand to ensure all members are actually instances of User
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            User user = (User) iterator.next();
            users.add(user);
        }
    }

    public Collection getUsers() throws JAXRException {
        return Collections.unmodifiableCollection(users);
    }

    public void removeUser(User user) throws JAXRException {
        if (primaryContact.equals(user)) {
            throw new InvalidRequestException("Cannot remove primaryContact");
        }
        users.remove(user);
    }

    public void removeUsers(Collection collection) throws JAXRException {
        if (collection.contains(primaryContact)) {
            throw new InvalidRequestException("Cannot remove primaryContact");
        }
        users.removeAll(collection);
    }


    public Collection getTelephoneNumbers(String phoneType) throws JAXRException {
        Set filteredNumbers;
        if (phoneType == null) {
            filteredNumbers = telephoneNumbers;
        } else {
            filteredNumbers = new HashSet(telephoneNumbers.size());
            for (Iterator i = telephoneNumbers.iterator(); i.hasNext();) {
                TelephoneNumber number = (TelephoneNumber) i.next();
                if (phoneType.equals(number.getType())) {
                    filteredNumbers.add(number);
                }
            }
        }
        return Collections.unmodifiableSet(filteredNumbers);
    }

    public void setTelephoneNumbers(Collection collection) throws JAXRException {
        // do this by hand to ensure all members are actually instances of TelephoneNumber
        Set numbers = new HashSet(collection.size());
        for (Iterator i = collection.iterator(); i.hasNext();) {
            TelephoneNumber number = (TelephoneNumber) i.next();
            numbers.add(number);
        }
        this.telephoneNumbers = numbers;
    }

    public void addService(Service service) throws JAXRException {
        services.add(service);
    }

    public void addServices(Collection collection) throws JAXRException {
        // do this by hand to ensure all members are actually instances of Service
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Service service = (Service) iterator.next();
            services.add(service);
        }
    }

    public Collection getServices() throws JAXRException {
        return Collections.unmodifiableSet(services);
    }


    public void removeService(Service service) throws JAXRException {
        services.remove(service);
    }

    public void removeServices(Collection collection) throws JAXRException {
        services.removeAll(collection);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Level 1 features must throw exceptions
    ///////////////////////////////////////////////////////////////////////////

    public Organization getParentOrganization() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public Collection getDescendantOrganizations() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public Organization getRootOrganization() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void addChildOrganization(Organization organization) throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void addChildOrganizations(Collection collection) throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public int getChildOrganizationCount() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public Collection getChildOrganizations() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void removeChildOrganization(Organization organization) throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void removeChildOrganizations(Collection collection) throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public PostalAddress getPostalAddress() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void setPostalAddress(PostalAddress postalAddress) throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }
}
