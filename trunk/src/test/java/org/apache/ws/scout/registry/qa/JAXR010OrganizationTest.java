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
package org.apache.ws.scout.registry.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;

import junit.framework.JUnit4TestAdapter;

import org.apache.ws.scout.BaseTestCase;
import org.apache.ws.scout.Creator;
import org.apache.ws.scout.Finder;
import org.apache.ws.scout.Printer;
import org.apache.ws.scout.Remover;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test to check Jaxr Publish
 * Open source UDDI Browser  <http://www.uddibrowser.org>
 * can be used to check your results
 * @author <mailto:anil@apache.org>Anil Saldhana
 * @since Nov 20, 2004
 */
public class JAXR010OrganizationTest extends BaseTestCase
{
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
    public void publishClassificationScheme()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
            Creator creator = new Creator(blm);
            
            Collection<ClassificationScheme> schemes = new ArrayList<ClassificationScheme>();
            ClassificationScheme classificationScheme = creator.createClassificationScheme(this.getClass().getName());
            schemes.add(classificationScheme);
            
            BulkResponse bulkResponse = blm.saveClassificationSchemes(schemes);
            assertEquals(JAXRResponse.STATUS_SUCCESS,bulkResponse.getStatus());
            
            
        } catch (JAXRException e) {
            e.printStackTrace();
            assertTrue(false);
        }   
    }
    
    @Test
    public void publishOrganization()
    {
        BulkResponse response = null;
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
            bqm = rs.getBusinessQueryManager();
            Creator creator = new Creator(blm);
            Finder finder = new Finder(bqm, uddiversion);
            
            Collection<Organization> orgs = new ArrayList<Organization>();
            Organization organization = creator.createOrganization(this.getClass().getName());
//          Add a Service
            Service service = creator.createService(this.getClass().getName());
            ServiceBinding serviceBinding = creator.createServiceBinding();
            service.addServiceBinding(serviceBinding);
            organization.addService(service);
            //Add a classification
            ClassificationScheme cs = finder.findClassificationSchemeByName(this.getClass().getName());
            Classification classification = creator.createClassification(cs);
            organization.addClassification(classification);
            
            orgs.add(organization);

            //Now save the Organization along with a Service, ServiceBinding and Classification
            BulkResponse br = blm.saveOrganizations(orgs);
            if (br.getStatus() == JAXRResponse.STATUS_SUCCESS)
            {
                System.out.println("Organization Saved");
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
                }
            }
            
        } catch (JAXRException e) {
            e.printStackTrace();
			assertTrue(false);
        }
        assertNull(response);
    }
    
    @Test
    public void queryOrganization()
    {
        login();
        try
        {
            // Get registry service and business query manager
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            System.out.println("We have the Business Query Manager");
            Printer printer = new Printer();
            Finder finder = new Finder(bqm, uddiversion);

            Collection orgs = finder.findOrganizationsByName(this.getClass().getName());
            if (orgs == null) {
                fail("Only Expecting 1 Organization");
            } else {
                assertEquals(1,orgs.size());
                // then step through them
                for (Iterator orgIter = orgs.iterator(); orgIter.hasNext();)
                {
                    Organization org = (Organization) orgIter.next();
                    System.out.println("Org name: " + printer.getName(org));
                    System.out.println("Org description: " + printer.getDescription(org));
                    System.out.println("Org key id: " + printer.getKey(org));

                    printer.printUser(org);
                    printer.printServices(org);
                    printer.printClassifications(org);
                }
            }//end else
        } catch (JAXRException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } 
    }
    
    @Test
    public void deleteOrganization()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
    //      Get registry service and business query manager
            bqm = rs.getBusinessQueryManager();
            System.out.println("We have the Business Query Manager");
            Finder finder = new Finder(bqm, uddiversion);
            Remover remover = new Remover(blm);
            Collection orgs = finder.findOrganizationsByName(this.getClass().getName());
            for (Iterator orgIter = orgs.iterator(); orgIter.hasNext();)
            {
                Organization org = (Organization) orgIter.next();
                remover.removeOrganization(org);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void deleteClassificationScheme()
    {
        login();
        try {
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();
            System.out.println("We have the Business Query Manager");
            Finder finder = new Finder(bqm, uddiversion);
            Remover remover = new Remover(blm);
            Collection schemes = finder.findClassificationSchemesByName(this.getClass().getName());
            for (Iterator iter = schemes.iterator(); iter.hasNext();)
            {
                ClassificationScheme scheme = (ClassificationScheme) iter.next();
                remover.removeClassificationScheme(scheme);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(JAXR010OrganizationTest.class);
    }
}
