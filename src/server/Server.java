package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

	
	/**
	 * En esta clase se encuentra el Servidor que abrirá el puerto ingresado por
	 * usuario y se mantendrá esperando la conexión de más jugadores
	 * 
	 * @author Juan Pablo Brenes
	 *
	 */
public class Server implements Runnable {


		private Thread _thread;
		private ServerSocket _serverSocket;
		private Socket _socket;
		private ArrayList<Player> players = new ArrayList<Player>();

		private volatile boolean _running;

		private int _port = 8001;



		/**
		 * Constructor de la clase
		 */
		public Server() {
			
		}



		/**
		 * Método que inicia el thread
		 */
		public void startServer() {
			_running = true;

			_thread = new Thread(this, "servidor");
			_thread.start();
		}



		/**
		 * Método que detiene el thread
		 */
		public synchronized void stopServer() {
			_running = false;

			try {
				_thread.join();
			}
			catch (InterruptedException e) {
				System.out.println("Error al detener el servidor");
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
			}
			catch (IOException e) {
				_open = false;
			}
			return _open;
		}



		/**
		 * Thread que se mantiene esperando la conexión de mas jugadores
		 */
		@Override
		public void run() {

			try {
				_serverSocket = new ServerSocket(_port);
				_socket = new Socket();

				while (_running) {
					_socket = _serverSocket.accept();
					players.add(new Player(_socket));
					System.out.println("Conexión establecida");
				}
				_serverSocket.close();
				_socket.close();
			}
			catch (Exception e) {
				System.out.println("Error dentro del servidor");
				e.printStackTrace();
			}
		}

	}

