package logic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 
 * @author Juan Pablo Brenes 6/3/2016
 * 
 *         Clase que permite la comunicación con cada usuario
 *
 */
public class User implements Runnable, ConstantsLogic {

	private Socket _socket;
	private Thread _thread;
	private volatile boolean _running;
	private DataInputStream _input;
	private DataOutputStream _output;
	private String _message;
	private boolean _debug;
	private static long _id;
	private Core _core;



	/**
	 * Constructor de la clase Asigna el socket a escuchar e inicia el thread
	 * 
	 * @param pSocket
	 *            Socket con el que se comunica
	 */
	public User(Socket pSocket, long pID, boolean pDebug, Core pCore) {
		_socket = pSocket;
		_id = pID;
		_core = pCore;
		_debug = pDebug;
		this.startRun();
	}



	/**
	 * Método que inicia el thread
	 */
	private void startRun() {
		_running = true;

		_thread = new Thread(this);
		_thread.start();
	}



	/**
	 * Método que detiene el thread
	 */
	private synchronized void stopRun() {
		_running = false;

		try {
			_thread.join();
		} catch (InterruptedException e) {
			if (_debug)
				System.err.println(USER_CLASS + USER_ERROR_STOP_THREAD + _id);
			e.printStackTrace();
		}
	}



	/**
	 * Método para obtener el id del usuario
	 * 
	 * @return ID
	 */
	public long getID() {
		return this._id;
	}



	private void killUser() {

		try {
			_socket.close();
		} catch (IOException e) {
			if (_debug)
				System.err.println(USER_CLASS + USER_ERROR_CLOSE_SOCKET + _id);
		}

		if (_debug)
			System.out.println(USER_CLASS + USER_CONNECTION_CLOSED + _id);

		_core.removeUser(this);
		this.stopRun();
	}




	/**
	 * Thread que mantiene la comunicación con el usuario
	 */
	@Override
	public void run() {

		try {
			_input = new DataInputStream(_socket.getInputStream());
			_output = new DataOutputStream(_socket.getOutputStream());

			while (_running) {
				// Se obtienen las peticiones del usuario
				_message = _input.readUTF();
				if (!_message.equals(USER_EMPTY_1) || !_message.equals(USER_EMPTY_2)) {
					if (_message.equals(USER_EXIT_MESSAGE)) {
						this.killUser();
					}
					else {
						System.out.println("ENTRANTE: " + _message);
						String reply = _core.parser(_message);
						System.out.println("RESPUESTA A GERALD!!!!!: " + reply);
						// Se envian las respuestas al usuario
						_output.writeUTF(reply);
						_message = "";
					}
				}
			}

		} catch (IOException e) {
			if (_debug)
				System.err.println(USER_CLASS + USER_ERROR_IO_SOCKET + _id);
		}

	}
}