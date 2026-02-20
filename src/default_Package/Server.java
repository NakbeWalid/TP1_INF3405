package default_Package;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Scanner;
public class Server {
private static ServerSocket listener;

public static void main( String[] args) throws Exception {
	
	//Atomic Integer pour eviter les problemes car plusieurs threads
	 final AtomicInteger nbClient = new AtomicInteger(0);
	 final UsersDatabase usersDb = new UsersDatabase("database.txt");

	 try (Scanner sc = new Scanner(System.in)) {
		 InetAddress serverIP;
		 int serverPort;

		 while (true) {
			 System.out.print("Entrez l'IP du serveur (IPv4) : ");
			 String ipServer = sc.nextLine().trim();
			 if (!InputValidators.isValidIPv4Format(ipServer)) {
				 System.out.println("Erreur: format IP invalide. Exemple valide: 127.0.0.1");
				 continue;
			 }

			 System.out.print("Entrez le port d'écoute (5000 à 5050) : ");
			 String portStr = sc.nextLine().trim();
			 Integer port = InputValidators.parsePortInRange(portStr, 5000, 5050);
			 if (port == null) {
				 System.out.println("Erreur: port invalide. Il doit être entre 5000 et 5050.");
				 continue;
			 }

			 try {
				 InetAddress candidate = InetAddress.getByName(ipServer);
				 if (!(candidate instanceof Inet4Address)) {
					 System.out.println("Erreur: l'adresse IP doit être IPv4.");
					 continue;
				 }
				 serverIP = candidate;
				 serverPort = port;

				 listener = new ServerSocket();
				 
				 // En gros c'est une methode qui permet a un socket de se lier a un port 
				 // meme si il est en etat de fermeture ( par exemple si tu crash et que tu te reconnete rapi				 listener.setReuseAddress(true);
				 listener.bind(new InetSocketAddress(serverIP, serverPort));
				 break;
			 } catch (Exception bindErr) {
				 System.out.println("Impossible de démarrer le serveur sur " + ipServer + ":" + port + " (" + bindErr.getMessage() + "). Réessaie.");
			 }
		 }

		 System.out.format("Serveur démarré sur %s:%d%n", serverIP.getHostAddress(), serverPort);

		 try {
			 while (true) {
				 Socket client = listener.accept();
				 new ClientHandler(client, nbClient.getAndIncrement(), usersDb).start();
			 }
		 } finally {
			 
			 // Fermeture de la connexion
			 listener.close();
		 }
	 }
 }

     
}

