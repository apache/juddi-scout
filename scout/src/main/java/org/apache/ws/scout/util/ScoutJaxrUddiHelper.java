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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.EmailAddress;
import javax.xml.registry.infomodel.ExternalIdentifier;
import javax.xml.registry.infomodel.ExternalLink;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.LocalizedString;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.PostalAddress;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.Slot;
import javax.xml.registry.infomodel.SpecificationLink;
import javax.xml.registry.infomodel.TelephoneNumber;
import javax.xml.registry.infomodel.User;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.scout.model.uddi.v2.AccessPoint;
import org.apache.ws.scout.model.uddi.v2.Address;
import org.apache.ws.scout.model.uddi.v2.AddressLine;
import org.apache.ws.scout.model.uddi.v2.BindingTemplate;
import org.apache.ws.scout.model.uddi.v2.BindingTemplates;
import org.apache.ws.scout.model.uddi.v2.BusinessEntity;
import org.apache.ws.scout.model.uddi.v2.BusinessService;
import org.apache.ws.scout.model.uddi.v2.BusinessServices;
import org.apache.ws.scout.model.uddi.v2.CategoryBag;
import org.apache.ws.scout.model.uddi.v2.Contact;
import org.apache.ws.scout.model.uddi.v2.Contacts;
import org.apache.ws.scout.model.uddi.v2.Description;
import org.apache.ws.scout.model.uddi.v2.DiscoveryURL;
import org.apache.ws.scout.model.uddi.v2.DiscoveryURLs;
import org.apache.ws.scout.model.uddi.v2.Email;
import org.apache.ws.scout.model.uddi.v2.HostingRedirector;
import org.apache.ws.scout.model.uddi.v2.IdentifierBag;
import org.apache.ws.scout.model.uddi.v2.InstanceDetails;
import org.apache.ws.scout.model.uddi.v2.KeyedReference;
import org.apache.ws.scout.model.uddi.v2.Name;
import org.apache.ws.scout.model.uddi.v2.ObjectFactory;
import org.apache.ws.scout.model.uddi.v2.OverviewDoc;
import org.apache.ws.scout.model.uddi.v2.Phone;
import org.apache.ws.scout.model.uddi.v2.PublisherAssertion;
import org.apache.ws.scout.model.uddi.v2.TModel;
import org.apache.ws.scout.model.uddi.v2.TModelBag;
import org.apache.ws.scout.model.uddi.v2.TModelInstanceDetails;
import org.apache.ws.scout.model.uddi.v2.TModelInstanceInfo;
import org.apache.ws.scout.model.uddi.v2.URLType;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;

