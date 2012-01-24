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
import java.net.URI;
import java.net.URISyntaxException;
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
	
	public static final String JUDDI_CLIENT_CONFIG_FILE      = "scout.juddi.client.config.file";
	
	public static final String DEFAULT_JUDDI_CLIENT_CONFIG_FILE = "META-INF/jaxr-uddi.xml";

    private String queryManagerURL;
    private String lifeCycleManagerURL;
    private String securityManagerURL;
    private String transportClass;
    private String semanticEquivalences;
    private String authenticationMethod;
    private Integer maxRows;
    private String postalAddressScheme;
	private String uddiNamespace;
	private String uddiVersion;
	
	private Properties properties = new Properties();
	
	private String uddiConfigFile;

    /**
     * Public no-arg constructor so that this ConnectionFactory can be
     * instantiated by the JAXR ConnectionFactory;
     */
    public ConnectionFactoryImpl()
    {
    }

    public Connection createConnection() throws JAXRException
    {
        if (queryManagerURL == null)
        {
            throw new InvalidRequestException("queryManager is not set");
        }
        URI queryManager;
        URI lifeCycleManager;
        URI securityManager = null;
        try
        {
            queryManager = new URI(queryManagerURL);
        } catch (URISyntaxException e)
        {
            throw new InvalidRequestException("Invalid queryManagerURL: " + queryManagerURL, e);
        }
        try
        {
            lifeCycleManager = lifeCycleManagerURL == null ? queryManager : new URI(lifeCycleManagerURL);
        } catch (URISyntaxException e)
        {
            throw new InvalidRequestException("Invalid lifeCycleManagerURL: " + lifeCycleManagerURL, e);
        }
        try
        {      
        	if (securityManagerURL != null) {
        		securityManager = new URI(securityManagerURL);
        	}
        } catch (URISyntaxException e) {
        	securityManager = null;
        }
        	return new ConnectionImpl(queryManager, lifeCycleManager, securityManager, transportClass, null, maxRows == null ? -1 : maxRows.intValue(),
                               uddiNamespace, uddiVersion, uddiConfigFile, properties);
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
        this.properties = properties;
        if (isUDDIv3(properties)) {
            // UDDI v3 uses the juddi client
            queryManagerURL     = "org.apache.juddi.v3.client.transport.wrapper.UDDIInquiryService#inquire";
            lifeCycleManagerURL = "org.apache.juddi.v3.client.transport.wrapper.UDDIPublicationService#publish";
            securityManagerURL  = "org.apache.juddi.v3.client.transport.wrapper.UDDISecurityService#secure";
            uddiConfigFile      = properties.getProperty(JUDDI_CLIENT_CONFIG_FILE);// DEFAULT_JUDDI_CLIENT_CONFIG_FILE);
        } else {
            queryManagerURL = properties.getProperty(QUERYMANAGER_PROPERTY);
            lifeCycleManagerURL = properties.getProperty(LIFECYCLEMANAGER_PROPERTY);
            securityManagerURL = properties.getProperty(SECURITYMANAGER_PROPERTY);
        }

        transportClass = properties.getProperty(RegistryImpl.TRANSPORT_CLASS_PROPERTY_NAME);
        semanticEquivalences = properties.getProperty(SEMANTICEQUIVALENCES_PROPERTY);
        authenticationMethod = properties.getProperty(AUTHENTICATIONMETHOD_PROPERTY);
        postalAddressScheme = properties.getProperty(POSTALADDRESSSCHEME_PROPERTY);
        uddiVersion = properties.getProperty(RegistryImpl.UDDI_VERSION_PROPERTY_NAME);
        uddiNamespace = properties.getProperty(RegistryImpl.UDDI_NAMESPACE_PROPERTY_NAME);

        String val = properties.getProperty(MAXROWS_PROPERTY);
        maxRows = (val == null) ? null : Integer.valueOf(val);
    }
    
    private boolean isUDDIv3(Properties properties) {
        if (properties.containsKey(RegistryImpl.UDDI_VERSION_PROPERTY_NAME)) {
            String version = properties.getProperty(RegistryImpl.UDDI_VERSION_PROPERTY_NAME);
            if (version.equals("3") || version.equals("3.0")) return true;
        }
        return false;
    }

    public static ConnectionFactory newInstance()
    {
        return new ConnectionFactoryImpl();
    }

    public String getAuthenticationMethod()
    {
        return authenticationMethod;
    }

    public String getLifeCycleManagerURL()
    {
        return lifeCycleManagerURL;
    }

    public Integer getMaxRows()
    {
        return maxRows;
    }

    public String getPostalAddressScheme()
    {
        return postalAddressScheme;
    }

    public String getQueryManagerURL()
    {
        return queryManagerURL;
    }

    public String getSemanticEquivalences()
    {
        return semanticEquivalences;
    }

	public String getTransportClass() {
		return transportClass;
	}
}
