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

import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import java.util.Properties;

/**
 * Tests connection to UDDI registry
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class TestConnection {
    private static Properties prop = new Properties();

    private static final String queryurl = "http://localhost:8080/juddi/inquiry";

    public static void main(String[] args) {
        try {
            prop.setProperty("javax.xml.registry.queryManagerURL", queryurl);
            prop.setProperty("javax.xml.registry.lifeCycleManagerURL", queryurl);
            prop.setProperty("javax.xml.registry.factoryClass",
                    "org.apache.juddi.jaxr.registry.ConnectionFactoryImpl");
            ConnectionFactory factory = ConnectionFactoryImpl.newInstance();
            factory.setProperties(prop);
            Connection conn = factory.createConnection();
            if (conn == null) System.out.println("No Connection");
        } catch (Exception e) {
            e.printStackTrace();
        }//end catch
    }//end main

}//end class
