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

import junit.framework.TestCase;

import javax.xml.registry.*;
import javax.xml.registry.infomodel.*;
import java.util.*;
import java.net.PasswordAuthentication;

/**
 * Test to check Jaxr Publish
 * Open source UDDI Browser  <http://www.uddibrowser.org>
 * can be used to check your results
 * @author <mailto:anil@apache.org>Anil Saldhana
 * @since Nov 20, 2004
 */
public class JaxrPublishOrgTest extends TestCase
{
    private Connection connection = null;
    //Tested on a local jboss instance
    private String userid = "jboss";
    private String passwd = "jboss";
    private BusinessLifeCycleManager blm = null;

    public void setUp()
    {
        // Define connection configuration properties
        // To query, you need only the query URL
        Properties props = new Properties();
        props.setProperty("javax.xml.registry.queryManagerURL",
                "http://localhost:8080/juddi/inquiry");
        props.setProperty("javax.xml.registry.lifeCycleManagerURL",
                "http://localhost:8080/juddi/publish");
        props.setProperty("javax.xml.registry.factoryClass",
                "org.apache.ws.scout.registry.ConnectionFactoryImpl");

        try
        {
            // Create the connection, passing it the configuration properties
            ConnectionFactory factory = ConnectionFactory.newInstance();
            factory.setProperties(props);
            connection = factory.createConnection();
        } catch (JAXRException e)
        {
            e.printStackTrace();
        }
    }

    public void tearDown()
    {
        try
        {
            if (connection != null)
                connection.close();
        } catch (JAXRException e)
        {

        }
    }

    public void testPublish()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();

            blm = rs.getBusinessLifeCycleManager();
            Collection orgs = new ArrayList();
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
        } catch (JAXRException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Jaxr Organization with 1 or more services
     * @return
     * @throws JAXRException
     */
    private Organization createOrganization()
            throws JAXRException
    {
        Organization org = blm.createOrganization(getIString("USA"));
        org.setDescription(getIString("Apache Software Foundation"));
        Service service = blm.createService(getIString("Apache JAXR Service"));
        service.setDescription(getIString("Services of UDDI Registry"));
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
        Collection postalAddresses = new ArrayList();
        postalAddresses.add(address);
        Collection emailAddresses = new ArrayList();
        EmailAddress emailAddress = blm.createEmailAddress("anil@apache.org");
        emailAddresses.add(emailAddress);

        Collection numbers = new ArrayList();
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

    /**
     * Does authentication with the uddi registry
     */
    private void login()
    {
        PasswordAuthentication passwdAuth = new PasswordAuthentication(userid,
                passwd.toCharArray());
        Set creds = new HashSet();
        creds.add(passwdAuth);

        try
        {
            connection.setCredentials(creds);
        } catch (JAXRException e)
        {
            e.printStackTrace();
        }
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
