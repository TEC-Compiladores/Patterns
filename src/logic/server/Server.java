package logic.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import logic.Core;
import logic.User;

/**
 * 
 * @author Juan Pablo Brenes 6/3/2016
 * 
 *         En esta clase se encuentra el Servidor que abrirá el puerto ingresado
 *         por usuario y se mantendrá esperando la conexión de más jugadores
 *
 */
public class Server implements Runnable, ConstantsServer {

	private Thread _thread;
	private ServerSocket _serverSocket;
	private Socket _socket;
	private ArrayList<User> players = new ArrayList<User>();
	private Core _core;

	private volatile boolean _running;
	private boolean _debug;
	private int _port;



	/**
	 * Constructor de la clase
	 */
	public Server(int pPort, boolean pDebug, Core pCore) {
		_port = pPort;
		_core = pCore;
		_debug = pDebug;
	}



	/**
	 * Método que inicia el thread
	 */
	public void startServer() {
		_running = true;

		_thread = new Thread(this, SERVER_THREAD_NAME);
		_thread.start();
	}



	/**
	 * Método que detiene el thread
	 */
	public synchronized void stopServer() {
		_running = false;

		try {
			_thread.join();
		} catch (InterruptedException e) {
			if (_debug)
				System.out.println(SERVER_CLASS + SERVER_ERROR_STOP_THREAD);
			e.printStackTrace();
		}
	}



	/**
	 * Método que establece el puerto que se usara para la conexión de los demás
	 * jugadores
	 * 
	 * @param pPort
	 *            Puerto elegido por el usuario
	 */
	public void setPort(int pPort) {
		_port = pPort;
	}



	/**
	 * Método que verifica si el puerto ingresado por el usuario es correcto
	 * 
	 * @param pPort
	 *            Puerto que ingreso el usuario
	 * @return True si el puerto se logro abrir, False si el puerto no se logro
	 *         abrir
	 */
	public boolean openPort(int pPort) {
		boolean _open = false;

		try {
			_serverSocket = new ServerSocket(pPort);
			_open = true;
			_serverSocket.close();
			this.setPort(pPort);
		} catch (IOException e) {
			_open = false;
		}
		return _open;
	}



	/**
	 * Thread que se mantiene esperando la conexión de mas usuarios
	 */
	@Override
	public void run() {

		try {
			_serverSocket = new ServerSocket(_port);
			_serverSocket.setReuseAddress(true);
			_socket = new Socket();

			while (_running) {
				_socket = _serverSocket.accept();
				if (_debug)
					System.out.println(SERVER_CLASS
							+ SERVER_SUCCESSFUL_CONNECTION);
				_core.newUser(_socket);
			}
			_serverSocket.close();
			_socket.close();
		} catch (Exception e) {
			if (_debug)
				System.out.println(SERVER_CLASS + SERVER_ERROR_IN_THREAD);
			e.printStackTrace();
		}
	}

}
