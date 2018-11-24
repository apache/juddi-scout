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
import org.uddi.api_v3.*;
import org.apache.ws.scout.transport.Transport;

/**
 * 
 * IRegistry interface.
 * 
 * <p>Only the functions that scout relies on, are in this interface.</p>
 *  
 * <i>Borrowed from jUDDI.</i>
 * 
 */

public interface IRegistryV3 extends IRegistryBase {
		
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
	 * @return Returns the transport.
	 */
	Transport getTransport();
	
	/**
	 * @param transport The transport to set.
	 */
	void setTransport(Transport transport);

	/**
	 * @exception RegistryV3Exception;
	 */
	PublisherAssertions setPublisherAssertions(String authInfo, PublisherAssertion[] assertionArray)
	throws RegistryV3Exception;

	/**
	 * "Used to register or update complete information about a businessService
	 *  exposed by a specified businessEntity."
	 *
	 * @exception RegistryV3Exception;
	 */
	ServiceDetail saveService(String authInfo, BusinessService[] serviceArray)
	throws RegistryV3Exception;

	/**
	 * "Used to register new bindingTemplate information or update existing
	 *  bindingTemplate information.  Use this to control information about
	 *  technical capabilities exposed by a registered business."
	 *
	 * @exception RegistryV3Exception;
	 */
	BindingDetail saveBinding(String authInfo, BindingTemplate[] bindingArray)
	throws RegistryV3Exception;
	
	/**
	 * "Used to register new businessEntity information or update existing
	 *  businessEntity information.  Use this to control the overall
	 *  information about the entire business.  Of the save_x APIs this one
	 *  has the broadest effect."
	 *
	 * @exception RegistryV3Exception;
	 */
	BusinessDetail saveBusiness(String authInfo, BusinessEntity[] businessArray)
	throws RegistryV3Exception;
	
	
	/**
	 * "Used to register or update complete information about a tModel."
	 *
	 * @exception RegistryV3Exception;
	 */
	TModelDetail saveTModel(String authInfo, TModel[] tModelArray)
	throws RegistryV3Exception;
	
	/**
	 * "Used to remove an existing bindingTemplate from the bindingTemplates
	 *  collection that is part of a specified businessService structure."
	 *
	 * @exception RegistryV3Exception;
	 */
	DispositionReport deleteBinding(String authInfo, String[] bindingKeyArray)
	throws RegistryV3Exception;
	
	/**
	 * "Used to delete registered businessEntity information from the registry."
	 *
	 * @exception RegistryV3Exception;
	 */
	DispositionReport deleteBusiness(String authInfo, String[] businessKeyArray)
	throws RegistryV3Exception;
	
	/**
	 * "Used to delete an existing businessService from the businessServices
	 *  collection that is part of a specified businessEntity."
	 *
	 * @exception RegistryV3Exception;
	 */
	DispositionReport deleteService(String authInfo, String[] serviceKeyArray)
	throws RegistryV3Exception;

	/**
	 * "Used to delete registered information about a tModel.  If there
	 *  are any references to a tModel when this call is made, the tModel
	 *  will be marked deleted instead of being physically removed."
	 *
	 * @exception RegistryV3Exception;
	 */
	DispositionReport deleteTModel(String authInfo, String[] tModelKeyArray)
	throws RegistryV3Exception;
	
	/**
	 * @exception RegistryV3Exception;
	 */
	AssertionStatusReport getAssertionStatusReport(String authInfo, String completionStatus)
	throws RegistryV3Exception;

	/**
	 * @exception RegistryV3Exception;
	 */
	DispositionReport deletePublisherAssertions(String authInfo, PublisherAssertion[] assertionArray)
	throws RegistryV3Exception;
	
	/**
	 * "Used to request an authentication token from an Operator Site.
	 *  Authentication tokens are required to use all other APIs defined
	 *  in the publishers API.  This server serves as the program's
	 *  equivalent of a login request."
	 *
	 * @exception RegistryV3Exception;
	 */
	AuthToken getAuthToken(String userID,String cred)
    throws RegistryV3Exception;

