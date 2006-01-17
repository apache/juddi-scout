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
package org.apache.ws.scout.registry.publish;

import junit.framework.TestCase;

import javax.xml.registry.*;
import javax.xml.registry.infomodel.*;

import java.util.*;
import java.net.PasswordAuthentication;

/**
 * Tests delete (and indirectly, find) concepts.
 * 
 * Open source UDDI Browser  <http://www.uddibrowser.org>
 * to check your results.
 *
 * Based on query/publish tests written by 
 * <a href="mailto:anil@apache.org">Anil Saldhana</a>.
 *
 * @author <a href="mailto:dbhole@redhat.com">Deepak Bhole</a>
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * 
 * @since Sep 27, 2005
 */
public class JAXR02DeleteConceptTest extends TestCase
{
    private Connection connection = null;
    
    String queryString = "Apache Scout Concept -- APACHE SCOUT TEST";

    private String userid = System.getProperty("uddi.test.uid") == null ? 
    						"juddi" : 
    						System.getProperty("uddi.test.uid");

    private String passwd = System.getProperty("uddi.test.pass") == null ? 
							"password" : 
							System.getProperty("uddi.test.pass");

    private BusinessLifeCycleManager blm = null;

    public void setUp()
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

        try
        {
            // Create the connection, passing it the configuration properties
            ConnectionFactory factory = ConnectionFactory.newInstance();
            factory.setProperties(props);
            connection = factory.createConnection();
        } catch (JAXRException e)
        {
            e.printStackTrace();
        }
    }

    public void tearDown()
    {
        try
        {
            if (connection != null)
                connection.close();
        } catch (JAXRException e)
        {

        }
    }

    public void testDeleteConcept()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            BusinessQueryManager bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();

            Collection findQualifiers = new ArrayList();
            findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);
            Collection namePatterns = new ArrayList();
            namePatterns.add("%" + queryString + "%");

            BulkResponse br = bqm.findConcepts(findQualifiers, namePatterns, null, null, null);

//          check how many organisation we have matched
            Collection concepts = br.getCollection();

            if (concepts == null)
            {
                System.out.println("\n-- Matched 0 orgs");

            } else
            {
                System.out.println("\n-- Matched " + concepts.size() + " concepts --\n");

                // then step through them
                for (Iterator conceptIter = concepts.iterator(); conceptIter.hasNext();)
                {
                    Concept c = (Concept) conceptIter.next();
                    
                    System.out.println("Id: " + c.getKey().getId());
                    System.out.println("Name: " + c.getName().getValue());

                    // Links are not yet implemented in scout -- so concepts 
                    // created via scout won't have links 
                    printExternalLinks(c);

                    // Print spacer between messages
                    System.out.println(" --- ");
                    
                    deleteConcept(c);
                    
                    System.out.println(" === ");
                }
            }//end else
        } catch (JAXRException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
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

    private void deleteConcept(Concept c)
    throws JAXRException
    {
    	Key key = c.getKey();
    	String id = key.getId();

    	System.out.println("Deleting concept with id " + id);

    	Collection keys = new ArrayList();
    	keys.add(key);
    	BulkResponse response = blm.deleteConcepts(keys);
    	
    	Collection exceptions = response.getExceptions();
    	if (exceptions == null) {
    		Collection retKeys = response.getCollection();
    		Iterator keyIter = retKeys.iterator();
    		javax.xml.registry.infomodel.Key orgKey = null;
    		if (keyIter.hasNext()) {
    			orgKey = 
    				(javax.xml.registry.infomodel.Key) keyIter.next();
    			id = orgKey.getId();
    			System.out.println("Concept with ID=" + id + " was deleted");
    		}
    	}
    }

    private void printExternalLinks(Concept c)
    throws JAXRException
    {
    	Collection links = c.getExternalLinks();
    	for (Iterator lnkIter = links.iterator(); lnkIter.hasNext();)
    	{
    		 System.out.println("Link: " + ((ExternalLink) lnkIter.next()).getExternalURI().charAt(0));
    	}
    }
}
