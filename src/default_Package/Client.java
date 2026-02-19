package default_Package;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        String ip;
        while (true) {
            System.out.print("Entrez l'IP du serveur (IPv4) : ");
            ip = sc.nextLine().trim();
            if (InputValidators.isValidIPv4Format(ip)) break;
            System.out.println("Erreur : format IP invalide. Exemple valide: 127.0.0.1");
        }


        int port;
        while (true) {
            System.out.print("Entrez le port d'écoute (5000 à 5050) : ");
            String portStr = sc.nextLine().trim();
            if (InputValidators.parsePortInRange(portStr, 5000, 5050) != null) {
                port = Integer.parseInt(portStr);
                break;
            }
            System.out.println("Erreur : le port doit être entre 5000 et 5050.");
        }

        try ( Socket socket = new Socket(ip, port); 
        	DataInputStream in = new DataInputStream(socket.getInputStream()); 
        	DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
        	
		        System.out.println(in.readUTF()); // "Entrez votre nom d'utilisateur :"
		        String username = sc.nextLine();
		        out.writeUTF(username);
		
		        System.out.println(in.readUTF()); // "Entrez votre mot de passe :" 
		        String password = sc.nextLine();
		        out.writeUTF(password);
		
		        String authResponse = in.readUTF(); // serveur renvoie la réponse
		        System.out.println(authResponse);
		
		        if (authResponse.startsWith("Erreur")) {
		            System.out.println("Connexion refusée.");
		            socket.close();
		            return;
		        }
		        
		        String imagePath;
		        File imageFile;
		        while (true) {
		            System.out.print("Nom de l'image à envoyer : ");
		            imagePath = sc.nextLine().trim();
		            imageFile = new File(imagePath);
		            if (imageFile.exists() && imageFile.isFile()) break;
		            System.out.println("Erreur : fichier introuvable → " + imageFile.getAbsolutePath());
		        }
		
		        System.out.print("Nom à donner à l'image traitée : ");
		        String outputName = sc.nextLine().trim();
		
		        out.writeUTF(imageFile.getName());
		        out.writeUTF(outputName);
		
		        byte[] imageBytes = readFileBytes(imageFile);
		        out.writeLong(imageBytes.length);
		        out.write(imageBytes);
		        out.flush();
		        System.out.println("Image envoyée au serveur avec succès.");
		        
		        long resultSize = in.readLong();
		        byte[] resultBytes = new byte[(int) resultSize];
		        int bytesRead = 0;
		        while (bytesRead < resultSize) {
		            int result = in.read(resultBytes, bytesRead, (int)(resultSize - bytesRead));
		            if (result == -1) break;
		            bytesRead += result;
		        }

		        File outputFile = new File(outputName + ".jpg");
		        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
		            fos.write(resultBytes);
		        }
		        System.out.println("Image traitée reçue et sauvegardée ici: " + outputFile.getAbsolutePath());
	       }
    }
    
    private static byte[] readFileBytes(File f) throws IOException {
        try (FileInputStream fis = new FileInputStream(f);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = fis.read(buf)) != -1) bos.write(buf, 0, n);
            return bos.toByteArray();
        }
    }
}
