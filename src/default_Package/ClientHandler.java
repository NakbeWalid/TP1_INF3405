package default_Package;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final int nbClient;
    private final UsersDatabase userDb;

    public ClientHandler(Socket so , int clientNb, UsersDatabase database) {
        this.socket = so;
        this.nbClient = clientNb;
        this.userDb = database;
        System.out.println("Nouveau client connecté depuis " + so.getInetAddress().getHostAddress() + ":" + so.getPort());
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
                    return;
                }
            } else {
                userDb.createUser(username, password);
                out.writeUTF("Compte créé et connexion réussie ! Bienvenue " + username);
            }

            String originalName = in.readUTF();
            String outputName = in.readUTF();

            String clientIP = socket.getInetAddress().getHostAddress();
            int clientPort = socket.getPort();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss"));
            System.out.printf("[%s - %s:%d - %s] : Image %s reçue pour traitement.%n", username, clientIP, clientPort, timestamp, originalName);

            long size = in.readLong();
            byte[] imgBytes = new byte[(int) size];
            int bytesRead = 0;
            while (bytesRead < size) {
                int result = in.read(imgBytes, bytesRead, (int)(size - bytesRead));
                if (result == -1) break;
                bytesRead += result;
            }

            BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(imgBytes));
            
            if (inputImage == null) {
                System.out.println("Erreur : impossible de décoder l'image.");
                return;
            }
            
            BufferedImage processedImage = Sobel.process(inputImage);
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(processedImage, "jpg", bos);
            byte[] processed = bos.toByteArray();

            out.writeLong(processed.length);
            out.write(processed);
            out.flush();

        } catch (IOException e) {
            System.out.println("Erreur avec le client# " + nbClient + ": " + e);
        } finally {
            try { socket.close(); } catch (IOException e) {}
            System.out.println("Connexion avec le client# " + nbClient + " terminée");
        }
    }
}
