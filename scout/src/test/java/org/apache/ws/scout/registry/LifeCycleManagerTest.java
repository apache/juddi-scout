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

import java.util.ArrayList;
import java.util.Collection;

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
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.PersonName;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.User;

import junit.framework.TestCase;

import org.apache.ws.scout.registry.infomodel.AssociationImpl;
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
import org.apache.ws.scout.registry.infomodel.PersonNameImpl;
import org.apache.ws.scout.registry.infomodel.PostalAddressImpl;
import org.apache.ws.scout.registry.infomodel.ServiceBindingImpl;
import org.apache.ws.scout.registry.infomodel.ServiceImpl;
import org.apache.ws.scout.registry.infomodel.SlotImpl;
import org.apache.ws.scout.registry.infomodel.SpecificationLinkImpl;
import org.apache.ws.scout.registry.infomodel.TelephoneNumberImpl;
import org.apache.ws.scout.registry.infomodel.UserImpl;

/**
 *
 * @version $Rev$ $Date$ $Author$
 */
public class LifeCycleManagerTest extends TestCase {
    private LifeCycleManager manager;

    public void testCreateObjectAssociation() throws JAXRException {
        Association assoc = (Association) manager.createObject(LifeCycleManager.ASSOCIATION);
        assertEquals(AssociationImpl.class, assoc.getClass());
        assertSame(manager, assoc.getLifeCycleManager());
    }

    public void testCreateObjectAuditableEvent() throws JAXRException {
        try {
            manager.createObject(LifeCycleManager.AUDITABLE_EVENT);
            fail();
        } catch (UnsupportedCapabilityException e) {
            // OK
        }
    }

    public void testCreateObjectClassification() throws JAXRException {
        Classification classification = (Classification) manager.createObject(LifeCycleManager.CLASSIFICATION);
        assertEquals(ClassificationImpl.class, classification.getClass());
        assertSame(manager, classification.getLifeCycleManager());
    }

    public void testCreateObjectClassificationScheme() throws JAXRException {
        ClassificationScheme classificationScheme = (ClassificationScheme) manager.createObject(LifeCycleManager.CLASSIFICATION_SCHEME);
        assertEquals(ClassificationSchemeImpl.class, classificationScheme.getClass());
        assertSame(manager, classificationScheme.getLifeCycleManager());
    }

    public void testCreateObjectConcept() throws JAXRException {
        Concept concept = (Concept) manager.createObject(LifeCycleManager.CONCEPT);
        assertEquals(ConceptImpl.class, concept.getClass());
        assertSame(manager, concept.getLifeCycleManager());
    }

    public void testCreateObjectEmailAddress() throws JAXRException {
        assertEquals(EmailAddressImpl.class, ((EmailAddress) manager.createObject(LifeCycleManager.EMAIL_ADDRESS)).getClass());
    }

    public void testCreateObjectExternalIdentifier() throws JAXRException {
        ExternalIdentifier externalIdentifier = ((ExternalIdentifier) manager.createObject(LifeCycleManager.EXTERNAL_IDENTIFIER));
        assertEquals(ExternalIdentifierImpl.class, externalIdentifier.getClass());
        assertSame(manager, externalIdentifier.getLifeCycleManager());
    }

    public void testCreateObjectExternalLink() throws JAXRException {
        ExternalLink externalLink = (ExternalLink) manager.createObject(LifeCycleManager.EXTERNAL_LINK);
        assertEquals(ExternalLinkImpl.class, externalLink.getClass());
        assertSame(manager, externalLink.getLifeCycleManager());
    }

    public void testCreateObjectExtrinsicObject() throws JAXRException {
        try {
            manager.createObject(LifeCycleManager.EXTRINSIC_OBJECT);
            fail();
        } catch (UnsupportedCapabilityException e) {
            // OK
        }
    }

    public void testCreateObjectInternationalString() throws JAXRException {
        assertEquals(InternationalStringImpl.class, manager.createObject(LifeCycleManager.INTERNATIONAL_STRING).getClass());
    }

    public void testCreateObjectKey() throws JAXRException {
        assertEquals(KeyImpl.class, manager.createObject(LifeCycleManager.KEY).getClass());
    }

