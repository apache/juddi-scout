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

import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.DiscoveryURL;
import org.apache.juddi.datatype.DiscoveryURLs;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.binding.AccessPoint;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.binding.TModelInstanceDetails;
import org.apache.juddi.datatype.binding.TModelInstanceInfo;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.datatype.business.Contacts;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.datatype.response.ServiceInfo;
import org.apache.juddi.datatype.response.TModelDetail;
import org.apache.juddi.datatype.response.TModelInfo;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.service.BusinessServices;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.ws.scout.registry.infomodel.*;

import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.infomodel.*;
import java.util.Collection;
import java.util.Vector;

/**
 * Helper class that does UDDI->Jaxr Mapping
 *
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 */
public class ScoutUddiJaxrHelper
{
   public static Association getAssociation(Collection orgs,
                                            LifeCycleManager lcm)
           throws JAXRException
   {
      Association asso = new AssociationImpl(lcm);
      Object[] arr = orgs.toArray();
      asso.setSourceObject((RegistryObject)arr[0]);
      asso.setTargetObject((RegistryObject)arr[1]);
      return asso;
   }

   public static Organization getOrganization(BusinessEntity entity,
                                              LifeCycleManager lcm)
           throws JAXRException
   {
      Vector namevect = entity.getNameVector();
      Name n = namevect != null ? (Name)namevect.elementAt(0) : null;
      String name = n != null ? n.getValue() : null;
      Vector descvect = entity.getDescriptionVector();
      Description desc = descvect != null ? (Description)descvect.elementAt(0): null;

      Organization org = new OrganizationImpl(lcm);
      if(name != null ) org.setName(getIString(name, lcm));
      if( desc != null) org.setDescription(getIString((String)desc.getValue(), lcm));
      org.setKey(lcm.createKey(entity.getBusinessKey()));

      //Set Services also
      BusinessServices services = entity.getBusinessServices();
      Vector svect = services.getBusinessServiceVector();
      for (int i = 0; svect != null && i < svect.size(); i++)
      {
         BusinessService s = (BusinessService)svect.elementAt(i);
         org.addService(getService(s, lcm));
      }

      /*
       *  Users
       *
       *  we need to take the first contact and designate as the
       *  'primary contact'.  Currently, the OrganizationImpl
       *  class does that automatically as a safety in case
       *  user forgets to set - lets be explicit here as to not
       *  depend on that behavior
       */

      Contacts contacts = entity.getContacts();
      Vector cvect = contacts.getContactVector();

      for (int i = 0; cvect != null && i < cvect.size(); i++)
      {
         Contact contact = (Contact)cvect.elementAt(i);
         User user = new UserImpl(null);
         String pname = contact.getPersonName().getValue();
         user.setPersonName(new PersonNameImpl(pname));

         if (i == 0)
         {
            org.setPrimaryContact(user);
         }
         else
         {
            org.addUser(user);
         }
      }

      //External Links
      DiscoveryURLs durls = entity.getDiscoveryURLs();
      if (durls != null)
      {
         Vector dvect = durls.getDiscoveryURLVector();
         for (int j = 0; j < dvect.size(); j++)
         {
            DiscoveryURL durl = (DiscoveryURL)dvect.elementAt(j);
            ExternalLink link = new ExternalLinkImpl(lcm);
            link.setExternalURI(durl.getValue());
            org.addExternalLink(link);
         }
      }


      //External Identifiers
      IdentifierBag ibag = entity.getIdentifierBag();
      if (ibag != null)
      {
         Vector keyrvect = ibag.getKeyedReferenceVector();
         for (int i = 0; i < keyrvect.size(); i++)
         {
            KeyedReference keyr = (KeyedReference)keyrvect.elementAt(i);
            ExternalIdentifier eid = new ExternalIdentifierImpl(lcm);
            String kkey = keyr.getTModelKey();
            if (kkey != null) eid.setKey(new KeyImpl(kkey));
            eid.setValue(keyr.getKeyValue());
            eid.setName(new InternationalStringImpl(keyr.getKeyName()));
            org.addExternalIdentifier(eid);
         }
      }
      return org;
   }


