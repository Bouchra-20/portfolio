package com.flopbox.ressources;

import com.flopbox.AuthService.AuthService;
import com.flopbox.exceptions.FlopBoxException;
import com.flopbox.exceptions.UnauthorizedException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Ressource JAX-RS gérant l'administration des utilisateurs et de leurs serveurs autorisés.
 * <p>
 * Fournit des points de terminaison pour l'inscription des utilisateurs et la gestion
 * (ajout, suppression, modification) de la liste blanche des alias de serveurs FTP
 * associés à chaque compte.
 * </p>
 */
@Path("/manage-servers")
public class ServerManagerRessource {
    @Inject
    private AuthService authService;
    /**
     * Enregistre un nouvel utilisateur dans le système FlopBox.
     *
     * @param newUser Le pseudonyme du nouvel utilisateur (via Header).
     * @param newPass Le mot de passe du nouvel utilisateur (via Header).
     * @return {@link Response} 201 (Created) en cas de succès, ou 409 (Conflict) si l'utilisateur existe déjà.
     */
    @POST
    @Path("/register")
    public Response signup(
            @HeaderParam("new-user") String newUser,
            @HeaderParam("new-password") String newPass) {

        try {
            if (newUser == null || newPass == null) {
                throw new BadRequestException("L'identifiant et le mot de passe sont obligatoires.");
            }
            authService.registerUser(newUser, newPass);
            return Response.status(Response.Status.CREATED)
                    .entity("Utilisateur " + newUser + " créé avec succès.")
                    .build();
        } catch (IOException e) {
            return Response.serverError().entity("Erreur serveur : " + e.getMessage()).build();
        } catch (FlopBoxException e) {
            return buildErrorResponse(e);
        }
    }

    /**
     * Associe un nouvel alias de serveur FTP au compte de l'utilisateur.
     *
     * @param alias    L'alias du serveur à ajouter (ex: "localhost:2121").
     * @param user     Identifiant de l'utilisateur effectuant l'action.
     * @param password Mot de passe de l'utilisateur.
     * @return {@link Response} 201 (Created) si l'ajout est réussi.
     */
    @POST
    @Path("/{alias}")
    public Response addServer(
            @PathParam("alias") String alias,
            @HeaderParam("user") String user,
            @HeaderParam("password") String password){

        try {
            validateFlopBoxUser(user, password);
            authService.addServer(user, alias);
            return Response.status(Response.Status.CREATED)
                    .entity("Serveur " + alias + " associé à " + user).build();
        } catch (FlopBoxException e) {
            return buildErrorResponse(e);
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Supprime un alias de serveur de la liste des autorisations de l'utilisateur.
     *
     * @param alias    L'alias du serveur à retirer.
     * @param user     Identifiant de l'utilisateur.
     * @param password Mot de passe de l'utilisateur.
     * @return {@link Response} 200 (OK) si la dissociation est effective.
     */
    @DELETE
    @Path("/{alias}")
    public Response removeServer(
            @PathParam("alias") String alias,
            @HeaderParam("user") String user,
            @HeaderParam("password") String password){
        try {
            validateFlopBoxUser(user, password);
            authService.removeServer(user, alias);
            return Response.ok("Serveur " + alias + " dissocié.").build();
        } catch (FlopBoxException e) {
            return buildErrorResponse(e);
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Met à jour un alias de serveur existant par un nouveau.
     *
     * @param oldAlias L'ancien alias à remplacer.
     * @param newAlias Le nouvel alias (via QueryParam 'new').
     * @param user     Identifiant de l'utilisateur.
     * @param password Mot de passe de l'utilisateur.
     * @return {@link Response} 200 (OK) après modification.
     */
    @PUT
    @Path("/{oldAlias}")
    public Response update(@PathParam("oldAlias") String oldAlias,
                           @QueryParam("new") String newAlias,
                           @HeaderParam("user") String user,
                           @HeaderParam("password") String password) {
        try {
            validateFlopBoxUser(user, password);

            authService.removeServer(user, oldAlias);
            authService.addServer(user, newAlias);
            return Response.ok("Serveur modifié de " + oldAlias + " vers " + newAlias).build();
        } catch (FlopBoxException e) {
            return buildErrorResponse(e);
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Valide l'existence et les identifiants d'un utilisateur FlopBox.
     *
     * @param u Nom d'utilisateur.
     * @param p Mot de passe.
     * @throws FlopBoxException Si l'authentification échoue (401).
     * @throws IOException      En cas d'erreur d'accès au fichier de persistance.
     */
    private void validateFlopBoxUser(String u, String p) throws FlopBoxException, IOException {
        if (!authService.checkUserOnly(u, p)) {
            throw new UnauthorizedException("Authentification FlopBox échouée pour : " + u);
        }
    }

    /**
     * Utilitaire pour transformer une exception métier en réponse HTTP JAX-RS.
     * @param e L'exception capturée.
     * @return Une réponse formatée avec le code statut adéquat.
     */
    private Response buildErrorResponse(FlopBoxException e) {
        return Response.status(e.getStatus()).entity(e.getMessage()).build();
    }
}
