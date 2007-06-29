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
package org.apache.ws.scout.registry.publish;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Key;

import org.apache.ws.scout.BaseTestCase;

/**
 * Tests publish classification schemes.
 * 
 * Open source UDDI Browser  <http://www.uddibrowser.org>
 * to check your results.
 *
 * Based on query/publish tests written by 
 * <a href="mailto:anil@apache.org">Anil Saldhana</a>.
 *
 * @author <a href="mailto:dbhole@redhat.com">Deepak Bhole</a>
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * 
 * @since Sep 27, 2005
 */
public class JAXR01PublishClassificationSchemeTest extends BaseTestCase
{

    private BusinessLifeCycleManager blm = null;

    public void setUp()
    {
        super.setUp();
    }

    public void tearDown()
    {
        super.tearDown();
    }

    public void testPublishClassificationScheme()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            BusinessQueryManager bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();

            ClassificationScheme cScheme = blm.createClassificationScheme("testScheme -- APACHE SCOUT TEST", "Sample Classification Scheme");
            Classification classification = createClassificationForUDDI(bqm);
            cScheme.addClassification(classification);

            ArrayList<ClassificationScheme> cSchemes = new ArrayList<ClassificationScheme>();
            cSchemes.add(cScheme);

            BulkResponse br = blm.saveClassificationSchemes(cSchemes);
            if (br.getStatus() == JAXRResponse.STATUS_SUCCESS)
            {
                System.out.println("Classification Saved");
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
                    fail(e.toString());
                }
            }
            
            //Classificat
        } catch (JAXRException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private Classification createClassificationForUDDI(BusinessQueryManager bqm)
            throws JAXRException
    {
        //Scheme which maps onto uddi tmodel
        ClassificationScheme udditmodel = bqm.findClassificationSchemeByName(null, "uddi-org:types");

        Classification cl = blm.createClassification(udditmodel, "wsdl", "wsdl");
        return cl;
    }

    /**
     * Does authentication with the uddi registry
     */
    private void login()
    {
        PasswordAuthentication passwdAuth = new PasswordAuthentication(userid,
                passwd.toCharArray());
        Set<PasswordAuthentication> creds = new HashSet<PasswordAuthentication>();
        creds.add(passwdAuth);

        try
        {
            connection.setCredentials(creds);
        } catch (JAXRException e)
        {
            e.printStackTrace();
        }
    }
}
