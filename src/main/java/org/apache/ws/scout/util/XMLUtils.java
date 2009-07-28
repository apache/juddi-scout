package org.apache.ws.scout.util;

import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

/**
 * Utilies for covertion between w3c formats and XML.
 * @author Kurt Stam (kurt.stam@jboss.com)
 */
public class XMLUtils {
	private static Log log = LogFactory.getLog(XMLUtils.class);
	
	/**
	 * Convert a org.w3c.dom.Node into an XML representation.
	 * @param node - containing an xml tree.
	 * @return - String containing the XML.
	 */
	public static String convertNodeToXMLString(Node node)
	  {
		  String xml=null;
		  try {
		      Source source = new DOMSource(node);
		      StringWriter stringWriter = new StringWriter();
		      javax.xml.transform.Result result = new StreamResult(stringWriter);
		      TransformerFactory factory = TransformerFactory.newInstance();
		      Transformer transformer = factory.newTransformer();
		      transformer.transform(source, result);
		      xml=stringWriter.getBuffer().toString();
		  } catch (TransformerConfigurationException e) {
		      log.error("Could not obtain the XML. ", e);
		  } catch (TransformerException e) {
			  log.error("Could not obtain the XML. ",e);
		  }
		  return xml;
	  }
}
