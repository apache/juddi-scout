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

import javax.xml.registry.infomodel.Key;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class KeyImpl implements Key {
    private String id;

    public KeyImpl() {
    }

    public KeyImpl(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyImpl)) return false;
        final KeyImpl key = (KeyImpl) o;
        if (id != null ? !id.equals(key.id) : key.id != null) return false;
        return true;
    }

    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    public String toString() {
        return id;
    }
}
