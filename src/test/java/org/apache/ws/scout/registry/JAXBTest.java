package org.apache.ws.scout.registry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.scout.model.uddi.v2.AssertionStatusReport;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class JAXBTest {

	private static String PUBLISHER_ASSERTION_RESPONSE="<assertionStatusReport generic=\"2.0\" operator=\"jUDDI.org\" xmlns=\"urn:uddi-org:api_v2\"><assertionStatusItem completionStatus=\"status:fromKey_incomplete\"><fromKey>5173FA70-81E6-11DE-B7B9-A9A7A2431DC4</fromKey><toKey>517AD840-81E6-11DE-B7B9-C5758FAC28A0</toKey><keyedReference keyName=\"Concept\" keyValue=\"Implements\" tModelKey=\"UUID:DB77450D-9FA8-45D4-A7BC-04411D14E384\"/><keysOwned><fromKey>5173FA70-81E6-11DE-B7B9-A9A7A2431DC4</fromKey></keysOwned></assertionStatusItem></assertionStatusReport>";

	/**
	 * Test handling of utf8 characters
	 */
	@Test
	public void unmarshallUTF8()
	{
		try {
			JAXBContext jaxbContext=JAXBContext.newInstance("org.apache.ws.scout.model.uddi.v2");
			Unmarshaller unMarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(PUBLISHER_ASSERTION_RESPONSE);
			JAXBElement<AssertionStatusReport> utf8Element = unMarshaller.unmarshal(new StreamSource(reader),AssertionStatusReport.class);
			List<org.apache.ws.scout.model.uddi.v2.AssertionStatusItem> items =  utf8Element.getValue().getAssertionStatusItem();
			System.out.println(items);
		} catch (JAXBException jaxbe) {
			fail("No exception should be thrown");
		}
	}
	
	@Test
	public void unmarshallElement()
	{
		try {
			JAXBContext jaxbContext=JAXBContext.newInstance("org.apache.ws.scout.model.uddi.v2");
			Unmarshaller unMarshaller = jaxbContext.createUnmarshaller();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
		    DocumentBuilder db = factory.newDocumentBuilder();
		    InputSource is = new InputSource();
		    is.setCharacterStream(new StringReader(PUBLISHER_ASSERTION_RESPONSE));
		    Document document = db.parse(is);
		    
			JAXBElement<AssertionStatusReport> utf8Element = unMarshaller.unmarshal(document,AssertionStatusReport.class);
			List<org.apache.ws.scout.model.uddi.v2.AssertionStatusItem> items =  utf8Element.getValue().getAssertionStatusItem();
			System.out.println(items);
		} catch (Exception jaxbe) {
			fail("No exception should be thrown");
		}
	}
	
	
	


}
