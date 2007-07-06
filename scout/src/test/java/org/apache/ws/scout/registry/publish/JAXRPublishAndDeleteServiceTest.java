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
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;

import org.apache.ws.scout.BaseTestCase;

/**
 * Tests Publish, Delete (and indirectly, find) for service bindings.
 * 
 * You can comment out the deletion portion and use 
 * Open source UDDI Browser  <http://www.uddibrowser.org>
 * to check your intermediate results.
 *
 * Based on query/publish tests written by 
 * <a href="mailto:anil@apache.org">Anil Saldhana</a>.
 *
 * @author <a href="mailto:dbhole@redhat.com">Deepak Bhole</a>
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 *
 * @since Sep 27, 2005
 */
public class JAXRPublishAndDeleteServiceTest extends BaseTestCase
{

	private BusinessLifeCycleManager blm = null;
    private BusinessQueryManager bqm = null;
    
    String serviceName = "Apache JAXR Service -- APACHE SCOUT TEST";
	String tempOrgName = "Apache JAXR Service Org -- APACHE SCOUT TEST";

    public void setUp()
    {
    	super.setUp();
    }

    public void tearDown()
    {
        super.tearDown();
    }

	/**
	 * Tests publishing and deleting of services.
	 * 
	 * Do not break this into testPublish(), testDelete(), etc. Order is
	 * important, and not all jvms can guarantee order since the JUnit framework
	 * uses getMethods() to gather test methods, and getMethods() does not
	 * guarantee order.
	 */
    public void testPublishFindAndDeleteService()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();

            System.out.println("\nCreating temporary organization...\n");
            Organization org = createTempOrg();
            
            System.out.println("\nCreating service...\n");
            createService(org);
            
            // All created ... now try to delete.
            
            findAndDeleteService(org.getKey());
            deleteTempOrg(org.getKey());
            
        } catch (JAXRException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private InternationalString getIString(String str)
            throws JAXRException
    {
        return blm.createInternationalString(str);
    }
    
    private void createService(Organization org) throws JAXRException {
        Service service = blm.createService(getIString(serviceName));
        service.setDescription(getIString("Services in UDDI Registry"));
        service.setProvidingOrganization(org);

        ArrayList<Service> services = new ArrayList<Service>();
        services.add(service);

        BulkResponse br = blm.saveServices(services);
        if (br.getStatus() == JAXRResponse.STATUS_SUCCESS)
        {
            System.out.println("Service Saved");
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
    }
    
    private void findAndDeleteService(Key orgKey) throws JAXRException {
    	Collection<String> findQualifiers = new ArrayList<String>();
        findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);
        Collection<String> namePatterns = new ArrayList<String>();
        namePatterns.add("%" + serviceName + "%");
        
        BulkResponse br = bqm.findServices(orgKey, findQualifiers, namePatterns, null, null);
        Collection services = br.getCollection();

        if (services == null)
        {
            System.out.println("\n-- Matched 0 orgs");

        } else
        {
            System.out.println("\n-- Matched " + services.size() + " services --\n");

            // then step through them
            for (Iterator conceptIter = services.iterator(); conceptIter.hasNext();)
            {
            	Service s = (Service) conceptIter.next();
                
                System.out.println("Id: " + s.getKey().getId());
                System.out.println("Name: " + s.getName().getValue());

                // Print spacer between messages
                System.out.println(" --- ");
                
                deleteService(s.getKey());

                System.out.println("\n ============================== \n");
            }
        }
    }
    
    private void deleteService(Key key) throws JAXRException {

    	String id = key.getId();

    	System.out.println("\nDeleting service with id " + id + "\n");

    	Collection<Key> keys = new ArrayList<Key>();
    	keys.add(key);
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
    
    private void deleteTempOrg(Key orgKey) throws JAXRException {

    	String id = orgKey.getId();

    	System.out.println("\nDeleting temporary organization with id " + id + "\n");

    	Collection<Key> keys = new ArrayList<Key>();
    	keys.add(orgKey);
    	BulkResponse response = blm.deleteOrganizations(keys);

    	Collection exceptions = response.getExceptions();
    	if (exceptions == null) {
    		Collection retKeys = response.getCollection();
    		Iterator keyIter = retKeys.iterator();
    		orgKey = null;
    		if (keyIter.hasNext()) {
    			orgKey = 
    				(javax.xml.registry.infomodel.Key) keyIter.next();
    			id = orgKey.getId();
    			System.out.println("Organization with ID=" + id + " was deleted");
    		}
    	}
    }
}
