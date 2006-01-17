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
 * Tests publish classification schemes.
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
public class JAXR01PublishClassificationSchemeTest extends TestCase
{
    private Connection connection = null;

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

    public void testPublishClassificationScheme()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            BusinessQueryManager bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();

            ClassificationScheme cScheme = blm.createClassificationScheme("testScheme -- APACHE SCOUT TEST", "Sample Classification Scheme");
            Classification classification = createClassificationForUDDI(bqm);
            cScheme.addClassification(classification);

            ArrayList cSchemes = new ArrayList();
            cSchemes.add(cScheme);

            BulkResponse br = blm.saveClassificationSchemes(cSchemes);
            if (br.getStatus() == JAXRResponse.STATUS_SUCCESS)
            {
                System.out.println("Classification Saved");
                Collection coll = br.getCollection();
                Iterator iter = coll.iterator();
                while (iter.hasNext())
                {
                    Key key = (Key) iter.next();
                    System.out.println("Saved Key=" + key.getId());
                }//end while
            } else
            {
                System.err.println("JAXRExceptions " +
                        "occurred during save:");
                Collection exceptions = br.getExceptions();
                Iterator iter = exceptions.iterator();
                while (iter.hasNext())
                {
                    Exception e = (Exception) iter.next();
                    System.err.println(e.toString());
                    fail(e.toString());
                }
            }
            
            //Classificat
        } catch (JAXRException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private Classification createClassificationForUDDI(BusinessQueryManager bqm)
            throws JAXRException
    {
        //Scheme which maps onto uddi tmodel
        ClassificationScheme udditmodel = bqm.findClassificationSchemeByName(null, "uddi-org:types");

        Classification cl = blm.createClassification(udditmodel, "wsdl", "wsdl");
        return cl;
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
}
