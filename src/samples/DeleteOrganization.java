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
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Organization;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Deletes an Organization in the UDDI Registry.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class DeleteOrganization {
   private static Properties prop = new Properties();

    private static final String queryurl = "http://localhost:8080/juddi/inquiry";
    private static final String publishurl="http://localhost:8080/juddi/publish";
    private static BusinessLifeCycleManager blm = null;
    private static BusinessQueryManager bqm = null;
    
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
          bqm = rs.getBusinessQueryManager();
          blm = rs.getBusinessLifeCycleManager();
          
          Collection keys = findOrganizations( "USA%");         
          BulkResponse response = blm.deleteOrganizations(keys);
          Collection exceptions = response.getExceptions();
          if (exceptions == null) {
                System.out.println("Organization deleted");
                Collection retKeys = response.getCollection();
                Iterator keyIter = retKeys.iterator();
                javax.xml.registry.infomodel.Key orgKey = null;
                while (keyIter.hasNext()) {
                    orgKey = 
                            (javax.xml.registry.infomodel.Key) keyIter.next();
                    String id = orgKey.getId();
                    System.out.println("Organization key was " + id);
                }
        }
     }catch (JAXRException e) {
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
    
    //Get a Collection of Keys for Organizations that match specific string
    private static Collection findOrganizations( String match)
    throws JAXRException{
        Collection keys = new ArrayList();
        try{
        
            ArrayList names = new ArrayList();
            names.add(match);

            Collection fQualifiers = new ArrayList();
            fQualifiers.add(FindQualifier.SORT_BY_NAME_DESC);

            BulkResponse br = bqm.findOrganizations(fQualifiers,
                    names, null, null, null, null);
        
            if (br.getStatus() == JAXRResponse.STATUS_SUCCESS) {
                System.out.println("Successfully queried the  registry");
                System.out.println("for organizations matching the " +
                       "name pattern: \"" + match + "\"");
            
                    Collection orgs = br.getCollection();
                    System.out.println("Results found: " + orgs.size() + "\n");
                    Iterator iter = orgs.iterator();
                    while (iter.hasNext()) {
                        Organization org = (Organization) iter.next();
                        keys.add( org.getKey() );                         
                    }
                } else {
                    System.err.println("One or more JAXRExceptions " +
                        "occurred during the query operation:");
                    Collection exceptions = br.getExceptions();
                    Iterator iter = exceptions.iterator();
                    while (iter.hasNext()) {
                        Exception e = (Exception) iter.next();
                        System.err.println(e.toString());
                    }
                }
        }catch(Exception e ) {
         e.printStackTrace();
        }//end catch
       return keys;
    }
}//end class
