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
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.datatype.response.BusinessInfo;
import org.apache.juddi.datatype.response.ServiceInfo;
import org.apache.ws.scout.util.ScoutUddiJaxrHelper;
import org.apache.ws.scout.registry.infomodel.PostalAddressImpl;
import org.apache.ws.scout.registry.infomodel.KeyImpl;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;
import org.apache.ws.scout.registry.infomodel.LocalizedStringImpl;
import org.apache.ws.scout.registry.infomodel.ExternalIdentifierImpl;
import org.apache.ws.scout.registry.infomodel.ExternalLinkImpl;
import org.apache.ws.scout.registry.infomodel.ConceptImpl;
import org.apache.ws.scout.registry.infomodel.EmailAddressImpl;
import org.apache.ws.scout.registry.infomodel.ClassificationImpl;
import org.apache.ws.scout.registry.infomodel.ClassificationSchemeImpl;
import org.apache.ws.scout.registry.infomodel.AssociationImpl;
import org.apache.ws.scout.registry.infomodel.OrganizationImpl;
import org.apache.ws.scout.registry.infomodel.PersonNameImpl;
import org.apache.ws.scout.registry.infomodel.ServiceImpl;
import org.apache.ws.scout.registry.infomodel.ServiceBindingImpl;
import org.apache.ws.scout.registry.infomodel.SpecificationLinkImpl;
import org.apache.ws.scout.registry.infomodel.SlotImpl;
import org.apache.ws.scout.registry.infomodel.TelephoneNumberImpl;
import org.apache.ws.scout.registry.infomodel.UserImpl;

import javax.activation.DataHandler;
import javax.xml.registry.BulkResponse;
import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.RegistryService;
import javax.xml.registry.UnsupportedCapabilityException;
import javax.xml.registry.infomodel.User;
import javax.xml.registry.infomodel.TelephoneNumber;
import javax.xml.registry.infomodel.ExtrinsicObject;
import javax.xml.registry.infomodel.Slot;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.SpecificationLink;
import javax.xml.registry.infomodel.PostalAddress;
import javax.xml.registry.infomodel.PersonName;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.LocalizedString;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.ExternalIdentifier;
import javax.xml.registry.infomodel.ExternalLink;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.EmailAddress;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.RegistryPackage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * Implements JAXR LifeCycleManager Interface
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 */
public abstract class LifeCycleManagerImpl implements LifeCycleManager {
    protected final RegistryServiceImpl registry;

    public LifeCycleManagerImpl(RegistryService registry) {
        this.registry = (RegistryServiceImpl) registry;
    }

    public RegistryService getRegistryService() {
        return registry;
    }

    public Object createObject(String interfaceName) throws JAXRException {
        // we don't use reflection so that we can work in environments where
        // we may not have permission to do so
        if (LifeCycleManager.ASSOCIATION.equals(interfaceName)) {
            return new AssociationImpl(this);
        }
        else if (LifeCycleManager.AUDITABLE_EVENT.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        }
        else if (LifeCycleManager.CLASSIFICATION.equals(interfaceName)) {
            return new ClassificationImpl(this);
        }
        else if (LifeCycleManager.CLASSIFICATION_SCHEME.equals(interfaceName)) {
            return new ClassificationSchemeImpl(this);
        }
        else if (LifeCycleManager.CONCEPT.equals(interfaceName)) {
            return new ConceptImpl(this);
        }
        else if (LifeCycleManager.EMAIL_ADDRESS.equals(interfaceName)) {
            return new EmailAddressImpl();
        }
        else if (LifeCycleManager.EXTERNAL_IDENTIFIER.equals(interfaceName)) {
            return new ExternalIdentifierImpl(this);
        }
        else if (LifeCycleManager.EXTERNAL_LINK.equals(interfaceName)) {
            return new ExternalLinkImpl(this);
        }
        else if (LifeCycleManager.EXTRINSIC_OBJECT.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        }
        else if (LifeCycleManager.INTERNATIONAL_STRING.equals(interfaceName)) {
            return new InternationalStringImpl();
        }
        else if (LifeCycleManager.KEY.equals(interfaceName)) {
            return new KeyImpl();
        }
        else if (LifeCycleManager.LOCALIZED_STRING.equals(interfaceName)) {
            return new LocalizedStringImpl();
        }
        else if (LifeCycleManager.ORGANIZATION.equals(interfaceName)) {
            return new OrganizationImpl(this);
        }
        else if (LifeCycleManager.PERSON_NAME.equals(interfaceName)) {
            return new PersonNameImpl();
        }
        else if (LifeCycleManager.POSTAL_ADDRESS.equals(interfaceName)) {
            return new PostalAddressImpl(registry.getDefaultPostalScheme());
        }
        else if (LifeCycleManager.REGISTRY_ENTRY.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        }
        else if (LifeCycleManager.REGISTRY_PACKAGE.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        }
        else if (LifeCycleManager.SERVICE.equals(interfaceName)) {
            return new ServiceImpl(this);
        }
        else if (LifeCycleManager.SERVICE_BINDING.equals(interfaceName)) {
            return new ServiceBindingImpl(this);
        }
        else if (LifeCycleManager.SLOT.equals(interfaceName)) {
            return new SlotImpl();
        }
        else if (LifeCycleManager.SPECIFICATION_LINK.equals(interfaceName)) {
            return new SpecificationLinkImpl(this);
        }
        else if (LifeCycleManager.TELEPHONE_NUMBER.equals(interfaceName)) {
            return new TelephoneNumberImpl();
        }
        else if (LifeCycleManager.USER.equals(interfaceName)) {
            return new UserImpl(this);
        }
        else if (LifeCycleManager.VERSIONABLE.equals(interfaceName)) {
            throw new UnsupportedCapabilityException();
        }
        else {
            throw new InvalidRequestException("Unknown interface: " + interfaceName);
        }
    }

