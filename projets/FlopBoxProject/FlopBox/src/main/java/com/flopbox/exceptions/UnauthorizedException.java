package com.flopbox.exceptions;

import javax.ws.rs.core.Response;

/**
 * Exception lancée lorsque l'authentification FlopBox échoue.
 * <p>
 * Indique que les identifiants fournis (nom d'utilisateur ou mot de passe)
 * ne correspondent à aucun compte valide dans le système de persistance.
 * </p>
 * * @return Un code d'erreur {@code 401 Unauthorized}.
 */
public class UnauthorizedException extends FlopBoxException {

    /**
     * @param message Raison de l'échec (ex: "Mot de passe incorrect" ou "Utilisateur inconnu").
     */
    public UnauthorizedException(String message) {
        super(message, Response.Status.UNAUTHORIZED);
    }
}