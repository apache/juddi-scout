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

import org.apache.juddi.IRegistry;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.datatype.response.BusinessInfo;
import org.apache.juddi.datatype.response.BusinessList;
import org.apache.juddi.datatype.response.TModelDetail;
import org.apache.juddi.datatype.response.TModelInfo;
import org.apache.juddi.datatype.response.TModelInfos;
import org.apache.juddi.datatype.response.TModelList;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.error.RegistryException;
import org.apache.ws.scout.registry.infomodel.ClassificationSchemeImpl;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;
import org.apache.ws.scout.registry.infomodel.KeyImpl;
import org.apache.ws.scout.registry.infomodel.ConceptImpl;
import org.apache.ws.scout.util.EnumerationHelper;
import org.apache.ws.scout.util.ScoutUddiJaxrHelper;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.RegistryService;
import javax.xml.registry.UnsupportedCapabilityException;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.RegistryObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Implements the JAXR BusinessQueryManager Interface
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 * @author Jeremy Boynes  <jboynes@apache.org>
 */
public class BusinessQueryManagerImpl implements BusinessQueryManager
{
    private final RegistryServiceImpl registryService;


    public BusinessQueryManagerImpl(RegistryServiceImpl registry)
    {
        this.registryService = registry;
    }

    public RegistryService getRegistryService()
    {
        return registryService;
    }

    public BulkResponse findOrganizations(Collection findQualifiers,
                                          Collection namePatterns,
                                          Collection classifications,
                                          Collection specifications,
                                          Collection externalIdentifiers,
                                          Collection externalLinks) throws JAXRException
    {
        IRegistry registry = registryService.getRegistry();
        try
        {
            FindQualifiers juddiFindQualifiers = mapFindQualifiers(findQualifiers);
            Vector nameVector = mapNamePatterns(namePatterns);
            BusinessList result = registry.findBusiness(nameVector,
                    null, null, null, null,
                    juddiFindQualifiers,
                    registryService.getMaxRows());
            Vector v = result.getBusinessInfos().getBusinessInfoVector();
            Collection orgs = new ArrayList();
            int len = 0;
            if (v != null)
            {
                len = v.size();
                orgs = new ArrayList(len);
            }
            for (int i = 0; i < len; i++)
            {
                BusinessInfo info = (BusinessInfo) v.elementAt(i);
                //Now get the details on the individual biz
                BusinessDetail detail = registry.getBusinessDetail(info.getBusinessKey());
                orgs.add(registryService.getLifeCycleManagerImpl().createOrganization(detail));
            }
            return new BulkResponseImpl(orgs);
        } catch (RegistryException e)
        {
            throw new JAXRException(e);
        }
    }

    public BulkResponse findAssociations(Collection findQualifiers,
                                         String sourceObjectId,
                                         String targetObjectId,
                                         Collection associationTypes) throws JAXRException
    {
        return null;
    }

    public BulkResponse findCallerAssociations(Collection findQualifiers,
                                               Boolean confirmedByCaller,
                                               Boolean confirmedByOtherParty,
                                               Collection associationTypes) throws JAXRException
    {
        return null;
    }