    public Association createAssociation(RegistryObject targetObject, Concept associationType) throws JAXRException {
        Association assoc = (Association) this.createObject(LifeCycleManager.ASSOCIATION);
        assoc.setTargetObject(targetObject);
        assoc.setAssociationType(associationType);
        return assoc;
    }

    public Classification createClassification(Concept concept) throws JAXRException, InvalidRequestException {
        if (concept.getClassificationScheme() == null) {
            throw new InvalidRequestException("Concept is not under classification scheme");
        }
        Classification classify = (Classification) this.createObject(LifeCycleManager.CLASSIFICATION);
        classify.setConcept(concept);
        return classify;
    }

    public Classification createClassification(ClassificationScheme scheme,
                                               InternationalString name,
                                               String value) throws JAXRException {
        Classification cl = (Classification) this.createObject(LifeCycleManager.CLASSIFICATION);
        cl.setClassificationScheme(scheme);
        cl.setName(name);
        cl.setValue(value);

        ((ClassificationImpl) cl).setExternal(true);

        return cl;
    }

    public Classification createClassification(ClassificationScheme scheme,
                                               String name, String value)
            throws JAXRException {
        return createClassification(scheme, this.createInternationalString(name), value);
    }

    public ClassificationScheme createClassificationScheme(Concept concept) throws JAXRException, InvalidRequestException {
        //Check if the passed concept has a classificationscheme or has a parent concept
        if (concept.getParentConcept() != null || concept.getClassificationScheme() != null) {
            throw new InvalidRequestException("Concept has classificationscheme or has a parent");
        }


        ClassificationScheme cs = new ClassificationSchemeImpl(this);
        cs.addChildConcept(concept);
        return cs;
    }

    public ClassificationScheme createClassificationScheme(InternationalString name,
                                                           InternationalString des)
            throws JAXRException, InvalidRequestException {
        ClassificationScheme cs = new ClassificationSchemeImpl(this);
        cs.setName(name);
        cs.setDescription(des);
        return cs;
    }

    public ClassificationScheme createClassificationScheme(String name, String desc)
            throws JAXRException, InvalidRequestException {
        return createClassificationScheme(this.createInternationalString(name),
                this.createInternationalString(desc));
    }

    public Concept createConcept(RegistryObject parent, InternationalString name, String value)
            throws JAXRException {
        ConceptImpl concept = new ConceptImpl(this);
        concept.setClassificationScheme((ClassificationScheme) parent);
        concept.setParent(parent);
        concept.setName(name);
        concept.setValue(value);
        return concept;
    }

    public Concept createConcept(RegistryObject parent, String name, String value) throws JAXRException {
        return createConcept(parent, this.createInternationalString(name), value);
    }

    public EmailAddress createEmailAddress(String address) throws JAXRException {
        return new EmailAddressImpl(address);
    }

    public EmailAddress createEmailAddress(String address, String type) throws JAXRException {
        return new EmailAddressImpl(address, type);
    }

