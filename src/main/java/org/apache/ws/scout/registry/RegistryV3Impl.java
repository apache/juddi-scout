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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.scout.transport.Transport;
import org.apache.ws.scout.transport.TransportException;
import org.apache.ws.scout.util.XMLUtils;
import org.uddi.api_v3.AssertionStatusReport;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.DiscoveryURLs;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetAssertionStatusReport;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetPublisherAssertions;
import org.uddi.api_v3.GetRegisteredInfo;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ObjectFactory;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.PublisherAssertions;
import org.uddi.api_v3.PublisherAssertionsResponse;
import org.uddi.api_v3.RegisteredInfo;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.SetPublisherAssertions;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelBag;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelList;
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

public class RegistryV3Impl implements IRegistryV3 {

	public static final String INQUIRY_ENDPOINT_PROPERTY_NAME = "scout.proxy.inquiryURL";
	public static final String PUBLISH_ENDPOINT_PROPERTY_NAME = "scout.proxy.publishURL";
	public static final String SECURITY_ENDPOINT_PROPERTY_NAME = "scout.proxy.securityURL";
	public static final String ADMIN_ENDPOINT_PROPERTY_NAME = "scout.proxy.adminURL";
	public static final String TRANSPORT_CLASS_PROPERTY_NAME = "scout.proxy.transportClass";
	public static final String SECURITY_PROVIDER_PROPERTY_NAME = "scout.proxy.securityProvider";
	public static final String PROTOCOL_HANDLER_PROPERTY_NAME = "scout.proxy.protocolHandler";
	public static final String UDDI_VERSION_PROPERTY_NAME = "scout.proxy.uddiVersion";
	public static final String UDDI_NAMESPACE_PROPERTY_NAME = "scout.proxy.uddiNamespace";

	public static final String DEFAULT_INQUIRY_ENDPOINT = "http://localhost/juddi/inquiry";
	public static final String DEFAULT_PUBLISH_ENDPOINT = "http://localhost/juddi/publish";
	public static final String DEFAULT_SECURITY_ENDPOINT = "http://localhost/juddi/security";
	public static final String DEFAULT_ADMIN_ENDPOINT = "http://localhost/juddi/admin";
	public static final String DEFAULT_TRANSPORT_CLASS = "org.apache.ws.scout.transport.AxisTransport";
	public static final String DEFAULT_SECURITY_PROVIDER = "com.sun.net.ssl.internal.ssl.Provider";
	public static final String DEFAULT_PROTOCOL_HANDLER = "com.sun.net.ssl.internal.www.protocol";
	public static final String DEFAULT_UDDI_VERSION = "2.0";
	public static final String DEFAULT_UDDI_NAMESPACE = "urn:uddi-org:api_v2";

	private URI adminURI;
	private URI inquiryURI;
	private URI publishURI;
	private URI securityURI;

	private Transport transport;

	private String securityProvider;
	private String protocolHandler;
	private String uddiVersion;
	private String uddiNamespace;
	
	private ObjectFactory objectFactory = new ObjectFactory();
	
	private Marshaller marshaller = null;
	private Unmarshaller unmarshaller = null;
	
	private static Log log = LogFactory.getLog(RegistryV3Impl.class);

