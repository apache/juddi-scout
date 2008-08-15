/**
 *
 * Copyright 2003-2004 The Apache Software Foundation
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

//
// This source code implements specifications defined by the Java
// Community Process. In order to remain compliant with the specification
// DO NOT add / change / or delete method signatures!
//
package javax.xml.registry;

import java.util.Collection;

import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;

/**
 * @version $Revision$ $Date$
 */
public interface BusinessLifeCycleManager extends LifeCycleManager {
    void confirmAssociation(Association assoc) throws JAXRException, InvalidRequestException;

    BulkResponse deleteAssociations(Collection<Key> associationKeys) throws JAXRException;

    BulkResponse deleteClassificationSchemes(Collection<Key> schemeKeys) throws JAXRException;

    BulkResponse deleteConcepts(Collection<Key> conceptKeys) throws JAXRException;

    BulkResponse deleteOrganizations(Collection<Key> organizationKeys) throws JAXRException;

    BulkResponse deleteServiceBindings(Collection<Key> bindingKeys) throws JAXRException;

    BulkResponse deleteServices(Collection<Key> serviceKeys) throws JAXRException;

    BulkResponse saveAssociations(Collection<Association> associations, boolean replace) throws JAXRException;

    BulkResponse saveClassificationSchemes(Collection<ClassificationScheme> schemes) throws JAXRException;

    BulkResponse saveConcepts(Collection<Concept> concepts) throws JAXRException;

    BulkResponse saveOrganizations(Collection<Organization> organizations) throws JAXRException;

    BulkResponse saveServiceBindings(Collection<ServiceBinding> bindings) throws JAXRException;

    BulkResponse saveServices(Collection<Service> services) throws JAXRException;

    void unConfirmAssociation(Association assoc) throws JAXRException, InvalidRequestException;
}
