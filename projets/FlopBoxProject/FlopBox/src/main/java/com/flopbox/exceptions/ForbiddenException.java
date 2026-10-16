package com.flopbox.exceptions;

import javax.ws.rs.core.Response;

/**
 * Exception lancée lorsque l'utilisateur est authentifié mais n'a pas les droits
 * d'accès sur l'alias du serveur demandé (403 Forbidden).
 */
public class ForbiddenException extends FlopBoxException {
    /**
     * Constructeur de ForbiddenException.
     * @param msg Le message d'erreur détaillé.
     */
    public ForbiddenException(String msg) { super(msg, Response.Status.FORBIDDEN); }
}