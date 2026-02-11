package default_Package;

import java.io.*;
import java.util.Scanner;

public class UsersDatabase {
    private final File database;

    public UsersDatabase(String fileName) {
        database = new File(fileName);
        try {
            if (!database.exists()) database.createNewFile();
        } catch (IOException e) {
            System.out.println("Impossible de créer le fichier de base: " + e);
        }
    }

    public synchronized boolean userExists(String username) {
        try (Scanner reader = new Scanner(database)) {
            while (reader.hasNextLine()) {
                String[] parts = reader.nextLine().split(":");
                if (parts.length == 2 && parts[0].equals(username)) return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized boolean checkPassword(String username, String password) {
        try (Scanner reader = new Scanner(database)) {
            while (reader.hasNextLine()) {
                String[] parts = reader.nextLine().split(":");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized void createUser(String username, String password) {
        try (FileWriter fw = new FileWriter(database, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(username + ":" + password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
