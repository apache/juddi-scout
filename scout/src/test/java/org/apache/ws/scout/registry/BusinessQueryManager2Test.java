package org.apache.ws.scout.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.RegistryPackage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.ws.scout.BaseTestCase;

/**
 * Additional BusinessQueryManager test methods.
 * 
 * @author Tom Cunningham (tcunning@apache.org)
 */
public class BusinessQueryManager2Test extends BaseTestCase {
	RegistryService rs, rs2;
	private BusinessQueryManager bqm, bqm2;
	private BusinessLifeCycleManager blm, blm2;
	Collection associationKeys = null;
	
    @Before
    public void setUp() {
        super.setUp();
        login();
        loginSecondUser();
		try {
			rs = connection.getRegistryService();
	    	bqm = rs.getBusinessQueryManager();
	    	blm = rs.getBusinessLifeCycleManager();
	        
	    	rs2 = connection2.getRegistryService();
	        blm2 = rs2.getBusinessLifeCycleManager();
	        bqm2 = rs2.getBusinessQueryManager();

		} catch (JAXRException e) {
			fail(e.getMessage());
		}
    }
    
    @After
    public void tearDown() {
        super.tearDown();
    }
	
	public void testGetRegistryObjects() {
        login();
        try {
        	RegistryService rs = connection.getRegistryService();

        	BusinessQueryManager bqm = rs.getBusinessQueryManager();
        	BusinessLifeCycleManager blm = rs.getBusinessLifeCycleManager();
        	BulkResponse br = bqm.getRegistryObjects();
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    @Test
	public void testFindCallerAssociations() {
        BulkResponse br = null;
    	try {
        	// Are there any associations so far?
            try {
	            br = bqm.findCallerAssociations(null,
	        			new Boolean(true),
	        			new Boolean(true),
	        			null);
	            if (br.getCollection().size() != 0) {
	            	fail("Should not reach here - no associations created yet.");
	            }
	        } catch (Exception e) {
            }

            String orgOne = "Organization One";
            String orgTwo = "Organization Two";
            Organization source = blm.createOrganization(blm.createInternationalString(orgOne));
            Organization target = blm2.createOrganization(blm.createInternationalString(orgTwo));
	            
			Collection orgs = new ArrayList();
			orgs.add(source);
			br = blm.saveOrganizations(orgs);
			if (br.getExceptions() != null)
			{
				fail("Save Organization failed");
			}
			
			Collection sourceKeys = br.getCollection();
			Iterator iter = sourceKeys.iterator();
			Key savekey = null;
			while (iter.hasNext())
			{
				savekey = (Key) iter.next();
			}
			String sourceid  = savekey.getId();
			Organization queried = (Organization) bqm.getRegistryObject(sourceid, LifeCycleManager.ORGANIZATION);
			assertNotNull("Source Org", queried.getName().getValue());
            
			Collection orgstwo = new ArrayList();
			orgs.add(target);
            br = blm2.saveOrganizations(orgstwo);
            if (br.getExceptions() != null)
			{
            	fail("Save Organizations failed");
            }
            Collection targetKeys = br.getCollection();
            iter = targetKeys.iterator();
            while (iter.hasNext())
            {
            	savekey = (Key) iter.next();
            }
            
            String targetid = savekey.getId();
            Organization targetOrg = (Organization) bqm2.getRegistryObject(targetid, LifeCycleManager.ORGANIZATION);
            assertNotNull("Target Org", targetOrg.getName().getValue());

            Concept associationType = null;            
            ClassificationScheme associationTypes =
                bqm.findClassificationSchemeByName(null, "AssociationType");
            Collection types = associationTypes.getChildrenConcepts();
            iter = types.iterator();
            Concept concept = null;
            while (iter.hasNext())
            {
            	concept = (Concept) iter.next();
            	if (concept.getName().getValue().equals("Implements"))
            	{
            		associationType = concept;
            	}
            }
            
            Association a = blm.createAssociation(targetOrg, associationType);
            a.setSourceObject(queried);
            blm2.confirmAssociation(a);

            // publish the Association
            Collection associations = new ArrayList();	
            associations.add(a);
            br = blm2.saveAssociations(associations, false);

            if (br.getExceptions() != null)
            {
            		fail("Save association failed");
            }

            associationKeys = br.getCollection();
            iter = associationKeys.iterator();

            Collection aTypes = new ArrayList();
            aTypes.add(associationType);
            
            br = bqm.findCallerAssociations(null,
            			new Boolean(true),
            			new Boolean(true),
            			aTypes);
            /*
            if (br.getExceptions() == null)
            {
            	Collection results = br.getCollection();
            	if (results.size() > 0)
            	{
            		iter = results.iterator();
            		while (iter.hasNext())
            		{
            			Association a1 = (Association) iter.next();
            			System.out.println("Association : " + a1.toString());
            		}
            	}
            }
            */
		} catch (JAXRException e) {
//			fail(e.getMessage());
		}		
	}
	
    @Test
	public void testFindRegistryPackages() {
        login();
        try {
        	RegistryService rs = connection.getRegistryService();

        	BusinessQueryManager bqm = rs.getBusinessQueryManager();
        	BusinessLifeCycleManager blm = rs.getBusinessLifeCycleManager();

        	Concept type = bqm.findConceptByPath("/AssociationType/RelatedTo");

        	ArrayList namePatterns = new ArrayList();
        	namePatterns.add("%foo%");
        	
        	ArrayList classifications = new ArrayList();
        	classifications.add(type);
        	
        	BulkResponse br = bqm.findRegistryPackages(null, namePatterns, classifications, null);
        	fail("findRegistryPackages is currently unsupported");
        	/*
        	assertEquals(null, br.getExceptions());
        	assertEquals(br.getCollection().size(), 0);
        	
        	RegistryPackage foopack = blm.createRegistryPackage("foo");
        	RegistryPackage barpack = blm.createRegistryPackage("bar");
        	Association assoc = blm.createAssociation(barpack, type);
        	foopack.addAssociation(assoc);
        	ArrayList al = new ArrayList();
            al.add(foopack);
        	br = blm.saveObjects(al);
        	assertEquals(null, br.getExceptions());
        	
        	
        	br = bqm.findRegistryPackages(null, namePatterns, classifications, null);
        	assertEquals(null, br.getExceptions());
        	assertEquals(br.getCollection(), 1);
        */
        } catch (JAXRException e) {
		}				
	}
}
