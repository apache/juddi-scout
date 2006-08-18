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

import org.apache.juddi.datatype.*;
import org.apache.juddi.datatype.PersonName;
import org.apache.juddi.datatype.assertion.PublisherAssertion;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.business.Contacts;
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.service.BusinessServices;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.binding.AccessPoint;
import org.apache.juddi.datatype.binding.BindingTemplates;
import org.apache.juddi.datatype.binding.HostingRedirector;
import org.apache.juddi.datatype.binding.TModelInstanceDetails;
import org.apache.juddi.datatype.binding.TModelInstanceInfo;
import org.apache.log4j.Logger;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;

import javax.xml.registry.infomodel.*;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.JAXRException;
import java.util.Vector;
import java.util.Locale;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Helper class that does Jaxr->UDDI Mapping
 *
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 */
public class ScoutJaxrUddiHelper
{
	private static Logger log = Logger.getLogger(ScoutJaxrUddiHelper.class);
	
	//Some constants
	private static final String UDDI_ORG_TYPES = "uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4";
	
    /**
     * Get UDDI Address given JAXR Postal Address
     */
    public static Address getAddress(PostalAddress post)
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

    public static BindingTemplate getBindingTemplateFromJAXRSB(ServiceBinding serve)
            throws JAXRException
    {
        BindingTemplate bt = new BindingTemplate();
        if( serve.getKey() != null) bt.setBindingKey(serve.getKey().getId());
        try
        {
            //Set Access URI
            String accessuri = serve.getAccessURI();
            if (accessuri != null)
            {
                AccessPoint ap = new AccessPoint();
                ap.setURLType(getURLType(accessuri));
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

           //Get Service information
           Service svc = serve.getService();
           if( svc != null)
           {
              bt.setServiceKey(svc.getKey().getId());
           }

            bt.addDescription(new Description(((RegistryObject) serve).getDescription().getValue()));

           //SpecificationLink
           Collection slcol = serve.getSpecificationLinks();
           TModelInstanceDetails tid = new TModelInstanceDetails();
           if(slcol != null && slcol.isEmpty() != false)
           {
              Iterator iter = slcol.iterator();
              while(iter.hasNext())
              {
                 SpecificationLink slink = (SpecificationLink)iter.next();

                 TModelInstanceInfo tinfo = new TModelInstanceInfo();
                 tinfo.setTModelKey(slink.getSpecificationObject().getKey().getId());
                 tid.addTModelInstanceInfo(tinfo); 
              }
              bt.setTModelInstanceDetails(tid);
           }
           log.debug("BindingTemplate=" + bt.toString());
        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return bt;
    }

    public static PublisherAssertion getPubAssertionFromJAXRAssociation(Association assc)
            throws JAXRException
    {
        PublisherAssertion pa = new PublisherAssertion();
        try
        {
            pa.setFromKey(assc.getSourceObject().getKey().getId());
            pa.setToKey(assc.getTargetObject().getKey().getId());
            Concept c = assc.getAssociationType();
            String v = c.getValue();
            KeyedReference kr = new KeyedReference();
            Key key = c.getKey();
            if(key == null )
            {
                //TODO:Need to check this.  If the concept is a predefined
                //enumeration, the key can be the parent classification scheme
                key = c.getClassificationScheme().getKey();
            }
            if(key != null)
              kr.setTModelKey(key.getId());
            kr.setKeyName("Concept");
            kr.setKeyValue(v);
            pa.setKeyedReference(kr);
        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return pa;
    }

    public static PublisherAssertion getPubAssertionFromJAXRAssociationKey(String key)
            throws JAXRException
    {
        PublisherAssertion pa = new PublisherAssertion();
        try
        {
            StringTokenizer token = new  StringTokenizer(key,":");
            if(token.hasMoreTokens())
            {
               pa.setFromKey(getToken(token.nextToken()));
               pa.setToKey(getToken(token.nextToken()));
               KeyedReference kr = new KeyedReference();
               //Sometimes the Key is UUID:something
               String str = getToken(token.nextToken());
               if("UUID".equals(str)) str += ":" + getToken(token.nextToken());
               kr.setTModelKey(str);
               kr.setKeyName(getToken(token.nextToken()));
               kr.setKeyValue(getToken(token.nextToken()));
               pa.setKeyedReference(kr);
            }

        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return pa;
    }

    public static BusinessService getBusinessServiceFromJAXRService(Service serve)
            throws JAXRException
    {
        BusinessService bs = new BusinessService();
        try
        {
            InternationalStringImpl iname = (InternationalStringImpl) ((RegistryObject) serve).getName();
            String name = iname.getValue();
            //bs.setDefaultNameString( name, Locale.getDefault().getLanguage());
            bs.addName(new Name(name, Locale.getDefault().getLanguage()));
            bs.addDescription(new Description(((RegistryObject) serve).getDescription().getValue()));

            Organization o = serve.getProvidingOrganization();

            /*
             * there may not always be a key...
             */
            if (o != null) {
                Key k = o.getKey();

                if (k != null) {
                    bs.setBusinessKey(k.getId());
                }
            }
            else {
                /*
                 * gmj - I *think* this is the right thing to do
                 */
                throw new JAXRException("Service has no associated organization");
            }

            if (serve.getKey() != null) {
                bs.setServiceKey(serve.getKey().getId());
            }
            
            //Add the ServiceBinding information
            bs.setBindingTemplates(getBindingTemplates(serve.getServiceBindings()));

            log.debug("BusinessService=" + bs.toString());
        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return bs;
    }

    public static TModel getTModelFromJAXRClassificationScheme(ClassificationScheme scheme)
            throws JAXRException
    {
        TModel tm = new TModel();
        try
        {
            /*
             * a fresh scheme might not have a key
             */

            Key k = scheme.getKey();

            if(k != null) {
                tm.setTModelKey(k.getId());

            }

            /*
             * There's no reason to believe these are here either
             */

            Slot s = scheme.getSlot("authorizedName");

            if (s != null) {
                tm.setAuthorizedName(s.getName());
            }

            s = scheme.getSlot("operator");

            if (s != null) {
                tm.setOperator(s.getName());
            }

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

    public static TModel getTModelFromJAXRConcept(Concept scheme)
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
             
            //External Links 
            Collection externalLinks = scheme.getExternalLinks(); 
            if(externalLinks != null && externalLinks.size() > 0)
            {
            	tm.setOverviewDoc(getOverviewDocFromExternalLink((ExternalLink)externalLinks.iterator().next()));
            }  
            
            //External Identifiers 
            IdentifierBag idBag = new IdentifierBag();
            idBag.setKeyedReferenceVector(getKeyedReferenceVector(scheme.getExternalIdentifiers()));
            tm.setIdentifierBag(idBag);
            
            //Classifications
            Collection classifications = scheme.getClassifications();
            if(classifications != null && classifications.isEmpty() == false)
            {
            	//Spec says from Concept, always assume the uddi-org:types
            	tm.setCategoryBag(getCategoryBagFromConcept()); 
            } 
        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return tm;
    }

    public static BusinessEntity getBusinessEntityFromJAXROrg(Organization org)
            throws JAXRException
    {
        BusinessEntity biz = new BusinessEntity();
        BusinessServices bss = new BusinessServices();
        Vector bvect = new Vector();

        try
        {
            //It may just be an update
            Key key = org.getKey();
            if(key  != null ) biz.setBusinessKey(key.getId());
            //Lets get the Organization attributes at the top level
            String language = Locale.getDefault().getLanguage();

            biz.addName(new Name(org.getName().getValue(), language));
            biz.addDescription(new Description(org.getDescription().getValue()));
            if(org.getPrimaryContact() != null )
                biz.setAuthorizedName(org.getPrimaryContact().getPersonName().getFullName());

            Collection s = org.getServices();
            log.debug("?Org has services=" + s.isEmpty());
            Iterator iter = s.iterator();
            while (iter.hasNext())
            {
                BusinessService bs =
                        ScoutJaxrUddiHelper.getBusinessServiceFromJAXRService((Service) iter.next());
                bvect.add(bs);
            }

            /*
             * map users : JAXR has concept of 'primary contact', which is a
             * special designation for one of the users, and D6.1 seems to say
             * that the first UDDI user is the primary contact
             */

            Contacts cts = new Contacts();
            Vector cvect = new Vector();

            User primaryContact = org.getPrimaryContact();
            Collection users = org.getUsers(); 
            
            /*
             * first do primary, and then filter that out in the loop
             */
            if (primaryContact != null) {
                Contact ct = getContactFromJAXRUser(primaryContact);
                cvect.add(ct);
            }

            Iterator it = users.iterator();
            while (it.hasNext())
            {
                User u = (User) it.next();

                if (u != primaryContact) {
                    Contact ct = getContactFromJAXRUser(u);
                    cvect.add(ct);
                }
            }

            bss.setBusinessServiceVector(bvect);
            cts.setContactVector(cvect);
            biz.setContacts(cts);

            biz.setBusinessServices(bss);

            //External Links
            Iterator exiter = org.getExternalLinks().iterator();
            while(exiter.hasNext())
            {
               ExternalLink link = (ExternalLink)exiter.next();
               /**Note: jUDDI adds its own discoverURL as the businessEntity**/
               biz.addDiscoveryURL(new DiscoveryURL("businessEntityExt",link.getExternalURI()));
            }
           //External Identifiers
           Collection exid = org.getExternalIdentifiers();
           Iterator exiditer = exid.iterator();
           while(exiditer.hasNext())
           {
              ExternalIdentifier ei = (ExternalIdentifier)exiditer.next();

              KeyedReference keyr = new KeyedReference();
              Key ekey = ei.getKey();
              if(ekey != null ) keyr.setTModelKey(ekey.getId());
              keyr.setKeyValue(ei.getValue());
              keyr.setKeyName(ei.getName().getValue());
              biz.addIdentifier(keyr);
           }
        } catch (Exception ud)
        {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return biz;
    }

    /**
     *  TODO - should we really return new Contact() rather than null on
     *     null input?
     *
     * Convert JAXR User Object to UDDI  Contact
     */
    public static Contact getContactFromJAXRUser(User user)
            throws JAXRException
    {
        Contact ct = new Contact();

        if (user == null) {
            return ct;
        }

        Vector addvect = new Vector();
        Vector phonevect = new Vector();
        Vector emailvect = new Vector();
        try
        {
            ct.setPersonName(new PersonName(user.getPersonName().getFullName()));
            ct.setUseType(user.getType());
            //Postal Address
            Collection postc = user.getPostalAddresses();
            Iterator iterator = postc.iterator();
            while (iterator.hasNext())
            {
                PostalAddress post = (PostalAddress) iterator.next();
                addvect.add(ScoutJaxrUddiHelper.getAddress(post));
            }
            //Phone Numbers
            Collection ph = user.getTelephoneNumbers(null);
            Iterator it = ph.iterator();
            while (it.hasNext())
            {
                TelephoneNumber t = (TelephoneNumber) it.next();
                Phone phone = new Phone();
                String str = t.getNumber();
                log.debug("Telephone=" + str);
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

   private static String getToken(String tokenstr)
   {
      //Token can have the value NULL which need to be converted into null
      if(tokenstr.equals("NULL")) tokenstr="";
      return tokenstr;
   }

   private static String getURLType( String accessuri)
   {
       String acc = accessuri.toLowerCase();
       String uri = "other";
       if( acc.startsWith("http:")) uri = "http";
       else if(acc.startsWith("https:")) uri = "https";
       else if(acc.startsWith("ftp:")) uri = "ftp";
       else if(acc.startsWith("phone:")) uri = "phone";//TODO:Handle this better

       return uri;
   }
   
   private static OverviewDoc getOverviewDocFromExternalLink(ExternalLink link)
   throws JAXRException
   {
	   OverviewDoc od = new OverviewDoc();
	   String url = link.getExternalURI();
	   if(url != null)
		   od.setOverviewURL(new OverviewURL(url));
	   InternationalString extDesc = link.getDescription();
	   if(extDesc != null)
		   od.addDescription(new Description(extDesc.getValue()));
	   return od;
   }
   
   private static Vector getKeyedReferenceVector(Collection externalIdentifiers)
   throws JAXRException
   {
	   Vector krv = new Vector();
	   if(externalIdentifiers != null)
	   {
		   Iterator iter = externalIdentifiers.iterator();
		   while(iter.hasNext())
		   {
			   KeyedReference kr = new KeyedReference();
			   ExternalIdentifier eid = (ExternalIdentifier)iter.next();
			   kr.setTModelKey(eid.getObjectType().getClassificationScheme().getKey().getId());
			   kr.setKeyName(eid.getObjectType().getName().getValue());
			   kr.setKeyValue(eid.getObjectType().getValue());

			   krv.add(kr); 
		   }
	   }
	   return krv;
   }
   
   private static CategoryBag getCategoryBagFromConcept()  
   {
	   /**
	    * JAXR 1.0 Specification: Section D6.4.4
	    * Specification related tModels mapped from Concept may be automatically
	    * categorized by the well-known uddi-org:types taxonomy in UDDI (with
	    * tModelKey uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4) as follows:
	    * The keyed reference is assigned a taxonomy value of specification.
	    */
	   CategoryBag cbag = new CategoryBag();
	   Vector krv = new Vector();
	   KeyedReference kr = new KeyedReference();
	   kr.setTModelKey(UDDI_ORG_TYPES);
	   kr.setKeyValue("specification"); 
	   krv.add(kr);
	   cbag.setKeyedReferenceVector(krv);
	   return cbag; 
   }
   
   private static BindingTemplates getBindingTemplates(Collection serviceBindings)
   throws JAXRException
   {
	   BindingTemplates bt = new BindingTemplates();
	   if(serviceBindings != null)
	   {
		   Iterator iter = serviceBindings.iterator();
		   while(iter.hasNext())
		   {
			   ServiceBinding sb = (ServiceBinding)iter.next();
			   bt.addBindingTemplate(getBindingTemplate(sb));
		   }
	   }
	   return bt; 
   } 
   
   private static BindingTemplate getBindingTemplate(ServiceBinding serviceBinding)
   throws JAXRException
   {
	   BindingTemplate bt = new BindingTemplate();
	   if(serviceBinding != null)
	   {
		   //Access URI
		   String accessURI = serviceBinding.getAccessURI();
		   if(accessURI != null)
			   bt.setAccessPoint(getAccessPoint(accessURI));
		   ServiceBinding sb = serviceBinding.getTargetBinding();
		   if(sb != null)
		   {
			  bt.setHostingRedirector(new HostingRedirector(accessURI));
		   }
		    
		   Key key = serviceBinding.getKey();
		   if(key != null)
		     bt.setBindingKey(key.getId());
		   Key serviceKey = serviceBinding.getService().getKey();
		   if(serviceKey != null)
		     bt.setServiceKey(serviceKey.getId());
		   //Description
		   InternationalString desc = serviceBinding.getDescription();
		   if(desc != null)
		   {
			   Vector dvect = new Vector();
			   dvect.add(new Description(desc.getValue()));
			   bt.setDescriptionVector(dvect);
		   } 
	   }
	   return bt; 
   }
   
   private static AccessPoint getAccessPoint(String accessURI)
   {
	   String type = AccessPoint.OTHER;
	   String temp = accessURI.toLowerCase();
	   if(temp.startsWith("https"))
		   type = AccessPoint.HTTPS;
	   else
		   if(temp.startsWith("http"))
			   type = AccessPoint.HTTP;
		   else
			   if(temp.startsWith("mail"))
			     type = AccessPoint.MAILTO;
			   else
				   if(temp.startsWith("ftp"))
			         type = AccessPoint.FTP;
				   else
					   if(Character.isLetterOrDigit(temp.charAt(0)))
				         type = AccessPoint.PHONE; 
	   
	   return new AccessPoint(type, accessURI);
   }
}