   public static Organization getOrganization(BusinessDetail bizdetail,
                                              LifeCycleManager lcm)
           throws JAXRException
   {
      Vector bz = bizdetail.getBusinessEntityVector();

      BusinessEntity entity = (BusinessEntity)bz.elementAt(0);
      Vector namevect = entity.getNameVector();
      Name n = namevect != null ? (Name)namevect.elementAt(0) : null;
      String name = n != null ? n.getValue(): null;
      Vector descvect = entity.getDescriptionVector();
      Description desc = descvect != null? (Description)descvect.elementAt(0) : null;

      Organization org = new OrganizationImpl(lcm);
      if( name != null ) org.setName(getIString(name, lcm));
      if( desc != null ) org.setDescription(getIString((String)desc.getValue(), lcm));
      org.setKey(lcm.createKey(entity.getBusinessKey()));

      //Set Services also
      BusinessServices services = entity.getBusinessServices();
      Vector svect = services.getBusinessServiceVector();
      for (int i = 0; svect != null && i < svect.size(); i++)
      {
         BusinessService s = (BusinessService)svect.elementAt(i);
         org.addService(getService(s, lcm));
      }

      /*
       *  Users
       *
       *  we need to take the first contact and designate as the
       *  'primary contact'.  Currently, the OrganizationImpl
       *  class does that automatically as a safety in case
       *  user forgets to set - lets be explicit here as to not
       *  depend on that behavior
       */
      Contacts contacts = entity.getContacts();
      Vector cvect = contacts != null ? contacts.getContactVector():null;
      for (int i = 0; cvect != null && i < cvect.size(); i++)
      {
         Contact contact = (Contact)cvect.elementAt(i);
         User user = new UserImpl(null);
         String pname = contact.getPersonName().getValue();
         user.setType(contact.getUseType());
         user.setPersonName(new PersonNameImpl(pname));

         if (i == 0)
         {
            org.setPrimaryContact(user);
         }
         else
         {
            org.addUser(user);
         }
      }

      //External Links
      DiscoveryURLs durls = entity.getDiscoveryURLs();
      if (durls != null)
      {
         Vector dvect = durls.getDiscoveryURLVector();
         for (int j = 0; j < dvect.size(); j++)
         {
            DiscoveryURL durl = (DiscoveryURL)dvect.elementAt(j);
            ExternalLink link = new ExternalLinkImpl(lcm);
            link.setExternalURI(durl.getValue());
            org.addExternalLink(link);
         }
      }

      //External Identifiers
      IdentifierBag ibag = entity.getIdentifierBag();
      if (ibag != null)
      {
         Vector keyrvect = ibag.getKeyedReferenceVector();
         for (int i = 0; i < keyrvect.size(); i++)
         {
            KeyedReference keyr = (KeyedReference)keyrvect.elementAt(i);
            ExternalIdentifier eid = new ExternalIdentifierImpl(lcm);
            String kkey = keyr.getTModelKey();
            if (kkey != null) eid.setKey(new KeyImpl(kkey));
            eid.setValue(keyr.getKeyValue());
            eid.setName(new InternationalStringImpl(keyr.getKeyName()));
            org.addExternalIdentifier(eid);
         }
      }
      return org;
   }

   public static InternationalString getIString(String str, LifeCycleManager blm)
           throws JAXRException
   {
      return blm.createInternationalString(str);
   }

