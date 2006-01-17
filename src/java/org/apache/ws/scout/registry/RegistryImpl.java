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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.apache.ws.scout.transport.Transport;

import uddiOrgApiV2.AssertionStatusReport;
import uddiOrgApiV2.AssertionStatusReportDocument;
import uddiOrgApiV2.AuthToken;
import uddiOrgApiV2.AuthTokenDocument;
import uddiOrgApiV2.BindingDetail;
import uddiOrgApiV2.BindingDetailDocument;
import uddiOrgApiV2.BindingTemplate;
import uddiOrgApiV2.BusinessDetail;
import uddiOrgApiV2.BusinessDetailDocument;
import uddiOrgApiV2.BusinessEntity;
import uddiOrgApiV2.BusinessList;
import uddiOrgApiV2.BusinessListDocument;
import uddiOrgApiV2.BusinessService;
import uddiOrgApiV2.CategoryBag;
import uddiOrgApiV2.DeleteBinding;
import uddiOrgApiV2.DeleteBindingDocument;
import uddiOrgApiV2.DeleteBusiness;
import uddiOrgApiV2.DeleteBusinessDocument;
import uddiOrgApiV2.DeletePublisherAssertions;
import uddiOrgApiV2.DeletePublisherAssertionsDocument;
import uddiOrgApiV2.DeleteService;
import uddiOrgApiV2.DeleteServiceDocument;
import uddiOrgApiV2.DeleteTModel;
import uddiOrgApiV2.DeleteTModelDocument;
import uddiOrgApiV2.DiscoveryURLs;
import uddiOrgApiV2.DispositionReport;
import uddiOrgApiV2.DispositionReportDocument;
import uddiOrgApiV2.FindBinding;
import uddiOrgApiV2.FindBindingDocument;
import uddiOrgApiV2.FindBusiness;
import uddiOrgApiV2.FindBusinessDocument;
import uddiOrgApiV2.FindQualifiers;
import uddiOrgApiV2.FindService;
import uddiOrgApiV2.FindServiceDocument;
import uddiOrgApiV2.FindTModel;
import uddiOrgApiV2.FindTModelDocument;
import uddiOrgApiV2.GetAssertionStatusReport;
import uddiOrgApiV2.GetAssertionStatusReportDocument;
import uddiOrgApiV2.GetAuthToken;
import uddiOrgApiV2.GetAuthTokenDocument;
import uddiOrgApiV2.GetBusinessDetail;
import uddiOrgApiV2.GetBusinessDetailDocument;
import uddiOrgApiV2.GetPublisherAssertions;
import uddiOrgApiV2.GetPublisherAssertionsDocument;
import uddiOrgApiV2.GetServiceDetail;
import uddiOrgApiV2.GetServiceDetailDocument;
import uddiOrgApiV2.GetTModelDetail;
import uddiOrgApiV2.GetTModelDetailDocument;
import uddiOrgApiV2.IdentifierBag;
import uddiOrgApiV2.Name;
import uddiOrgApiV2.PublisherAssertion;
import uddiOrgApiV2.PublisherAssertions;
import uddiOrgApiV2.PublisherAssertionsDocument;
import uddiOrgApiV2.SaveBinding;
import uddiOrgApiV2.SaveBindingDocument;
import uddiOrgApiV2.SaveBusiness;
import uddiOrgApiV2.SaveBusinessDocument;
import uddiOrgApiV2.SaveService;
import uddiOrgApiV2.SaveServiceDocument;
import uddiOrgApiV2.SaveTModel;
import uddiOrgApiV2.SaveTModelDocument;
import uddiOrgApiV2.ServiceDetail;
import uddiOrgApiV2.ServiceDetailDocument;
import uddiOrgApiV2.ServiceList;
import uddiOrgApiV2.ServiceListDocument;
import uddiOrgApiV2.SetPublisherAssertions;
import uddiOrgApiV2.SetPublisherAssertionsDocument;
import uddiOrgApiV2.TModel;
import uddiOrgApiV2.TModelBag;
import uddiOrgApiV2.TModelDetail;
import uddiOrgApiV2.TModelDetailDocument;
import uddiOrgApiV2.TModelList;
import uddiOrgApiV2.TModelListDocument;

