package org.apache.ws.scout.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;

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

            //Now save the Organization along with a Service, ServiceBinding and Classification
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
    //TODO cover these methods
    
//    BulkResponse deleteAssociations(Collection<Key> associationKeys) throws JAXRException;
//
//    BulkResponse deleteClassificationSchemes(Collection<Key> schemeKeys) throws JAXRException;
//
//    BulkResponse deleteConcepts(Collection<Key> conceptKeys) throws JAXRException;
//
//    BulkResponse deleteServiceBindings(Collection<Key> bindingKeys) throws JAXRException;
//
//    BulkResponse deleteServices(Collection<Key> serviceKeys) throws JAXRException;
//
//    BulkResponse saveAssociations(Collection<Association> associations, boolean replace) throws JAXRException;
//
//    BulkResponse saveClassificationSchemes(Collection<ClassificationScheme> schemes) throws JAXRException;
//
//    BulkResponse saveConcepts(Collection<Concept> concepts) throws JAXRException;
//
      
//
//    BulkResponse saveServiceBindings(Collection<ServiceBinding> bindings) throws JAXRException;
//
//    BulkResponse saveServices(Collection<Service> services) throws JAXRException;
//
//    void unConfirmAssociation(Association assoc) throws JAXRException, InvalidRequestException;
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(BusinessLifeCyleManagerlTest.class);
    }

}
