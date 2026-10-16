package org.example.command;

import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Implémentation de la commande FTP 'DELE' (Delete).
 * <p>
 * Cette commande permet au client de supprimer un fichier spécifique sur le serveur.
 * Elle intègre une vérification de sécurité pour s'assurer que le fichier cible
 * se trouve dans le périmètre autorisé de l'utilisateur.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class DELECommand implements CommandExecutor{

    private ClientHandler clientHandler;
    private PrintWriter out;

    /**
     * Construit une nouvelle commande DELE.
     * @param out Le flux de sortie pour envoyer les codes de réponse (250, 550).
     * @param clientHandler Le gestionnaire de session client pour la résolution des chemins.
     */
    public DELECommand(PrintWriter out, ClientHandler clientHandler) {
        this.out = out;
        this.clientHandler = clientHandler;
    }

    /**
     * Exécute la suppression du fichier.
     * <p>
     * La suppression échoue si le fichier n'existe pas, si l'argument pointe vers
     * un répertoire ou si les permissions système sont insuffisantes.
     * </p>
     * @param argument Le nom ou le chemin du fichier à supprimer.
     */
    @Override
    public void execute(String argument) {
        try {
            Path safePath = clientHandler.resolveSafePath(argument);
            File file = safePath.toFile();

            if (file.exists() && file.delete()) {
                out.println(FTPCode.C250 + " File deleted.");
            } else {
                out.println(FTPCode.C550 + " File not found or could not be deleted.");
            }
        } catch (SecurityException e) {
            out.println(FTPCode.C550 + " Access denied.");
        }
    }
}