/**
 * RegistryImpl is the implementation of IRegistry.
 * 
 * <p>The execute() function signature has been changed slightly from the jUDDI
 * version, since the URL can no longer be decided dynamically (in an easy
 * enough manner) as we don't use jUDDI data types anymore.</p>
 * 
 * <i>The function code is borrowed from jUDDI, with appropriate modifications so
 * that xmlbeans data types are used intead of jUDDI data types.</i>
 * 
 */

public class RegistryImpl implements IRegistry {

	public static final String INQUIRY_ENDPOINT_PROPERTY_NAME = "scout.proxy.inquiryURL";
	public static final String PUBLISH_ENDPOINT_PROPERTY_NAME = "scout.proxy.publishURL";
	public static final String ADMIN_ENDPOINT_PROPERTY_NAME = "scout.proxy.adminURL";
	public static final String TRANSPORT_CLASS_PROPERTY_NAME = "scout.proxy.transportClass";
	public static final String SECURITY_PROVIDER_PROPERTY_NAME = "scout.proxy.securityProvider";
	public static final String PROTOCOL_HANDLER_PROPERTY_NAME = "scout.proxy.protocolHandler";
	public static final String UDDI_VERSION_PROPERTY_NAME = "scout.proxy.uddiVersion";
	public static final String UDDI_NAMESPACE_PROPERTY_NAME = "scout.proxy.uddiNamespace";

	public static final String DEFAULT_INQUIRY_ENDPOINT = "http://localhost/juddi/inquiry";
	public static final String DEFAULT_PUBLISH_ENDPOINT = "http://localhost/juddi/publish";
	public static final String DEFAULT_ADMIN_ENDPOINT = "http://localhost/juddi/admin";
	public static final String DEFAULT_TRANSPORT_CLASS = "org.apache.ws.scout.transport.AxisTransport";
	public static final String DEFAULT_SECURITY_PROVIDER = "com.sun.net.ssl.internal.ssl.Provider";
	public static final String DEFAULT_PROTOCOL_HANDLER = "com.sun.net.ssl.internal.www.protocol";
	public static final String DEFAULT_UDDI_VERSION = "2.0";
	public static final String DEFAULT_UDDI_NAMESPACE = "urn:uddi-org:api_v2";

	private URL adminURL;
	private URL inquiryURL;
	private URL publishURL;

	private Transport transport;

	private String securityProvider;
	private String protocolHandler;
	private String uddiVersion;
	private String uddiNamespace;

	/**
	 * Creates a new instance of RegistryImpl.
	 */
	public RegistryImpl(Properties props) {
		super();

		this.init(props);
	}

	/**
	 * 
	 */
	private void init(Properties props) {
		// We need to have a non-null Properties
		// instance so initialization takes place.
		if (props == null)
			props = new Properties();

		// Override defaults with specific specific values
		try {
			String iURL = props.getProperty(INQUIRY_ENDPOINT_PROPERTY_NAME);
			if (iURL != null)
				this.setInquiryURL(new URL(iURL));
			else
				this.setInquiryURL(new URL(DEFAULT_INQUIRY_ENDPOINT));

			String pURL = props.getProperty(PUBLISH_ENDPOINT_PROPERTY_NAME);
			if (pURL != null)
				this.setPublishURL(new URL(pURL));
			else
				this.setPublishURL(new URL(DEFAULT_PUBLISH_ENDPOINT));

			String aURL = props.getProperty(ADMIN_ENDPOINT_PROPERTY_NAME);
			if (aURL != null)
				this.setAdminURL(new URL(aURL));
			else
				this.setAdminURL(new URL(DEFAULT_ADMIN_ENDPOINT));
		} catch (MalformedURLException muex) {
			muex.printStackTrace();
		}

		String secProvider = props.getProperty(SECURITY_PROVIDER_PROPERTY_NAME);
		if (secProvider != null)
			this.setSecurityProvider(secProvider);
		else
			this.setSecurityProvider(DEFAULT_SECURITY_PROVIDER);

		String protoHandler = props.getProperty(PROTOCOL_HANDLER_PROPERTY_NAME);
		if (protoHandler != null)
			this.setProtocolHandler(protoHandler);
		else
			this.setProtocolHandler(DEFAULT_PROTOCOL_HANDLER);

		String uddiVer = props.getProperty(UDDI_VERSION_PROPERTY_NAME);
		if (uddiVer != null)
			this.setUddiVersion(uddiVer);
		else
			this.setUddiVersion(DEFAULT_UDDI_VERSION);

		String uddiNS = props.getProperty(UDDI_NAMESPACE_PROPERTY_NAME);
		if (uddiNS != null)
			this.setUddiNamespace(uddiNS);
		else
			this.setUddiNamespace(DEFAULT_UDDI_NAMESPACE);

		String transClass = props.getProperty(TRANSPORT_CLASS_PROPERTY_NAME);
		if (transClass != null)
			this.setTransport(this.getTransport(transClass));
		else
			this.setTransport(this.getTransport(DEFAULT_TRANSPORT_CLASS));
	}

