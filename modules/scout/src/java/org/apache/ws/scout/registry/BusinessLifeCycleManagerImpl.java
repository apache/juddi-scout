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
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.BindingDetail;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.datatype.response.ServiceDetail;
import org.apache.juddi.datatype.response.TModelDetail;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.error.RegistryException;
import org.apache.ws.scout.registry.infomodel.KeyImpl;
import org.apache.ws.scout.util.ScoutJaxrUddiHelper;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.DeleteException;
import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.SaveException;
import javax.xml.registry.UnexpectedObjectException;
import javax.xml.registry.LifeCycleManager;
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
        implements BusinessLifeCycleManager, Serializable
{

    public BusinessLifeCycleManagerImpl(RegistryService registry)
    {
        super(registry);
    }

    //Override from Base Class
    public BulkResponse deleteObjects(Collection keys, String objectType) throws JAXRException
    {
        BulkResponse  bulk = null;

        if(objectType == LifeCycleManager.ASSOCIATION)
        {
            bulk = this.deleteAssociations(keys);
        } else
        if(objectType == LifeCycleManager.CLASSIFICATION_SCHEME)
        {
            bulk = this.deleteClassificationSchemes(keys);
        } else
        if(objectType == LifeCycleManager.CONCEPT)
        {
            bulk = this.deleteConcepts(keys);
        } else
        if(objectType == LifeCycleManager.ORGANIZATION)
        {
            bulk = this.deleteOrganizations(keys) ;
        } else
        if(objectType == LifeCycleManager.SERVICE)
        {
            bulk = this.deleteServices(keys);
        }else
        if(objectType == LifeCycleManager.SERVICE_BINDING)
        {
            bulk = this.deleteServiceBindings(keys);
        }else
        throw new JAXRException( "Delete Operation for "+objectType + " not implemented by Scout");

        return bulk;
    }

    public BulkResponse deleteAssociations(Collection associationKeys) throws JAXRException
    {
        return this.deleteOperation(associationKeys, "DELETE_ASSOCIATION");
    }

    public BulkResponse deleteClassificationSchemes(Collection schemeKeys) throws JAXRException
    {
        return this.deleteOperation(schemeKeys, "DELETE_CLASSIFICATIONSCHEME");
    }

    public BulkResponse deleteConcepts(Collection conceptKeys) throws JAXRException
    {
        return this.deleteOperation(conceptKeys, "DELETE_CONCEPT");
    }

    public BulkResponse deleteOrganizations(Collection orgkeys) throws JAXRException
    {
        return this.deleteOperation(orgkeys, "DELETE_ORG");
    }

    public BulkResponse deleteServiceBindings(Collection bindingKeys) throws JAXRException
    {
        return this.deleteOperation(bindingKeys, "DELETE_SERVICEBINDING");
    }

    public BulkResponse deleteServices(Collection serviceKeys) throws JAXRException
    {
        return this.deleteOperation(serviceKeys, "DELETE_SERVICE");
    }

    public BulkResponse saveObjects(Collection col ) throws JAXRException
    {
        BulkResponseImpl bulk = new BulkResponseImpl();
        Iterator iter = col.iterator();
        //TODO:Check if juddi can provide a facility to store a collection of heterogenous
        //objects
        Collection keys = new ArrayList();
        Collection suc = new ArrayList();
        Collection exc = new ArrayList();
        bulk.setCollection(suc);
        bulk.setExceptions(exc);
        while(iter.hasNext())
        {
            keys.clear();
            RegistryObject reg = (RegistryObject)iter.next();
            keys.add(reg.getKey());
            BulkResponse  br = null;
            if(reg instanceof javax.xml.registry.infomodel.Association)
            {
                br = saveAssociations(keys, true);
            } else
            if(reg instanceof javax.xml.registry.infomodel.ClassificationScheme)
            {
                br = saveClassificationSchemes(keys );
            }
            else
            if(reg instanceof javax.xml.registry.infomodel.Concept)
            {
                br = saveConcepts(keys );
            }else
            if(reg instanceof javax.xml.registry.infomodel.Organization)
            {
                br = saveOrganizations(keys );
            }else
            if(reg instanceof javax.xml.registry.infomodel.Service)
            {
                br = saveServices(keys );
            }else
            if(reg instanceof javax.xml.registry.infomodel.ServiceBinding)
            {
                br = saveServiceBindings(keys );
            }

            if(br != null ) updateBulkResponse(bulk,br);
        }

        return bulk;
    }

    public BulkResponse saveAssociations(Collection associationKeys, boolean replace) throws JAXRException
    {    //TODO
        return null;
    }

    public BulkResponse saveClassificationSchemes(Collection schemes) throws JAXRException
    {
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector entityvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();


        Iterator iter = schemes.iterator();
        while (iter.hasNext())
        {
            try
            {
                TModel en =
                        ScoutJaxrUddiHelper.getTModelFromJAXRClassificationScheme((ClassificationScheme) iter.next());
                entityvect.add(en);
            } catch (ClassCastException ce)
            {
                throw new UnexpectedObjectException();
            }
        }
        System.out.println("Method:save_classificationscheme: ENlength=" + entityvect.size());
        // Save business
        TModelDetail td = null;
        try
        {
            td = (TModelDetail) executeOperation(entityvect, "SAVE_TMODEL");
        } catch (RegistryException e)
        {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
        }

        entityvect = td.getTModelVector();
        System.out.println("After Saving TModel. Obtained vector size:" + entityvect.size());
        for (int i = 0; entityvect != null && i < entityvect.size(); i++)
        {
            TModel tm = (TModel) entityvect.elementAt(i);
            coll.add(new KeyImpl(tm.getTModelKey()));
        }

        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveConcepts(Collection concepts) throws JAXRException
    {
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector entityvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();


        Iterator iter = concepts.iterator();
        while (iter.hasNext())
        {
            try
            {
                TModel en =
                        ScoutJaxrUddiHelper.getTModelFromJAXRConcept((Concept) iter.next());
                entityvect.add(en);
            } catch (ClassCastException ce)
            {
                throw new UnexpectedObjectException();
            }
        }
        System.out.println("Method:save_concept: ENlength=" + entityvect.size());
        // Save business
        TModelDetail td = null;
        try
        {
            td = (TModelDetail) executeOperation(entityvect, "SAVE_TMODEL");
        } catch (RegistryException e)
        {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
        }

        entityvect = td.getTModelVector();
        System.out.println("After Saving TModel. Obtained vector size:" + entityvect.size());
        for (int i = 0; entityvect != null && i < entityvect.size(); i++)
        {
            TModel tm = (TModel) entityvect.elementAt(i);
            coll.add(new KeyImpl(tm.getTModelKey()));
        }

        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveOrganizations(Collection organizations) throws JAXRException
    {
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector entityvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();


        Iterator iter = organizations.iterator();
        while (iter.hasNext())
        {
            try
            {
                BusinessEntity en =
                        ScoutJaxrUddiHelper.getBusinessEntityFromJAXROrg((Organization) iter.next());
                entityvect.add(en);
            } catch (ClassCastException ce)
            {
                throw new UnexpectedObjectException();
            }
        }
        System.out.println("Method:save_business: ENlength=" + entityvect.size());
        // Save business
        BusinessDetail bd = null;
        try
        {
            bd = (BusinessDetail) executeOperation(entityvect, "SAVE_ORG");
        } catch (RegistryException e)
        {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
        }

        entityvect = bd.getBusinessEntityVector();
        System.out.println("After Saving Business. Obtained vector size:" + entityvect.size());
        for (int i = 0; entityvect != null && i < entityvect.size(); i++)
        {
            BusinessEntity entity = (BusinessEntity) entityvect.elementAt(i);
            coll.add(new KeyImpl(entity.getBusinessKey()));
        }

        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveServiceBindings(Collection bindings) throws JAXRException
    {
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector sbvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        Iterator iter = bindings.iterator();
        while (iter.hasNext())
        {
            try
            {
                BindingTemplate bs = ScoutJaxrUddiHelper.getBindingTemplateFromJAXRSB((ServiceBinding) iter.next());
                sbvect.add(bs);
            } catch (ClassCastException ce)
            {
                throw new UnexpectedObjectException();
            }
        }
        // Save ServiceBinding
        BindingDetail bd = null;
        try
        {
            bd = (BindingDetail) executeOperation(sbvect, "SAVE_SERVICE_BINDING");
        } catch (RegistryException e)
        {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
        }

        sbvect = bd.getBindingTemplateVector();
        for (int i = 0; sbvect != null && i < sbvect.size(); i++)
        {
            BindingTemplate bt = (BindingTemplate) sbvect.elementAt(i);
            coll.add(new KeyImpl(bt.getBindingKey()));
        }
        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public BulkResponse saveServices(Collection services) throws JAXRException
    {
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector svect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();


        Iterator iter = services.iterator();
        while (iter.hasNext())
        {
            try
            {
                BusinessService bs = ScoutJaxrUddiHelper.getBusinessServiceFromJAXRService((Service) iter.next());
                svect.add(bs);
            } catch (ClassCastException ce)
            {
                throw new UnexpectedObjectException();
            }
        }
        // Save Service
        ServiceDetail sd = null;
        try
        {
            sd = (ServiceDetail) executeOperation(svect, "SAVE_SERVICE");
        } catch (RegistryException e)
        {
            exceptions.add(new SaveException(e.getLocalizedMessage()));
            bulk.setStatus(JAXRResponse.STATUS_FAILURE);
        }

        svect = sd.getBusinessServiceVector();
        for (int i = 0; svect != null && i < svect.size(); i++)
        {
            BusinessService entity = (BusinessService) svect.elementAt(i);
            coll.add(new KeyImpl(entity.getServiceKey()));
        }
        bulk.setCollection(coll);
        bulk.setExceptions(exceptions);

        return bulk;
    }

    public void confirmAssociation(Association assoc) throws JAXRException, InvalidRequestException
    {
        //TODO
    }

    public void unConfirmAssociation(Association assoc) throws JAXRException, InvalidRequestException
    {  //TODO
    }

    //Protected Methods
    protected org.apache.juddi.datatype.RegistryObject executeOperation(Vector datavect, String op)
            throws org.apache.juddi.error.RegistryException, JAXRException
    {
        org.apache.juddi.datatype.RegistryObject regobj = null;

        IRegistry ireg = null;
        if (registry != null) ireg = registry.getRegistry();

        ConnectionImpl connection = registry.getConnection();
        AuthToken token = getAuthToken(connection, ireg);


        if (op.equalsIgnoreCase("SAVE_SERVICE"))
        {
            regobj = ireg.saveService(token.getAuthInfo(), datavect);
        } else if (op.equalsIgnoreCase("SAVE_SERVICE_BINDING"))
        {
            regobj = ireg.saveBinding(token.getAuthInfo(), datavect);
        } else if (op.equalsIgnoreCase("SAVE_ORG"))
        {
            regobj = ireg.saveBusiness(token.getAuthInfo(), datavect);
        } else if (op.equalsIgnoreCase("SAVE_TMODEL"))
        {
            regobj = ireg.saveTModel(token.getAuthInfo(), datavect);
        } else if (op.equalsIgnoreCase("DELETE_ORG"))
        {
            regobj = ireg.deleteBusiness(token.getAuthInfo(), datavect);
        } else if (op.equalsIgnoreCase("DELETE_SERVICE"))
        {
            regobj = ireg.deleteService(token.getAuthInfo(), datavect);
        } else if (op.equalsIgnoreCase("DELETE_SERVICEBINDING"))
        {
            regobj = ireg.deleteBinding(token.getAuthInfo(), datavect);
        } else if (op.equalsIgnoreCase("DELETE_CONCEPT"))
        {
            regobj = ireg.deleteTModel(token.getAuthInfo(), datavect);
        } else if (op.equalsIgnoreCase("DELETE_ASSOCIATION"))
        {
            regobj = ireg.deletePublisherAssertions(token.getAuthInfo(), datavect);
        } else if (op.equalsIgnoreCase("DELETE_CLASSIFICATIONSCHEME"))
        {
            regobj = ireg.deleteTModel(token.getAuthInfo(), datavect);
        } else
            throw new JAXRException("Unsupported operation:" + op);

        return regobj;

    }


    protected BulkResponse deleteOperation(Collection keys, String op)
            throws JAXRException
    {
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector keyvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        try
        {
            Iterator iter = keys.iterator();

            while (iter.hasNext())
            {
                Key key = (Key) iter.next();
                keyvect.add(key.getId());
            }
            System.out.println("Method:" + op + ": ENlength=" + keyvect.size());
            // Save business
            DispositionReport bd = (DispositionReport) executeOperation(keyvect, op);

            keyvect = bd.getResultVector();
            System.out.println("After deleting Business. Obtained vector size:" + keyvect.size());
            for (int i = 0; keyvect != null && i < keyvect.size(); i++)
            {
                Result result = (Result) keyvect.elementAt(i);
                int errno = result.getErrno();
                if (errno == 0)
                    coll.addAll(keys);
                else
                {
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
        catch (JAXRException tran)
        {
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
            throws JAXRException
    {
        Set creds = connection.getCredentials();
        Iterator it = creds.iterator();
        String username = "", pwd = "";
        while (it.hasNext())
        {
            PasswordAuthentication pass = (PasswordAuthentication) it.next();
            username = pass.getUserName();
            pwd = new String(pass.getPassword());
        }
        AuthToken token = null;
        try
        {
            token = ireg.getAuthToken(username, pwd);
        } catch (Exception e)
        {
            throw new JAXRException(e);
        }
        return token;
    }

    private void updateBulkResponse(BulkResponseImpl bulk, BulkResponse  br)
    throws JAXRException
    {
        bulk.getCollection().addAll(br.getCollection());
        bulk.getExceptions().addAll(br.getExceptions());
        if(bulk.getStatus() == JAXRResponse.STATUS_SUCCESS)
            bulk.setStatus(br.getStatus());
    }

}
