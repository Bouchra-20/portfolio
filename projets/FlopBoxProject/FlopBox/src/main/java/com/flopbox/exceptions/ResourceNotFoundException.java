package com.flopbox.exceptions;

import javax.ws.rs.core.Response;

/**
 * Exception lancée lorsqu'un fichier ou un répertoire demandé est introuvable.
 * <p>
 * Cette exception est généralement levée lors des opérations de téléchargement (download),
 * de suppression (delete) ou de navigation vers un chemin inexistant.
 * </p>
 * * @return Un code d'erreur {@code 404 Not Found}.
 */
public class ResourceNotFoundException extends FlopBoxException {
    /**
     * @param msg Le nom de la ressource ou le chemin qui n'a pas pu être localisé.
     */
    public ResourceNotFoundException(String msg) {
        super(msg, Response.Status.NOT_FOUND);
    }
}