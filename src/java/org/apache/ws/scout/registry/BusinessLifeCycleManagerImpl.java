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
        return null;
    }

    public BulkResponse deleteClassificationSchemes(Collection schemeKeys) throws JAXRException
    {
        return null;
    }

    public BulkResponse deleteConcepts(Collection conceptKeys) throws JAXRException
    {
        return null;
    }

    public BulkResponse deleteOrganizations(Collection organizationKeys) throws JAXRException
    {
        return null;
    }

    public BulkResponse deleteServiceBindings(Collection bindingKeys) throws JAXRException
    {
        return null;
    }

    public BulkResponse deleteServices(Collection serviceKeys) throws JAXRException
    {
        return null;
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
                        getTModelFromJAXRClassificationScheme((ClassificationScheme) iter.next());
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
                        getTModelFromJAXRConcept((Concept) iter.next());
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
                        getBusinessEntityFromJAXROrg((Organization) iter.next());
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
                BindingTemplate bs = getBindingTemplateFromJAXRSB((ServiceBinding) iter.next());
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
                BusinessService bs = getBusinessServiceFromJAXRService((Service) iter.next());
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
        } else
            throw new JAXRException("Unsupported operation:" + op);

        return regobj;
    }

    //Private methods
    private BusinessEntity getBusinessEntityFromJAXROrg(Organization org)
            throws JAXRException
    {
        BusinessEntity biz = new BusinessEntity();
        BusinessServices bss = new BusinessServices();
        Contacts cts = new Contacts();
        Vector bvect = new Vector();
        Vector cvect = new Vector();

        try
        {
            //Lets get the Organization attributes at the top level
            String language = Locale.getDefault().getLanguage();

            biz.addName(new Name(org.getName().getValue(), language));
            biz.addDescription(new Description(org.getDescription().getValue()));
            if(org.getPrimaryContact() != null )
                biz.setAuthorizedName(org.getPrimaryContact().getPersonName().getFullName());

            Collection s = org.getServices();
            System.out.println("?Org has services=" + s.isEmpty());
            Iterator iter = s.iterator();
            while (iter.hasNext())
            {
                BusinessService bs =
                        getBusinessServiceFromJAXRService((Service) iter.next());
                bvect.add(bs);
            }

            Collection users = org.getUsers();
            System.out.println("?Org has users=" + users.isEmpty());
            Iterator it = users.iterator();
            while (it.hasNext())
            {
                Contact ct =
                        getContactFromJAXRUser((User) it.next());
                cvect.add(ct);
            }

            bss.setBusinessServiceVector(bvect);
            cts.setContactVector(cvect);
            biz.setContacts(cts);

            biz.setBusinessServices(bss);
        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return biz;
    }

    /**
     * Convert JAXR User Object to UDDI  Contact
     */
    private Contact getContactFromJAXRUser(User user)
            throws JAXRException
    {
        Contact ct = new Contact();
        Vector addvect = new Vector();
        Vector phonevect = new Vector();
        Vector emailvect = new Vector();
        try
        {
            ct.setPersonName(new PersonName(user.getPersonName().getFullName()));
            //Postal Address
            Collection postc = user.getPostalAddresses();
            Iterator iterator = postc.iterator();
            while (iterator.hasNext())
            {
                PostalAddress post = (PostalAddress) iterator.next();
                addvect.add(getAddress(post));
            }
            //Phone Numbers
            Collection ph = user.getTelephoneNumbers(null);
            Iterator it = ph.iterator();
            while (it.hasNext())
            {
                TelephoneNumber t = (TelephoneNumber) it.next();
                Phone phone = new Phone();
                String str = t.getNumber();
                System.out.println("Telephone=" + str);
                phone.setValue(str);
                // phone.setText( str );
                phonevect.add(phone);
            }

            //Email Addresses
            Collection ec = user.getEmailAddresses();
            Iterator iter = ec.iterator();
            while (iter.hasNext())
            {
                EmailAddress ea = (EmailAddress) iter.next();
                Email email = new Email();
                email.setValue(ea.getAddress());
                //email.setText( ea.getAddress() );
                email.setUseType(ea.getType());
                emailvect.add(email);
            }
            ct.setAddressVector(addvect);
            ct.setPhoneVector(phonevect);
            ct.setEmailVector(emailvect);
        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return ct;
    }

    /**
     * Get UDDI Address given JAXR Postal Address
     */
    private Address getAddress(PostalAddress post)
            throws JAXRException
    {
        Address address = new Address();

        Vector addvect = new Vector();

        String stnum = post.getStreetNumber();
        String st = post.getStreet();
        String city = post.getCity();
        String country = post.getCountry();
        String code = post.getPostalCode();
        String state = post.getStateOrProvince();

        AddressLine stnumAL = new AddressLine();
        stnumAL.setKeyName("STREET_NUMBER");
        stnumAL.setKeyValue(stnum);

        AddressLine stAL = new AddressLine();
        stAL.setKeyName("STREET");
        stAL.setKeyValue(st);

        AddressLine cityAL = new AddressLine();
        cityAL.setKeyName("CITY");
        cityAL.setKeyValue(city);

        AddressLine countryAL = new AddressLine();
        countryAL.setKeyName("COUNTRY");
        countryAL.setKeyValue(country);

        AddressLine codeAL = new AddressLine();
        codeAL.setKeyName("POSTALCODE");
        codeAL.setKeyValue(code);

        AddressLine stateAL = new AddressLine();
        stateAL.setKeyName("STATE");
        stateAL.setKeyValue(state);

        //Add the AddressLine to vector
        addvect.add(stnumAL);
        addvect.add(stAL);
        addvect.add(cityAL);
        addvect.add(countryAL);
        addvect.add(codeAL);
        addvect.add(stateAL);

        address.setAddressLineVector(addvect);

        return address;
    }

    private BindingTemplate getBindingTemplateFromJAXRSB(ServiceBinding serve)
            throws JAXRException
    {
        BindingTemplate bt = new BindingTemplate();
        try
        {
            InternationalStringImpl iname = (InternationalStringImpl) ((RegistryObject) serve).getName();
            String name = iname.getValue();
            //Set Access URI
            String accessuri = serve.getAccessURI();
            if (accessuri != null)
            {
                AccessPoint ap = new AccessPoint();
                ap.setURL(accessuri);
                bt.setAccessPoint(ap);
            }
            ServiceBinding sb = serve.getTargetBinding();
            if (sb != null)
            {
                HostingRedirector red = new HostingRedirector();
                Key key = sb.getKey();
                if( key != null) red.setBindingKey(key.getId());
                bt.setHostingRedirector(red);
            }
            //TODO:Need to look further at the mapping b/w BindingTemplate and Jaxr ServiceBinding


            bt.addDescription(new Description(((RegistryObject) serve).getDescription().getValue()));
            System.out.println("BindingTemplate=" + bt.toString());
        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return bt;
    }

    private BusinessService getBusinessServiceFromJAXRService(Service serve)
            throws JAXRException
    {
        BusinessService bs = new BusinessService();
        try
        {
            InternationalStringImpl iname = (InternationalStringImpl) ((RegistryObject) serve).getName();
            String name = iname.getValue();
            //bs.setDefaultNameString( name, Locale.getDefault().getLanguage());
            bs.addName(new Name(name, Locale.getDefault().getLanguage()));
            /**
             bs.setBusinessKey( ((RegistryObject) serve).getKey().getId() );
             **/

            bs.addDescription(new Description(((RegistryObject) serve).getDescription().getValue()));
            System.out.println("BusinessService=" + bs.toString());
        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return bs;
    }

    private TModel getTModelFromJAXRClassificationScheme(ClassificationScheme scheme)
            throws JAXRException
    {
        TModel tm = new TModel();
        try
        {
            tm.setTModelKey(scheme.getKey().getId());
            tm.setAuthorizedName(scheme.getSlot("authorizedName").getName());
            tm.setOperator(scheme.getSlot("operator").getName());


            InternationalStringImpl iname = (InternationalStringImpl) ((RegistryObject) scheme).getName();
            String name = iname.getValue();
            tm.setName(new Name(name, Locale.getDefault().getLanguage()));
            tm.addDescription(new Description( scheme.getDescription().getValue()));
            //ToDO:  overviewDoc,identifierBag,categoryBag
        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return tm;
    }

    private TModel getTModelFromJAXRConcept(Concept scheme)
            throws JAXRException
    {
        TModel tm = new TModel();
        if(scheme == null ) return null;
        try
        {
            Key key = scheme.getKey();
            if(key != null) tm.setTModelKey(key.getId());
            Slot sl1 = scheme.getSlot("authorizedName");
            if( sl1 != null ) tm.setAuthorizedName(sl1.getName());

            Slot sl2 = scheme.getSlot("operator");
            if( sl2 != null ) tm.setOperator(sl2.getName());


            InternationalStringImpl iname = (InternationalStringImpl) ((RegistryObject) scheme).getName();
            String name = iname.getValue();
            tm.setName(new Name(name, Locale.getDefault().getLanguage()));
            tm.addDescription(new Description( scheme.getDescription().getValue()));
            //ToDO:  overviewDoc,identifierBag,categoryBag
        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return tm;
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
