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
package javax.xml.registry;

import java.util.Properties;
import java.util.Collection;

/**
 * @version $Revision$ $Date$
 */
public abstract class ConnectionFactory {
    public ConnectionFactory() {
    }

    public abstract Connection createConnection() throws JAXRException;

    public abstract FederatedConnection createFederatedConnection(Collection connections) throws JAXRException;

    public abstract Properties getProperties() throws JAXRException;

    public abstract void setProperties(Properties properties) throws JAXRException;

    public static ConnectionFactory newInstance() throws JAXRException {
        String className = System.getProperty("javax.xml.registry.ConnectionFactoryClass", "org.apache.ws.scout.registry.ConnectionFactoryImpl");
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ConnectionFactory.class.getClassLoader();
        }
        try {
            Class factoryClass = cl.loadClass(className);
            return (ConnectionFactory) factoryClass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new JAXRException("Unable to load JAXR ConnectionFactoryClass: " + className, e);
        } catch (InstantiationException e) {
            throw new JAXRException("Unable to instantiate JAXR ConnectionFactoryClass: " + className, e);
        } catch (IllegalAccessException e) {
            throw new JAXRException("Unable to instantiate JAXR ConnectionFactoryClass: " + className, e);
        }
    }
}
