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
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.RegistryObject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ConceptImpl extends RegistryObjectImpl implements Concept {
    private String value = new String();

    private RegistryObject parent = new RegistryObjectImpl(null);
    private Concept parentconcept = null;

    private ClassificationSchemeImpl scheme = new ClassificationSchemeImpl(null);
    private Collection childconcepts = new ArrayList();

    /**
     * Creates a new instance of ConceptImpl
     */
    public ConceptImpl(LifeCycleManager lifeCycleManager) {
        super(lifeCycleManager);
    }

    public void addChildConcept(Concept concept) {
        this.childconcepts.add(concept);
    }

    public void addChildConcepts(Collection collection) {
        this.childconcepts.addAll(collection);
    }

    public int getChildConceptCount() {
        return this.childconcepts.size();
    }

    public Collection getChildrenConcepts() {
        return this.childconcepts;
    }

    public ClassificationScheme getClassificationScheme() {
        return scheme;
    }

    public Collection getDescendantConcepts() {
        return null;
    }

    public RegistryObject getParent() {
        return parent;
    }

    public Concept getParentConcept() {
        return parentconcept;
    }

    public String getPath() {
        return null;
    }

    public String getValue() throws JAXRException {
        return value;
    }

    public void removeChildConcept(Concept concept) {
    }

    public void removeChildConcepts(Collection collection) {
        this.childconcepts.removeAll(collection);
    }

    public void setValue(String str) {
        value = str;
    }

    public void setParent(RegistryObject parent)
    {
        this.parent = parent;
    }

    public void setParentconcept(Concept parentconcept)
    {
        this.parentconcept = parentconcept;
    }

    public void setScheme(ClassificationSchemeImpl scheme)
    {
        this.scheme = scheme;
    }

    public void setChildconcepts(Collection childconcepts)
    {
        this.childconcepts = childconcepts;
    }
}
