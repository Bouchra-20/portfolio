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
 * Implémentation de la commande FTP 'STOR' (Store).
 * <p>
 * Cette commande permet au client d'envoyer un fichier vers le serveur (Upload).
 * Elle ouvre un flux d'entrée depuis la socket de données et écrit le contenu
 * dans un nouveau fichier sur le disque local du serveur.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class STORCommand implements CommandExecutor{

    private PrintWriter out;
    private ClientHandler clientHandlerThread;

    /**
     * Initialise la commande STOR.
     * @param out Le flux de sortie pour envoyer la réponse.
     * @param clientHandler Le gestionnaire de session client.
     */
    public STORCommand(PrintWriter out, ClientHandler clientHandler) {
        this.out = out;
        this.clientHandlerThread = clientHandler;
    }

    /**
     * Gère la réception du fichier envoyé par le client.
     * <p>
     * La méthode s'appuie sur une socket de données préalablement établie (PORT ou PASV).
     * Elle utilise un tampon (buffer) de 8 Ko pour optimiser l'écriture sur disque.
     * </p>
     * @param argument Nom du fichier à créer ou écraser sur le serveur.
     */
    @Override
    public void execute(String argument) {
        if (!clientHandlerThread.isAuthentified()) {
            out.println(FTPCode.C530);
            return;
        }

        Path filePath = clientHandlerThread.resolveSafePath(argument);

        out.println(FTPCode.C150);

        try {
            Socket dataSocket = clientHandlerThread.getDataSocket();
            if (dataSocket == null) {
                out.println(FTPCode.C425 + " Use PORT or PASV first.");
                return;
            }

            out.println(FTPCode.C150);

            try (dataSocket;
                 BufferedInputStream bis = new BufferedInputStream(dataSocket.getInputStream());
                 BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(filePath))) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                bos.flush();
            }
            out.println(FTPCode.C226);

        } catch (IOException e) {
            out.println(FTPCode.C425 + " Error during upload.");
            return;
        } finally {
            closeDataServerSocket();
        }

        out.println(FTPCode.C226);
    }

    /**
     * Libère les ressources de la socket de données après le transfert.
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