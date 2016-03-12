package logic;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import logic.server.Server;
import dataAccess.Xml;


/**
 * 
 * @author Juan Pablo Brenes 6/3/2016
 * 
 *         Clase principal del programa
 *
 */
public class Core implements ConstantsLogic {

	private static int _port;
	private static boolean _debug;
	private static AtomicLong _idCounter;
	private static final Object _lock = new Object();;


	private Server _server;
	private Xml _xml;
	private List<User> _users;
	private int _userCounter;



	/**
	 * Constructor de la clase
	 * 
	 * @param pPort
	 *            Puerto que abrira el servidor
	 * @param pDebug
	 *            Indica si se quiere información de debug
	 */
	public Core(int pPort, boolean pDebug) {
		_port = pPort;
		_debug = pDebug;
		_idCounter = new AtomicLong();


		_users = Collections.synchronizedList(new ArrayList<User>());
		_userCounter = CORE_ZERO;

		// this.startServer();
		_xml = new Xml(_debug);
	}



	/**
	 * Método para obtener un id, que identifica a cada uno de los usuarios
	 * conectados
	 * 
	 * @return ID para el usuario
	 */
	private long getID() {
		return _idCounter.incrementAndGet();
	}



	/**
	 * Método que inicia el servidor con el puerto especificado
	 */
	private void startServer() {
		_server = new Server(_port, _debug, this);
		_server.startServer();
		if (_debug)
			System.out.println(CORE_CLASS + CORE_SERVER_STARTED + _port);
	}



	/**
	 * Método que agrega un nuevo usuario a la lista de usuarios
	 * 
	 * Es llamado por el server cuando se recibe una nueva conexión
	 * 
	 * @param pSocket
	 *            Socket con el que se debe comunicar
	 */
	public void newUser(Socket pSocket) {
		long id = this.getID();
		synchronized (_users) {
			_users.add(new User(pSocket, id, _debug, this));
			_userCounter++;
		}
		if (_debug)
			System.out.println(CORE_CLASS + CORE_ASSIGNED_ID + id);
	}



	/**
	 * Método que remueve un usuario de la lista de usuarios
	 * 
	 * Es llamado por el usuario a eliminar, cuando recibe el mensaje de
	 * desconexión
	 * 
	 * @param pUser
	 *            Usuario a eliminar
	 */
	public void removeUser(User pUser) {
		long id = pUser.getID();
		int index;

		synchronized (_users) {
			Iterator<User> iterator = _users.iterator();
			for (index = 0; index < _users.size(); index++) {
				if (iterator.next().getID() == id)
					break;
			}
			_users.remove(index);
			_userCounter--;
		}
		if (_debug)
			System.out.println(CORE_CLASS + CORE_REMOVED_USER + id);
	}



	public String parser(String pMessage) {

		synchronized (_lock) {
			String reply = this._xml.manageMessage(pMessage);

			if (reply == null) {
				System.err.println(CORE_CLASS + CORE_NULL_REPLY);
			}
			return reply;

		}
	}


}
