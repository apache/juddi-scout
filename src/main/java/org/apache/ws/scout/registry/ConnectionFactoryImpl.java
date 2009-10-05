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
	private static final String QUERYMANAGER_PROPERTY = "javax.xml.registry.queryManagerURL";
    private static final String LIFECYCLEMANAGER_PROPERTY = "javax.xml.registry.lifeCycleManagerURL";
    private static final String SECURITYMANAGER_PROPERTY = "javax.xml.registry.securityManagerURL";
    private static final String SEMANTICEQUIVALENCES_PROPERTY = "javax.xml.registry.semanticEquivalences";
    private static final String POSTALADDRESSSCHEME_PROPERTY = "javax.xml.registry.postalAddressScheme";
    private static final String AUTHENTICATIONMETHOD_PROPERTY = "javax.xml.registry.security.authenticationMethod";
    private static final String MAXROWS_PROPERTY = "javax.xml.registry.uddi.maxRows";

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
                               uddiNamespace, uddiVersion);
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
        Properties props = new Properties();
        if (queryManagerURL != null)
        {
            props.put(QUERYMANAGER_PROPERTY, queryManagerURL);
        }
        if (lifeCycleManagerURL != null)
        {
            props.put(LIFECYCLEMANAGER_PROPERTY, lifeCycleManagerURL);
        }
        if (securityManagerURL != null)
        {
        	props.put(SECURITYMANAGER_PROPERTY, securityManagerURL);
        }
        if (semanticEquivalences != null)
        {
            props.put(SEMANTICEQUIVALENCES_PROPERTY, semanticEquivalences);
        }
        if (postalAddressScheme != null)
        {
            props.put(POSTALADDRESSSCHEME_PROPERTY, postalAddressScheme);
        }
        if (authenticationMethod != null)
        {
            props.put(AUTHENTICATIONMETHOD_PROPERTY, authenticationMethod);
        }
        if (maxRows != null)
        {
            props.put(MAXROWS_PROPERTY, maxRows.toString());
        }
        if (uddiNamespace != null) {
        	props.put(RegistryImpl.UDDI_NAMESPACE_PROPERTY_NAME, uddiNamespace);
        }
        if (uddiVersion != null) {
        	props.put(RegistryImpl.UDDI_VERSION_PROPERTY_NAME, uddiVersion);
        }

        return props;
    }

    /**
     * Update the properties used by this ConnectionFactory to obtain a connection.
     *
     * @param properties the new properties for this ConnectionFactory
     */
    public void setProperties(Properties properties)
    {
        queryManagerURL = properties.getProperty(QUERYMANAGER_PROPERTY);
        lifeCycleManagerURL = properties.getProperty(LIFECYCLEMANAGER_PROPERTY);
        securityManagerURL = properties.getProperty(SECURITYMANAGER_PROPERTY);

        transportClass = properties.getProperty(RegistryImpl.TRANSPORT_CLASS_PROPERTY_NAME);
        semanticEquivalences = properties.getProperty(SEMANTICEQUIVALENCES_PROPERTY);
        authenticationMethod = properties.getProperty(AUTHENTICATIONMETHOD_PROPERTY);
        postalAddressScheme = properties.getProperty(POSTALADDRESSSCHEME_PROPERTY);
        uddiVersion = properties.getProperty(RegistryImpl.UDDI_VERSION_PROPERTY_NAME);
        uddiNamespace = properties.getProperty(RegistryImpl.UDDI_NAMESPACE_PROPERTY_NAME);

        String val = properties.getProperty(MAXROWS_PROPERTY);
        maxRows = (val == null) ? null : Integer.valueOf(val);
    }

    public static ConnectionFactory newInstance()
    {
        return new ConnectionFactoryImpl();
    }

    public String getAuthenticationMethod()
    {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(String authenticationMethod)
    {
        this.authenticationMethod = authenticationMethod;
    }

    public String getLifeCycleManagerURL()
    {
        return lifeCycleManagerURL;
    }

    public void setLifeCycleManagerURL(String lifeCycleManagerURL)
    {
        this.lifeCycleManagerURL = lifeCycleManagerURL;
    }

    public Integer getMaxRows()
    {
        return maxRows;
    }

    public void setMaxRows(Integer maxRows)
    {
        this.maxRows = maxRows;
    }

    public String getPostalAddressScheme()
    {
        return postalAddressScheme;
    }

    public void setPostalAddressScheme(String postalAddressScheme)
    {
        this.postalAddressScheme = postalAddressScheme;
    }

    public String getQueryManagerURL()
    {
        return queryManagerURL;
    }

    public void setQueryManagerURL(String queryManagerURL)
    {
        this.queryManagerURL = queryManagerURL;
    }

    public String getSemanticEquivalences()
    {
        return semanticEquivalences;
    }

    public void setSemanticEquivalences(String semanticEquivalences)
    {
        this.semanticEquivalences = semanticEquivalences;
    }

	public String getTransportClass() {
		return transportClass;
	}

	public void setTransportClass(String transportClass) {
		this.transportClass = transportClass;
	}
}
