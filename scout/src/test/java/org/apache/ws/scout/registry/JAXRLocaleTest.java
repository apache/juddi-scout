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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;

import junit.framework.JUnit4TestAdapter;

import org.apache.ws.scout.BaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 */
public class JAXRLocaleTest extends BaseTestCase
{
    private BusinessLifeCycleManager blm = null;

    @Before
    public void setUp()
    {
       super.setUp();
    }

    @After
    public void tearDown()
    {
        super.tearDown();
    }

    @Test
    public void testPublishOrganizationAndService() throws Exception {
        login();

        RegistryService rs = connection.getRegistryService();
        BusinessQueryManager bqm = rs.getBusinessQueryManager();
        blm = rs.getBusinessLifeCycleManager();

        InternationalString is;
        BulkResponse br;
        Key key;
        Locale locale = Locale.GERMAN;

        // create Organization
        
        Organization organization = (Organization) blm.createObject(BusinessLifeCycleManager.ORGANIZATION);

        is = getIString(locale, "Apache Scout Org");
        organization.setName(is);
        is = getIString(locale, "This is the org for Apache Scout Test");
        organization.setDescription(is);

        Collection<Organization> organizations = new ArrayList<Organization>();
        organizations.add(organization);

        br = blm.saveOrganizations(organizations);
        checkResponse(br);

        assertEquals(1, br.getCollection().size());
        key = (Key) br.getCollection().iterator().next();

        Organization organization1 = (Organization) bqm.getRegistryObject(key.getId(), LifeCycleManager.ORGANIZATION);

        System.out.println(organization1.getName().getValue() + " " + organization1.getDescription().getValue());
        
        assertEquals(organization.getName().getValue(locale),
                     organization1.getName().getValue(locale));
        
        assertEquals(organization.getDescription().getValue(locale), 
                     organization1.getDescription().getValue(locale));
                       
        // create Service
        Service service = (Service) blm.createObject(BusinessLifeCycleManager.SERVICE);

        is = getIString(locale, "Apache Scout Service");
        service.setName(is);
        is = getIString(locale, "This is the service for Apache Scout Test");
        service.setDescription(is);

        organization1.addService(service);
        
        Collection<Service> services = new ArrayList<Service>();
        services.add(service);

        br = blm.saveServices(services);
        checkResponse(br);

        assertEquals(1, br.getCollection().size());
        key = (Key) br.getCollection().iterator().next();

        Service service1 = (Service) bqm.getRegistryObject(key.getId(), LifeCycleManager.SERVICE);

        System.out.println(service1.getName().getValue() + " " + service1.getDescription().getValue());
        
        assertEquals(service.getName().getValue(locale),
                     service1.getName().getValue(locale));
        
        assertEquals(service.getDescription().getValue(locale), 
                     service1.getDescription().getValue(locale));
        
        //Cleanup
        Collection<Key> serviceKeys = new ArrayList<Key>();
        serviceKeys.add(key);
        blm.deleteServices(serviceKeys);
        
        Collection<Key> orgKeys = new ArrayList<Key>();
        orgKeys.add(organization1.getKey());
        blm.deleteOrganizations(orgKeys); 
    }
    
    public void testPublishConcept() throws Exception {
        login();

        RegistryService rs = connection.getRegistryService();
        BusinessQueryManager bqm = rs.getBusinessQueryManager();
        blm = rs.getBusinessLifeCycleManager();

        Locale locale = Locale.GERMAN;

        Concept concept = (Concept) blm.createObject(BusinessLifeCycleManager.CONCEPT);
        InternationalString is;

        is = getIString(locale, "Apache Scout Concept -- APACHE SCOUT TEST");
        concept.setName(is);
        is = getIString(locale, "This is the concept for Apache Scout Test");
        concept.setDescription(is);

        Collection<Concept> concepts = new ArrayList<Concept>();
        concepts.add(concept);

        BulkResponse br = blm.saveConcepts(concepts);
        checkResponse(br);

        assertEquals(1, br.getCollection().size());
        Key key = (Key) br.getCollection().iterator().next();

        Concept concept1 = (Concept) bqm.getRegistryObject(key.getId(), LifeCycleManager.CONCEPT);

        System.out.println(concept1.getName().getValue() + " " + concept1.getDescription().getValue());

      
        assertEquals(concept.getName().getValue(locale),
                      concept1.getName().getValue(locale));         

        assertEquals(concept.getDescription().getValue(locale), 
                     concept1.getDescription().getValue(locale));
        
        //cleanup
        Collection<Key> conceptKeys = new ArrayList<Key>();
        conceptKeys.add(concept1.getKey());
        blm.deleteOrganizations(conceptKeys);
    }

    private void checkResponse(BulkResponse br) throws JAXRException {
        if (br.getStatus() == JAXRResponse.STATUS_SUCCESS)
        {
            System.out.println("Object saved.");
            Collection coll = br.getCollection();
            Iterator iter = coll.iterator();
            while (iter.hasNext())
            {
                Key key = (Key) iter.next();
                System.out.println("Saved Key=" + key.getId());
            }//end while
        } else
        {
            System.err.println("JAXRExceptions " +
                    "occurred during save:");
            Collection exceptions = br.getExceptions();
            Iterator iter = exceptions.iterator();
            while (iter.hasNext())
            {
                Exception e = (Exception) iter.next();
                System.err.println(e.toString());
                fail(e.toString());
            }
        }
    }

    private InternationalString getIString(Locale locale, String str)
            throws JAXRException
    {
        return blm.createInternationalString(locale, str);
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(JAXRLocaleTest.class);
    }

}
