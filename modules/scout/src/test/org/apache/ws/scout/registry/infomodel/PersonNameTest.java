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
package org.apache.ws.scout.registry.infomodel;

import javax.xml.registry.infomodel.PersonName;
import javax.xml.registry.JAXRException;
import javax.xml.registry.UnsupportedCapabilityException;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class PersonNameTest extends TestCase {
    private PersonName name;

    public void testLevel1Methods() throws JAXRException {
        try {
            name.getFirstName();
            fail("Expected UnsupportedCapabilityException");
        } catch (UnsupportedCapabilityException e) {
            // OK
        }
        try {
            name.getMiddleName();
            fail("Expected UnsupportedCapabilityException");
        } catch (UnsupportedCapabilityException e) {
            // OK
        }
        try {
            name.getLastName();
            fail("Expected UnsupportedCapabilityException");
        } catch (UnsupportedCapabilityException e) {
            // OK
        }
        try {
            name.setFirstName(null);
            fail("Expected UnsupportedCapabilityException");
        } catch (UnsupportedCapabilityException e) {
            // OK
        }
        try {
            name.setMiddleName(null);
            fail("Expected UnsupportedCapabilityException");
        } catch (UnsupportedCapabilityException e) {
            // OK
        }
        try {
            name.setLastName(null);
            fail("Expected UnsupportedCapabilityException");
        } catch (UnsupportedCapabilityException e) {
            // OK
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        name = new PersonNameImpl();
    }
}
