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
package org.apache.ws.scout.registry.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.EmailAddress;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.PersonName;
import javax.xml.registry.infomodel.PostalAddress;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.TelephoneNumber;
import javax.xml.registry.infomodel.User;

import junit.framework.JUnit4TestAdapter;

import org.apache.ws.scout.BaseTestCase;
import org.apache.ws.scout.Creator;
import org.apache.ws.scout.Finder;
import org.apache.ws.scout.Printer;
import org.apache.ws.scout.Remover;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test to check that the primary contact is added
 * @author <a href="mailto:tcunning@redhat.com">Tom Cunningham</a>
 * @since Dec 6, 2007
 */
public class JAXR015PrimaryContactTest extends BaseTestCase
{
	private static final String PERSON_NAME = "John AXel Rose";
	private static final String PHONE_NUMBER = "111-222-3333";
	private static final String STREET_NUMBER = "1";
	private static final String STREET = "Uddi Drive";
	private static final String CITY = "Apache Town";
	private static final String STATE = "CA";
	private static final String COUNTRY = "USA";
	private static final String POSTAL_CODE = "00000-1111";

	private static final String EMAIL = "jaxr@apache.org";

    @Before
    public void setUp()
    {
        super.setUp();
    }
    
    @After
    public void tearDown()
    {
      super.tearDown();
    }
    
    @Test 
    public void publishClassificationScheme()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
            Creator creator = new Creator(blm);
            
            Collection<ClassificationScheme> schemes = new ArrayList<ClassificationScheme>();
            ClassificationScheme classificationScheme = creator.createClassificationScheme(this.getClass().getName());
            schemes.add(classificationScheme);
            
