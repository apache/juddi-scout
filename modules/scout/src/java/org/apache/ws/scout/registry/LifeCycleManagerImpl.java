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

import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.response.BusinessInfo;
import org.apache.juddi.datatype.response.ServiceInfo;
import org.apache.ws.scout.registry.infomodel.ClassificationImpl;
import org.apache.ws.scout.registry.infomodel.ClassificationSchemeImpl;
import org.apache.ws.scout.registry.infomodel.ConceptImpl;
import org.apache.ws.scout.registry.infomodel.EmailAddressImpl;
import org.apache.ws.scout.registry.infomodel.ExternalIdentifierImpl;
import org.apache.ws.scout.registry.infomodel.ExternalLinkImpl;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;
import org.apache.ws.scout.registry.infomodel.KeyImpl;
import org.apache.ws.scout.registry.infomodel.LocalizedStringImpl;
import org.apache.ws.scout.registry.infomodel.OrganizationImpl;
import org.apache.ws.scout.registry.infomodel.PostalAddressImpl;
import org.apache.ws.scout.registry.infomodel.RegistryEntryImpl;
import org.apache.ws.scout.registry.infomodel.ServiceImpl;
import org.apache.ws.scout.registry.infomodel.TelephoneNumberImpl;
import org.apache.ws.scout.registry.infomodel.UserImpl;

