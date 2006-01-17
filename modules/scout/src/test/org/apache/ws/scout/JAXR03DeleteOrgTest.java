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

import junit.framework.TestCase;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

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
public class JAXR03DeleteOrgTest extends TestCase
{
    Connection connection = null;
    
    String queryString = "USA -- APACHE SCOUT TEST";

    BusinessQueryManager bqm = null;
    BusinessLifeCycleManager blm = null;

    private String userid = System.getProperty("uddi.test.uid") == null ? 
							"juddi" : 
							System.getProperty("uddi.test.uid");

    private String passwd = System.getProperty("uddi.test.pass") == null ? 
							"password" : 
							System.getProperty("uddi.test.pass");

	public void testDelete() throws Exception
    {

        // Define connection configuration properties
        // To query, you need only the query URL
        Properties props = new Properties();

        props.setProperty("javax.xml.registry.queryManagerURL",
        				System.getProperty("javax.xml.registry.queryManagerURL") == null ? 
        				"http://localhost:8080/juddi/inquiry" : 
        				System.getProperty("javax.xml.registry.queryManagerURL"));

        props.setProperty("javax.xml.registry.lifeCycleManagerURL",
						System.getProperty("javax.xml.registry.lifeCycleManagerURL") == null ? 
						"http://localhost:8080/juddi/publish" :
						System.getProperty("javax.xml.registry.lifeCycleManagerURL"));

        props.setProperty("javax.xml.registry.factoryClass",
                "org.apache.ws.scout.registry.ConnectionFactoryImpl");
        props.setProperty("javax.xml.registry.uddi.maxRows",
        		"2");

        try
        {
            // Create the connection, passing it the configuration properties
            ConnectionFactory factory = ConnectionFactory.newInstance();
            factory.setProperties(props);
            connection = factory.createConnection();
            login();
        } catch (JAXRException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

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
        } finally
        {
            connection.close();
        }

    }
    
    /**
     * Does authentication with the uddi registry
     */
    private void login()
    {
        PasswordAuthentication passwdAuth = new PasswordAuthentication(userid,
                passwd.toCharArray());
        Set creds = new HashSet();
        creds.add(passwdAuth);

        try
        {
            connection.setCredentials(creds);
        } catch (JAXRException e)
        {
            e.printStackTrace();
        }
    }

    private Collection findOrganizationsByName(String queryStr) throws JAXRException {
    	// Define find qualifiers and name patterns
    	Collection findQualifiers = new ArrayList();
    	findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);
    	Collection namePatterns = new ArrayList();
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
    	Collection keys = new ArrayList();
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
