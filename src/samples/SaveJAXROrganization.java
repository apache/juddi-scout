/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.ws.scout.registry.ConnectionFactoryImpl;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
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
import javax.xml.registry.infomodel.TelephoneNumber;
import javax.xml.registry.infomodel.User;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Saves an Organization in the UDDI Registry
 * @author Anil Saldhana  <anil@apache.org>
 */

public class SaveJAXROrganization {
    private static Properties prop = new Properties();

    private static final String queryurl = "http://localhost:8080/juddi/inquiry";
    private static final String publishurl="http://localhost:8080/juddi/publish";
    private static BusinessLifeCycleManager blm = null;
    
    public static void main(String[] args) {
     try{
         
          PasswordAuthentication passwdAuth = new PasswordAuthentication("jdoe", 
          "juddi".toCharArray());
          Set creds = new HashSet();
          creds.add(passwdAuth);
          
          setProperties();    
          Connection conn = getConnection();  
          conn.setCredentials(creds);
          
          RegistryService rs = conn.getRegistryService();
          blm = rs.getBusinessLifeCycleManager();
          Collection orgs = new ArrayList();

          Organization org = blm.createOrganization(getIString("USA"));
          org.setDescription(getIString("Apache Software Foundation"));

         Service service = blm.createService(getIString("Apache JAXR Service"));
         service.setDescription(getIString("Services of UDDI Registry"));

          User user = blm.createUser();
          org.setPrimaryContact(user);
          
          PersonName personName = blm.createPersonName("Steve Viens");  
         
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
          EmailAddress emailAddress = blm.createEmailAddress("sviens@apache.org");
          emailAddresses.add(emailAddress);

          Collection numbers = new ArrayList();
          numbers.add(telephoneNumber);

          user.setPersonName(personName);
          user.setPostalAddresses(postalAddresses);
          user.setEmailAddresses(emailAddresses);
          user.setTelephoneNumbers(numbers);

          //Concepts for NAICS and computer           
          ClassificationScheme cScheme = getClassificationScheme(
                        "ntis-gov:naics",   "");
          
          Key cKey = blm.createKey("uuid:C0B9FE13-324F-413D-5A5B-2004DB8E5CC2");
          cScheme.setKey(cKey);
 
          Classification classification =  blm.createClassification(cScheme,
                        "Computer Systems Design and Related Services", 
                        "5415");

          org.addClassification(classification);

          ClassificationScheme cScheme1 =getClassificationScheme(
                               "D-U-N-S", "");                               
                               
          Key cKey1 =  blm.createKey("uuid:3367C81E-FF1F-4D5A-B202-3EB13AD02423");
          cScheme1.setKey(cKey1);

          ExternalIdentifier ei =
                    blm.createExternalIdentifier(cScheme1, "D-U-N-S number",
                        "08-146-6849");

          org.addExternalIdentifier(ei);
          org.addService(service);

          orgs.add(org);

          BulkResponse br = blm.saveOrganizations(orgs);
          if (br.getStatus() == JAXRResponse.STATUS_SUCCESS) {
                    System.out.println("Organization Saved");
                    Collection coll = br.getCollection();
                    Iterator iter = coll.iterator();
                    while (iter.hasNext()) {
                        Key key = (Key)iter.next();
                        System.out.println("Saved Key="+key.getId());
                    }//end while
          } else {
                    System.err.println("JAXRExceptions " +
                        "occurred during save:");
                    Collection exceptions = br.getExceptions();
                    Iterator iter = exceptions.iterator();
                    while (iter.hasNext()) {
                        Exception e = (Exception) iter.next();
                        System.err.println(e.toString());
                    }
                }
            } catch (JAXRException e) {
                e.printStackTrace();
            }catch( Exception es){
                es.printStackTrace();
            }catch( Throwable t){
                t.printStackTrace();
                System.out.println("Message from throwable="+t.getMessage());
            }
    }
          
          
    private static void setProperties(){
        prop.setProperty("javax.xml.registry.queryManagerURL",queryurl);
        prop.setProperty("javax.xml.registry.lifeCycleManagerURL", publishurl);
        prop.setProperty("javax.xml.registry.factoryClass",
              "org.apache.juddi.jaxr.registry.ConnectionFactoryImpl");
    }
    
    private static Connection getConnection() throws JAXRException{
        ConnectionFactory factory = ConnectionFactoryImpl.newInstance();
        factory.setProperties(prop);
        Connection conn = factory.createConnection();
        if( conn == null ) System.out.println( "No Connection" );
        return conn;    
    }
    
    private static InternationalString getIString( String str)
    throws JAXRException{
        return blm.createInternationalString(str);
    }
    
    private static ClassificationScheme getClassificationScheme(
                     String str1, String str2)
    throws JAXRException{
      ClassificationScheme cs = blm.createClassificationScheme(
                                        getIString(str1),
                                        getIString(str2));    
      return cs;
    }
}
