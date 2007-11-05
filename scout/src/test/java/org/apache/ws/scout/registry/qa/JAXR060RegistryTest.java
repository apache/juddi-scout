/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.apache.ws.scout.registry.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.User;

import junit.framework.JUnit4TestAdapter;

import org.apache.ws.scout.BaseTestCase;
import org.apache.ws.scout.Creator;
import org.apache.ws.scout.Finder;
import org.apache.ws.scout.Remover;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Testing the registry.
 * 
 * @author kstam
 *
 */
public class JAXR060RegistryTest extends BaseTestCase
{
    @Before
    public void setUp() {
        super.setUp();
    }

    @After
    public void tearDown() {
        super.tearDown();
    }
    
    @Test
    public void publishClassificationScheme()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();
            ClassificationScheme cScheme = blm.createClassificationScheme("org.jboss.soa.esb.:category", "JBossESB Classification Scheme");
            ArrayList<ClassificationScheme> cSchemes = new ArrayList<ClassificationScheme>();
            cSchemes.add(cScheme);
            BulkResponse br = blm.saveClassificationSchemes(cSchemes);
            assertEquals(JAXRResponse.STATUS_SUCCESS, br.getStatus());
        } catch (JAXRException je) {
            fail(je.getMessage());
        }
    }
   
	/**
	 * Tests the successful creation of the RED HAT/JBossESB Organization.
	 */
	@Test
	public void publishOrganization() 
	{
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
            Creator creator = new Creator(blm);
            
            Collection<Organization> organizations = new ArrayList<Organization>();
            Organization organization = creator.createOrganization("Red Hat/JBossESB");
            organizations.add(organization);
            BulkResponse br = blm.saveOrganizations(organizations);
			assertEquals(BulkResponse.STATUS_SUCCESS, br.getStatus());
		} catch (JAXRException je) {
			je.printStackTrace();
			assertTrue(false);
		}
	}
	@SuppressWarnings("unchecked")
    @Test
	public void findOrganization() 
	{
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            Finder finder = new Finder(bqm);
			Collection<Organization> orgs = finder.findOrganizationsByName("Red Hat/JBossESB");
			Organization org = orgs.iterator().next();
			assertEquals("Red Hat/JBossESB", org.getName().getValue());
		} catch (JAXRException je) {
			fail(je.getMessage());
		}
		try {
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            Finder finder = new Finder(bqm);
            Collection<Organization> orgs = finder.findOrganizationsByName("Not Existing Org");
			assertEquals(0, orgs.size());
		} catch (JAXRException je) {
			fail(je.getMessage());
		}
	}
	/**
	 * Tests the successful registration of a Service.
	 *
	 */
	@SuppressWarnings("unchecked")
    @Test
	public void publishService()
	{
        login();
		try {
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            Finder finder = new Finder(bqm);
            Collection<Organization> orgs = finder.findOrganizationsByName("Red Hat/JBossESB");
            Organization organization = orgs.iterator().next();
            
            blm = rs.getBusinessLifeCycleManager();
            //Adding the category as prefix for the name
            Service service = blm.createService(blm.createInternationalString("Registry Test ServiceName"));
            service.setDescription(blm.createInternationalString("Registry Test Service Description"));
            Collection<String> findQualifiers = new ArrayList<String>();
            findQualifiers.add(FindQualifier.AND_ALL_KEYS);
            findQualifiers.add(FindQualifier.SORT_BY_NAME_DESC);
            ClassificationScheme cScheme = bqm.findClassificationSchemeByName(findQualifiers, "org.jboss.soa.esb.:category");
            Classification classification = blm.createClassification(cScheme, "category", "registry");
            service.addClassification(classification);
            organization.addService(service);
            Collection<Service> services = new ArrayList<Service>();
            services.add(service);
            BulkResponse br = blm.saveServices(services);
            assertEquals(BulkResponse.STATUS_SUCCESS, br.getStatus());
		} catch (JAXRException je) {
			fail(je.getMessage());
		}
	}
    
    @Test
    public void findServicesByClassification()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();
            Finder finder = new Finder(bqm);
            //Find the service
            Service service = finder.findService("registry","Registry Test ServiceName", blm);
            assertEquals("Registry Test ServiceName", service.getName().getValue());
        } catch (JAXRException je) {
            fail(je.getMessage());
        }
    }
     
	@Test
	public void publishServiceBinding()
	{
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();
            Finder finder = new Finder(bqm);
            //Find the service
            Service service = finder.findService("registry","Registry Test ServiceName", blm);
            
            ServiceBinding serviceBinding = blm.createServiceBinding();
            serviceBinding.setDescription(blm.createInternationalString("eprDescription"));
            String xml = "<epr>epr uri</epr>";
            serviceBinding.setAccessURI(xml);
        
            ArrayList<ServiceBinding> serviceBindings = new ArrayList<ServiceBinding>();
            serviceBindings.add(serviceBinding);
            service.addServiceBindings(serviceBindings);
            Collection<String> findQualifiers = new ArrayList<String>();
            findQualifiers.add(FindQualifier.AND_ALL_KEYS);
            findQualifiers.add(FindQualifier.SORT_BY_NAME_DESC);
            ClassificationScheme cScheme = bqm.findClassificationSchemeByName(findQualifiers, "org.jboss.soa.esb.:category");
            Classification classification = blm.createClassification(cScheme, "category", "registry");
            service.addClassification(classification);
           
            BulkResponse br  = blm.saveServiceBindings(serviceBindings);
            assertEquals(BulkResponse.STATUS_SUCCESS, br.getStatus());
            BulkResponse br2  = blm.saveServiceBindings(serviceBindings); //Save one more
            assertEquals(BulkResponse.STATUS_SUCCESS, br2.getStatus());
           
            //Delete one binding
            Collection<ServiceBinding> serviceBindings2 = finder.findServiceBindings(service.getKey());
            ServiceBinding serviceBinding2 = serviceBindings2.iterator().next();
            Remover remover = new Remover(blm);
            remover.removeServiceBinding(serviceBinding2);
            
		} catch (JAXRException re) {
			fail(re.getMessage());
		}
	}
	/**
	 * Queries the newly added information
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
    @Test
	public void findServicesForAnOrganization()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            Finder finder = new Finder(bqm);
			Collection<Organization> orgs = finder.findOrganizationsByName("Red Hat/JBossESB");
            Organization org = orgs.iterator().next();
			//Listing out the services and their Bindings
			System.out.println("-------------------------------------------------");
            System.out.println("Organization name: " + org.getName().getValue());
            System.out.println("Description: " + org.getDescription().getValue());
            System.out.println("Key id: " + org.getKey().getId());
			User primaryContact = org.getPrimaryContact();
            System.out.println("Primary Contact: " + primaryContact.getPersonName().getFullName());
			Collection services = org.getServices();
			for (Iterator serviceIter = services.iterator();serviceIter.hasNext();) {
				Service service = (Service) serviceIter.next();
                System.out.println("- Service Name: " + service.getName().getValue());
                System.out.println("  Service Key : " + service.getKey().getId());
				Collection serviceBindings = service.getServiceBindings();
				for (Iterator serviceBindingIter = serviceBindings.iterator();serviceBindingIter.hasNext();){
					ServiceBinding serviceBinding = (ServiceBinding) serviceBindingIter.next();
                    System.out.println("  ServiceBinding Description: " + serviceBinding.getDescription().getValue());
					String xml = serviceBinding.getAccessURI();
                    System.out.println("  ServiceBinding URI: " + xml);
					assertEquals("<epr>epr uri</epr>",xml);
				}
			}
            System.out.println("-------------------------------------------------");
	    } catch (Exception je) {
	    	fail(je.getMessage());
		}
    }
    
    @Test
    public void deleteService()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();
            Finder finder = new Finder(bqm);
            //Find the service
            Service service = finder.findService("registry","Registry Test ServiceName", blm);
            Remover remover = new Remover(blm);
            remover.removeService(service);
        } catch (JAXRException je) {
            fail(je.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    @Test
    public void deleteOrganization()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();
            Finder finder = new Finder(bqm);
            Collection<Organization> orgs = finder.findOrganizationsByName("Red Hat/JBossESB");
            Organization org = orgs.iterator().next();
            Remover remover = new Remover(blm);
            remover.removeOrganization(org);
        } catch (JAXRException je) {
            fail(je.getMessage());
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
            Collection<String> findQualifiers = new ArrayList<String>();
            findQualifiers.add(FindQualifier.AND_ALL_KEYS);
            findQualifiers.add(FindQualifier.SORT_BY_NAME_DESC);
            ClassificationScheme cScheme = bqm.findClassificationSchemeByName(findQualifiers, "org.jboss.soa.esb.:category");
            Remover remover = new Remover(blm);
            remover.removeClassificationScheme(cScheme);
        } catch (JAXRException je) {
            fail(je.getMessage());
        }
    }
	
   
	

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(JAXR060RegistryTest.class);
	}

}
