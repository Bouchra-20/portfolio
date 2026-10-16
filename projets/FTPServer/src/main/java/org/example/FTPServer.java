package org.example;

import org.example.user.UserManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Le serveur FTP principal chargé d'écouter les connexions entrantes.
 * <p>
 * Il initialise la {@link ServerSocket} sur le port spécifié et délègue
 * chaque nouvelle connexion à un nouveau {@link ClientHandler} dans un thread dédié.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class FTPServer {
    /** La socket serveur qui écoute les connexions entrantes. */
    private ServerSocket serverSocket;
    /** Le port de communication (par défaut 2121 dans ce projet). */
    private int port;
    /** Le chemin du répertoire racine physique sur le serveur. */
    private String directory;
    /** Référence vers le gestionnaire central des comptes utilisateurs. */
    private UserManager userManager;

    /**
     * Constructeur du serveur FTP.
     * @param port Port sur lequel le serveur doit écouter.
     * @param directory Chemin racine pour le stockage des fichiers.
     * @param userManager Instance de gestion des utilisateurs.
     */
    public FTPServer(int port, String directory, UserManager userManager) {
        this.port = port;
        this.directory = directory;
        this.userManager = userManager;
    }

    /**
     * Démarre la boucle d'écoute du serveur.
     * <p>
     * Cette méthode est bloquante et accepte des clients indéfiniment.
     * </p>
     * @throws IOException Si le port est déjà utilisé ou si la socket ne peut être ouverte.
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        try {
            while(true){
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket, directory, userManager)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'acceptation d'une connexion client", e);
        }
    }
}
