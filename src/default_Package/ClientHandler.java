package default_Package;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
	private Socket socket;
	private int nbClient;
	
	public ClientHandler(Socket so , int clientNb) {
		this.socket=so;
		this.nbClient=clientNb;
		System.out.println("Connextion Avec Le Server( " +so.getInetAddress() + ")Etabli avec Succes");
	}
	public void run() { // Création de thread qui envoi un message à un client
		try {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		out.writeUTF("Hello from server - you are client#" + nbClient); 
		} catch (IOException e) {
		System.out.println("Error handling client# " + nbClient + ": " + e);
		} finally {
		try {
		socket.close();
		} catch (IOException e) {
		System.out.println("Couldn't close a socket, what's going on?");}
		System.out.println("Connection with client# " + nbClient+ " closed");}
		}
		}
