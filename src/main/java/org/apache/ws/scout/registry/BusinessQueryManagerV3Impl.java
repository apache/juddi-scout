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

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.RegistryService;
import javax.xml.registry.UnsupportedCapabilityException;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.LocalizedString;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.uddi.api_v3.*;
import org.apache.ws.scout.registry.infomodel.AssociationImpl;
import org.apache.ws.scout.registry.infomodel.ClassificationSchemeImpl;
import org.apache.ws.scout.registry.infomodel.ConceptImpl;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;
import org.apache.ws.scout.registry.infomodel.KeyImpl;
import org.apache.ws.scout.registry.infomodel.ServiceBindingImpl;
import org.apache.ws.scout.registry.infomodel.ServiceImpl;
import org.apache.ws.scout.util.EnumerationHelper;
import org.apache.ws.scout.util.ScoutJaxrUddiV3Helper;
import org.apache.ws.scout.util.ScoutUddiV3JaxrHelper;

/**
 * Implements the JAXR BusinessQueryManager Interface
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * @author <a href="mailto:jboynes@apache.org">Jeremy Boynes</a>
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 */
public class BusinessQueryManagerV3Impl implements BusinessQueryManager
{
    private final RegistryServiceImpl registryService;
    private Log log = LogFactory.getLog(this.getClass());

    private static ObjectFactory objectFactory = new ObjectFactory();

    public BusinessQueryManagerV3Impl(RegistryServiceImpl registry)
    {
        this.registryService = registry;
    }

    public RegistryService getRegistryService()
    {
        return registryService;
    }

    /**
     * Finds organizations in the registry that match the specified parameters
     *
     * @param findQualifiers
     * @param namePatterns
     * @param classifications
     * @param specifications
     * @param externalIdentifiers
     * @param externalLinks
     * @return BulkResponse
     * @throws JAXRException
     */
    public BulkResponse findOrganizations(Collection findQualifiers,
                                          Collection namePatterns,
                                          Collection classifications,
                                          Collection specifications,
                                          Collection externalIdentifiers,
                                          Collection externalLinks) throws JAXRException
    {
        IRegistryV3 registry = (IRegistryV3) registryService.getRegistry();
        try
        {
            FindQualifiers juddiFindQualifiers = mapFindQualifiers(findQualifiers);
            Name[] nameArray = mapNamePatterns(namePatterns);
            BusinessList result = registry.findBusiness(nameArray,
                    null, 
                    ScoutJaxrUddiV3Helper.getIdentifierBagFromExternalIdentifiers(externalIdentifiers), 
                    ScoutJaxrUddiV3Helper.getCategoryBagFromClassifications(classifications), 
                    ScoutJaxrUddiV3Helper.getTModelBagFromSpecifications(specifications),
                    juddiFindQualifiers,
                    registryService.getMaxRows());
            
            BusinessInfo[] bizInfoArr =null;
            BusinessInfos bizInfos = result.getBusinessInfos();
            LinkedHashSet<Organization> orgs = new LinkedHashSet<Organization>();
            if(bizInfos != null)
            {
            	List<BusinessInfo> bizInfoList = bizInfos.getBusinessInfo();
            	for (BusinessInfo businessInfo : bizInfoList) {
                    //Now get the details on the individual biz
                    BusinessDetail detail = registry.getBusinessDetail(businessInfo.getBusinessKey());
                    BusinessLifeCycleManagerV3Impl blcm = (BusinessLifeCycleManagerV3Impl)registryService.getLifeCycleManagerImpl();
                    orgs.add(blcm.createOrganization(detail));
				}
            	bizInfoArr = new BusinessInfo[bizInfoList.size()];
            	bizInfoList.toArray(bizInfoArr);
            }
            return new BulkResponseImpl(orgs);
        } catch (RegistryV3Exception e)
        {
            throw new JAXRException(e);
        }
    }

