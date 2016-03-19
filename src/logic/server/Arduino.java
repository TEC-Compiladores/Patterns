package logic.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Arduino implements ConstantsServer {

	private String _ip;
	private int _port;
	private Socket _socket;
	private PrintWriter _out;
	private boolean _connected;
	boolean _debug;



	/**
	 * 
	 * Constructor de la clase
	 * 
	 * @param pDebug
	 */
	public Arduino(boolean pDebug) {
		this._connected = false;
		_debug = pDebug;
	}



	/**
	 * Método que realiza la conexión con el arduino
	 * 
	 * @param pIP
	 *            IP del arduino
	 * @param pPort
	 *            Puerto del arduino
	 * @return
	 */
	public boolean connect(String pIP, int pPort) {
		_ip = pIP;
		_port = pPort;

		try {
			_socket = new Socket(_ip, _port);
			this._connected = true;
			_out = new PrintWriter(_socket.getOutputStream(), true);
			_out.println("3Test");
			if (_debug)
				System.out.println(ARDUINO_CLASS + ARDUINO_SUCCESSFUL_CONNECTION);

		} catch (UnknownHostException e) {
			if (_debug)
				System.err.println(ARDUINO_CLASS + ARDUINO_ERROR_CONNECTION);
		} catch (IOException e) {
			if (_debug)
				System.err.println(ARDUINO_CLASS + ARDUINO_ERROR_IO);
		}

		return _connected;
	}



	/**
	 * Método para enviar un mensaje al arduino
	 * 
	 * @param pMessage
	 *            Mensaje a enviar
	 */
	public void sendMessage(String pMessage) {
		if (this._connected) {
			if (_debug)
				System.out.println(ARDUINO_CLASS + ARDUINO_MESSAGE_SEND + pMessage);
			_out.println(pMessage);
		}
	}
}
