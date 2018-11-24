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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.RegistryObject;

import junit.framework.JUnit4TestAdapter;

import org.apache.ws.scout.BaseTestCase;
import org.apache.ws.scout.Creator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests Publish, Delete (and indirectly, find) for associations.
 * 
 * You can comment out the deletion portion and use Open source UDDI Browser
 * <http://www.uddibrowser.org> to check your intermediate results.
 * 
 * Based on query/publish tests written by <a href="mailto:anil@apache.org">Anil
 * Saldhana</a>.
 * 
 * @author <a href="mailto:dbhole@redhat.com">Deepak Bhole</a>
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * 
 * @since Sep 27, 2005
 */
public class JAXR030AssociationsTest extends BaseTestCase {

	private BusinessQueryManager bqm = null;

	String associationType = "AssociationType/RelatedTo";
	private static String tempSrcOrgName = "Apache Source Org -- APACHE SCOUT TEST";
	private static String tempTgtOrgName = "Apache Target Org -- APACHE SCOUT TEST";

	private Organization sOrg=null;
	private Organization tOrg=null;

    @Before
	public void setUp() {
		super.setUp();
	}

    @After
	public void tearDown() {
		super.tearDown();
	}

	/**
	 * Tests publishing and deleting of associations.
	 * 
	 * Do not break this into testPublish(), testDelete(), etc. Order is
	 * important, and not all jvms can guarantee order since the JUnit framework
	 * uses getMethods() to gather test methods, and getMethods() does not
	 * guarantee order.
	 */
    @Test
	public void testPublishFindAndDeleteAssociation() {
		login();
		
		try {
			
			RegistryService rs = connection.getRegistryService();
			bqm = rs.getBusinessQueryManager();
			blm = rs.getBusinessLifeCycleManager();
			
			//deleting any pre-exisiting organizations
			ArrayList<Organization> orgs = findTempOrgs();
			for (Organization organization : orgs) {
				Collection<Key> keys = new ArrayList<Key>();
				keys.add(organization.getKey());
				blm.deleteOrganizations(keys);
			}
			
            Creator creator = new Creator(blm);

			System.out.println("\nCreating temporary organizations...\n");
            Organization org1 = creator.createOrganization(tempSrcOrgName);
            Organization org2 = creator.createOrganization(tempTgtOrgName);
            Collection<Organization> organizations = new ArrayList<Organization>();
            organizations.add(org1);
            organizations.add(org2);
            blm.saveOrganizations(organizations);
           
			System.out.println("\nSearching for newly created organizations...\n");
			ArrayList<Organization> newOrgs = findTempOrgs();
                        Assert.assertEquals(2, newOrgs.size());
			sOrg = newOrgs.get(0);
			tOrg = newOrgs.get(1);

			System.out.println("\nCreating association...\n");
			createAssociation(sOrg, tOrg);

			// All created ... now try to delete.
			String associationID = findAndDeleteAssociation();
			
			//Let us look for associations now
			BulkResponse associationResp = 
				bqm.findCallerAssociations(null, Boolean.TRUE, Boolean.TRUE, null);
			
			if(associationResp.getExceptions() != null)
			{
				System.out.println(associationResp.getExceptions());
				fail("Association lookup failed");
			}
			else
			{
				Collection retAssocs = associationResp.getCollection();
                if (retAssocs.size() == 0)
                {
                    //Pass
                } else
                {
                   Iterator iterAss = retAssocs.iterator();
                   while(iterAss.hasNext())
                   {
                      Association assc = (Association) iterAss.next();
                      if(assc.getKey().getId().equals(associationID)) {
                    	  System.out.println("found: " + associationID);
                          fail("Deleted Association found");
                      }
                   }
                } 
			}
			 
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
	}
    
	private void createAssociation(Organization sOrg, Organization tOrg)
			throws JAXRException {

		Concept type = bqm.findConceptByPath(associationType);
		Association association = blm.createAssociation(tOrg, type);
		sOrg.addAssociation(association);

		ArrayList<Association> associations = new ArrayList<Association>();
		associations.add(association);

		BulkResponse br = blm.saveAssociations(associations, true);
		if (br.getStatus() == JAXRResponse.STATUS_SUCCESS) {
			System.out.println("Association Saved");
			Collection coll = br.getCollection();
			Iterator iter = coll.iterator();
			while (iter.hasNext()) {
				System.out.println("Saved Key=" + iter.next());
			}// end while
		} else {
			System.err.println("JAXRExceptions " + "occurred during save:");
			Collection exceptions = br.getExceptions();
			Iterator iter = exceptions.iterator();
			while (iter.hasNext()) {
				Exception e = (Exception) iter.next();
				System.err.println(e.toString());
			}
            deleteTempOrgs();
		}
	}

	private String findAndDeleteAssociation() throws JAXRException {

		String id = null;
		
		String sOrgID = sOrg.getKey().getId();
		String tOrgID = tOrg.getKey().getId();

		Collection<String> findQualifiers = new ArrayList<String>();
		findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);

		Concept type = bqm.findConceptByPath(associationType);
		ArrayList<Concept> conceptTypes = new ArrayList<Concept>(1);
		conceptTypes.add(type);

		BulkResponse br = bqm.findAssociations(findQualifiers, sOrgID, tOrgID,
				conceptTypes);
		Collection associations = br.getCollection();

		if (associations == null) {
			System.out.println("\n-- Matched 0 orgs");
            fail("Expected 1 association");

		} else {
			System.out.println("\n-- Matched " + associations.size()
					+ " associations --\n");
            assertEquals(1,associations.size());

			// then step through them
			for (Iterator conceptIter = associations.iterator(); conceptIter
					.hasNext();) {
				Association a = (Association) conceptIter.next();

				System.out.println("Id: " + a.getKey().getId());
				System.out.println("Name: " + a.getName().getValue());

				// Print spacer between messages
				System.out.println(" --- ");

				id = a.getKey().getId();
				deleteAssociation(a.getKey());

				System.out.println("\n ============================== \n");
			} 
		}
		return id;
	}

