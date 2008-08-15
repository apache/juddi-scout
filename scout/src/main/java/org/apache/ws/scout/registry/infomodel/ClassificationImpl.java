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

/**
 * Implements JAXR Classification Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 */
public class ClassificationImpl extends RegistryObjectImpl
        implements javax.xml.registry.infomodel.Classification
{

    private ClassificationScheme scheme = new ClassificationSchemeImpl(null);
    private Concept concept = new ConceptImpl(null);
    private boolean external = false;
    private String value;

    private RegistryObject classfiedobj = null;

    /**
     * Creates a new instance of ClassificationImpl
     */
    public ClassificationImpl(LifeCycleManager lifeCycleManager)
    {
        super(lifeCycleManager);
    }

    public ClassificationScheme getClassificationScheme()
            throws JAXRException
    {
        return scheme;
    }

    public RegistryObject getClassifiedObject() throws JAXRException
    {
        return classfiedobj;
    }

    public Concept getConcept() throws JAXRException
    {
        return concept;
    }

    /**
     *
     * @return he value of the taxonomy element if external Classification;
     *      the value of the  Concept representing the taxonomy element
     *      if internal Classification
     * @throws JAXRException
     */
    public String getValue() throws JAXRException
    {
        if (isExternal()) {
            return value;
        }
        else {
            return concept.getValue();
        }
    }

    public void setExternal(boolean b) {
        this.external = b;
    }

    public boolean isExternal() throws JAXRException
    {
        return external;
    }

    public void setClassificationScheme(ClassificationScheme cscheme)
            throws JAXRException
    {
        scheme = cscheme;

        /*
         * not 100% clear, but I *think* the JavaDoc indicates that if
         * our internality dictates that of the scheme.
         */

       ((ClassificationSchemeImpl) scheme).setExternal(isExternal());
    }

    public void setClassifiedObject(RegistryObject registryObject)
            throws JAXRException
    {
        classfiedobj = registryObject;
    }

    public void setConcept(Concept cpt) throws JAXRException
    {
        concept = cpt;
    }

    public void setValue(String str) throws JAXRException
    {
        value = str;
    }

}
