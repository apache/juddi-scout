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

import uddiOrgApiV2.Address;
import uddiOrgApiV2.AddressLine;
import uddiOrgApiV2.DiscoveryURL;
import uddiOrgApiV2.DiscoveryURLs;
import uddiOrgApiV2.Email;
import uddiOrgApiV2.KeyedReference;
import uddiOrgApiV2.Name;
import uddiOrgApiV2.Phone;
import uddiOrgApiV2.PublisherAssertion;
import uddiOrgApiV2.BusinessEntity;
import uddiOrgApiV2.Contacts;
import uddiOrgApiV2.Contact;
import uddiOrgApiV2.BusinessService;
import uddiOrgApiV2.BusinessServices;
import uddiOrgApiV2.TModel;
import uddiOrgApiV2.AccessPoint;
import uddiOrgApiV2.BindingTemplate;
import uddiOrgApiV2.Description;
import uddiOrgApiV2.HostingRedirector;
import uddiOrgApiV2.IdentifierBag;
import uddiOrgApiV2.URLType;
import uddiOrgApiV2.TModelInstanceDetails;
import uddiOrgApiV2.TModelInstanceInfo;

import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;

import javax.xml.registry.infomodel.*;
import javax.xml.registry.JAXRException;
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
public class ScoutJaxrUddiHelper {
    /**
     * Get UDDI Address given JAXR Postal Address
     */
	public static Address getAddress(PostalAddress post) throws JAXRException {
		Address address = Address.Factory.newInstance();

		AddressLine[] addarr = new AddressLine[6];

        String stnum = post.getStreetNumber();
        String st = post.getStreet();
        String city = post.getCity();
        String country = post.getCountry();
        String code = post.getPostalCode();
        String state = post.getStateOrProvince();

		AddressLine stnumAL = AddressLine.Factory.newInstance();
        stnumAL.setKeyName("STREET_NUMBER");
		if (stnum != null) {
        stnumAL.setKeyValue(stnum);
		}

		AddressLine stAL = AddressLine.Factory.newInstance();
        stAL.setKeyName("STREET");
		if (st != null) {
        stAL.setKeyValue(st);
		}

		AddressLine cityAL = AddressLine.Factory.newInstance();
        cityAL.setKeyName("CITY");
		if (city != null) {
        cityAL.setKeyValue(city);
		}

		AddressLine countryAL = AddressLine.Factory.newInstance();
        countryAL.setKeyName("COUNTRY");
		if (country != null) {
        countryAL.setKeyValue(country);
		}

		AddressLine codeAL = AddressLine.Factory.newInstance();
        codeAL.setKeyName("POSTALCODE");
		if (code != null) {
        codeAL.setKeyValue(code);
		}

		AddressLine stateAL = AddressLine.Factory.newInstance();
        stateAL.setKeyName("STATE");
		if (state != null) {
        stateAL.setKeyValue(state);
		}

		// Add the AddressLine to vector
		addarr[0] = stnumAL;
		addarr[1] = stAL;
		addarr[2] = cityAL;
		addarr[3] = countryAL;
		addarr[4] = codeAL;
		addarr[5] = stateAL;

		address.setAddressLineArray(addarr);

        return address;
    }

