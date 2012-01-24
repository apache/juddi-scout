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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.registry.CapabilityProfile;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;
import javax.xml.registry.UnsupportedCapabilityException;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;

/**
 * @version $Revision$ $Date$
 */
public class RegistryServiceTest {
    private RegistryService registry;

    @Before
    public void setUp() throws Exception {
        registry = new RegistryServiceImpl(null, null, -1, "3.0");
    }
    
    @Test
    public void testCapabilityProfile() throws JAXRException {
        CapabilityProfile profile = registry.getCapabilityProfile();
        assertNotNull(profile);
        assertEquals(0, profile.getCapabilityLevel());
    }

    @Test
    public void testDeclarativeQueryManager() throws JAXRException {
        try {
            registry.getDeclarativeQueryManager();
        } catch (UnsupportedCapabilityException e) {
            // ok
        }
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(RegistryServiceTest.class);
    }

   
}