    public ClassificationScheme findClassificationSchemeByName(Collection findQualifiers,
                                                               String namePatterns) throws JAXRException
    {
        ClassificationScheme scheme = null;

        if (namePatterns.equalsIgnoreCase("uddi-org:types"))
        {
            scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
            scheme.setName(new InternationalStringImpl("uddi-org:types"));
            scheme.setKey(new KeyImpl(TModel.TYPES_TMODEL_KEY));
        } else if (namePatterns.equalsIgnoreCase("dnb-com:D-U-N-S"))
        {
            scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
            scheme.setName(new InternationalStringImpl("dnb-com:D-U-N-S"));
            scheme.setKey(new KeyImpl(TModel.D_U_N_S_TMODEL_KEY));
        } else if (namePatterns.equalsIgnoreCase("uddi-org:iso-ch:3166:1999"))
        {
            scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
            scheme.setName(new InternationalStringImpl("uddi-org:iso-ch:3166:1999"));
            scheme.setKey(new KeyImpl(TModel.ISO_CH_TMODEL_KEY));
        } else if (namePatterns.equalsIgnoreCase("unspsc-org:unspsc"))
        {
            scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
            scheme.setName(new InternationalStringImpl("unspsc-org:unspsc"));
            scheme.setKey(new KeyImpl(TModel.UNSPSC_TMODEL_KEY));
        } else if (namePatterns.equalsIgnoreCase("ntis-gov:naics"))
        {
            scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
            scheme.setName(new InternationalStringImpl("ntis-gov:naics"));
            scheme.setKey(new KeyImpl(TModel.NAICS_TMODEL_KEY));
        }
        else
        {   //TODO:Before going to the registry, check if it a predefined Enumeration

            /*
             * predefined Enumerations
             */

            if ("AssociationType".equals(namePatterns)) {
                scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());

                scheme.setName(new InternationalStringImpl(namePatterns));

                scheme.setKey(new KeyImpl(TModel.UNSPSC_TMODEL_KEY));

                addChildConcept((ClassificationSchemeImpl)scheme, "RelatedTo");
                addChildConcept((ClassificationSchemeImpl)scheme, "HasChild");
                addChildConcept((ClassificationSchemeImpl)scheme, "HasMember");
                addChildConcept((ClassificationSchemeImpl)scheme, "HasParent");
                addChildConcept((ClassificationSchemeImpl)scheme, "ExternallyLinks");
                addChildConcept((ClassificationSchemeImpl)scheme, "Contains");
                addChildConcept((ClassificationSchemeImpl)scheme, "EquivalentTo");
                addChildConcept((ClassificationSchemeImpl)scheme, "Extends");
                addChildConcept((ClassificationSchemeImpl)scheme, "Implements");
                addChildConcept((ClassificationSchemeImpl)scheme, "InstanceOf");
                addChildConcept((ClassificationSchemeImpl)scheme, "Supersedes");
                addChildConcept((ClassificationSchemeImpl)scheme, "Uses");
                addChildConcept((ClassificationSchemeImpl)scheme, "Replaces");
                addChildConcept((ClassificationSchemeImpl)scheme, "ResponsibleFor");
                addChildConcept((ClassificationSchemeImpl)scheme, "SubmitterOf");
            }
            else if ("ObjectType".equals(namePatterns)) {
                scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());

                scheme.setName(new InternationalStringImpl(namePatterns));

                scheme.setKey(new KeyImpl(TModel.UNSPSC_TMODEL_KEY));

                addChildConcept((ClassificationSchemeImpl)scheme, "CPP");
                addChildConcept((ClassificationSchemeImpl)scheme, "CPA");
                addChildConcept((ClassificationSchemeImpl)scheme, "Process");
                addChildConcept((ClassificationSchemeImpl)scheme, "WSDL");
                addChildConcept((ClassificationSchemeImpl)scheme, "Association");
                addChildConcept((ClassificationSchemeImpl)scheme, "AuditableEvent");
                addChildConcept((ClassificationSchemeImpl)scheme, "Classification");
                addChildConcept((ClassificationSchemeImpl)scheme, "Concept");
                addChildConcept((ClassificationSchemeImpl)scheme, "ExternalIdentifier");
                addChildConcept((ClassificationSchemeImpl)scheme, "ExternalLink");
                addChildConcept((ClassificationSchemeImpl)scheme, "ExtrinsicObject");
                addChildConcept((ClassificationSchemeImpl)scheme, "Organization");
                addChildConcept((ClassificationSchemeImpl)scheme, "Package");
                addChildConcept((ClassificationSchemeImpl)scheme, "Service");
                addChildConcept((ClassificationSchemeImpl)scheme, "ServiceBinding");
                addChildConcept((ClassificationSchemeImpl)scheme, "User");
            }
            else if ("PhoneType".equals(namePatterns)) {
                scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());

                scheme.setName(new InternationalStringImpl(namePatterns));

                scheme.setKey(new KeyImpl(TModel.UNSPSC_TMODEL_KEY));

