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
package org.apache.ws.scout.registry.query;

import junit.framework.TestCase;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.EmailAddress;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.PersonName;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.TelephoneNumber;
import javax.xml.registry.infomodel.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

/**
 * Testcase for JaxrQuery
 * @author <mailto:dims@yahoo.com>Davanum Srinivas
 * @author <mailto:anil@apache.org>Anil Saldhana
 */
public class JAXRQueryTest extends TestCase
{
    public void testQuery() throws Exception
    {
        String queryString = "IBM";
        Connection connection = null;

        // Define connection configuration properties
        // To query, you need only the query URL
        Properties props = new Properties();
        props.setProperty("javax.xml.registry.queryManagerURL",
                //        "http://uddi.microsoft.com:80/inquire");
                "http://localhost:8080/juddi/inquiry");
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
            fail(e.getMessage());
        }

        try
        {
            // Get registry service and business query manager
            RegistryService rs = connection.getRegistryService();
            BusinessQueryManager bqm = rs.getBusinessQueryManager();
            System.out.println("We have the Business Query Manager");

            // Define find qualifiers and name patterns
            Collection findQualifiers = new ArrayList();
            findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);
            Collection namePatterns = new ArrayList();
            namePatterns.add("%" + queryString + "%");

            // Find based upon qualifier type and values
            System.out.println("\n-- searching the registry --\n");
            BulkResponse response =
                    bqm.findOrganizations(findQualifiers,
                            namePatterns,
                            null,
                            null,
                            null,
                            null);

            // check how many organisation we have matched
            Collection orgs = response.getCollection();
            if (orgs == null)
            {
                System.out.println("\n-- Matched 0 orgs");

            } else
            {
                System.out.println("\n-- Matched " + orgs.size() + " organisations --\n");

                // then step through them
                for (Iterator orgIter = orgs.iterator(); orgIter.hasNext();)
                {
                    Organization org = (Organization) orgIter.next();
                    System.out.println("Org name: " + getName(org));
                    System.out.println("Org description: " + getDescription(org));
                    System.out.println("Org key id: " + getKey(org));

                    printUser(org);

                    printServices(org);
                    // Print spacer between organizations
                    System.out.println(" --- ");
                }
            }//end else
        } catch (JAXRException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        } finally
        {
            connection.close();
        }

    }

    private static void printServices(Organization org)
            throws JAXRException
    {
        // Display service and binding information
        Collection services = org.getServices();
        for (Iterator svcIter = services.iterator(); svcIter.hasNext();)
        {
            Service svc = (Service) svcIter.next();
            System.out.println(" Service name: " + getName(svc));
            System.out.println(" Service description: " + getDescription(svc));
            Collection serviceBindings = svc.getServiceBindings();
            for (Iterator sbIter = serviceBindings.iterator(); sbIter.hasNext();)
            {
                ServiceBinding sb = (ServiceBinding) sbIter.next();
                System.out.println("  Binding Description: " + getDescription(sb));
                System.out.println("  Access URI: " + sb.getAccessURI());
            }
        }
    }

    private static void printUser(Organization org)
    throws JAXRException
    {
        // Display primary contact information
        User pc = org.getPrimaryContact();
        if (pc != null)
        {
            PersonName pcName = pc.getPersonName();
            System.out.println(" Contact name: " + pcName.getFullName());
            Collection phNums = pc.getTelephoneNumbers(pc.getType());
            for (Iterator phIter = phNums.iterator(); phIter.hasNext();)
            {
                TelephoneNumber num = (TelephoneNumber) phIter.next();
                System.out.println("  Phone number: " + num.getNumber());
            }
            Collection eAddrs = pc.getEmailAddresses();
            for (Iterator eaIter = eAddrs.iterator(); eaIter.hasNext();)
            {
                System.out.println("  Email Address: " + (EmailAddress) eaIter.next());
            }
        }
    }

    private static String getName(RegistryObject ro) throws JAXRException
    {
        if (ro != null && ro.getName() != null)
        {
            return ro.getName().getValue();
        }
        return "";
    }

    private static String getDescription(RegistryObject ro) throws JAXRException
    {
        if (ro != null && ro.getDescription() != null)
        {
            return ro.getDescription().getValue();
        }
        return "";
    }

    private static String getKey(RegistryObject ro) throws JAXRException
    {
        if (ro != null && ro.getKey() != null)
        {
            return ro.getKey().getId();
        }
        return "";
    }
}
