package org.example.command;

import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * Implémentation de la commande FTP 'PORT' (Mode Actif).
 * <p>
 * Cette commande permet au client de spécifier l'adresse IP et le port sur lesquels
 * il attend que le serveur initie la connexion de données. Contrairement au mode passif,
 * c'est ici le serveur qui agit comme client pour le transfert.
 * </p>
 * <p>
 * L'argument doit être composé de 6 octets séparés par des virgules (h1,h2,h3,h4,p1,p2).
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class PORTCommand implements CommandExecutor {

    private PrintWriter out;
    private ClientHandler clientHandlerThread;

    /**
     * Construit une commande PORT.
     * @param out Le flux de sortie pour le canal de contrôle.
     * @param clientHandlerThread La session client pour stocker les informations de connexion.
     */
    public PORTCommand(PrintWriter out, ClientHandler clientHandlerThread) {
        this.out = out;
        this.clientHandlerThread = clientHandlerThread;
    }
    /**
     * Exécute le parsing de l'adresse et du port fournis par le client.
     * <p>
     * La méthode effectue les étapes suivantes :
     * 1. Vérification de l'authentification.
     * 2. Découpage de l'argument (split sur virgule).
     * 3. Reconstitution de l'adresse IP (4 premiers octets).
     * 4. Calcul du port via la formule : port = (p1 * 256) + p2.
     * 5. Enregistrement des informations dans le handler via {@code setActivePortInfo}.
     * </p>
     * @param argument Chaîne formatée de type "127,0,0,1,15,165".
     */
    @Override
    public void execute(String argument) {
        if (!clientHandlerThread.isAuthentified()) {
            out.println(FTPCode.C530);
            return;
        }

        try {
            String[] parts = argument.split(",");
            if (parts.length != 6) throw new Exception("Invalid PORT format");

            String ip = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
            int port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);

            clientHandlerThread.setActivePortInfo(ip, port);

            out.println(FTPCode.C200);
        }catch (Exception e) {
            out.println(FTPCode.C501);
        }
    }
}
