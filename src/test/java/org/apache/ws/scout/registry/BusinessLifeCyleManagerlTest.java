package org.apache.ws.scout.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;

import junit.framework.JUnit4TestAdapter;

import org.apache.ws.scout.BaseTestCase;
import org.apache.ws.scout.Creator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *  Tests the BusinessLifecycleManagerImpl class
 */
public class BusinessLifeCyleManagerlTest extends BaseTestCase 
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
    
    @SuppressWarnings("unchecked")
    @Test
    public void saveDeleteOrganizations() 
    {
        login();
        Collection<Key> orgKeys = new ArrayList<Key>();
        try {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
            Creator creator = new Creator(blm);
            Collection<Organization> orgs = new ArrayList<Organization>();
            Organization organization = creator.createOrganization(this.getClass().getName());
            orgs.add(organization);

            //save the Organization
            BulkResponse br = blm.saveOrganizations(orgs);
            assertEquals(JAXRResponse.STATUS_SUCCESS, br.getStatus());
            orgKeys = (Collection<Key>) br.getCollection();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
        try {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
            BulkResponse br = blm.deleteOrganizations(orgKeys);
            assertEquals(JAXRResponse.STATUS_SUCCESS, br.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void saveDeleteClassificationSchemes() 
    {
        login();
       
        try {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
            
            ClassificationScheme cScheme = blm.createClassificationScheme("testScheme -- APACHE SCOUT TEST", "Sample Classification Scheme");

            ArrayList<ClassificationScheme> cSchemes = new ArrayList<ClassificationScheme>();
            cSchemes.add(cScheme);
            //save
            BulkResponse br = blm.saveClassificationSchemes(cSchemes);
            assertEquals(BulkResponse.STATUS_SUCCESS, br.getStatus());
            //remove
            BulkResponse br2 = blm.deleteClassificationSchemes((Collection<Key>)br.getCollection());
            assertEquals(BulkResponse.STATUS_SUCCESS, br2.getStatus());
        } catch (JAXRException je) {
            fail(je.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void saveDeleteServices() 
    {
        login();
       
        try {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
            Creator creator = new Creator(blm);
            Collection<Organization> orgs = new ArrayList<Organization>();
            Organization organization = creator.createOrganization(this.getClass().getName());
            orgs.add(organization);
//          save the Organization
            BulkResponse br = blm.saveOrganizations(orgs);
            assertEquals(JAXRResponse.STATUS_SUCCESS, br.getStatus());
            organization.setKey((Key)br.getCollection().iterator().next());
            
            Service service = creator.createService(this.getClass().getName());
            ArrayList<Service> services = new ArrayList<Service>();
            organization.addService(service);
            services.add(service);
            //save service
            BulkResponse br2 = blm.saveServices(services);
            assertEquals(BulkResponse.STATUS_SUCCESS, br2.getStatus());
            //remove service
            BulkResponse br3 = blm.deleteServices((Collection<Key>)br2.getCollection());
            assertEquals(BulkResponse.STATUS_SUCCESS, br3.getStatus());
            //remove organization
            BulkResponse br4 = blm.deleteOrganizations((Collection<Key>)br.getCollection());
            assertEquals(JAXRResponse.STATUS_SUCCESS, br4.getStatus());
        } catch (JAXRException je) {
            fail(je.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void saveDeleteServiceBindings() 
    {
        login();
       
        try {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
            Creator creator = new Creator(blm);
            Collection<Organization> orgs = new ArrayList<Organization>();
            Organization organization = creator.createOrganization(this.getClass().getName());
            orgs.add(organization);
//          save the Organization
            BulkResponse br = blm.saveOrganizations(orgs);
            assertEquals(JAXRResponse.STATUS_SUCCESS, br.getStatus());
            organization.setKey((Key)br.getCollection().iterator().next());
            
            Service service = creator.createService(this.getClass().getName());
            ArrayList<Service> services = new ArrayList<Service>();
            organization.addService(service);
            services.add(service);
            //save service
            BulkResponse br2 = blm.saveServices(services);
            assertEquals(BulkResponse.STATUS_SUCCESS, br2.getStatus());
            
            service.setKey((Key)br2.getCollection().iterator().next());
            
            //save serviceBinding
            ServiceBinding serviceBinding = creator.createServiceBinding();
            service.addServiceBinding(serviceBinding);
            ArrayList<ServiceBinding> serviceBindings = new ArrayList<ServiceBinding>();
            serviceBindings.add(serviceBinding);
            BulkResponse br5 = blm.saveServiceBindings(serviceBindings);
            assertEquals(BulkResponse.STATUS_SUCCESS, br5.getStatus());
            
            //remove serviceBindings
            BulkResponse br6 = blm.deleteServiceBindings((Collection<Key>)br5.getCollection());
            assertEquals(BulkResponse.STATUS_SUCCESS, br6.getStatus());
            
            //remove service
            BulkResponse br3 = blm.deleteServices((Collection<Key>)br2.getCollection());
            assertEquals(BulkResponse.STATUS_SUCCESS, br3.getStatus());
            //remove organization
            BulkResponse br4 = blm.deleteOrganizations((Collection<Key>)br.getCollection());
            assertEquals(JAXRResponse.STATUS_SUCCESS, br4.getStatus());
        } catch (JAXRException je) {
            fail(je.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void saveDeleteConcepts() 
    {
        login();
        try {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
            Collection<Concept> concepts = new ArrayList<Concept>();
            Concept concept = blm.createConcept(null, "TestConcept", "");
            InternationalString is = blm.createInternationalString("This is the concept for Apache Scout Test");
            concept.setDescription(is);
            concepts.add(concept);

            BulkResponse br = blm.saveConcepts(concepts);
            assertEquals(BulkResponse.STATUS_SUCCESS, br.getStatus());
            
            BulkResponse br2 = blm.deleteConcepts((Collection<Key>)br.getCollection());
            assertEquals(BulkResponse.STATUS_SUCCESS, br2.getStatus());
        } catch (JAXRException je) {
            fail(je.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void saveDeleteAssociations() 
    {
        login();
        try {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
            bqm = rs.getBusinessQueryManager();
            Creator creator = new Creator(blm);
            
            System.out.println("\nCreating temporary organizations...\n");
            Organization sOrg = creator.createOrganization("sourceOrg");
            Organization tOrg = creator.createOrganization("targetOrg");
            Collection<Organization> organizations = new ArrayList<Organization>();
            organizations.add(sOrg);
            organizations.add(tOrg);
            BulkResponse br = blm.saveOrganizations(organizations);
            assertEquals(BulkResponse.STATUS_SUCCESS, br.getStatus());
            //setting the keys on the organizations
            Collection<Key> keys = (Collection<Key>) br.getCollection();
            Iterator<Key> iterator = keys.iterator();
            sOrg.setKey(iterator.next());
            tOrg.setKey(iterator.next());
            //creating the RelatedTo Association between these two organizations
            Concept type = bqm.findConceptByPath("/AssociationType/RelatedTo");
            Association association = blm.createAssociation(tOrg, type);
            sOrg.addAssociation(association);
            ArrayList<Association> associations = new ArrayList<Association>();
            associations.add(association);
            //save associations
            BulkResponse br2 = blm.saveAssociations(associations, true);
            assertEquals(BulkResponse.STATUS_SUCCESS, br2.getStatus());
            
            //delete association
            BulkResponse br3 = blm.deleteAssociations((Collection<Key>)br2.getCollection());
            assertEquals(BulkResponse.STATUS_SUCCESS, br3.getStatus());
            
            //delete organizations
            BulkResponse br4 = blm.deleteOrganizations((Collection<Key>)br.getCollection());
            assertEquals(BulkResponse.STATUS_SUCCESS, br4.getStatus());
           
        } catch (JAXRException je) {
            fail(je.getMessage());
	    je.printStackTrace();
        }
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(BusinessLifeCyleManagerlTest.class);
    }

}
