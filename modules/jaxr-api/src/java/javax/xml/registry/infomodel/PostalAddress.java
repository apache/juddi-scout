/**
 *
 * Copyright 2003-2004 The Apache Software Foundation
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

//
// This source code implements specifications defined by the Java
// Community Process. In order to remain compliant with the specification
// DO NOT add / change / or delete method signatures!
//
package javax.xml.registry.infomodel;

import javax.xml.registry.JAXRException;

/**
 * @version $Revision$ $Date$
 */
public interface PostalAddress extends ExtensibleObject {
    String getCity() throws JAXRException;

    String getCountry() throws JAXRException;

    String getPostalCode() throws JAXRException;

    ClassificationScheme getPostalScheme() throws JAXRException;

    String getStateOrProvince() throws JAXRException;

    String getStreet() throws JAXRException;

    String getStreetNumber() throws JAXRException;

    String getType() throws JAXRException;

    void setCity(String city) throws JAXRException;

    void setCountry(String country) throws JAXRException;

    void setPostalCode(String postalCode) throws JAXRException;

    void setPostalScheme(ClassificationScheme postalScheme) throws JAXRException;

    void setStateOrProvince(String state) throws JAXRException;

    void setStreet(String street) throws JAXRException;

    void setStreetNumber(String streetNumber) throws JAXRException;

    void setType(String type) throws JAXRException;
}
