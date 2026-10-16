package org.example.command;

import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Implémentation de la commande FTP 'MKD' (Make Directory).
 * <p>
 * Permet la création de nouveaux répertoires sur le serveur.
 * En cas de succès, le serveur répond avec le code 257 incluant le nom
 * du dossier créé entre guillemets, conformément à la RFC 959.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class MKDCommand implements CommandExecutor {
    private PrintWriter out;
    private ClientHandler clientHandler;

    /**
     * Initialise la commande MKD.
     * @param out Le flux de sortie pour envoyer la réponse.
     * @param clientHandler Le gestionnaire de session client.
     */
    public MKDCommand(PrintWriter out, ClientHandler clientHandler) {
        this.out = out;
        this.clientHandler = clientHandler;
    }

    /**
     * Exécute la création d'un répertoire.
     * <p>
     * Utilise {@code mkdirs()} pour s'assurer que les dossiers parents nécessaires
     * sont également créés si besoin, tout en respectant le confinement de la racine.
     * </p>
     * @param argument Nom du répertoire à créer.
     */
    @Override
    public void execute(String argument) {
        try {
            Path safePath = clientHandler.resolveSafePath(argument);
            File newDir = safePath.toFile();

            if (newDir.exists()) {
                out.println(FTPCode.C550 +" Directory already exists.");
            } else if (newDir.mkdirs()) {
                out.println(FTPCode.C257 + " \"" + argument + "\" created.");
            } else {
                out.println(FTPCode.C550 + " Could not create directory.");
            }
        } catch (SecurityException e) {
            out.println(FTPCode.C550 +" Access denied.");
        }
    }
}