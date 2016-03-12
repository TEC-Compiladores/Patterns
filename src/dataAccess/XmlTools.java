package dataAccess;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XmlTools implements ConstantsDataAccess {

	private boolean _debug;



	public XmlTools(boolean pDebug) {
		_debug = pDebug;
	}



	/**
	 * Método que crea un objeto de tipo "org.w3c.dom.Document" a partir de un
	 * string que almacena xml
	 * 
	 * @param pString
	 *            String con el xml
	 * @return Objeto con el xml
	 */
	public Document parseToDocument(String pString) {
		Document doc = null;
		boolean flag = true;

		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(pString));
			doc = db.parse(is);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			flag = false;
			e.printStackTrace();
		}

		if (!flag)
			return null;
		else
			return doc;
	}



	/**
	 * Método que obtiene en forma de string el xml almacenado dentro de una
	 * objeto tipo "org.w3c.dom.Document"
	 * 
	 * @param pDocument
	 *            Objeto con el xml
	 * @return Información del xml dentro del objeto en forma de string
	 */
	public String parseToString(Document pDocument) {
		String xml;

		try {
			DOMSource domSource = new DOMSource(pDocument);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);

			xml = writer.toString();

		} catch (TransformerException tfe) {
			if (_debug)
				System.err.println(TOOLS_CLASS + TOOLS_ERROR_PARSE_TO_DOC);
			xml = null;
		}

		return xml;
	}





	/**
	 * Método para obtener un valor de un xml
	 * 
	 * @param pDocument
	 *            Objeto con el xml
	 * @param pXPath
	 *            Dirrección al valor a obtener en el xml
	 * @return Valor deseado del xml
	 */
	public String getValue(Document pDocument, String pXPath) {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		String value = TOOLS_EMPTY;

		try {
			XPathExpression expression = xpath.compile(pXPath);
			value = (String) expression.evaluate(pDocument, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			value = null;
			if (_debug)
				System.err.println(TOOLS_CLASS + TOOLS_ERROR_XPATH_VALUE + pXPath);
		}

		return value;
	}



	/**
	 * Método para obtener una lista de valores contenidos dentro de un xml
	 * 
	 * @param pDocument
	 *            Objeto que contiene el xml
	 * @param pXPath
	 *            Dirección a los valores a obtener en el xml
	 * @return
	 */
	public NodeList getValueList(Document pDocument, String pXPath) {
		NodeList values;
		try {

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expression = xpath.compile(pXPath);
			values = (NodeList) expression.evaluate(pDocument, XPathConstants.NODESET);

		} catch (XPathExpressionException e) {
			values = null;
			if (_debug)
				System.err.println(TOOLS_CLASS + TOOLS_ERROR_XPATH_VALUE_LIST + pXPath);
		}

		return values;
	}



	/**
	 * Método que permite crear un objeto de tipo "org.w3c.dom.Document" para
	 * crear un xml
	 * 
	 * @return Objeto que permite la creación de un xml
	 */
	public Document createEmptyDoc() {
		Document document = null;

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			document = docBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			if (_debug)
				System.err.println(TOOLS_CLASS + TOOLS_ERROR_NEW_EMPTY_DOC);
		}

		return document;


	}

}
