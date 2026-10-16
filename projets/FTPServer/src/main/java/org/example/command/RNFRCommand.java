package org.example.command;

import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Implémentation de la commande FTP 'RNFR' (Rename From).
 * <p>
 * Première étape du processus de renommage. Cette commande vérifie l'existence
 * du fichier source et le mémorise temporairement dans la session du client.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class RNFRCommand implements CommandExecutor{

    private ClientHandler clientHandler;
    private PrintWriter out;

    /**
     * Initialise la commande RNFR.
     * @param out Le flux de sortie pour envoyer la réponse.
     * @param client Le gestionnaire de session client.
     */
    public RNFRCommand(PrintWriter out, ClientHandler client) {
        this.out = out;
        this.clientHandler = client;
    }
    /**
     * Vérifie si le fichier à renommer existe et est accessible.
     * <p>
     * En cas de succès, le serveur répond avec le code 350, indiquant qu'il
     * attend la commande RNTO pour finaliser l'action.
     * </p>
     * @param argument Nom ou chemin du fichier source.
     */
    @Override
    public void execute(String argument) {
        try {
            Path path = clientHandler.resolveSafePath(argument);
            if (path.toFile().exists()) {
                clientHandler.setFileToRename(argument);
                out.println(FTPCode.C350);
            } else {
                out.println(FTPCode.C550 + " File not found.");
            }
        } catch (SecurityException e) { out.println(FTPCode.C550 +" Access denied."); }
    }
}