	  /**
	   * Used to locate information about one or more businesses. Returns a
	   * businessList message that matches the conditions specified.
	   *
	   * @exception RegistryV3Exception;
	   */
	  BusinessList findBusiness(Name[] nameArray,DiscoveryURLs discoveryURLs,IdentifierBag identifierBag,CategoryBag categoryBag,TModelBag tModelBag,FindQualifiers findQualifiers,int maxRows)
	    throws RegistryV3Exception;
	  
	  /**
	   * "Used to get the full businessEntity information for one or more
	   *  businesses. Returns a businessDetail message."
	   *
	   * @exception RegistryV3Exception;
	   */
	  BusinessDetail getBusinessDetail(String businessKey)
	    throws RegistryV3Exception;

	  /**
	   * "Used to get the full businessEntity information for one or more
	   *  businesses. Returns a businessDetail message."
	   *
	   * @exception RegistryV3Exception;
	   */
	  BusinessDetail getBusinessDetail(String[] businessKeyVector)
	    throws RegistryV3Exception;
	  
	  /**
	   * @exception RegistryV3Exception;
	   */
	  PublisherAssertions getPublisherAssertions(String authInfo)
	    throws RegistryV3Exception;
	  
	  /**
	   * @exception RegistryV3Exception;
	   */
	  RegisteredInfo getRegisteredInfo(String authInfo)
	  	throws RegistryV3Exception;
	  
	  /**
	   * "Used to locate one or more tModel information structures. Returns a
	   *  tModelList structure."
	   *
	   * @exception RegistryV3Exception;
	   */
	  TModelList findTModel(String name,CategoryBag categoryBag,IdentifierBag identifierBag,FindQualifiers findQualifiers,int maxRows)
	  throws RegistryV3Exception;
	  
	  /**
           * 
	   * "Used to locate specific bindings within a registered
	   *  businessService. Returns a bindingDetail message."
	   *
	   * @exception RegistryV3Exception
           * @param serviceKey
           * @param categoryBag
           * @param tModelBag
           * @param findQualifiers
           * @param maxRows
           * @return
           * @throws RegistryV3Exception 
           */
	  BindingDetail findBinding(String serviceKey,CategoryBag categoryBag,TModelBag tModelBag,FindQualifiers findQualifiers,int maxRows)
	  throws RegistryV3Exception;
	  
	  /**
	   * "Used to locate specific services within a registered
	   *  businessEntity. Return a serviceList message." From the
	   *  XML spec (API, p18) it appears that the name, categoryBag,
	   *  and tModelBag arguments are mutually exclusive.
	   *
	   * @exception RegistryV3Exception;
	   */
	  ServiceList findService(String businessKey,Name[] nameArray,CategoryBag categoryBag,TModelBag tModelBag,FindQualifiers findQualifiers,int maxRows)
	  throws RegistryV3Exception;
	  
	  /**
	   * "Used to get full details for a given set of registered tModel
	   *  data. Returns a tModelDetail message."
	   *
	   * @exception RegistryV3Exception;
	   */
	  TModelDetail getTModelDetail(String tModelKey)
	  throws RegistryV3Exception;
	  
	  /**
	   * "Used to get full details for a given set of registered tModel
	   *  data. Returns a tModelDetail message."
	   *
	   * @exception RegistryV3Exception;
	   */
	  TModelDetail getTModelDetail(String[] tModelKeyArray)
	  throws RegistryV3Exception;

	  /**
	   * "Used to get full details for a given set of registered
	   *  businessService data. Returns a serviceDetail message."
	   *
	   * @exception RegistryV3Exception;
	   */
	  ServiceDetail getServiceDetail(String serviceKey)
	    throws RegistryV3Exception;

	  /**
	   * "Used to get full details for a given set of registered
	   *  businessService data. Returns a serviceDetail message."
	   *
	   * @exception RegistryV3Exception;
	   */
	  ServiceDetail getServiceDetail(String[] serviceKeyArray)
	    throws RegistryV3Exception;


}
