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

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.EmailAddress;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class EmailAddressTest extends TestCase {
    private EmailAddress address;

    public void testNullAddress() throws JAXRException {
        assertNull(address.getAddress());
        assertNull(address.getType());
    }

    public void testSetAddress() throws JAXRException {
        address.setAddress("foo@example.com");
        assertEquals("foo@example.com", address.getAddress());
    }

    public void testSetType() throws JAXRException {
        address.setType("Internet");
        assertEquals("Internet", address.getType());
    }

    protected void setUp() throws Exception {
        super.setUp();
        address = new EmailAddressImpl();
    }
}
