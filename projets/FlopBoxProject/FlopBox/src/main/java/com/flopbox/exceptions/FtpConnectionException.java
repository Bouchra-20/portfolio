package com.flopbox.exceptions;

import javax.ws.rs.core.Response;

/**
 * Exception lancée lorsque la passerelle FlopBox ne parvient pas à joindre
 * le serveur FTP cible (502 Bad Gateway).
 */
public class FtpConnectionException extends FlopBoxException {
    /**
     * Constructeur de FtpConnectionException.
     * @param msg Le message d'erreur détaillé.
     */
    public FtpConnectionException(String msg) { super(msg, Response.Status.BAD_GATEWAY); }
}