    public BulkResponse findAssociations(Collection findQualifiers,
                                         String sourceObjectId,
                                         String targetObjectId,
                                         Collection associationTypes) throws JAXRException
    {
        //TODO: Currently we just return all the Association objects owned by the caller
        IRegistryV3 registry = (IRegistryV3) registryService.getRegistry();
        try
        {
            ConnectionImpl con = ((RegistryServiceImpl)getRegistryService()).getConnection();
            AuthToken auth = this.getAuthToken(con,registry);
            PublisherAssertions result =
                    registry.getPublisherAssertions(auth.getAuthInfo());
            List<PublisherAssertion> publisherAssertionList = result.getPublisherAssertion();
            LinkedHashSet<Association> col = new LinkedHashSet<Association>();
            for (PublisherAssertion pas : publisherAssertionList) {
                String sourceKey = pas.getFromKey();
                String targetKey = pas.getToKey();
                Collection<Key> orgcol = new ArrayList<Key>();
                orgcol.add(new KeyImpl(sourceKey));
                orgcol.add(new KeyImpl(targetKey));
                BulkResponse bl = getRegistryObjects(orgcol, LifeCycleManager.ORGANIZATION);
                Association asso = ScoutUddiV3JaxrHelper.getAssociation(bl.getCollection(),
                                             registryService.getBusinessLifeCycleManager());
                KeyedReference keyr = pas.getKeyedReference();
                Concept c = new ConceptImpl(getRegistryService().getBusinessLifeCycleManager());
                c.setName(new InternationalStringImpl(keyr.getKeyName()));
                c.setKey( new KeyImpl(keyr.getTModelKey()) );
                c.setValue(keyr.getKeyValue());
                asso.setAssociationType(c);
                col.add(asso);
            }
            return new BulkResponseImpl(col);
        } catch (RegistryV3Exception e)
        {
            throw new JAXRException(e);
        }
    }

    public BulkResponse findCallerAssociations(Collection findQualifiers,
                                               Boolean confirmedByCaller,
                                               Boolean confirmedByOtherParty,
                                               Collection associationTypes) throws JAXRException
    {
        //TODO: Currently we just return all the Association objects owned by the caller
        IRegistryV3 registry = (IRegistryV3) registryService.getRegistry();
        try
        {
            ConnectionImpl con = ((RegistryServiceImpl)getRegistryService()).getConnection();
            AuthToken auth = this.getAuthToken(con,registry);
           
            AssertionStatusReport report = null;
            String confirm = "";
            boolean caller = confirmedByCaller.booleanValue();
            boolean other = confirmedByOtherParty.booleanValue();

            if(caller  && other   )
                        confirm = Constants.COMPLETION_STATUS_COMPLETE;
            else
              if(!caller  && other  )
                        confirm = Constants.COMPLETION_STATUS_FROMKEY_INCOMPLETE;
            else
                 if(caller  && !other   )
                        confirm = Constants.COMPLETION_STATUS_TOKEY_INCOMPLETE;

            report = registry.getAssertionStatusReport(auth.getAuthInfo(),confirm);
            
            
            List<AssertionStatusItem> assertionStatusItemList = report.getAssertionStatusItem();
            LinkedHashSet<Association> col = new LinkedHashSet<Association>();
            for (AssertionStatusItem asi : assertionStatusItemList) {
                String sourceKey = asi.getFromKey();
                String targetKey = asi.getToKey();
                Collection<Key> orgcol = new ArrayList<Key>();
                orgcol.add(new KeyImpl(sourceKey));
                orgcol.add(new KeyImpl(targetKey));
                BulkResponse bl = getRegistryObjects(orgcol, LifeCycleManager.ORGANIZATION);
                Association asso = ScoutUddiV3JaxrHelper.getAssociation(bl.getCollection(),
                                             registryService.getBusinessLifeCycleManager());
                //Set Confirmation
                ((AssociationImpl)asso).setConfirmedBySourceOwner(caller);
                ((AssociationImpl)asso).setConfirmedByTargetOwner(other);

                if(confirm != Constants.COMPLETION_STATUS_COMPLETE)
                     ((AssociationImpl)asso).setConfirmed(false);

                Concept c = new ConceptImpl(getRegistryService().getBusinessLifeCycleManager());
                KeyedReference keyr = asi.getKeyedReference();
                c.setKey(new KeyImpl(keyr.getTModelKey()));
                c.setName(new InternationalStringImpl(keyr.getKeyName()));
                c.setValue(keyr.getKeyValue());
                asso.setKey(new KeyImpl(keyr.getTModelKey())); //TODO:Validate this
                asso.setAssociationType(c);
                col.add(asso);
            }


            return new BulkResponseImpl(col);
        } catch (RegistryV3Exception e)
        {
            throw new JAXRException(e);
        }
    }

