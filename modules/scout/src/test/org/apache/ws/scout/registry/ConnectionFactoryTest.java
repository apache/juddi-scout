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
package org.apache.ws.scout.registry;

import java.util.Properties;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.JAXRException;
import javax.xml.registry.Connection;
import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.UnsupportedCapabilityException;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class ConnectionFactoryTest extends TestCase {
    private ConnectionFactoryImpl factory;

    public void testNewInstanceWithDefault() throws JAXRException {
        Properties props = System.getProperties();
        props.remove("javax.xml.registry.ConnectionFactoryClass");
        ConnectionFactory factory = ConnectionFactory.newInstance();
        assertEquals(ConnectionFactoryImpl.class, factory.getClass());
    }

    public void testNewInstanceWithProperty() throws JAXRException {
        System.setProperty("javax.xml.registry.ConnectionFactoryClass", ConnectionFactoryImpl.class.getName());
        ConnectionFactory factory = ConnectionFactory.newInstance();
        assertEquals(ConnectionFactoryImpl.class, factory.getClass());
    }

    public void testSetQueryManagerURL() {
        String url = "http://localhost";
        factory.setQueryManagerURL(url);
        assertEquals(url, factory.getQueryManagerURL());
        assertEquals(url, factory.getProperties().getProperty("javax.xml.registry.queryManagerURL"));
    }

    public void testSetLifeCycleURL() {
        String url = "http://localhost";
        factory.setLifeCycleManagerURL(url);
        assertEquals(url, factory.getLifeCycleManagerURL());
        assertEquals(url, factory.getProperties().getProperty("javax.xml.registry.lifeCycleManagerURL"));
    }

    public void testSetSemanticEquivalences() {
        String urns =
                "urn:uuid:0a1324f7-6d4a-4d73-a088-9ab1d00c9a91,urn:uuid:23a5feac-26b9-4525-82fc-997885a0e6a2" + '|' +
                "urn:uuid:1acf6ed2-cd6e-4797-aad8-8937a3cff88b,urn:uuid:152d6f28-cb56-4e5d-9f55-96b132def0e4";
        factory.setSemanticEquivalences(urns);
        assertEquals(urns, factory.getSemanticEquivalences());
        assertEquals(urns, factory.getProperties().getProperty("javax.xml.registry.semanticEquivalences"));
    }

    public void testSetAuthenticationMethod() {
        String method = "HTTP_BASIC";
        factory.setAuthenticationMethod(method);
        assertEquals(method, factory.getAuthenticationMethod());
        assertEquals(method, factory.getProperties().getProperty("javax.xml.registry.security.authenticationMethod"));
    }

    public void testSetPostalAddressScheme() {
        String scheme = "User Defined";
        factory.setPostalAddressScheme(scheme);
        assertEquals(scheme, factory.getPostalAddressScheme());
        assertEquals(scheme, factory.getProperties().getProperty("javax.xml.registry.postalAddressScheme"));
    }

    public void testMaxRows() {
        Integer maxRows = new Integer(1234);
        factory.setMaxRows(maxRows);
        assertEquals(maxRows, factory.getMaxRows());
        assertEquals(maxRows.toString(), factory.getProperties().getProperty("javax.xml.registry.uddi.maxRows"));
    }

    public void testNullQueryManagerURL() {
        factory.setQueryManagerURL(null);
        assertFalse(factory.getProperties().containsKey("javax.xml.registry.queryManagerURL"));
    }

    public void testNullLifeCycleManagerURL() {
        factory.setLifeCycleManagerURL(null);
        assertFalse(factory.getProperties().containsKey("javax.xml.registry.lifeCycleManagerURL"));
    }

    public void testNullSemanticEquivalences() {
        factory.setSemanticEquivalences(null);
        assertFalse(factory.getProperties().containsKey("javax.xml.registry.semanticEquivalences"));
    }

    public void testNullAuthenticationMethod() {
        factory.setAuthenticationMethod(null);
        assertFalse(factory.getProperties().containsKey("javax.xml.registry.security.authenticationMethod"));
    }

    public void testNullMaxRows() {
        factory.setMaxRows(null);
        assertFalse(factory.getProperties().containsKey("javax.xml.registry.uddi.maxRows"));
    }

    public void testNullPostalAddressScheme() {
        factory.setPostalAddressScheme(null);
        assertFalse(factory.getProperties().containsKey("javax.xml.registry.postalAddressScheme"));
    }

    public void testCreateConnection() throws JAXRException {
        factory.setQueryManagerURL("http://localhost");
        factory.setLifeCycleManagerURL("http://localhost");
        Connection c = factory.createConnection();
        try {
            assertEquals(ConnectionImpl.class, c.getClass());
        } finally{
            c.close();
        }
    }

    public void testCreateConnectionWithNullLifeCycleURL() throws JAXRException {
        factory.setQueryManagerURL("http://localhost");
        factory.setLifeCycleManagerURL(null);
        Connection c = factory.createConnection();
        try {
            assertEquals(ConnectionImpl.class, c.getClass());
        } finally{
            c.close();
        }
    }

    public void testCreateConnectionWithInvalidQueryURL() {
        factory.setQueryManagerURL("foo:bar");
        try {
            factory.createConnection();
            fail("did not reject invalid URL");
        } catch (InvalidRequestException e) {
            // OK
        } catch (JAXRException e) {
            fail("threw JAXRException");
        }
    }

    public void testCreateConnectionWithInvalidLifeCycleURL() {
        factory.setQueryManagerURL("http://localhost");
        factory.setLifeCycleManagerURL("foo:bar");
        try {
            factory.createConnection();
            fail("did not reject invalid URL");
        } catch (InvalidRequestException e) {
            // OK
        } catch (JAXRException e) {
            fail("threw JAXRException");
        }
    }

    public void testCreateConnectionWithNullQueryURL() {
        try {
            factory.createConnection();
            fail("did not reject invalid URL");
        } catch (InvalidRequestException e) {
            // OK
        } catch (JAXRException e) {
            fail("threw JAXRException");
        }
    }

    public void testCreateFederatedConnection() {
        try {
            factory.createFederatedConnection(null);
            fail("did not get expected Exception");
        } catch (UnsupportedCapabilityException e) {
            // OK
        } catch (JAXRException e) {
            fail("threw JAXRException");
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new ConnectionFactoryImpl();
    }

    protected void tearDown() throws Exception {
        factory = null;
        super.tearDown();
    }
}
