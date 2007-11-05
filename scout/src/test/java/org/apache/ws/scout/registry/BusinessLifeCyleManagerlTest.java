package org.apache.ws.scout.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;

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
        
            
            
    //TODO cover these methods
    
//    BulkResponse deleteAssociations(Collection<Key> associationKeys) throws JAXRException;
//
//    BulkResponse deleteConcepts(Collection<Key> conceptKeys) throws JAXRException;
//
//    BulkResponse deleteServiceBindings(Collection<Key> bindingKeys) throws JAXRException;
//
//    BulkResponse saveAssociations(Collection<Association> associations, boolean replace) throws JAXRException;
//
//    BulkResponse saveConcepts(Collection<Concept> concepts) throws JAXRException;  
//
//    BulkResponse saveServiceBindings(Collection<ServiceBinding> bindings) throws JAXRException;
//
//    void unConfirmAssociation(Association assoc) throws JAXRException, InvalidRequestException;
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(BusinessLifeCyleManagerlTest.class);
    }

}
