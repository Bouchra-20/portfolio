package org.example.command;

import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implémentation de la commande FTP 'RETR' (Retrieve).
 * <p>
 * Cette commande permet au client de télécharger un fichier depuis le serveur.
 * Elle nécessite l'établissement préalable d'un canal de données via les commandes
 * PASV ou PORT. Le transfert est réalisé de manière optimisée via des flux bufferisés.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class RETRCommand implements CommandExecutor {

    private PrintWriter out;
    private ClientHandler clientHandlerThread;
    /**
     * Construit une commande RETR.
     * @param out Le canal de contrôle pour envoyer les codes d'état (150, 226, 425, 550).
     * @param clientHandler Le gestionnaire de session pour accéder aux fichiers et à la socket de données.
     */
    public RETRCommand(PrintWriter out, ClientHandler clientHandler) {
        this.out = out;
        this.clientHandlerThread = clientHandler;
    }
    /**
     * Exécute le transfert de fichier du serveur vers le client.
     * <p>
     * Processus :
     * 1. Vérification de l'authentification.
     * 2. Résolution sécurisée du chemin pour éviter le "Path Traversal".
     * 3. Vérification de l'existence de la cible et qu'il ne s'agit pas d'un dossier.
     * 4. Récupération de la socket de données négociée.
     * 5. Lecture du fichier par blocs de 8 Ko et écriture sur le flux de sortie de la socket.
     * </p>
     * @param argument Le nom ou le chemin du fichier à récupérer.
     */
    @Override
    public void execute(String argument) {
        if (!clientHandlerThread.isAuthentified()) {
            out.println(FTPCode.C530);
            return;
        }


        Path filePath = clientHandlerThread.resolveSafePath(argument);

        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
            try {
                Socket dataSocket = clientHandlerThread.getDataSocket();

                if (dataSocket == null) {
                    out.println(FTPCode.C425 + " Use PORT or PASV first.");
                    return;
                }

                out.println(FTPCode.C150);

                try (dataSocket;
                     BufferedOutputStream bos = new BufferedOutputStream(dataSocket.getOutputStream());
                     BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(filePath))) {

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                    bos.flush();
                }
                out.println(FTPCode.C226);

            } catch (IOException e) {
                out.println(FTPCode.C425);
            }
        } else {
            out.println(FTPCode.C550 + " File not found.");
        }
    }
    /**
     * Ferme proprement la ServerSocket de données associée au handler.
     * Utilisé pour libérer les ressources réseau une fois le transfert terminé ou annulé.
     */
    private void closeDataServerSocket() {
        try {
            if (clientHandlerThread.dataServerSocket != null) {
                clientHandlerThread.dataServerSocket.close();
                clientHandlerThread.dataServerSocket = null;
            }
        } catch (IOException e) { throw new RuntimeException(e) ; }
    }

}