import javax.activation.DataHandler;
import javax.xml.registry.BulkResponse;
import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.RegistryService;
import javax.xml.registry.UnsupportedCapabilityException;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.EmailAddress;
import javax.xml.registry.infomodel.ExternalIdentifier;
import javax.xml.registry.infomodel.ExternalLink;
import javax.xml.registry.infomodel.ExtrinsicObject;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.LocalizedString;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.PersonName;
import javax.xml.registry.infomodel.PostalAddress;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.RegistryPackage;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.Slot;
import javax.xml.registry.infomodel.SpecificationLink;
import javax.xml.registry.infomodel.TelephoneNumber;
import javax.xml.registry.infomodel.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * Implements JAXR LifeCycleManager Interface
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class LifeCycleManagerImpl implements LifeCycleManager {
    private final RegistryService registry;

    public LifeCycleManagerImpl(RegistryService registry) {
        this.registry = registry;
    }

    public RegistryService getRegistryService() {
        return registry;
    }

    public Object createObject(String interfaceName) throws JAXRException {
        // we don't use reflection so that we can work in environments where
        // we may not have permission to do so
        if (LifeCycleManager.ASSOCIATION.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        } else if (LifeCycleManager.AUDITABLE_EVENT.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        } else if (LifeCycleManager.CLASSIFICATION.equals(interfaceName)) {
            return new ClassificationImpl(this);
        } else if (LifeCycleManager.CLASSIFICATION_SCHEME.equals(interfaceName)) {
            return new ClassificationSchemeImpl(this);
        } else if (LifeCycleManager.CONCEPT.equals(interfaceName)) {
            return new ConceptImpl(this);
        } else if (LifeCycleManager.EMAIL_ADDRESS.equals(interfaceName)) {
            return new EmailAddressImpl();
        } else if (LifeCycleManager.EXTERNAL_IDENTIFIER.equals(interfaceName)) {
            return new ExternalIdentifierImpl(this);
        } else if (LifeCycleManager.EXTERNAL_LINK.equals(interfaceName)) {
            return new ExternalLinkImpl(this);
        } else if (LifeCycleManager.EXTRINSIC_OBJECT.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        } else if (LifeCycleManager.INTERNATIONAL_STRING.equals(interfaceName)) {
            return new InternationalStringImpl();
        } else if (LifeCycleManager.KEY.equals(interfaceName)) {
            return new KeyImpl();
        } else if (LifeCycleManager.LOCALIZED_STRING.equals(interfaceName)) {
            return new LocalizedStringImpl();
        } else if (LifeCycleManager.ORGANIZATION.equals(interfaceName)) {
            return new OrganizationImpl(this);
        } else if (LifeCycleManager.PERSON_NAME.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        } else if (LifeCycleManager.POSTAL_ADDRESS.equals(interfaceName)) {
            return new PostalAddressImpl(registry.getDefaultPostalScheme());
        } else if (LifeCycleManager.REGISTRY_ENTRY.equals(interfaceName)) {
            return new RegistryEntryImpl(this);
        } else if (LifeCycleManager.REGISTRY_PACKAGE.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        } else if (LifeCycleManager.SERVICE.equals(interfaceName)) {
            return new ServiceImpl(this);
        } else if (LifeCycleManager.SERVICE_BINDING.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        } else if (LifeCycleManager.SLOT.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        } else if (LifeCycleManager.SPECIFICATION_LINK.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        } else if (LifeCycleManager.TELEPHONE_NUMBER.equals(interfaceName)) {
            return new TelephoneNumberImpl();
        } else if (LifeCycleManager.USER.equals(interfaceName)) {
            return new UserImpl(this);
        } else if (LifeCycleManager.VERSIONABLE.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        } else {
            throw new InvalidRequestException("Unknown interface: " + interfaceName);
        }
    }

    public Association createAssociation(RegistryObject targetObject, Concept associationType) throws JAXRException {
        return null;
    }

    public Classification createClassification(Concept concept) throws JAXRException, InvalidRequestException {
        return null;
    }

    public Classification createClassification(ClassificationScheme scheme, InternationalString name, String value) throws JAXRException {
        return null;
    }

    public Classification createClassification(ClassificationScheme scheme, String name, String value) throws JAXRException {
        return null;
    }

    public ClassificationScheme createClassificationScheme(Concept concept) throws JAXRException, InvalidRequestException {
        return null;
    }

    public ClassificationScheme createClassificationScheme(InternationalString name, InternationalString description) throws JAXRException, InvalidRequestException {
        return null;
    }

    public ClassificationScheme createClassificationScheme(String name, String description) throws JAXRException, InvalidRequestException {
        return null;
    }

    public Concept createConcept(RegistryObject parent, InternationalString name, String value) throws JAXRException {
        return null;
    }

    public Concept createConcept(RegistryObject parent, String name, String value) throws JAXRException {
        return null;
    }

    public EmailAddress createEmailAddress(String address) throws JAXRException {
        return null;
    }

    public EmailAddress createEmailAddress(String address, String type) throws JAXRException {
        return null;
    }

    public ExternalIdentifier createExternalIdentifier(ClassificationScheme identificationScheme, InternationalString name, String value) throws JAXRException {
        return null;
    }

    public ExternalIdentifier createExternalIdentifier(ClassificationScheme identificationScheme, String name, String value) throws JAXRException {
        return null;
    }

    public ExternalLink createExternalLink(String externalURI, InternationalString description) throws JAXRException {
        return null;
    }

    public ExternalLink createExternalLink(String externalURI, String description) throws JAXRException {
        return null;
    }

    public InternationalString createInternationalString() throws JAXRException {
        return new InternationalStringImpl();
    }

    public InternationalString createInternationalString(String value) throws JAXRException {
        return new InternationalStringImpl(Locale.getDefault(), value, LocalizedString.DEFAULT_CHARSET_NAME);
    }

    public InternationalString createInternationalString(Locale locale, String value) throws JAXRException {
        return new InternationalStringImpl(locale, value, LocalizedString.DEFAULT_CHARSET_NAME);
    }

    public Key createKey(String id) {
        return new KeyImpl(id);
    }

    public LocalizedString createLocalizedString(Locale locale, String value) throws JAXRException {
        return new LocalizedStringImpl(locale, value, LocalizedString.DEFAULT_CHARSET_NAME);
    }

    public LocalizedString createLocalizedString(Locale locale, String value, String charsetName) throws JAXRException {
        return new LocalizedStringImpl(locale, value, charsetName);
    }

    public Organization createOrganization(InternationalString name) throws JAXRException {
        return null;
    }

    public Organization createOrganization(String name) throws JAXRException {
        return null;
    }

    public PersonName createPersonName(String fullName) throws JAXRException {
        return null;
    }

    public PostalAddress createPostalAddress(String streetNumber, String street, String city, String stateOrProvince, String country, String postalCode, String type) throws JAXRException {
        return null;
    }

    public Service createService(InternationalString name) throws JAXRException {
        return null;
    }

    public Service createService(String name) throws JAXRException {
        return null;
    }

    public ServiceBinding createServiceBinding() throws JAXRException {
        return null;
    }

    public Slot createSlot(String name, String value, String slotType) throws JAXRException {
        return null;
    }

    public Slot createSlot(String name, Collection values, String slotType) throws JAXRException {
        return null;
    }

    public SpecificationLink createSpecificationLink() throws JAXRException {
        return null;
    }

    public TelephoneNumber createTelephoneNumber() throws JAXRException {
        return null;
    }

    public User createUser() throws JAXRException {
        return null;
    }

    public BulkResponse deleteObjects(Collection keys, String objectType) throws JAXRException {
        return null;
    }

    public BulkResponse saveObjects(Collection objects) throws JAXRException {
        return null;
    }

    /*************************************************************************
     * Level 1 Features
     ************************************************************************/

    /**
     * @param repositoryItem
     * @return
     * @throws JAXRException
     */
    public ExtrinsicObject createExtrinsicObject(DataHandler repositoryItem) throws JAXRException {
        throw new UnsupportedCapabilityException();
    }

    public PersonName createPersonName(String firstName, String middleName, String lastName) throws JAXRException {
        throw new UnsupportedCapabilityException();
    }

    public RegistryPackage createRegistryPackage(InternationalString name) throws JAXRException {
        throw new UnsupportedCapabilityException();
    }

    public RegistryPackage createRegistryPackage(String name) throws JAXRException {
        throw new UnsupportedCapabilityException();
    }

    public BulkResponse deprecateObjects(Collection keys) throws JAXRException {
        throw new UnsupportedCapabilityException();
    }

    public BulkResponse unDeprecateObjects(Collection keys) throws JAXRException {
        throw new UnsupportedCapabilityException();
    }

    public BulkResponse deleteObjects(Collection keys) throws JAXRException {
        throw new UnsupportedCapabilityException();
    }

    Organization createOrganization(BusinessInfo info) throws JAXRException {
        String key = info.getBusinessKey();
        Vector names = info.getNameVector();
        Vector descriptions = info.getDescriptionVector();
        Vector serviceInfos = info.getServiceInfos().getServiceInfoVector();
        OrganizationImpl org = new OrganizationImpl(this);
        org.setKey(createKey(key));
        if (names != null && !names.isEmpty()) {
            org.setName(createInternationalString(((Name) names.get(0)).getValue()));
        }
        if (descriptions != null && !descriptions.isEmpty()) {
            org.setDescription(createInternationalString(((Description) descriptions.get(0)).getValue()));
        }
        if (serviceInfos != null && !serviceInfos.isEmpty()) {
            List services = new ArrayList(serviceInfos.size());
            for (int i = 0; i < serviceInfos.size(); i++) {
                ServiceInfo serviceInfo = (ServiceInfo) serviceInfos.elementAt(i);
                services.add(createService(serviceInfo));
            }
            org.addServices(services);
        }
        return org;
    }

    Service createService(ServiceInfo info) throws JAXRException {
        String key = info.getServiceKey();
        Vector names = info.getNameVector();
        ServiceImpl service = new ServiceImpl(this);
        service.setKey(createKey(key));
        if (!names.isEmpty()) {
            service.setName(createInternationalString(((Name) names.get(0)).getValue()));
        }
        return service;
    }
}
