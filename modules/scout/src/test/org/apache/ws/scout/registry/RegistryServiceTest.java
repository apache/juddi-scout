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

import junit.framework.TestCase;

import javax.xml.registry.CapabilityProfile;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;
import javax.xml.registry.UnsupportedCapabilityException;

/**
 * @version $Revision$ $Date$
 */
public class RegistryServiceTest extends TestCase {
    private RegistryService registry;

    public void testCapabilityProfile() throws JAXRException {
        CapabilityProfile profile = registry.getCapabilityProfile();
        assertNotNull(profile);
        assertEquals(0, profile.getCapabilityLevel());
    }

    public void testDeclarativeQueryManager() throws JAXRException {
        try {
            registry.getDeclarativeQueryManager();
        } catch (UnsupportedCapabilityException e) {
            // ok
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = new RegistryServiceImpl(null, null, -1);
    }
}
