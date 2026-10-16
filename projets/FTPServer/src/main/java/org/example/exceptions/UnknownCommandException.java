package org.example.exceptions;
/**
 * Exception levée lorsque le serveur reçoit une commande non reconnue ou
 * non implémentée par le protocole actuel.
 */
public class UnknownCommandException extends RuntimeException {
    /**
     * Construit une exception pour commande inconnue.
     * @param message Le message d'erreur détaillé.
     */
    public UnknownCommandException(String message) {
        super(message);
    }
}
