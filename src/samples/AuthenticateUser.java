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
import javax.xml.registry.*;

import java.util.*;
import java.net.PasswordAuthentication;

import org.apache.ws.scout.registry.ConnectionFactoryImpl;

/**
 * Tests authentication to the UDDI registry.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class AuthenticateUser {
    
    private static Properties prop = new Properties();

    private static final String queryurl = "http://localhost:8080/juddi/inquiry";
    private static final String publishurl="http://localhost:8080/juddi/publish";

   public static void main(String[] args) {
     try{
      prop.setProperty("javax.xml.registry.queryManagerURL",queryurl);
      prop.setProperty("javax.xml.registry.lifeCycleManagerURL", publishurl);
      prop.setProperty("javax.xml.registry.factoryClass",
              "org.apache.juddi.jaxr.registry.ConnectionFactoryImpl");
      
      PasswordAuthentication passwdAuth = new PasswordAuthentication("jdoe", 
          "juddi".toCharArray());
      Set creds = new HashSet();
      creds.add(passwdAuth);
      
      ConnectionFactory factory = ConnectionFactoryImpl.newInstance();
      factory.setProperties(prop);
      Connection conn = factory.createConnection();
      if( conn == null ) System.out.println( "No Connection" );
      conn.setCredentials(creds);
     }catch(Exception e ) {
         e.printStackTrace();
     }//end catch
   }//end main
    
}
