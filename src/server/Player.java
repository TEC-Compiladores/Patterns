package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Player implements Runnable {
	

		private Socket _socket;
		private Thread _thread;
		private volatile boolean _running;
		private DataInputStream _input;
		private DataOutputStream _output;
		private String _message;



		public Player(Socket pSocket) {
			_socket = pSocket;
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
			}
			catch (InterruptedException e) {
				System.out.println("Ocurrio un error en el thread de LogicPlayer");
				e.printStackTrace();
			}
		}



		/**
		 * Thread que se mantiene recibiendo los datos enviados por el control
		 */
		@Override
		public void run() {

			try {
				_input = new DataInputStream(_socket.getInputStream());
				_output = new DataOutputStream(_socket.getOutputStream());

				while (_running) {
					_message = _input.readUTF();
					if (_message != "" || _message != " ") {
						if (_message.equals("exit")) {
							_socket.close();
							System.out.println("Se cerro una conexión");
							this.stopRun();							
							return;
						}
						else {
							System.out.println(_message);
						}

					}
					// _output.writeUTF(_life + "," + _fuel + "," + _bullets);
				}

			}
			catch (IOException e) {

			}

		}
	}