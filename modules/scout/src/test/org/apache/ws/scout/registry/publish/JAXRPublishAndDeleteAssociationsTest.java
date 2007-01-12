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
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.RegistryObject;

import org.apache.ws.scout.BaseTestCase;


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
public class JAXRPublishAndDeleteAssociationsTest extends BaseTestCase {

	private BusinessLifeCycleManager blm = null;
	private BusinessQueryManager bqm = null;

	String associationType = "/AssociationType/RelatedTo";
	String tempSrcOrgName = "Apache Source Org -- APACHE SCOUT TEST";
	String tempTgtOrgName = "Apache Target Org -- APACHE SCOUT TEST";

	Organization sOrg, tOrg;

	public void setUp() {
		super.setUp();
	}

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

	public void testPublishFindAndDeleteAssociation() {
		login();
		try {
			RegistryService rs = connection.getRegistryService();
			bqm = rs.getBusinessQueryManager();
			blm = rs.getBusinessLifeCycleManager();

			System.out.println("\nCreating temporary organization...\n");
			createTempOrgs();

			System.out
					.println("\nSearching for newly created organizations...\n");
			ArrayList orgs = findTempOrgs();

			sOrg = (Organization) orgs.get(0);
			tOrg = (Organization) orgs.get(1);

			System.out.println("\nCreating association...\n");
			createAssociation(sOrg, tOrg);

			// All created ... now try to delete.
			findAndDeleteAssociation();

		} catch (JAXRException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {

		}
	}

	/**
	 * Does authentication with the uddi registry
	 */
	private void login() {
		PasswordAuthentication passwdAuth = new PasswordAuthentication(userid,
				passwd.toCharArray());
		Set creds = new HashSet();
		creds.add(passwdAuth);

		try {
			connection.setCredentials(creds);
		} catch (JAXRException e) {
			e.printStackTrace();
		}
	}

	private InternationalString getIString(String str) throws JAXRException {
		return blm.createInternationalString(str);
	}

	private void createAssociation(Organization sOrg, Organization tOrg)
			throws JAXRException {

		Concept type = bqm.findConceptByPath(associationType);
		Association association = blm.createAssociation(tOrg, type);
		sOrg.addAssociation(association);

		ArrayList associations = new ArrayList();
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
				deleteTempOrgs();
			}
		}
	}

	private void findAndDeleteAssociation() throws JAXRException {

		String sOrgID = sOrg.getKey().getId();
		String tOrgID = tOrg.getKey().getId();

		Collection findQualifiers = new ArrayList();
		findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);

		Concept type = bqm.findConceptByPath(associationType);
		ArrayList conceptTypes = new ArrayList(1);
		conceptTypes.add(type);

		BulkResponse br = bqm.findAssociations(findQualifiers, sOrgID, tOrgID,
				conceptTypes);
		Collection associations = br.getCollection();

		if (associations == null) {
			System.out.println("\n-- Matched 0 orgs");

		} else {
			System.out.println("\n-- Matched " + associations.size()
					+ " associations --\n");

			// then step through them
			for (Iterator conceptIter = associations.iterator(); conceptIter
					.hasNext();) {
				Association a = (Association) conceptIter.next();

				System.out.println("Id: " + a.getKey().getId());
				System.out.println("Name: " + a.getName().getValue());

				// Print spacer between messages
				System.out.println(" --- ");

				deleteAssociation(a.getKey());

				System.out.println("\n ============================== \n");
			}
		}
	}

	private void deleteAssociation(Key key) throws JAXRException {

		String id = key.getId();

		System.out.println("\nDeleting association with id " + id + "\n");

		Collection keys = new ArrayList();
		keys.add(key);
		BulkResponse response = blm.deleteAssociations(keys);

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

	private void createTempOrgs() throws JAXRException {

		Key orgKey = null;
		Organization sOrg = blm.createOrganization(getIString(tempSrcOrgName));
		Organization tOrg = blm.createOrganization(getIString(tempTgtOrgName));
		sOrg
				.setDescription(getIString("Temporary source organization to test saveAssociations()"));
		tOrg
				.setDescription(getIString("Temporary target organization to test saveAssociations()"));

		Collection orgs = new ArrayList();
		orgs.add(sOrg);
		orgs.add(tOrg);
		BulkResponse br = blm.saveOrganizations(orgs);

		if (br.getStatus() == JAXRResponse.STATUS_SUCCESS) {
			Iterator iter = br.getCollection().iterator();

			while (iter.hasNext()) {
				orgKey = (Key) iter.next();
				System.out.println("Temporary Organization Created with id="
						+ orgKey.getId());
			}
		} else {
			System.err.println("JAXRExceptions "
					+ "occurred during creation of temporary organization:");

			Iterator iter = br.getCollection().iterator();

			while (iter.hasNext()) {
				Exception e = (Exception) iter.next();
				System.err.println(e.toString());
			}
		}
	}

	private ArrayList findTempOrgs() throws JAXRException {

		ArrayList toReturn = new ArrayList(2);

		// Define find qualifiers and name patterns
		Collection findQualifiers = new ArrayList();
		findQualifiers.add(FindQualifier.SORT_BY_NAME_ASC);
		Collection namePatterns = new ArrayList();
		namePatterns.add("%" + tempSrcOrgName + "%");
		namePatterns.add("%" + tempTgtOrgName + "%");

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

			Collection keys = new ArrayList();
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
}
