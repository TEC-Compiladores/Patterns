package dataAccess;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Xml implements ConstantsDataAccess {

	private Document _doc;
	private DocumentBuilderFactory _docFactory;
	private XPathFactory _xpathFactory;
	private XPath _xpath;
	private Node[] _nodes;
	private boolean _debug;

	private XmlTools _tools;



	public Xml(boolean pDebug) {
		_docFactory = DocumentBuilderFactory.newInstance();
		_docFactory.setNamespaceAware(true);
		_xpathFactory = XPathFactory.newInstance();
		_xpath = _xpathFactory.newXPath();
		_debug = pDebug;
		_tools = new XmlTools(_debug);

		this.checkFile();

	}



	/**
	 * Método que verifica si el archivo xml del juego existe o debe ser creado
	 */
	private void checkFile() {
		File file = new File(XML_PATH + XML_FILE_NAME);
		if (file.exists() && file.isFile()) {
			if (_debug)
				System.out.println(XML_CLASS + XML_FILE_FOUND);
			this.loadFile();
		}
		else {
			if (_debug)
				System.out.println(XML_CLASS + XML_FILE_NOT_FOUND);
			this.createFile();
		}
	}



	/**
	 * Método que carga el archivo xml del juego y carga sus nodos principales
	 * dentro de un arreglo para su facil modificación
	 */
	private void loadFile() {

		try {
			DocumentBuilder builder = _docFactory.newDocumentBuilder();
			_doc = builder.parse(XML_PATH + XML_FILE_NAME);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			if (_debug)
				System.err.println(XML_CLASS + XML_ERROR_LOAD_FILE);
		}

		_nodes = new Node[3];
		Node root = _doc.getFirstChild();
		_nodes[XML_NODE_USERS] = root.getChildNodes().item(XML_NODE_USERS);
		_nodes[XML_NODE_GAMES] = root.getChildNodes().item(XML_NODE_GAMES);
		_nodes[XML_NODE_SCORES] = root.getChildNodes().item(XML_NODE_SCORES);
		if (_debug)
			System.out.println(XML_CLASS + XML_SUCCESSFUL_LOAD);
	}



	/**
	 * Método que crea el archivo xml donde se almacena la informacion del juego
	 * 
	 * Guarda en una arreglo los nodos principales del xml para su fácil
	 * modificación
	 */
	private void createFile() {
		try {
			DocumentBuilder docBuilder = _docFactory.newDocumentBuilder();
			_doc = docBuilder.newDocument();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}

		_nodes = new Node[3];
		Element root = _doc.createElement(XML_NAME_ROOT_XML);
		_doc.appendChild(root);

		Element users = _doc.createElement(XML_NAME_NODE_USERS);
		root.appendChild(users);
		_nodes[XML_NODE_USERS] = root.getChildNodes().item(XML_NODE_USERS);

		Element games = _doc.createElement(XML_NAME_NODE_GAMES);
		root.appendChild(games);
		_nodes[XML_NODE_GAMES] = root.getChildNodes().item(XML_NODE_GAMES);


		Element scores = _doc.createElement(XML_NAME_NODE_SCORES);
		root.appendChild(scores);
		_nodes[XML_NODE_SCORES] = _doc.getChildNodes().item(XML_NODE_SCORES);

		this.saveFile();

		if (_debug)
			System.out.println(XML_CLASS + XML_SUCCESSFUL_CREATED);
	}



	/**
	 * Método que guarda en disco el archivo xml del juego
	 */
	private void saveFile() {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();

		try {
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, XML_INDENT_PROPERTY);
			transformer.setOutputProperty(OutputKeys.METHOD, XML_METHOD_PROPERTY);
			transformer.setOutputProperty(OutputKeys.ENCODING, XML_ENCODING_PROPERTY);
			transformer.setOutputProperty(XML_APACHE_INDENT_PROPERTY, XML_INDENT_AMOUNT);
			DOMSource source = new DOMSource(_doc);
			StreamResult result = new StreamResult(new File(XML_PATH + XML_FILE_NAME));
			transformer.transform(source, result);

		} catch (TransformerException e) {
			if (_debug)
				System.err.println(XML_CLASS + XML_ERROR_SAVE_FILE);
		}

	}



	public String manageMessage(String pMessage) {

		Document doc = this._tools.parseToDocument(pMessage);
		String message = this._tools.getValue(_doc, XML_XPATH_MESSAGE);
		String reply = null;

		if (message == XML_REQUEST_GAMELIST) {
			reply = this.getListOfGames();
		}
		else if (message == XML_REGISER_NEWGAME) {
			this.registerNewGame(doc);
			reply = " ";
		}

		return reply;
	}



	/**
	 * Método que lee el archivo xml con la información del juego y obtiene una
	 * lista con el nombre de todos los juegos activos. Esta lista se guarda en
	 * otro xml el cual se convierte a string para ser enviado por el servidor
	 * como respuesta a la petición de algún usuario
	 * 
	 * @return String con el xml de respuesta
	 */
	private String getListOfGames() {
		NodeList gameList = _tools.getValueList(_doc, XML_XPATH_GAME_NAMES);
		Document document = _tools.createEmptyDoc();

		Element root = document.createElement(XML_REPLY_ROOT);
		document.appendChild(root);

		Element games = document.createElement("ActiveGames");
		root.appendChild(games);

		for (int i = 0; i < gameList.getLength(); i++) {
			Element gameName = document.createElement(XML_REPLY_GAME_NODE);
			gameName.setNodeValue(gameList.item(i).getNodeValue());
			games.appendChild(gameName);
		}

		return _tools.parseToString(document);
	}



	/**
	 * Método que registra en el archivo xml del juego la creación de un nuevo
	 * juego. Se obtiene la información del nuevo juego a partir del xml enviado
	 * por el usuario y se almacena en el archivo del servidor
	 * 
	 * @param pDocument
	 *            Xml enviado por el usuario
	 */
	private void registerNewGame(Document pDocument) {
		Node root = pDocument.getFirstChild();
		Node game = root.getChildNodes().item(3);
		Node adopt = _doc.adoptNode(game);


		_nodes[XML_NODE_GAMES].appendChild(adopt);
		this.saveFile();
	}

}
