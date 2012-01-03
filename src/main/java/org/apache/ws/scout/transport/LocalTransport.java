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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.scout.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Local Message transport class.
 * 
 * <p>This transport calls jUDDI directly.</p>
 * 
 * @author Kurt Stam (kurt.stam@redhat.com)
 */
public class LocalTransport implements Transport
{
  private static Log log = LogFactory.getLog(LocalTransport.class);

  /** 
   * Sends an element and returns an element.
   */
  public Element send(Element request,URI endpointURI)
    throws TransportException
  {    
    Element response = null;

    if (log.isDebugEnabled()) {
    	log.debug("\nRequest message:\n" + XMLUtils.convertNodeToXMLString(request));
    	log.debug("Calling " + endpointURI + " locally");
    }
    try {
    	String className = endpointURI.getPath();
    	String methodName = endpointURI.getFragment();
    	log.debug("Calling class=" + className);
    	log.debug("Method=" + methodName);
    	Class<?> c = Class.forName(className);
    	Object requestHandler = c.newInstance();
    	Method method = c.getMethod(methodName, Element.class);
    	Node node = (Node) method.invoke(requestHandler, request);
    	if (node!=null && node.getFirstChild()!=null) {
    		response = (Element) node.getFirstChild();
    	}
    } catch (Exception ex) {
      throw new TransportException(ex);
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
    throws TransportException
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
    	throw new TransportException(ex);
    }
    log.debug("\nResponse message:\n" + response);
    return response;
  }
  
  
}
