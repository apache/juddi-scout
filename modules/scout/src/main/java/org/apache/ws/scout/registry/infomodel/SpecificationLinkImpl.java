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
package org.apache.ws.scout.registry.infomodel;

import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.SpecificationLink;
import java.util.Collection;

/**
 * Implements JAXR API
 *
 * @author <mailto:anil@apache.org>Anil Saldhana
 * @since Nov 20, 2004
 */
public class SpecificationLinkImpl extends RegistryObjectImpl
        implements SpecificationLink
{
    private Collection usageParams;
    private InternationalString descr;
    private RegistryObject specObj;
    private ServiceBinding binding;

    public SpecificationLinkImpl(LifeCycleManager lifeCycleManager)
    {
        super(lifeCycleManager);
    }

    public ServiceBinding getServiceBinding() throws JAXRException
    {
        return binding;
    }

    public RegistryObject getSpecificationObject() throws JAXRException
    {
        return specObj;
    }

    public InternationalString getUsageDescription() throws JAXRException
    {
        return descr;
    }

    public Collection getUsageParameters() throws JAXRException
    {
        return usageParams;
    }

    public void setSpecificationObject(RegistryObject registryObject) throws JAXRException
    {
        specObj = registryObject;
    }

    public void setUsageDescription(InternationalString is) throws JAXRException
    {
        descr = is;
    }

    public void setUsageParameters(Collection collection) throws JAXRException
    {
        usageParams = collection;
    }

    //Specific API
    public void setServiceBinding(ServiceBinding s)
    {
        binding = s;
    }
}
