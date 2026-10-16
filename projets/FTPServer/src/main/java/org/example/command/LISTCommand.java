package org.example.command;

import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;

/**
 * Implémentation de la commande FTP 'LIST'.
 * <p>
 * Cette commande énumère le contenu d'un répertoire et envoie les informations détaillées
 * (droits, taille, nom) au client via le canal de données secondaire.
 * Le format de sortie simule le style Unix "ls -l" pour assurer la compatibilité
 * avec les clients FTP standards (FileZilla, WinSCP).
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class LISTCommand implements CommandExecutor{

    private ClientHandler clienthandlerThread;
    private PrintWriter out;

    /**
     * Initialise la commande LIST.
     * @param clienthandlerThread Le thread gérant la session client actuelle.
     * @param out Le canal de contrôle pour les messages d'état (150, 226, 425).
     */
    public LISTCommand(ClientHandler clienthandlerThread, PrintWriter out) {
        this.clienthandlerThread = clienthandlerThread;
        this.out = out;
    }

    /**
     * Exécute le listage du répertoire.
     * <p>
     * Cette méthode ouvre une connexion de données, parcourt les fichiers du dossier
     * résolu, et formate chaque entrée avant l'envoi.
     * </p>
     * @param command Argument optionnel spécifiant le dossier à lister. Si null, liste le dossier courant.
     */
    @Override
    public void execute(String command) {
        File dirToList;
        try {
            String pathArg = (command == null) ? "" : command;
            Path safePath = clienthandlerThread.resolveSafePath(pathArg);
            dirToList = safePath.toFile();
        } catch (SecurityException e) {
            out.println(FTPCode.C550 + " Access denied.");
            return;
        }

        if (!dirToList.exists() || !dirToList.isDirectory()) {
            out.println(FTPCode.C550 + " Directory not found.");
            return;
        }

        try {
            Socket dataSocket = clienthandlerThread.getDataSocket();
            if (dataSocket == null) {
                out.println(FTPCode.C425);
                return;
            }

            out.println(FTPCode.C150);

            try (dataSocket;
                 PrintWriter dataWriter = new PrintWriter(dataSocket.getOutputStream(), true)) {

                File[] files = dirToList.listFiles();
                if (files != null) {
                    for (File file : files) {
                        dataWriter.println(formatListing(file));
                    }
                }
                dataWriter.flush();
            }
            out.println(FTPCode.C226);

        } catch (IOException e) {
            out.println(FTPCode.C425);
        }
    }
    /**
     * Formate les informations d'un fichier selon le standard Unix.
     * <p>
     * Exemple de sortie : -rwxr-xr-x 1 owner group 1024 Jan 01 00:00 filename
     * </p>
     * @param f Le fichier ou répertoire à formater.
     * @return Une chaîne de caractères formatée pour le client FTP.
     */
    private String formatListing(File f) {
        StringBuilder sb = new StringBuilder();
        sb.append(f.isDirectory() ? "d" : "-");
        sb.append(f.canRead() ? "r" : "-");
        sb.append(f.canWrite() ? "w" : "-");
        sb.append(f.canExecute() ? "x" : "-");
        sb.append("r-xr-x");

        sb.append("   1 owner    group ");

        String size = String.format("%10d", f.length());
        sb.append(size).append(" ");

        sb.append("Jan 01 00:00 ");

        sb.append(f.getName());

        return sb.toString();
    }
}
