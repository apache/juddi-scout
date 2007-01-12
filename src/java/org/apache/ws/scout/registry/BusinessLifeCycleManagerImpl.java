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

import org.apache.ws.scout.uddi.BindingTemplate;
import org.apache.ws.scout.uddi.BusinessEntity;
import org.apache.ws.scout.uddi.AssertionStatusItem;
import org.apache.ws.scout.uddi.AssertionStatusReport;
import org.apache.ws.scout.uddi.AuthToken;
import org.apache.ws.scout.uddi.BindingDetail;
import org.apache.ws.scout.uddi.BusinessDetail;
import org.apache.ws.scout.uddi.BusinessService;
import org.apache.ws.scout.uddi.DispositionReport;
import org.apache.ws.scout.uddi.ErrInfo;
import org.apache.ws.scout.uddi.PublisherAssertions;
import org.apache.ws.scout.uddi.Result;
import org.apache.ws.scout.uddi.ServiceDetail;
import org.apache.ws.scout.uddi.TModel;
import org.apache.ws.scout.uddi.PublisherAssertion;
import org.apache.ws.scout.uddi.KeyedReference;
import org.apache.ws.scout.uddi.TModelDetail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.scout.registry.IRegistry;
import org.apache.ws.scout.registry.RegistryException;
import org.apache.ws.scout.registry.infomodel.KeyImpl;
import org.apache.ws.scout.transport.LocalTransport;
import org.apache.ws.scout.util.ScoutJaxrUddiHelper;
import org.apache.xmlbeans.XmlObject;

