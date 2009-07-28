/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
package org.apache.ws.scout.registry;

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
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;

import org.apache.ws.scout.BaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * Test for SCOUT-55 which attempts to test whether getRegistryObjects() returns caller structures, or all structures. 
 * At the moment there appears no way of implementing this using the UDDI API, so the actual test is being checked in
 * commented out (it will fail).    Would be good to have if we can figure out how to fix SCOUT-55.
 *
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class OwnershipTest extends BaseTestCase {
	RegistryService rs, rs2;
	private BusinessQueryManager bqm, bqm2;
	@SuppressWarnings("unused")
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
    
    private InternationalString getIString(String str)
    throws JAXRException
    {
        return blm.createInternationalString(str);
    }

    
    private Organization createTempOrg(String tempOrgName, BusinessLifeCycleManager blm) throws JAXRException {
        Key orgKey = null;
        Organization org = blm.createOrganization(getIString(tempOrgName));
        org.setDescription(getIString("Temporary organization to test saveService()"));

        Collection<Organization> orgs = new ArrayList<Organization>();
        orgs.add(org);
        BulkResponse br = blm.saveOrganizations(orgs);

        if (br.getStatus() == JAXRResponse.STATUS_SUCCESS)
        {
            orgKey = (Key) br.getCollection().iterator().next();
            System.out.println("Temporary Organization Created with id=" + orgKey.getId());
            org.setKey(orgKey);
        }  else
        {
            System.err.println("JAXRExceptions " +
                    "occurred during creation of temporary organization:");

            Iterator iter = br.getCollection().iterator();

            while (iter.hasNext()) {
                Exception e = (Exception) iter.next();
                System.err.println(e.toString());
            }

            fail();
        }
        return org;
    }

    private void deleteTempOrg(Key key) throws JAXRException {

        if (key == null) {
                return;
        }

        String id = key.getId();

        System.out.println("\nDeleting temporary organization with id " + id + "\n");

        Collection<Key> keys = new ArrayList<Key>();
        keys.add(key);
        BulkResponse response = blm.deleteOrganizations(keys);

        Collection exceptions = response.getExceptions();
        if (exceptions == null) {
                Collection retKeys = response.getCollection();
                Iterator keyIter = retKeys.iterator();
                Key orgKey = null;
                if (keyIter.hasNext()) {
                        orgKey =
                                (javax.xml.registry.infomodel.Key) keyIter.next();
                        id = orgKey.getId();
                        System.out.println("Organization with ID=" + id + " was deleted");
                }
        }
    }
    
    @Test
	public void testGetRegistryObjects() {

    	login();
        try {

        	Organization org1 = createTempOrg("OWNERSHIPTEST Organization 1", blm);
        	Organization org2 = createTempOrg("OWNERSHIPTEST Organization 2", blm);
        	
        	BulkResponse br = bqm.getRegistryObjects(LifeCycleManager.ORGANIZATION);
            if (br.getStatus() == JAXRResponse.STATUS_SUCCESS)
            {
            	System.out.println("BR.size : " + br.getCollection().size());
            	Collection coll = br.getCollection();
            	Iterator iterator = coll.iterator();
            	while (iterator.hasNext()) {
            		Organization org = (Organization) iterator.next();
            		System.out.println("BR " + org.getName().getValue(Locale.US));
            	}

            	if (br.getCollection().size() == 0) {
            		fail("Found no organizations for user 1");
            	}

            }
        	
        	BulkResponse br2 = bqm2.getRegistryObjects(LifeCycleManager.ORGANIZATION);
            if (br2.getStatus() == JAXRResponse.STATUS_SUCCESS)
            {
            	if (br2.getCollection().size() != 0) {
            		fail("There should be no found organizations for user 2, found "
            				+ br2.getCollection().size());
            	}
            	Collection coll = br2.getCollection();
            	Iterator iterator = coll.iterator();
            	while (iterator.hasNext()) {
            		Organization org = (Organization) iterator.next();
            		System.out.println("BR2 " + org.getName().getValue(Locale.US));
            	}
            }        	
        	
        	deleteTempOrg(org1.getKey());
        	deleteTempOrg(org2.getKey());
        
		} catch (JAXRException e) {
			e.printStackTrace();
		}
		
	}
}
