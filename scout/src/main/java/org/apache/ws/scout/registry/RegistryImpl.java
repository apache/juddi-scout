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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.scout.transport.Transport;
import org.apache.ws.scout.uddi.AssertionStatusReport;
import org.apache.ws.scout.uddi.AssertionStatusReportDocument;
import org.apache.ws.scout.uddi.AuthToken;
import org.apache.ws.scout.uddi.AuthTokenDocument;
import org.apache.ws.scout.uddi.BindingDetail;
import org.apache.ws.scout.uddi.BindingDetailDocument;
import org.apache.ws.scout.uddi.BindingTemplate;
import org.apache.ws.scout.uddi.BusinessDetail;
import org.apache.ws.scout.uddi.BusinessDetailDocument;
import org.apache.ws.scout.uddi.BusinessEntity;
import org.apache.ws.scout.uddi.BusinessList;
import org.apache.ws.scout.uddi.BusinessListDocument;
import org.apache.ws.scout.uddi.BusinessService;
import org.apache.ws.scout.uddi.CategoryBag;
import org.apache.ws.scout.uddi.DeleteBinding;
import org.apache.ws.scout.uddi.DeleteBindingDocument;
import org.apache.ws.scout.uddi.DeleteBusiness;
import org.apache.ws.scout.uddi.DeleteBusinessDocument;
import org.apache.ws.scout.uddi.DeletePublisherAssertions;
import org.apache.ws.scout.uddi.DeletePublisherAssertionsDocument;
import org.apache.ws.scout.uddi.DeleteService;
import org.apache.ws.scout.uddi.DeleteServiceDocument;
import org.apache.ws.scout.uddi.DeleteTModel;
import org.apache.ws.scout.uddi.DeleteTModelDocument;
import org.apache.ws.scout.uddi.DiscoveryURLs;
import org.apache.ws.scout.uddi.DispositionReport;
import org.apache.ws.scout.uddi.DispositionReportDocument;
import org.apache.ws.scout.uddi.FindBinding;
import org.apache.ws.scout.uddi.FindBindingDocument;
import org.apache.ws.scout.uddi.FindBusiness;
import org.apache.ws.scout.uddi.FindBusinessDocument;
import org.apache.ws.scout.uddi.FindQualifiers;
import org.apache.ws.scout.uddi.FindService;
import org.apache.ws.scout.uddi.FindServiceDocument;
import org.apache.ws.scout.uddi.FindTModel;
import org.apache.ws.scout.uddi.FindTModelDocument;
import org.apache.ws.scout.uddi.GetAssertionStatusReport;
import org.apache.ws.scout.uddi.GetAssertionStatusReportDocument;
import org.apache.ws.scout.uddi.GetAuthToken;
import org.apache.ws.scout.uddi.GetAuthTokenDocument;
import org.apache.ws.scout.uddi.GetBusinessDetail;
import org.apache.ws.scout.uddi.GetBusinessDetailDocument;
import org.apache.ws.scout.uddi.GetPublisherAssertions;
import org.apache.ws.scout.uddi.GetPublisherAssertionsDocument;
import org.apache.ws.scout.uddi.GetServiceDetail;
import org.apache.ws.scout.uddi.GetServiceDetailDocument;
import org.apache.ws.scout.uddi.GetTModelDetail;
import org.apache.ws.scout.uddi.GetTModelDetailDocument;
import org.apache.ws.scout.uddi.IdentifierBag;
import org.apache.ws.scout.uddi.Name;
import org.apache.ws.scout.uddi.PublisherAssertion;
import org.apache.ws.scout.uddi.PublisherAssertions;
import org.apache.ws.scout.uddi.PublisherAssertionsDocument;
import org.apache.ws.scout.uddi.SaveBinding;
import org.apache.ws.scout.uddi.SaveBindingDocument;
import org.apache.ws.scout.uddi.SaveBusiness;
import org.apache.ws.scout.uddi.SaveBusinessDocument;
import org.apache.ws.scout.uddi.SaveService;
import org.apache.ws.scout.uddi.SaveServiceDocument;
import org.apache.ws.scout.uddi.SaveTModel;
import org.apache.ws.scout.uddi.SaveTModelDocument;
import org.apache.ws.scout.uddi.ServiceDetail;
import org.apache.ws.scout.uddi.ServiceDetailDocument;
import org.apache.ws.scout.uddi.ServiceList;
import org.apache.ws.scout.uddi.ServiceListDocument;
import org.apache.ws.scout.uddi.SetPublisherAssertions;
import org.apache.ws.scout.uddi.SetPublisherAssertionsDocument;
import org.apache.ws.scout.uddi.TModel;
import org.apache.ws.scout.uddi.TModelBag;
import org.apache.ws.scout.uddi.TModelDetail;
import org.apache.ws.scout.uddi.TModelDetailDocument;
import org.apache.ws.scout.uddi.TModelList;
import org.apache.ws.scout.uddi.TModelListDocument;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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

	private URI adminURI;
	private URI inquiryURI;
	private URI publishURI;

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
				this.setInquiryURI(new URI(iURL));
			else
				this.setInquiryURI(new URI(DEFAULT_INQUIRY_ENDPOINT));

			String pURL = props.getProperty(PUBLISH_ENDPOINT_PROPERTY_NAME);
			if (pURL != null)
				this.setPublishURI(new URI(pURL));
			else
				this.setPublishURI(new URI(DEFAULT_PUBLISH_ENDPOINT));

			String aURL = props.getProperty(ADMIN_ENDPOINT_PROPERTY_NAME);
			if (aURL != null)
				this.setAdminURI(new URI(aURL));
			else
				this.setAdminURI(new URI(DEFAULT_ADMIN_ENDPOINT));
		} catch (URISyntaxException muex) {
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
		URI endPointURL = null;
		if (urltype.equalsIgnoreCase("INQUIRY"))
			endPointURL = this.getInquiryURI();
		else
			endPointURL = this.getPublishURI();

		// A SOAP request is made and a SOAP response
		// is returned.

		return transport.send(uddiRequest, endPointURL);
	}

	/**
	 * 
	 */
	public XmlObject execute(XmlObject uddiRequest, URI endPointURI)
			throws RegistryException {

        Document doc;
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder= docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse(uddiRequest.newInputStream());
        } catch (SAXException saxe) {
            throw (new RegistryException(saxe));
        } catch (ParserConfigurationException pce) {
            throw (new RegistryException(pce));
        } catch (IOException ioe) {
            throw (new RegistryException(ioe));
        }
		Element request = doc.getDocumentElement();

	    request.setAttribute("generic", this.getUddiVersion());
	    //request.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns", this.getUddiNamespace());
	    // A SOAP request is made and a SOAP response
	    // is returned.

	    Element response = transport.send(request, endPointURI);
        
        if (response.getNamespaceURI()==null) {
            response.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns", this.getUddiNamespace());
        }
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
            XmlCursor cursor = uddiResponse.newCursor();
            cursor.toNextToken();
            //set the namespace if it is empty here.  This is needed for the find_element_user to work.
            if ("".equals(cursor.getName().getNamespaceURI())) {
                cursor.setName(new QName(this.getUddiNamespace(), cursor.getName().getLocalPart()));
                //there seems to have a bug in setName and it will set the next Start with xmlns="".
                //The workaround is to set it to uddiNamespace when it is empty.
                while (cursor.hasNextToken()) {
                    cursor.toNextToken();
                    if (cursor.isStart()) {
                        if ("".equals(cursor.getName().getNamespaceURI())) {
                            cursor.setName(new QName(this.getUddiNamespace(), cursor.getName().getLocalPart()));
                        }
                    }
                }
                cursor.dispose();
            }
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

			DispositionReport dispRpt = null;

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
	public URI getAdminURI() {
		return this.adminURI;
	}

	/**
	 * @param url
	 *            The adminURL to set.
	 */
	public void setAdminURI(URI url) {
		this.adminURI = url;
	}

	/**
	 * @return Returns the inquiryURL.
	 */
	public URI getInquiryURI() {
		return this.inquiryURI;
	}

	/**
	 * @param inquiryURI
	 *            The inquiryURI to set.
	 */
	public void setInquiryURI(URI inquiryURI) {
		this.inquiryURI = inquiryURI;
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
	public void setProtocolHandler(String protocolHandler) {
		this.protocolHandler = protocolHandler;
	}

	/**
	 * @return Returns the publishURL.
	 */
	public URI getPublishURI() {
		return this.publishURI;
	}

	/**
	 * @param publishURI
	 *            The publishURI to set.
	 */
	public void setPublishURI(URI publishURI) {
		this.publishURI = publishURI;
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
	public void setSecurityProvider(String securityProvider) {
		this.securityProvider = securityProvider;
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
	public void setUddiVersion(String uddiVersion) {
		this.uddiVersion = uddiVersion;
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
        XmlObject o = execute(doc, this.getPublishURI()).changeType(
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
        XmlObject o = execute(doc, this.getPublishURI()).changeType(
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
        XmlObject o = execute(doc, this.getPublishURI()).changeType(
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
        XmlObject o = execute(doc, this.getPublishURI()).changeType(
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
        XmlObject o = execute(doc, this.getPublishURI()).changeType(
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
		} else {
			request.setTModelBag(TModelBag.Factory.newInstance());
 		}

		if (findQualifiers != null) {
			request.setFindQualifiers(findQualifiers);
		}

		request.setMaxRows(maxRows);

        BusinessList bl;
        XmlObject o = execute(doc, this.getInquiryURI()).changeType(
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
		} else {
			TModelBag tmb = TModelBag.Factory.newInstance();
			tmb.setTModelKeyArray(new String[1]);
			request.setTModelBag(tmb);
 		}

		if (findQualifiers != null) {
			request.setFindQualifiers(findQualifiers);
		}
		request.setMaxRows(maxRows);

        BindingDetail bd;
        XmlObject o = execute(doc, this.getInquiryURI()).changeType(
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
        XmlObject o = execute(doc, this.getInquiryURI()).changeType(
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
        XmlObject o = execute(doc, this.getInquiryURI()).changeType(
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
        XmlObject o = execute(doc, this.getPublishURI()).changeType(
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
        XmlObject o = execute(doc, this.getPublishURI()).changeType(
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
        XmlObject o = execute(doc, this.getInquiryURI()).changeType(
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
        XmlObject o = execute(doc, this.getPublishURI()).changeType(
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
        XmlObject o = execute(doc, this.getInquiryURI()).changeType(
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
        XmlObject o = execute(doc, this.getInquiryURI()).changeType(
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
        XmlObject o = execute(doc, this.getPublishURI()).changeType(
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
        XmlObject o = execute(doc, this.getPublishURI()).changeType(
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
        XmlObject o = execute(doc, this.getPublishURI()).changeType(
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
        XmlObject o = execute(doc, this.getPublishURI()).changeType(
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
        XmlObject o = execute(doc, this.getPublishURI()).changeType(
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
			 //log.warn("Failed to load the class " + name + " with context
			 //class loader " + e);
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
