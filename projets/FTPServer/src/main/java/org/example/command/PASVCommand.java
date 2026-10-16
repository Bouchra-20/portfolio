package org.example.command;

import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Implémentation de la commande FTP 'PASV' (Passive Mode).
 * <p>
 * Cette commande demande au serveur d'écouter sur un port de données éphémère
 * et d'attendre une connexion du client au lieu d'initier lui-même la connexion.
 * </p>
 * <p>
 * La réponse du serveur contient l'adresse IP et le port encodés sous forme de 6 entiers
 * séparés par des virgules (h1,h2,h3,h4,p1,p2).
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class PASVCommand implements CommandExecutor{

    private PrintWriter out;
    private ClientHandler clientHandlerThread;

    /**
     * Initialise la commande PASV.
     * @param out Le flux de sortie pour le canal de contrôle.
     * @param clientHandler Le gestionnaire de session pour stocker la {@link ServerSocket} de données.
     */
    public PASVCommand(PrintWriter out, ClientHandler clientHandler) {
        this.out = out;
        this.clientHandlerThread = clientHandler;
    }
    /**
     * Exécute l'ouverture du mode passif.
     * <p>
     * 1. Vérifie l'authentification.<br>
     * 2. Ferme toute ancienne socket de données existante.<br>
     * 3. Ouvre une nouvelle {@link ServerSocket} sur un port libre (0).<br>
     * 4. Calcule les octets du port (p1, p2) et formate la réponse 227.
     * </p>
     * @param argument Ignoré pour cette commande.
     */
    @Override
    public void execute(String argument) {
        if (!clientHandlerThread.isAuthentified()) {
            out.println(FTPCode.C530);
            return;
        }

        try {
            if (clientHandlerThread.dataServerSocket != null && !clientHandlerThread.dataServerSocket.isClosed()) {
                clientHandlerThread.dataServerSocket.close();
            }
            clientHandlerThread.dataServerSocket = new ServerSocket(0);
            int port = clientHandlerThread.dataServerSocket.getLocalPort();

            String hostAddress = InetAddress.getLocalHost().getHostAddress().replace('.', ',');

            int p1 = port / 256;
            int p2 = port % 256;
            out.println(FTPCode.C227 + " (" + hostAddress + "," + p1 + "," + p2 + ")\r\n");

        } catch (IOException e) {
            out.println(FTPCode.C425);
        }
    }

}
