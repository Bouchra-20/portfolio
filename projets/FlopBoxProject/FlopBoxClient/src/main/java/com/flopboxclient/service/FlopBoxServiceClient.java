package com.flopboxclient.service;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.File;

/**
 * Client REST haute performance pour l'interface avec le Proxy FlopBox.
 *
 * Ce client encapsule les appels HTTP vers l'API Gateway qui fait le pont avec
 * les serveurs FTP. Il gère l'authentification à deux niveaux :
 * 1. Authentification API (user/password du proxy).
 * 2. Authentification FTP (ftp-user/ftp-pass pour la session distante).
 */
public class FlopBoxServiceClient {
    private final String baseUrl;
    private final String user;
    private final String password;
    private final Client client;

    /**
     * Initialise le client de service FlopBox.
     *
     * @param baseUrl  L'URL de base de l'API (ex: http://localhost:8080)
     * @param user     Le nom d'utilisateur pour l'accès au Proxy
     * @param password Le mot de passe pour l'accès au Proxy
     */
    public FlopBoxServiceClient(String baseUrl, String user, String password) {
        this.baseUrl = baseUrl;
        this.user = user;
        this.password = password;
        this.client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class)
                .build();
    }

    /**
     * Télécharge un fichier binaire depuis le serveur FTP via le Proxy.
     *
     * @param alias       Identifiant du serveur serveur cible sur le Proxy
     * @param path        Chemin relatif du fichier sur le serveur distant
     * @param destination Fichier local où enregistrer le contenu
     * @param ftpUser     Identifiant de connexion au serveur FTP
     * @param ftpPass     Mot de passe de connexion au serveur FTP
     * @throws Exception  Si le serveur retourne un code autre que 200 ou en cas d'erreur E/S
     */
    public void downloadFile(String alias, String path, File destination, String ftpUser, String ftpPass) throws Exception {
        Response response = client.target(baseUrl).path("servers").path(alias).path("files").path(path)
                .request()
                .header("user", user)
                .header("password", password)
                .header("ftp-user", ftpUser)
                .header("ftp-pass", ftpPass)
                .get();

        if (response.getStatus() == 200) {
            InputStream in = response.readEntity(InputStream.class);
            Files.copy(in, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            throw new Exception("Erreur download: " + response.getStatus());
        }
    }
    /**
     * Récupère la liste des fichiers et dossiers d'un répertoire distant.
     *
     * @param alias   Identifiant du serveur cible
     * @param path    Chemin du répertoire à lister
     * @param ftpUser Identifiant FTP
     * @param ftpPass Mot de passe FTP
     * @return Une chaîne de caractères représentant l'arborescence (format texte brut)
     */
    public String getFileList(String alias, String path, String ftpUser, String ftpPass) {
        WebTarget target = client.target(baseUrl).path("servers").path(alias).path("files");
        if (path != null && !path.isEmpty()) {
            target = target.path(path);
        }

        return target.request(MediaType.TEXT_PLAIN)
                .header("user", user)
                .header("password", password)
                .header("ftp-user", ftpUser)
                .header("ftp-pass", ftpPass)
                .get(String.class);
    }
    /**
     * Téléverse un fichier local vers le serveur distant en utilisant MultiPart.
     *
     * @param alias        Identifiant du serveur cible
     * @param path         Chemin de destination sur le serveur (nom du fichier inclus)
     * @param fileToUpload Fichier local à envoyer
     * @param ftpUser      Identifiant FTP
     * @param ftpPass      Mot de passe FTP
     * @throws Exception   Si le téléversement échoue (codes HTTP différents de 200 ou 201)
     */
    public void uploadFile(String alias, String path, File fileToUpload, String ftpUser, String ftpPass) throws Exception {
        client.register(MultiPartFeature.class);

        FileDataBodyPart filePart = new FileDataBodyPart("file", fileToUpload);
        try (FormDataMultiPart multiPart = new FormDataMultiPart()) {
            multiPart.bodyPart(filePart);

            Response response = client.target(baseUrl)
                    .path("servers")
                    .path(alias)
                    .path("files")
                    .path(path)
                    .request()
                    .header("user", user)
                    .header("password", password)
                    .header("ftp-user", ftpUser)
                    .header("ftp-pass", ftpPass)
                    .post(Entity.entity(multiPart, multiPart.getMediaType()));

            if (response.getStatus() != 200 && response.getStatus() != 201) {
                String errorMsg = response.readEntity(String.class);
                throw new Exception("Erreur upload (" + response.getStatus() + ") : " + errorMsg);
            }

            System.out.println("Upload réussi : " + path);
            multiPart.close();
        }


    }

    /**
     * Supprime un fichier ou un répertoire sur le serveur FTP distant.
     *
     * @param alias   Identifiant du serveur cible
     * @param path    Chemin de l'élément à supprimer
     * @param ftpUser Identifiant FTP
     * @param ftpPass Mot de passe FTP
     * @throws Exception Si la suppression échoue (codes HTTP différents de 200 ou 204)
     */
    public void deleteFile(String alias, String path, String ftpUser, String ftpPass) throws Exception {
        Response response = client.target(baseUrl)
                .path("servers")
                .path(alias)
                .path("files")
                .path(path)
                .request()
                .header("user", user)
                .header("password", password)
                .header("ftp-user", ftpUser)
                .header("ftp-pass", ftpPass)
                .delete();

        if (response.getStatus() != 200 && response.getStatus() != 204) {
            throw new Exception("Erreur suppression: " + response.getStatus());
        }
    }

    /**
     * Renomme ou déplace un fichier/répertoire sur le serveur distant.
     * <p>
     * Utilise le verbe HTTP personnalisé "MOVE".
     *
     * @param alias   Identifiant du serveur cible
     * @param oldPath Chemin actuel de l'élément
     * @param newPath Nouveau chemin ou nouveau nom de l'élément
     * @param ftpUser Identifiant FTP
     * @param ftpPass Mot de passe FTP
     * @throws Exception Si le renommage échoue (codes HTTP différents de 200 ou 204)
     */
    public void renameFile(String alias, String oldPath, String newPath, String ftpUser, String ftpPass) throws Exception {
        Response response = client.target(baseUrl)
                .path("servers")
                .path(alias)
                .path("files")
                .path(oldPath)
                .queryParam("moveTarget", newPath)
                .request()
                .header("user", user)
                .header("password", password)
                .header("ftp-user", ftpUser)
                .header("ftp-pass", ftpPass)
                .build("MOVE", null)
                .invoke();

        if (response.getStatus() != 200 && response.getStatus() != 204) {
            throw new Exception("Erreur renommage: " + response.getStatus());
        }
    }
}