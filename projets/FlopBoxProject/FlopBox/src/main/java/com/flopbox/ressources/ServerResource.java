package com.flopbox.ressources;

import com.flopbox.AuthService.AuthService;
import com.flopbox.AuthService.FtpSessionFactory;
import com.flopbox.FTPManager;
import com.flopbox.exceptions.FlopBoxException;
import com.flopbox.exceptions.FtpOperationException;
import com.flopbox.exceptions.ResourceNotFoundException;
import com.flopbox.exceptions.UnauthorizedException;
import org.apache.commons.net.ftp.FTPClient;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.apache.commons.net.ftp.FTPFile;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Ressource JAX-RS principale gérant les opérations sur les serveurs FTP distants.
 * <p>
 * Agit comme une passerelle (Proxy) entre le client REST et les serveurs FTP.
 * Supporte le listing récursif, le téléchargement, l'upload binaire, la recherche
 * globale et la gestion de fichiers (renommage, suppression, création de dossiers).
 * </p>
 */
@Path("/servers")
public class ServerResource {

    private AuthService authService = new AuthService();

    /**
     * Teste la connectivité avec un serveur FTP spécifique.
     * @param alias    L'alias du serveur cible.
     * @param mode     Le mode de connexion FTP (actif/passif).
     * @param user     Identifiant utilisateur FlopBox (Header).
     * @param password Mot de passe utilisateur FlopBox (Header).
     * @param ftpUser  Identifiant de connexion au serveur FTP distant (Header).
     * @param ftpPass  Mot de passe de connexion au serveur FTP distant (Header).
     * @return 200 OK si la connexion et l'authentification FTP réussissent.
     */
    @GET
    @Path("/{alias}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkConnection(
            @PathParam("alias") String alias,
            @HeaderParam("ftp-mode") String mode,
            @HeaderParam("user") String user,
            @HeaderParam("password") String password,
            @HeaderParam("ftp-user") String ftpUser,
            @HeaderParam("ftp-pass") String ftpPass) {

        FTPClient ftpClient = null;
        try {
            validateAccess(user, password, alias);
            ftpClient = createFtpSession(alias, ftpUser, ftpPass, mode);


            return Response.ok("Connexion réussie au serveur FTP !").build();

        } catch (FlopBoxException e) {
            return buildErrorResponse(e);
        } finally {
            FTPManager.disconnect(ftpClient);
        }
    }

    /**
     * Récupère l'arborescence récursive des fichiers et dossiers du serveur FTP.
     * @param alias    L'alias du serveur cible.
     * @param mode     Le mode de connexion FTP (actif/passif).
     * @param user     Identifiant utilisateur FlopBox (Header).
     * @param password Mot de passe utilisateur FlopBox (Header).
     * @param ftpUser  Identifiant de connexion au serveur FTP distant (Header).
     * @param ftpPass  Mot de passe de connexion au serveur FTP distant (Header).
     * @return Une représentation textuelle de l'arborescence.
     */
    @GET
    @Path("/{alias}/files")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getFiles(
            @PathParam("alias") String alias,
            @HeaderParam("ftp-mode") String mode,
            @HeaderParam("user") String user,
            @HeaderParam("password") String password,
            @HeaderParam("ftp-user") String ftpUser,
            @HeaderParam("ftp-pass") String ftpPass) {

        FTPClient ftpClient = null;
        try {
            validateAccess(user, password, alias);

            ftpClient = createFtpSession(alias, ftpUser, ftpPass, mode);


            StringBuilder treeBuilder = new StringBuilder("Arborescence complète :\n");

            FTPManager.listRecursive(ftpClient, ".", treeBuilder, 0);

            return Response.ok(treeBuilder.toString()).build();

        } catch (FlopBoxException e) {
            return buildErrorResponse(e);
        } catch (IOException e) {
            return Response.serverError().entity("Erreur de lecture : " + e.getMessage()).build();
        } finally {
            FTPManager.disconnect(ftpClient);
        }
    }

