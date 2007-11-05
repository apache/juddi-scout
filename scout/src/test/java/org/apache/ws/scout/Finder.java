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
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;

/**
 * Find RegistryObjects
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt Stam</a>
 * 
 */
public class Finder
{
    private BusinessQueryManager bqm;
    
    public Finder(BusinessQueryManager bqm) {
        super();
        this.bqm = bqm;
    }

    public Collection findOrganizationsByName(String queryStr) throws JAXRException {
    	// Define find qualifiers and name patterns
    	Collection<String> findQualifiers = new ArrayList<String>();
    	findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);
    	Collection<String> namePatterns = new ArrayList<String>();
    	namePatterns.add("%" + queryStr + "%");
    	
    	// Find based upon qualifier type and values
    	System.out.println("\n-- searching the registry --\n");
        BulkResponse response =
    		bqm.findOrganizations(findQualifiers,
    				namePatterns,
    				null,
    				null,
    				null,
    				null);
    	
    	return response.getCollection();
    }
    
    public Collection findClassificationSchemesByName(String queryStr) throws JAXRException {
        // Define find qualifiers and name patterns
        Collection<String> findQualifiers = new ArrayList<String>();
        findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);
        Collection<String> namePatterns = new ArrayList<String>();
        namePatterns.add("%" + queryStr + "%");
        
        // Find based upon qualifier type and values
        System.out.println("\n-- searching the registry --\n");
        BulkResponse response =
            bqm.findClassificationSchemes(findQualifiers,
                    namePatterns,
                    null,
                    null);
        
        return response.getCollection();
    }
    /**
     * Search a service, by matching the name and the classification.
     * 
     * @param category - name of the category classification
     * @param serviceName - name of the service
     * @param blm
     * @return JAXR Service
     * @throws JAXRException
     */
    public Service findService(String category, String serviceName, BusinessLifeCycleManager blm) throws JAXRException
    {
        if (category==null) {
            category="";
        }
        if (serviceName==null) {
            serviceName="";
        }  
        // Define find qualifiers and name patterns
        Collection<String> findQualifiers = new ArrayList<String>();
        findQualifiers.add(FindQualifier.AND_ALL_KEYS);
        findQualifiers.add(FindQualifier.EXACT_NAME_MATCH);
        findQualifiers.add(FindQualifier.SORT_BY_NAME_DESC);
        ClassificationScheme cScheme = bqm.findClassificationSchemeByName(findQualifiers, "org.jboss.soa.esb.:category");
        Collection<Classification> classifications = new ArrayList<Classification>();
        Classification classification = 
            blm.createClassification( 
              cScheme, 
              "category", category );
        classifications.add(classification);
        Collection<String> namePatterns = new ArrayList<String>();
        namePatterns.add(serviceName);
        //Find based upon qualifier type and values
        BulkResponse response = bqm.findServices(null, findQualifiers,
                namePatterns, classifications, null);
        if (response.getStatus()==JAXRResponse.STATUS_SUCCESS) {
            for (Iterator servIter = response.getCollection().iterator(); servIter.hasNext();) 
            {
                Service service = (Service) servIter.next();
                return service;
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public Collection<ServiceBinding> findServiceBindings(Key serviceKey) throws JAXRException
    {
        Collection<ServiceBinding> serviceBindings=null;
        Collection<String> findQualifiers = new ArrayList<String>();
        findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);
        BulkResponse bulkResponse = bqm.findServiceBindings(serviceKey,findQualifiers,null,null);
        if (bulkResponse.getStatus()==JAXRResponse.STATUS_SUCCESS){
            serviceBindings = (Collection<ServiceBinding>) bulkResponse.getCollection();
        }
        return serviceBindings;
    }

}
