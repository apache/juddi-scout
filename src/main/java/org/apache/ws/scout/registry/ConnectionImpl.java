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
import java.util.Properties;
import java.util.Set;

import javax.xml.registry.Connection;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClerkManager;

/**
 * Apache Scout Implementation of a JAXR Connection.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 * @author Tom Cunningham <tcunning@apache.org>
 */
public class ConnectionImpl implements Connection, Serializable
{
    public static final String JUDDI_CLIENT_CONFIG_FILE         = "scout.juddi.client.config.file";
    public static final String DEFAULT_JUDDI_CLIENT_CONFIG_FILE = "META-INF/jaxr-uddi.xml";
    public static final String DEFAULT_UDDI_VERSION             = "2.0";
    
	private static final long serialVersionUID = 3542404895814764176L;
	private static Log log = LogFactory.getLog(ConnectionImpl.class);
	private boolean closed = false;
    private boolean synchronous = true;
    private Set credentials;
    private final IRegistryBase registry;
    private final String postalScheme;
    private final int maxRows;
    private String uddiVersion;
    UDDIClerkManager manager = null;

    public ConnectionImpl(Properties properties)
    {
        postalScheme = properties.getProperty(ConnectionFactoryImpl.POSTALADDRESSSCHEME_PROPERTY);
        String val = properties.getProperty(ConnectionFactoryImpl.MAXROWS_PROPERTY);
        maxRows = (val == null) ? -1 : Integer.valueOf(val);
        uddiVersion = properties.getProperty(ConnectionFactoryImpl.UDDI_VERSION_PROPERTY, DEFAULT_UDDI_VERSION);
     
        String uddiConfigFile      = properties.getProperty(JUDDI_CLIENT_CONFIG_FILE);// DEFAULT_JUDDI_CLIENT_CONFIG_FILE);
        if (isUDDIv3(uddiVersion)) {
            String nodeName = null;
            String managerName = null;
            if (manager==null && uddiConfigFile!=null) {
                try {
                    manager = new UDDIClerkManager(uddiConfigFile, properties);
                    manager.start();
                } catch (ConfigurationException e) {
                    log.error(e.getMessage(),e);
                }
            }
            if (manager !=null) {
                try {
                    managerName = manager.getName();
                    nodeName = manager.getClientConfig().getHomeNode().getName();
                } catch (ConfigurationException e) {
                    log.error(e.getMessage(),e);
                }
            }
            registry = new RegistryV3Impl(properties, nodeName, managerName);
        } else {
            registry = new RegistryImpl(properties);           	
        }

        //this.postalScheme = postalScheme;
        //this.maxRows = maxRows;

    }
    
    private boolean isUDDIv3(String version) {
        if (version.startsWith("3")) return true;
        return false;
    }

    public RegistryService getRegistryService() throws JAXRException
    {
        RegistryServiceImpl reg = new RegistryServiceImpl(registry, postalScheme, maxRows, uddiVersion);
        reg.setConnection(this);
        return reg;
    }

    public void close()
    {
        closed = true;
    }

    public boolean isClosed()
    {
        return closed;
    }

    public Set getCredentials()
    {
        return credentials;
    }

    public void setCredentials(Set credentials)
    {
        this.credentials = credentials;
    }

    public boolean isSynchronous()
    {
        return synchronous;
    }

    public void setSynchronous(boolean synchronous)
    {
        this.synchronous = synchronous;
    }
}
