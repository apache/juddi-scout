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

import java.net.PasswordAuthentication;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.JAXRException;

import junit.framework.TestCase;

/**
 * Test to check Jaxr Publish
 * Open source UDDI Browser  <http://www.uddibrowser.org>
 * can be used to check your results
 * @author <mailto:kurt.stam@jboss.com>Kurt Stam
 * @since Sept 21, 2006
 */
public class BaseTestCase extends TestCase
{
    protected Connection connection = null;

    protected String userid = System.getProperty("uddi.test.uid") == null ? 
    						"jdoe" : 
    						System.getProperty("uddi.test.uid");

    protected String passwd = System.getProperty("uddi.test.pass") == null ? 
							"password" : 
							System.getProperty("uddi.test.pass");

    public void setUp()
    {
        System.out.println("************************************************************");
        try
        {
            Properties scoutProperties = new Properties();
            scoutProperties.load(getClass().getResourceAsStream("/scout.properties"));
            
            Properties juddiProperties = new Properties();
            juddiProperties.load(getClass().getResourceAsStream("/juddi.properties"));
            
            final String INQUERY_URI      = scoutProperties.getProperty("inquery.uri");
            final String PUBLISH_URI      = scoutProperties.getProperty("publish.uri");
            final String TRANSPORT_CLASS  = scoutProperties.getProperty("transport.class");
             
            if (scoutProperties.getProperty("userid")!=null) {
                userid = scoutProperties.getProperty("userid");
            }
            if (scoutProperties.getProperty("password")!=null) {
                passwd = scoutProperties.getProperty("password");
            }
            
            // Define connection configuration properties
            // To query, you need only the query URL
            Properties props = new Properties();
            
            props.setProperty("javax.xml.registry.queryManagerURL",
            				System.getProperty("javax.xml.registry.queryManagerURL") == null ? 
            				INQUERY_URI :
            				System.getProperty("javax.xml.registry.queryManagerURL"));
    
            props.setProperty("javax.xml.registry.lifeCycleManagerURL",
            				System.getProperty("javax.xml.registry.lifeCycleManagerURL") == null ? 
            				PUBLISH_URI :
            				System.getProperty("javax.xml.registry.lifeCycleManagerURL"));
    
            props.setProperty("javax.xml.registry.factoryClass",
                    "org.apache.ws.scout.registry.ConnectionFactoryImpl");
            
            
            props.setProperty("scout.proxy.transportClass", TRANSPORT_CLASS);
            //System.setProperty("scout.proxy.transportClass", TRANSPORT_CLASS);

       
            // Create the connection, passing it the configuration properties
            ConnectionFactory factory = ConnectionFactory.newInstance();
            factory.setProperties(props);
            connection = factory.createConnection();
        } catch (Exception e)
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
            
        } catch (Exception e)
        {

        }
    }
    
    /**
     * Does authentication with the uddi registry
     */
    public void login()
    {
        PasswordAuthentication passwdAuth = new PasswordAuthentication(userid,
                passwd.toCharArray());
        Set<PasswordAuthentication> creds = new HashSet<PasswordAuthentication>();
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