	/**
	 * 
	 * @param uddiRequest
	 * @return
	 * @throws RegistryException
	 */
	public String execute(String uddiRequest, String urltype)
			throws RegistryException {
		URL endPointURL = null;
		if (urltype.equalsIgnoreCase("INQUIRY"))
			endPointURL = this.getInquiryURL();
		else
			endPointURL = this.getPublishURL();

		// A SOAP request is made and a SOAP response
		// is returned.

		return transport.send(uddiRequest, endPointURL);
	}

	/**
	 * 
	 */
	public XmlObject execute(XmlObject uddiRequest, URL endPointURL)
			throws RegistryException {

		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(uddiRequest.newInputStream());
		} catch (SAXException saxe) {
			throw (new RegistryException(saxe));
		} catch (ParserConfigurationException pce) {
			throw (new RegistryException(pce));
		} catch (IOException ioe) {
			throw (new RegistryException(ioe));
		}

		Element request = doc.getDocumentElement();

		request.setAttribute("generic", this.getUddiVersion());
		request.setAttribute("xmlns", this.getUddiNamespace());

		// A SOAP request is made and a SOAP response
		// is returned.

		Element response = transport.send(request, endPointURL);

		// First, let's make sure that a response
		// (any response) is found in the SOAP Body.

		String responseName = response.getLocalName();
		if (responseName == null) {
			throw new RegistryException("Unsupported response "
					+ "from registry. A value was not present.");
		}

		// Let's now try to determine which UDDI response
		// we received and unmarshal it appropriately or
		// throw a RegistryException if it's unknown.

		// Well, we have now determined that something was
		// returned and it is "a something" that we know
		// about so let's unmarshal it into a RegistryObject

		// Next, let's make sure we didn't recieve a SOAP
		// Fault. If it is a SOAP Fault then throw it
		// immediately.

		XmlObject uddiResponse = null;
		try {
			uddiResponse = XmlObject.Factory.parse(response);
		} catch (XmlException xmle) {
			throw (new RegistryException(xmle));
		}

		if (responseName.toLowerCase().equals("fault")) {
			NodeList nodeList = null;
			
			// Child Elements
			String fCode = null;
			nodeList = response.getElementsByTagName("faultcode");
			if (nodeList.getLength() > 0)
				fCode = nodeList.item(0).getNodeValue();

			String fString = null;
			nodeList = response.getElementsByTagName("faultstring");
			if (nodeList.getLength() > 0)
				fString = nodeList.item(0).getNodeValue();

			String fActor = null;
			nodeList = response.getElementsByTagName("faultactor");
			if (nodeList.getLength() > 0)
				fActor = nodeList.item(0).getNodeValue();

			DispositionReport dispRpt = DispositionReport.Factory.newInstance();

			nodeList = response.getElementsByTagName("detail");
			if (nodeList.getLength() > 0) {
				nodeList = ((Element) nodeList.item(0))
						.getElementsByTagName("dispositionReport");
				if (nodeList.getLength() > 0) {
					XmlObject dispRptObj = null;
					try {
						dispRptObj = XmlObject.Factory.parse((Element) nodeList
								.item(0));
					} catch (XmlException xmle) {
						throw (new RegistryException(xmle));
					}

					XmlObject o = dispRptObj.changeType(DispositionReportDocument.type);
					dispRpt = ((DispositionReportDocument) o).getDispositionReport();
				}
			}

			RegistryException e = new RegistryException(fCode, fString, fActor, dispRpt);
			
			// FIXME: This should be removed after testing!
			System.err.println("SOAP message:");
			System.err.println(uddiResponse.xmlText());
			
			// Create RegistryException instance and return
			throw e;
		}

