package org.purc.purcforms.server.util;

import java.util.Vector;

import org.purc.purcforms.server.xpath.XPathExpression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 
 * @author daniel
 *
 */
public class LanguageUtil {

	public static final String ATTRIBUTE_NAME_XPATH = "xpath";
	public static final String ATTRIBUTE_NAME_VALUE = "value";
	
	
	/*public static String translate(String srcXml, String languageXml){
		return translate(XMLParser.parse(srcXml),XMLParser.parse(languageXml).getDocumentElement());
	}*/

	private static String translate(Document doc, Node parentLangNode){
		NodeList nodes = parentLangNode.getChildNodes();
		for(int index = 0; index < nodes.getLength(); index++){
			Node node = nodes.item(index);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			String xpath = ((Element)node).getAttribute(ATTRIBUTE_NAME_XPATH);
			String value = ((Element)node).getAttribute(ATTRIBUTE_NAME_VALUE);
			if(xpath == null || value == null)
				continue;

			Vector result = new XPathExpression(doc, xpath).getResult();
			if(result != null && result.size() > 0){
				Element targetNode = (Element)result.get(0);
				int pos = xpath.lastIndexOf('@');
				if(pos > 0 && xpath.indexOf('=',pos) < 0){
					String attributeName = xpath.substring(pos + 1, xpath.indexOf(']',pos));
					targetNode.setAttribute(attributeName, value);
				}
				else
					targetNode.setTextContent(value);
			}
		}
		return doc.toString();
	}
}
