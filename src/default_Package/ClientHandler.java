package default_Package;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final int nbClient;
    private final UsersDatabase userDb;

    public ClientHandler(Socket so , int clientNb, UsersDatabase database) {
        this.socket = so;
        this.nbClient = clientNb;
        this.userDb = database;
        System.out.println("Connextion Avec Le Server( " + so.getInetAddress() + ")Etabli avec Succes");
    }

    public void run() { 
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            out.writeUTF("Entrez votre nom d'utilisateur :");
            String username = in.readUTF();

            out.writeUTF("Entrez votre mot de passe :");
            String password = in.readUTF();

            if (userDb.userExists(username)) {
                if (userDb.checkPassword(username, password)) {
                    out.writeUTF("Connexion réussie ! Bienvenue " + username);
                } else {
                    out.writeUTF("Erreur dans la saisie du mot de passe");
                }
            } else {
                userDb.createUser(username, password);
                out.writeUTF("Compte créé et connexion réussie ! Bienvenue " + username);
            }

        } catch (IOException e) {
            System.out.println("Error handling client# " + nbClient + ": " + e);
        } finally {
            try { socket.close(); } catch (IOException e) {}
            System.out.println("Connection with client# " + nbClient + " closed");
        }
    }
}
