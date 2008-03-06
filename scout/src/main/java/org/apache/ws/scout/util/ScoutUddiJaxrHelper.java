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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.EmailAddress;
import javax.xml.registry.infomodel.ExternalIdentifier;
import javax.xml.registry.infomodel.ExternalLink;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.PostalAddress;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.TelephoneNumber;
import javax.xml.registry.infomodel.User;

import org.apache.ws.scout.registry.infomodel.AssociationImpl;
import org.apache.ws.scout.registry.infomodel.ClassificationImpl;
import org.apache.ws.scout.registry.infomodel.ClassificationSchemeImpl;
import org.apache.ws.scout.registry.infomodel.ConceptImpl;
import org.apache.ws.scout.registry.infomodel.EmailAddressImpl;
import org.apache.ws.scout.registry.infomodel.ExternalIdentifierImpl;
import org.apache.ws.scout.registry.infomodel.ExternalLinkImpl;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;
import org.apache.ws.scout.registry.infomodel.KeyImpl;
import org.apache.ws.scout.registry.infomodel.OrganizationImpl;
import org.apache.ws.scout.registry.infomodel.PersonNameImpl;
import org.apache.ws.scout.registry.infomodel.PostalAddressImpl;
import org.apache.ws.scout.registry.infomodel.ServiceBindingImpl;
import org.apache.ws.scout.registry.infomodel.ServiceImpl;
import org.apache.ws.scout.registry.infomodel.SpecificationLinkImpl;
import org.apache.ws.scout.registry.infomodel.TelephoneNumberImpl;
import org.apache.ws.scout.registry.infomodel.UserImpl;
import org.apache.ws.scout.uddi.AccessPoint;
import org.apache.ws.scout.uddi.Address;
import org.apache.ws.scout.uddi.AddressLine;
import org.apache.ws.scout.uddi.BindingTemplate;
import org.apache.ws.scout.uddi.BindingTemplates;
import org.apache.ws.scout.uddi.BusinessDetail;
import org.apache.ws.scout.uddi.BusinessEntity;
import org.apache.ws.scout.uddi.BusinessService;
import org.apache.ws.scout.uddi.BusinessServices;
import org.apache.ws.scout.uddi.CategoryBag;
import org.apache.ws.scout.uddi.Contact;
import org.apache.ws.scout.uddi.Contacts;
import org.apache.ws.scout.uddi.Description;
import org.apache.ws.scout.uddi.DiscoveryURL;
import org.apache.ws.scout.uddi.DiscoveryURLs;
import org.apache.ws.scout.uddi.Email;
import org.apache.ws.scout.uddi.HostingRedirector;
import org.apache.ws.scout.uddi.IdentifierBag;
import org.apache.ws.scout.uddi.InstanceDetails;
import org.apache.ws.scout.uddi.KeyedReference;
import org.apache.ws.scout.uddi.Name;
import org.apache.ws.scout.uddi.OverviewDoc;
import org.apache.ws.scout.uddi.Phone;
import org.apache.ws.scout.uddi.ServiceInfo;
import org.apache.ws.scout.uddi.TModel;
import org.apache.ws.scout.uddi.TModelDetail;
import org.apache.ws.scout.uddi.TModelInfo;
import org.apache.ws.scout.uddi.TModelInstanceDetails;
import org.apache.ws.scout.uddi.TModelInstanceInfo;

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
      Name[] namearray = entity.getNameArray();
      Name n = namearray != null && namearray.length > 0 ? namearray[0] : null;
      String name = n != null ? n.getStringValue() : null;
      Description[] descarray = entity.getDescriptionArray();
      Description desc = descarray != null && descarray.length > 0 ? descarray[0]: null;

      Organization org = new OrganizationImpl(lcm);
      if(name != null ) {
          org.setName(getIString(n.getLang(), name, lcm));
      }
      if( desc != null) {
          org.setDescription(getIString(desc.getLang(), desc.getStringValue(), lcm));
      }
      org.setKey(lcm.createKey(entity.getBusinessKey()));

      //Set Services also
      BusinessServices services = entity.getBusinessServices();
      BusinessService[] sarr = services != null ? services.getBusinessServiceArray() : null;
      for (int i = 0; sarr != null && i < sarr.length; i++)
      {
         BusinessService s = (BusinessService)sarr[i];
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
      Contact[] carr = contacts != null ? contacts.getContactArray() : null;

      for (int i = 0; carr != null && i < carr.length; i++)
      {
         Contact contact = (Contact)carr[i];
         User user = new UserImpl(null);
         String pname = contact.getPersonName();
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
         DiscoveryURL[] darr = durls.getDiscoveryURLArray();
         for (int j = 0; darr != null && j < darr.length; j++)
         {
            DiscoveryURL durl = (DiscoveryURL)darr[j];
            ExternalLink link = new ExternalLinkImpl(lcm);
            link.setExternalURI(durl.getStringValue());
            org.addExternalLink(link);
         }
      }

      org.addExternalIdentifiers(getExternalIdentifiers(entity.getIdentifierBag(), lcm));
      org.addClassifications(getClassifications(entity.getCategoryBag(), lcm));
      
      return org;
   }


   public static Organization getOrganization(BusinessDetail bizdetail,
                                              LifeCycleManager lcm)
           throws JAXRException
   {
      BusinessEntity[] bz = bizdetail.getBusinessEntityArray();

      BusinessEntity entity = bz[0];
      Name[] namearr = entity.getNameArray();
      Name n = namearr != null && namearr.length > 0 ? namearr[0] : null;
      String name = n != null ? n.getStringValue(): null;
      Description[] descarr = entity.getDescriptionArray();
      Description desc = descarr != null && descarr.length > 0 ? descarr[0] : null;

      Organization org = new OrganizationImpl(lcm);
      if( name != null ) {
          org.setName(getIString(n.getLang(), name, lcm));
      }
      if( desc != null ) {
          org.setDescription(getIString(desc.getLang(), desc.getStringValue(), lcm));
      }
      org.setKey(lcm.createKey(entity.getBusinessKey()));

      //Set Services also
      BusinessServices services = entity.getBusinessServices();
      BusinessService[] sarr = services != null ? services.getBusinessServiceArray() : null;
      for (int i = 0; sarr != null && i < sarr.length; i++)
      {
         BusinessService s = (BusinessService)sarr[i];
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
      Contact[] carr = contacts != null ? contacts.getContactArray():null;
      for (int i = 0; carr != null && i < carr.length; i++)
      {
         Contact contact = carr[i];
         User user = new UserImpl(null);
         String pname = contact.getPersonName();
         user.setType(contact.getUseType());
         user.setPersonName(new PersonNameImpl(pname));
         

         Email[] emails = (Email[]) contact.getEmailArray();
         ArrayList<EmailAddress> tempEmails = new ArrayList<EmailAddress>();
         for (int x = 0; x < emails.length; x++) {
        	 tempEmails.add(new EmailAddressImpl(emails[x].getStringValue(), null));
         }
         user.setEmailAddresses(tempEmails);
         
         Address[] addresses = (Address[]) contact.getAddressArray();
         ArrayList<PostalAddress> tempAddresses = new ArrayList<PostalAddress>();
         for (int x = 0; x < addresses.length; x++) {
        	 AddressLine[] alines = addresses[x].getAddressLineArray();
        	 PostalAddress pa = getPostalAddress(alines);
        	 tempAddresses.add(pa);
         }
         user.setPostalAddresses(tempAddresses);
         Phone[] phones = contact.getPhoneArray();
         ArrayList<TelephoneNumber> tempPhones = new ArrayList<TelephoneNumber>();
         for (int x = 0; x < phones.length; x++) {
        	 TelephoneNumberImpl tni = new TelephoneNumberImpl();
        	 tni.setType(phones[x].getUseType());
        	 tni.setNumber(phones[x].getStringValue());
        	 tempPhones.add(tni);
         }
         user.setTelephoneNumbers(tempPhones);
         
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
         DiscoveryURL[] darr = durls.getDiscoveryURLArray();
         for (int j = 0; darr != null && j < darr.length; j++)
         {
            DiscoveryURL durl = darr[j];
            ExternalLink link = new ExternalLinkImpl(lcm);
            link.setExternalURI(durl.getStringValue());
            org.addExternalLink(link);
         }
      }

      org.addExternalIdentifiers(getExternalIdentifiers(entity.getIdentifierBag(), lcm));
      org.addClassifications(getClassifications(entity.getCategoryBag(), lcm));
      
      return org;
   }

   private static PostalAddress getPostalAddress(AddressLine[] al) throws JAXRException {
	   PostalAddress pa = new PostalAddressImpl();
	   HashMap<String, String> hm = new HashMap<String, String>();
	   for (int y = 0; y < al.length; y++) {
		   hm.put(al[y].getKeyName(), al[y].getKeyValue());
	   }        	 
	   
	   if (hm.containsKey("STREET_NUMBER")) {
		   pa.setStreetNumber(hm.get("STREET_NUMBER"));
	   }

	   if (hm.containsKey("STREET")) {
		   pa.setStreet(hm.get("STREET"));
	   }
	   
	   if (hm.containsKey("CITY")) {
		   pa.setCity(hm.get("CITY"));
	   }
	   
	   if (hm.containsKey("COUNTRY")) {
		   pa.setCountry(hm.get("COUNTRY"));
	   }
	
	   if (hm.containsKey("POSTALCODE")) {
		   pa.setPostalCode(hm.get("POSTALCODE"));
	   }
	   
	   if (hm.containsKey("STATE")) {
		   pa.setStateOrProvince(hm.get("STATE"));
	   }
	   
	   return pa;
   }
   
   private static InternationalString getIString(String lang, String str, LifeCycleManager blm)
       throws JAXRException
   {
       return blm.createInternationalString(getLocale(lang), str);
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

      Name[] namearr = bs.getNameArray();

      Name n = namearr != null && namearr.length > 0 ? namearr[0] : null;

      if (n != null) {
    	  String name = n.getStringValue();
    	  serve.setName(lcm.createInternationalString(getLocale(n.getLang()), name));
      }

      Description[] descarr = bs.getDescriptionArray();
      Description desc = descarr != null && descarr.length > 0 ? descarr[0] : null;
      if (desc != null ) {
          serve.setDescription(lcm.createInternationalString(getLocale(desc.getLang()), desc.getStringValue()));
      }
      
      //Populate the ServiceBindings for this Service
      BindingTemplates bts = bs.getBindingTemplates();
      BindingTemplate[] btarr = bts != null ? bts.getBindingTemplateArray() : null;
      for (int i = 0; btarr != null && i < btarr.length; i++)
      {
    	  BindingTemplate bindingTemplate = (BindingTemplate)btarr[i];
          serve.addServiceBinding(getServiceBinding(bindingTemplate, lcm));
      }
      
      serve.addClassifications(getClassifications(bs.getCategoryBag(), lcm));
      
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

      Name[] namearr = si.getNameArray();
      Name n = namearr != null && namearr.length > 0 ? namearr[0] : null;

      if (n != null) {
    	  String name = n.getStringValue();
    	  service.setName(lcm.createInternationalString(getLocale(n.getLang()), name));
      }

      return service;
   }

   public static ServiceBinding getServiceBinding(BindingTemplate bs, LifeCycleManager lcm)
           throws JAXRException
   {
      ServiceBinding serviceBinding = new ServiceBindingImpl(lcm);

      String keystr = bs.getServiceKey();
      if (keystr != null)
      {
         Service svc = new ServiceImpl(lcm);
         svc.setKey(lcm.createKey(keystr));
         ((ServiceBindingImpl)serviceBinding).setService(svc);
      }
      String bindingKey = bs.getBindingKey();
      if(bindingKey != null) serviceBinding.setKey(new KeyImpl(bindingKey));
     
      //Access URI
      AccessPoint access = bs.getAccessPoint();
      if (access != null) serviceBinding.setAccessURI(access.getStringValue());

      //Description
      Description[] da = bs.getDescriptionArray();
      if (da != null && da.length > 0)
      {
         Description des = da[0];
         serviceBinding.setDescription(new InternationalStringImpl(des.getStringValue()));
      }
      /**Section D.10 of JAXR 1.0 Specification */
      
      TModelInstanceDetails details = bs.getTModelInstanceDetails();
      TModelInstanceInfo[] tmodelInstanceInfoArray = details.getTModelInstanceInfoArray();
      for (int i = 0; tmodelInstanceInfoArray != null && i < tmodelInstanceInfoArray.length; i++)
      {
         TModelInstanceInfo info = (TModelInstanceInfo)tmodelInstanceInfoArray[i];
         InstanceDetails idetails = info.getInstanceDetails(); 
         Collection<ExternalLink> elinks = getExternalLinks(idetails.getOverviewDoc(),lcm);
         SpecificationLinkImpl slink = new SpecificationLinkImpl(lcm);
         slink.addExternalIdentifiers(elinks);
         serviceBinding.addSpecificationLink(slink); 
         
         ConceptImpl c = new ConceptImpl(lcm);
         c.setExternalLinks(elinks);
         c.setKey(lcm.createKey(info.getTModelKey())); 
         c.setName(lcm.createInternationalString(idetails.getInstanceParms()));
         c.setValue(idetails.getInstanceParms());
         
         slink.setSpecificationObject(c);
      }
      
      HostingRedirector hr = bs.getHostingRedirector();
      if(hr != null)
      {
         ServiceBinding sb = lcm.createServiceBinding();
         sb.setKey(new KeyImpl(hr.getBindingKey()));
         serviceBinding.setTargetBinding(sb);
      }

      return serviceBinding;
   }

   public static Concept getConcept(TModelDetail tm, LifeCycleManager lcm)
           throws JAXRException
   {
      Concept concept = new ConceptImpl(lcm);
      TModel[] tc = tm.getTModelArray();
      TModel tmodel = tc != null && tc.length > 0 ? tc[0] : null;
      
      if (tmodel != null) {
    	  concept.setKey(lcm.createKey(tmodel.getTModelKey()));
    	  concept.setName(lcm.createInternationalString(getLocale(tmodel.getName().getLang()), tmodel.getName().getStringValue()));

    	  Description desc = getDescription(tmodel);
    	  if( desc != null ) {
    	      concept.setDescription(lcm.createInternationalString(getLocale(desc.getLang()), desc.getStringValue()));
    	  }

          concept.addExternalIdentifiers(getExternalIdentifiers(tmodel.getIdentifierBag(), lcm));
          concept.addClassifications(getClassifications(tmodel.getCategoryBag(), lcm));
      }
      return concept;
   }

   public static Concept getConcept(TModel tmodel, LifeCycleManager lcm)
           throws JAXRException
   {
      Concept concept = new ConceptImpl(lcm);
      concept.setKey(lcm.createKey(tmodel.getTModelKey()));
      concept.setName(lcm.createInternationalString(getLocale(tmodel.getName().getLang()), tmodel.getName().getStringValue()));

      Description desc = getDescription(tmodel);
      if (desc != null) {
          concept.setDescription(lcm.createInternationalString(getLocale(desc.getLang()), desc.getStringValue()));
      }
      
      concept.addExternalIdentifiers(getExternalIdentifiers(tmodel.getIdentifierBag(), lcm));
      concept.addClassifications(getClassifications(tmodel.getCategoryBag(), lcm));

      return concept;
   }

   public static Concept getConcept(TModelInfo tm, LifeCycleManager lcm)
           throws JAXRException
   {
      Concept concept = new ConceptImpl(lcm);
      concept.setKey(lcm.createKey(tm.getTModelKey()));
      concept.setName(lcm.createInternationalString(getLocale(tm.getName().getLang()), tm.getName().getStringValue()));

      return concept;
   }

   private static Description getDescription( TModel tmodel )
   {
      Description[] descarr = tmodel.getDescriptionArray();
      Description desc = descarr != null && descarr.length > 0 ? descarr[0] : null;
      return desc;
   }

   /**
    * Classifications - going to assume all are external since UDDI does not use "Concepts".
    * 
    * @param cbag
    * @param destinationObj
    * @param lcm
    * @throws JAXRException
    */
   public static Collection getClassifications(CategoryBag cbag, LifeCycleManager lcm) throws JAXRException {
	   Collection<Classification> classifications = null;
	   if (cbag != null) {
		    classifications = new ArrayList<Classification>();
			KeyedReference[] keyrarr = cbag.getKeyedReferenceArray();
			for (int i = 0; keyrarr != null && i < keyrarr.length; i++)
			{
				KeyedReference keyr = (KeyedReference)keyrarr[i];
				Classification classification = new ClassificationImpl(lcm);
				classification.setValue(keyr.getKeyValue());
				classification.setName(new InternationalStringImpl(keyr.getKeyName()));
				 
				String tmodelKey = keyr.getTModelKey();
				if (tmodelKey != null) {
					ClassificationScheme scheme = new ClassificationSchemeImpl(lcm);
					scheme.setKey(new KeyImpl(tmodelKey));
					classification.setClassificationScheme(scheme);
				}
				classifications.add(classification);
			}
		}
	    return classifications;
	}
   
   public static Collection<ExternalLink> getExternalLinks(OverviewDoc odoc , LifeCycleManager lcm)
   throws JAXRException
   {
       ArrayList<ExternalLink> alist = new ArrayList<ExternalLink>(1);
       if(odoc != null)
       {
           Description[] descVect = odoc.getDescriptionArray();
           String desc = "";
           if(descVect != null && descVect.length > 0) {
             desc = ((Description)descVect[0]).getStringValue(); 
           }
           alist.add(lcm.createExternalLink(odoc.getOverviewURL(),desc));
       }
       
       return alist;
   }
   
   /**
    * External Identifiers
    * 
    * @param ibag
    * @param destinationObj
    * @param lcm
    * @throws JAXRException
    */
   public static Collection<ExternalIdentifier> getExternalIdentifiers(IdentifierBag ibag, LifeCycleManager lcm) throws JAXRException {
	  Collection<ExternalIdentifier> extidentifiers = null;
      if (ibag != null) {
    	  extidentifiers = new ArrayList<ExternalIdentifier>();
          KeyedReference[] keyrarr = ibag.getKeyedReferenceArray();
          for (int i = 0; keyrarr != null && i < keyrarr.length; i++)
          {
             KeyedReference keyr = (KeyedReference)keyrarr[i];
             ExternalIdentifier extId = new ExternalIdentifierImpl(lcm);
             extId.setValue(keyr.getKeyValue());
             extId.setName(new InternationalStringImpl(keyr.getKeyName()));
             
             String tmodelKey = keyr.getTModelKey();
             if (tmodelKey != null) {
            	 ClassificationScheme scheme = new ClassificationSchemeImpl(lcm);
            	 scheme.setKey(new KeyImpl(tmodelKey));
            	 extId.setIdentificationScheme(scheme);
             }
             extidentifiers.add(extId);
          }
      }
      return extidentifiers;
   }
   
   private static Locale getLocale(String lang) {
       if (lang == null || lang.trim().length() == 0) {
           return Locale.getDefault();
       } else if (lang.equalsIgnoreCase(Locale.getDefault().getLanguage())) {
           return Locale.getDefault();
       } else {
           return new Locale(lang);
       } 
   }
}
