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
package org.apache.ws.scout.util;

import org.apache.juddi.datatype.Address;
import org.apache.juddi.datatype.AddressLine;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.DiscoveryURL;
import org.apache.juddi.datatype.Email;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.PersonName;
import org.apache.juddi.datatype.Phone;
import org.apache.juddi.datatype.binding.AccessPoint;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.binding.HostingRedirector;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.datatype.business.Contacts;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.datatype.response.TModelDetail;
import org.apache.juddi.datatype.response.TModelInfo;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.service.BusinessServices;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;
import org.apache.ws.scout.registry.infomodel.OrganizationImpl;
import org.apache.ws.scout.registry.infomodel.UserImpl;
import org.apache.ws.scout.registry.infomodel.PersonNameImpl;
import org.apache.ws.scout.registry.infomodel.ServiceImpl;
import org.apache.ws.scout.registry.infomodel.ConceptImpl;

import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.EmailAddress;
import javax.xml.registry.infomodel.ExternalLink;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.PostalAddress;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.Slot;
import javax.xml.registry.infomodel.TelephoneNumber;
import javax.xml.registry.infomodel.User;
import javax.xml.registry.infomodel.InternationalString;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

/**
 * Helper class that does UDDI->Jaxr Mapping
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ScoutUddiJaxrHelper
{
    public static Organization getOrganization(BusinessEntity entity,
                                                   LifeCycleManager lcm)
                throws JAXRException
        {
            Vector namevect = entity.getNameVector();
            Name n = (Name)namevect.elementAt(0);
            String name = n.getValue() ;
            Vector descvect = entity.getDescriptionVector();
            Description desc = (Description)descvect.elementAt(0);

            Organization org = new OrganizationImpl(lcm);
            org.setName(getIString(name,lcm));
            org.setDescription(getIString((String)desc.getValue(),lcm));
            org.setKey(lcm.createKey(entity.getBusinessKey()));

            //Set Services also
            BusinessServices services = entity.getBusinessServices();
            Vector svect = services.getBusinessServiceVector();
            for(int i=0; svect != null && i< svect.size();i++)
            {
                BusinessService s = (BusinessService)svect.elementAt(i);
                org.addService(getService(s,lcm));
            }
            //Get Contacts or Users
            Contacts contacts = entity.getContacts();
            Vector cvect = contacts.getContactVector();
            for(int i=0; cvect != null && i< cvect.size();i++)
            {
                Contact contact = (Contact)cvect.elementAt(i);
                User user = new UserImpl(null);
                String pname = contact.getPersonName().getValue();
                user.setPersonName(new PersonNameImpl(pname));
                org.addUser(user);
            }
            return org;
        }


    public static Organization getOrganization(BusinessDetail bizdetail,
                                               LifeCycleManager lcm)
            throws JAXRException
    {
        Vector bz = bizdetail.getBusinessEntityVector();
        BusinessEntity entity = (BusinessEntity) bz.elementAt(0);
        Vector namevect = entity.getNameVector();
        Name n = (Name)namevect.elementAt(0);
        String name = n.getValue() ;
        Vector descvect = entity.getDescriptionVector();
        Description desc = (Description)descvect.elementAt(0);

        Organization org = new OrganizationImpl(lcm);
        org.setName(getIString(name,lcm));
        org.setDescription(getIString((String)desc.getValue(),lcm));
        org.setKey(lcm.createKey(entity.getBusinessKey()));

        //Set Services also
        BusinessServices services = entity.getBusinessServices();
        Vector svect = services.getBusinessServiceVector();
        for(int i=0; svect != null && i< svect.size();i++)
        {
            BusinessService s = (BusinessService)svect.elementAt(i);
            org.addService(getService(s,lcm));
        }
        //Get Contacts or Users
        Contacts contacts = entity.getContacts();
        Vector cvect = contacts.getContactVector();
        for(int i=0; cvect != null && i< cvect.size();i++)
        {
            Contact contact = (Contact)cvect.elementAt(i);
            User user = new UserImpl(null);
            String pname = contact.getPersonName().getValue();
            user.setType( contact.getUseType());
            user.setPersonName(new PersonNameImpl(pname));
            org.addUser(user);
        }
        return org;
    }

    public static InternationalString getIString(String str, LifeCycleManager blm)
            throws JAXRException
    {
        return blm.createInternationalString(str);
    }

    public static Service getService(BusinessService bs,LifeCycleManager lcm )
            throws JAXRException
    {
        Service serve = new ServiceImpl(lcm);
        String keystr = bs.getBusinessKey();
        if(keystr != null ) serve.setKey(lcm.createKey(keystr));
        Vector namevect = bs.getNameVector();
        Name n = (Name)namevect.elementAt(0);
        String name = n.getValue() ;
        serve.setName(lcm.createInternationalString(name));
        Vector descvect = bs.getDescriptionVector();
        Description desc = (Description)descvect.elementAt(0);
        serve.setDescription(lcm.createInternationalString(desc.getValue()));
        return serve;
    }

    public static Concept getConcept(TModelDetail tm, LifeCycleManager lcm)
    throws JAXRException
    {
        Concept concept = new ConceptImpl(lcm);
        Vector tc = tm.getTModelVector();
        TModel tmodel = (TModel)tc.elementAt(0);
        concept.setKey(lcm.createKey(tmodel.getTModelKey()));
        concept.setName(lcm.createInternationalString( tmodel.getName() ) );

        Vector descvect = tmodel.getDescriptionVector();
        Description desc = (Description)descvect.elementAt(0);
        concept.setDescription(lcm.createInternationalString(desc.getValue()));

        return concept;
    }

    public static Concept getConcept(TModel tmodel, LifeCycleManager lcm)
    throws JAXRException
    {
        Concept concept = new ConceptImpl(lcm);
        concept.setKey(lcm.createKey(tmodel.getTModelKey()));
        concept.setName(lcm.createInternationalString( tmodel.getName() ) );

        Vector descvect = tmodel.getDescriptionVector();
        Description desc = (Description)descvect.elementAt(0);
        concept.setDescription(lcm.createInternationalString(desc.getValue()));

        return concept;
    }

    public static Concept getConcept(TModelInfo tm, LifeCycleManager lcm)
    throws JAXRException
    {
        Concept concept = new ConceptImpl(lcm);
        concept.setKey(lcm.createKey(tm.getTModelKey()));
        concept.setName(lcm.createInternationalString( tm.getName().getValue() ) );

        return concept;
    }

}
