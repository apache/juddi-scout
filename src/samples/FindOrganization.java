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
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.Service;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

/**
 * Locates an Organization in the UDDI Registry
 * @author Anil Saldhana  <anil@apache.org>
 */
public class FindOrganization {
    private static Properties prop = new Properties();

    private static final String queryurl = "http://localhost:8080/juddi/inquiry";
    private static final String publishurl="http://localhost:8080/juddi/publish";

	   public static void main(String[] args) {
	     try{
	          setProperties();    
	          Connection conn = getConnection();        
	      
	        RegistryService rs = conn.getRegistryService();
	        BusinessQueryManager bqm = rs.getBusinessQueryManager();
	        ArrayList names = new ArrayList();
	        //String qname= "%S%";
	        String qname= "%";
	        names.add(qname);
	
	        Collection fQualifiers = new ArrayList();
	        fQualifiers.add(FindQualifier.SORT_BY_NAME_DESC);
	
	        BulkResponse br = bqm.findOrganizations(fQualifiers,
	                    names, null, null, null, null);
	        
	        if (br.getStatus() == JAXRResponse.STATUS_SUCCESS) {
	            System.out.println("Successfully queried the  registry");
	            System.out.println("for organizations matching the " +
	                       "name pattern: \"" + qname + "\"");
	            
	                    Collection orgs = br.getCollection();
	                    System.out.println("Results found: " + orgs.size() + "\n");
	                    Iterator iter = orgs.iterator();
	                    while (iter.hasNext()) {
	                        Organization org = (Organization) iter.next();
	                        System.out.println("Organization Name: " +
	                            getName(org));
	                        System.out.println("Organization Key: " +
	                            org.getKey().getId());
	                        System.out.println("Organization Description: " +
	                            getDescription(org));
	
	                        Collection services = org.getServices();
	                        Iterator siter = services.iterator();
	                        while (siter.hasNext()) {
	                            Service service = (Service) siter.next();
	                            System.out.println("\tService Name: " +
	                                getName(service));
	                            System.out.println("\tService Key: " +
	                                service.getKey().getId());
	                            System.out.println("\tService Description: " +
	                                getDescription(service));
	                        }
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
	  }//end main 
	   
	 private static void setProperties(){
	     prop.setProperty("javax.xml.registry.queryManagerURL",queryurl);
	     prop.setProperty("javax.xml.registry.lifeCycleManagerURL", publishurl);
	     prop.setProperty("javax.xml.registry.factoryClass",
	              "org.apache.juddi.jaxr.registry.ConnectionFactoryImpl");
	 }
	     
	 private static String getName(RegistryObject regobj ) 
        throws JAXRException {
        try {
              return regobj.getName().getValue();
        } catch (NullPointerException npe) {
            return "";
        }
    }

	private static String getDescription(RegistryObject regobj ) 
	    throws JAXRException {
	        try {
	             return regobj.getDescription().getValue();
	        } catch (NullPointerException npe) {
	            return "";
	        }
	 }

	private static Connection getConnection() throws JAXRException{
	    ConnectionFactory factory = ConnectionFactoryImpl.newInstance();
	    factory.setProperties(prop);
	    Connection conn = factory.createConnection();
	    if( conn == null ) System.out.println( "No Connection" );
	    return conn;
	    
	}  
}
