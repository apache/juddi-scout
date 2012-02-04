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

import java.io.Serializable;
import java.util.Collection;
import java.util.Properties;

import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.FederatedConnection;
import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.JAXRException;
import javax.xml.registry.UnsupportedCapabilityException;

/**
 * Our implmentation of javax.xml.registry.ConnectionFactory.
 * Also exposes the properties as JavaBean properties to ease use
 * with a managed environment such as an application server.
 *
 * @author Anil Saldhana  <anil@apache.org>
 * @author Jeremy Boynes  <jboynes@apache.org>
 * @author Tom Cunningham <tcunning@apache.org>
 */
public class ConnectionFactoryImpl extends ConnectionFactory implements Serializable
{
	private static final long serialVersionUID = -6902106826496922256L;
	public static final String QUERYMANAGER_PROPERTY         = "javax.xml.registry.queryManagerURL";
	public static final String LIFECYCLEMANAGER_PROPERTY     = "javax.xml.registry.lifeCycleManagerURL";
	public static final String SECURITYMANAGER_PROPERTY      = "javax.xml.registry.securityManagerURL";
	public static final String SEMANTICEQUIVALENCES_PROPERTY = "javax.xml.registry.semanticEquivalences";
	public static final String POSTALADDRESSSCHEME_PROPERTY  = "javax.xml.registry.postalAddressScheme";
	public static final String AUTHENTICATIONMETHOD_PROPERTY = "javax.xml.registry.security.authenticationMethod";
	public static final String MAXROWS_PROPERTY              = "javax.xml.registry.uddi.maxRows";
	
	public static final String ADMIN_ENDPOINT_PROPERTY       = "scout.proxy.adminURL";
	public static final String TRANSPORT_CLASS_PROPERTY      = "scout.proxy.transportClass";
	public static final String SECURITY_PROVIDER_PROPERTY    = "scout.proxy.securityProvider";
	public static final String PROTOCOL_HANDLER_PROPERTY     = "scout.proxy.protocolHandler";
	public static final String UDDI_VERSION_PROPERTY         = "scout.proxy.uddiVersion";
	public static final String UDDI_NAMESPACE_PROPERTY       = "scout.proxy.uddiNamespace";
	
	private Properties properties = new Properties();

    /**
     * Public no-arg constructor so that this ConnectionFactory can be
     * instantiated by the JAXR ConnectionFactory;
     */
    public ConnectionFactoryImpl() {}

    public Connection createConnection() throws JAXRException
    {
        //The JAXR spec requires the queryManagerURL to be defined
        String queryManagerURL = properties.getProperty(QUERYMANAGER_PROPERTY);
        if (queryManagerURL==null) throw new InvalidRequestException("Missing required property " + QUERYMANAGER_PROPERTY);
        return new ConnectionImpl(properties);
    }

    public FederatedConnection createFederatedConnection(Collection collection) throws JAXRException
    {
        throw new UnsupportedCapabilityException("FederatedConnections are not supported in this release");
    }

    /**
     * Returns a value copy of the properties that will be used to create
     * a Connections. Operations on this Properties objects will not affect
     * this ConnectionFactory; use setProperties(Properties) to save changes.
     *
     * @return a Properties object containing the properies that will be used to create Connection
     */
    public Properties getProperties()
    {
        return properties;
    }

    /**
     * Update the properties used by this ConnectionFactory to obtain a connection.
     *
     * @param properties the new properties for this ConnectionFactory
     */
    public void setProperties(Properties properties)
    {
        if (properties==null) properties = new Properties();
        this.properties.putAll(properties);
    }

    public static ConnectionFactory newInstance()
    {
        return new ConnectionFactoryImpl();
    }

}