                addChildConcept((ClassificationSchemeImpl)scheme, "OfficePhone");
                addChildConcept((ClassificationSchemeImpl)scheme, "HomePhone");
                addChildConcept((ClassificationSchemeImpl)scheme, "MobilePhone");
                addChildConcept((ClassificationSchemeImpl)scheme, "Beeper");
                addChildConcept((ClassificationSchemeImpl)scheme, "FAX");
            }
            else if ("URLType".equals(namePatterns)) {
                scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());

                scheme.setName(new InternationalStringImpl(namePatterns));

                scheme.setKey(new KeyImpl(TModel.UNSPSC_TMODEL_KEY));

                addChildConcept((ClassificationSchemeImpl)scheme, "HTTP");
                addChildConcept((ClassificationSchemeImpl)scheme, "HTTPS");
                addChildConcept((ClassificationSchemeImpl)scheme, "SMTP");
                addChildConcept((ClassificationSchemeImpl)scheme, "PHONE");
                addChildConcept((ClassificationSchemeImpl)scheme, "FAX");
                addChildConcept((ClassificationSchemeImpl)scheme, "OTHER");
            }
            else if ("PostalAddressAttributes".equals(namePatterns)) {
                scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());

                scheme.setName(new InternationalStringImpl(namePatterns));

                scheme.setKey(new KeyImpl(TModel.UNSPSC_TMODEL_KEY));

                addChildConcept((ClassificationSchemeImpl)scheme, "StreetNumber");
                addChildConcept((ClassificationSchemeImpl)scheme, "Street");
                addChildConcept((ClassificationSchemeImpl)scheme, "City");
                addChildConcept((ClassificationSchemeImpl)scheme, "State");
                addChildConcept((ClassificationSchemeImpl)scheme, "PostalCode");
                addChildConcept((ClassificationSchemeImpl)scheme, "Country");
            }
            else {

                //Lets ask the uddi registry if it has the TModels
                IRegistry registry = registryService.getRegistry();
                FindQualifiers juddiFindQualifiers = mapFindQualifiers(findQualifiers);
                Vector nameVector = new Vector();
                nameVector.add(namePatterns);
                try
                {
                    //We are looking for one exact match, so getting upto 3 records is fine
                    TModelList list = registry.findTModel(namePatterns, null, null, juddiFindQualifiers, 3);
                    TModelInfos infos = null;
                    Vector tmvect = null;
                    if (list != null) infos = list.getTModelInfos();
                    if (infos != null) tmvect = infos.getTModelInfoVector();
                    if (tmvect != null)
                    {
                        if (tmvect.size() > 1)
                            throw new InvalidRequestException("Multiple matches found");

                        TModelInfo info = (TModelInfo) tmvect.elementAt(0);
                        scheme.setName(new InternationalStringImpl(info.getName().getValue()));
                        scheme.setKey(new KeyImpl(info.getTModelKey()));
                    }

                } catch (RegistryException e)
                {
                    e.printStackTrace();
                    throw new JAXRException(e.getLocalizedMessage());
                }
            }
        }
        return scheme;
    }

    /**
     * Creates a new Concept, associates w/ parent scheme, and then
     * adds as decendent.
     * @param scheme
     * @param name
     * @throws JAXRException
     */
    private void addChildConcept(ClassificationSchemeImpl scheme, String name)
        throws JAXRException {
        Concept c = new ConceptImpl(registryService.getLifeCycleManagerImpl());

        c.setName(new InternationalStringImpl(name));
        c.setValue(name);
        ((ConceptImpl)c).setScheme((ClassificationSchemeImpl)scheme);

        scheme.addChildConcept(c);
    }

    public BulkResponse findClassificationSchemes(Collection findQualifiers,
                                                  Collection namePatterns,
                                                  Collection classifications,
                                                  Collection externalLinks) throws JAXRException
    {
        return null;
    }

    public Concept findConceptByPath(String path) throws JAXRException
    {
        //We will store the enumerations datastructure in the util package
        /**
         * I am not clear about how this association type enumerations
         * are to be implemented.
         */
        return EnumerationHelper.getConceptByPath(path);
    }

    public BulkResponse findConcepts(Collection findQualifiers,
                                     Collection namePatterns,
                                     Collection classifications,
                                     Collection externalIdentifiers,
                                     Collection externalLinks) throws JAXRException
    {
        Collection col = new ArrayList();

        //Lets ask the uddi registry if it has the TModels
        IRegistry registry = registryService.getRegistry();
        FindQualifiers juddiFindQualifiers = mapFindQualifiers(findQualifiers);
        Iterator iter = null;
        if (namePatterns != null) iter = namePatterns.iterator();
        while (iter.hasNext())
        {
            String namestr = (String) iter.next();
            try
            {
                TModelList list = registry.findTModel(namestr, null, null, juddiFindQualifiers, 10);
                TModelInfos infos = null;
                Vector tmvect = null;
                if (list != null) infos = list.getTModelInfos();
                if (infos != null) tmvect = infos.getTModelInfoVector();
                for (int i = 0; tmvect != null && i < tmvect.size(); i++)
                {
                    TModelInfo info = (TModelInfo) tmvect.elementAt(i);
                    col.add(ScoutUddiJaxrHelper.getConcept(info, this.registryService.getBusinessLifeCycleManager()));
                }

            } catch (RegistryException e)
            {
                e.printStackTrace();
                throw new JAXRException(e.getLocalizedMessage());
            }
        }

        return new BulkResponseImpl(col);
    }

    public BulkResponse findRegistryPackages(Collection findQualifiers,
                                             Collection namePatterns,
                                             Collection classifications,
                                             Collection externalLinks) throws JAXRException
    {
        return null;
    }

    public BulkResponse findServiceBindings(Key serviceKey,
                                            Collection findQualifiers,
                                            Collection classifications,
                                            Collection specifications) throws JAXRException
    {
        return null;
    }

    public BulkResponse findServices(Key orgKey, Collection findQualifiers,
                                     Collection namePattersn,
                                     Collection classifications,
                                     Collection specificationa) throws JAXRException
    {
        return null;
    }

    public RegistryObject getRegistryObject(String id) throws JAXRException
    {
        return null;
    }

    public RegistryObject getRegistryObject(String id, String objectType) throws JAXRException
    {
        IRegistry registry = registryService.getRegistry();
        RegistryObject regobj = null;
        if (LifeCycleManager.CLASSIFICATION_SCHEME.equalsIgnoreCase(objectType))
        {
            try
            {
                TModelDetail tmodeldetail = registry.getTModelDetail(id);
                regobj = (RegistryObject) ScoutUddiJaxrHelper.getConcept(tmodeldetail, registryService.getBusinessLifeCycleManager());

            } catch (RegistryException e)
            {
                e.printStackTrace();
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        if (LifeCycleManager.ORGANIZATION.equalsIgnoreCase(objectType))
        {
            //Get the Organization from the uddi registry
            try
            {
                BusinessDetail orgdetail = registry.getBusinessDetail(id);
                regobj = (RegistryObject) ScoutUddiJaxrHelper.getOrganization(orgdetail, registryService.getBusinessLifeCycleManager());
            } catch (RegistryException e)
            {
                e.printStackTrace();
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        if (LifeCycleManager.CONCEPT.equalsIgnoreCase(objectType))
        {
            try
            {
                TModelDetail tmodeldetail = registry.getTModelDetail(id);
                regobj = (RegistryObject) ScoutUddiJaxrHelper.getConcept(tmodeldetail, registryService.getBusinessLifeCycleManager());

            } catch (RegistryException e)
            {
                e.printStackTrace();
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        return regobj;
    }

    public BulkResponse getRegistryObjects() throws JAXRException
    {
        return null;
    }

    public BulkResponse getRegistryObjects(Collection objectKeys) throws JAXRException
    {
        return null;
    }

    public BulkResponse getRegistryObjects(Collection objectKeys, String objectType) throws JAXRException
    {
        IRegistry registry = registryService.getRegistry();
        //Convert into a vector of strings
        Vector keys = new Vector();
        Iterator iter = objectKeys.iterator();
        while(iter.hasNext())
        {
            Key key = (Key)iter.next();
            keys.add(key.getId());
        }
        Collection col = new ArrayList();
        LifeCycleManager lcm = registryService.getLifeCycleManagerImpl();

        if (LifeCycleManager.CLASSIFICATION_SCHEME.equalsIgnoreCase(objectType))
        {
            try
            {
                TModelDetail tmodeldetail = registry.getTModelDetail(keys);
                Vector tmvect = tmodeldetail.getTModelVector();
                for (int i = 0; tmvect != null && i < tmvect.size(); i++)
                {
                    col.add(ScoutUddiJaxrHelper.getConcept((TModel) tmvect.elementAt(i), lcm));
                }

            } catch (RegistryException e)
            {
                e.printStackTrace();
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        if (LifeCycleManager.ORGANIZATION.equalsIgnoreCase(objectType))
        {
            //Get the Organization from the uddi registry
            try
            {
                BusinessDetail orgdetail = registry.getBusinessDetail(keys);
                Vector bizvect = orgdetail.getBusinessEntityVector();
                for (int i = 0; bizvect != null && i < bizvect.size(); i++)
                {
                    col.add(ScoutUddiJaxrHelper.getOrganization((BusinessEntity) bizvect.elementAt(i), lcm));
                }
            } catch (RegistryException e)
            {
                e.printStackTrace();
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        if (LifeCycleManager.CONCEPT.equalsIgnoreCase(objectType))
        {
            try
            {
                TModelDetail tmodeldetail = registry.getTModelDetail(keys);
                Vector tmvect = tmodeldetail.getTModelVector();
                for (int i = 0; tmvect != null && i < tmvect.size(); i++)
                {
                    col.add(ScoutUddiJaxrHelper.getConcept((TModel) tmvect.elementAt(i), lcm));
                }

            } catch (RegistryException e)
            {
                e.printStackTrace();
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        return new BulkResponseImpl(col);

    }

    public BulkResponse getRegistryObjects(String objectTypes) throws JAXRException
    {
        return null;
    }

    private static final Map findQualifierMapping;

    static
    {
        findQualifierMapping = new HashMap();
        findQualifierMapping.put(FindQualifier.AND_ALL_KEYS, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.AND_ALL_KEYS));
        findQualifierMapping.put(FindQualifier.CASE_SENSITIVE_MATCH, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.CASE_SENSITIVE_MATCH));
        findQualifierMapping.put(FindQualifier.COMBINE_CLASSIFICATIONS, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.COMBINE_CATEGORY_BAGS));
        findQualifierMapping.put(FindQualifier.EXACT_NAME_MATCH, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.EXACT_NAME_MATCH));
        findQualifierMapping.put(FindQualifier.OR_ALL_KEYS, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.OR_ALL_KEYS));
        findQualifierMapping.put(FindQualifier.OR_LIKE_KEYS, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.OR_LIKE_KEYS));
        findQualifierMapping.put(FindQualifier.SERVICE_SUBSET, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.SERVICE_SUBSET));
        findQualifierMapping.put(FindQualifier.SORT_BY_DATE_ASC, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.SORT_BY_DATE_ASC));
        findQualifierMapping.put(FindQualifier.SORT_BY_DATE_DESC, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.SORT_BY_DATE_DESC));
        findQualifierMapping.put(FindQualifier.SORT_BY_NAME_ASC, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.SORT_BY_NAME_ASC));
        findQualifierMapping.put(FindQualifier.SORT_BY_NAME_DESC, new org.apache.juddi.datatype.request.FindQualifier(org.apache.juddi.datatype.request.FindQualifier.SORT_BY_NAME_DESC));
//        findQualifierMapping.put(FindQualifier.SOUNDEX, null);
    }

    static FindQualifiers mapFindQualifiers(Collection jaxrQualifiers) throws UnsupportedCapabilityException
    {
        if (jaxrQualifiers == null)
        {
            return null;
        }
        FindQualifiers result = new FindQualifiers(jaxrQualifiers.size());
        for (Iterator i = jaxrQualifiers.iterator(); i.hasNext();)
        {
            String jaxrQualifier = (String) i.next();
            org.apache.juddi.datatype.request.FindQualifier juddiQualifier =
                    (org.apache.juddi.datatype.request.FindQualifier) findQualifierMapping.get(jaxrQualifier);
            if (juddiQualifier == null)
            {
                throw new UnsupportedCapabilityException("jUDDI does not support FindQualifer: " + jaxrQualifier);
            }
            result.addFindQualifier(juddiQualifier);
        }
        return result;
    }

    static Vector mapNamePatterns(Collection namePatterns)
    {
        if (namePatterns == null)
            return null;
        Vector result = new Vector(namePatterns.size());
        for (Iterator i = namePatterns.iterator(); i.hasNext();)
        {
            String pattern = (String) i.next();
            result.add(new Name(pattern));
        }
        return result;
    }
}
