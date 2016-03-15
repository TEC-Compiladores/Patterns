package dataAccess;

import java.io.File;


public interface ConstantsDataAccess {


	// ###################### CLASS XML ######################

	// ERROR
	public static final String XML_ERROR_LOAD_FILE = "Error al cargar el archivo xml";
	public static final String XML_ERROR_SAVE_FILE = "Error al guardar el archivo xml";
	public static final String XML_ERROR_INCORRECT_FORMAT = "Se recibio un mensaje incorrecto";

	// NOTIFICATIONS
	public static final String XML_FILE_FOUND = "Se encontro un archivo xml de juego";
	public static final String XML_FILE_NOT_FOUND = "No se encontro un archivo xml de juego";
	public static final String XML_SUCCESSFUL_LOAD = "Archivo xml cargado con éxito";
	public static final String XML_SUCCESSFUL_CREATED = "Archivo xml creado con éxito";
	public static final String XML_UNKNOW_MESSAGE = "Mensaje desconocido";

	//
	public static final File XML_FILE_FOR_PATH = new File("");

	public static final String XML_PATH = XML_FILE_FOR_PATH.getAbsolutePath();
	public static final String XML_FILE_NAME = "/Game.xml";
	public static final String XML_XPATH_MESSAGE = "/Request/message/text()";
	public static final String XML_INDENT_PROPERTY = "yes";
	public static final String XML_METHOD_PROPERTY = "xml";
	public static final String XML_ENCODING_PROPERTY = "UTF-8";
	public static final String XML_APACHE_INDENT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount";
	public static final String XML_INDENT_AMOUNT = "4";
	public static final String XML_CLASS = "XML >> ";
	public static final String XML_NAME_ROOT_XML = "PatternRecognizer";
	public static final String XML_NAME_NODE_USERS = "Users";
	public static final String XML_NAME_NODE_GAMES = "Games";
	public static final String XML_NAME_NODE_SCORES = "Scores";
	public static final String XML_REQUEST_GAMELIST = "GameList";
	public static final String XML_REQUEST_GAMEINFO = "GameInfo";
	public static final String XML_REQUEST_PLAYINFO = "PlayInfo";
	public static final String XML_REGISTER_NEWGAME = "NewGame";
	public static final String XML_REGISTER_NEWPLAYER = "NewPlayer";
	public static final String XML_REGISTER_NEWATTEMPT = "NewAttempt";
	public static final String XML_REGISTER_GAMEWIN = "GameWin";
	public static final String XML_XPATH_GAME_NAMES = "/PatternRecognizer/Games/Game/name";
	public static final String XML_REPLY_ROOT = "Reply";
	public static final String XML_REPLY_ACTIVEGAMES_NODE = "ActiveGames";
	public static final String XML_REPLY_GAME_NODE = "game";

	public static final int XML_NODE_USERS = 1;
	public static final int XML_NODE_GAMES = 3;
	public static final int XML_NODE_SCORES = 5;



	// ###################### CLASS XMLTOOLS ######################

	// ERROR
	public static final String TOOLS_ERROR_XPATH_VALUE = "Error en expresión al obtener un valor del xml. Expresion: ";
	public static final String TOOLS_ERROR_PARSE_TO_DOC = "Error al convertir un documento a string";
	public static final String TOOLS_ERROR_PARSE_TO_STRING = "Error al convertir un string a documento";
	public static final String TOOLS_ERROR_XPATH_VALUE_LIST = "Error en expresión al obtener una lista de valores. Expresión: ";
	public static final String TOOLS_ERROR_NEW_EMPTY_DOC = "Error al crear un documento xml vacío";

	//
	public static final String TOOLS_CLASS = "XMLTOOLS >> ";
	public static final String TOOLS_EMPTY = "";


}