	public static BindingTemplate getBindingTemplateFromJAXRSB(
			ServiceBinding serve) throws JAXRException {
		BindingTemplate bt = BindingTemplate.Factory.newInstance();
		if (serve.getKey() != null && serve.getKey().getId() != null)
			bt.setBindingKey(serve.getKey().getId());
		try {
			// Set Access URI
            String accessuri = serve.getAccessURI();
			if (accessuri != null) {
				AccessPoint ap = AccessPoint.Factory.newInstance();
                ap.setURLType(getURLType(accessuri));
				ap.setStringValue(accessuri);
                bt.setAccessPoint(ap);
            }
            ServiceBinding sb = serve.getTargetBinding();
			if (sb != null) {
				HostingRedirector red = HostingRedirector.Factory.newInstance();
                Key key = sb.getKey();
				if (key != null && key.getId() != null)
					red.setBindingKey(key.getId());
                bt.setHostingRedirector(red);
            }
			// TODO:Need to look further at the mapping b/w BindingTemplate and
			// Jaxr ServiceBinding

			// Get Service information
           Service svc = serve.getService();
			if (svc != null && svc.getKey() != null && svc.getKey().getId() != null) {
              bt.setServiceKey(svc.getKey().getId());
           }

			Description emptyDesc = bt.addNewDescription();

			if (((RegistryObject) serve).getDescription() != null && 
				((RegistryObject) serve).getDescription().getValue() != null) {

				emptyDesc.setStringValue(((RegistryObject) serve)
						.getDescription().getValue());

			}

			// SpecificationLink
           Collection slcol = serve.getSpecificationLinks();
			TModelInstanceDetails tid = TModelInstanceDetails.Factory
					.newInstance();
			if (slcol != null && slcol.isEmpty() != false) {
              Iterator iter = slcol.iterator();
				while (iter.hasNext()) {
					SpecificationLink slink = (SpecificationLink) iter.next();

					TModelInstanceInfo emptyTInfo = tid
							.addNewTModelInstanceInfo();

					if (slink.getSpecificationObject().getKey() != null && 
						slink.getSpecificationObject().getKey().getId() != null) {
						emptyTInfo.setTModelKey(slink.getSpecificationObject()
								.getKey().getId());
					}
              }
              bt.setTModelInstanceDetails(tid);
           }
            System.out.println("BindingTemplate=" + bt.toString());
		} catch (Exception ud) {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return bt;
    }

	public static PublisherAssertion getPubAssertionFromJAXRAssociation(
			Association assc) throws JAXRException {
		PublisherAssertion pa = PublisherAssertion.Factory.newInstance();
		try {
			if (assc.getSourceObject().getKey() != null && 
				assc.getSourceObject().getKey().getId() != null) {
            pa.setFromKey(assc.getSourceObject().getKey().getId());
			}
			
			if (assc.getTargetObject().getKey() != null &&
				assc.getTargetObject().getKey().getId() != null) {
            pa.setToKey(assc.getTargetObject().getKey().getId());
			}
            Concept c = assc.getAssociationType();
            String v = c.getValue();
			KeyedReference kr = KeyedReference.Factory.newInstance();
            Key key = c.getKey();
			if (key == null) {
				// TODO:Need to check this. If the concept is a predefined
				// enumeration, the key can be the parent classification scheme
                key = c.getClassificationScheme().getKey();
            }
			if (key == null || key.getId() == null) {
				// The parent classification scheme may not always contain the
				// key. It is okay if it doesn't, since the UDDI v2 spec allows
				// TModelKey to be absent.

				// TODO: This setting to "" should not be needed at all.
				// However, a bug in jUDDI needs it to be there. See:
				// http://issues.apache.org/jira/browse/JUDDI-78
				kr.setTModelKey("");
			} else {
            kr.setTModelKey(key.getId());
			}
            kr.setKeyName("Concept");

			if (v != null) {
            kr.setKeyValue(v);
			}

            pa.setKeyedReference(kr);
		} catch (Exception ud) {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return pa;
    }

	public static PublisherAssertion getPubAssertionFromJAXRAssociationKey(
			String key) throws JAXRException {
		PublisherAssertion pa = PublisherAssertion.Factory.newInstance();
		try {
			StringTokenizer token = new StringTokenizer(key, ":");
			if (token.hasMoreTokens()) {
               pa.setFromKey(getToken(token.nextToken()));
               pa.setToKey(getToken(token.nextToken()));
				KeyedReference kr = KeyedReference.Factory.newInstance();
				// Sometimes the Key is UUID:something
               String str = getToken(token.nextToken());
				if ("UUID".equals(str))
					str += ":" + getToken(token.nextToken());
               kr.setTModelKey(str);
               kr.setKeyName(getToken(token.nextToken()));
               kr.setKeyValue(getToken(token.nextToken()));
               pa.setKeyedReference(kr);
            }

		} catch (Exception ud) {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return pa;
    }

	public static BusinessService getBusinessServiceFromJAXRService(
			Service serve) throws JAXRException {
		BusinessService bs = BusinessService.Factory.newInstance();
		try {
			InternationalStringImpl iname = (InternationalStringImpl) ((RegistryObject) serve)
					.getName();
            String name = iname.getValue();
			// bs.setDefaultNameString( name,
			// Locale.getDefault().getLanguage());
			Name emptyName = bs.addNewName();
			
			if (name != null) emptyName.setStringValue(name);
			emptyName.setLang(Locale.getDefault().getLanguage());

			Description emptyDesc = bs.addNewDescription();
			
			if (((RegistryObject) serve).getDescription() != null && 
				((RegistryObject) serve).getDescription().getValue() != null) {

				emptyDesc.setStringValue(((RegistryObject) serve).getDescription()
						.getValue());
			}

            Organization o = serve.getProvidingOrganization();

            /*
             * there may not always be a key...
             */
            if (o != null) {
                Key k = o.getKey();

				if (k != null && k.getId() != null) {
                    bs.setBusinessKey(k.getId());
                }
			} else {
                /*
                 * gmj - I *think* this is the right thing to do
                 */
				throw new JAXRException(
						"Service has no associated organization");
            }

			if (serve.getKey() != null && serve.getKey().getId() != null) {
                bs.setServiceKey(serve.getKey().getId());
            }

            System.out.println("BusinessService=" + bs.toString());
		} catch (Exception ud) {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return bs;
    }

	public static TModel getTModelFromJAXRClassificationScheme(
			ClassificationScheme scheme) throws JAXRException {
		TModel tm = TModel.Factory.newInstance();
		try {
            /*
             * a fresh scheme might not have a key
             */

            Key k = scheme.getKey();

			if (k != null && k.getId() != null) {
                tm.setTModelKey(k.getId());

            }

            /*
             * There's no reason to believe these are here either
             */

            Slot s = scheme.getSlot("authorizedName");

			if (s != null && s.getName() != null) {
                tm.setAuthorizedName(s.getName());
            }

            s = scheme.getSlot("operator");

			if (s != null && s.getName() != null) {
                tm.setOperator(s.getName());
            }

			InternationalStringImpl iname = (InternationalStringImpl) ((RegistryObject) scheme)
					.getName();
            String name = iname.getValue();

			Name emptyName = tm.addNewName();
			
			if (name != null) {
				emptyName.setStringValue(name);
			}
			emptyName.setLang(Locale.getDefault().getLanguage());

			Description emptyDesc = tm.addNewDescription();
			
			if (scheme.getDescription() != null && scheme.getDescription().getValue() != null) {
				emptyDesc.setStringValue(scheme.getDescription().getValue());
			}
			// ToDO: overviewDoc,identifierBag,categoryBag
		} catch (Exception ud) {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return tm;
    }

    public static TModel getTModelFromJAXRConcept(Concept scheme)
			throws JAXRException {
		TModel tm = TModel.Factory.newInstance();
		if (scheme == null)
			return null;
		try {
            Key key = scheme.getKey();
			if (key != null && key.getId() != null)
				tm.setTModelKey(key.getId());
            Slot sl1 = scheme.getSlot("authorizedName");
			if (sl1 != null && sl1.getName() != null)
				tm.setAuthorizedName(sl1.getName());

            Slot sl2 = scheme.getSlot("operator");
			if (sl2 != null && sl2.getName() != null)
				tm.setOperator(sl2.getName());

			InternationalStringImpl iname = (InternationalStringImpl) ((RegistryObject) scheme)
					.getName();
            String name = iname.getValue();

			Name emptyName = tm.addNewName();
			
			if (name != null) {
				emptyName.setStringValue(name);
			}
			emptyName.setLang(Locale.getDefault().getLanguage());

			Description emptyDesc = tm.addNewDescription();
			
			if (scheme.getDescription() != null && scheme.getDescription().getValue() != null) {
				emptyDesc.setStringValue(scheme.getDescription().getValue());
			}

			// ToDO: overviewDoc,identifierBag,categoryBag
		} catch (Exception ud) {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return tm;
    }

    public static BusinessEntity getBusinessEntityFromJAXROrg(Organization org)
			throws JAXRException {
		BusinessEntity biz = BusinessEntity.Factory.newInstance();
		BusinessServices bss = BusinessServices.Factory.newInstance();
		BusinessService[] barr = new BusinessService[0];

		try {
			// It may just be an update
            Key key = org.getKey();
			if (key != null && key.getId() != null)
				biz.setBusinessKey(key.getId());
			// Lets get the Organization attributes at the top level
            String language = Locale.getDefault().getLanguage();

			Name emptyName = biz.addNewName();
			
			if (org.getName() != null && org.getName().getValue() != null) {
				emptyName.setStringValue(org.getName().getValue());
			}
			emptyName.setLang(language);

			Description emptyDesc = biz.addNewDescription();
			
			if (org.getDescription() != null && org.getDescription().getValue() != null) {
				emptyDesc.setStringValue(org.getDescription().getValue());
			}

			if (org.getPrimaryContact() != null && 
				org.getPrimaryContact().getPersonName()!= null &&
				org.getPrimaryContact().getPersonName().getFullName() != null) {

				biz.setAuthorizedName(org.getPrimaryContact().getPersonName()
						.getFullName());
			}

            Collection s = org.getServices();
            System.out.println("?Org has services=" + s.isEmpty());

			barr = new BusinessService[s.size()];

            Iterator iter = s.iterator();
			int barrPos = 0;
			while (iter.hasNext()) {
				BusinessService bs = ScoutJaxrUddiHelper
						.getBusinessServiceFromJAXRService((Service) iter
								.next());
				barr[barrPos] = bs;
				barrPos++;
            }

            /*
             * map users : JAXR has concept of 'primary contact', which is a
             * special designation for one of the users, and D6.1 seems to say
             * that the first UDDI user is the primary contact
             */

			Contacts cts = Contacts.Factory.newInstance();
			Contact[] carr = new Contact[0];

            User primaryContact = org.getPrimaryContact();
            Collection users = org.getUsers();

            // Expand array to necessary size only (xmlbeans does not like
            // null items in cases like this)

            int carrSize = 0;

            if (primaryContact != null) {
                carrSize += 1;
            }

            // TODO: Clean this up and make it more efficient
            Iterator it = users.iterator();
            while (it.hasNext()) {
                User u = (User) it.next();
                if (u != primaryContact) {
                    carrSize++;
                }
            }

            carr = new Contact[carrSize];

            // TODO - remove this
            System.out.println("?Org has users=" + users.isEmpty());

            /*
             * first do primary, and then filter that out in the loop
             */
            if (primaryContact != null) {
                Contact ct = getContactFromJAXRUser(primaryContact);
                carr[0] = ct;
            }

            it = users.iterator();
            int carrPos = 1;
            while (it.hasNext()) {
                User u = (User) it.next();

                if (u != primaryContact) {
                    Contact ct = getContactFromJAXRUser(u);
                    carr[carrPos] = ct;
                    carrPos++;
                }
            }

			bss.setBusinessServiceArray(barr);
			cts.setContactArray(carr);
            biz.setContacts(cts);

            biz.setBusinessServices(bss);

			// External Links
            Iterator exiter = org.getExternalLinks().iterator();
			while (exiter.hasNext()) {
				ExternalLink link = (ExternalLink) exiter.next();
				/** Note: jUDDI adds its own discoverURL as the businessEntity* */
				DiscoveryURLs emptyDUs = biz.addNewDiscoveryURLs();
				DiscoveryURL emptyDU = emptyDUs.addNewDiscoveryURL();
				emptyDU.setUseType("businessEntityExt");
				
				if (link.getExternalURI() != null) {
					emptyDU.setStringValue(link.getExternalURI());
				}
            }
			// External Identifiers
           Collection exid = org.getExternalIdentifiers();
           Iterator exiditer = exid.iterator();
			while (exiditer.hasNext()) {
				ExternalIdentifier ei = (ExternalIdentifier) exiditer.next();

				IdentifierBag ibag = biz.addNewIdentifierBag();
				KeyedReference keyr = ibag.addNewKeyedReference();
              Key ekey = ei.getKey();
				if (ekey != null && ekey.getId() != null)
					keyr.setTModelKey(ekey.getId());
				
				if (ei.getValue() != null) {
              keyr.setKeyValue(ei.getValue());
				}
				
				if (ei.getName() != null && ei.getName().getValue() != null) {
              keyr.setKeyName(ei.getName().getValue());
           }
			}
		} catch (Exception ud) {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return biz;
    }

    /**
	 * TODO - should we really return new Contact() rather than null on null
	 * input?
     *
     * Convert JAXR User Object to UDDI  Contact
     */
    public static Contact getContactFromJAXRUser(User user)
			throws JAXRException {
		Contact ct = Contact.Factory.newInstance();

        if (user == null) {
            return ct;
        }

		Address[] addarr = new Address[0];
		Phone[] phonearr = new Phone[0];
		Email[] emailarr = new Email[0];
		try {
			
			if (user.getPersonName() != null && user.getPersonName().getFullName() != null) {
				ct.setPersonName(user.getPersonName().getFullName());
			}
			
			if (user.getType() != null) {
            ct.setUseType(user.getType());
			}
			// Postal Address
            Collection postc = user.getPostalAddresses();

			addarr = new Address[postc.size()];

            Iterator iterator = postc.iterator();
			int addarrPos = 0;
			while (iterator.hasNext()) {
                PostalAddress post = (PostalAddress) iterator.next();
				addarr[addarrPos] = ScoutJaxrUddiHelper.getAddress(post);
				addarrPos++;
            }
			// Phone Numbers
            Collection ph = user.getTelephoneNumbers(null);

			phonearr = new Phone[ph.size()];

            Iterator it = ph.iterator();
			int phonearrPos = 0;
			while (it.hasNext()) {
                TelephoneNumber t = (TelephoneNumber) it.next();
				Phone phone = Phone.Factory.newInstance();
                String str = t.getNumber();
                System.out.println("Telephone=" + str);
				
				// FIXME: If phone number is null, should the phone 
				// not be set at all, or set to empty string?
				if (str != null) {
					phone.setStringValue(str);
				} else {
					phone.setStringValue("");
				}

				phonearr[phonearrPos] = phone;
				phonearrPos++;
            }

			// Email Addresses
            Collection ec = user.getEmailAddresses();

			emailarr = new Email[ec.size()];

            Iterator iter = ec.iterator();
			int emailarrPos = 0;
			while (iter.hasNext()) {
                EmailAddress ea = (EmailAddress) iter.next();
				Email email = Email.Factory.newInstance();
				
				if (ea.getAddress() != null) {
					email.setStringValue(ea.getAddress());
				}
				// email.setText( ea.getAddress() );
				
				if (ea.getType() != null) {
                email.setUseType(ea.getType());
            }

				emailarr[emailarrPos] = email;
				emailarrPos++;
			}
			ct.setAddressArray(addarr);
			ct.setPhoneArray(phonearr);
			ct.setEmailArray(emailarr);
		} catch (Exception ud) {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return ct;
    }

	private static String getToken(String tokenstr) {
		// Token can have the value NULL which need to be converted into null
		if (tokenstr.equals("NULL"))
			tokenstr = "";
      return tokenstr;
   }

	private static URLType.Enum getURLType(String accessuri) {
       String acc = accessuri.toLowerCase();
		URLType.Enum uri = URLType.OTHER;
		if (acc.startsWith("http:"))
			uri = URLType.HTTP;
		else if (acc.startsWith("https:"))
			uri = URLType.HTTPS;
		else if (acc.startsWith("ftp:"))
			uri = URLType.FTP;
		else if (acc.startsWith("phone:"))
			uri = URLType.PHONE;// TODO:Handle this better

       return uri;
   }

}
