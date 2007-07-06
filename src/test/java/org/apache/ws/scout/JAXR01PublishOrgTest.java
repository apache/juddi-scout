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
package org.apache.ws.scout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.EmailAddress;
import javax.xml.registry.infomodel.ExternalIdentifier;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.PersonName;
import javax.xml.registry.infomodel.PostalAddress;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.TelephoneNumber;
import javax.xml.registry.infomodel.User;

/**
 * Test to check Jaxr Publish
 * Open source UDDI Browser  <http://www.uddibrowser.org>
 * can be used to check your results
 * @author <mailto:anil@apache.org>Anil Saldhana
 * @since Nov 20, 2004
 */
public class JAXR01PublishOrgTest extends BaseTestCase
{

    private BusinessLifeCycleManager blm = null;

    public void setUp()
    {
        super.setUp();
    }

    public void tearDown()
    {
      super.tearDown();
    }

    public void testPublish()
    {
        BulkResponse response = null;
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();

            blm = rs.getBusinessLifeCycleManager();
            Collection<Organization> orgs = new ArrayList<Organization>();
            Organization org = createOrganization();

            orgs.add(org);
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
            
            BusinessQueryManager bqm = rs.getBusinessQueryManager();
            System.out.println("We have the Business Query Manager");

            // Define find qualifiers and name patterns
            Collection<String> findQualifiers = new ArrayList<String>();
            findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);
            Collection<String> namePatterns = new ArrayList<String>();
            namePatterns.add("%Kurt%");

            // Find based upon qualifier type and values
            System.out.println("\n-- searching the registry --\n");
            response =
                    bqm.findOrganizations(findQualifiers,
                            namePatterns,
                            null,
                            null,
                            null,
                            null);
            
        } catch (JAXRException e) {
            e.printStackTrace();
			fail(e.getMessage());
        }
        assertNotNull(response);
    }

    /**
     * Creates a Jaxr Organization with 1 or more services
     * @return
     * @throws JAXRException
     */
    private Organization createOrganization()
            throws JAXRException
    {
        Organization org = blm.createOrganization(getIString("Kurt SCOUT TEST"));
        org.setDescription(getIString("Apache Software Foundation"));
        Service service = blm.createService(getIString("Apache JAXR Service with Binding"));
        service.setDescription(getIString("Services of UDDI Registry"));
        
        ServiceBinding serviceBinding = blm.createServiceBinding();
		serviceBinding.setName(blm.createInternationalString("JBossESB Test ServiceBinding"));
		serviceBinding.setDescription(blm.createInternationalString("Binding Description"));
		serviceBinding.setAccessURI("http://www.jboss.com/services/TestService");
	    service.addServiceBinding(serviceBinding);
        
        User user = blm.createUser();
        org.setPrimaryContact(user);
        PersonName personName = blm.createPersonName("Anil S");
        TelephoneNumber telephoneNumber = blm.createTelephoneNumber();
        telephoneNumber.setNumber("410-666-7777");
        telephoneNumber.setType(null);
        PostalAddress address
                = blm.createPostalAddress("1901",
                        "Munsey Drive", "Forest Hill",
                        "MD", "USA", "21050-2747", "");
        Collection<PostalAddress> postalAddresses = new ArrayList<PostalAddress>();
        postalAddresses.add(address);
        Collection<EmailAddress> emailAddresses = new ArrayList<EmailAddress>();
        EmailAddress emailAddress = blm.createEmailAddress("anil@apache.org");
        emailAddresses.add(emailAddress);

        Collection<TelephoneNumber> numbers = new ArrayList<TelephoneNumber>();
        numbers.add(telephoneNumber);
        user.setPersonName(personName);
        user.setPostalAddresses(postalAddresses);
        user.setEmailAddresses(emailAddresses);
        user.setTelephoneNumbers(numbers);

        ClassificationScheme cScheme = getClassificationScheme("ntis-gov:naics", "");
        Key cKey = blm.createKey("uuid:C0B9FE13-324F-413D-5A5B-2004DB8E5CC2");
        cScheme.setKey(cKey);
        Classification classification = blm.createClassification(cScheme,
                "Computer Systems Design and Related Services",
                "5415");
        org.addClassification(classification);
        ClassificationScheme cScheme1 = getClassificationScheme("D-U-N-S", "");
        Key cKey1 = blm.createKey("uuid:3367C81E-FF1F-4D5A-B202-3EB13AD02423");
        cScheme1.setKey(cKey1);
        ExternalIdentifier ei =
                blm.createExternalIdentifier(cScheme1, "D-U-N-S number",
                        "08-146-6849");
        org.addExternalIdentifier(ei);
        org.addService(service);
        return org;
    }

    

    private InternationalString getIString(String str)
            throws JAXRException
    {
        return blm.createInternationalString(str);
    }

    private ClassificationScheme getClassificationScheme(String str1, String str2)
            throws JAXRException
    {
        ClassificationScheme cs = blm.createClassificationScheme(getIString(str1),
                getIString(str2));
        return cs;
    }

}
