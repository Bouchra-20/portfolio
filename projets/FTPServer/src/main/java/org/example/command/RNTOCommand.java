package org.example.command;

import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;

import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Implémentation de la commande FTP 'RNTO' (Rename To).
 * <p>
 * Seconde étape du processus de renommage. Elle récupère le nom source mémorisé
 * par RNFR et effectue le déplacement physique vers la destination spécifiée.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class RNTOCommand implements CommandExecutor{

    private ClientHandler clientHandler;
    private PrintWriter out;

    /**
     * Initialise la commande RNTO.
     * @param out Le flux de sortie pour envoyer la réponse.
     * @param clientHandler Le gestionnaire de session client.
     */
    public RNTOCommand(PrintWriter out, ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.out = out;
    }

    /**
     * Finalise le renommage du fichier.
     * <p>
     * Cette méthode valide la séquence (doit suivre un RNFR) et tente le renommage
     * via le système de fichiers. L'état "fileToRename" est réinitialisé après l'opération.
     * </p>
     * @param argument Nouveau nom ou nouveau chemin pour le fichier.
     */
    @Override
    public void execute(String argument) {
        String fromPathStr = clientHandler.getFileToRename();
        if (fromPathStr == null) {
            out.println(FTPCode.C503 +" (use RNFR first).");
            return;
        }

        try {
            Path source = clientHandler.resolveSafePath(fromPathStr);
            Path target = clientHandler.resolveSafePath(argument);

            if (source.toFile().renameTo(target.toFile())) {
                out.println(FTPCode.C250 + " File renamed successfully.");
            } else {
                out.println(FTPCode.C550 + " Rename failed.");
            }
        } catch (SecurityException e) {
            out.println(FTPCode.C550 + " Access denied.");
        } finally {
            clientHandler.setFileToRename(null);
        }
    }
}
