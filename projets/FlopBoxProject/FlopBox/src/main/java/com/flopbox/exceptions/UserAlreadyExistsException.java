package com.flopbox.exceptions;

import javax.ws.rs.core.Response;

/**
 * Exception lancée lors d'une tentative d'inscription avec un pseudonyme déjà utilisé.
 * <p>
 * Garantit l'unicité des comptes dans le fichier {@code users.json}.
 * </p>
 * * @return Un code d'erreur {@code 409 Conflict}.
 */
public class UserAlreadyExistsException extends FlopBoxException {

    /**
     * @param message Message d'alerte indiquant que le nom d'utilisateur est déjà réservé.
     */
    public UserAlreadyExistsException(String message) {
        super(message, Response.Status.CONFLICT);
    }
}