/**
 * Helper class that does Jaxr->UDDI Mapping
 *
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class ScoutJaxrUddiHelper 
{
    private static final String UDDI_ORG_TYPES = "uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4";
	private static Log log = LogFactory.getLog(ScoutJaxrUddiHelper.class);
	private static ObjectFactory objectFactory = new ObjectFactory();
	
    /**
     * Get UDDI Address given JAXR Postal Address
     */
	public static Address getAddress(PostalAddress postalAddress) throws JAXRException {
		Address address = objectFactory.createAddress();

		AddressLine[] addarr = new AddressLine[6];

        String stnum = postalAddress.getStreetNumber();
        String st = postalAddress.getStreet();
        String city = postalAddress.getCity();
        String country = postalAddress.getCountry();
        String code = postalAddress.getPostalCode();
        String state = postalAddress.getStateOrProvince();

		AddressLine stnumAL = objectFactory.createAddressLine();
        stnumAL.setKeyName("STREET_NUMBER");
		if (stnum != null) {
        stnumAL.setKeyValue(stnum);
		}

		AddressLine stAL = objectFactory.createAddressLine();
        stAL.setKeyName("STREET");
		if (st != null) {
        stAL.setKeyValue(st);
		}

		AddressLine cityAL = objectFactory.createAddressLine();
        cityAL.setKeyName("CITY");
		if (city != null) {
        cityAL.setKeyValue(city);
		}

		AddressLine countryAL = objectFactory.createAddressLine();
        countryAL.setKeyName("COUNTRY");
		if (country != null) {
        countryAL.setKeyValue(country);
		}

		AddressLine codeAL = objectFactory.createAddressLine();
        codeAL.setKeyName("POSTALCODE");
		if (code != null) {
        codeAL.setKeyValue(code);
		}

		AddressLine stateAL = objectFactory.createAddressLine();
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

		address.getAddressLine().addAll(Arrays.asList(addarr));

        return address;
    }

	public static BindingTemplate getBindingTemplateFromJAXRSB(
			ServiceBinding serviceBinding) throws JAXRException {
		BindingTemplate bt = objectFactory.createBindingTemplate();
		if (serviceBinding.getKey() != null && serviceBinding.getKey().getId() != null) {
			bt.setBindingKey(serviceBinding.getKey().getId());
		} else {
			bt.setBindingKey("");
		}
	
		try {
			// Set Access URI
            String accessuri = serviceBinding.getAccessURI();
			if (accessuri != null) {
				AccessPoint accessPoint = objectFactory.createAccessPoint();
                accessPoint.setURLType(getURLType(accessuri));
				accessPoint.setValue(accessuri);
                bt.setAccessPoint(accessPoint);
            }
            ServiceBinding sb = serviceBinding.getTargetBinding();
			if (sb != null) {
				HostingRedirector red = objectFactory.createHostingRedirector();
                Key key = sb.getKey();
				if (key != null && key.getId() != null) {
					red.setBindingKey(key.getId());
                } else {
                    red.setBindingKey("");
                }
                bt.setHostingRedirector(red);
            } else {
            	if (bt.getAccessPoint() == null) {
            		bt.setAccessPoint(objectFactory.createAccessPoint());
            	}
            }
			// TODO:Need to look further at the mapping b/w BindingTemplate and
			// Jaxr ServiceBinding

			// Get Service information
           Service svc = serviceBinding.getService();
			if (svc != null && svc.getKey() != null && svc.getKey().getId() != null) {
              bt.setServiceKey(svc.getKey().getId());
           }
			
			InternationalString idesc = ((RegistryObject) serviceBinding).getDescription();
            
            if (idesc != null) {
                for (LocalizedString locName : idesc.getLocalizedStrings()) {
                    Description desc = objectFactory.createDescription();
                    bt.getDescription().add(desc);
                    desc.setValue(locName.getValue());
                    desc.setLang(locName.getLocale().getLanguage());                
                }
            }

			// SpecificationLink
           Collection<SpecificationLink> slcol = serviceBinding.getSpecificationLinks();
			TModelInstanceDetails tid = objectFactory.createTModelInstanceDetails();
			if (slcol != null && !slcol.isEmpty()) {
              Iterator<SpecificationLink> iter = slcol.iterator();
				while (iter.hasNext()) {
					SpecificationLink slink = (SpecificationLink) iter.next();

					TModelInstanceInfo emptyTInfo = objectFactory.createTModelInstanceInfo();
					tid.getTModelInstanceInfo().add(emptyTInfo);

                    RegistryObject specificationObject = slink.getSpecificationObject();
					if (specificationObject.getKey() != null && specificationObject.getKey().getId() != null) {
						emptyTInfo.setTModelKey(specificationObject.getKey().getId());
                        if (specificationObject.getDescription()!=null) {
                            for (LocalizedString locDesc : specificationObject.getDescription().getLocalizedStrings()) {
                                Description description = objectFactory.createDescription();
                                emptyTInfo.getDescription().add(description);
                                description.setValue(locDesc.getValue());
                                description.setLang(locDesc.getLocale().getLanguage());
                            }
                        }
                        Collection<ExternalLink> externalLinks = slink.getExternalLinks();
                        if (externalLinks!=null && externalLinks.size()>0) {
                            for (ExternalLink link : externalLinks) {
                                InstanceDetails ids = objectFactory.createInstanceDetails();
                                emptyTInfo.setInstanceDetails(ids);
                                if (link.getDescription()!=null) {
                                    Description description = objectFactory.createDescription();
                                    ids.getDescription().add(description);
                                    description.setValue(link.getDescription().getValue());
                                }
                                if (link.getExternalURI()!=null) {
                                    OverviewDoc overviewDoc = objectFactory.createOverviewDoc();
                                    ids.setOverviewDoc(overviewDoc);
                                    overviewDoc.setOverviewURL(link.getExternalURI());
                                }
                            } 
                        }
					}
              }
            }
			bt.setTModelInstanceDetails(tid);
			log.debug("BindingTemplate=" + bt.toString());
		} catch (Exception ud) {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return bt;
    }

	public static PublisherAssertion getPubAssertionFromJAXRAssociation(
			Association association) throws JAXRException {
		PublisherAssertion pa = objectFactory.createPublisherAssertion();
		try {
			if (association.getSourceObject().getKey() != null && 
				association.getSourceObject().getKey().getId() != null) {
            pa.setFromKey(association.getSourceObject().getKey().getId());
			}
			
			if (association.getTargetObject().getKey() != null &&
				association.getTargetObject().getKey().getId() != null) {
            pa.setToKey(association.getTargetObject().getKey().getId());
			}
            Concept c = association.getAssociationType();
            String v = c.getValue();
			KeyedReference kr = objectFactory.createKeyedReference();
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
				//kr.setTModelKey("");
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
		PublisherAssertion pa = objectFactory.createPublisherAssertion();
		try {
			StringTokenizer token = new StringTokenizer(key, ":");
			if (token.hasMoreTokens()) {
               pa.setFromKey(getToken(token.nextToken()));
               pa.setToKey(getToken(token.nextToken()));
				KeyedReference kr = objectFactory.createKeyedReference();
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
			Service service) throws JAXRException {
		BusinessService bs = objectFactory.createBusinessService();
		try {
			InternationalString iname = ((RegistryObject) service).getName();
						
			for (LocalizedString locName : iname.getLocalizedStrings()) {
			    Name name = objectFactory.createName();
			    bs.getName().add(name);
			    name.setValue(locName.getValue());
			    name.setLang(locName.getLocale().getLanguage());                
			}
	         
            InternationalString idesc = ((RegistryObject) service).getDescription();
    
            if (idesc != null) {
                for (LocalizedString locName : idesc.getLocalizedStrings()) {
                    Description desc = objectFactory.createDescription();
                    bs.getDescription().add(desc);
                    desc.setValue(locName.getValue());
                    desc.setLang(locName.getLocale().getLanguage());                
                }
            }

            Organization o = service.getProvidingOrganization();

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

			if (service.getKey() != null && service.getKey().getId() != null) {
                bs.setServiceKey(service.getKey().getId());
            } else {
                bs.setServiceKey("");
            }

            CategoryBag catBag = getCategoryBagFromClassifications(service.getClassifications());
            if (catBag!=null) {
                bs.setCategoryBag(catBag);
            }

            //Add the ServiceBinding information
            BindingTemplates bt = getBindingTemplates(service.getServiceBindings());
            if (bt != null) {
                bs.setBindingTemplates(bt);
            }
   		    
            log.debug("BusinessService=" + bs.toString());
		} catch (Exception ud) {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return bs;
    }

	public static TModel getTModelFromJAXRClassificationScheme(
			ClassificationScheme classificationScheme) throws JAXRException {
		TModel tm = objectFactory.createTModel();
		try {
            /*
             * a fresh scheme might not have a key
             */

            Key k = classificationScheme.getKey();

            if (k != null && k.getId() != null) {
                tm.setTModelKey(k.getId());
            } else {
                tm.setTModelKey("");
            }

            /*
             * There's no reason to believe these are here either
             */

            Slot s = classificationScheme.getSlot("authorizedName");

			if (s != null && s.getName() != null) {
                tm.setAuthorizedName(s.getName());
            }

            s = classificationScheme.getSlot("operator");

			if (s != null && s.getName() != null) {
                tm.setOperator(s.getName());
            }

			InternationalString iname = ((RegistryObject) classificationScheme).getName();
			 
			for (LocalizedString locName : iname.getLocalizedStrings()) {
			    Name name = objectFactory.createName();
			    tm.setName(name);
			    name.setValue(locName.getValue());
			    name.setLang(locName.getLocale().getLanguage());                
			}
	         
			InternationalString idesc = ((RegistryObject) classificationScheme).getDescription();
			
			if (idesc != null) {
			    for (LocalizedString locName : idesc.getLocalizedStrings()) {
			        Description desc = objectFactory.createDescription();
			        tm.getDescription().add(desc);
			        desc.setValue(locName.getValue());
	                desc.setLang(locName.getLocale().getLanguage());                
	            }
			}

            IdentifierBag idBag = getIdentifierBagFromExternalIdentifiers(classificationScheme.getExternalIdentifiers());
            if (idBag!=null) {
                tm.setIdentifierBag(idBag);
            }
            CategoryBag catBag = getCategoryBagFromClassifications(classificationScheme.getClassifications());
            if (catBag!=null) {
                tm.setCategoryBag(catBag);
            }
			
			// ToDO: overviewDoc
		} catch (Exception ud) {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return tm;
    }

    public static TModel getTModelFromJAXRConcept(Concept concept)
			throws JAXRException {
    	TModel tm = objectFactory.createTModel();
		if (concept == null)
			return null;
		try {
            Key key = concept.getKey();
			if (key != null && key.getId() != null)
				tm.setTModelKey(key.getId());
            Slot sl1 = concept.getSlot("authorizedName");
			if (sl1 != null && sl1.getName() != null)
				tm.setAuthorizedName(sl1.getName());

            Slot sl2 = concept.getSlot("operator");
			if (sl2 != null && sl2.getName() != null)
				tm.setOperator(sl2.getName());

			InternationalString iname = ((RegistryObject) concept).getName();
			
			for (LocalizedString locName : iname.getLocalizedStrings()) {
			    Name name = objectFactory.createName();
			    tm.setName(name);
			    name.setValue(locName.getValue());
			    name.setLang(locName.getLocale().getLanguage());			    
			}
			
			InternationalString idesc = ((RegistryObject) concept).getDescription();
			
            if (idesc != null) {
                for (LocalizedString locName : idesc.getLocalizedStrings()) {
                    Description desc = objectFactory.createDescription();
                    tm.getDescription().add(desc);
                    desc.setValue(locName.getValue());
                    desc.setLang(locName.getLocale().getLanguage());
                }
            }
//          External Links 
            Collection<ExternalLink> externalLinks = concept.getExternalLinks(); 
            if(externalLinks != null && externalLinks.size() > 0)
            {
                tm.setOverviewDoc(getOverviewDocFromExternalLink((ExternalLink)externalLinks.iterator().next()));
            }  

            IdentifierBag idBag = getIdentifierBagFromExternalIdentifiers(concept.getExternalIdentifiers());
            if (idBag!=null) {
                tm.setIdentifierBag(idBag);
            }
            CategoryBag catBag = getCategoryBagFromClassifications(concept.getClassifications());
            if (catBag!=null) {
                tm.setCategoryBag(catBag);
            }

		} catch (Exception ud) {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return tm;
    }
    
    public static BusinessEntity getBusinessEntityFromJAXROrg(Organization organization)
			throws JAXRException {
		BusinessEntity biz = objectFactory.createBusinessEntity();
		BusinessServices bss = objectFactory.createBusinessServices();
		BusinessService[] barr = new BusinessService[0];

		try {
			// It may just be an update
            Key key = organization.getKey();
			if (key != null && key.getId() != null) {
				biz.setBusinessKey(key.getId());
            } else {
                biz.setBusinessKey("");
            }
			// Lets get the Organization attributes at the top level
			
			InternationalString iname = organization.getName();
			
			if (iname != null) {    
			    for (LocalizedString locName : iname.getLocalizedStrings()) {
			        Name name = objectFactory.createName();
			        biz.getName().add(name);
			        name.setValue(locName.getValue());
			        name.setLang(locName.getLocale().getLanguage());                
			    }
			}
			
			InternationalString idesc = organization.getDescription();
			
			if (idesc != null) {
			    for (LocalizedString locName : idesc.getLocalizedStrings()) {
			        Description desc = objectFactory.createDescription();
			        biz.getDescription().add(desc);
			        desc.setValue(locName.getValue());
			        desc.setLang(locName.getLocale().getLanguage());                
			    }
			}
			
			if (organization.getPrimaryContact() != null && 
				organization.getPrimaryContact().getPersonName()!= null &&
				organization.getPrimaryContact().getPersonName().getFullName() != null) {

				biz.setAuthorizedName(organization.getPrimaryContact().getPersonName()
						.getFullName());
			}

            Collection<Service> s = organization.getServices();
            log.debug("?Org has services=" + s.isEmpty());

			barr = new BusinessService[s.size()];

            Iterator<Service> iter = s.iterator();
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

			Contacts cts = objectFactory.createContacts();
			Contact[] carr = new Contact[0];

            User primaryContact = organization.getPrimaryContact();
            Collection<User> users = organization.getUsers();

            // Expand array to necessary size only (xmlbeans does not like
            // null items in cases like this)

            int carrSize = 0;

            if (primaryContact != null) {
                carrSize += 1;
            }

            // TODO: Clean this up and make it more efficient
            Iterator<User> it = users.iterator();
            while (it.hasNext()) {
                User u = (User) it.next();
                if (u != primaryContact) {
                    carrSize++;
                }
            }

            carr = new Contact[carrSize];

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

			bss.getBusinessService().addAll(Arrays.asList(barr));
            if (carr.length>0) {
                cts.getContact().addAll(Arrays.asList(carr));
                biz.setContacts(cts);
            }
            biz.setBusinessServices(bss);

            // External Links
            Iterator<ExternalLink> exiter = organization.getExternalLinks().iterator();
            DiscoveryURLs emptyDUs = null;
            boolean first = true;
            while (exiter.hasNext()) {
                ExternalLink link = (ExternalLink) exiter.next();
                /** Note: jUDDI adds its own discoverURL as the businessEntity* */
                if (first) {
                    emptyDUs = objectFactory.createDiscoveryURLs();
                    biz.setDiscoveryURLs(emptyDUs);
                    first = false;
                }
                DiscoveryURL emptyDU = objectFactory.createDiscoveryURL();
                emptyDUs.getDiscoveryURL().add(emptyDU);
                emptyDU.setUseType("businessEntityExt");
				
                if (link.getExternalURI() != null) {
                    emptyDU.setValue(link.getExternalURI());
                }
            }
			
          IdentifierBag idBag = getIdentifierBagFromExternalIdentifiers(organization.getExternalIdentifiers());
          if (idBag!=null) {
              biz.setIdentifierBag(idBag);
          }
          CategoryBag catBag = getCategoryBagFromClassifications(organization.getClassifications());
          if (catBag!=null) {
              biz.setCategoryBag(catBag);
          }
			
		} catch (Exception ud) {
            throw new JAXRException("Apache JAXR Impl:", ud);
        }
        return biz;
    }

    /**
     *
     * Convert JAXR User Object to UDDI  Contact
     */
    public static Contact getContactFromJAXRUser(User user)
			throws JAXRException {
		Contact ct = objectFactory.createContact();
        if (user == null) {
            return null;
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
            Collection<PostalAddress> postc = user.getPostalAddresses();

			addarr = new Address[postc.size()];

            Iterator<PostalAddress> iterator = postc.iterator();
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
				Phone phone = objectFactory.createPhone();
                String str = t.getNumber();
                log.debug("Telephone=" + str);
				
				// FIXME: If phone number is null, should the phone 
				// not be set at all, or set to empty string?
				if (str != null) {
					phone.setValue(str);
				} else {
					phone.setValue("");
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
				Email email = objectFactory.createEmail();
				
				if (ea.getAddress() != null) {
					email.setValue(ea.getAddress());
				}
				// email.setText( ea.getAddress() );
				
				if (ea.getType() != null) {
                email.setUseType(ea.getType());
            }

				emailarr[emailarrPos] = email;
				emailarrPos++;
			}
			ct.getAddress().addAll(Arrays.asList(addarr));
			ct.getPhone().addAll(Arrays.asList(phonearr));
			ct.getEmail().addAll(Arrays.asList(emailarr));
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

	private static URLType getURLType(String accessuri) {
       String acc = accessuri.toLowerCase();
		URLType uri = URLType.OTHER;
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
    
	/**
     * According to JAXR Javadoc, there are two types of classification, internal and external and they use the Classification, Concept,     
     * and ClassificationScheme objects.  It seems the only difference between internal and external (as related to UDDI) is that the
     * name/value pair of the categorization is held in the Concept for internal classifications and the Classification for external (bypassing
     * the Concept entirely).
     * 
     * The translation to UDDI is simple.  Relevant objects have a category bag which contains a bunch of KeyedReferences (name/value pairs).  
     * These KeyedReferences optionally refer to a tModel that identifies the type of category (translates to the ClassificationScheme key).  If
     * this is set and the tModel doesn't exist in the UDDI registry, then an invalid key error will occur when trying to save the object.
     * 
     * @param regObj
     * @param destinationObj
     * @throws JAXRException
     */
	public static CategoryBag getCategoryBagFromClassifications(Collection classifications) throws JAXRException {
    	try {
			if (classifications == null || classifications.size()==0)
				return null;
    		
    		// Classifications
			CategoryBag cbag = objectFactory.createCategoryBag();
			Iterator classiter = classifications.iterator();
			while (classiter.hasNext()) {
				Classification classification = (Classification) classiter.next();
				if (classification != null ) {
					KeyedReference keyr = objectFactory.createKeyedReference();
					cbag.getKeyedReference().add(keyr);
	
					InternationalStringImpl iname = null;
					String value = null;
					ClassificationScheme scheme = classification.getClassificationScheme();
                    if (scheme==null || (classification.isExternal() && classification.getConcept()==null)) {
                        /*
                        * JAXR 1.0 Specification: Section D6.4.4
                        * Specification related tModels mapped from Concept may be automatically
                        * categorized by the well-known uddi-org:types taxonomy in UDDI (with
                        * tModelKey uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4) as follows:
                        * The keyed reference is assigned a taxonomy value of specification.
                        */
                        keyr.setTModelKey(UDDI_ORG_TYPES);
                        keyr.setKeyValue("specification"); 
                    } else {
    					if (classification.isExternal()) {
                            iname = (InternationalStringImpl) ((RegistryObject) classification).getName();
                            value = classification.getValue();
    					} else {
    						Concept concept = classification.getConcept();
    						if (concept != null) {
    							iname = (InternationalStringImpl) ((RegistryObject) concept).getName();
    							value = concept.getValue();
    							scheme = concept.getClassificationScheme();
    						}
    					}
    	
    					String name = iname.getValue();
    					if (name != null)
    						keyr.setKeyName(name);
    	
    					if (value != null)
    						keyr.setKeyValue(value);
    					
    					if (scheme != null) {
    						Key key = scheme.getKey();
    						if (key != null && key.getId() != null)
    							keyr.setTModelKey(key.getId());
    					}
    				}
                }
			}
			return cbag;
    	} catch (Exception ud) {
			throw new JAXRException("Apache JAXR Impl:", ud);
		}
    }

	public static TModelBag getTModelBagFromSpecifications(Collection specifications) throws JAXRException {
    	try {
			if (specifications == null || specifications.size()==0)
				return null;
    		
    		// Classifications
			TModelBag tbag = objectFactory.createTModelBag();
			Iterator speciter = specifications.iterator();
			while (speciter.hasNext()) {
				SpecificationLink specification = (SpecificationLink) speciter.next();
				if (specification.getSpecificationObject() != null) {
					RegistryObject ro = specification.getSpecificationObject();
					if (ro.getKey() != null) {
						Key key = ro.getKey();
						tbag.getTModelKey().add(key.toString());
					}
				}
			}
			return tbag;
    	} catch (Exception ud) {
			throw new JAXRException("Apache JAXR Impl:", ud);
		}
    }

	
	/**
     * Adds the objects identifiers from JAXR's external identifier collection
     * 
     * @param identifiers
     * @param ibag
     * @throws JAXRException
     */
	public static IdentifierBag getIdentifierBagFromExternalIdentifiers(Collection identifiers) throws JAXRException {
    	try {
			if (identifiers == null || identifiers.size()==0)
				return null;
    		
    		// Identifiers
			IdentifierBag ibag = objectFactory.createIdentifierBag();
			Iterator iditer = identifiers.iterator();
			while (iditer.hasNext()) {
				ExternalIdentifier extid = (ExternalIdentifier) iditer.next();
				if (extid != null ) {
					KeyedReference keyr = objectFactory.createKeyedReference();
					ibag.getKeyedReference().add(keyr);
	
					InternationalStringImpl iname = (InternationalStringImpl) ((RegistryObject) extid).getName();
					String value = extid.getValue();
					ClassificationScheme scheme = extid.getIdentificationScheme();
	
					String name = iname.getValue();
					if (name != null)
						keyr.setKeyName(name);
	
					if (value != null)
						keyr.setKeyValue(value);
					
					if (scheme != null) {
						Key key = scheme.getKey();
						if (key != null && key.getId() != null)
							keyr.setTModelKey(key.getId());
					}
				}
			}
			return ibag;
    	} catch (Exception ud) {
			throw new JAXRException("Apache JAXR Impl:", ud);
		}
    }
    
    private static OverviewDoc getOverviewDocFromExternalLink(ExternalLink link)
       throws JAXRException
       {
           OverviewDoc od = objectFactory.createOverviewDoc();
           String url = link.getExternalURI();
           if(url != null)
               od.setOverviewURL(url);
           InternationalString extDesc = link.getDescription();
           if(extDesc != null) {
               Description description = objectFactory.createDescription();
               od.getDescription().add(description);
               description.setValue(extDesc.getValue());
           }
           return od;
       }

    private static BindingTemplates getBindingTemplates(Collection serviceBindings)
        throws JAXRException {
        BindingTemplates bt = null;
        if(serviceBindings != null && serviceBindings.size() > 0) {
            bt = objectFactory.createBindingTemplates();
            Iterator iter = serviceBindings.iterator();
            int currLoc = 0;
            BindingTemplate[] bindingTemplateArray = new BindingTemplate[serviceBindings.size()];
            while(iter.hasNext()) {
                ServiceBinding sb = (ServiceBinding)iter.next();
                bindingTemplateArray[currLoc] = getBindingTemplateFromJAXRSB(sb);
                currLoc++;
            }
            if (bindingTemplateArray != null) {
                bt.getBindingTemplate().addAll(Arrays.asList(bindingTemplateArray));
            }
        }
        return bt; 
    } 
}
