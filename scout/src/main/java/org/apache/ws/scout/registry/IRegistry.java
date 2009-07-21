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

import org.apache.ws.scout.model.uddi.v2.*;
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

public interface IRegistry {
	
	String execute(String uddiRequest, String urltype) throws RegistryException;
	
	/**
	 * @return Returns the inquiryURL.
	 */
	URI getInquiryURI();
	
	/**
	 * @param inquiryURL The inquiryURL to set.
	 */
	void setInquiryURI(URI uri);
	
	/**
	 * @return Returns the publishURL.
	 */
	URI getPublishURI();	
	
	/**
	 * @param publishURL The publishURL to set.
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
	 * @exception RegistryException;
	 */
	PublisherAssertions setPublisherAssertions(String authInfo, PublisherAssertion[] assertionArray)
	throws RegistryException;

	/**
	 * "Used to register or update complete information about a businessService
	 *  exposed by a specified businessEntity."
	 *
	 * @exception RegistryException;
	 */
	ServiceDetail saveService(String authInfo, BusinessService[] serviceArray)
	throws RegistryException;

	/**
	 * "Used to register new bindingTemplate information or update existing
	 *  bindingTemplate information.  Use this to control information about
	 *  technical capabilities exposed by a registered business."
	 *
	 * @exception RegistryException;
	 */
	BindingDetail saveBinding(String authInfo, BindingTemplate[] bindingArray)
	throws RegistryException;
	
	/**
	 * "Used to register new businessEntity information or update existing
	 *  businessEntity information.  Use this to control the overall
	 *  information about the entire business.  Of the save_x APIs this one
	 *  has the broadest effect."
	 *
	 * @exception RegistryException;
	 */
	BusinessDetail saveBusiness(String authInfo, BusinessEntity[] businessArray)
	throws RegistryException;
	
	
	/**
	 * "Used to register or update complete information about a tModel."
	 *
	 * @exception RegistryException;
	 */
	TModelDetail saveTModel(String authInfo, TModel[] tModelArray)
	throws RegistryException;
	
	/**
	 * "Used to remove an existing bindingTemplate from the bindingTemplates
	 *  collection that is part of a specified businessService structure."
	 *
	 * @exception RegistryException;
	 */
	DispositionReport deleteBinding(String authInfo, String[] bindingKeyArray)
	throws RegistryException;
	
	/**
	 * "Used to delete registered businessEntity information from the registry."
	 *
	 * @exception RegistryException;
	 */
	DispositionReport deleteBusiness(String authInfo, String[] businessKeyArray)
	throws RegistryException;
	
	/**
	 * "Used to delete an existing businessService from the businessServices
	 *  collection that is part of a specified businessEntity."
	 *
	 * @exception RegistryException;
	 */
	DispositionReport deleteService(String authInfo, String[] serviceKeyArray)
	throws RegistryException;

	/**
	 * "Used to delete registered information about a tModel.  If there
	 *  are any references to a tModel when this call is made, the tModel
	 *  will be marked deleted instead of being physically removed."
	 *
	 * @exception RegistryException;
	 */
	DispositionReport deleteTModel(String authInfo, String[] tModelKeyArray)
	throws RegistryException;
	
	/**
	 * @exception RegistryException;
	 */
	AssertionStatusReport getAssertionStatusReport(String authInfo, String completionStatus)
	throws RegistryException;

	/**
	 * @exception RegistryException;
	 */
	DispositionReport deletePublisherAssertions(String authInfo, PublisherAssertion[] assertionArray)
	throws RegistryException;
	
	/**
	 * "Used to request an authentication token from an Operator Site.
	 *  Authentication tokens are required to use all other APIs defined
	 *  in the publishers API.  This server serves as the program's
	 *  equivalent of a login request."
	 *
	 * @exception RegistryException;
	 */
	AuthToken getAuthToken(String userID,String cred)
    throws RegistryException;

	  /**
	   * Used to locate information about one or more businesses. Returns a
	   * businessList message that matches the conditions specified.
	   *
	   * @exception RegistryException;
	   */
	  BusinessList findBusiness(Name[] nameArray,DiscoveryURLs discoveryURLs,IdentifierBag identifierBag,CategoryBag categoryBag,TModelBag tModelBag,FindQualifiers findQualifiers,int maxRows)
	    throws RegistryException;
	  
	  /**
	   * "Used to get the full businessEntity information for one or more
	   *  businesses. Returns a businessDetail message."
	   *
	   * @exception RegistryException;
	   */
	  BusinessDetail getBusinessDetail(String businessKey)
	    throws RegistryException;

	  /**
	   * "Used to get the full businessEntity information for one or more
	   *  businesses. Returns a businessDetail message."
	   *
	   * @exception RegistryException;
	   */
	  BusinessDetail getBusinessDetail(String[] businessKeyVector)
	    throws RegistryException;
	  
	  /**
	   * @exception RegistryException;
	   */
	  PublisherAssertions getPublisherAssertions(String authInfo)
	    throws RegistryException;
	  
	  /**
	   * @exception RegistryException;
	   */
	  RegisteredInfo getRegisteredInfo(String authInfo)
	  	throws RegistryException;
	  
	  /**
	   * "Used to locate one or more tModel information structures. Returns a
	   *  tModelList structure."
	   *
	   * @exception RegistryException;
	   */
	  TModelList findTModel(String name,CategoryBag categoryBag,IdentifierBag identifierBag,FindQualifiers findQualifiers,int maxRows)
	  throws RegistryException;
	  
	  /**
	   * "Used to locate specific bindings within a registered
	   *  businessService. Returns a bindingDetail message."
	   *
	   * @exception RegistryException
	   */
	  BindingDetail findBinding(String serviceKey,CategoryBag categoryBag,TModelBag tModelBag,FindQualifiers findQualifiers,int maxRows)
	  throws RegistryException;
	  
	  /**
	   * "Used to locate specific services within a registered
	   *  businessEntity. Return a serviceList message." From the
	   *  XML spec (API, p18) it appears that the name, categoryBag,
	   *  and tModelBag arguments are mutually exclusive.
	   *
	   * @exception RegistryException;
	   */
	  ServiceList findService(String businessKey,Name[] nameArray,CategoryBag categoryBag,TModelBag tModelBag,FindQualifiers findQualifiers,int maxRows)
	  throws RegistryException;
	  
	  /**
	   * "Used to get full details for a given set of registered tModel
	   *  data. Returns a tModelDetail message."
	   *
	   * @exception RegistryException;
	   */
	  TModelDetail getTModelDetail(String tModelKey)
	  throws RegistryException;
	  
	  /**
	   * "Used to get full details for a given set of registered tModel
	   *  data. Returns a tModelDetail message."
	   *
	   * @exception RegistryException;
	   */
	  TModelDetail getTModelDetail(String[] tModelKeyArray)
	  throws RegistryException;

	  /**
	   * "Used to get full details for a given set of registered
	   *  businessService data. Returns a serviceDetail message."
	   *
	   * @exception RegistryException;
	   */
	  ServiceDetail getServiceDetail(String serviceKey)
	    throws RegistryException;

	  /**
	   * "Used to get full details for a given set of registered
	   *  businessService data. Returns a serviceDetail message."
	   *
	   * @exception RegistryException;
	   */
	  ServiceDetail getServiceDetail(String[] serviceKeyArray)
	    throws RegistryException;


}
