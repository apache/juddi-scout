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

import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.EmailAddress;
import javax.xml.registry.infomodel.ExternalLink;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.PersonName;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.TelephoneNumber;
import javax.xml.registry.infomodel.User;

/**
 * Print out code for Registry Objects
 * @author <mailto:kstam@apache.org>Kurt Stam
 */
public class Printer
{
   
    public void printOrganisation(Organization org) throws JAXRException
    {
        System.out.println("Org name: " + getName(org));
        System.out.println("Org description: " + getDescription(org));
        System.out.println("Org key id: " + getKey(org));
        printUser(org);
        printServices(org);
        printClassifications(org);
    }         

    public void printServices(Organization org)
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

    public void printUser(Organization org)
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
    
    public void printExternalLinks(Concept concept)
    throws JAXRException
    {
        Collection links = concept.getExternalLinks();
        for (Iterator lnkIter = links.iterator(); lnkIter.hasNext();)
        {
             System.out.println("Link: " + ((ExternalLink) lnkIter.next()).getExternalURI().charAt(0));
        }
    }

    public String getName(RegistryObject ro) throws JAXRException
    {
        if (ro != null && ro.getName() != null)
        {
            return ro.getName().getValue();
        }
        return "";
    }

    public String getDescription(RegistryObject ro) throws JAXRException
    {
        if (ro != null && ro.getDescription() != null)
        {
            return ro.getDescription().getValue();
        }
        return "";
    }

    public String getKey(RegistryObject ro) throws JAXRException
    {
        if (ro != null && ro.getKey() != null)
        {
            return ro.getKey().getId();
        }
        return "";
    }

    public void printClassifications(Organization ro) throws JAXRException
    {
    	Collection c = ro.getClassifications();
    	Iterator i = c.iterator();

    	System.out.println("Classification: " + ro.getClassifications());
    	while (i.hasNext()) {
    		Classification cl = (Classification)i.next();
    		System.out.println("Classification: " + cl.getName());
    	}
    }
    
}