   public static Service getService(BusinessService bs, LifeCycleManager lcm)
           throws JAXRException
   {
      Service serve = new ServiceImpl(lcm);

      String keystr = bs.getServiceKey();

      if (keystr != null)
      {
         serve.setKey(lcm.createKey(keystr));
      }

      Vector namevect = bs.getNameVector();

      Name n = (Name)namevect.elementAt(0);
      String name = n.getValue();
      serve.setName(lcm.createInternationalString(name));
      Vector descvect = bs.getDescriptionVector();
      Description desc = descvect != null ? (Description)descvect.elementAt(0) : null;
      if(desc != null ) serve.setDescription(lcm.createInternationalString(desc.getValue()));
      return serve;
   }

   public static Service getService(ServiceInfo si, LifeCycleManager lcm)
           throws JAXRException
   {
      Service service = new ServiceImpl(lcm);

      String keystr = si.getServiceKey();

      if (keystr != null)
      {
         service.setKey(lcm.createKey(keystr));
      }

      Vector namevect = si.getNameVector();
      Name n = (Name)namevect.elementAt(0);
      String name = n.getValue();
      service.setName(lcm.createInternationalString(name));

      return service;
   }

   public static ServiceBinding getServiceBinding(BindingTemplate bs, LifeCycleManager lcm)
           throws JAXRException
   {
      ServiceBinding serve = new ServiceBindingImpl(lcm);


      TModelInstanceDetails details = bs.getTModelInstanceDetails();
      Vector tiv = details.getTModelInstanceInfoVector();
      for (int i = 0; tiv != null && i < tiv.size(); i++)
      {
         TModelInstanceInfo info = (TModelInstanceInfo)tiv.elementAt(i);
      }
      String keystr = bs.getServiceKey();
      if (keystr != null)
      {
         Service svc = new ServiceImpl(lcm);
         svc.setKey(lcm.createKey(keystr));
         ((ServiceBindingImpl)serve).setService(svc);
      }
      String bindingKey = bs.getBindingKey();
      if(bindingKey != null) serve.setKey(new KeyImpl(bindingKey));
      //TODO:Add more stuff
      //Access URI
      AccessPoint access = bs.getAccessPoint();
      if (access != null) serve.setAccessURI(access.getURL());

      //Description
      Vector dv = bs.getDescriptionVector();
      if (dv != null)
      {
         Description des = (Description)dv.elementAt(0);
         serve.setDescription(new InternationalStringImpl(des.getValue()));
      }

      return serve;
   }

   public static Concept getConcept(TModelDetail tm, LifeCycleManager lcm)
           throws JAXRException
   {
      Concept concept = new ConceptImpl(lcm);
      Vector tc = tm.getTModelVector();
      TModel tmodel = (TModel)tc.elementAt(0);
      concept.setKey(lcm.createKey(tmodel.getTModelKey()));
      concept.setName(lcm.createInternationalString(tmodel.getName()));

      Vector descvect = tmodel.getDescriptionVector();
      Description desc = getDescription(tmodel);
      if( desc != null ) concept.setDescription(lcm.createInternationalString(desc.getValue()));

      return concept;
   }

   public static Concept getConcept(TModel tmodel, LifeCycleManager lcm)
           throws JAXRException
   {
      Concept concept = new ConceptImpl(lcm);
      concept.setKey(lcm.createKey(tmodel.getTModelKey()));
      concept.setName(lcm.createInternationalString(tmodel.getName()));

      Vector descvect = tmodel.getDescriptionVector();
      Description desc = getDescription(tmodel);
      concept.setDescription(lcm.createInternationalString(desc.getValue()));

      return concept;
   }

   public static Concept getConcept(TModelInfo tm, LifeCycleManager lcm)
           throws JAXRException
   {
      Concept concept = new ConceptImpl(lcm);
      concept.setKey(lcm.createKey(tm.getTModelKey()));
      concept.setName(lcm.createInternationalString(tm.getName().getValue()));

      return concept;
   }

   private static Description getDescription( TModel tmodel )
   {
      Vector descvect = tmodel.getDescriptionVector();
      Description desc = descvect != null ? (Description)descvect.elementAt(0) : null;
      return desc;
   }

}
