package logic;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import logic.server.Arduino;
import logic.server.Server;
import dataAccess.Xml;


/**
 * 
 * @author Juan Pablo Brenes 6/3/2016
 * 
 *         Clase principal del programa
 *
 */
public class Core implements Runnable, ConstantsLogic {

	private static int _port;
	private static boolean _debug;
	private volatile boolean _running;
	private int _userCounter;
	private static AtomicLong _idCounter;
	private static final Object _lock = new Object();


	private Thread _thread;
	private Server _server;
	private Arduino _arduino;
	private Xml _xml;
	private List<User> _users;




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

		_xml = new Xml(this, _debug);
		this._arduino = new Arduino();
		_arduino.connect("", 80);
		this.startServer();
		this.startThread();
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



	public void notifyArduino(String pString) {
		_arduino.sendMessage(pString);
	}



	// private String cleanMessage(String pString) {
	// StringBuilder builder = new StringBuilder();
	//
	// for (int i = 0; i < pString.length(); i++) {
	// char c = pString.charAt(i);
	// builder.append(c);
	// }
	//
	//
	// return builder.toString();
	// }



	public String parser(String pMessage) {

		synchronized (_lock) {
			// String newMessage = this.cleanMessage(pMessage);
			String reply = this._xml.manageMessage(pMessage);

			if (reply == null) {
				System.err.println(CORE_CLASS + CORE_NULL_REPLY);
			}
			return reply;

		}
	}



	private void closeServer() {
		this._server.stopServer();


		Iterator<User> iterator = _users.iterator();
		for (int index = 0; index < _users.size(); index++) {
			iterator.next().killUser();
		}

		this.stopThread();

		System.out.println("$$$$$$");
	}





	/**
	 * Método que inicia el thread
	 */
	public void startThread() {
		_running = true;

		_thread = new Thread(this, "Core");
		_thread.start();
	}



	/**
	 * Método que detiene el thread
	 */
	public synchronized void stopThread() {
		_running = false;

		// try {
		// _thread.join();
		// } catch (InterruptedException e) {
		// if (_debug)
		// System.err.println(CORE_CLASS +
		// "Error al detener el thread en core");
		// e.printStackTrace();
		// }
	}



	@Override
	public void run() {
		Scanner scan = new Scanner(System.in);

		while (_running) {
			String s = scan.next();
			if (s.equals("exit")) {
				System.out.println("SALIR DEL PROGRAMA");
				// this.closeServer();
			}

		}


	}


}
