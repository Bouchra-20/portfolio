package com.flopbox.exceptions;

import javax.ws.rs.core.Response;

/**
 * Exception lancée lorsqu'une commande FTP valide échoue durant son exécution.
 * <p>
 * Typiquement utilisée pour les erreurs de transfert, de création de dossier
 * ou de renommage refusés par le serveur distant malgré une connexion active.
 * </p>
 * * @return Un code d'erreur {@code 500 Internal Server Error}.
 */
public class FtpOperationException extends FlopBoxException {
    /**
     * @param msg Le message d'erreur détaillé renvoyé par le serveur FTP ou la passerelle.
     */
    public FtpOperationException(String msg) {
        super(msg, Response.Status.INTERNAL_SERVER_ERROR);
    }
}