		return uddiResponse;
	}

	/**
	 * @return Returns the adminURL.
	 */
	public URL getAdminURL() {
		return this.adminURL;
	}

	/**
	 * @param adminURL
	 *            The adminURL to set.
	 */
	public void setAdminURL(URL url) {
		this.adminURL = url;
	}

	/**
	 * @return Returns the inquiryURL.
	 */
	public URL getInquiryURL() {
		return this.inquiryURL;
	}

	/**
	 * @param inquiryURL
	 *            The inquiryURL to set.
	 */
	public void setInquiryURL(URL url) {
		this.inquiryURL = url;
	}

	/**
	 * @return Returns the protocolHandler.
	 */
	public String getProtocolHandler() {
		return this.protocolHandler;
	}

	/**
	 * @param protocolHandler
	 *            The protocolHandler to set.
	 */
	public void setProtocolHandler(String protoHandler) {
		this.protocolHandler = protoHandler;
	}

	/**
	 * @return Returns the publishURL.
	 */
	public URL getPublishURL() {
		return this.publishURL;
	}

	/**
	 * @param publishURL
	 *            The publishURL to set.
	 */
	public void setPublishURL(URL url) {
		this.publishURL = url;
	}

	/**
	 * @return Returns the securityProvider.
	 */
	public String getSecurityProvider() {
		return this.securityProvider;
	}

	/**
	 * @param securityProvider
	 *            The securityProvider to set.
	 */
	public void setSecurityProvider(String secProvider) {
		this.securityProvider = secProvider;
	}

	/**
	 * @return Returns the transport.
	 */
	public Transport getTransport() {
		return transport;
	}

	/**
	 * @param transport
	 *            The transport to set.
	 */
	public void setTransport(Transport transport) {
		this.transport = transport;
	}

	/**
	 * @return Returns the uddiNS.
	 */
	public String getUddiNamespace() {
		return this.uddiNamespace;
	}

	/**
	 * @param uddiNS
	 *            The uddiNS to set.
	 */
	public void setUddiNamespace(String uddiNS) {
		this.uddiNamespace = uddiNS;
	}

	/**
	 * @return Returns the uddiVersion.
	 */
	public String getUddiVersion() {
		return this.uddiVersion;
	}

	/**
	 * @param uddiVersion
	 *            The uddiVersion to set.
	 */
	public void setUddiVersion(String uddiVer) {
		this.uddiVersion = uddiVer;
	}

	/**
	 * "Used to remove an existing bindingTemplate from the bindingTemplates
	 * collection that is part of a specified businessService structure."
	 * 
	 * @exception RegistryException;
	 */
	public DispositionReport deleteBinding(String authInfo,
			String[] bindingKeyArray) throws RegistryException {
		DeleteBindingDocument doc = DeleteBindingDocument.Factory.newInstance();
		DeleteBinding request = doc.addNewDeleteBinding();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (bindingKeyArray != null) {
			request.setBindingKeyArray(bindingKeyArray);
		}

		DispositionReport dr;
		XmlObject o = execute(doc, this.getPublishURL()).changeType(
				DispositionReportDocument.type);
		dr = ((DispositionReportDocument) o).getDispositionReport();

		return dr;
	}

	/**
	 * "Used to delete registered businessEntity information from the registry."
	 * 
	 * @exception RegistryException;
	 */
	public DispositionReport deleteBusiness(String authInfo,
			String[] businessKeyArray) throws RegistryException {
		DeleteBusinessDocument doc = DeleteBusinessDocument.Factory
				.newInstance();
		DeleteBusiness request = doc.addNewDeleteBusiness();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (businessKeyArray != null) {
			request.setBusinessKeyArray(businessKeyArray);
		}

		DispositionReport dr;
		XmlObject o = execute(doc, this.getPublishURL()).changeType(
				DispositionReportDocument.type);
		dr = ((DispositionReportDocument) o).getDispositionReport();

		return dr;
	}

	/**
	 * @exception RegistryException;
	 */
	public DispositionReport deletePublisherAssertions(String authInfo,
			PublisherAssertion[] assertionArray) throws RegistryException {
		DeletePublisherAssertionsDocument doc = DeletePublisherAssertionsDocument.Factory
				.newInstance();
		DeletePublisherAssertions request = doc
				.addNewDeletePublisherAssertions();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (assertionArray != null) {
			request.setPublisherAssertionArray(assertionArray);
		}

		DispositionReport dr;
		XmlObject o = execute(doc, this.getPublishURL()).changeType(
				DispositionReportDocument.type);
		dr = ((DispositionReportDocument) o).getDispositionReport();

		return dr;
	}

	/**
	 * "Used to delete an existing businessService from the businessServices
	 * collection that is part of a specified businessEntity."
	 * 
	 * @exception RegistryException;
	 */
	public DispositionReport deleteService(String authInfo,
			String[] serviceKeyArray) throws RegistryException {
		DeleteServiceDocument doc = DeleteServiceDocument.Factory.newInstance();
		DeleteService request = doc.addNewDeleteService();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (serviceKeyArray != null) {
			request.setServiceKeyArray(serviceKeyArray);
		}

		DispositionReport dr;
		XmlObject o = execute(doc, this.getPublishURL()).changeType(
				DispositionReportDocument.type);
		dr = ((DispositionReportDocument) o).getDispositionReport();

		return dr;
	}

	/**
	 * "Used to delete registered information about a tModel. If there are any
	 * references to a tModel when this call is made, the tModel will be marked
	 * deleted instead of being physically removed."
	 * 
	 * @exception RegistryException;
	 */
	public DispositionReport deleteTModel(String authInfo,
			String[] tModelKeyArray) throws RegistryException {
		DeleteTModelDocument doc = DeleteTModelDocument.Factory.newInstance();
		DeleteTModel request = doc.addNewDeleteTModel();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (tModelKeyArray != null) {
			request.setTModelKeyArray(tModelKeyArray);
		}

		DispositionReport dr;
		XmlObject o = execute(doc, this.getPublishURL()).changeType(
				DispositionReportDocument.type);
		dr = ((DispositionReportDocument) o).getDispositionReport();

		return dr;
	}

	/**
	 * Used to locate information about one or more businesses. Returns a
	 * businessList message that matches the conditions specified.
	 * 
	 * @exception RegistryException;
	 */
	public BusinessList findBusiness(Name[] nameArray,
			DiscoveryURLs discoveryURLs, IdentifierBag identifierBag,
			CategoryBag categoryBag, TModelBag tModelBag,
			FindQualifiers findQualifiers, int maxRows)
			throws RegistryException {
		FindBusinessDocument doc = FindBusinessDocument.Factory.newInstance();
		FindBusiness request = doc.addNewFindBusiness();

		if (nameArray != null) {
			request.setNameArray(nameArray);
		}

		if (discoveryURLs != null) {
			request.setDiscoveryURLs(discoveryURLs);
		}

		if (identifierBag != null) {
			request.setIdentifierBag(identifierBag);
		}

		if (categoryBag != null) {
			request.setCategoryBag(categoryBag);
		}

		if (tModelBag != null) {
			request.setTModelBag(tModelBag);
		}

		if (findQualifiers != null) {
			request.setFindQualifiers(findQualifiers);
		}

		request.setMaxRows(maxRows);

		BusinessList bl;
		XmlObject o = execute(doc, this.getInquiryURL()).changeType(
				BusinessListDocument.type);
		bl = ((BusinessListDocument) o).getBusinessList();

		return bl;
	}

	/**
	 * "Used to locate specific bindings within a registered businessService.
	 * Returns a bindingDetail message."
	 * 
	 * @exception RegistryException
	 */
	public BindingDetail findBinding(String serviceKey,
			CategoryBag categoryBag, TModelBag tModelBag,
			FindQualifiers findQualifiers, int maxRows)
			throws RegistryException {
		// FIXME: Juddi's methods also set category bag (per uddi spec v3).
		// However, we are sticking to v2 for now, so categorybag doesn't
		// exist under FindBinding. It is fine for now, since the incoming
		// parameter value is always null anyways -- but this may change
		// in the future.

		FindBindingDocument doc = FindBindingDocument.Factory.newInstance();
		FindBinding request = doc.addNewFindBinding();

		if (serviceKey != null) {
			request.setServiceKey(serviceKey);
		}

		if (tModelBag != null) {
			request.setTModelBag(tModelBag);
		}

		if (findQualifiers != null) {
			request.setFindQualifiers(findQualifiers);
		}
		request.setMaxRows(maxRows);

		BindingDetail bd;
		XmlObject o = execute(request, this.getInquiryURL()).changeType(
				BindingDetailDocument.type);
		bd = ((BindingDetailDocument) o).getBindingDetail();

		return bd;
	}

	/**
	 * "Used to locate specific services within a registered businessEntity.
	 * Return a serviceList message." From the XML spec (API, p18) it appears
	 * that the name, categoryBag, and tModelBag arguments are mutually
	 * exclusive.
	 * 
	 * @exception RegistryException;
	 */
	public ServiceList findService(String businessKey, Name[] nameArray,
			CategoryBag categoryBag, TModelBag tModelBag,
			FindQualifiers findQualifiers, int maxRows)
			throws RegistryException {
		FindServiceDocument doc = FindServiceDocument.Factory.newInstance();
		FindService request = doc.addNewFindService();

		if (businessKey != null) {
			request.setBusinessKey(businessKey);
		}

		if (nameArray != null) {
			request.setNameArray(nameArray);
		}

		if (categoryBag != null) {
			request.setCategoryBag(categoryBag);
		}

		if (tModelBag != null) {
			request.setTModelBag(tModelBag);
		}

		if (findQualifiers != null) {
			request.setFindQualifiers(findQualifiers);
		}

		request.setMaxRows(maxRows);

		ServiceList sl;
		XmlObject o = execute(doc, this.getInquiryURL()).changeType(
				ServiceListDocument.type);
		sl = ((ServiceListDocument) o).getServiceList();

		return sl;
	}

	/**
	 * "Used to locate one or more tModel information structures. Returns a
	 * tModelList structure."
	 * 
	 * @exception RegistryException;
	 */
	public TModelList findTModel(String name, CategoryBag categoryBag,
			IdentifierBag identifierBag, FindQualifiers findQualifiers,
			int maxRows) throws RegistryException {
		FindTModelDocument doc = FindTModelDocument.Factory.newInstance();
		FindTModel request = doc.addNewFindTModel();

		Name n = Name.Factory.newInstance();

		if (name != null) {
			n.setStringValue(name);
		}

		request.setName(n);

		if (categoryBag != null) {
			request.setCategoryBag(categoryBag);
		}

		if (identifierBag != null) {
			request.setIdentifierBag(identifierBag);
		}

		if (findQualifiers != null) {
			request.setFindQualifiers(findQualifiers);
		}

		request.setMaxRows(maxRows);

		TModelList tml;
		XmlObject o = execute(doc, this.getInquiryURL()).changeType(
				TModelListDocument.type);
		tml = ((TModelListDocument) o).getTModelList();

		return tml;
	}

	/**
	 * @exception RegistryException;
	 */
	public AssertionStatusReport getAssertionStatusReport(String authInfo,
			String completionStatus) throws RegistryException {
		GetAssertionStatusReportDocument doc = GetAssertionStatusReportDocument.Factory
				.newInstance();
		GetAssertionStatusReport request = doc.addNewGetAssertionStatusReport();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (completionStatus != null) {
			request.setCompletionStatus(completionStatus);
		}

		AssertionStatusReport asr;
		XmlObject o = execute(doc, this.getPublishURL()).changeType(
				AssertionStatusReportDocument.type);
		asr = ((AssertionStatusReportDocument) o).getAssertionStatusReport();

		return asr;
	}

	/**
	 * "Used to request an authentication token from an Operator Site.
	 * Authentication tokens are required to use all other APIs defined in the
	 * publishers API. This server serves as the program's equivalent of a login
	 * request."
	 * 
	 * @exception RegistryException;
	 */
	public AuthToken getAuthToken(String userID, String cred)
			throws RegistryException {
		GetAuthTokenDocument doc = GetAuthTokenDocument.Factory.newInstance();
		GetAuthToken request = doc.addNewGetAuthToken();

		if (userID != null) {
			request.setUserID(userID);
		}

		if (cred != null) {
			request.setCred(cred);
		}

		AuthToken at;
		XmlObject o = execute(doc, this.getPublishURL()).changeType(
				AuthTokenDocument.type);
		at = ((AuthTokenDocument) o).getAuthToken();

		return at;
	}

	/**
	 * Used to get the full businessEntity information for a particular business
	 * entity. Returns a businessDetail message.
	 * 
	 * @exception RegistryException;
	 */
	public BusinessDetail getBusinessDetail(String businessKey)
			throws RegistryException {
		String[] keys = new String[1];
		keys[0] = businessKey;

		return getBusinessDetail(keys);
	}

	/**
	 * "Used to get the full businessEntity information for one or more
	 * businesses. Returns a businessDetail message."
	 * 
	 * @exception RegistryException;
	 */
	public BusinessDetail getBusinessDetail(String[] businessKeyArray)
			throws RegistryException {
		GetBusinessDetailDocument doc = GetBusinessDetailDocument.Factory
				.newInstance();
		GetBusinessDetail request = doc.addNewGetBusinessDetail();

		if (businessKeyArray != null) {
			request.setBusinessKeyArray(businessKeyArray);
		}

		BusinessDetail bd;
		XmlObject o = execute(doc, this.getInquiryURL()).changeType(
				BusinessDetailDocument.type);
		bd = ((BusinessDetailDocument) o).getBusinessDetail();

		return bd;
	}

	/**
	 * @exception RegistryException;
	 */
	public PublisherAssertions getPublisherAssertions(String authInfo)
			throws RegistryException {
		GetPublisherAssertionsDocument doc = GetPublisherAssertionsDocument.Factory
				.newInstance();
		GetPublisherAssertions request = doc.addNewGetPublisherAssertions();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		PublisherAssertions pa;
		XmlObject o = execute(doc, this.getPublishURL()).changeType(
				PublisherAssertionsDocument.type);
		pa = ((PublisherAssertionsDocument) o).getPublisherAssertions();

		return pa;
	}

	/**
	 * "Used to get full details for a particular registered businessService.
	 * Returns a serviceDetail message."
	 * 
	 * @exception RegistryException;
	 */
	public ServiceDetail getServiceDetail(String serviceKey)
			throws RegistryException {
		String[] keys = new String[1];
		keys[0] = serviceKey;

		return getServiceDetail(keys);
	}

	/**
	 * "Used to get full details for a given set of registered businessService
	 * data. Returns a serviceDetail message."
	 * 
	 * @exception RegistryException;
	 */
	public ServiceDetail getServiceDetail(String[] serviceKeyArray)
			throws RegistryException {
		GetServiceDetailDocument doc = GetServiceDetailDocument.Factory
				.newInstance();
		GetServiceDetail request = doc.addNewGetServiceDetail();

		if (serviceKeyArray != null) {
			request.setServiceKeyArray(serviceKeyArray);
		}

		ServiceDetail sd;
		XmlObject o = execute(doc, this.getInquiryURL()).changeType(
				ServiceDetailDocument.type);
		sd = ((ServiceDetailDocument) o).getServiceDetail();

		return sd;
	}

	/**
	 * "Used to get full details for a particular registered TModel. Returns a
	 * tModelDetail message."
	 * 
	 * @exception RegistryException;
	 */
	public TModelDetail getTModelDetail(String tModelKey)
			throws RegistryException {
		String[] keys = new String[1];
		keys[0] = tModelKey;

		return getTModelDetail(keys);
	}

	/**
	 * "Used to get full details for a given set of registered tModel data.
	 * Returns a tModelDetail message."
	 * 
	 * @exception RegistryException;
	 */
	public TModelDetail getTModelDetail(String[] tModelKeyArray)
			throws RegistryException {
		GetTModelDetailDocument doc = GetTModelDetailDocument.Factory
				.newInstance();
		GetTModelDetail request = doc.addNewGetTModelDetail();

		if (tModelKeyArray != null) {
			request.setTModelKeyArray(tModelKeyArray);
		}

		TModelDetail tmd;
		XmlObject o = execute(doc, this.getInquiryURL()).changeType(
				TModelDetailDocument.type);
		tmd = ((TModelDetailDocument) o).getTModelDetail();

		return tmd;
	}

	/**
	 * @exception RegistryException;
	 */
	public PublisherAssertions setPublisherAssertions(String authInfo,
			PublisherAssertion[] assertionArray) throws RegistryException {
		SetPublisherAssertionsDocument doc = SetPublisherAssertionsDocument.Factory
				.newInstance();
		SetPublisherAssertions request = doc.addNewSetPublisherAssertions();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (assertionArray != null) {
			request.setPublisherAssertionArray(assertionArray);
		}

		PublisherAssertions pa;
		XmlObject o = execute(doc, this.getPublishURL()).changeType(
				PublisherAssertionsDocument.type);
		pa = ((PublisherAssertionsDocument) o).getPublisherAssertions();

		return pa;
	}

	/**
	 * "Used to register new bindingTemplate information or update existing
	 * bindingTemplate information. Use this to control information about
	 * technical capabilities exposed by a registered business."
	 * 
	 * @exception RegistryException;
	 */
	public BindingDetail saveBinding(String authInfo,
			BindingTemplate[] bindingArray) throws RegistryException {
		SaveBindingDocument doc = SaveBindingDocument.Factory.newInstance();
		SaveBinding request = doc.addNewSaveBinding();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (bindingArray != null) {
			request.setBindingTemplateArray(bindingArray);
		}

		BindingDetail bd;
		XmlObject o = execute(doc, this.getPublishURL()).changeType(
				BindingDetailDocument.type);
		bd = ((BindingDetailDocument) o).getBindingDetail();

		return bd;
	}

	/**
	 * "Used to register new businessEntity information or update existing
	 * businessEntity information. Use this to control the overall information
	 * about the entire business. Of the save_x APIs this one has the broadest
	 * effect."
	 * 
	 * @exception RegistryException;
	 */
	public BusinessDetail saveBusiness(String authInfo,
			BusinessEntity[] businessArray) throws RegistryException {
		SaveBusinessDocument doc = SaveBusinessDocument.Factory.newInstance();
		SaveBusiness request = doc.addNewSaveBusiness();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (businessArray != null) {
			request.setBusinessEntityArray(businessArray);
		}

		BusinessDetail bd;
		XmlObject o = execute(doc, this.getPublishURL()).changeType(
				BusinessDetailDocument.type);
		bd = ((BusinessDetailDocument) o).getBusinessDetail();

		return bd;
	}

	/**
	 * "Used to register or update complete information about a businessService
	 * exposed by a specified businessEntity."
	 * 
	 * @exception RegistryException;
	 */
	public ServiceDetail saveService(String authInfo,
			BusinessService[] serviceArray) throws RegistryException {
		SaveServiceDocument doc = SaveServiceDocument.Factory.newInstance();
		SaveService request = doc.addNewSaveService();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (serviceArray != null) {
			request.setBusinessServiceArray(serviceArray);
		}

		ServiceDetail sd;
		XmlObject o = execute(doc, this.getPublishURL()).changeType(
				ServiceDetailDocument.type);
		sd = ((ServiceDetailDocument) o).getServiceDetail();

		return sd;
	}

	/**
	 * "Used to register or update complete information about a tModel."
	 * 
	 * @exception RegistryException;
	 */
	public TModelDetail saveTModel(String authInfo, TModel[] tModelArray)
			throws RegistryException {
		SaveTModelDocument doc = SaveTModelDocument.Factory.newInstance();
		SaveTModel request = doc.addNewSaveTModel();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (tModelArray != null) {
			request.setTModelArray(tModelArray);
		}

		TModelDetail tmd;
		XmlObject o = execute(doc, this.getPublishURL()).changeType(
				TModelDetailDocument.type);
		tmd = ((TModelDetailDocument) o).getTModelDetail();

		return tmd;
	}

	/**
	 * Returns an implementation of Transport based on the className passed in.
	 * If a null value is passed then the default Transport implementation
	 * "org.apache.ws.scout.transport.AxisTransport" is created and returned.
	 * 
	 * @return Transport
	 */
	public Transport getTransport(String className) {
		Transport transport = null;
		Class transportClass = null;

		// If a Transport class name isn't supplied use
		// the default Transport implementation.
		if (className == null)
			className = DEFAULT_TRANSPORT_CLASS;

		try {
			// instruct class loader to load the TransportFactory
			transportClass = getClassForName(className);
		} catch (ClassNotFoundException cnfex) {
			cnfex.printStackTrace();
		}

		try {
			// try to instantiate the TransportFactory
			transport = (Transport) transportClass.newInstance();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}

		return transport;
	}

	/**
	 * 
	 * @param name
	 * @return The class object for the name given
	 * @throws ClassNotFoundException
	 * @throws NoClassDefFoundError
	 */
	public static Class getClassForName(String name)
			throws ClassNotFoundException, NoClassDefFoundError {
		Class clazz = null;

		try {
			// log.info("Using the Context ClassLoader");
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			clazz = Class.forName(name, true, ccl);
		} catch (Exception e) {
			// log.warn("Failed to load the class " + name + " with context
			// class loader " + e);
		}

		if (null == clazz) {
			ClassLoader scl = ClassLoader.getSystemClassLoader();

			try {
				clazz = Class.forName(name, true, scl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return clazz;
	}
}