            BulkResponse bulkResponse = blm.saveClassificationSchemes(schemes);
            assertEquals(JAXRResponse.STATUS_SUCCESS,bulkResponse.getStatus());
            
            
        } catch (JAXRException e) {
            e.printStackTrace();
            assertTrue(false);
        }   
    }
    
    @Test
    public void publishOrganization()
    {
        BulkResponse response = null;
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
            bqm = rs.getBusinessQueryManager();
            Creator creator = new Creator(blm);
            Finder finder = new Finder(bqm, uddiversion);
            
            Collection<Organization> orgs = new ArrayList<Organization>();
            Organization organization = creator.createOrganization(this.getClass().getName());
//          Add a Service
            Service service = creator.createService(this.getClass().getName());
            ServiceBinding serviceBinding = creator.createServiceBinding();
            service.addServiceBinding(serviceBinding);
            organization.addService(service);
            //Add a classification
            ClassificationScheme cs = finder.findClassificationSchemeByName(this.getClass().getName());
            Classification classification = creator.createClassification(cs);
            organization.addClassification(classification);
            
            User user = blm.createUser();
            PersonName personName = blm.createPersonName(PERSON_NAME);
            TelephoneNumber telephoneNumber = blm.createTelephoneNumber();
            telephoneNumber.setNumber(PHONE_NUMBER);
            telephoneNumber.setType(null);
            PostalAddress address = blm.createPostalAddress(STREET_NUMBER,
                STREET, CITY, STATE, COUNTRY, POSTAL_CODE, "");

            Collection<PostalAddress> postalAddresses = new ArrayList<PostalAddress>();
            postalAddresses.add(address);
            Collection<EmailAddress> emailAddresses = new ArrayList<EmailAddress>();
            EmailAddress emailAddress = blm.createEmailAddress(EMAIL);
            emailAddresses.add(emailAddress);

            Collection<TelephoneNumber> numbers = new ArrayList<TelephoneNumber>();
            numbers.add(telephoneNumber);
            user.setPersonName(personName);
            user.setPostalAddresses(postalAddresses);
            user.setEmailAddresses(emailAddresses);
            user.setTelephoneNumbers(numbers);
            organization.setPrimaryContact(user);

            orgs.add(organization);            
            
            //Now save the Organization along with a Service, ServiceBinding and Classification
            BulkResponse br = blm.saveOrganizations(orgs);
            if (br.getStatus() == JAXRResponse.STATUS_SUCCESS)
            {
                System.out.println("Organization Saved");
                Collection coll = br.getCollection();
                Iterator iter = coll.iterator();
                while (iter.hasNext())
                {
                    Key key = (Key) iter.next();
                    System.out.println("Saved Key=" + key.getId());
                }//end while
            } else
            {
                System.err.println("JAXRExceptions " +
                        "occurred during save:");
                Collection exceptions = br.getExceptions();
                Iterator iter = exceptions.iterator();
                while (iter.hasNext())
                {
                    Exception e = (Exception) iter.next();
                    System.err.println(e.toString());
                }
            }
            
        } catch (JAXRException e) {
            e.printStackTrace();
			assertTrue(false);
        }
        assertNull(response);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void queryOrganization()
    {
        login();
        try
        {
            // Get registry service and business query manager
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            System.out.println("We have the Business Query Manager");
            Printer printer = new Printer();
            Finder finder = new Finder(bqm, uddiversion);

            Collection orgs = finder.findOrganizationsByName(this.getClass().getName());
            if (orgs == null) {
                fail("Only Expecting 1 Organization");
            } else {
                assertEquals(1,orgs.size());
                // then step through them
                for (Iterator orgIter = orgs.iterator(); orgIter.hasNext();)
                {
                    Organization org = (Organization) orgIter.next();
                    System.out.println("Org name: " + printer.getName(org));
                    System.out.println("Org description: " + printer.getDescription(org));
                    System.out.println("Org key id: " + printer.getKey(org));

                    User user = org.getPrimaryContact();
                    System.out.println("Primary Contact Full Name : " + user.getPersonName().getFullName());
					assertEquals("User name does not match", user.getPersonName().getFullName(), PERSON_NAME);
					
                    Collection<EmailAddress> emailAddresses = user.getEmailAddresses();
					System.out.println("Found " + emailAddresses.size() + " email addresses.");
                    assertEquals("Should have found 1 email address, found " + emailAddresses.size(), 1, emailAddresses.size());
					for (EmailAddress email : emailAddresses) {
                    	System.out.println("Primary Contact email : " + email.getAddress());
						assertEquals("Email should be " + EMAIL, EMAIL, email.getAddress());
                    }
					
                    Collection<PostalAddress> postalAddresses = user.getPostalAddresses();
					System.out.println("Found " + postalAddresses.size() + " postal addresses.");
                    assertEquals("Should have found 1 postal address, found " + postalAddresses.size(), 1, postalAddresses.size());
					for (PostalAddress postalAddress : postalAddresses) {
						System.out.println("Postal Address is " + postalAddress);
						assertEquals("Street number should be " + STREET_NUMBER, STREET_NUMBER, postalAddress.getStreetNumber());
						assertEquals("Street should be " + STREET, STREET, postalAddress.getStreet());
						assertEquals("City should be " + CITY, CITY, postalAddress.getCity());
						assertEquals("State should be " + STATE, STATE, postalAddress.getStateOrProvince());
						assertEquals("Country should be " + COUNTRY, COUNTRY, postalAddress.getCountry());
						assertEquals("Postal code should be " + POSTAL_CODE, POSTAL_CODE, postalAddress.getPostalCode());
                    }
					
					Collection<TelephoneNumber> numbers = user.getTelephoneNumbers(null);
					System.out.println("Found " + numbers.size() + " telephone numbers.");
                    assertEquals("Should have found 1 phone number, found " + numbers.size(), 1, numbers.size());
					for (TelephoneNumber tele : numbers) {
						System.out.println("Phone number is " + tele.getNumber());
						assertEquals("Telephone number should be " + PHONE_NUMBER, PHONE_NUMBER, tele.getNumber());
					}
                    printer.printServices(org);
                    printer.printClassifications(org);
                }
            }//end else
        } catch (JAXRException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } 
    }
    
    @Test
    public void deleteOrganization()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            blm = rs.getBusinessLifeCycleManager();
    //      Get registry service and business query manager
            bqm = rs.getBusinessQueryManager();
            System.out.println("We have the Business Query Manager");
            Finder finder = new Finder(bqm, uddiversion);
            Remover remover = new Remover(blm);
            Collection orgs = finder.findOrganizationsByName(this.getClass().getName());
            for (Iterator orgIter = orgs.iterator(); orgIter.hasNext();)
            {
                Organization org = (Organization) orgIter.next();
                remover.removeOrganization(org);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void deleteClassificationScheme()
    {
        login();
        try {
            RegistryService rs = connection.getRegistryService();
            bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();
            System.out.println("We have the Business Query Manager");
            Finder finder = new Finder(bqm, uddiversion);
            Remover remover = new Remover(blm);
            Collection schemes = finder.findClassificationSchemesByName(this.getClass().getName());
            for (Iterator iter = schemes.iterator(); iter.hasNext();)
            {
                ClassificationScheme scheme = (ClassificationScheme) iter.next();
                remover.removeClassificationScheme(scheme);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(JAXR015PrimaryContactTest.class);
    }
}
