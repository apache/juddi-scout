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

import javax.xml.registry.JAXRException;
import javax.xml.registry.UnsupportedCapabilityException;
import javax.xml.registry.infomodel.TelephoneNumber;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class TelephoneNumberImpl implements TelephoneNumber {
    private String number;
    private String type;

    public TelephoneNumberImpl() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAreaCode() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void setAreaCode(String areaCode) throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public String getCountryCode() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void setCountryCode(String countryCode) throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public String getExtension() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void setExtension(String extension) throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public String getUrl() throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public void setUrl(String url) throws JAXRException {
        throw new UnsupportedCapabilityException("Level 1 feature");
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TelephoneNumberImpl)) return false;

        final TelephoneNumberImpl telephoneNumber = (TelephoneNumberImpl) o;

        if (number != null ? !number.equals(telephoneNumber.number) : telephoneNumber.number != null) return false;
        if (type != null ? !type.equals(telephoneNumber.type) : telephoneNumber.type != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (number != null ? number.hashCode() : 0);
        result = 29 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public String toString() {
        return number == null ? "null" : number;
    }
}
