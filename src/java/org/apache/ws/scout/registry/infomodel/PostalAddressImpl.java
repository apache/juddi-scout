/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ws.scout.registry.infomodel;

import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.PostalAddress;

/**
 * Implements PostalAddress Interface
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class PostalAddressImpl extends ExtensibleObjectImpl implements PostalAddress
{
    private static final String EMPTY_STRING = "";
    private String street = EMPTY_STRING;
    private String streetNumber = EMPTY_STRING;
    private String city = EMPTY_STRING;
    private String stateOrProvince = EMPTY_STRING;
    private String postalCode = EMPTY_STRING;
    private String country = EMPTY_STRING;
    private String type = EMPTY_STRING;
    private ClassificationScheme postalScheme;

    /**
     * Creates a new instance of PostalAddressImpl
     */
    public PostalAddressImpl(ClassificationScheme postalScheme)
    {
        this.postalScheme = postalScheme;
    }

    public PostalAddressImpl()
    {
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }

    public ClassificationScheme getPostalScheme()
    {
        return postalScheme;
    }

    public void setPostalScheme(ClassificationScheme postalScheme)
    {
        this.postalScheme = postalScheme;
    }

    public String getStateOrProvince()
    {
        return stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince)
    {
        this.stateOrProvince = stateOrProvince;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getStreetNumber()
    {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber)
    {
        this.streetNumber = streetNumber;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
