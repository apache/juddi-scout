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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Key; 
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.Service;

import org.apache.ws.scout.registry.ConnectionFactoryImpl;
import org.apache.ws.scout.registry.infomodel.KeyImpl;



/**  
 * Locates a Service in the UDDI Registry
 * @author Anil Saldhana  <anil@apache.org>
 */
public class FindService {
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
	        
	        Key key = new KeyImpl( "67AB9FD0-C3D9-11D8-BC4B-D52B9593C1C0");
	
	        BulkResponse br = bqm.findServices(key, fQualifiers,
	                    names, null, null);
	        
	        if (br != null && 
	                 br.getStatus() == JAXRResponse.STATUS_SUCCESS) {
	            System.out.println("Successfully queried the  registry");
	            System.out.println("for services matching the " +
	                       "name pattern: \"" + qname + "\"");
	            
	                    Collection sers = br.getCollection();
	                    System.out.println("Results found: " + sers.size() + "\n");
	                    Iterator iter = sers.iterator();
	                    while (iter.hasNext()) {
	                        Service s = (Service) iter.next();
	                        System.out.println("Service Name: " +
	                            getName(s));
	                        System.out.println("Service Key: " +
	                            s.getKey().getId());
	                        System.out.println("Organization Description: " +
	                            getDescription(s));	                         
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
