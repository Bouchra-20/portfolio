package org.example.command;

import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implémentation de la commande FTP 'CWD' (Change Working Directory).
 * <p>
 * Cette classe permet de modifier le répertoire de travail actuel de l'utilisateur.
 * Elle inclut une logique de normalisation pour transformer les chemins physiques
 * du serveur en chemins relatifs virtuels pour le client FTP.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class CWDCommand implements CommandExecutor {

    private PrintWriter out;
    private ClientHandler clientHandler;

    /**
     * Construit une nouvelle commande CWD.
     * @param out Le flux de sortie pour envoyer les codes de réponse au client.
     * @param clientHandler Le gestionnaire de session du client pour modifier l'état du chemin.
     */
    public CWDCommand(PrintWriter out, ClientHandler clientHandler) {
        this.out = out;
        this.clientHandler = clientHandler;
    }

    /**
     * Exécute le changement de répertoire.
     * <p>
     * La méthode résout le chemin demandé, vérifie son existence et s'assure
     * qu'il s'agit d'un dossier. Elle calcule ensuite le chemin relatif par rapport
     * à la racine du serveur pour l'affichage client.
     * </p>
     * @param argument Le chemin (relatif ou absolu) vers le répertoire cible.
     */
    @Override
    public void execute(String argument) {

        try {
            Path safePath = clientHandler.resolveSafePath(argument);
            File newDir = safePath.toFile();

            if (newDir.exists() && newDir.isDirectory()) {
                Path root = Paths.get(clientHandler.getDirectory()).toAbsolutePath().normalize();
                Path relative = root.relativize(safePath.toAbsolutePath().normalize());

                String newRelStr = relative.toString().replace('\\', '/');
                if (newRelStr.isEmpty() || newRelStr.equals(".")) {
                    clientHandler.setCurrentRelativePath("/");
                } else {
                    clientHandler.setCurrentRelativePath("/" + newRelStr);
                }

                out.println(FTPCode.C250 + " Directory changed to " + clientHandler.getCurrentRelativePath());
            } else {
                out.println(FTPCode.C550 + " Not a directory");
            }

        } catch (SecurityException e) {
            out.println(FTPCode.C550 + " Access denied (out of root).");
        }
    }
}