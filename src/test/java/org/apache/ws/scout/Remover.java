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
import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;

/**
 * Remove Registry Objects
 * 
 */
public class Remover extends BaseTestCase
{
    BusinessLifeCycleManager blm = null;
    
    public Remover(BusinessLifeCycleManager blm) {
        super();
        this.blm = blm;
    }
    
    public void removeOrganization(Organization org) throws JAXRException {

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
    
    public void removeClassificationScheme(ClassificationScheme cs)
    throws JAXRException
    {
        Key key = cs.getKey();
        String id = key.getId();

        System.out.println("Deleting concept with id " + id);

        Collection<Key> keys = new ArrayList<Key>();
        keys.add(key);
        BulkResponse response = blm.deleteClassificationSchemes(keys);
        
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
    
    public void removeConcept(Concept concept)
    throws JAXRException
    {
        Key key = concept.getKey();
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
    
    public void removeService(Service service)
    throws JAXRException
    {
        Key key = service.getKey();
        String id = key.getId();

        System.out.println("Deleting service with id " + id);

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
    
    public void removeServiceBinding(ServiceBinding serviceBinding)
    throws JAXRException
    {
        Key key = serviceBinding.getKey();
        String id = key.getId();

        System.out.println("Deleting serviceBinding with id " + id);

        Collection<Key> keys = new ArrayList<Key>();
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
                System.out.println("ServiceBinding with ID=" + id + " was deleted");
            }
        }
    }

 

}
