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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.ExternalLink;
import javax.xml.registry.infomodel.Key;

import org.apache.ws.scout.BaseTestCase;

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
public class JAXR02DeleteConceptTest extends BaseTestCase
{
    String queryString = "Apache Scout Concept -- APACHE SCOUT TEST";

    private BusinessLifeCycleManager blm = null;

    public void setUp()
    {
        super.setUp();
    }

    public void tearDown()
    {
        super.tearDown();
    }

    public void testDeleteConcept()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            BusinessQueryManager bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();

            Collection<String> findQualifiers = new ArrayList<String>();
            findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);
            Collection<String> namePatterns = new ArrayList<String>();
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

    private void deleteConcept(Concept c)
    throws JAXRException
    {
    	Key key = c.getKey();
    	String id = key.getId();

    	System.out.println("Deleting concept with id " + id);

    	Collection<Key> keys = new ArrayList<Key>();
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
