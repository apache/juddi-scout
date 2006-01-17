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
Tests Publish, Delete (and indirectly, find) for service bindings.
 * 
 * You can comment out the deletion portion and use 
 * Open source UDDI Browser  <http://www.uddibrowser.org>
 * to check your intermediate results
 *
 * Based on query/publish tests written by 
 * <a href="mailto:anil@apache.org">Anil Saldhana</a>.
 *
 * @author <a href="mailto:dbhole@redhat.com">Deepak Bhole</a>
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 *
 * @since Sep 27, 2005
 */
public class JAXRPublishAndDeleteServiceBindingTest extends TestCase
{
    private Connection connection = null;

    private String userid = System.getProperty("uddi.test.uid") == null ? 
    						"juddi" : 
    						System.getProperty("uddi.test.uid");

    private String passwd = System.getProperty("uddi.test.pass") == null ? 
							"password" : 
							System.getProperty("uddi.test.pass");

	private BusinessLifeCycleManager blm = null;
    
    String serviceBindingName = "Apache JAXR Service Binding -- APACHE SCOUT TEST";
    String serviceName = "Apache JAXR Service -- APACHE SCOUT TEST";
	String tempOrgName = "Apache JAXR Service Org -- APACHE SCOUT TEST";
	
	Service tmpSvc = null;
	Organization tmpOrg = null;
	

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

            deleteTempService();
            deleteTempOrg();

            if (connection != null)
                connection.close();
        } catch (JAXRException e)
        {

        }
    }

	/**
	 * Tests publishing and deleting of service bindings.
	 * 
	 * Do not break this into testPublish(), testDelete(), etc. Order is
	 * important, and not all jvms can guarantee order since the JUnit framework
	 * uses getMethods() to gather test methods, and getMethods() does not
	 * guarantee order.
	 */

    public void testPublishFindAndDeleteServiceBinding()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();

            System.out.println("\nCreating temporary organization...\n");
            tmpOrg = createTempOrg();
            
            System.out.println("\nCreating service...\n");
            tmpSvc = createTempService();
            
            System.out.println("\nCreating service binding...\n");
            Key sbKey = createServiceBinding();
            
            // All created ... now try to delete.
            
            // No find service binding.. search by name is not currently supported. 
            
            deleteServiceBinding(sbKey);
            
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

    private InternationalString getIString(String str)
            throws JAXRException
    {
        return blm.createInternationalString(str);
    }

    private Key createServiceBinding() throws JAXRException {
    	Key key = null;
        ServiceBinding serviceBinding = blm.createServiceBinding();
        serviceBinding.setName(getIString(serviceBindingName));
        serviceBinding.setDescription(getIString("UDDI service binding"));
        tmpSvc.addServiceBinding(serviceBinding);

        ArrayList serviceBindings = new ArrayList();
        serviceBindings.add(serviceBinding);

        BulkResponse br = blm.saveServiceBindings(serviceBindings);
        if (br.getStatus() == JAXRResponse.STATUS_SUCCESS)
        {
            System.out.println("Service Binding Saved");
            key = (Key) br.getCollection().iterator().next();
            System.out.println("Saved Key=" + key.getId());
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
        
        return key;
    }

    private void deleteServiceBinding(Key key) throws JAXRException {

    	String id = key.getId();

    	System.out.println("\nDeleting service binding with id " + id + "\n");

    	Collection keys = new ArrayList();
    	keys.add(key);
    	BulkResponse response = blm.deleteServiceBindings(keys);

    	Collection exceptions = response.getExceptions();
    	if (exceptions == null) {
    		Collection retKeys = response.getCollection();
    		Iterator keyIter = retKeys.iterator();
    		javax.xml.registry.infomodel.Key orgKey = null;
    		if (keyIter.hasNext()) {
    			orgKey = 
    				(javax.xml.registry.infomodel.Key) keyIter.next();
    			id = orgKey.getId();
    			System.out.println("Service binding with ID=" + id + " was deleted");
    		}
    	}
    }

    private Service createTempService() throws JAXRException {
    	
        Service service = blm.createService(getIString(serviceName));
        service.setDescription(getIString("Services in UDDI Registry"));
        service.setProvidingOrganization(tmpOrg);

        ArrayList services = new ArrayList();
        services.add(service);

        BulkResponse br = blm.saveServices(services);
        if (br.getStatus() == JAXRResponse.STATUS_SUCCESS)
        {
            System.out.println("Service Saved");
            Key key = (Key) br.getCollection().iterator().next();
            System.out.println("Saved Key=" + key.getId());
            service.setKey(key);
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
            }
        }
        
        return service;
    }

    private void deleteTempService() throws JAXRException {

    	if (tmpSvc == null) {
    		return;
    	}

    	String id = tmpSvc.getKey().getId();

    	System.out.println("\nDeleting service with id " + id + "\n");

    	Collection keys = new ArrayList();
    	keys.add(tmpSvc.getKey());
    	BulkResponse response = blm.deleteServices(keys);

    	Collection exceptions = response.getExceptions();
    	if (exceptions == null) {
    		Collection retKeys = response.getCollection();
    		Iterator keyIter = retKeys.iterator();
    		javax.xml.registry.infomodel.Key orgKey = null;
    		if (keyIter.hasNext()) {
    			orgKey = 
    				(javax.xml.registry.infomodel.Key) keyIter.next();
    			id = orgKey.getId();
    			System.out.println("Service with ID=" + id + " was deleted");
    		}
    	}
    }
    
    private Organization createTempOrg() throws JAXRException {

        Key orgKey = null;
        Organization org = blm.createOrganization(getIString(tempOrgName));
        org.setDescription(getIString("Temporary organization to test saveService()"));

        Collection orgs = new ArrayList();
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
    
    private void deleteTempOrg() throws JAXRException {

    	if (tmpOrg == null) {
    		return;
    	}
    	
    	String id = tmpOrg.getKey().getId();

    	System.out.println("\nDeleting temporary organization with id " + id + "\n");

    	Collection keys = new ArrayList();
    	keys.add(tmpOrg.getKey());
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
}
