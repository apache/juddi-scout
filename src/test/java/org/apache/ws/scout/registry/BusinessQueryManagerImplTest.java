package org.apache.ws.scout.registry;

import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.ClassificationScheme;

import junit.framework.TestCase;

/**
 *  Tests the BusinessLifecycleManagerImpl class
 */
public class BusinessQueryManagerImplTest extends TestCase {

    public void testFindClassificationSchemeByName() throws JAXRException {

        BusinessQueryManager blm = new BusinessQueryManagerImpl(new RegistryServiceImpl(null, null, -1));

        ClassificationScheme scheme = blm.findClassificationSchemeByName(null, "AssociationType");
        assertNotNull(scheme);
        assertTrue(scheme.getChildConceptCount() == 15);

        scheme = blm.findClassificationSchemeByName(null, "ObjectType");
        assertNotNull(scheme);
        assertTrue(scheme.getChildConceptCount() == 16);

        scheme = blm.findClassificationSchemeByName(null, "ObjectType");
        assertNotNull(scheme);
        assertTrue(scheme.getChildConceptCount() == 16);

        scheme = blm.findClassificationSchemeByName(null, "PhoneType");
        assertNotNull(scheme);
        assertTrue(scheme.getChildConceptCount() == 5);

        scheme = blm.findClassificationSchemeByName(null, "URLType");
        assertNotNull(scheme);
        assertTrue(scheme.getChildConceptCount() == 6);

        scheme = blm.findClassificationSchemeByName(null, "PostalAddressAttributes");
        assertNotNull(scheme);
        assertTrue(scheme.getChildConceptCount() == 6);

    }

}
