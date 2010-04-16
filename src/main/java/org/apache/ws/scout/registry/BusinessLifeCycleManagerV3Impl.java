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

import java.io.Serializable;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.DeleteException;
import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.RegistryService;
import javax.xml.registry.SaveException;
import javax.xml.registry.UnexpectedObjectException;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.scout.registry.infomodel.ConceptImpl;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;
import org.apache.ws.scout.registry.infomodel.KeyImpl;
import org.apache.ws.scout.registry.infomodel.OrganizationImpl;
import org.apache.ws.scout.registry.infomodel.ServiceImpl;
import org.apache.ws.scout.util.ScoutJaxrUddiV3Helper;
import org.apache.ws.scout.util.ScoutUddiV3JaxrHelper;
import org.uddi.api_v3.AssertionStatusItem;
import org.uddi.api_v3.AssertionStatusReport;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.ErrInfo;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ObjectFactory;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.PublisherAssertions;
import org.uddi.api_v3.Result;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;

/**
 * Implements JAXR BusinessLifeCycleManager Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class BusinessLifeCycleManagerV3Impl extends LifeCycleManagerImpl
        implements BusinessLifeCycleManager, Serializable {
		
	private static final long serialVersionUID = -1145007155334678356L;

	private Log log = LogFactory.getLog(this.getClass());
    
    private transient ObjectFactory objectFactory = new ObjectFactory();
	
    private static Hashtable cachedAuthTokenHash = null;
    
    public BusinessLifeCycleManagerV3Impl(RegistryService registry) {
        super(registry);
        if(objectFactory == null)
        	objectFactory = new ObjectFactory();
        if (cachedAuthTokenHash == null) 
        	cachedAuthTokenHash = new Hashtable();
    }

    /**
     * Deletes one or more previously submitted objects from the registry
     * using the object keys and a specified objectType attribute.
     *
     * @param keys
     * @param objectType
     * @return BulkResponse object
     * @throws JAXRException
     */
    public BulkResponse deleteObjects(Collection keys, String objectType) throws JAXRException {
        BulkResponse bulk = null;

        if (objectType == LifeCycleManager.ASSOCIATION) {
            bulk = this.deleteAssociations(keys);
        }
        else if (objectType == LifeCycleManager.CLASSIFICATION_SCHEME) {
            bulk = this.deleteClassificationSchemes(keys);
        }
        else if (objectType == LifeCycleManager.CONCEPT) {
            bulk = this.deleteConcepts(keys);
        }
        else if (objectType == LifeCycleManager.ORGANIZATION) {
            bulk = this.deleteOrganizations(keys);
        }
        else if (objectType == LifeCycleManager.SERVICE) {
            bulk = this.deleteServices(keys);
        }
        else if (objectType == LifeCycleManager.SERVICE_BINDING) {
            bulk = this.deleteServiceBindings(keys);
        }
        else {
            throw new JAXRException("Delete Operation for " + objectType + " not implemented by Scout");
        }

        return bulk;
    }

    public BulkResponse deleteAssociations(Collection associationKeys) throws JAXRException {
        return this.deleteOperation(associationKeys, "DELETE_ASSOCIATION");
    }

    public BulkResponse deleteClassificationSchemes(Collection schemeKeys) throws JAXRException {
        return this.deleteOperation(schemeKeys, "DELETE_CLASSIFICATIONSCHEME");
    }

    public BulkResponse deleteConcepts(Collection conceptKeys) throws JAXRException {
        return this.deleteOperation(conceptKeys, "DELETE_CONCEPT");
    }

    public BulkResponse deleteOrganizations(Collection orgkeys) throws JAXRException {
        return this.deleteOperation(orgkeys, "DELETE_ORG");
    }

    public BulkResponse deleteServiceBindings(Collection bindingKeys) throws JAXRException {
        return this.deleteOperation(bindingKeys, "DELETE_SERVICEBINDING");
    }

    public BulkResponse deleteServices(Collection serviceKeys) throws JAXRException {
        return this.deleteOperation(serviceKeys, "DELETE_SERVICE");
    }

    /**
     * Saves one or more Objects to the registry. An object may be a
     * RegistryObject  subclass instance. If an object is not in the registry,
     * it is created in the registry.  If it already exists in the registry
     * and has been modified, then its  state is updated (replaced) in the
     * registry
     * <p/>
     * TODO:Check if juddi can provide a facility to store a collection of heterogenous
     * objects
     * <p/>
     * TODO - does this belong here?  it's really an overload of
     * LifecycleManager.saveObjects, but all the help we need
     * like saveOrganization() is up here...
     *
     * @param col
     * @return a BulkResponse containing the Collection of keys for those objects
     *         that were saved successfully and any SaveException that was encountered
     *         in case of partial commit
     * @throws JAXRException
     */
    public BulkResponse saveObjects(Collection col) throws JAXRException {

        Iterator iter = col.iterator();

        LinkedHashSet<Object> suc = new LinkedHashSet<Object>();
        Collection<Exception> exc = new ArrayList<Exception>();

        while (iter.hasNext()) {
            RegistryObject reg = (RegistryObject) iter.next();

            BulkResponse br = null;

            Collection<RegistryObject> c = new ArrayList<RegistryObject>();
            c.add(reg);

            if (reg instanceof javax.xml.registry.infomodel.Association) {
                br = saveAssociations(c, true);
            }
            else if (reg instanceof javax.xml.registry.infomodel.ClassificationScheme) {
                br = saveClassificationSchemes(c);
            }
            else if (reg instanceof javax.xml.registry.infomodel.Concept) {
                br = saveConcepts(c);
            }
            else if (reg instanceof javax.xml.registry.infomodel.Organization) {
                br = saveOrganizations(c);
            }
            else if (reg instanceof javax.xml.registry.infomodel.Service) {
                br = saveServices(c);
            }
            else if (reg instanceof javax.xml.registry.infomodel.ServiceBinding) {
                br = saveServiceBindings(c);
            }
            else {
                throw new JAXRException("Delete Operation for " + reg.getClass() 
                        + " not implemented by Scout");
            }

            if (br.getCollection() != null) {
                suc.addAll(br.getCollection());
            }

            if (br.getExceptions() != null) {
                exc.addAll(br.getExceptions());
            }
        }

        BulkResponseImpl bulk = new BulkResponseImpl();

        /*
         *  TODO - what is the right status?
         */
        bulk.setStatus(JAXRResponse.STATUS_SUCCESS);

        bulk.setCollection(suc);
        bulk.setExceptions(exc);

        return bulk;
    }


    public BulkResponse saveAssociations(Collection associations, boolean replace) throws JAXRException {
        BulkResponseImpl bulk = new BulkResponseImpl();
        PublisherAssertion[] sarr = new PublisherAssertion[associations.size()];

        Collection<Key> coll = new ArrayList<Key>();
        Collection<Exception> exceptions = new ArrayList<Exception>();

        Iterator iter = associations.iterator();
        int currLoc = 0;
        while (iter.hasNext()) {
            
                Association association = (Association) iter.next();
                association.getSourceObject();
                PublisherAssertion pa = ScoutJaxrUddiV3Helper.getPubAssertionFromJAXRAssociation(association);
                sarr[currLoc] = pa;
                currLoc++;
            
                // Save PublisherAssertion
                PublisherAssertions bd = null;
                try {
                    bd = (PublisherAssertions) executeOperation(sarr, "SAVE_ASSOCIATION");
                }
                catch (RegistryV3Exception e) {
                    exceptions.add(new SaveException(e));
                    bulk.setExceptions(exceptions);
                    bulk.setStatus(JAXRResponse.STATUS_FAILURE);
                    return bulk;
                }
                if(bd != null)
                {
                	List<PublisherAssertion> publisherAssertionList = bd.getPublisherAssertion();
                	PublisherAssertion[] keyarr = new PublisherAssertion[publisherAssertionList.size()];
                	publisherAssertionList.toArray(keyarr);
                	
                	for (int i = 0; keyarr != null && i < keyarr.length; i++) {
                		PublisherAssertion result = (PublisherAssertion) keyarr[i];
                        KeyedReference keyr = result.getKeyedReference();
                        Concept c = new ConceptImpl(getRegistryService().getBusinessLifeCycleManager());
                        c.setName(new InternationalStringImpl(keyr.getKeyName()));
                        c.setKey( new KeyImpl(keyr.getTModelKey()) );
                        c.setValue(keyr.getKeyValue());
                        association.setAssociationType(c);
                        coll.add(association.getKey());
                   }
                }
        }
        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveClassificationSchemes(Collection schemes) throws JAXRException {
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        TModel[] entityarr = new TModel[schemes.size()];

        LinkedHashSet<Key> coll = new LinkedHashSet<Key>();
        Collection<Exception> exceptions = new ArrayList<Exception>();

        Iterator iter = schemes.iterator();
        int currLoc = 0;
        while (iter.hasNext()) {
            try {
                TModel en =
                		ScoutJaxrUddiV3Helper.getTModelFromJAXRClassificationScheme((ClassificationScheme) iter.next());
                entityarr[currLoc] = en;
                currLoc++;
            }
            catch (ClassCastException ce) {
                throw new UnexpectedObjectException();
            }
        }
        log.debug("Method:save_classificationscheme: ENlength=" + entityarr.length);
        // Save business
        TModelDetail td = null;
        try {
            td = (TModelDetail) executeOperation(entityarr, "SAVE_TMODEL");
        }
        catch (RegistryV3Exception e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        List<TModel> tmodelList = td.getTModel();
        entityarr = new TModel[tmodelList.size()];
        tmodelList.toArray(entityarr); 
        log.debug("After Saving TModel. Obtained vector size:" + entityarr != null ? entityarr.length : 0);
        for (int i = 0; entityarr != null && i < entityarr.length; i++) {
            TModel tm = (TModel) entityarr[i];
            coll.add(new KeyImpl(tm.getTModelKey()));
        }

        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveConcepts(Collection concepts) throws JAXRException {
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        TModel[] entityarr = new TModel[concepts.size()];

        LinkedHashSet<Key> coll = new LinkedHashSet<Key>();
        Collection<Exception> exceptions = new ArrayList<Exception>();

        Iterator iter = concepts.iterator();
        int currLoc = 0;
        while (iter.hasNext()) {
            try {
                TModel en =
                	ScoutJaxrUddiV3Helper.getTModelFromJAXRConcept((Concept) iter.next());
                entityarr[currLoc] = en;
                currLoc++;
            }
            catch (ClassCastException ce) {
                throw new UnexpectedObjectException();
            }
        }
        log.debug("Method:save_concept: ENlength=" + entityarr.length);
        // Save business
        TModelDetail td = null;
        try {
            td = (TModelDetail) executeOperation(entityarr, "SAVE_TMODEL");
        }
        catch (RegistryV3Exception e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        List<TModel> tmodelList = td.getTModel();
        entityarr = new TModel[tmodelList.size()];
        tmodelList.toArray(entityarr);
        
        log.debug("After Saving TModel. Obtained vector size:" + entityarr != null ? entityarr.length : 0);
        for (int i = 0; entityarr != null && i < entityarr.length; i++) {
            TModel tm = (TModel) entityarr[i];
            coll.add(new KeyImpl(tm.getTModelKey()));
        }

        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveOrganizations(Collection organizations) throws JAXRException {
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        BusinessEntity[] entityarr = new BusinessEntity[organizations.size()];

        LinkedHashSet<Key> coll = new LinkedHashSet<Key>();
        Collection<Exception> exceptions = new ArrayList<Exception>();

        Iterator iter = organizations.iterator();
        int currLoc = 0;
        while (iter.hasNext()) {
            try {
                BusinessEntity en =
                	ScoutJaxrUddiV3Helper.getBusinessEntityFromJAXROrg((Organization) iter.next());
                entityarr[currLoc] = en;
                currLoc++;
            }
            catch (ClassCastException ce) {
                throw new UnexpectedObjectException();
            }
        }
        log.debug("Method:save_business: ENlength=" + entityarr.length);
        // Save business
        BusinessDetail bd = null;
        try {
            bd = (BusinessDetail) executeOperation(entityarr, "SAVE_ORG");
        }
        catch (RegistryV3Exception e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        List<BusinessEntity> bizEntityList = bd.getBusinessEntity();
        
        entityarr = new BusinessEntity[bizEntityList.size()];
        bizEntityList.toArray(entityarr);
        
        log.debug("After Saving Business. Obtained vector size:" + entityarr != null ? entityarr.length : 0);
        for (int i = 0; entityarr != null && i < entityarr.length; i++) {
            BusinessEntity entity = (BusinessEntity) entityarr[i];
            coll.add(new KeyImpl(entity.getBusinessKey()));
        }

        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveServiceBindings(Collection bindings) throws JAXRException {
        BulkResponseImpl bulk = new BulkResponseImpl();
        BindingTemplate[] sbarr = new BindingTemplate[bindings.size()];

        LinkedHashSet<Key> coll = new LinkedHashSet<Key>();
        Collection<Exception> exceptions = new ArrayList<Exception>();

        Iterator iter = bindings.iterator();
        int currLoc = 0;
        while (iter.hasNext()) {
            try {
                BindingTemplate bs = ScoutJaxrUddiV3Helper.getBindingTemplateFromJAXRSB((ServiceBinding) iter.next());
                sbarr[currLoc] = bs;
                currLoc++;
            }
            catch (ClassCastException ce) {
                throw new UnexpectedObjectException();
            }
        }
        // Save ServiceBinding
        BindingDetail bd = null;
        try {
            bd = (BindingDetail) executeOperation(sbarr, "SAVE_SERVICE_BINDING");
        }
        catch (RegistryV3Exception e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        List<BindingTemplate> bindingTemplateList = bd.getBindingTemplate();
        sbarr = new BindingTemplate[bindingTemplateList.size()];
        bindingTemplateList.toArray(sbarr);
        
        for (int i = 0; sbarr != null && i < sbarr.length; i++) {
            BindingTemplate bt = (BindingTemplate) sbarr[i];
            coll.add(new KeyImpl(bt.getBindingKey()));
        }
        if (coll.size()>0) {
            bulk.setCollection(coll);
        }
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveServices(Collection services) throws JAXRException {
        BulkResponseImpl bulk = new BulkResponseImpl();
        BusinessService[] sarr = new BusinessService[services.size()];

        LinkedHashSet<Key> coll = new LinkedHashSet<Key>();
        Collection<Exception> exceptions = new ArrayList<Exception>();


        Iterator iter = services.iterator();
        int currLoc = 0;
        while (iter.hasNext()) {
            try {
                BusinessService bs = ScoutJaxrUddiV3Helper.getBusinessServiceFromJAXRService((Service) iter.next());
                sarr[currLoc] = bs;
                currLoc++;
            }
            catch (ClassCastException ce) {
                throw new UnexpectedObjectException();
            }
        }
        // Save Service
        ServiceDetail sd = null;
        try {
            sd = (ServiceDetail) executeOperation(sarr, "SAVE_SERVICE");
        }
        catch (RegistryV3Exception e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        List<BusinessService> bizServiceList = sd.getBusinessService();
        sarr = new BusinessService[bizServiceList.size()];
        bizServiceList.toArray(sarr);
        
        for (int i = 0; sarr != null && i < sarr.length; i++) {
            BusinessService entity = (BusinessService) sarr[i];
            coll.add(new KeyImpl(entity.getServiceKey()));
        }
        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public void confirmAssociation(Association assoc) throws JAXRException, InvalidRequestException {
       //Store it in the UDDI registry
       HashSet<Association> col = new HashSet<Association>();
       col.add(assoc);
       BulkResponse br = this.saveAssociations(col, true);
       if(br.getExceptions()!= null)
          throw new JAXRException("Confiming the Association Failed");
    }

    public void unConfirmAssociation(Association assoc) throws JAXRException, InvalidRequestException {
       //TODO
       //Delete it from the UDDI registry
       Collection<Key> col = new ArrayList<Key>();
       col.add(assoc.getKey());
       BulkResponse br = this.deleteAssociations(col);
       if(br.getExceptions()!= null)
          throw new JAXRException("UnConfiming the Association Failed");
    }

    //Protected Methods
    protected Object executeOperation(Object dataarray, String op)
            throws RegistryV3Exception, JAXRException {
        if (registry == null) {
            throw new IllegalStateException("No registry");
        }

        IRegistryV3 ireg = (IRegistryV3) registry.getRegistry();
        
        ConnectionImpl connection = registry.getConnection();
        AuthToken token = getAuthToken(connection, ireg);
        if (token == null) {
            throw new IllegalStateException("No auth token returned");
        }

        Object regobj;
        if(op.equalsIgnoreCase("SAVE_ASSOCIATION"))
        { 	
        	try {
        		regobj = ireg.setPublisherAssertions(token.getAuthInfo(), (PublisherAssertion[]) dataarray);
        	} catch (RegistryV3Exception rve) {
        		String username = getUsernameFromCredentials(connection.getCredentials());
        		if (cachedAuthTokenHash.containsKey(username)) {
        			cachedAuthTokenHash.remove(username);
        		}
        		token = getAuthToken(connection, ireg);
        		regobj = ireg.setPublisherAssertions(token.getAuthInfo(), (PublisherAssertion[]) dataarray);
        	}
        } else if (op.equalsIgnoreCase("SAVE_SERVICE")) {
        	try {
        		regobj = ireg.saveService(token.getAuthInfo(), (BusinessService[])dataarray);
        	} catch (RegistryV3Exception rve) {
        		String username = getUsernameFromCredentials(connection.getCredentials());
        		if (cachedAuthTokenHash.containsKey(username)) {
        			cachedAuthTokenHash.remove(username);
        		}
        		token = getAuthToken(connection, ireg);
        		regobj = ireg.saveService(token.getAuthInfo(), (BusinessService[])dataarray);
        	}
        }
        else if (op.equalsIgnoreCase("SAVE_SERVICE_BINDING")) {
        	try {
        		regobj = ireg.saveBinding(token.getAuthInfo(), (BindingTemplate[]) dataarray);
        	} catch (RegistryV3Exception rve) {
        		String username = getUsernameFromCredentials(connection.getCredentials());
        		if (cachedAuthTokenHash.containsKey(username)) {
        			cachedAuthTokenHash.remove(username);
        		}
        		token = getAuthToken(connection, ireg);
        		regobj = ireg.saveBinding(token.getAuthInfo(), (BindingTemplate[]) dataarray);        		
        	}
        }
        else if (op.equalsIgnoreCase("SAVE_ORG")) {
        	try {
        		regobj = ireg.saveBusiness(token.getAuthInfo(), (BusinessEntity[]) dataarray);
        	} catch (RegistryV3Exception rve) {
        		String username = getUsernameFromCredentials(connection.getCredentials());
        		if (cachedAuthTokenHash.containsKey(username)) {
        			cachedAuthTokenHash.remove(username);
        		}
        		token = getAuthToken(connection, ireg);
        		regobj = ireg.saveBusiness(token.getAuthInfo(), (BusinessEntity[]) dataarray);
        	}
        }
        else if (op.equalsIgnoreCase("SAVE_TMODEL")) {
        	try {
        		regobj = ireg.saveTModel(token.getAuthInfo(), (TModel[]) dataarray);
        	} catch (RegistryV3Exception rve) {
        		String username = getUsernameFromCredentials(connection.getCredentials());
        		if (cachedAuthTokenHash.containsKey(username)) {
        			cachedAuthTokenHash.remove(username);
        		}
        		token = getAuthToken(connection, ireg);
        		regobj = ireg.saveTModel(token.getAuthInfo(), (TModel[]) dataarray);
        	}
        }
        else if (op.equalsIgnoreCase("DELETE_ORG")) {
            try {
                clearPublisherAssertions(token.getAuthInfo(), ireg);
            	regobj = ireg.deleteBusiness(token.getAuthInfo(), (String[]) dataarray);
        	} catch (RegistryV3Exception rve) {
        		String username = getUsernameFromCredentials(connection.getCredentials());
        		if (cachedAuthTokenHash.containsKey(username)) {
        			cachedAuthTokenHash.remove(username);
        		}
        		token = getAuthToken(connection, ireg);
                clearPublisherAssertions(token.getAuthInfo(), ireg);
        		regobj = ireg.deleteBusiness(token.getAuthInfo(), (String[]) dataarray);
        	}
        }
        else if (op.equalsIgnoreCase("DELETE_SERVICE")) {
        	try {
        		regobj = ireg.deleteService(token.getAuthInfo(), (String[]) dataarray);
	    	} catch (RegistryV3Exception rve) {
	    		String username = getUsernameFromCredentials(connection.getCredentials());
	    		if (cachedAuthTokenHash.containsKey(username)) {
	    			cachedAuthTokenHash.remove(username);
	    		}
	    		token = getAuthToken(connection, ireg);
	            clearPublisherAssertions(token.getAuthInfo(), ireg);
        		regobj = ireg.deleteService(token.getAuthInfo(), (String[]) dataarray);
	    	}
        }
        else if (op.equalsIgnoreCase("DELETE_SERVICEBINDING")) {
        	try	{
        		regobj = ireg.deleteBinding(token.getAuthInfo(), (String[]) dataarray);
	    	} catch (RegistryV3Exception rve) {
	    		String username = getUsernameFromCredentials(connection.getCredentials());
	    		if (cachedAuthTokenHash.containsKey(username)) {
	    			cachedAuthTokenHash.remove(username);
	    		}
	    		token = getAuthToken(connection, ireg);
	            clearPublisherAssertions(token.getAuthInfo(), ireg);
        		regobj = ireg.deleteBinding(token.getAuthInfo(), (String[]) dataarray);
	    	}
        }
        else if (op.equalsIgnoreCase("DELETE_CONCEPT")) {
            try {
            	regobj = ireg.deleteTModel(token.getAuthInfo(), (String[]) dataarray);
	    	} catch (RegistryV3Exception rve) {
	    		String username = getUsernameFromCredentials(connection.getCredentials());
	    		if (cachedAuthTokenHash.containsKey(username)) {
	    			cachedAuthTokenHash.remove(username);
	    		}
	    		token = getAuthToken(connection, ireg);
	            clearPublisherAssertions(token.getAuthInfo(), ireg);
            	regobj = ireg.deleteTModel(token.getAuthInfo(), (String[]) dataarray);
	    	}
        }
        else if (op.equalsIgnoreCase("DELETE_ASSOCIATION")) {
        	int len = ((String[]) dataarray).length;
            PublisherAssertion[] paarr = new PublisherAssertion[len];
            for(int i=0;i<len;i++)
            {
               String keystr = ((String[])dataarray)[i];
               paarr[i] = ScoutJaxrUddiV3Helper.getPubAssertionFromJAXRAssociationKey(keystr);
            }
            try {
            	regobj = ireg.deletePublisherAssertions(token.getAuthInfo(), paarr);
            } catch (RegistryV3Exception rve) {
	    		String username = getUsernameFromCredentials(connection.getCredentials());
	    		if (cachedAuthTokenHash.containsKey(username)) {
	    			cachedAuthTokenHash.remove(username);
	    		}
	    		token = getAuthToken(connection, ireg);
	            clearPublisherAssertions(token.getAuthInfo(), ireg);
            	regobj = ireg.deletePublisherAssertions(token.getAuthInfo(), paarr);
	    	}
        }
        else if (op.equalsIgnoreCase("DELETE_CLASSIFICATIONSCHEME")) {
        	try {
        		regobj = ireg.deleteTModel(token.getAuthInfo(), (String[]) dataarray);
	    	} catch (RegistryV3Exception rve) {
	    		String username = getUsernameFromCredentials(connection.getCredentials());
	    		if (cachedAuthTokenHash.containsKey(username)) {
	    			cachedAuthTokenHash.remove(username);
	    		}
	    		token = getAuthToken(connection, ireg);
	            clearPublisherAssertions(token.getAuthInfo(), ireg);
        		regobj = ireg.deleteTModel(token.getAuthInfo(), (String[]) dataarray);
	    	}	        		
        }
        else {
            throw new JAXRException("Unsupported operation:" + op);
        }

        return regobj;
    }

    private void clearPublisherAssertions( String authinfo,IRegistryV3 ireg)
    {
       Vector<PublisherAssertion> pasvect  = null;
       PublisherAssertion[] pasarr  = null;
       try
       {
          AssertionStatusReport report = ireg.getAssertionStatusReport(authinfo,"status:complete");
          List<AssertionStatusItem> assertionStatusItemList = report.getAssertionStatusItem();
          AssertionStatusItem[] assertionStatusItemArr = 
        	  new AssertionStatusItem[assertionStatusItemList.size()];

          int len = assertionStatusItemArr != null? assertionStatusItemArr.length : 0;
          for (int i = 0; i < len; i++)
          {
                AssertionStatusItem asi = assertionStatusItemArr[i];
               /* String sourceKey = asi.getFromKey();
                String targetKey = asi.getToKey();
                PublisherAssertion pa = new PublisherAssertion();
                pa.setFromKey(sourceKey);
                pa.setToKey(targetKey);
                KeyedReference keyr = asi.getKeyedReference();
                pa.setKeyedReference(keyr);
                pa.setTModelKey(keyr.getTModelKey());
                pa.setKeyName(keyr.getKeyName());
                pa.setKeyValue(keyr.getKeyValue());
                if(pasvect == null) pasvect = new Vector(len);
                pasvect.add(pa);*/
                if(pasvect == null) pasvect = new Vector<PublisherAssertion>(len);
                pasvect.add(this.getPublisherAssertion(asi));
           }
          report = ireg.getAssertionStatusReport(authinfo,"status:toKey_incomplete");
          assertionStatusItemArr = report.getAssertionStatusItem().toArray(assertionStatusItemArr);

          len = assertionStatusItemArr != null? assertionStatusItemArr.length : 0;
          for (int i = 0; i < len; i++)
          {
                AssertionStatusItem asi = (AssertionStatusItem) assertionStatusItemArr[i];
                if(pasvect == null) pasvect = new Vector<PublisherAssertion>(len);
                pasvect.add(this.getPublisherAssertion(asi));
          }

          report = ireg.getAssertionStatusReport(authinfo,"status:fromKey_incomplete");
          assertionStatusItemArr = report.getAssertionStatusItem().toArray(assertionStatusItemArr);

          len = assertionStatusItemArr != null? assertionStatusItemArr.length : 0;
          for (int i = 0; i < len; i++)
          {
                AssertionStatusItem asi = (AssertionStatusItem) assertionStatusItemArr[i];
                if(pasvect == null) pasvect = new Vector<PublisherAssertion>(len);
                pasvect.add(this.getPublisherAssertion(asi));
          }

          if (pasvect != null) {
        	  pasarr = new PublisherAssertion[pasvect.size()];
        	  Iterator iter = pasvect.iterator();
        	  int pasarrPos = 0;
        	  while (iter.hasNext()) {
        		  pasarr[pasarrPos] = ((PublisherAssertion) iter.next());
        		  pasarrPos++;
        	  }
          }
       }
       catch (RegistryV3Exception e)
       {
          throw new RuntimeException(e);
       }

          if(pasarr != null && pasarr.length > 0)
             try
             {
                ireg.deletePublisherAssertions(authinfo, pasarr);
             }
             catch (RegistryV3Exception e)
             { 
                log.debug("Ignoring exception " + e.getMessage(),e);
             }
       }



    protected BulkResponse deleteOperation(Collection<Key> keys, String op)
            throws JAXRException {
        if(keys == null)
        throw new JAXRException("Keys provided to "+op+" are null");
       
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        String[] keyarr = new String[keys.size()];
        Result[] keyResultArr;

        LinkedHashSet<Key> coll = new LinkedHashSet<Key>();
        Collection<Exception> exceptions = new ArrayList<Exception>();

        try {
            Iterator iter = keys.iterator();
            int currLoc = 0;
            while (iter.hasNext()) {
                Key key = (Key) iter.next();
                keyarr[currLoc] = key.getId();
                currLoc++;
            }
            // Save business
            DispositionReport bd = (DispositionReport) executeOperation(keyarr, op);
            List<Result> resultList = bd.getResult();
            keyResultArr = new Result[resultList.size()];
            resultList.toArray(keyResultArr); 
            
            log.debug("After deleting Business. Obtained vector size:" + keyResultArr != null ? keyResultArr.length : 0);
            for (int i = 0; keyResultArr != null && i < keyResultArr.length; i++) {
                Result result = (Result) keyResultArr[i];
                int errno = result.getErrno();
                if (errno == 0) {
                    coll.addAll(keys);
                }
                else {
                    ErrInfo errinfo = result.getErrInfo();
                    DeleteException de = new DeleteException(errinfo.getErrCode() + ":" + errinfo.getValue());
                    bulk.setStatus(JAXRResponse.STATUS_FAILURE);
                    exceptions.add(de);
                }
            }
        }
        catch (RegistryV3Exception regExcept) {

            /*
             * jUDDI (and prollie others) throw an exception on any fault in
             * the transaction w/ the registry, so we don't get any partial
             * success
             */
            DeleteException de = new DeleteException(regExcept.getFaultCode()
                    + ":" + regExcept.getFaultString(), regExcept);

            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            exceptions.add(de);
        }
        catch (JAXRException tran) {
            exceptions.add(new JAXRException("Apache JAXR Impl:", tran));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
        }

        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    private String getUsernameFromCredentials(Set credentials) {
        String username = "", pwd = "";
                
        if (credentials != null) {
        	Iterator it = credentials.iterator();
        	while (it.hasNext()) {
        		PasswordAuthentication pass = (PasswordAuthentication) it.next();
        		username = pass.getUserName();
        	}
        }
        return username;
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
        String username = "", pwd = "";
                
        if (creds != null) {
        	Iterator it = creds.iterator();
        	while (it.hasNext()) {
        		PasswordAuthentication pass = (PasswordAuthentication) it.next();
        		username = pass.getUserName();
        		pwd = new String(pass.getPassword());
        	}
        }

        if ((cachedAuthTokenHash != null) && (cachedAuthTokenHash.containsKey(username))) {
        	return (AuthToken) cachedAuthTokenHash.get(username);
        }
        
        
        AuthToken token = null;
        try {
            token = ireg.getAuthToken(username, pwd);
        }
        catch (Exception e)
        { 
            throw new JAXRException(e);
        }
        cachedAuthTokenHash.put(username, token);
        return token;	
    }

    private PublisherAssertion getPublisherAssertion(AssertionStatusItem asi)
    {
    	PublisherAssertion pa = this.objectFactory.createPublisherAssertion();
        
    	if(asi != null)
    	{
            String sourceKey = asi.getFromKey();
            String targetKey = asi.getToKey();
        
            if (sourceKey != null) {
            pa.setFromKey(sourceKey);
            }
            
            if (targetKey != null) {
            pa.setToKey(targetKey);
            }
            
            KeyedReference keyr = asi.getKeyedReference();
            
            if (keyr != null) {
            pa.setKeyedReference(keyr);
            }
            //pa.setTModelKey(keyr.getTModelKey());
            //pa.setKeyName(keyr.getKeyName());
            //pa.setKeyValue(keyr.getKeyValue()); // -CBC- These are redundant?
    		
    	}return pa;
    }
    
    Organization createOrganization(BusinessDetail bizDetail) throws JAXRException {
        return ScoutUddiV3JaxrHelper.getOrganization(bizDetail, this);
    }    
    
    Organization createOrganization(BusinessInfo bizInfo) throws JAXRException {
        String key = bizInfo.getBusinessKey();
        List<Name> names = bizInfo.getName(); 
        
        List<Description> descriptions = bizInfo.getDescription();
        List<ServiceInfo> serviceInfos = bizInfo.getServiceInfos().getServiceInfo();
        
        OrganizationImpl org = new OrganizationImpl(this);
        org.setKey(createKey(key));
        if (names != null && names.size() > 0) {
            org.setName(createInternationalString(names.get(0).getValue()));
        }
        if (descriptions != null && descriptions.size() > 0) {
            org.setDescription(createInternationalString(descriptions.get(0).getValue()));
        }
        if (serviceInfos != null && serviceInfos.size() > 0) {
            List<Service> services = new ArrayList<Service>(serviceInfos.size());
            for (int i = 0; i < serviceInfos.size(); i++) {
                ServiceInfo serviceInfo = serviceInfos.get(i);
                services.add(createService(serviceInfo));
            }
            org.addServices(services);
        }

        return org;
    }

    Service createService(ServiceInfo serviceInfo) throws JAXRException {
        String key = serviceInfo.getServiceKey();
        List<Name> names = serviceInfo.getName();
        ServiceImpl service = new ServiceImpl(this);
        service.setKey(createKey(key));
        if (names != null && names.size() > 0) {
            service.setName(createInternationalString(names.get(0).getValue()));
        }
        return service;
    }

}
