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
package org.apache.ws.scout.transport;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.scout.registry.RegistryException;
import org.apache.ws.scout.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * RMI Message transport class.
 * 
 * <p>This transpor calls jUDDI using RMI.</p>
 * 
 * @author Kurt Stam (kurt.stam@redhat.com)
 */
public class RMITransport implements Transport
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(RMITransport.class);

  /** 
   * Sends an element and returns an element.
   */
  public Element send(Element request,URI endpointURI)
    throws RegistryException
  {    
    Element response = null;

    if (log.isDebugEnabled()) {
    	log.debug("\nRequest message:\n" + XMLUtils.convertNodeToXMLString(request));
    	log.debug("Calling " + endpointURI + " using rmi");
    }
    
    try {
		String host       = endpointURI.getHost();
		int port          = endpointURI.getPort();
		String scheme     = endpointURI.getScheme();
    	String service    = endpointURI.getPath();
    	String className  = endpointURI.getQuery();
    	String methodName = endpointURI.getFragment();
    	Properties env    = new Properties();
    	//It be a lot nicer if this is configured through properties, but for now
    	//I'd like to keep the changes localized, so this seems pretty reasonable.
    	String factoryInitial = SecurityActions.getProperty("java.naming.factory.initial");
        if (factoryInitial==null) factoryInitial = "org.jnp.interfaces.NamingContextFactory";
        String factoryURLPkgs = SecurityActions.getProperty("java.naming.factory.url.pkgs");
        if (factoryURLPkgs==null) factoryURLPkgs = "org.jboss.naming";
        env.setProperty("java.naming.factory.initial", factoryInitial);
        env.setProperty("java.naming.factory.url.pkgs", factoryURLPkgs);
    	env.setProperty("java.naming.provider.url", scheme + "://" + host + ":" + port);
    	log.debug("Initial Context using env=" + env.toString());
    	InitialContext context = new InitialContext(env);
    	log.debug("Calling service=" + service + ", Class = " + className + ", Method=" + methodName);
    	//Looking up the object (i.e. Publish)
    	Object requestHandler = context.lookup(service);
    	//Loading up the stub
    	Class<?> c = Class.forName(className);
    	//Getting a handle to method we want to call (i.e. publish.publish(Element element))
    	Method method = c.getMethod(methodName, Element.class);
    	//Calling that method
    	Node node = (Node) method.invoke(requestHandler, request);
    	//The result is in the first element
    	response = (Element) node.getFirstChild();
    }
    catch (Exception ex) {
      throw new RegistryException(ex);
    }
    if (log.isDebugEnabled()) {
    	log.debug("\nResponse message:\n" + XMLUtils.convertNodeToXMLString(response));
    }
    return response;
  }
  
  /**
   * Sends an XML, responds with an XML.
   */
  public String send(String request,URI endpointURI)
    throws RegistryException
  {    
    String response = null;
    log.debug("\nRequest message:\n" + request);
    try {
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = factory.newDocumentBuilder();
        Document document = parser.parse(request);
        Element element = document.getDocumentElement();
        response= XMLUtils.convertNodeToXMLString(send(element, endpointURI));
    } catch (Exception ex) { 
    	throw new RegistryException(ex);
    }
    log.debug("\nResponse message:\n" + response);
    return response;
  }
  
  
}
