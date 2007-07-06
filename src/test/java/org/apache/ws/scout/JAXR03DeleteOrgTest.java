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
package org.apache.ws.scout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;

/**
 * Testcase for Delete organization.
 * 
 * Based on query/publish tests written by 
 * <a href="mailto:anil@apache.org">Anil Saldhana</a>.
 *
 * @author <a href="mailto:dbhole@redhat.com">Deepak Bhole</a>
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * 
 * @since Sep 27, 2005
 */
public class JAXR03DeleteOrgTest extends BaseTestCase
{
    
    String queryString = "USA -- APACHE SCOUT TEST";

    BusinessQueryManager bqm = null;
    BusinessLifeCycleManager blm = null;
    
    @Override
    public void setUp() {
    	// TODO Auto-generated method stub
    	super.setUp();
    }
    
    @Override
    public void tearDown() {
    	// TODO Auto-generated method stub
    	super.tearDown();
    }

	public void testDelete() throws Exception
    {
		login();
        try
        {
            // Get registry service and business query manager
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();
            System.out.println("We have the Business Query Manager");

            Collection orgs = findOrganizationsByName(queryString);

            if (orgs == null)
            {
                System.out.println("\n-- Matched 0 orgs");

            } else
            {
            	for (Iterator orgIter = orgs.iterator(); orgIter.hasNext();)
            	{
            		Organization org = (Organization) orgIter.next();
            		deleteOrgnanization(org);
            	}
            }//end else
        } catch (JAXRException e)
        {
            e.printStackTrace();
			fail(e.getMessage());
        }

    }

    private Collection findOrganizationsByName(String queryStr) throws JAXRException {
    	// Define find qualifiers and name patterns
    	Collection<String> findQualifiers = new ArrayList<String>();
    	findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);
    	Collection<String> namePatterns = new ArrayList<String>();
    	namePatterns.add("%" + queryStr + "%");
    	
    	BulkResponse response;
    	
    	// Find based upon qualifier type and values
    	System.out.println("\n-- searching the registry --\n");
    	response =
    		bqm.findOrganizations(findQualifiers,
    				namePatterns,
    				null,
    				null,
    				null,
    				null);
    	
    	return response.getCollection();
    }
    
    private void deleteOrgnanization(Organization org) throws JAXRException {

    	Key key = org.getKey();
    	
    	String id = key.getId();
    	System.out.println("Deleting organization with id " + id);
    	Collection<Key> keys = new ArrayList<Key>();
    	keys.add(key);
    	BulkResponse response = blm.deleteOrganizations(keys);
    	Collection exceptions = response.getExceptions();
    	if (exceptions == null) {
    		System.out.println("Organization deleted");
    		Collection retKeys = response.getCollection();
    		Iterator keyIter = retKeys.iterator();
    		javax.xml.registry.infomodel.Key orgKey = null;
    		if (keyIter.hasNext()) {
    			orgKey = 
    				(javax.xml.registry.infomodel.Key) keyIter.next();
    			id = orgKey.getId();
    			System.out.println("Organization key was " + id);
    		}
    	}
    }

}
