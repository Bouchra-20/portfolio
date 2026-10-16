package org.example.command;

import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Implémentation de la commande FTP 'RMD' (Remove Directory).
 * <p>
 * Cette commande permet de supprimer un répertoire vide sur le serveur.
 * Elle assure que l'utilisateur ne peut pas supprimer de dossiers en dehors
 * de sa racine grâce au mécanisme de résolution de chemin sécurisé.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class RMDCommand implements CommandExecutor {
    private PrintWriter out;
    private ClientHandler clientHandler;

    /**
     * Construit une commande RMD.
     * @param out Le flux de sortie vers le client.
     * @param clientHandler Le gestionnaire de session client.
     */
    public RMDCommand(PrintWriter out, ClientHandler clientHandler) {
        this.out = out;
        this.clientHandler = clientHandler;
    }

    /**
     * Exécute la suppression du répertoire.
     * <p>
     * La suppression échoue (code 550) si :
     * - Le chemin n'existe pas ou n'est pas un répertoire.
     * - Le répertoire n'est pas vide (contrainte du système de fichiers).
     * - Le serveur n'a pas les droits d'écriture/suppression sur le dossier.
     * </p>
     * @param argument Le nom ou le chemin du répertoire à supprimer.
     */
    @Override
    public void execute(String argument) {
        try {
            Path safePath = clientHandler.resolveSafePath(argument);
            File dir = safePath.toFile();

            if (dir.exists() && dir.isDirectory()) {
                if (dir.delete()) {
                    out.println(FTPCode.C250 + " Directory deleted.");
                } else {
                    out.println(FTPCode.C550 +" Directory not empty or access denied.");
                }
            } else {
                out.println(FTPCode.C550 +" Not a directory.");
            }
        } catch (SecurityException e) {
            out.println(FTPCode.C550 +" Access denied.");
        }
    }
}