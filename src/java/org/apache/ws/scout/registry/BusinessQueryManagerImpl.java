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
package org.apache.ws.scout.registry;

import org.apache.juddi.IRegistry;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.datatype.response.BusinessInfo;
import org.apache.juddi.datatype.response.BusinessList;
import org.apache.juddi.error.RegistryException;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;
import javax.xml.registry.UnsupportedCapabilityException;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.RegistryObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * @version $Revision$ $Date$
 */
class BusinessQueryManagerImpl implements BusinessQueryManager {
    private final RegistryServiceImpl registryService;

    public BusinessQueryManagerImpl(RegistryServiceImpl registry) {
        this.registryService = registry;
    }

    public RegistryService getRegistryService() {
        return registryService;
    }

    public BulkResponse findOrganizations(Collection findQualifiers, Collection namePatterns, Collection classifications, Collection specifications, Collection externalIdentifiers, Collection externalLinks) throws JAXRException {
        IRegistry registry = registryService.getRegistry();
        try {
            FindQualifiers juddiFindQualifiers = mapFindQualifiers(findQualifiers);
            Vector nameVector = mapNamePatterns(namePatterns);
            BusinessList result = registry.findBusiness(nameVector, null, null, null, null, juddiFindQualifiers, registryService.getMaxRows());

            Vector v = result.getBusinessInfos().getBusinessInfoVector();
            Collection orgs = new ArrayList(v.size());
            for (int i = 0; i < v.size(); i++) {
                BusinessInfo info = (BusinessInfo) v.elementAt(i);
                orgs.add(registryService.getLifeCycleManagerImpl().createOrganization(info));
            }
            return new BulkResponseImpl(orgs);
        } catch (RegistryException e) {
            throw new JAXRException(e);
        }
    }

    public BulkResponse findAssociations(Collection findQualifiers, String sourceObjectId, String targetObjectId, Collection associationTypes) throws JAXRException {
        return null;
    }

    public BulkResponse findCallerAssociations(Collection findQualifiers, Boolean confirmedByCaller, Boolean confirmedByOtherParty, Collection associationTypes) throws JAXRException {
        return null;
    }

    public ClassificationScheme findClassificationSchemeByName(Collection findQualifiers, String namePatters) throws JAXRException {
        return null;
    }

    public BulkResponse findClassificationSchemes(Collection findQualifiers, Collection namePatterns, Collection classifications, Collection externalLinks) throws JAXRException {
        return null;
    }

    public Concept findConceptByPath(String path) throws JAXRException {
        return null;
    }

    public BulkResponse findConcepts(Collection findQualifiers, Collection namePatterns, Collection classifications, Collection externalIdentifiers, Collection externalLinks) throws JAXRException {
        return null;
    }

    public BulkResponse findRegistryPackages(Collection findQualifiers, Collection namePatterns, Collection classifications, Collection externalLinks) throws JAXRException {
        return null;
    }

    public BulkResponse findServiceBindings(Key serviceKey, Collection findQualifiers, Collection classifications, Collection specificationa) throws JAXRException {
        return null;
    }

    public BulkResponse findServices(Key orgKey, Collection findQualifiers, Collection namePattersn, Collection classifications, Collection specificationa) throws JAXRException {
        return null;
    }

    public RegistryObject getRegistryObject(String id) throws JAXRException {
        return null;
    }

    public RegistryObject getRegistryObject(String id, String objectType) throws JAXRException {
        return null;
    }

    public BulkResponse getRegistryObjects() throws JAXRException {
        return null;
    }

    public BulkResponse getRegistryObjects(Collection objectKeys) throws JAXRException {
        return null;
    }

    public BulkResponse getRegistryObjects(Collection objectKeys, String objectTypes) throws JAXRException {
        return null;
    }

    public BulkResponse getRegistryObjects(String objectTypes) throws JAXRException {
        return null;
    }

    private static final Map findQualifierMapping;
    static {
        findQualifierMapping = new HashMap();
        findQualifierMapping.put(FindQualifier.AND_ALL_KEYS, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.AND_ALL_KEYS));
        findQualifierMapping.put(FindQualifier.CASE_SENSITIVE_MATCH, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.CASE_SENSITIVE_MATCH));
        findQualifierMapping.put(FindQualifier.COMBINE_CLASSIFICATIONS, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.COMBINE_CATEGORY_BAGS));
        findQualifierMapping.put(FindQualifier.EXACT_NAME_MATCH, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.EXACT_NAME_MATCH));
        findQualifierMapping.put(FindQualifier.OR_ALL_KEYS, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.OR_ALL_KEYS));
        findQualifierMapping.put(FindQualifier.OR_LIKE_KEYS, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.OR_LIKE_KEYS));
        findQualifierMapping.put(FindQualifier.SERVICE_SUBSET, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.SERVICE_SUBSET));
        findQualifierMapping.put(FindQualifier.SORT_BY_DATE_ASC, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.SORT_BY_DATE_ASC));
        findQualifierMapping.put(FindQualifier.SORT_BY_DATE_DESC, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.SORT_BY_DATE_DESC));
        findQualifierMapping.put(FindQualifier.SORT_BY_NAME_ASC, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.SORT_BY_NAME_ASC));
        findQualifierMapping.put(FindQualifier.SORT_BY_NAME_DESC,new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.SORT_BY_NAME_DESC));
//        findQualifierMapping.put(FindQualifier.SOUNDEX, null);
    }

    static FindQualifiers mapFindQualifiers(Collection jaxrQualifiers) throws UnsupportedCapabilityException {
        if (jaxrQualifiers == null) {
            return null;
        }

        FindQualifiers result = new FindQualifiers(jaxrQualifiers.size());
        for (Iterator i = jaxrQualifiers.iterator(); i.hasNext();) {
            String jaxrQualifier = (String) i.next();
            org.apache.juddi.datatype.request.FindQualifier juddiQualifier =
                    (org.apache.juddi.datatype.request.FindQualifier) findQualifierMapping.get(jaxrQualifier);
            if (juddiQualifier == null) {
                throw new UnsupportedCapabilityException("jUDDI does not support FindQualifer: " + jaxrQualifier);
            }
            result.addFindQualifier(juddiQualifier);
        }
        return result;
    }

    static Vector mapNamePatterns(Collection namePatterns) {
        return namePatterns == null ? null :  new Vector(namePatterns);
    }
}
