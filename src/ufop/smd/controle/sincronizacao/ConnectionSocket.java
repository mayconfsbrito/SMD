package ufop.smd.controle.sincronizacao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Message;

/**
 * Classe de conexão via socket
 *
 * @author Maycon Fernando Silva Brito @email mayconfsbrito@gmail.com
 */
public class ConnectionSocket {

	private static ConnectionSocket connection;
	private int porta;
	private String host;
	private Socket socket;
	private Message msg;
	private DataOutputStream out;
	private DataInputStream in;
	private InputStream ins;
	private OutputStream ous;
	
	//Handler para notificação na telar
	private Handler handler;
	
	/**
	 * Construtor da classe (Privado)
	 */
	private ConnectionSocket(String host, Integer porta){
		this.host = host;
		this.porta = porta;
	}
	
	/**
	 * Cria e retorna o objeto ConnectionSocket
	 */
	public static ConnectionSocket createConnection(String host, Integer porta){
		connection = new ConnectionSocket(host, porta);
		
		return connection;
	}
	
	/**
	 * Retorna a conexão atual
	 */
	public static ConnectionSocket getConnection(){
		return connection;
	}
	
	/**
	 * Conecta ao servidor
	 */
	public void connect() throws UnknownHostException, IOException{
		this.socket = new Socket(host, porta);
		out = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(socket.getInputStream());
		ins = socket.getInputStream();
		ous = socket.getOutputStream();
	}
	
	/**
	 * Método get para recebimento de mensagem
	 */
	public DataInputStream getDataInput(){
		return in;
	}
	
	/**
	 * Método get para envio de mensagem
	 * @return
	 */
	public DataOutputStream getDataOutput(){
		return out;
	}
	
	public InputStream getIns() {
		return ins;
	}

	public OutputStream getOus() {
		return ous;
	}

	/**
	 * Método para desconectar do servidor
	 */
	public void disconnect() throws Exception {
		socket.close();
	}
}
