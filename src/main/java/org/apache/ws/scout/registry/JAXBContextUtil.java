package org.apache.ws.scout.registry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JAXBContextUtil {

	public static final String UDDI_V2_VERSION = "2.0";
	public static final String UDDI_V3_VERSION = "3.0";
	
	private static Log log = LogFactory.getLog(JAXBContextUtil.class);
	private static final Map<String, JAXBContext> JAXBContexts = new HashMap<String, JAXBContext>();

	static {
		try {
			JAXBContexts.put(UDDI_V2_VERSION, JAXBContext.newInstance(new Class[] {org.apache.ws.scout.model.uddi.v2.ObjectFactory.class}));
			JAXBContexts.put(UDDI_V3_VERSION, JAXBContext.newInstance(new Class[] {org.uddi.api_v3.ObjectFactory.class}));
		} catch (JAXBException e) {
			log.error("Initialization of JAXBMarshaller failed:" + e, e);
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static JAXBContext getContext(String uddiVersion) {
		return JAXBContexts.get(uddiVersion);
	}
	
}
