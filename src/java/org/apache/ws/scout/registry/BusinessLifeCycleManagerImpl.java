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

import java.util.Collection;
import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Association;

/**
 * @version $Revision$ $Date$
 */
public class BusinessLifeCycleManagerImpl extends LifeCycleManagerImpl implements BusinessLifeCycleManager {
    public BusinessLifeCycleManagerImpl(RegistryService registry) {
        super(registry);
    }

    public BulkResponse deleteAssociations(Collection associationKeys) throws JAXRException {
        return null;
    }

    public BulkResponse deleteClassificationSchemes(Collection schemeKeys) throws JAXRException {
        return null;
    }

    public BulkResponse deleteConcepts(Collection conceptKeys) throws JAXRException {
        return null;
    }

    public BulkResponse deleteOrganizations(Collection organizationKeys) throws JAXRException {
        return null;
    }

    public BulkResponse deleteServiceBindings(Collection bindingKeys) throws JAXRException {
        return null;
    }

    public BulkResponse deleteServices(Collection serviceKeys) throws JAXRException {
        return null;
    }

    public BulkResponse saveAssociations(Collection associationKeys, boolean replace) throws JAXRException {
        return null;
    }

    public BulkResponse saveClassificationSchemes(Collection schemeKeys) throws JAXRException {
        return null;
    }

    public BulkResponse saveConcepts(Collection conceptKeys) throws JAXRException {
        return null;
    }

    public BulkResponse saveOrganizations(Collection organizationKeys) throws JAXRException {
        return null;
    }

    public BulkResponse saveServiceBindings(Collection bindingKeys) throws JAXRException {
        return null;
    }

    public BulkResponse saveServices(Collection serviceKeys) throws JAXRException {
        return null;
    }

    public void confirmAssociation(Association assoc) throws JAXRException, InvalidRequestException {
    }

    public void unConfirmAssociation(Association assoc) throws JAXRException, InvalidRequestException {
    }
}
