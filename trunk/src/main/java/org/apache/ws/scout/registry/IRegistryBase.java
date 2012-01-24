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

import java.net.URI;

import org.apache.ws.scout.transport.Transport;
import org.apache.ws.scout.transport.TransportException;

/**
 * 
 * IRegistryBase interface.
 * 
 * <p>Provides a common interface to IRegistry and IRegistryV3 and any
 * subsequent directory implementation.</p>
 *  
 * <i>Borrowed from jUDDI.</i>
 * 
 *
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */

public interface IRegistryBase {

	String execute(String uddiRequest, String urltype) throws TransportException;
	
	/**
	 * @return Returns the inquiryURL.
	 */
	URI getInquiryURI();
	
	/**
	 * @param uri The inquiry uri to set.
	 */
	void setInquiryURI(URI uri);
	
	/**
	 * @return Returns the publishURL.
	 */
	URI getPublishURI();	
	
	/**
	 * @param uri The publish uri to set.
	 */
	void setPublishURI(URI uri);
	
	/**
	 * @return Returns the publishURL.
	 */
	URI getSecurityURI();	
	
	/**
	 * @param uri The publish uri to set.
	 */
	void setSecurityURI(URI uri);

	
	/**
	 * @return Returns the transport.
	 */
	Transport getTransport();
	
	/**
	 * @param transport The transport to set.
	 */
	void setTransport(Transport transport);

}
