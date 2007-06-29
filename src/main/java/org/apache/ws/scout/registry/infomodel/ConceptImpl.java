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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.RegistryObject;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ConceptImpl extends RegistryObjectImpl implements Concept
{
    private String value = new String();

    private RegistryObject parent = null;
    private Concept parentconcept = null;

    private ClassificationScheme scheme = null;
    private Collection childconcepts = new ArrayList();

    /**
     * Creates a new instance of ConceptImpl
     */
    public ConceptImpl(LifeCycleManager lifeCycleManager)
    {
        super(lifeCycleManager);
    }

    public void addChildConcept(Concept concept)
    {
        this.childconcepts.add(concept);
        ((ConceptImpl)concept).setParentconcept(this);
    }

    public void addChildConcepts(Collection collection)
    {
        Iterator iter = collection.iterator();
        while(iter.hasNext())
        {
            Concept c = (Concept)iter.next();
            ((ConceptImpl)c).setParentconcept(this);
            childconcepts.add(c);
        }

    }

    public int getChildConceptCount()
    {
        return this.childconcepts.size();
    }

    public Collection getChildrenConcepts()
    {
        return this.childconcepts;
    }

    public ClassificationScheme getClassificationScheme()
    {
        return scheme;
    }

    public Collection getDescendantConcepts()
    {
        Collection coll = new ArrayList();
        Iterator iter = childconcepts.iterator();
        while(iter != null && iter.hasNext())
        {
            ConceptImpl c = (ConceptImpl)iter.next();
            coll.add(c);
            coll.addAll(c.getDescendantConcepts());
        }
        return coll;
    }

    public RegistryObject getParent()
    {
        return parent;
    }

    public Concept getParentConcept()
    {
        return parentconcept;
    }

    public String getPath()
    {
        return null;
    }

    public String getValue() throws JAXRException
    {
        return value;
    }

    public void removeChildConcept(Concept c)
    {
        ((ConceptImpl)c).setParentconcept(null);
        childconcepts.remove(c);
    }

    public void removeChildConcepts(Collection collection)
    {
        Iterator iter = collection.iterator();
        while(iter.hasNext())
        {
            Concept c = (Concept)iter.next();
            ((ConceptImpl)c).setParentconcept(null);
            childconcepts.add(c);
        }
    }

    public void setValue(String str)
    {
        value = str;
    }

    public void setParent(RegistryObject parent)
    {
        this.parent = parent;
    }

    public void setParentconcept(Concept parentconcept)
    {
        this.parentconcept = parentconcept;
        parent = null; //We deal with concept as parent
    }

    public void setScheme(ClassificationSchemeImpl scheme)
    {
        this.scheme = scheme;
    }

    public void setChildconcepts(Collection childconcepts)
    {
        this.childconcepts.clear();
        Iterator iter = childconcepts.iterator();
        while(iter.hasNext())
        {
            Concept c = (Concept)iter.next();
            ((ConceptImpl)c).setParentconcept(this);
            childconcepts.add(c);
        }
    }

    //Specific API
    public void setClassificationScheme(ClassificationScheme sc)
    {
        scheme = sc;
        parent = sc;
    }
}