    /**
     *  TODO - need to support the qualifiers
     *
     * @param findQualifiers
     * @param namePatterns
     * @return ClassificationScheme
     * @throws JAXRException
     */
    public ClassificationScheme findClassificationSchemeByName(Collection findQualifiers,
                                                               String namePatterns) throws JAXRException
    {
        ClassificationScheme scheme = null;

        if (namePatterns.indexOf("uddi-org:types") != -1) {

            scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
            scheme.setName(new InternationalStringImpl(namePatterns));
            scheme.setKey(new KeyImpl(Constants.TMODEL_TYPES_TMODEL_KEY));
        }
        else if (namePatterns.indexOf("dnb-com:D-U-N-S") != -1) {

            scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
            scheme.setName(new InternationalStringImpl(namePatterns));
            scheme.setKey(new KeyImpl(Constants.TMODEL_D_U_N_S_TMODEL_KEY));
        }
        else if (namePatterns.indexOf("uddi-org:iso-ch:3166:1999") != -1)
        {
            scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
            scheme.setName(new InternationalStringImpl(namePatterns));
            scheme.setKey(new KeyImpl(Constants.TMODEL_ISO_CH_TMODEL_KEY));
        }
        else if (namePatterns.indexOf("uddi-org:iso-ch:3166-1999") != -1)
        {
            scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
            scheme.setName(new InternationalStringImpl(namePatterns));
            scheme.setKey(new KeyImpl(Constants.TMODEL_ISO_CH_TMODEL_KEY));
        }
        else if (namePatterns.indexOf("iso-ch:3166:1999") != -1)
        {
            scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
            scheme.setName(new InternationalStringImpl(namePatterns));
            scheme.setKey(new KeyImpl(Constants.TMODEL_ISO_CH_TMODEL_KEY));
        }
        else if (namePatterns.indexOf("iso-ch:3166-1999") != -1)
        {
            scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
            scheme.setName(new InternationalStringImpl(namePatterns));
            scheme.setKey(new KeyImpl(Constants.TMODEL_ISO_CH_TMODEL_KEY));
        }
        else if (namePatterns.indexOf("unspsc-org:unspsc") != -1) {

            scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
            scheme.setName(new InternationalStringImpl(namePatterns));
            scheme.setKey(new KeyImpl(Constants.TMODEL_UNSPSC_TMODEL_KEY));
        }
        else if (namePatterns.indexOf("ntis-gov:naics") != -1) {

            scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
            scheme.setName(new InternationalStringImpl(namePatterns));
            scheme.setKey(new KeyImpl(Constants.TMODEL_NAICS_TMODEL_KEY));
        }
        else
        {   //TODO:Before going to the registry, check if it a predefined Enumeration

            /*
             * predefined Enumerations
             */

            if ("AssociationType".equals(namePatterns)) {
                scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());

                scheme.setName(new InternationalStringImpl(namePatterns));

                scheme.setKey(new KeyImpl(Constants.TMODEL_UNSPSC_TMODEL_KEY));

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

                scheme.setKey(new KeyImpl(Constants.TMODEL_UNSPSC_TMODEL_KEY));

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

                scheme.setKey(new KeyImpl(Constants.TMODEL_UNSPSC_TMODEL_KEY));

                addChildConcept((ClassificationSchemeImpl)scheme, "OfficePhone");
                addChildConcept((ClassificationSchemeImpl)scheme, "HomePhone");
                addChildConcept((ClassificationSchemeImpl)scheme, "MobilePhone");
                addChildConcept((ClassificationSchemeImpl)scheme, "Beeper");
                addChildConcept((ClassificationSchemeImpl)scheme, "FAX");
            }
            else if ("URLType".equals(namePatterns)) {
                scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());

                scheme.setName(new InternationalStringImpl(namePatterns));

                scheme.setKey(new KeyImpl(Constants.TMODEL_UNSPSC_TMODEL_KEY));

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

                scheme.setKey(new KeyImpl(Constants.TMODEL_UNSPSC_TMODEL_KEY));

                addChildConcept((ClassificationSchemeImpl)scheme, "StreetNumber");
                addChildConcept((ClassificationSchemeImpl)scheme, "Street");
                addChildConcept((ClassificationSchemeImpl)scheme, "City");
                addChildConcept((ClassificationSchemeImpl)scheme, "State");
                addChildConcept((ClassificationSchemeImpl)scheme, "PostalCode");
                addChildConcept((ClassificationSchemeImpl)scheme, "Country");
            }
            else {

                //Lets ask the uddi registry if it has the TModels
                IRegistryV3 registry = (IRegistryV3) registryService.getRegistry();
                FindQualifiers juddiFindQualifiers = mapFindQualifiers(findQualifiers);
                try
                {
                    //We are looking for one exact match, so getting upto 3 records is fine
                    TModelList list = registry.findTModel(namePatterns, null, null, juddiFindQualifiers, 3);
                    if (list != null) {
                        TModelInfos infos = list.getTModelInfos();
                        if (infos != null) {
                            List<TModelInfo> tmodelInfoList = infos.getTModelInfo();
                            if (tmodelInfoList.size() > 1) {
                                throw new InvalidRequestException("Multiple matches found:" + tmodelInfoList.size());
                            }
                            if (tmodelInfoList.size() ==1) {
                                TModelInfo info = tmodelInfoList.get(0);
                                scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
                                scheme.setName(new InternationalStringImpl(info.getName().getValue()));
                                scheme.setKey(new KeyImpl(info.getTModelKey()));
                            }
                        }
                    }

                } catch (RegistryV3Exception e)
                { 
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
		//TODO: Handle this better
        LinkedHashSet<ClassificationScheme> col = new LinkedHashSet<ClassificationScheme>();
		Iterator iter = namePatterns.iterator();
		String name = "";
		while(iter.hasNext())
		{
			name = (String)iter.next();
			break;
		}
		
        ClassificationScheme classificationScheme = findClassificationSchemeByName(findQualifiers,name);
        if (classificationScheme!=null) {
            col.add(classificationScheme);
        }
		return new BulkResponseImpl(col);
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
        LinkedHashSet<Concept> col = new LinkedHashSet<Concept>();

        //Lets ask the uddi registry if it has the TModels
        IRegistryV3 registry = (IRegistryV3) registryService.getRegistry();
        FindQualifiers juddiFindQualifiers = mapFindQualifiers(findQualifiers);
        Iterator iter = null;
        if (namePatterns != null) iter = namePatterns.iterator();
        while (iter.hasNext())
        {
            String namestr = (String) iter.next();
            try
            {
                TModelList list = registry.findTModel(namestr, 
                        ScoutJaxrUddiV3Helper.getCategoryBagFromClassifications(classifications), 
                        ScoutJaxrUddiV3Helper.getIdentifierBagFromExternalIdentifiers(externalIdentifiers), 
                		juddiFindQualifiers, 10);
               
                if (list != null && list.getTModelInfos()!=null) {
                	List<TModelInfo> tmodelInfoList = list.getTModelInfos().getTModelInfo();
                	if (tmodelInfoList!=null) {
                		for (TModelInfo info: tmodelInfoList) {
                            col.add(ScoutUddiV3JaxrHelper.getConcept(info, this.registryService.getBusinessLifeCycleManager()));
						}
                	}
                }
            } catch (RegistryV3Exception e) { 
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
        throw new UnsupportedCapabilityException();
    }

    public BulkResponse findServiceBindings(Key serviceKey,
                                            Collection findQualifiers,
                                            Collection classifications,
                                            Collection specifications) throws JAXRException
    {
        BulkResponseImpl blkRes = new BulkResponseImpl();

        IRegistryV3 iRegistry = (IRegistryV3) registryService.getRegistry();
        FindQualifiers juddiFindQualifiers = mapFindQualifiers(findQualifiers);

        try
        {
 
            BindingDetail bindingDetail = iRegistry.findBinding(serviceKey.getId(),
                    ScoutJaxrUddiV3Helper.getCategoryBagFromClassifications(classifications), 
            		ScoutJaxrUddiV3Helper.getTModelBagFromSpecifications(specifications),
            		juddiFindQualifiers,registryService.getMaxRows());

            /*
             * now convert  from jUDDI ServiceInfo objects to JAXR Services
             */
            if (bindingDetail != null) {

            	List<BindingTemplate> bindingTemplateList = bindingDetail.getBindingTemplate();
                BindingTemplate[] bindarr = new BindingTemplate[bindingTemplateList.size()];
                bindingTemplateList.toArray(bindarr);
                
                LinkedHashSet<ServiceBinding> col = new LinkedHashSet<ServiceBinding>();

                for (int i=0; bindarr != null && i < bindarr.length; i++) {
                    BindingTemplate si = bindarr[i];
                    ServiceBinding sb =  ScoutUddiV3JaxrHelper.getServiceBinding(si,
                            registryService.getBusinessLifeCycleManager());
                    col.add(sb);
                   //Fill the Service object by making a call to registry
                   Service s = (Service)getRegistryObject(serviceKey.getId(), LifeCycleManager.SERVICE);
                   ((ServiceBindingImpl)sb).setService(s);
                }

                blkRes.setCollection(col);
            }
        }
        catch (RegistryV3Exception e) {
            throw new JAXRException(e.getLocalizedMessage());
        }

        return blkRes;
    }


    /**
     * Finds all Service objects that match all of the criteria specified by
     * the parameters of this call.  This is a logical AND operation between
     * all non-null parameters
     *
     * TODO - support findQualifiers, classifications and specifications
     *
     * @param orgKey
     * @param findQualifiers
     * @param namePatterns
     * @param classifications
     * @param specificationa
     * @return BulkResponse
     * @throws JAXRException
     */
    public BulkResponse findServices(Key orgKey, Collection findQualifiers,
                                     Collection namePatterns,
                                     Collection classifications,
                                     Collection specificationa) throws JAXRException
    {
        BulkResponseImpl blkRes = new BulkResponseImpl();

        IRegistryV3 iRegistry = (IRegistryV3) registryService.getRegistry();
        FindQualifiers juddiFindQualifiers = mapFindQualifiers(findQualifiers);
        Name[] juddiNames = mapNamePatterns(namePatterns);

        try
        {
            /*
             * hit the registry.  The key is not required for UDDI2
             */

            String id = null;

            if (orgKey != null) {
                id = orgKey.getId();
            }

            ServiceList serviceList = iRegistry.findService(id, 
            		juddiNames,
                    ScoutJaxrUddiV3Helper.getCategoryBagFromClassifications(classifications), 
                    null, 
                    juddiFindQualifiers, registryService.getMaxRows());

            /*
             * now convert  from jUDDI ServiceInfo objects to JAXR Services
             */
            if (serviceList != null) {

                ServiceInfos serviceInfos = serviceList.getServiceInfos();
                LinkedHashSet<Service> col = new LinkedHashSet<Service>();
                
                if(serviceInfos != null && serviceInfos.getServiceInfo()!=null) {
                	for (ServiceInfo si : serviceInfos.getServiceInfo()) {
                		Service srv = (Service) getRegistryObject(si.getServiceKey(), LifeCycleManager.SERVICE);
                        col.add(srv);
					}
                	
                }
                blkRes.setCollection(col);
            }
        }
        catch (RegistryV3Exception e) {
            throw new JAXRException(e.getLocalizedMessage());
        }

        return blkRes;
    }

    public RegistryObject getRegistryObject(String id) throws JAXRException
    {
        throw new UnsupportedCapabilityException();
    }

    public RegistryObject getRegistryObject(String id, String objectType) throws JAXRException
    {
        IRegistryV3 registry = (IRegistryV3) registryService.getRegistry();
        BusinessLifeCycleManager lcm = registryService.getBusinessLifeCycleManager();

        if (LifeCycleManager.CLASSIFICATION_SCHEME.equalsIgnoreCase(objectType)) {

            try {

                TModelDetail tmodeldetail = registry.getTModelDetail(id);
                Concept c = ScoutUddiV3JaxrHelper.getConcept(tmodeldetail, lcm);

                /*
                 * now turn into a concrete ClassificationScheme
                 */

                ClassificationScheme scheme = new ClassificationSchemeImpl(lcm);

                scheme.setName(c.getName());
                scheme.setDescription(c.getDescription());
                scheme.setKey(c.getKey());

                return scheme;
            }
            catch (RegistryV3Exception e) {
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        else if (LifeCycleManager.ORGANIZATION.equalsIgnoreCase(objectType)) {        	
            try
            {
                BusinessDetail orgdetail = registry.getBusinessDetail(id);
                return ScoutUddiV3JaxrHelper.getOrganization(orgdetail, lcm);
            }
            catch (RegistryV3Exception e) {
                throw new JAXRException(e.getLocalizedMessage());
            }

        }
        else if (LifeCycleManager.CONCEPT.equalsIgnoreCase(objectType)) {

            try
            {
                TModelDetail tmodeldetail = registry.getTModelDetail(id);
                return ScoutUddiV3JaxrHelper.getConcept(tmodeldetail, lcm);
            }
            catch (RegistryV3Exception e) { 
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        else if (LifeCycleManager.SERVICE.equalsIgnoreCase(objectType)) {

            try {
                ServiceDetail sd = registry.getServiceDetail(id);
                if (sd != null && sd.getBusinessService()!=null) {
                    for (BusinessService businessService : sd.getBusinessService()) {
                    	Service service = getServiceFromBusinessService(businessService, lcm);
                    	return service;
					}
                }
            }
            catch (RegistryV3Exception e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    /**
     *  Helper routine to take a jUDDI business service and turn into a useful
     *  Service.  Needs to go back to the registry to get the organization to
     *  properly hydrate the Service
     *
     * @param bs  BusinessService object to turn in to a Service
     * @param lcm manager to use
     * @return new Service object
     * @throws JAXRException
     */
    protected Service getServiceFromBusinessService(BusinessService bs, LifeCycleManager lcm)
        throws JAXRException {

        ServiceImpl service  = (ServiceImpl) ScoutUddiV3JaxrHelper.getService(bs, lcm);
        service.setSubmittingOrganizationKey(bs.getBusinessKey());

        return service;
    }

    /**
     * Gets the RegistryObjects owned by the caller. The objects
     * are returned as their concrete type (e.g. Organization, User etc.)
     *
     *  TODO - need to figure out what the set are.  This is just to get some
     *  basic functionality
     *
     * @return BulkResponse
     * @throws JAXRException
     */
    public BulkResponse getRegistryObjects() throws JAXRException
    {
        String types[] = {
            LifeCycleManager.ORGANIZATION,
            LifeCycleManager.SERVICE};

        LinkedHashSet<Object> c = new LinkedHashSet<Object>();

        for (int i = 0; i < types.length; i++) {
            try {
                BulkResponse bk = getRegistryObjects(types[i]);

                if (bk.getCollection() != null) {
                    c.addAll(bk.getCollection());
                }
            } catch(JAXRException e) {
            	log.debug("ignore - just a problem with that type? " + e.getMessage(), e);
            }
        }

        return new BulkResponseImpl(c);
    }

    public BulkResponse getRegistryObjects(Collection objectKeys) throws JAXRException
    {
        throw new UnsupportedCapabilityException();
    }

    public BulkResponse getRegistryObjects(Collection objectKeys, String objectType) throws JAXRException
    {
        IRegistryV3 registry = (IRegistryV3) registryService.getRegistry();
        //Convert into a vector of strings
        String[] keys = new String[objectKeys.size()];
        int currLoc = 0;
        for (Key key : (Collection<Key>) objectKeys) {
            keys[currLoc] = key.getId();
            currLoc++;
        }
        LinkedHashSet<RegistryObject> col = new LinkedHashSet<RegistryObject>();
        LifeCycleManager lcm = registryService.getLifeCycleManagerImpl();

        if (LifeCycleManager.CLASSIFICATION_SCHEME.equalsIgnoreCase(objectType))
        {
            try
            {
                TModelDetail tmodeldetail = registry.getTModelDetail(keys);
                List<TModel> tmodelList = tmodeldetail.getTModel();

                for (TModel tModel: tmodelList)
                {
                    col.add(ScoutUddiV3JaxrHelper.getConcept(tModel, lcm));
                }

            } catch (RegistryV3Exception e)
            { 
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        else if (LifeCycleManager.ORGANIZATION.equalsIgnoreCase(objectType))
        {
        	ConnectionImpl con = ((RegistryServiceImpl)getRegistryService()).getConnection();
            AuthToken auth = this.getAuthToken(con,registry);
        	
            try
            {
            	RegisteredInfo ri = registry.getRegisteredInfo(auth.getAuthInfo());
                if (ri != null) {
                    BusinessInfos infos = ri.getBusinessInfos();
                    if (infos != null) {
                        List<BusinessInfo> bizInfoList = infos.getBusinessInfo();
                        for (BusinessInfo businessInfo: bizInfoList) {
                            BusinessDetail detail = registry.getBusinessDetail(businessInfo.getBusinessKey());
                            col.add(((BusinessLifeCycleManagerV3Impl)registryService.getLifeCycleManagerImpl()).createOrganization(detail));
                        }
                    }
                }
            } catch (RegistryV3Exception e) {
                    throw new JAXRException(e.getLocalizedMessage());
            }
        }
        else if (LifeCycleManager.CONCEPT.equalsIgnoreCase(objectType))
        {
            try {
                TModelDetail tmodeldetail = registry.getTModelDetail(keys);
                List<TModel> tmodelList = tmodeldetail.getTModel();

                for (TModel tmodel: tmodelList)
                {
                    col.add(ScoutUddiV3JaxrHelper.getConcept(tmodel, lcm));
                }

            }
            catch (RegistryV3Exception e)
            { 
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        else if (LifeCycleManager.SERVICE.equalsIgnoreCase(objectType)) {

            try {
                ServiceDetail serviceDetail = registry.getServiceDetail(keys);

                if (serviceDetail != null) {
                    List<BusinessService> bizServiceList = serviceDetail.getBusinessService();

                    for (BusinessService businessService: bizServiceList) {

                        Service service = getServiceFromBusinessService(businessService, lcm);
                        
                        col.add(service);
                    }
                }
            }
            catch (RegistryV3Exception e) {
                throw new JAXRException(e);
            }
        }
        else {
            throw new JAXRException("Unsupported type " + objectType +
                    " for getRegistryObjects() in Apache Scout");
        }

        return new BulkResponseImpl(col);

    }

    public BulkResponse getRegistryObjects(String id) throws JAXRException
    {
        if (LifeCycleManager.ORGANIZATION.equalsIgnoreCase(id)) {
            IRegistryV3 registry = (IRegistryV3) registryService.getRegistry();
        	ConnectionImpl con = ((RegistryServiceImpl)getRegistryService()).getConnection();
            AuthToken auth = this.getAuthToken(con,registry);
    		LinkedHashSet<Organization> orgs = null;
            try
            {
            	RegisteredInfo ri = registry.getRegisteredInfo(auth.getAuthInfo());
            	if (ri != null && ri.getBusinessInfos()!=null) {
            		List<BusinessInfo> bizInfoList = ri.getBusinessInfos().getBusinessInfo();
            		orgs = new LinkedHashSet<Organization>();
            		for (BusinessInfo businessInfo : bizInfoList) {
            			BusinessDetail detail = registry.getBusinessDetail(businessInfo.getBusinessKey());
                        orgs.add(((BusinessLifeCycleManagerV3Impl)registryService.getLifeCycleManagerImpl()).createOrganization(detail));
					}
            	}
            	
            } catch (RegistryV3Exception re) {
            	throw new JAXRException(re);
            }
            return new BulkResponseImpl(orgs);
        }
        else if (LifeCycleManager.SERVICE.equalsIgnoreCase(id)) {
            List<String> a = new ArrayList<String>();
            a.add("%");

            BulkResponse br = this.findServices(null,null, a, null, null);

            return br;
        }
        else
        {
            throw new JAXRException("Unsupported type for getRegistryObjects() :" + id);
        }

    }

    static FindQualifiers mapFindQualifiers(Collection jaxrQualifiers) throws UnsupportedCapabilityException
    {
        if (jaxrQualifiers == null)
        {
            return null;
        }
        FindQualifiers result = objectFactory.createFindQualifiers();
        for (Iterator i = jaxrQualifiers.iterator(); i.hasNext();)
        {
            String jaxrQualifier = (String) i.next();
            String juddiQualifier = jaxrQualifier;
            if (juddiQualifier == null)
            {
                throw new UnsupportedCapabilityException("jUDDI does not support FindQualifer: " + jaxrQualifier);
            }
            result.getFindQualifier().add(juddiQualifier);
        }
        return result;
    }

    static Name[] mapNamePatterns(Collection namePatterns)
        throws JAXRException
    {
        if (namePatterns == null)
            return null;
        Name[] result = new Name[namePatterns.size()];
        int currLoc = 0;
        for (Iterator i = namePatterns.iterator(); i.hasNext();)
        {
            Object obj = i.next();
            Name name = objectFactory.createName();
            if (obj instanceof String) {
                name.setValue((String)obj);
            } else if (obj instanceof LocalizedString) {
                LocalizedString ls = (LocalizedString)obj;
                name.setValue(ls.getValue());
                name.setLang(ls.getLocale().getLanguage());
            }
            result[currLoc] = name;
            currLoc++;
        }
        return result;
    }

   /**
     * Get the Auth Token from the registry
     *
     * @param connection
     * @param ireg
     * @return auth token
     * @throws JAXRException
     */
    private AuthToken getAuthToken(ConnectionImpl connection, IRegistryV3 ireg)
            throws JAXRException {
        Set creds = connection.getCredentials();
        Iterator it = creds.iterator();
        String username = "", pwd = "";
        while (it.hasNext()) {
            PasswordAuthentication pass = (PasswordAuthentication) it.next();
            username = pass.getUserName();
            pwd = new String(pass.getPassword());
        }
        AuthToken token = null;
        try {
            token = ireg.getAuthToken(username, pwd);
        }
        catch (Exception e) {
            throw new JAXRException(e);
        }
        return token;
    }
}
