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

import org.apache.ws.scout.registry.infomodel.ClassificationSchemeImpl;
import org.apache.ws.scout.registry.infomodel.ConceptImpl;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;
import org.apache.ws.scout.registry.infomodel.KeyImpl;
import org.apache.ws.scout.registry.infomodel.ServiceBindingImpl;
import org.apache.ws.scout.registry.infomodel.AssociationImpl;
import org.apache.ws.scout.util.EnumerationHelper;
import org.apache.ws.scout.util.ScoutUddiJaxrHelper;

import uddiOrgApiV2.AssertionStatusItem;
import uddiOrgApiV2.AssertionStatusReport;
import uddiOrgApiV2.AuthToken;
import uddiOrgApiV2.BindingDetail;
import uddiOrgApiV2.BindingTemplate;
import uddiOrgApiV2.BusinessDetail;
import uddiOrgApiV2.BusinessEntity;
import uddiOrgApiV2.BusinessInfo;
import uddiOrgApiV2.BusinessList;
import uddiOrgApiV2.BusinessService;
import uddiOrgApiV2.FindQualifiers;
import uddiOrgApiV2.KeyedReference;
import uddiOrgApiV2.Name;
import uddiOrgApiV2.PublisherAssertion;
import uddiOrgApiV2.PublisherAssertions;
import uddiOrgApiV2.ServiceDetail;
import uddiOrgApiV2.ServiceInfo;
import uddiOrgApiV2.ServiceInfos;
import uddiOrgApiV2.ServiceList;
import uddiOrgApiV2.TModel;
import uddiOrgApiV2.TModelDetail;
import uddiOrgApiV2.TModelInfo;
import uddiOrgApiV2.TModelInfos;
import uddiOrgApiV2.TModelList;

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
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Implements the JAXR BusinessQueryManager Interface
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * @author <a href="mailto:jboynes@apache.org">Jeremy Boynes</a>
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
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

    /**
     * Finds organizations in the registry that match the specified parameters
     *
     * @param findQualifiers
     * @param namePatterns
     * @param classifications
     * @param specifications
     * @param externalIdentifiers
     * @param externalLinks
     * @return
     * @throws JAXRException
     */
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
            Name[] nameArray = mapNamePatterns(namePatterns);
            BusinessList result = registry.findBusiness(nameArray,
                    null, null, null, null,
                    juddiFindQualifiers,
                    registryService.getMaxRows());
            BusinessInfo[] a = result.getBusinessInfos() != null ? result.getBusinessInfos().getBusinessInfoArray() : null;

            Collection orgs = new ArrayList();
            int len = 0;
            if (a != null)
            {
                len = a.length;
                orgs = new ArrayList(len);
            }
            for (int i = 0; i < len; i++)
            {
                BusinessInfo info = a[i];
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
        //TODO: Currently we just return all the Association objects owned by the caller
        IRegistry registry = registryService.getRegistry();
        try
        {
            ConnectionImpl con = ((RegistryServiceImpl)getRegistryService()).getConnection();
            AuthToken auth = this.getAuthToken(con,registry);
            PublisherAssertions result =
                    registry.getPublisherAssertions(auth.getAuthInfo());
            PublisherAssertion[] a = result.getPublisherAssertionArray();

            Collection col = null;
            int len = 0;
            if (a != null)
            {
                len = a.length;
                col = new ArrayList(len);
            }
            for (int i = 0; i < len; i++)
            {
                PublisherAssertion pas = a[i];
                String sourceKey = pas.getFromKey();
                String targetKey = pas.getToKey();
                Collection orgcol = new ArrayList();
                orgcol.add(new KeyImpl(sourceKey));
                orgcol.add(new KeyImpl(targetKey));
                BulkResponse bl = getRegistryObjects(orgcol, LifeCycleManager.ORGANIZATION);
                Association asso = ScoutUddiJaxrHelper.getAssociation(bl.getCollection(),
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
        } catch (RegistryException e)
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
        IRegistry registry = registryService.getRegistry();
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
            AssertionStatusItem[] a = report.getAssertionStatusItemArray();
            Collection col = new ArrayList();
            int len = 0;
            if (a != null)
            {
                len = a.length;
                col = new ArrayList(len);
            }
            for (int i = 0; i < len; i++)
            {
                AssertionStatusItem asi = a[i];
                String sourceKey = asi.getFromKey();
                String targetKey = asi.getToKey();
                Collection orgcol = new ArrayList();
                orgcol.add(new KeyImpl(sourceKey));
                orgcol.add(new KeyImpl(targetKey));
                BulkResponse bl = getRegistryObjects(orgcol, LifeCycleManager.ORGANIZATION);
                Association asso = ScoutUddiJaxrHelper.getAssociation(bl.getCollection(),
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
        } catch (RegistryException e)
        {
            throw new JAXRException(e);
        }
    }

    /**
     *  TODO - need to support the qualifiers
     *
     * @param findQualifiers
     * @param namePatterns
     * @return
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
                IRegistry registry = registryService.getRegistry();
                FindQualifiers juddiFindQualifiers = mapFindQualifiers(findQualifiers);
                try
                {
                    //We are looking for one exact match, so getting upto 3 records is fine
                    TModelList list = registry.findTModel(namePatterns, null, null, juddiFindQualifiers, 3);
                    TModelInfos infos = null;
                    TModelInfo[] tmarr = null;
                    if (list != null) infos = list.getTModelInfos();
                    if (infos != null) tmarr = infos.getTModelInfoArray();
                    if (tmarr != null && tmarr.length > 0)
                    {
                        if (tmarr.length > 1)
                            throw new InvalidRequestException("Multiple matches found");

                        TModelInfo info = tmarr[0];
                        scheme = new ClassificationSchemeImpl(registryService.getLifeCycleManagerImpl());
                        scheme.setName(new InternationalStringImpl(info.getName().getStringValue()));
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
        //TODO: Handle this better
        Collection col = new ArrayList();
        Iterator iter = namePatterns.iterator();
        String name = "";
        while(iter.hasNext())
        {
          name = (String)iter.next();
          break;
        }

        col.add(this.findClassificationSchemeByName(findQualifiers,name));
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
                TModelInfo[] tmarr = null;
                if (list != null) infos = list.getTModelInfos();
                if (infos != null) tmarr = infos.getTModelInfoArray();
                for (int i = 0; tmarr != null && i < tmarr.length; i++)
                {
                    TModelInfo info = tmarr[i];
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
        throw new UnsupportedCapabilityException();
    }

    public BulkResponse findServiceBindings(Key serviceKey,
                                            Collection findQualifiers,
                                            Collection classifications,
                                            Collection specifications) throws JAXRException
    {
        BulkResponseImpl blkRes = new BulkResponseImpl();

        IRegistry iRegistry = registryService.getRegistry();
        FindQualifiers juddiFindQualifiers = mapFindQualifiers(findQualifiers);

        try
        {
 
            BindingDetail l = iRegistry.findBinding(serviceKey.getId(),null,null,juddiFindQualifiers,registryService.getMaxRows());

            /*
             * now convert  from jUDDI ServiceInfo objects to JAXR Services
             */
            if (l != null) {

                BindingTemplate[] bindarr= l.getBindingTemplateArray();
                Collection col = new ArrayList();

                for (int i=0; bindarr != null && i < bindarr.length; i++) {
                    BindingTemplate si = bindarr[i];
                    ServiceBinding sb =  ScoutUddiJaxrHelper.getServiceBinding(si,
                            registryService.getBusinessLifeCycleManager());
                    col.add(sb);
                   //Fill the Service object by making a call to registry
                   Service s = (Service)getRegistryObject(serviceKey.getId(), LifeCycleManager.SERVICE);
                   ((ServiceBindingImpl)sb).setService(s);
                }

                blkRes.setCollection(col);
            }
        }
        catch (RegistryException e) {
            e.printStackTrace();
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
     * @return
     * @throws JAXRException
     */
    public BulkResponse findServices(Key orgKey, Collection findQualifiers,
                                     Collection namePatterns,
                                     Collection classifications,
                                     Collection specificationa) throws JAXRException
    {
        BulkResponseImpl blkRes = new BulkResponseImpl();

        IRegistry iRegistry = registryService.getRegistry();
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

            ServiceList l = iRegistry.findService(id, juddiNames,
                    null, null, juddiFindQualifiers, registryService.getMaxRows());

            /*
             * now convert  from jUDDI ServiceInfo objects to JAXR Services
             */
            if (l != null) {

                ServiceInfos serviceInfos = l.getServiceInfos();

                ServiceInfo[] a = (serviceInfos != null ? serviceInfos.getServiceInfoArray() : null);

                Collection col = new ArrayList();

                for (int i=0; a != null && i < a.length; i++) {
                    ServiceInfo si = (ServiceInfo) a[i];
                    col.add(ScoutUddiJaxrHelper.getService(si,
                            registryService.getBusinessLifeCycleManager()));
                }

                blkRes.setCollection(col);
            }
        }
        catch (RegistryException e) {
            e.printStackTrace();
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
        IRegistry registry = registryService.getRegistry();
        BusinessLifeCycleManager lcm = registryService.getBusinessLifeCycleManager();

        if (LifeCycleManager.CLASSIFICATION_SCHEME.equalsIgnoreCase(objectType)) {

            try {

                TModelDetail tmodeldetail = registry.getTModelDetail(id);
                Concept c = ScoutUddiJaxrHelper.getConcept(tmodeldetail, lcm);

                /*
                 * now turn into a concrete ClassificationScheme
                 */

                ClassificationScheme scheme = new ClassificationSchemeImpl(lcm);

                scheme.setName(c.getName());
                scheme.setDescription(c.getDescription());
                scheme.setKey(c.getKey());

                return scheme;
            }
            catch (RegistryException e) {
                e.printStackTrace();
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        else if (LifeCycleManager.ORGANIZATION.equalsIgnoreCase(objectType)) {

            try
            {
                BusinessDetail orgdetail = registry.getBusinessDetail(id);
                return ScoutUddiJaxrHelper.getOrganization(orgdetail, lcm);
            }
            catch (RegistryException e) {
                e.printStackTrace();
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        else if (LifeCycleManager.CONCEPT.equalsIgnoreCase(objectType)) {

            try
            {
                TModelDetail tmodeldetail = registry.getTModelDetail(id);
                return ScoutUddiJaxrHelper.getConcept(tmodeldetail, lcm);
            }
            catch (RegistryException e) {
                e.printStackTrace();
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        else if (LifeCycleManager.SERVICE.equalsIgnoreCase(objectType)) {

            try {

               
                ServiceDetail sd = registry.getServiceDetail(id);

                if (sd != null) {

                    BusinessService[] a = sd.getBusinessServiceArray();

                    if (a != null && a.length != 0) {
                        Service service = getServiceFromBusinessService(a[0], lcm);

                        return service;
                    }
                }
            }
            catch (RegistryException e) {
                e.printStackTrace();
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

        Service service  = ScoutUddiJaxrHelper.getService(bs, lcm);

        /*
         * now get the Organization if we can
         */

        String busKey = bs.getBusinessKey();

        if (busKey != null) {
            Organization o = (Organization) getRegistryObject(busKey,
                    LifeCycleManager.ORGANIZATION);
            service.setProvidingOrganization(o);
        }

        return service;
    }

    /**
     * Gets the RegistryObjects owned by the caller. The objects
     * are returned as their concrete type (e.g. Organization, User etc.)
     *
     *  TODO - need to figure out what the set are.  This is just to get some
     *  basic functionality
     *
     * @return
     * @throws JAXRException
     */
    public BulkResponse getRegistryObjects() throws JAXRException
    {
        String types[] = {
            LifeCycleManager.ORGANIZATION,
            LifeCycleManager.SERVICE};

        Collection c = new ArrayList();

        for (int i = 0; i < types.length; i++) {
            try {
                BulkResponse bk = getRegistryObjects(types[i]);

                if (bk.getCollection() != null) {
                    c.addAll(bk.getCollection());
                }
            }
            catch(JAXRException e) {
                // ignore - just a problem with that type?
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
        IRegistry registry = registryService.getRegistry();
        //Convert into a vector of strings
        String[] keys = new String[objectKeys.size()];
        int currLoc = 0;
        Iterator iter = objectKeys.iterator();
        while(iter.hasNext())
        {
            Key key = (Key)iter.next();
            keys[currLoc] = key.getId();
            currLoc++;
        }
        Collection col = new ArrayList();
        LifeCycleManager lcm = registryService.getLifeCycleManagerImpl();

        if (LifeCycleManager.CLASSIFICATION_SCHEME.equalsIgnoreCase(objectType))
        {
            try
            {
                TModelDetail tmodeldetail = registry.getTModelDetail(keys);
                TModel[] tmarray = tmodeldetail.getTModelArray();
                for (int i = 0; tmarray != null && i < tmarray.length; i++)
                {
                    col.add(ScoutUddiJaxrHelper.getConcept(tmarray[i], lcm));
                }

            } catch (RegistryException e)
            {
                e.printStackTrace();
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        else if (LifeCycleManager.ORGANIZATION.equalsIgnoreCase(objectType))
        {
            //Get the Organization from the uddi registry
            try
            {
                BusinessDetail orgdetail = registry.getBusinessDetail(keys);
                BusinessEntity[] bizarr = orgdetail.getBusinessEntityArray();
                for (int i = 0; bizarr != null && i < bizarr.length; i++)
                {
                    col.add(ScoutUddiJaxrHelper.getOrganization(bizarr[i], lcm));
                }
            } catch (RegistryException e)
            {
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        else if (LifeCycleManager.CONCEPT.equalsIgnoreCase(objectType))
        {
            try {
                TModelDetail tmodeldetail = registry.getTModelDetail(keys);
                TModel[] tmarr = tmodeldetail.getTModelArray();
                for (int i = 0; tmarr != null && i < tmarr.length; i++)
                {
                    col.add(ScoutUddiJaxrHelper.getConcept(tmarr[i], lcm));
                }

            }
            catch (RegistryException e)
            {
                e.printStackTrace();
                throw new JAXRException(e.getLocalizedMessage());
            }
        }
        else if (LifeCycleManager.SERVICE.equalsIgnoreCase(objectType)) {

            try {
                ServiceDetail serviceDetail = registry.getServiceDetail(keys);

                if (serviceDetail != null) {

                    BusinessService[] a = serviceDetail.getBusinessServiceArray();

                    for (int i=0; a != null && i < a.length; i++) {

                        Service service = getServiceFromBusinessService(a[i], lcm);

                        col.add(service);
                    }
                }
            }
            catch (RegistryException e) {
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
            List a = new ArrayList();
            a.add("%");

            BulkResponse br = findOrganizations(null, a, null, null, null, null);

            return br;
        }
        else if (LifeCycleManager.SERVICE.equalsIgnoreCase(id)) {
            List a = new ArrayList();
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
        FindQualifiers result = FindQualifiers.Factory.newInstance();
        for (Iterator i = jaxrQualifiers.iterator(); i.hasNext();)
        {
            String jaxrQualifier = (String) i.next();
            String juddiQualifier = jaxrQualifier;
            if (juddiQualifier == null)
            {
                throw new UnsupportedCapabilityException("jUDDI does not support FindQualifer: " + jaxrQualifier);
            }
            result.addFindQualifier(juddiQualifier);
        }
        return result;
    }

    static Name[] mapNamePatterns(Collection namePatterns)
    {
        if (namePatterns == null)
            return null;
        Name[] result = new Name[namePatterns.size()];
        int currLoc = 0;
        for (Iterator i = namePatterns.iterator(); i.hasNext();)
        {
            String pattern = (String) i.next();
            Name n = Name.Factory.newInstance();
            n.setStringValue(pattern);
            result[currLoc] = n;
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
    private AuthToken getAuthToken(ConnectionImpl connection, IRegistry ireg)
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