	private void deleteAssociation(Key key) throws JAXRException {

		String id = key.getId();

		System.out.println("\nDeleting association with id " + id + "\n");

		Collection<Key> keys = new ArrayList<Key>();
		keys.add(key);
		BulkResponse response = blm.deleteAssociations(keys);

        assertEquals(BulkResponse.STATUS_SUCCESS, response.getStatus());
		Collection exceptions = response.getExceptions();
		if (exceptions == null) {
			Collection retKeys = response.getCollection();
			Iterator keyIter = retKeys.iterator();
			javax.xml.registry.infomodel.Key orgKey = null;
			if (keyIter.hasNext()) {
				orgKey = (javax.xml.registry.infomodel.Key) keyIter.next();
				id = orgKey.getId();
				System.out
						.println("Association with ID=" + id + " was deleted");
			}
		}
	}

	private ArrayList<Organization> findTempOrgs() throws JAXRException {

		ArrayList<Organization> toReturn = new ArrayList<Organization>();

		// Define find qualifiers and name patterns
		Collection<String> findQualifiers = new ArrayList<String>();
		findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);
		Collection<String> namePatterns = new ArrayList<String>();
		if ("3.0".equals(uddiversion)) {
			namePatterns.add(tempSrcOrgName);
			namePatterns.add(tempTgtOrgName);
		} else {
			namePatterns.add("%" + tempSrcOrgName + "%");
			namePatterns.add("%" + tempTgtOrgName + "%");
		}
		// Find based upon qualifier type and values
		System.out.println("\n-- searching the registry --\n");
		BulkResponse response = bqm.findOrganizations(findQualifiers,
				namePatterns, null, null, null, null);

		// check how many organisation we have matched
		Collection orgs = response.getCollection();
		if (orgs == null) {
			System.out.println("\n-- Matched 0 orgs");

		} else {
			System.out.println("\n-- Matched " + orgs.size()
					+ " organisations --\n");

			// then step through them
			for (Iterator orgIter = orgs.iterator(); orgIter.hasNext();) {
				Organization org = (Organization) orgIter.next();

				System.out.println("Org name: " + getName(org));
				System.out.println("Org description: " + getDescription(org));
				System.out.println("Org key id: " + getKey(org));

				if (getName(org).indexOf(tempSrcOrgName) > -1) {
					toReturn.add(0, org);
				} else {
					toReturn.add(1, org);
				}

				// Print spacer between organizations
				System.out.println("\n ============================== \n");
			}
		}// end else

		return toReturn;
	}

	private void deleteTempOrgs() {

		try {

			Key sOrgKey = sOrg.getKey();
			Key tOrgKey = tOrg.getKey();

			System.out.println("\nDeleting temporary organizations with ids "
					+ sOrgKey + " and " + tOrgKey + "\n");

			Collection<Key> keys = new ArrayList<Key>();
			keys.add(sOrgKey);
			keys.add(tOrgKey);
			BulkResponse response = blm.deleteOrganizations(keys);

			Collection exceptions = response.getExceptions();
			if (exceptions == null) {
				Collection retKeys = response.getCollection();
				Iterator keyIter = retKeys.iterator();
				Key orgKey = null;
				while (keyIter.hasNext()) {
					orgKey = (javax.xml.registry.infomodel.Key) keyIter.next();
					String id = orgKey.getId();
					System.out.println("Organization with ID=" + id
							+ " was deleted");
				}
			}
		} catch (JAXRException jaxre) {
			jaxre.printStackTrace();
		}
	}

	private static String getName(RegistryObject ro) throws JAXRException {
		if (ro != null && ro.getName() != null) {
			return ro.getName().getValue();
		}
		return "";
	}

	private static String getDescription(RegistryObject ro)
			throws JAXRException {
		if (ro != null && ro.getDescription() != null) {
			return ro.getDescription().getValue();
		}
		return "";
	}

	private static String getKey(RegistryObject ro) throws JAXRException {
		if (ro != null && ro.getKey() != null) {
			return ro.getKey().getId();
		}
		return "";
	}
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(JAXR030AssociationsTest.class);
    }
}