import javax.xml.registry.*;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.RegistryObject;
import java.io.Serializable;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Implements JAXR BusinessLifeCycleManager Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 */
public class BusinessLifeCycleManagerImpl extends LifeCycleManagerImpl
        implements BusinessLifeCycleManager, Serializable {
	
	private Log log = LogFactory.getLog(this.getClass());
	
    public BusinessLifeCycleManagerImpl(RegistryService registry) {
        super(registry);
    }

    /**
     * Deletes one or more previously submitted objects from the registry
     * using the object keys and a specified objectType attribute.
     *
     * @param keys
     * @param objectType
     * @return
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

        Collection suc = new ArrayList();
        Collection exc = new ArrayList();

        while (iter.hasNext()) {
            RegistryObject reg = (RegistryObject) iter.next();

            BulkResponse br = null;

            Collection c = new ArrayList();
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
                suc.addAll(br.getExceptions());
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


    public BulkResponse saveAssociations(Collection asso, boolean replace) throws JAXRException {    //TODO
        BulkResponseImpl bulk = new BulkResponseImpl();
        PublisherAssertion[] sarr = new PublisherAssertion[asso.size()];

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        Iterator iter = asso.iterator();
        int currLoc = 0;
        while (iter.hasNext()) {
            try {
                PublisherAssertion pa = ScoutJaxrUddiHelper.getPubAssertionFromJAXRAssociation((Association) iter.next());
                sarr[currLoc] = pa;
                currLoc++;
            }
            catch (ClassCastException ce) {
                throw new UnexpectedObjectException();
            }
        }
        // Save PublisherAssertion
        PublisherAssertions bd = null;
        try {
            bd = (PublisherAssertions) executeOperation(sarr, "SAVE_ASSOCIATION");
        }
        catch (RegistryException e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setExceptions(exceptions);
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }
        if(bd != null)
        {
        	PublisherAssertion[] keyarr = bd.getPublisherAssertionArray();
        	for (int i = 0; keyarr != null && i < keyarr.length; i++) {
        		PublisherAssertion result = (PublisherAssertion) keyarr[i];
               KeyedReference kr = result.getKeyedReference();
               coll.add(kr.getTModelKey()); //TODO:Verify This

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

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        Iterator iter = schemes.iterator();
        int currLoc = 0;
        while (iter.hasNext()) {
            try {
                TModel en =
                        ScoutJaxrUddiHelper.getTModelFromJAXRClassificationScheme((ClassificationScheme) iter.next());
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
        catch (RegistryException e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        entityarr = td.getTModelArray();
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

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        Iterator iter = concepts.iterator();
        int currLoc = 0;
        while (iter.hasNext()) {
            try {
                TModel en =
                        ScoutJaxrUddiHelper.getTModelFromJAXRConcept((Concept) iter.next());
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
        catch (RegistryException e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        entityarr = td.getTModelArray();
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

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        Iterator iter = organizations.iterator();
        int currLoc = 0;
        while (iter.hasNext()) {
            try {
                BusinessEntity en =
                        ScoutJaxrUddiHelper.getBusinessEntityFromJAXROrg((Organization) iter.next());
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
        catch (RegistryException e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        entityarr = bd.getBusinessEntityArray();
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

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        Iterator iter = bindings.iterator();
        int currLoc = 0;
        while (iter.hasNext()) {
            try {
                BindingTemplate bs = ScoutJaxrUddiHelper.getBindingTemplateFromJAXRSB((ServiceBinding) iter.next());
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
        catch (RegistryException e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        sbarr = bd.getBindingTemplateArray();
        for (int i = 0; sbarr != null && i < sbarr.length; i++) {
            BindingTemplate bt = (BindingTemplate) sbarr[i];
            coll.add(new KeyImpl(bt.getBindingKey()));
        }
        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveServices(Collection services) throws JAXRException {
        BulkResponseImpl bulk = new BulkResponseImpl();
        BusinessService[] sarr = new BusinessService[services.size()];

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();


        Iterator iter = services.iterator();
        int currLoc = 0;
        while (iter.hasNext()) {
            try {
                BusinessService bs = ScoutJaxrUddiHelper.getBusinessServiceFromJAXRService((Service) iter.next());
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
        catch (RegistryException e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        sarr = sd.getBusinessServiceArray();
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
       Collection col = new ArrayList();
       col.add(assoc);
       BulkResponse br = this.saveAssociations(col, true);
       if(br.getExceptions()!= null)
          throw new JAXRException("Confiming the Association Failed");
    }

    public void unConfirmAssociation(Association assoc) throws JAXRException, InvalidRequestException {
       //TODO
       //Delete it from the UDDI registry
       Collection col = new ArrayList();
       col.add(assoc.getKey());
       BulkResponse br = this.deleteAssociations(col);
       if(br.getExceptions()!= null)
          throw new JAXRException("UnConfiming the Association Failed");
    }

    //Protected Methods
    protected XmlObject executeOperation(Object dataarray, String op)
            throws RegistryException, JAXRException {
        //org.apache.juddi.datatype.RegistryObject regobj = null;
    	XmlObject regobj = null;

        IRegistry ireg = null;
        if (registry != null) {
            ireg = registry.getRegistry();
        }

        ConnectionImpl connection = registry.getConnection();
        AuthToken token = getAuthToken(connection, ireg);

        if(op.equalsIgnoreCase("SAVE_ASSOCIATION"))
        {
            regobj = ireg.setPublisherAssertions(token.getAuthInfo(), (PublisherAssertion[]) dataarray);
        } else
        if (op.equalsIgnoreCase("SAVE_SERVICE")) {
            regobj = ireg.saveService(token.getAuthInfo(), (BusinessService[])dataarray);
        }
        else if (op.equalsIgnoreCase("SAVE_SERVICE_BINDING")) {
            regobj = ireg.saveBinding(token.getAuthInfo(), (BindingTemplate[]) dataarray);
        }
        else if (op.equalsIgnoreCase("SAVE_ORG")) {
            regobj = ireg.saveBusiness(token.getAuthInfo(), (BusinessEntity[]) dataarray);
        }
        else if (op.equalsIgnoreCase("SAVE_TMODEL")) {
            regobj = ireg.saveTModel(token.getAuthInfo(), (TModel[]) dataarray);
        }
        else if (op.equalsIgnoreCase("DELETE_ORG")) {
            clearPublisherAssertions(token.getAuthInfo(), (String[]) dataarray,ireg);
            regobj = ireg.deleteBusiness(token.getAuthInfo(), (String[]) dataarray);
        }
        else if (op.equalsIgnoreCase("DELETE_SERVICE")) {
            regobj = ireg.deleteService(token.getAuthInfo(), (String[]) dataarray);
        }
        else if (op.equalsIgnoreCase("DELETE_SERVICEBINDING")) {
            regobj = ireg.deleteBinding(token.getAuthInfo(), (String[]) dataarray);
        }
        else if (op.equalsIgnoreCase("DELETE_CONCEPT")) {
            regobj = ireg.deleteTModel(token.getAuthInfo(), (String[]) dataarray);
        }
        else if (op.equalsIgnoreCase("DELETE_ASSOCIATION")) {
           int len = ((String[]) dataarray).length;
            PublisherAssertion[] paarr = new PublisherAssertion[len];
            for(int i=0;i<len;i++)
            {
               String keystr = ((String[])dataarray)[i];
               paarr[i] = ScoutJaxrUddiHelper.getPubAssertionFromJAXRAssociationKey(keystr);
            }
            regobj = ireg.deletePublisherAssertions(token.getAuthInfo(), paarr);
        }
        else if (op.equalsIgnoreCase("DELETE_CLASSIFICATIONSCHEME")) {
            regobj = ireg.deleteTModel(token.getAuthInfo(), (String[]) dataarray);
        }
        else {
            throw new JAXRException("Unsupported operation:" + op);
        }

        return regobj;
    }

    private void clearPublisherAssertions( String authinfo,String[] orgkeys,IRegistry ireg)
    {
       Vector pasvect  = null;
       PublisherAssertion[] pasarr  = null;
       try
       {
          AssertionStatusReport report = ireg.getAssertionStatusReport(authinfo,"status:complete");
          AssertionStatusItem[] a = report.getAssertionStatusItemArray();

          int len = a != null? a.length : 0;
          for (int i = 0; i < len; i++)
          {
                AssertionStatusItem asi = a[i];
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
                if(pasvect == null) pasvect = new Vector(len);
                pasvect.add(this.getPublisherAssertion(asi));
           }
          report = ireg.getAssertionStatusReport(authinfo,"status:toKey_incomplete");
          a = report.getAssertionStatusItemArray();

          len = a != null? a.length : 0;
          for (int i = 0; i < len; i++)
          {
                AssertionStatusItem asi = (AssertionStatusItem) a[i];
                if(pasvect == null) pasvect = new Vector(len);
                pasvect.add(this.getPublisherAssertion(asi));
          }

          report = ireg.getAssertionStatusReport(authinfo,"status:fromKey_incomplete");
          a = report.getAssertionStatusItemArray();

          len = a != null? a.length : 0;
          for (int i = 0; i < len; i++)
          {
                AssertionStatusItem asi = (AssertionStatusItem) a[i];
                if(pasvect == null) pasvect = new Vector(len);
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
       catch (RegistryException e)
       {
          e.printStackTrace();
       }

          if(pasarr != null && pasarr.length > 0)
             try
             {
                ireg.deletePublisherAssertions(authinfo, pasarr);
             }
             catch (RegistryException e)
             {
                e.printStackTrace();
                //IGNORE
             }
       }



    protected BulkResponse deleteOperation(Collection keys, String op)
            throws JAXRException {
        if(keys == null)
        throw new JAXRException("Keys provided to "+op+" are null");
       
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        String[] keyarr = new String[keys.size()];
        Result[] keyResultArr;

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        try {
            Iterator iter = keys.iterator();
            int currLoc = 0;
            while (iter.hasNext()) {
                Key key = (Key) iter.next();
                keyarr[currLoc] = key.getId();
                currLoc++;
            }
            //System.out.println("Method:" + op + ": ENlength=" + keyvect.size());
            // Save business
            DispositionReport bd = (DispositionReport) executeOperation(keyarr, op);

            keyResultArr = bd.getResultArray();
            System.out.println("After deleting Business. Obtained vector size:" + keyResultArr != null ? keyResultArr.length : 0);
            for (int i = 0; keyResultArr != null && i < keyResultArr.length; i++) {
                Result result = (Result) keyResultArr[i];
                int errno = result.getErrno();
                if (errno == 0) {
                    coll.addAll(keys);
                }
                else {
                    ErrInfo errinfo = result.getErrInfo();
                    DeleteException de = new DeleteException(errinfo.getErrCode() + ":" + errinfo.getStringValue());
                    bulk.setStatus(JAXRResponse.STATUS_FAILURE);
                    exceptions.add(de);
                }
            }
        }
        catch (RegistryException regExcept) {

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
        String username = "", pwd = "";
        if (creds != null) {
        	Iterator it = creds.iterator();
        while (it.hasNext()) {
            PasswordAuthentication pass = (PasswordAuthentication) it.next();
            username = pass.getUserName();
            pwd = new String(pass.getPassword());
        }
        }

        AuthToken token = null;
        try {
            token = ireg.getAuthToken(username, pwd);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JAXRException(e);
        }
        return token;
    }

    private PublisherAssertion getPublisherAssertion(AssertionStatusItem asi)
    {
        String sourceKey = asi.getFromKey();
        String targetKey = asi.getToKey();
        PublisherAssertion pa = PublisherAssertion.Factory.newInstance();
        
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
        return pa;
    }

}
