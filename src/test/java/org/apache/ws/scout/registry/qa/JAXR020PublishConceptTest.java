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
package org.apache.ws.scout.registry.qa;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.ExternalLink;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Key;

import junit.framework.JUnit4TestAdapter;

import org.apache.ws.scout.BaseTestCase;
import org.apache.ws.scout.Printer;
import org.apache.ws.scout.Remover;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests publish concepts.
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
public class JAXR020PublishConceptTest extends BaseTestCase
{
    private static String CONCEPT_NAME = "Apache Scout Concept -- APACHE SCOUT TEST";
    
    @Before
    public void setUp()
    {
       super.setUp();
    }

    @After
    public void tearDown()
    {
        super.tearDown();
    }

    @Test
    public void testPublishConcept()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            BusinessQueryManager bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();
           
            Concept concept = blm.createConcept(null, CONCEPT_NAME, "");
            InternationalString is = blm.createInternationalString("This is the concept for Apache Scout Test");
            concept.setDescription(is);


            //Lets provide a link to juddi registry
            ExternalLink wslink =
                    blm.createExternalLink("http://to-rhaps4.toronto.redhat.com:9000/juddi",
                            "juddi");
            concept.addExternalLink(wslink);
            Classification cl = createClassificationForUDDI(bqm);

            concept.addClassification(cl);

            Collection<Concept> concepts = new ArrayList<Concept>();
            concepts.add(concept);

            Key key=null;
            BulkResponse br = blm.saveConcepts(concepts);
            if (br.getStatus() == JAXRResponse.STATUS_SUCCESS)
            {
                System.out.println("Concept Saved");
                Collection coll = br.getCollection();
                Iterator iter = coll.iterator();
                while (iter.hasNext())
                {
                    key = (Key) iter.next();
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
            
            Concept savedConcept = (Concept)bqm.getRegistryObject(key.getId(),LifeCycleManager.CONCEPT);
            System.out.println("Save concept=" + savedConcept);
            
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
    
    public void testDeleteConcept()
    {
        login();
        try
        {
            RegistryService rs = connection.getRegistryService();
            BusinessQueryManager bqm = rs.getBusinessQueryManager();
            blm = rs.getBusinessLifeCycleManager();
            Printer printer = new Printer();
            Remover remover = new Remover(blm);

            Collection<String> findQualifiers = new ArrayList<String>();
            findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);
            Collection<String> namePatterns = new ArrayList<String>();
            namePatterns.add("%" + CONCEPT_NAME + "%");

            BulkResponse br = bqm.findConcepts(findQualifiers, namePatterns, null, null, null);

//          check how many organisation we have matched
            Collection concepts = br.getCollection();

            if (concepts == null)
            {
                System.out.println("\n-- Matched 0 orgs");

            } else
            {
                System.out.println("\n-- Matched " + concepts.size() + " concepts --\n");

                // then step through them
                for (Iterator conceptIter = concepts.iterator(); conceptIter.hasNext();)
                {
                    Concept c = (Concept) conceptIter.next();
                    
                    System.out.println("Id: " + c.getKey().getId());
                    System.out.println("Name: " + c.getName().getValue());

                    // Links are not yet implemented in scout -- so concepts 
                    // created via scout won't have links 
                    printer.printExternalLinks(c);

                    // Print spacer between messages
                    System.out.println(" --- ");
                    
                    remover.removeConcept(c);
                    
                    System.out.println(" === ");
                }
            }//end else
        } catch (JAXRException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(JAXR020PublishConceptTest.class);
    }

}