    public ExternalIdentifier createExternalIdentifier(ClassificationScheme ids,
                                                       InternationalString name,
                                                       String value) throws JAXRException {
        return new ExternalIdentifierImpl(this, ids, name, value);
    }

    public ExternalIdentifier createExternalIdentifier(ClassificationScheme ids,
                                                       String name, String value) throws JAXRException {
        return createExternalIdentifier(ids, this.createInternationalString(name), value);
    }

    public ExternalLink createExternalLink(String uri, InternationalString desc) throws JAXRException {
        ExternalLink ext = new ExternalLinkImpl(this);
        ext.setExternalURI(uri);
        ext.setDescription(desc);
        return ext;
    }

    public ExternalLink createExternalLink(String uri, String desc) throws JAXRException {
        return createExternalLink(uri, createInternationalString(desc));
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
        Organization org = (Organization) this.createObject(LifeCycleManager.ORGANIZATION);
        org.setName(name);
        return org;
    }

    public Organization createOrganization(String name) throws JAXRException {
        Organization org = (Organization) this.createObject(LifeCycleManager.ORGANIZATION);
        org.setName(this.createInternationalString(name));
        return org;
    }

    public PersonName createPersonName(String fullName) throws JAXRException {
        PersonName pn = (PersonName) this.createObject(LifeCycleManager.PERSON_NAME);
        pn.setFullName(fullName);
        return pn;
    }

    public PostalAddress createPostalAddress(String streetNumber,
                                             String street,
                                             String city,
                                             String stateOrProvince,
                                             String country,
                                             String postalCode, String type) throws JAXRException {
        PostalAddress post = new PostalAddressImpl();
        post.setStreetNumber(streetNumber);
        post.setStreet(street);
        post.setCity(city);
        post.setStateOrProvince(stateOrProvince);
        post.setCountry(country);
        post.setPostalCode(postalCode);
        post.setType(type);
        return post;
    }

    public Service createService(InternationalString name) throws JAXRException {
        Service ser = (Service) this.createObject(LifeCycleManager.SERVICE);
        ser.setName(name);
        return ser;
    }

    public Service createService(String name) throws JAXRException {
        return createService(this.createInternationalString(name));
    }

    public ServiceBinding createServiceBinding() throws JAXRException {
        return (ServiceBinding) this.createObject(LifeCycleManager.SERVICE_BINDING);
    }

    public Slot createSlot(String name, String value, String slotType) throws JAXRException {
        Collection col = new ArrayList();
        col.add(value);
        Slot slot = (Slot) this.createObject(LifeCycleManager.SLOT);
        slot.setName(name);
        slot.setValues(col);
        slot.setSlotType(slotType);
        return slot;
    }

    public Slot createSlot(String name, Collection values, String slotType) throws JAXRException {
        Slot slot = (Slot) this.createObject(LifeCycleManager.SLOT);
        slot.setName(name);
        slot.setValues(values);
        slot.setSlotType(slotType);
        return slot;
    }

    public SpecificationLink createSpecificationLink() throws JAXRException {
        return (SpecificationLink) this.createObject(LifeCycleManager.SPECIFICATION_LINK);
    }

    public TelephoneNumber createTelephoneNumber() throws JAXRException {
        return (TelephoneNumber) this.createObject(LifeCycleManager.TELEPHONE_NUMBER);
    }

    public User createUser() throws JAXRException {
        return (User) this.createObject(LifeCycleManager.USER);
    }

    /**
     * aves one or more Objects to the registry. An object may be a
     * RegistryObject  subclass instance. If an object is not in the registry,
     * it is created in the registry.  If it already exists in the registry
     * and has been modified, then its  state is updated (replaced) in the
     * registry
     *
     * @param objects
     * @return a BulkResponse containing the Collection of keys for those objects
     *         that were saved successfully and any SaveException that was encountered
     *         in case of partial commit
     * @throws JAXRException
     */

    public abstract BulkResponse saveObjects(Collection objects) throws JAXRException;

    /**
     * Deletes one or more previously submitted objects from the registry
     * using the object keys and a specified objectType attribute.
     *
     * @param keys
     * @param objectType
     * @return
     * @throws JAXRException
     */
    public abstract BulkResponse deleteObjects(Collection keys, String objectType) throws JAXRException;


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

    Organization createOrganization(BusinessDetail detail) throws JAXRException {
        return ScoutUddiJaxrHelper.getOrganization(detail, this);
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
