/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ws.scout.registry.infomodel;

import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.UnsupportedCapabilityException;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.ExternalIdentifier;
import javax.xml.registry.infomodel.ExternalLink;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.RegistryObject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements  RegistryObject Interface
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class RegistryObjectImpl extends ExtensibleObjectImpl implements RegistryObject {
    private final LifeCycleManager lifeCycleManager;
    private Key key;
    private InternationalString name = new InternationalStringImpl();
    private InternationalString desc = new InternationalStringImpl();

    private Set classifications = new HashSet();
    private Set associations = new HashSet();
    private Set externalIds = new HashSet();
    private Set externalLinks = new HashSet();

    private OrganizationImpl submittingOrganization;

    public RegistryObjectImpl(LifeCycleManager lifeCycleManager) {
        this.lifeCycleManager = lifeCycleManager;
    }

    public Key getKey() {
        return key;
    }

    public InternationalString getDescription() {
        return desc;
    }

    public void setDescription(InternationalString description) {
        this.desc = description;
    }

    public InternationalString getName() {
        return name;
    }

    public void setName(InternationalString name) {
        this.name = name;
    }

    public void setKey(Key k) {
        key = k;
    }

    public String toXML() throws JAXRException {
        throw new UnsupportedCapabilityException("toXML is not supported");
    }

    public void addClassification(Classification classification) {
        classifications.add(classification);
    }

    public void addClassifications(Collection collection) {
        for (Iterator i = collection.iterator(); i.hasNext();) {
            Classification classification = (Classification) i.next();
            classifications.add(classification);
        }
    }

    public void removeClassification(Classification classification) {
        classifications.remove(classification);
    }

    public void removeClassifications(Collection collection) {
        classifications.removeAll(collection);
    }

    public Collection getClassifications() {
        return Collections.unmodifiableSet(classifications);
    }

    public void setClassifications(Collection collection) {
        Set newClassifications = new HashSet(collection.size());
        for (Iterator i = collection.iterator(); i.hasNext();) {
            Classification classification = (Classification) i.next();
            newClassifications.add(classification);
        }
        classifications = newClassifications;
    }

    public void addAssociation(Association association) {
        associations.add(association);
    }

    public void addAssociations(Collection collection) throws JAXRException {
        for (Iterator i = collection.iterator(); i.hasNext();) {
            Association association = (Association) i.next();
            associations.add(association);
        }
    }

    public Collection getAssociations() throws JAXRException {
        return Collections.unmodifiableSet(associations);
    }

    public void setAssociations(Collection collection) {
        Set newAssociations = new HashSet(collection.size());
        for (Iterator i = collection.iterator(); i.hasNext();) {
            Association association = (Association) i.next();
            newAssociations.add(association);
        }
        associations = newAssociations;
    }

    public void removeAssociation(Association association) {
        associations.remove(association);
    }


    public void removeAssociations(Collection collection) {
        associations.removeAll(collection);
    }

    public void addExternalIdentifier(ExternalIdentifier externalIdentifier) {
        externalIds.add(externalIdentifier);
    }

    public void addExternalIdentifiers(Collection collection) {
        for (Iterator i = collection.iterator(); i.hasNext();) {
            ExternalIdentifier externalId = (ExternalIdentifier) i.next();
            externalIds.add(externalId);
        }
    }

    public void removeExternalIdentifier(ExternalIdentifier externalIdentifier) {
        externalIds.remove(externalIdentifier);
    }

    public void removeExternalIdentifiers(Collection collection) {
        externalIds.removeAll(collection);
    }

    public Collection getExternalIdentifiers() {
        return externalIds;
    }

    public void setExternalIdentifiers(Collection collection) {
        Set newExternalIds = new HashSet(collection.size());
        for (Iterator i = collection.iterator(); i.hasNext();) {
            ExternalIdentifier externalId = (ExternalIdentifier) i.next();
            newExternalIds.add(externalId);
        }
        externalIds = newExternalIds;
    }

    public void addExternalLink(ExternalLink externalLink) {
        externalLinks.add(externalLink);
    }

    public void addExternalLinks(Collection collection) {
        for (Iterator i = collection.iterator(); i.hasNext();) {
            ExternalLink externalLink = (ExternalLink) i.next();
            externalLinks.add(externalLink);
        }
    }

    public void removeExternalLink(ExternalLink externalLink) {
        externalLinks.remove(externalLink);
    }

    public void removeExternalLinks(Collection collection) {
        externalLinks.removeAll(collection);
    }

    public Collection getExternalLinks() {
        return Collections.unmodifiableSet(externalLinks);
    }

    public void setExternalLinks(Collection collection) {
        Set newExternalLinks = new HashSet(collection.size());
        for (Iterator i = collection.iterator(); i.hasNext();) {
            ExternalLink externalLink = (ExternalLink) i.next();
            newExternalLinks.add(externalLink);
        }
        externalLinks = newExternalLinks;
    }

    public Organization getSubmittingOrganization() {
        return submittingOrganization;
    }

    public LifeCycleManager getLifeCycleManager() {
        return lifeCycleManager;
    }

    /**
     * The spec does not define how equality is defined for RegistryObject's.
     * We choose to define it as having the same class and key value; if the
     * key is null then the objects are not equal.
     *
     * @param obj the object to compare to
     * @return true if the other object is of the same class and has the same key value
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || !this.getClass().equals(obj.getClass())) return false;
        final RegistryObjectImpl other = (RegistryObjectImpl) obj;
        return this.key != null && key.equals(other.key);
    }

    public int hashCode() {
        return key == null ? 0 : key.hashCode();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Level 1 features must throw exceptions
    ///////////////////////////////////////////////////////////////////////////

    public Collection getAuditTrail() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public Collection getAssociatedObjects() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public Concept getObjectType() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public Collection getRegistryPackages() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }
}
