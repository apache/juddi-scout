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
import org.apache.juddi.datatype.business.*;
import org.apache.juddi.datatype.service.*;
import org.apache.juddi.datatype.*;
import org.apache.juddi.datatype.PersonName;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.binding.AccessPoint;
import org.apache.juddi.datatype.binding.HostingRedirector;
import org.apache.juddi.datatype.response.*;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;
import org.apache.ws.scout.registry.infomodel.KeyImpl;
import org.apache.ws.scout.util.ScoutJaxrUddiHelper;

import javax.xml.registry.*;
import javax.xml.registry.infomodel.*;
import javax.xml.registry.infomodel.RegistryObject;
import java.util.*;
import java.net.PasswordAuthentication;

/**
 * Implements JAXR BusinessLifeCycleManager Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class BusinessLifeCycleManagerImpl extends LifeCycleManagerImpl
        implements BusinessLifeCycleManager
{

    public BusinessLifeCycleManagerImpl(RegistryService registry)
    {
        super(registry);
    }

    public BulkResponse deleteAssociations(Collection associationKeys) throws JAXRException
    {
        return this.deleteOperation(associationKeys,"DELETE_ASSOCIATION");
    }

    public BulkResponse deleteClassificationSchemes(Collection schemeKeys) throws JAXRException
    {
        return this.deleteOperation(schemeKeys,"DELETE_CLASSIFICATIONSCHEME");
    }

    public BulkResponse deleteConcepts(Collection conceptKeys) throws JAXRException
    {
        return this.deleteOperation(conceptKeys,"DELETE_CONCEPT");
    }

    public BulkResponse deleteOrganizations(Collection orgkeys) throws JAXRException
    {
       return this.deleteOperation(orgkeys,"DELETE_ORG");
    }

    public BulkResponse deleteServiceBindings(Collection bindingKeys) throws JAXRException
    {
       return this.deleteOperation(bindingKeys,"DELETE_SERVICEBINDING");
    }

    public BulkResponse deleteServices(Collection serviceKeys) throws JAXRException
    {
        return this.deleteOperation(serviceKeys,"DELETE_SERVICE");
    }

    public BulkResponse saveAssociations(Collection associationKeys, boolean replace) throws JAXRException
    {
        return null;
    }

    public BulkResponse saveClassificationSchemes(Collection schemes) throws JAXRException
    {
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector entityvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        try
        {
            Iterator iter = schemes.iterator();
            while (iter.hasNext())
            {
                TModel en =
                        ScoutJaxrUddiHelper.getTModelFromJAXRClassificationScheme((ClassificationScheme) iter.next());
                entityvect.add(en);
            }
            System.out.println("Method:save_business: ENlength=" + entityvect.size());
            // Save business
            TModelDetail td = (TModelDetail) executeOperation(entityvect, "SAVE_TMODEL");

            entityvect = td.getTModelVector();
            System.out.println("After Saving TModel. Obtained vector size:" + entityvect.size());
            for (int i = 0; entityvect != null && i < entityvect.size(); i++)
            {
                TModel tm = (TModel) entityvect.elementAt(i);
                coll.add(new KeyImpl(tm.getTModelKey() ));
            }

            bulk.setCollection(coll);
            bulk.setExceptions(exceptions);
        } catch (Exception tran)
        {
            throw new JAXRException("Apache JAXR Impl:", tran);
        }
        return bulk;
    }

    public BulkResponse saveConcepts(Collection concepts) throws JAXRException
    {
       //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector entityvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        try
        {
            Iterator iter = concepts.iterator();
            while (iter.hasNext())
            {
                TModel en =
                        ScoutJaxrUddiHelper.getTModelFromJAXRConcept((Concept) iter.next());
                entityvect.add(en);
            }
            System.out.println("Method:save_concept: ENlength=" + entityvect.size());
            // Save business
            TModelDetail td = (TModelDetail) executeOperation(entityvect, "SAVE_TMODEL");

            entityvect = td.getTModelVector();
            System.out.println("After Saving TModel. Obtained vector size:" + entityvect.size());
            for (int i = 0; entityvect != null && i < entityvect.size(); i++)
            {
                TModel tm = (TModel) entityvect.elementAt(i);
                coll.add(new KeyImpl(tm.getTModelKey() ));
            }

            bulk.setCollection(coll);
            bulk.setExceptions(exceptions);
        } catch (Exception tran)
        {
            throw new JAXRException("Apache JAXR Impl:", tran);
        }
        return bulk;
    }

    public BulkResponse saveOrganizations(Collection organizations) throws JAXRException
    {
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector entityvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        try
        {
            Iterator iter = organizations.iterator();
            while (iter.hasNext())
            {
                BusinessEntity en =
                        ScoutJaxrUddiHelper.getBusinessEntityFromJAXROrg((Organization) iter.next());
                entityvect.add(en);
            }
            System.out.println("Method:save_business: ENlength=" + entityvect.size());
            // Save business
            BusinessDetail bd = (BusinessDetail) executeOperation(entityvect, "SAVE_ORG");

            entityvect = bd.getBusinessEntityVector();
            System.out.println("After Saving Business. Obtained vector size:" + entityvect.size());
            for (int i = 0; entityvect != null && i < entityvect.size(); i++)
            {
                BusinessEntity entity = (BusinessEntity) entityvect.elementAt(i);
                coll.add(new KeyImpl(entity.getBusinessKey()));
            }

            bulk.setCollection(coll);
            bulk.setExceptions(exceptions);
        } catch (Exception tran)
        {
            throw new JAXRException("Apache JAXR Impl:", tran);
        }
        return bulk;
    }

    public BulkResponse saveServiceBindings(Collection bindings) throws JAXRException
    {
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector sbvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        try
        {
            Iterator iter = bindings.iterator();
            while (iter.hasNext())
            {
                BindingTemplate bs = ScoutJaxrUddiHelper.getBindingTemplateFromJAXRSB((ServiceBinding) iter.next());
                sbvect.add(bs);
            }
            // Save ServiceBinding
            BindingDetail bd = (BindingDetail) executeOperation(sbvect, "SAVE_SERVICE_BINDING");

            sbvect = bd.getBindingTemplateVector();
            for (int i = 0; sbvect != null && i < sbvect.size(); i++)
            {
                BindingTemplate bt = (BindingTemplate) sbvect.elementAt(i);
                coll.add(new KeyImpl(bt.getBindingKey()));
            }
            bulk.setCollection(coll);
            bulk.setExceptions(exceptions);
        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return bulk;
    }

    public BulkResponse saveServices(Collection services) throws JAXRException
    {
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector svect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

        try
        {
            Iterator iter = services.iterator();
            while (iter.hasNext())
            {
                BusinessService bs = ScoutJaxrUddiHelper.getBusinessServiceFromJAXRService((Service) iter.next());
                svect.add(bs);
            }
            // Save Service
            ServiceDetail sd = (ServiceDetail) executeOperation(svect, "SAVE_SERVICE");

            svect = sd.getBusinessServiceVector();
            for (int i = 0; svect != null && i < svect.size(); i++)
            {
                BusinessService entity = (BusinessService) svect.elementAt(i);
                coll.add(new KeyImpl(entity.getBusinessKey()));
            }
            bulk.setCollection(coll);
            bulk.setExceptions(exceptions);
        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return bulk;
    }

    public void confirmAssociation(Association assoc) throws JAXRException, InvalidRequestException
    {
    }

    public void unConfirmAssociation(Association assoc) throws JAXRException, InvalidRequestException
    {
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
        }else if (op.equalsIgnoreCase("DELETE_SERVICEBINDING"))
        {
            regobj = ireg.deleteBinding(token.getAuthInfo(), datavect);
        }else if (op.equalsIgnoreCase("DELETE_CONCEPT"))
        {
            regobj = ireg.deleteTModel(token.getAuthInfo(), datavect);
        } else if (op.equalsIgnoreCase("DELETE_ASSOCIATION"))
        {
            regobj = ireg.deletePublisherAssertions(token.getAuthInfo(), datavect);
        }
        else if (op.equalsIgnoreCase("DELETE_CLASSIFICATIONSCHEME"))
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
                Key key = (Key)iter.next();
                keyvect.add(key.getId());
            }
            System.out.println("Method:"+op+": ENlength=" + keyvect.size());
            // Save business
            DispositionReport bd = (DispositionReport) executeOperation(keyvect, op);

            keyvect = bd.getResultVector();
            System.out.println("After deleting Business. Obtained vector size:" + keyvect.size());
            for (int i = 0; keyvect != null && i < keyvect.size(); i++)
            {
                Result result = (Result) keyvect.elementAt(i);
                int errno = result.getErrno();
                if(errno == 0) coll.addAll(keys);
                else
                {
                    ErrInfo errinfo = result.getErrInfo();
                    DeleteException de = new DeleteException(errinfo.getErrCode()+":"+errinfo.getErrMsg());
                    exceptions.add(de);
                }
            }

            bulk.setCollection(coll);
            bulk.setExceptions(exceptions);
        } catch (Exception tran)
        {
            throw new JAXRException("Apache JAXR Impl:", tran);
        }
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

}