    /**
     * Télécharge un fichier depuis le serveur FTP.
     * Utilise {@link StreamingOutput} pour optimiser la mémoire lors du transfert de gros fichiers.
     *
     * @param filename Nom du fichier à récupérer.
     * @param alias    L'alias du serveur cible.
     * @param mode     Le mode de connexion FTP (actif/passif).
     * @param user     Identifiant utilisateur FlopBox (Header).
     * @param password Mot de passe utilisateur FlopBox (Header).
     * @param ftpUser  Identifiant de connexion au serveur FTP distant (Header).
     * @param ftpPass  Mot de passe de connexion au serveur FTP distant (Header).
     * @return Un flux binaire (Octet-Stream) déclenchant le téléchargement côté client.
     */
    @GET
    @Path("/{alias}/files/{filename: .*}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(
            @PathParam("filename") String filename,
            @PathParam("alias") String alias,
            @HeaderParam("ftp-mode") String mode,
            @HeaderParam("user") String user,
            @HeaderParam("password") String password,
            @HeaderParam("ftp-user") String ftpUser,
            @HeaderParam("ftp-pass") String ftpPass) {

        try {
            validateAccess(user, password, alias);
            FTPClient ftpClient = createFtpSession(alias, ftpUser, ftpPass, mode);
            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);

            String actualFileName = filename;
            if (filename.contains("/")) {
                String remoteDir = filename.substring(0, filename.lastIndexOf("/"));
                actualFileName = filename.substring(filename.lastIndexOf("/") + 1);

                ftpClient.changeWorkingDirectory("/");
                boolean changed = ftpClient.changeWorkingDirectory(remoteDir);
                if (!changed) throw new ResourceNotFoundException("Dossier introuvable : " + remoteDir);
            }

            final InputStream is = ftpClient.retrieveFileStream(actualFileName);

            if (is == null) {
                int replyCode = ftpClient.getReplyCode();
                throw new ResourceNotFoundException("Fichier introuvable. Code FTP: " + replyCode);
            }

            StreamingOutput stream = output -> {
                try {
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }
                    output.flush();
                } finally {
                    ftpClient.completePendingCommand();
                    FTPManager.disconnect(ftpClient);
                }
            };

