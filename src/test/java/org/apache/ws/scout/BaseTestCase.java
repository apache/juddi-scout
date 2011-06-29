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

import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.JAXRException;

import org.apache.ws.scout.registry.RegistryImpl;
/**
 * Test to check Jaxr Publish
 * Open source UDDI Browser  <http://www.uddibrowser.org>
 * can be used to check your results
 * @author <mailto:kurt.stam@jboss.com>Kurt Stam
 * @since Sept 21, 2006
 */
public class BaseTestCase
{	
    protected Connection connection;
    protected Connection connection2;
    
    protected BusinessLifeCycleManager blm;
    protected BusinessQueryManager bqm;

    //Set some default values
	protected String uddiversion = RegistryImpl.DEFAULT_UDDI_VERSION;
    protected String uddinamespace = RegistryImpl.DEFAULT_UDDI_NAMESPACE;
    protected String userid = System.getProperty("uddi.test.uid")  == null ? "jdoe"     : System.getProperty("uddi.test.uid");
    protected String passwd = System.getProperty("uddi.test.pass") == null ? "password" : System.getProperty("uddi.test.pass");
    
    protected String userid2 = System.getProperty("uddi.test.uid2")  == null ? "jdoe2"     : System.getProperty("uddi.test.uid2");
    protected String passwd2 = System.getProperty("uddi.test.pass2") == null ? "password2" : System.getProperty("uddi.test.pass2");
    
    protected int maxRows   = 100;

    /**
     * Reads scout properties, and creates a connection using these properties.
     *
     */
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
            final String SECURITY_URI    = scoutProperties.getProperty("security.uri");
            final String TRANSPORT_CLASS  = scoutProperties.getProperty("transport.class");
             
            if (scoutProperties.getProperty("userid")!=null) {
                userid = scoutProperties.getProperty("userid");
            }
            if (scoutProperties.getProperty("password")!=null) {
                passwd = scoutProperties.getProperty("password");
            }
            
            if (scoutProperties.getProperty("userid2")!=null) {
                userid = scoutProperties.getProperty("userid2");
            }
            if (scoutProperties.getProperty("password2")!=null) {
                passwd = scoutProperties.getProperty("password2");
            }
            
            if (scoutProperties.getProperty("scout.proxy.uddiVersion") != null)
            {
            	uddiversion = scoutProperties.getProperty("scout.proxy.uddiVersion","2.0");
            }
            
            if (scoutProperties.getProperty("scout.proxy.uddiNamespace") != null) {
            	uddinamespace = scoutProperties.getProperty("scout.proxy.uddiNamespace");
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
            if ("3.0".equals(uddiversion)) {
            	props.setProperty("javax.xml.registry.securityManagerURL",
    				System.getProperty("javax.xml.registry.securityManagerURL") == null ? 
    				SECURITY_URI :
    				System.getProperty("javax.xml.registry.securityManagerURL"));
            }
            props.setProperty("javax.xml.registry.factoryFactoryClass",
                    "org.apache.ws.scout.? it isregistry.ConnectionFactoryImpl");
            props.setProperty("scout.proxy.transportClass", TRANSPORT_CLASS);
            props.setProperty("javax.xml.registry.uddi.maxRows", String.valueOf(maxRows));
       
            props.setProperty("scout.proxy.uddiVersion", uddiversion);
            props.setProperty("scout.proxy.uddiNamespace", uddinamespace);

            // Create the connection, passing it the configuration properties
            ConnectionFactory factory = ConnectionFactory.newInstance();
            factory.setProperties(props);
            connection = factory.createConnection();
            connection2 = factory.createConnection();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Closes down the connection to the registry.
     *
     */
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
    
    /**
     * Does authentication with the uddi registry
     */
    public void loginSecondUser()
    {
        PasswordAuthentication passwdAuth = new PasswordAuthentication(userid2,
                passwd2.toCharArray());
        Set<PasswordAuthentication> creds = new HashSet<PasswordAuthentication>();
        creds.add(passwdAuth);

        try
        {
            connection2.setCredentials(creds);
        } catch (JAXRException e)
        {
            e.printStackTrace();
        }
    }
}
