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
import javax.xml.registry.UnsupportedCapabilityException;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 */
public class ClassificationSchemeImpl
        extends RegistryEntryImpl
        implements ClassificationScheme
{

    private Collection<Concept> childConcepts = new ArrayList<Concept>();

    //private int valueType = 1; KS: not used.

    private boolean external = false;
    
    /**
     * Creates a new instance of ClassificationSchemeImpl
     */
    public ClassificationSchemeImpl(LifeCycleManager lifeCycleManager)
    {
        super(lifeCycleManager);
    }

    public void addChildConcept(Concept concept)
            throws JAXRException
    {
        childConcepts.add(concept);
    }

    public void addChildConcepts(Collection collection)
            throws JAXRException
    {
        childConcepts.addAll(collection);
    }

    public int getChildConceptCount()
            throws JAXRException
    {
        return childConcepts.size();
    }

    public Collection getChildrenConcepts() throws JAXRException
    {
        return childConcepts;
    }

    public Collection getDescendantConcepts() throws JAXRException
    {
        Collection<Concept> coll = new ArrayList<Concept>();
        Iterator iter = childConcepts.iterator();
        while(iter != null && iter.hasNext())
        {
            ConceptImpl c = (ConceptImpl)iter.next();
            coll.add(c);
            coll.addAll(c.getDescendantConcepts());
        }
        return coll;
    }

    public int getValueType()
        throws JAXRException
    {
        /*
         * we are a level 0 provider
         */

        throw new UnsupportedCapabilityException();
    }

    protected void setExternal(boolean b) {
        this.external = b;
    }

    public boolean isExternal() throws JAXRException
    {
        return this.external;
    }

    public void removeChildConcept(Concept concept)
            throws JAXRException
    {
        this.childConcepts.remove(concept);
    }

    public void removeChildConcepts(Collection collection)
            throws JAXRException
    {
        this.childConcepts.removeAll(collection);
    }

    public void setValueType(int param)
        throws JAXRException
    {
        /*
         * we are a level 0 provider
         */

        throw new UnsupportedCapabilityException();
    }

}