	/**
	 * Creates a new instance of RegistryImpl.
	 */
	public RegistryV3Impl(Properties props) {
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

			String sURL = props.getProperty(SECURITY_ENDPOINT_PROPERTY_NAME);
			if (sURL != null)
				this.setSecurityURI(new URI(sURL));
			else
				this.setSecurityURI(new URI(DEFAULT_SECURITY_ENDPOINT));
			
			String aURL = props.getProperty(ADMIN_ENDPOINT_PROPERTY_NAME);
			if (aURL != null)
				this.setAdminURI(new URI(aURL));
			else
				this.setAdminURI(new URI(DEFAULT_ADMIN_ENDPOINT));
		} catch (URISyntaxException muex) {
			throw new RuntimeException(muex);
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
		
		try
		{
			JAXBContext v3context = JAXBContextUtil.getContext(JAXBContextUtil.UDDI_V3_VERSION);
			this.unmarshaller = v3context.createUnmarshaller(); 
			this.marshaller = v3context.createMarshaller();

		}
		catch(JAXBException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param uddiRequest
	 * @return String
	 * @throws RegistryV3Exception
	 */
	public String execute(String uddiRequest, String urltype)
			throws TransportException {
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
	public JAXBElement<?> execute(JAXBElement<?> uddiRequest, URI endPointURI)
			throws RegistryV3Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
        Document doc;
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder= docBuilderFactory.newDocumentBuilder();
            this.marshaller.marshal(uddiRequest, baos);
            doc = docBuilder.parse(new ByteArrayInputStream(baos.toByteArray()));
        } catch (SAXException saxe) {
            throw (new RegistryV3Exception(saxe));
        } catch (ParserConfigurationException pce) {
            throw (new RegistryV3Exception(pce));
        } catch (IOException ioe) {
            throw (new RegistryV3Exception(ioe));
        } catch (JAXBException ioe) {
            throw (new RegistryV3Exception(ioe));
        }
		Element request = doc.getDocumentElement();

	    request.setAttribute("xmlns", this.getUddiNamespace());
	    if (!"3.0".equals(this.getUddiVersion())) {
	    	request.setAttribute("generic", this.getUddiVersion());
	    }
	    //request.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns", this.getUddiNamespace());
	    // A SOAP request is made and a SOAP response
	    // is returned.

	    Element response;
	    try {
	    	response = transport.send(request, endPointURI);
	    } catch (TransportException te) {
	    	throw new RegistryV3Exception(te);
	    }
	    /* if (response.hasAttributes()) {
		    NamedNodeMap am = response.getAttributes();
		    ArrayList<String> al = new ArrayList<String>();
		    for (int i = 0; i < am.getLength(); i++) {
		    	Node n = am.item(i);
		    	String attribute = n.getNodeName();
		    	if (attribute!= null && attribute.startsWith("xmlns")) {
		    		al.add(attribute);
		    	}
		    }
		    for (String attr : al) {
		    	response.removeAttribute(attr);
		    }
	    }*/

	    if (response.getNamespaceURI()==null) {
            response.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns", this.getUddiNamespace());
        }
	    
	    // If we are getting responses from a UDDI v3, remove the xmlns
	    
	    // First, let's make sure that a response
	    // (any response) is found in the SOAP Body.

	    String responseName = response.getLocalName();
	    if (responseName == null) {
	        throw new RegistryV3Exception("Unsupported response "
	                + "from registry. A value was not present.");
		} 
	    
        // Let's now try to determine which UDDI response
        // we received and unmarshal it appropriately or
        // throw a RegistryV3Exception if it's unknown.
        // Well, we have now determined that something was
        // returned and it is "a something" that we know
        // about so let's unmarshal it into a RegistryObject
        // Next, let's make sure we didn't recieve a SOAP
        // Fault. If it is a SOAP Fault then throw it
        // immediately.

        JAXBElement<?> uddiResponse = null;
	    try {
	    	String xml = XMLUtils.convertNodeToXMLString(response);
	        log.debug("Response is: " + xml);
	    	
		StringReader reader = new StringReader(xml);
		uddiResponse = (JAXBElement<?>) unmarshaller.unmarshal(new StreamSource(reader));
	    	//It is probably faster not to go to a String, but JAXB has issues with this
	        //uddiResponse = (JAXBElement<?>) unmarshaller.unmarshal(response);

	    } catch (JAXBException xmle) {
	        throw (new RegistryV3Exception(xmle));
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
					JAXBElement<DispositionReport> dispRptObj = null;
					try {
						dispRptObj = (JAXBElement<DispositionReport>) unmarshaller.unmarshal((Element) nodeList
								.item(0));
					} catch (JAXBException xmle) {
						throw (new RegistryV3Exception(xmle));
					}
                    dispRpt = dispRptObj.getValue();
                }
			}

			RegistryV3Exception e = new RegistryV3Exception(fCode, fString, fActor, dispRpt);
		
			// Create RegistryV3Exception instance and return
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
	 * @return Returns the publishURL.
	 */
	public URI getSecurityURI() {
		return this.securityURI;
	}

	/**
	 * @param publishURI
	 *            The publishURI to set.
	 */
	public void setPublishURI(URI publishURI) {
		this.publishURI = publishURI;
	}
	
	/**
	 * @param publishURI
	 *            The publishURI to set.
	 */
	public void setSecurityURI(URI securityURI) {
		this.securityURI = securityURI;
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
	 * @exception RegistryV3Exception;
	 */
	public DispositionReport deleteBinding(String authInfo,
			String[] bindingKeyArray) throws RegistryV3Exception {
		DeleteBinding request = this.objectFactory.createDeleteBinding();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (bindingKeyArray != null) {
			request.getBindingKey().addAll(Arrays.asList(bindingKeyArray));
		}

        DispositionReport dr;
        JAXBElement<?> o = execute(this.objectFactory.createDeleteBinding(request), this.getPublishURI());
        dr = (DispositionReport) o.getValue();

        return dr;
	}

	/**
	 * "Used to delete registered businessEntity information from the registry."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public DispositionReport deleteBusiness(String authInfo,
			String[] businessKeyArray) throws RegistryV3Exception {
		DeleteBusiness request = this.objectFactory.createDeleteBusiness();
		
		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (businessKeyArray != null) {
			request.getBusinessKey().addAll(Arrays.asList(businessKeyArray));
		}

        DispositionReport dr;
        JAXBElement<?> o = execute(this.objectFactory.createDeleteBusiness(request), this.getPublishURI());
        dr = (DispositionReport) o.getValue();

        return dr;
	}

	/**
	 * @exception RegistryV3Exception;
	 */
	public DispositionReport deletePublisherAssertions(String authInfo,
			PublisherAssertion[] assertionArray) throws RegistryV3Exception {
		DeletePublisherAssertions request = this.objectFactory.createDeletePublisherAssertions();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (assertionArray != null) {
			request.getPublisherAssertion().addAll(Arrays.asList(assertionArray));
		}

        DispositionReport dr;
        JAXBElement<?> o = execute(this.objectFactory.createDeletePublisherAssertions(request), 
        		this.getPublishURI());
        dr = (DispositionReport) o.getValue();

        return dr;
	}

	/**
	 * "Used to delete an existing businessService from the businessServices
	 * collection that is part of a specified businessEntity."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public DispositionReport deleteService(String authInfo,
			String[] serviceKeyArray) throws RegistryV3Exception {
		DeleteService request = this.objectFactory.createDeleteService();
		
		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (serviceKeyArray != null) {
			request.getServiceKey().addAll(Arrays.asList(serviceKeyArray));
		}

        DispositionReport dr;
        JAXBElement<?> o = execute(this.objectFactory.createDeleteService(request), 
        		this.getPublishURI());
        dr = (DispositionReport) o.getValue();

        return dr;
	}

	/**
	 * "Used to delete registered information about a tModel. If there are any
	 * references to a tModel when this call is made, the tModel will be marked
	 * deleted instead of being physically removed."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public DispositionReport deleteTModel(String authInfo,
			String[] tModelKeyArray) throws RegistryV3Exception {
		DeleteTModel request = this.objectFactory.createDeleteTModel();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (tModelKeyArray != null) {
			request.getTModelKey().addAll(Arrays.asList(tModelKeyArray));
		}

        DispositionReport dr;
        JAXBElement<?> o = execute(this.objectFactory.createDeleteTModel(request), 
        		this.getPublishURI());
        dr = (DispositionReport) o.getValue();

        return dr;
	}

	/**
	 * Used to locate information about one or more businesses. Returns a
	 * businessList message that matches the conditions specified.
	 * 
	 * @exception RegistryV3Exception;
	 */
	public BusinessList findBusiness(Name[] nameArray,
			DiscoveryURLs discoveryURLs, IdentifierBag identifierBag,
			CategoryBag categoryBag, TModelBag tModelBag,
			FindQualifiers findQualifiers, int maxRows)
			throws RegistryV3Exception {
		FindBusiness request = this.objectFactory.createFindBusiness();

		if (nameArray != null) {
			request.getName().addAll(Arrays.asList(nameArray));
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
        JAXBElement<?> o = execute(this.objectFactory.createFindBusiness(request),
        		this.getInquiryURI());
        bl = (BusinessList) o.getValue();

        return bl;
	}

	/**
	 * "Used to locate specific bindings within a registered businessService.
	 * Returns a bindingDetail message."
	 * 
	 * @exception RegistryV3Exception
	 */
	public BindingDetail findBinding(String serviceKey,
			CategoryBag categoryBag, TModelBag tModelBag,
			FindQualifiers findQualifiers, int maxRows)
			throws RegistryV3Exception {
		// FIXME: Juddi's methods also set category bag (per uddi spec v3).
		// However, we are sticking to v2 for now, so categorybag doesn't
		// exist under FindBinding. It is fine for now, since the incoming
		// parameter value is always null anyways -- but this may change
		// in the future.

		FindBinding request = this.objectFactory.createFindBinding();

		if (serviceKey != null) {
			request.setServiceKey(serviceKey);
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

        BindingDetail bd;
        JAXBElement<?> o = execute(this.objectFactory.createFindBinding(request), 
        		this.getInquiryURI());
        bd = (BindingDetail) o.getValue();

        return bd;
	}

	/**
	 * "Used to locate specific services within a registered businessEntity.
	 * Return a serviceList message." From the XML spec (API, p18) it appears
	 * that the name, categoryBag, and tModelBag arguments are mutually
	 * exclusive.
	 * 
	 * @exception RegistryV3Exception;
	 */
	public ServiceList findService(String businessKey, Name[] nameArray,
			CategoryBag categoryBag, TModelBag tModelBag,
			FindQualifiers findQualifiers, int maxRows)
			throws RegistryV3Exception {
		FindService request = this.objectFactory.createFindService();

		if (businessKey != null) {
			request.setBusinessKey(businessKey);
		}

		if (nameArray != null) {
			request.getName().addAll(Arrays.asList(nameArray));
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
        JAXBElement<?> o = execute(this.objectFactory.createFindService(request), 
        		this.getInquiryURI());
        sl = (ServiceList) o.getValue();

        return sl;
	}

	/**
	 * "Used to locate one or more tModel information structures. Returns a
	 * tModelList structure."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public TModelList findTModel(String name, CategoryBag categoryBag,
			IdentifierBag identifierBag, FindQualifiers findQualifiers,
			int maxRows) throws RegistryV3Exception {
		FindTModel request = this.objectFactory.createFindTModel();

		Name jaxbName = this.objectFactory.createName();

		if (name != null) {
			jaxbName.setValue(name);
		}

		request.setName(jaxbName);

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
        JAXBElement<?> o = execute(this.objectFactory.createFindTModel(request), 
        		this.getInquiryURI());
        tml = (TModelList) o.getValue();

        return tml;
	}

	/**
	 * @exception RegistryV3Exception;
	 */
	public AssertionStatusReport getAssertionStatusReport(String authInfo,
			String completionStatus) throws RegistryV3Exception {
		GetAssertionStatusReport request = this.objectFactory.createGetAssertionStatusReport();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (completionStatus != null) {
			CompletionStatus cs = CompletionStatus.fromValue(completionStatus);
			request.setCompletionStatus(cs);
		}

        AssertionStatusReport asr;
        JAXBElement<?> o = execute(this.objectFactory.createGetAssertionStatusReport(request), 
        		this.getPublishURI());
        asr = (AssertionStatusReport) o.getValue();

        return asr;
	}

	/**
	 * "Used to request an authentication token from an Operator Site.
	 * Authentication tokens are required to use all other APIs defined in the
	 * publishers API. This server serves as the program's equivalent of a login
	 * request."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public AuthToken getAuthToken(String userID, String cred)
			throws RegistryV3Exception {
		GetAuthToken request = this.objectFactory.createGetAuthToken();

		if (userID != null) {
			request.setUserID(userID);
		}

		if (cred != null) {
			request.setCred(cred);
		}

		URI getAuthTokenURI = null;
		if ("3.0".equals(uddiVersion)) {
			getAuthTokenURI = this.getSecurityURI();
		} else {
			getAuthTokenURI = this.getPublishURI();
		}
		
        AuthToken at;
        JAXBElement<?> o = execute(this.objectFactory.createGetAuthToken(request), 
        		getAuthTokenURI);
        at = (AuthToken) o.getValue();

        return at;
	}

	/**
	 * Used to get the full businessEntity information for a particular business
	 * entity. Returns a businessDetail message.
	 * 
	 * @exception RegistryV3Exception;
	 */
	public BusinessDetail getBusinessDetail(String businessKey)
			throws RegistryV3Exception {
		String[] keys = new String[1];
		keys[0] = businessKey;

		return getBusinessDetail(keys);
	}

	/**
	 * "Used to get the full businessEntity information for one or more
	 * businesses. Returns a businessDetail message."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public BusinessDetail getBusinessDetail(String[] businessKeyArray)
			throws RegistryV3Exception {
		GetBusinessDetail request = this.objectFactory.createGetBusinessDetail();

		if (businessKeyArray != null) {
			request.getBusinessKey().addAll(Arrays.asList(businessKeyArray));
		}

        BusinessDetail bd;
        JAXBElement<?> o = execute(this.objectFactory.createGetBusinessDetail(request), 
        		this.getInquiryURI());
        bd = (BusinessDetail) o.getValue(); 
        return bd;
	}

	/**
	 * @exception RegistryV3Exception;
	 */
	public PublisherAssertions getPublisherAssertions(String authInfo)
			throws RegistryV3Exception {
		GetPublisherAssertions request = this.objectFactory.createGetPublisherAssertions();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

        PublisherAssertions pa = new PublisherAssertions();
        JAXBElement<?> o = execute(this.objectFactory.createGetPublisherAssertions(request),
        		this.getPublishURI());
        PublisherAssertionsResponse par = (PublisherAssertionsResponse) o.getValue();
        List<PublisherAssertion> assertions = par.getPublisherAssertion();
        for (int i = 0; i < assertions.size(); i++ ) {
        	pa.getPublisherAssertion().add((PublisherAssertion)assertions.get(i));
        }

        return pa;
	}

	/**
	 * @exception RegistryV3Exception;
	 */
	public RegisteredInfo getRegisteredInfo(String authInfo)
			throws RegistryV3Exception {
		GetRegisteredInfo request = this.objectFactory.createGetRegisteredInfo();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

        RegisteredInfo ri;
        JAXBElement<?> o = execute(this.objectFactory.createGetRegisteredInfo(request), 
        		this.getPublishURI());
        ri = (RegisteredInfo) o.getValue();

        return ri;
	}
	
	/**
	 * "Used to get full details for a particular registered businessService.
	 * Returns a serviceDetail message."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public ServiceDetail getServiceDetail(String serviceKey)
			throws RegistryV3Exception {
		String[] keys = new String[1];
		keys[0] = serviceKey;

		return getServiceDetail(keys);
	}

	/**
	 * "Used to get full details for a given set of registered businessService
	 * data. Returns a serviceDetail message."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public ServiceDetail getServiceDetail(String[] serviceKeyArray)
			throws RegistryV3Exception {
		GetServiceDetail request = this.objectFactory.createGetServiceDetail();

		if (serviceKeyArray != null) {
			request.getServiceKey().addAll(Arrays.asList(serviceKeyArray));
		}

        ServiceDetail sd;
        JAXBElement<?> o = execute(this.objectFactory.createGetServiceDetail(request), 
        		this.getInquiryURI());
        sd = (ServiceDetail) o.getValue();

        return sd;
	}

	/**
	 * "Used to get full details for a particular registered TModel. Returns a
	 * tModelDetail message."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public TModelDetail getTModelDetail(String tModelKey)
			throws RegistryV3Exception {
		String[] keys = new String[1];
		keys[0] = tModelKey;

		return getTModelDetail(keys);
	}

	/**
	 * "Used to get full details for a given set of registered tModel data.
	 * Returns a tModelDetail message."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public TModelDetail getTModelDetail(String[] tModelKeyArray)
			throws RegistryV3Exception {
		GetTModelDetail request = this.objectFactory.createGetTModelDetail();

		if (tModelKeyArray != null) {
			request.getTModelKey().addAll(Arrays.asList(tModelKeyArray));
		}

        TModelDetail tmd;
        JAXBElement<?> o = execute(this.objectFactory.createGetTModelDetail(request), 
        		this.getInquiryURI());
        tmd = (TModelDetail) o.getValue();

        return tmd;
	}

	/**
	 * @exception RegistryV3Exception;
	 */
	public PublisherAssertions setPublisherAssertions(String authInfo,
			PublisherAssertion[] assertionArray) throws RegistryV3Exception {
		SetPublisherAssertions request = this.objectFactory.createSetPublisherAssertions();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (assertionArray != null) {
			request.getPublisherAssertion().addAll(Arrays.asList(assertionArray));
		}

        PublisherAssertions pa;
        JAXBElement<?> o = execute(this.objectFactory.createSetPublisherAssertions(request), 
        		this.getPublishURI());
        pa = (PublisherAssertions) o.getValue();

        return pa;
	}

	/**
	 * "Used to register new bindingTemplate information or update existing
	 * bindingTemplate information. Use this to control information about
	 * technical capabilities exposed by a registered business."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public BindingDetail saveBinding(String authInfo,
			BindingTemplate[] bindingArray) throws RegistryV3Exception {
		SaveBinding request = this.objectFactory.createSaveBinding();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (bindingArray != null) {
			request.getBindingTemplate().addAll(Arrays.asList(bindingArray));
		}
		
        BindingDetail bd;
        JAXBElement<?> o = execute(this.objectFactory.createSaveBinding(request), 
        		this.getPublishURI());
        bd = (BindingDetail) o.getValue();

        return bd;
	}

	/**
	 * "Used to register new businessEntity information or update existing
	 * businessEntity information. Use this to control the overall information
	 * about the entire business. Of the save_x APIs this one has the broadest
	 * effect."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public BusinessDetail saveBusiness(String authInfo,
			BusinessEntity[] businessArray) throws RegistryV3Exception {
		SaveBusiness request = this.objectFactory.createSaveBusiness();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}
			
		if (businessArray != null) {
			for (int i = 0; i < businessArray.length; i++) {
				BusinessEntity be = businessArray[i];
				if (be.getBusinessServices().getBusinessService().size() == 0) {
					be.setBusinessServices(null);
				}
			}

			request.getBusinessEntity().addAll(Arrays.asList(businessArray));
		}
		
        BusinessDetail bd;
        JAXBElement<?> o = execute(this.objectFactory.createSaveBusiness(request), 
        		this.getPublishURI());
        bd = (BusinessDetail) o.getValue();

        return bd;
	}

	/**
	 * "Used to register or update complete information about a businessService
	 * exposed by a specified businessEntity."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public ServiceDetail saveService(String authInfo,
			BusinessService[] serviceArray) throws RegistryV3Exception {
		SaveService request = this.objectFactory.createSaveService();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (serviceArray != null) {
			request.getBusinessService().addAll(Arrays.asList(serviceArray));
		}

        ServiceDetail sd;
        JAXBElement<?> o = execute(this.objectFactory.createSaveService(request), 
        		this.getPublishURI());
        sd = (ServiceDetail) o.getValue();

        return sd;
	}

	/**
	 * "Used to register or update complete information about a tModel."
	 * 
	 * @exception RegistryV3Exception;
	 */
	public TModelDetail saveTModel(String authInfo, TModel[] tModelArray)
			throws RegistryV3Exception {
		SaveTModel request = this.objectFactory.createSaveTModel();

		if (authInfo != null) {
			request.setAuthInfo(authInfo);
		}

		if (tModelArray != null) {
			request.getTModel().addAll(Arrays.asList(tModelArray));
		}

        TModelDetail tmd;
        JAXBElement<?> o = execute(this.objectFactory.createSaveTModel(request), 
        		this.getPublishURI());
        tmd = (TModelDetail) o.getValue();
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
			throw new RuntimeException(cnfex);
		}

		try {
			// try to instantiate the TransportFactory
			transport = (Transport) transportClass.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
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
			ClassLoader ccl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() 
		    {
				public ClassLoader run() {
					return Thread.currentThread().getContextClassLoader();
		        }
			});
			
			clazz = Class.forName(name, true, ccl);
		} catch (Exception e) {
			 log.debug("Failed to load the class " + name + " with context class loader " + e);
		}

		if (null == clazz) {
			ClassLoader scl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>()
			{ 
				public ClassLoader run() {
					return ClassLoader.getSystemClassLoader();
				}
			});

			try {
				clazz = Class.forName(name, true, scl);
			} catch (Exception e) {
		          throw new RuntimeException(e);
			}
		}

		return clazz;
	}
}