    public void testCreateObjectLocalizedString() throws JAXRException {
        assertEquals(LocalizedStringImpl.class, manager.createObject(LifeCycleManager.LOCALIZED_STRING).getClass());
    }

    public void testCreateObjectOrganization() throws JAXRException {
        Organization organization = (Organization) manager.createObject(LifeCycleManager.ORGANIZATION);
        assertEquals(OrganizationImpl.class, organization.getClass());
        assertSame(manager, organization.getLifeCycleManager());
    }

    public void testCreateObjectPersonName() throws JAXRException {

        PersonName pn = (PersonName) manager.createObject(LifeCycleManager.PERSON_NAME);
        assertEquals(PersonNameImpl.class, pn.getClass());
    }

    public void testCreateObjectPostalAddress() throws JAXRException {
        assertEquals(PostalAddressImpl.class, manager.createObject(LifeCycleManager.POSTAL_ADDRESS).getClass());
    }

    public void testCreateObjectRegistryEntry() throws JAXRException {
        try {
            manager.createObject(LifeCycleManager.REGISTRY_ENTRY);
            fail();
        } catch (UnsupportedCapabilityException e) {
            // OK
        }
    }

    public void testCreateObjectRegistryPackage() throws JAXRException {
        try {
            manager.createObject(LifeCycleManager.REGISTRY_PACKAGE);
            fail();
        } catch (UnsupportedCapabilityException e) {
            // OK
        }

        try {
            manager.createRegistryPackage(new InternationalStringImpl("Foo"));
            fail();
        } catch (UnsupportedCapabilityException e) {
            // OK
        }

        try {
            manager.createRegistryPackage("Foo");
            fail();
        } catch (UnsupportedCapabilityException e) {
            // OK
        }

    }

    public void testCreateObjectService() throws JAXRException {
        Service service = (Service) manager.createObject(LifeCycleManager.SERVICE);
        assertEquals(ServiceImpl.class, service.getClass());
        assertSame(manager, service.getLifeCycleManager());
    }

    public void testCreateObjectServiceBinding() throws JAXRException {
        ServiceBinding service = (ServiceBinding) manager.createObject(LifeCycleManager.SERVICE_BINDING);
        assertEquals(ServiceBindingImpl.class, service.getClass());
        assertSame(manager, service.getLifeCycleManager());
    }

    public void testCreateObjectSlot() throws JAXRException {
        assertEquals(SlotImpl.class, manager.createObject(LifeCycleManager.SLOT).getClass());
    }

    public void testCreateObjectSpecificationLink() throws JAXRException {
        assertEquals(SpecificationLinkImpl.class, manager.createObject(LifeCycleManager.SPECIFICATION_LINK).getClass());
    }

    public void testCreateObjectTelephoneNumber() throws JAXRException {
        assertEquals(TelephoneNumberImpl.class, manager.createObject(LifeCycleManager.TELEPHONE_NUMBER).getClass());
    }

    public void testCreateObjectUser() throws JAXRException {
        User user = (User) manager.createObject(LifeCycleManager.USER);
        assertEquals(UserImpl.class, user.getClass());
        assertSame(manager, user.getLifeCycleManager());
    }

    public void testCreateObjectInvalid() throws JAXRException {
        try {
            manager.createObject("Foo");
            fail();
        } catch (InvalidRequestException e) {
            // OK
        }
    }

    public void testDeprecateObjects() throws JAXRException {
        try {
            manager.deprecateObjects(new ArrayList());
            fail();
        } catch (UnsupportedCapabilityException e) {
            // OK
        }
    }

    public void testUnDeprecateObjects() throws JAXRException {
        try {
            manager.unDeprecateObjects(new ArrayList());
            fail();
        } catch (UnsupportedCapabilityException e) {
            // OK
        }
    }

    protected class ConcreteLifeCycleManager extends LifeCycleManagerImpl {

        public ConcreteLifeCycleManager(RegistryService rs) {
            super(rs);
        }

        public BulkResponse saveObjects(Collection objects) throws JAXRException {
            return new BulkResponseImpl();
        }

        public BulkResponse deleteObjects(Collection keys, String objectType) throws JAXRException {
            return new BulkResponseImpl();
        }
    }
    protected void setUp() throws Exception {
        super.setUp();
        manager = new ConcreteLifeCycleManager(new RegistryServiceImpl(null, null, -1));
    }
}
