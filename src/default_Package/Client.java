package default_Package;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        System.out.print("IP du serveur: ");
        String ip = sc.nextLine();

        System.out.print("Port du serveur: ");
        int port = Integer.parseInt(sc.nextLine());

        Socket socket = new Socket(ip, port);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        System.out.println(in.readUTF()); // "Entrez votre nom d'utilisateur :"
        String username = sc.nextLine();
        out.writeUTF(username);

        System.out.println(in.readUTF()); // "Entrez votre mot de passe :"
        String password = sc.nextLine();
        out.writeUTF(password);

        System.out.println(in.readUTF()); // serveur renvoie la réponse

        socket.close();
    }
}
