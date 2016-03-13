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
		_nodes[0] = root.getChildNodes().item(XML_NODE_USERS);
		_nodes[1] = root.getChildNodes().item(XML_NODE_GAMES);
		_nodes[2] = root.getChildNodes().item(XML_NODE_SCORES);
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
		_nodes[0] = root.getChildNodes().item(XML_NODE_USERS);

		Element games = _doc.createElement(XML_NAME_NODE_GAMES);
		root.appendChild(games);
		_nodes[1] = root.getChildNodes().item(XML_NODE_GAMES);


		Element scores = _doc.createElement(XML_NAME_NODE_SCORES);
		root.appendChild(scores);
		_nodes[2] = _doc.getChildNodes().item(XML_NODE_SCORES);

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

		this._doc = null;
		this._nodes = null;
		this.loadFile();

	}



	public String manageMessage(String pMessage) {

		Document doc = this._tools.parseToDocument(pMessage);
		if (doc == null) {
			System.err.println(XML_CLASS + "Se recibio un mensaje incorrecto");
			return null;
		}

		String message = this._tools.getValue(doc, XML_XPATH_MESSAGE);
		System.out.println("##" + message + "##");
		String reply = null;

		if (message.equals(XML_REQUEST_GAMELIST)) {
			reply = this.getListOfGames();
		}
		else if (message.equals(XML_REGISER_NEWGAME)) {
			reply = this.registerNewGame(doc);
		}
		else if (message.equals(XML_REGISTER_NEWPLAYER)) {
			reply = this.registerNewPlayer(doc);
		}
		else if (message.equals("GameInfo")) {
			reply = this.getGameInfo(doc);
		}
		else if (message.equals("PlayInfo")) {
			reply = this.getPlayInfo(doc);
		}
		else if (message.equals("NewAttempt")) {
			reply = this.registerNewAttempt(doc);
		}
		else
			System.out.println("Mensaje desconocido");

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

		Element amount = document.createElement("amount");
		amount.appendChild(document.createTextNode(String.valueOf(gameList.getLength())));
		root.appendChild(amount);

		System.out.println("CANTIDAD DE JUEGOS: " + gameList.getLength());
		// Element games = document.createElement(XML_REPLY_ACTIVEGAMES_NODE);

		for (int i = 0; i < gameList.getLength(); i++) {
			Element gameName = document.createElement(XML_REPLY_GAME_NODE);
			gameName.appendChild(document.createTextNode(gameList.item(i).getTextContent()));
			root.appendChild(gameName);
		}

		// root.appendChild(games);

		String reply = _tools.parseToString(document);

		return reply;
	}



	/**
	 * Método que registra en el archivo xml del juego la creación de un nuevo
	 * juego. Se obtiene la información del nuevo juego a partir del xml enviado
	 * por el usuario y se almacena en el archivo del servidor
	 * 
	 * @param pDocument
	 *            Xml enviado por el usuario
	 */
	private String registerNewGame(Document pDocument) {
		Node root = pDocument.getFirstChild();
		Node game = root.getChildNodes().item(1);
		Node adopt = _doc.adoptNode(game);


		_nodes[1].appendChild(adopt);
		this.saveFile();

		String reply = "1";

		return reply;
	}



	/**
	 * Método que registra dentro del archivo xml del juego el nombre de un
	 * nuevo jugador
	 * 
	 * @param pDocument
	 *            Xml enviado por el usuario
	 */
	private String registerNewPlayer(Document pDocument) {
		Node root = pDocument.getFirstChild();
		Node name = root.getChildNodes().item(1);
		Node adopt = _doc.adoptNode(name);

		_nodes[0].appendChild(adopt);
		this.saveFile();

		String reply = "1";

		return reply;
	}



	private String getGameInfo(Document pDocument) {
		String gameName = this._tools.getValue(pDocument, "/Request/gameName/text()");

		String creator = this._tools.getValue(_doc, "/PatternRecognizer/Games/Game[name=\""
				+ gameName + "\"]/creator/text()");
		String attempts = this._tools.getValue(_doc, "/PatternRecognizer/Games/Game[name=\""
				+ gameName + "\"]/attempts/text()");
		String date = this._tools.getValue(_doc, "/PatternRecognizer/Games/Game[name=\"" + gameName
				+ "\"]/date/text()");

		Document doc = this._tools.createEmptyDoc();

		Element root = doc.createElement("Reply");
		doc.appendChild(root);

		Element info = doc.createElement("Info");

		Element creatorRE = doc.createElement("creator");
		creatorRE.appendChild(doc.createTextNode(creator));
		info.appendChild(creatorRE);

		Element attemptsRE = doc.createElement("attempts");
		attemptsRE.appendChild(doc.createTextNode(attempts));
		info.appendChild(attemptsRE);

		Element dateRE = doc.createElement("date");
		dateRE.appendChild(doc.createTextNode(date));
		info.appendChild(dateRE);

		root.appendChild(info);

		String reply = this._tools.parseToString(doc);

		return reply;
	}



	private String getPlayInfo(Document pDoc) {
		String gameName = this._tools.getValue(pDoc, "/Request/gameName/text()");

		String pattern = this._tools.getValue(_doc, "/PatternRecognizer/Games/Game[name=\""
				+ gameName + "\"]/pattern/text()");

		Node Originalexamples = this._tools.getNode(_doc, "/PatternRecognizer/Games/Game[name=\""
				+ gameName + "\"]/Examples");


		Document doc = this._tools.createEmptyDoc();
		Element root = doc.createElement("Reply");
		doc.appendChild(root);

		Element playInfo = doc.createElement("PlayInfo");

		Element patternRE = doc.createElement("pattern");
		patternRE.appendChild(doc.createTextNode(pattern));
		playInfo.appendChild(patternRE);

		Element examples = doc.createElement("Examples");
		Element example1 = doc.createElement("example");
		example1.appendChild(doc.createTextNode(Originalexamples.getChildNodes().item(1)
				.getTextContent()));
		examples.appendChild(example1);

		Element example2 = doc.createElement("example");
		example2.appendChild(doc.createTextNode(Originalexamples.getChildNodes().item(3)
				.getTextContent()));
		examples.appendChild(example2);

		Element example3 = doc.createElement("example");
		example3.appendChild(doc.createTextNode(Originalexamples.getChildNodes().item(5)
				.getTextContent()));
		examples.appendChild(example3);

		Element example4 = doc.createElement("example");
		example4.appendChild(doc.createTextNode(Originalexamples.getChildNodes().item(7)
				.getTextContent()));
		examples.appendChild(example4);

		Element example5 = doc.createElement("example");
		example5.appendChild(doc.createTextNode(Originalexamples.getChildNodes().item(9)
				.getTextContent()));
		examples.appendChild(example5);

		playInfo.appendChild(examples);
		root.appendChild(playInfo);

		String reply = this._tools.parseToString(doc);

		return reply;
	}



	private String registerNewAttempt(Document pDoc) {
		String gameName = this._tools.getValue(pDoc, "/Request/gameName/text()");

		Node node = this._tools.getNode(_doc, "/PatternRecognizer/Games/Game[name=\"" + gameName
				+ "\"]/attempts");

		int attempts = Integer.valueOf(node.getTextContent());

		node.setTextContent(String.valueOf(attempts += 1));

		this.saveFile();

		String reply = "1";

		return reply;



	}

}
