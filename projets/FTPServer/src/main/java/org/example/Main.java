package org.example;

import org.example.user.User;
import org.example.user.UserManager;

/**
 * Point d'entrée principal de l'application.
 * <p>
 * Configure les utilisateurs autorisés, définit le répertoire racine du serveur
 * et lance l'instance du {@link FTPServer}.
 * </p>
 */
public class Main {
    /**
     * Méthode principale.
     * @param args Arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {

        User user = new User("Bouchra", "Bouchra");
        UserManager userManager = new UserManager();
        userManager.addUser(user);

        FTPServer ftpServer = new FTPServer(2121, "/home/m1eserv/bouchra.bouamama.etu/Documents", userManager);

        try {
            ftpServer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}