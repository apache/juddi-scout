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

import java.net.PasswordAuthentication;
import java.util.HashSet;
import java.util.Set;

import javax.xml.registry.JAXRException;

/**
 * Test to check Jaxr Publish
 * Open source UDDI Browser  <http://www.uddibrowser.org>
 * can be used to check your results
 * @author <mailto:anil@apache.org>Anil Saldhana
 * @since Nov 20, 2004
 */
public class JAXR00ConnectionTest extends BaseTestCase
{
    public void setUp()
    {
        super.setUp();
    }

    public void tearDown()
    {
      super.tearDown();
    }

    public void testConnection()
    {
        PasswordAuthentication passwdAuth = new PasswordAuthentication(userid,
                passwd.toCharArray());
        Set<PasswordAuthentication> creds = new HashSet<PasswordAuthentication>();
        creds.add(passwdAuth);

        try
        {
            connection.setCredentials(creds);
            assertNotNull(connection);
            
        } catch (JAXRException e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
        
    }
}
