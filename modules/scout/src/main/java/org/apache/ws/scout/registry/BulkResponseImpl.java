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

package org.apache.ws.scout.registry;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Implements JAXR BulkResponse Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author <a href="mailto:anil@apache.org">Anil Saldhana</a>
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 */
public class BulkResponseImpl extends JAXRResponseImpl implements BulkResponse
{
    private boolean partialResponse = false;

    private Collection exceptions = new ArrayList();
    private HashSet collection = new HashSet();
    /**
     * Creates a new instance of BulkResponseImpl
     */
    public BulkResponseImpl()
    {
    }

    BulkResponseImpl(HashSet collection)
    {
        this.collection = collection;
    }

    /**
     * Get Collection of RegistryObjects *
     */
    public Collection getCollection() throws JAXRException
    {
        return this.collection;
    }

    /**
     * The javadoc is unclear.  it says for getExceptions() :
     *   "Get the Collection of RegistryException instances in case of partial
     *     commit. Caller thread will block here if result is not yet available.
     *     Return null if result is available and there is no RegistryException(s)."
     * Yet the return javadoc says :
     *   "Collection of RegistryException instances. The Collection may be empty but not null."
     *
     * So my interpretation is return null if result avail, and empty collection otherwise
     *
     * @return
     * @throws JAXRException
     */
    public Collection getExceptions() throws JAXRException
    {
        return (this.exceptions.size() == 0 ? null : exceptions);
    }

    public boolean isPartialResponse() throws JAXRException
    {
        if (exceptions.size() > 0) {
            this.partialResponse = true;
        }
        return this.partialResponse;
    }

    public void setPartialResponse(boolean b) throws JAXRException
    {
        this.partialResponse = b;
    }

    public void setCollection(HashSet coll) throws JAXRException
    {
        this.collection = coll;
    }

    /**
     * Setter for property exceptions.
     *
     * @param exceptions New value of property exceptions.
     */
    public void setExceptions(Collection exceptions)
    {
        this.exceptions = exceptions;
    }

}
