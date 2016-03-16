package logic.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class Arduino {

	private String _ip;
	private int _port;
	private Socket _socket;
	private DataOutputStream _outComing;
	boolean _connected;



	public Arduino() {
		_connected = false;
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
			_connected = true;
			_outComing = new DataOutputStream(_socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		if (_connected) {
			try {
				_outComing.writeUTF(pMessage);
				_outComing.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
