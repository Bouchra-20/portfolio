package com.flopbox.exceptions;

import javax.ws.rs.core.Response;

/**
 * Classe de base pour toutes les exceptions spécifiques au projet FlopBox.
 * <p>
 * Elle transporte un état HTTP ({@link Response.Status}) qui permet aux ressources JAX-RS
 * de renvoyer automatiquement le bon code d'erreur au client.
 * </p>
 */
public class FlopBoxException extends Exception {
    /** Le statut HTTP associé à l'exception. */
    private final Response.Status status;

    /**
     * Construit une nouvelle exception avec un message et un statut HTTP.
     * @param message Description détaillée de l'erreur.
     * @param status  Le code de statut HTTP associé à cette erreur.
     */
    public FlopBoxException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }

    /** @return Le statut HTTP associé à l'exception. */
    public Response.Status getStatus() { return status; }
}