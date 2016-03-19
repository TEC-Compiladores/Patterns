package logic.server;

/**
 * @author Juan Pablo Brenes 6/3/2016
 * 
 *         Constantes del paquete server
 *
 */
interface ConstantsServer {

	// ###################### CLASS SERVER ######################

	// ERROR
	public static final String SERVER_ERROR_STOP_THREAD = "Error al detener el thread del servidor";
	public static final String SERVER_ERROR_IN_THREAD = "Error dentro del thread del servidor";

	// NOTIFICATIONS
	public static final String SERVER_SUCCESSFUL_CONNECTION = "Se realizó la conexión de un nuevo usuario";

	//
	public static final String SERVER_THREAD_NAME = "Servidor";
	public static final String SERVER_CLASS = "SERVER >> ";


	// ###################### CLASS ARDUINO ######################

	// ERROR
	public static final String ARDUINO_ERROR_IO = "Error al enviar un mensaje al arduino";
	public static final String ARDUINO_ERROR_CONNECTION = "Error al intentar conectarse con el arduino";

	// NOTIFICATION
	public static final String ARDUINO_SUCCESSFUL_CONNECTION = "Conexión exitosa con el arduino";

	//
	public static final String ARDUINO_CLASS = "ARDUINO >> ";
	public static final String ARDUINO_MESSAGE_SEND = "Enviado al arduino: ";


}