            return Response.ok(stream)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .build();

        } catch (FlopBoxException e) {
            return buildErrorResponse(e);
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Téléverse un fichier vers le serveur FTP en mode binaire.
     * @param filename Le nom du fichier sur le serveur distant.
     * @param fileStream Le flux de données issu du formulaire Multipart.
     * @param alias    L'alias du serveur cible.
     * @param mode     Le mode de connexion FTP (actif/passif).
     * @param user     Identifiant utilisateur FlopBox (Header).
     * @param password Mot de passe utilisateur FlopBox (Header).
     * @param ftpUser  Identifiant de connexion au serveur FTP distant (Header).
     * @param ftpPass  Mot de passe de connexion au serveur FTP distant (Header).
     * @return 201 Created si l'upload est accepté par le serveur FTP.
     */
    @POST
    @Path("/{alias}/files/{filename: .*}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @PathParam("filename") String filename,
            @FormDataParam("file") InputStream fileStream,
            @PathParam("alias") String alias,
            @HeaderParam("ftp-mode") String mode,
            @HeaderParam("user") String user,
            @HeaderParam("password") String password,
            @HeaderParam("ftp-user") String ftpUser,
            @HeaderParam("ftp-pass") String ftpPass) {


        FTPClient ftpClient = null;
        try (fileStream) {
            validateAccess(user, password, alias);

            ftpClient = createFtpSession(alias, ftpUser, ftpPass, mode);

            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);

            String actualFileName = filename;
            if (filename.contains("/")) {
                String remoteDir = filename.substring(0, filename.lastIndexOf("/"));
                actualFileName = filename.substring(filename.lastIndexOf("/") + 1);

                ftpClient.changeWorkingDirectory("/");

                String[] folders = remoteDir.split("/");
                for (String folder : folders) {
                    if (folder.isEmpty()) continue;
                    boolean changed = ftpClient.changeWorkingDirectory(folder);
                    if (!changed) {
                        if (ftpClient.makeDirectory(folder)) {
                            ftpClient.changeWorkingDirectory(folder);
                        } else {
                            throw new FtpOperationException("Impossible de créer le répertoire distant : " + folder);
                        }
                    }
                }
            }

            boolean success = ftpClient.storeFile(actualFileName, fileStream);
            if (!success) throw new FtpOperationException("Le serveur FTP a refusé le stockage du fichier.");

            return Response.status(201).entity("Upload binaire réussi").build();
        } catch (FlopBoxException e) {
            return buildErrorResponse(e);
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Crée un nouveau répertoire sur le serveur FTP distant.
     *
     * @param alias    L'alias du serveur cible.
     * @param dirname  Le nom du dossier à créer.
     * @param mode     Le mode de connexion FTP (actif/passif).
     * @param user     Identifiant utilisateur FlopBox (Header).
     * @param password Mot de passe utilisateur FlopBox (Header).
     * @param ftpUser  Identifiant de connexion au serveur FTP distant (Header).
     * @param ftpPass  Mot de passe de connexion au serveur FTP distant (Header).
     * @return {@link Response} 201 (Created) en cas de succès, 500 en cas d'échec de la commande FTP.
     */
    @POST
    @Path("/{alias}/directory/{dirname}")
    public Response createDir(@PathParam("alias") String alias,
                              @PathParam("dirname") String dirname,
                              @HeaderParam("ftp-mode") String mode,
                              @HeaderParam("user") String user,
                              @HeaderParam("password") String password,
                              @HeaderParam("ftp-user") String ftpUser,
                              @HeaderParam("ftp-pass") String ftpPass) {
        try {
            validateAccess(user, password, alias);

            FTPClient ftp = createFtpSession(alias, ftpUser, ftpPass, mode);

            boolean ok = ftp.makeDirectory(dirname);
            FTPManager.disconnect(ftp);

            return ok ? Response.status(201).entity("Dossier créé").build()
                    : Response.status(500).entity("Échec création").build();
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        } catch (FlopBoxException e) {
            return buildErrorResponse(e);
        }
    }
    /**
     * Supprime un fichier ou un répertoire (si vide) sur le serveur FTP.
     * @param alias    L'alias du serveur cible.
     * @param filename Le nom de la ressource à supprimer.
     * @param mode     Le mode de connexion FTP (actif/passif).
     * @param user     Identifiant utilisateur FlopBox (Header).
     * @param password Mot de passe utilisateur FlopBox (Header).
     * @param ftpUser  Identifiant de connexion au serveur FTP distant (Header).
     * @param ftpPass  Mot de passe de connexion au serveur FTP distant (Header).
     * @return 200 OK ou 404 si la ressource est introuvable.
     */
    @DELETE
    @Path("/{alias}/files/{filename: .*}")
    public Response delete(@PathParam("alias") String alias,
                           @PathParam("filename") String filename,
                           @HeaderParam("ftp-mode") String mode,
                           @HeaderParam("user") String user,
                           @HeaderParam("password") String password,
                           @HeaderParam("ftp-user") String ftpUser,
                           @HeaderParam("ftp-pass") String ftpPass) {
        FTPClient ftp = null;
        try {
            validateAccess(user, password, alias);

            ftp = createFtpSession(alias, ftpUser, ftpPass, mode);
            FTPFile[] details = ftp.listFiles(filename);

            boolean success;
            success = ftp.deleteFile(filename);

            if (!success) {
                try {
                    FTPManager.deleteRecursiveFTP(ftp, filename);
                    success = true;
                } catch (IOException e) {
                    success = false;
                }
            }

            if (!success) {
                throw new ResourceNotFoundException("Impossible de supprimer : " + filename + ". Vérifiez si la ressource existe.");
            }

            return Response.ok("Supprimé avec succès").build();
        } catch (FlopBoxException e) {
            return buildErrorResponse(e);
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        } finally {
            FTPManager.disconnect(ftp);
        }
    }

    /**
     * Renomme un fichier ou un dossier distant sur le serveur FTP.
     * <p>
     * Cette opération utilise la commande FTP {@code RNFR} (Rename From) et {@code RNTO} (Rename To).
     * Elle nécessite une validation préalable des droits d'accès de l'utilisateur FlopBox
     * sur l'alias concerné.
     * </p>
     *
     * @param alias    L'alias du serveur cible.
     * @param mode     Le mode de connexion FTP (actif/passif).
     * @param oldname  Le nom actuel de la ressource (chemin relatif).
     * @param newName  Le nouveau nom souhaité (via le paramètre de requête 'new').
     * @param user     Identifiant utilisateur FlopBox (Header).
     * @param password Mot de passe utilisateur FlopBox (Header).
     * @param ftpUser  Identifiant de connexion au serveur FTP distant (Header).
     * @param ftpPass  Mot de passe de connexion au serveur FTP distant (Header).
     * @return {@link Response} 200 (OK) si le renommage est réussi, 500 (Internal Server Error) sinon.
     */
    @PUT
    @Path("/{alias}/files/{oldname}")
    public Response rename(@PathParam("alias") String alias,
                           @HeaderParam("ftp-mode") String mode,
                           @PathParam("oldname") String oldname,
                           @QueryParam("new") String newName,
                           @HeaderParam("user") String user,
                           @HeaderParam("password") String password,
                           @HeaderParam("ftp-user") String ftpUser,
                           @HeaderParam("ftp-pass") String ftpPass) {
        try {
            validateAccess(user, password, alias);

            FTPClient ftp = createFtpSession(alias, ftpUser, ftpPass, mode);

            boolean ok = ftp.rename(oldname, newName);
            FTPManager.disconnect(ftp);

            return ok ? Response.ok("Renommé en " + newName).build() : Response.status(500).build();
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        } catch (FlopBoxException e) {
            return buildErrorResponse(e);
        }
    }

    /**
     * Effectue une recherche de fichiers par mot-clé sur l'ensemble des serveurs
     * auxquels l'utilisateur a accès.
     *
     * @param keyword  Le nom ou fragment de nom de fichier recherché.
     * @param mode     Le mode de connexion FTP (actif/passif).
     * @param fUser    Identifiant utilisateur FlopBox (Header).
     * @param fPass    Mot de passe utilisateur FlopBox (Header).
     * @param ftpUser  Identifiant de connexion au serveur FTP distant (Header).
     * @param ftpPass  Mot de passe de connexion au serveur FTP distant (Header).
     * @return La liste des occurrences trouvées avec leurs URLs d'accès respectives.
     */
    @GET
    @Path("/search")
    @Produces(MediaType.TEXT_PLAIN)
    public Response searchGlobal(
            @QueryParam("name") String keyword,
            @HeaderParam("ftp-mode") String mode,
            @HeaderParam("user") String fUser,
            @HeaderParam("password") String fPass,
            @HeaderParam("ftp-user") String ftpUser,
            @HeaderParam("ftp-pass") String ftpPass) {

        AuthService auth = new AuthService();
        StringBuilder results = new StringBuilder("Résultats de recherche pour : " + keyword + "\n");

        try {
            List<String> servers = auth.getUserServers(fUser, fPass);

            for (String alias : servers) {

                FTPClient ftp = null;
                try {
                    ftp = FtpSessionFactory.createFtpClient(alias, ftpUser, ftpPass, mode);
                    FTPManager.searchRecursive(ftp, "/", keyword, alias, results);
                } catch (Exception e) {
                    results.append("[!] Impossible de scanner ").append(alias).append(" (Erreur de connexion)\n");
                } finally {
                    if (ftp != null){
                        try { FTPManager.disconnect(ftp); } catch (Exception ignored) {}
                    }
                }
            }

            return Response.ok(results.toString()).build();

        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        } catch (UnauthorizedException e){
            return buildErrorResponse(e);
        }
    }

    /**
     * Valide l'accès double : Identifiants FlopBox ET droit d'accès à l'alias du serveur.
     *
     * @param u Utilisateur FlopBox.
     * @param p Mot de passe FlopBox.
     * @param a Alias du serveur cible.
     * @throws FlopBoxException Forbidden (403) ou Unauthorized (401).
     */
    private void validateAccess(String u, String p, String a) throws FlopBoxException {
        try {
            if (!authService.checkUserOnly(u, p)) {
                throw new UnauthorizedException("Identifiants FlopBox incorrects pour l'utilisateur : " + u);
            }

            if (!authService.validateAccess(u, p, a)) {
                throw new ForbiddenException("L'utilisateur '" + u + "' n'est pas autorisé à accéder au serveur '" + a + "'.");
            }
        }catch (IOException e){
            throw new FtpOperationException("Erreur lors de la validation des accès : " + e.getMessage());        }
    }

    /**
     * Initialise une session FTP connectée et authentifiée via la {@link FtpSessionFactory}.
     * @param alias L'alias du serveur (Hôte:Port).
     * @param u     Le nom d'utilisateur FTP.
     * @param p     Le mot de passe FTP.
     * @param m     Le mode de transfert (active/passive).
     * @return Une instance de {@link FTPClient} prête à l'emploi.
     * @throws FlopBoxException Si la connexion ou l'authentification FTP échoue.
     */
    private FTPClient createFtpSession(String alias, String u, String p, String m) throws FlopBoxException {
        return FtpSessionFactory.createFtpClient(alias, u, p, m);
    }

    /**
     * Génère une réponse HTTP d'erreur basée sur une exception personnalisée FlopBox.
     * <p>
     * Cette méthode centralise la conversion des codes d'erreur métier en codes de statut HTTP
     * (ex: {@code ForbiddenException} devient un {@code 403}).
     * </p>
     *
     * @param e L'exception métier capturée.
     * @return Une instance de {@link Response} contenant le message d'erreur et le statut HTTP adéquat.
     */
    private Response buildErrorResponse(FlopBoxException e) {
        return Response.status(e.getStatus()).entity(e.getMessage()).build();
    }

}