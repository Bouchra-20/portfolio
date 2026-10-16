package com.flopbox;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

/**
 * Fournit des méthodes statiques pour interagir avec des serveurs FTP distants.
 * <p>
 * Gère le cycle de vie des connexions, l'authentification, ainsi que les parcours
 * récursifs pour le listing et la recherche de fichiers.
 * </p>
 * @author Bouchra Bouamama
 */
public class FTPManager {

    /**
     * Établit une connexion authentifiée avec un serveur FTP.
     * @param user        Nom d'utilisateur FTP.
     * @param password    Mot de passe FTP.
     * @param host        Adresse IP ou nom d'hôte du serveur.
     * @param port        Numéro de port (généralement 21 ou 2121).
     * @param passiveMode Définit si le mode passif doit être activé (true) ou actif (false).
     * @return Une instance de {@link FTPClient} connectée.
     * @throws IOException Si la connexion échoue ou si les identifiants sont invalides.
     */
    public static FTPClient getConnectedClient(String user, String password, String host, int port, boolean passiveMode) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(host, port);

        int reply = ftpClient.getReplyCode();
        if (!org.apache.commons.net.ftp.FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new IOException("Le serveur FTP a refusé la connexion (Code: " + reply + ")");
        }
        boolean login = ftpClient.login(user, password);

        if (!login) {
            ftpClient.disconnect();
            throw new IOException("Authentification FTP échouée");
        }

        if(passiveMode) {
            ftpClient.enterLocalPassiveMode();
        }else{
            ftpClient.enterLocalActiveMode();
        }

        return ftpClient;
    }

    /**
     * Déconnecte proprement le client FTP du serveur distant.
     * @param ftpClient L'instance du client à fermer.
     */
    public static void disconnect(FTPClient ftpClient) {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
        }
    }

    /**
     * Parcourt récursivement le serveur FTP pour construire une représentation
     * textuelle de l'arborescence.
     * @param ftpClient Client FTP actif.
     * @param path      Chemin courant du parcours.
     * @param sb        {@link StringBuilder} accumulant le résultat du listing.
     * @param level     Niveau de profondeur actuel (utilisé pour l'indentation visuelle).
     * @throws IOException En cas d'erreur de lecture sur le serveur distant.
     */
    public static void listRecursive(FTPClient ftpClient, String path, StringBuilder sb, int level) throws IOException {
        FTPFile[] files = ftpClient.listFiles(path);
        if (files == null) return;
        for (FTPFile file : files) {
            String name = file.getName();
            if (name.equals(".") || name.equals("..")) {
                continue;
            }
            for(int i = 0; i < level; i++) {
                sb.append("  |--");
            }
            if (file.isDirectory()) {
                sb.append("[DIR] ").append(file.getName()).append("\n");

                String nextPath = path.endsWith("/") ? path + name : path + "/" + name;
                listRecursive(ftpClient, nextPath, sb, level + 1);
            }else {
                sb.append("[FILE] ").append(file.getName()).append("\n");
            }
        }
    }

    /**
     * Recherche récursivement des fichiers dont le nom contient un mot-clé spécifique.
     * @param ftp         Client FTP actif.
     * @param path        Répertoire de départ de la recherche.
     * @param keyword     Le mot-clé à rechercher (insensible à la casse).
     * @param serverAlias L'alias du serveur en cours de scan (pour le formatage du résultat).
     * @param sb          Accumulateur des résultats trouvés avec leurs URLs d'accès.
     * @throws IOException En cas d'erreur réseau lors du parcours.
     */
    public static void searchRecursive(FTPClient ftp, String path, String keyword, String serverAlias, StringBuilder sb) throws IOException {
        FTPFile[] files = ftp.listFiles(path);
        if (files == null) return;

        for (FTPFile file : files) {
            String name = file.getName();
            if (name.equals(".") || name.equals("..")) continue;

            String fullPath = path.equals("/") ? "/" + name : path + "/" + name;

            if (name.toLowerCase().contains(keyword.toLowerCase())) {
                sb.append("Trouvé sur ").append(serverAlias)
                        .append(" -> http://localhost:8080/servers/").append(serverAlias)
                        .append("/files").append(fullPath).append("\n");
            }

            if (file.isDirectory()) {
                searchRecursive(ftp, fullPath, keyword, serverAlias, sb);
            }
        }
    }

    /**
     * Utilitaire de bas niveau pour supprimer un dossier et tout son contenu sur le serveur FTP.
     *
     * Algorithme :
     * 1. Liste tous les éléments du dossier cible.
     * 2. Récursion : Si l'élément est un dossier, la méthode s'appelle elle-même.
     * 3. Suppression : Supprime les fichiers un par un, puis le dossier parent une fois vide.
     *
     * @param ftp Instance active de FTPClient.
     * @param path Chemin absolu ou relatif du dossier à détruire.
     * @throws IOException En cas de problème de droits ou de déconnexion.
     */
    public static void deleteRecursiveFTP(FTPClient ftp, String path) throws IOException {
        FTPFile[] files = ftp.listFiles(path);
        if (files != null && files.length > 0) {
            for (FTPFile file : files) {
                String fileName = file.getName();
                if (fileName.equals(".") || fileName.equals("..")) continue;

                String fullPath = path + "/" + fileName;
                if (file.isDirectory()) {
                    deleteRecursiveFTP(ftp, fullPath);
                } else {
                    ftp.deleteFile(fullPath);
                }
            }
        }
        ftp.removeDirectory(path);
    }
}