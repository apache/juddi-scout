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

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Key;

import org.apache.ws.scout.BaseTestCase;

import junit.framework.TestCase;

/**
 * Tests delete (and indirectly, find) classification schemes.
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
public class JAXR02DeleteClassificationSchemeTest extends BaseTestCase
{
    String queryString = "testScheme -- APACHE SCOUT TEST";

    private BusinessLifeCycleManager blm = null;

    public void setUp()
    {
        super.setUp();
    }

    public void tearDown()
    {
        super.tearDown();
    }

    public void testDeleteClassificationScheme()
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

            BulkResponse br = bqm.findClassificationSchemes(findQualifiers, namePatterns, null, null);

//          check how many organisation we have matched
            Collection classificationSchemes = br.getCollection();

            if (classificationSchemes == null)
            {
                System.out.println("\n-- Matched 0 orgs");

            } else
            {
                System.out.println("\n-- Matched " + classificationSchemes.size() + " concepts --\n");

                // then step through them
                for (Iterator conceptIter = classificationSchemes.iterator(); conceptIter.hasNext();)
                {
                	ClassificationScheme cs = (ClassificationScheme) conceptIter.next();
                    
                    System.out.println("Id: " + cs.getKey().getId());
                    System.out.println("Name: " + cs.getName().getValue());

                    // Print spacer between messages
                    System.out.println(" --- ");
                    
                    deleteClassificationScheme(cs);

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

    private void deleteClassificationScheme(ClassificationScheme cs)
    throws JAXRException
    {
    	Key key = cs.getKey();
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
}
