package default_Package;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.net.InetAddress;

import java.util.Scanner;
public class Server {
private static ServerSocket listener;

public static void main( String[] args) throws Exception {
	
	 int nbClient=0;
	 Scanner sc = new Scanner(System.in);
     System.out.println("Entrez l'ip Server : ");
     String ipServer = sc.nextLine();
     sc.close();
     int serverPort = 5000;
 	listener= new ServerSocket();
 	

     // En gros c'est une methode qui permet a un socket de se lier a un port 
     //meme si il est en etat de fermeture ( par exemple si tu crash et que tu te reconnete rapidement)
     listener.setReuseAddress(true);
     InetAddress serverIP = InetAddress.getByName(ipServer);

     listener.bind(new InetSocketAddress(serverIP, serverPort));
     System.out.format("The server is running on %s:%d%n", ipServer, serverPort);
     
     try {
    	
    	while (true) {
    	
    		new ClientHandler(listener.accept(), nbClient++).start();
    		System.out.println("Je vais la ");
    	}
    	} finally {
    	// Fermeture de la connexion
    	listener.close();
    	} 
     }

     
}

