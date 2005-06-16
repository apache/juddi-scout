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
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.response.*;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.datatype.assertion.PublisherAssertion;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.error.RegistryException;
import org.apache.ws.scout.registry.infomodel.KeyImpl;
import org.apache.ws.scout.registry.infomodel.ConceptImpl;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;
import org.apache.ws.scout.util.ScoutJaxrUddiHelper;
import org.apache.ws.scout.util.ScoutUddiJaxrHelper;

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
     * aves one or more Objects to the registry. An object may be a
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
        Vector svect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();


        Iterator iter = asso.iterator();
        while (iter.hasNext()) {
            try {
                PublisherAssertion pa = ScoutJaxrUddiHelper.getPubAssertionFromJAXRAssociation((Association) iter.next());
                svect.add(pa);
            }
            catch (ClassCastException ce) {
                throw new UnexpectedObjectException();
            }
        }
        // Save PublisherAssertion
        PublisherAssertions bd = null;
        try {
            bd = (PublisherAssertions) executeOperation(svect, "SAVE_ASSOCIATION");
        }
        catch (RegistryException e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setExceptions(exceptions);
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }
        if(bd != null)
        {
           Vector keyvect = bd.getPublisherAssertionVector();
           for (int i = 0; keyvect != null && i < keyvect.size(); i++) {
               PublisherAssertion result = (PublisherAssertion) keyvect.elementAt(i);
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
        Vector entityvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();


        Iterator iter = schemes.iterator();
        while (iter.hasNext()) {
            try {
                TModel en =
                        ScoutJaxrUddiHelper.getTModelFromJAXRClassificationScheme((ClassificationScheme) iter.next());
                entityvect.add(en);
            }
            catch (ClassCastException ce) {
                throw new UnexpectedObjectException();
            }
        }
        System.out.println("Method:save_classificationscheme: ENlength=" + entityvect.size());
        // Save business
        TModelDetail td = null;
        try {
            td = (TModelDetail) executeOperation(entityvect, "SAVE_TMODEL");
        }
        catch (RegistryException e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        entityvect = td.getTModelVector();
        System.out.println("After Saving TModel. Obtained vector size:" + entityvect.size());
        for (int i = 0; entityvect != null && i < entityvect.size(); i++) {
            TModel tm = (TModel) entityvect.elementAt(i);
            coll.add(new KeyImpl(tm.getTModelKey()));
        }

        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveConcepts(Collection concepts) throws JAXRException {
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector entityvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();


        Iterator iter = concepts.iterator();
        while (iter.hasNext()) {
            try {
                TModel en =
                        ScoutJaxrUddiHelper.getTModelFromJAXRConcept((Concept) iter.next());
                entityvect.add(en);
            }
            catch (ClassCastException ce) {
                throw new UnexpectedObjectException();
            }
        }
        System.out.println("Method:save_concept: ENlength=" + entityvect.size());
        // Save business
        TModelDetail td = null;
        try {
            td = (TModelDetail) executeOperation(entityvect, "SAVE_TMODEL");
        }
        catch (RegistryException e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        entityvect = td.getTModelVector();
        System.out.println("After Saving TModel. Obtained vector size:" + entityvect.size());
        for (int i = 0; entityvect != null && i < entityvect.size(); i++) {
            TModel tm = (TModel) entityvect.elementAt(i);
            coll.add(new KeyImpl(tm.getTModelKey()));
        }

        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveOrganizations(Collection organizations) throws JAXRException {
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector entityvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();


        Iterator iter = organizations.iterator();
        while (iter.hasNext()) {
            try {
                BusinessEntity en =
                        ScoutJaxrUddiHelper.getBusinessEntityFromJAXROrg((Organization) iter.next());
                entityvect.add(en);
            }
            catch (ClassCastException ce) {
                throw new UnexpectedObjectException();
            }
        }
        System.out.println("Method:save_business: ENlength=" + entityvect.size());
        // Save business
        BusinessDetail bd = null;
        try {
            bd = (BusinessDetail) executeOperation(entityvect, "SAVE_ORG");
        }
        catch (RegistryException e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        entityvect = bd.getBusinessEntityVector();
        System.out.println("After Saving Business. Obtained vector size:" + entityvect.size());
        for (int i = 0; entityvect != null && i < entityvect.size(); i++) {
            BusinessEntity entity = (BusinessEntity) entityvect.elementAt(i);
            coll.add(new KeyImpl(entity.getBusinessKey()));
        }

        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveServiceBindings(Collection bindings) throws JAXRException {
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector sbvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        Iterator iter = bindings.iterator();
        while (iter.hasNext()) {
            try {
                BindingTemplate bs = ScoutJaxrUddiHelper.getBindingTemplateFromJAXRSB((ServiceBinding) iter.next());
                sbvect.add(bs);
            }
            catch (ClassCastException ce) {
                throw new UnexpectedObjectException();
            }
        }
        // Save ServiceBinding
        BindingDetail bd = null;
        try {
            bd = (BindingDetail) executeOperation(sbvect, "SAVE_SERVICE_BINDING");
        }
        catch (RegistryException e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        sbvect = bd.getBindingTemplateVector();
        for (int i = 0; sbvect != null && i < sbvect.size(); i++) {
            BindingTemplate bt = (BindingTemplate) sbvect.elementAt(i);
            coll.add(new KeyImpl(bt.getBindingKey()));
        }
        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveServices(Collection services) throws JAXRException {
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector svect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();


        Iterator iter = services.iterator();
        while (iter.hasNext()) {
            try {
                BusinessService bs = ScoutJaxrUddiHelper.getBusinessServiceFromJAXRService((Service) iter.next());
                svect.add(bs);
            }
            catch (ClassCastException ce) {
                throw new UnexpectedObjectException();
            }
        }
        // Save Service
        ServiceDetail sd = null;
        try {
            sd = (ServiceDetail) executeOperation(svect, "SAVE_SERVICE");
        }
        catch (RegistryException e) {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
            return bulk;
        }

        svect = sd.getBusinessServiceVector();
        for (int i = 0; svect != null && i < svect.size(); i++) {
            BusinessService entity = (BusinessService) svect.elementAt(i);
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
    protected org.apache.juddi.datatype.RegistryObject executeOperation(Vector datavect, String op)
            throws org.apache.juddi.error.RegistryException, JAXRException {
        org.apache.juddi.datatype.RegistryObject regobj = null;

        IRegistry ireg = null;
        if (registry != null) {
            ireg = registry.getRegistry();
        }

        ConnectionImpl connection = registry.getConnection();
        AuthToken token = getAuthToken(connection, ireg);

        if(op.equalsIgnoreCase("SAVE_ASSOCIATION"))
        {
            regobj = ireg.setPublisherAssertions(token.getAuthInfo(), datavect);
        } else
        if (op.equalsIgnoreCase("SAVE_SERVICE")) {
            regobj = ireg.saveService(token.getAuthInfo(), datavect);
        }
        else if (op.equalsIgnoreCase("SAVE_SERVICE_BINDING")) {
            regobj = ireg.saveBinding(token.getAuthInfo(), datavect);
        }
        else if (op.equalsIgnoreCase("SAVE_ORG")) {
            regobj = ireg.saveBusiness(token.getAuthInfo(), datavect);
        }
        else if (op.equalsIgnoreCase("SAVE_TMODEL")) {
            regobj = ireg.saveTModel(token.getAuthInfo(), datavect);
        }
        else if (op.equalsIgnoreCase("DELETE_ORG")) {
            clearPublisherAssertions(token.getAuthInfo(),datavect,ireg);
            regobj = ireg.deleteBusiness(token.getAuthInfo(), datavect);
        }
        else if (op.equalsIgnoreCase("DELETE_SERVICE")) {
            regobj = ireg.deleteService(token.getAuthInfo(), datavect);
        }
        else if (op.equalsIgnoreCase("DELETE_SERVICEBINDING")) {
            regobj = ireg.deleteBinding(token.getAuthInfo(), datavect);
        }
        else if (op.equalsIgnoreCase("DELETE_CONCEPT")) {
            regobj = ireg.deleteTModel(token.getAuthInfo(), datavect);
        }
        else if (op.equalsIgnoreCase("DELETE_ASSOCIATION")) {
           int len = datavect.size();
            Vector pavect = new Vector(len);
            for(int i=0;i<len;i++)
            {
               String keystr = (String)datavect.elementAt(i);
               pavect.add(ScoutJaxrUddiHelper.getPubAssertionFromJAXRAssociationKey(keystr));
            }
            regobj = ireg.deletePublisherAssertions(token.getAuthInfo(), pavect);
        }
        else if (op.equalsIgnoreCase("DELETE_CLASSIFICATIONSCHEME")) {
            regobj = ireg.deleteTModel(token.getAuthInfo(), datavect);
        }
        else {
            throw new JAXRException("Unsupported operation:" + op);
        }

        return regobj;
    }

    private void clearPublisherAssertions( AuthInfo authinfo,Vector orgkeys,IRegistry ireg)
    {
       Vector pasvect  = null;
       try
       {
          AssertionStatusReport report = ireg.getAssertionStatusReport(authinfo,"status:complete");
          Vector v = report.getAssertionStatusItemVector();

          int len = v != null? v.size() : 0;
          for (int i = 0; i < len; i++)
          {
                AssertionStatusItem asi = (AssertionStatusItem) v.elementAt(i);
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
          v = report.getAssertionStatusItemVector();

          len = v != null? v.size() : 0;
          for (int i = 0; i < len; i++)
          {
                AssertionStatusItem asi = (AssertionStatusItem) v.elementAt(i);
                if(pasvect == null) pasvect = new Vector(len);
                pasvect.add(this.getPublisherAssertion(asi));
          }

          report = ireg.getAssertionStatusReport(authinfo,"status:fromKey_incomplete");
          v = report.getAssertionStatusItemVector();

          len = v != null? v.size() : 0;
          for (int i = 0; i < len; i++)
          {
                AssertionStatusItem asi = (AssertionStatusItem) v.elementAt(i);
                if(pasvect == null) pasvect = new Vector(len);
                pasvect.add(this.getPublisherAssertion(asi));
          }
       }
       catch (RegistryException e)
       {
          e.printStackTrace();
       }

          if(pasvect != null && pasvect.size() > 0)
             try
             {
                ireg.deletePublisherAssertions(authinfo,pasvect);
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
        Vector keyvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        try {
            Iterator iter = keys.iterator();

            while (iter.hasNext()) {
                Key key = (Key) iter.next();
                keyvect.add(key.getId());
            }
            //System.out.println("Method:" + op + ": ENlength=" + keyvect.size());
            // Save business
            DispositionReport bd = (DispositionReport) executeOperation(keyvect, op);

            keyvect = bd.getResultVector();
            System.out.println("After deleting Business. Obtained vector size:" + keyvect.size());
            for (int i = 0; keyvect != null && i < keyvect.size(); i++) {
                Result result = (Result) keyvect.elementAt(i);
                int errno = result.getErrno();
                if (errno == 0) {
                    coll.addAll(keys);
                }
                else {
                    ErrInfo errinfo = result.getErrInfo();
                    DeleteException de = new DeleteException(errinfo.getErrCode() + ":" + errinfo.getErrMsg());
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
        PublisherAssertion pa = new PublisherAssertion();
        pa.setFromKey(sourceKey);
        pa.setToKey(targetKey);
        KeyedReference keyr = asi.getKeyedReference();
        pa.setKeyedReference(keyr);
        pa.setTModelKey(keyr.getTModelKey());
        pa.setKeyName(keyr.getKeyName());
        pa.setKeyValue(keyr.getKeyValue());
        return pa;
    }

}
