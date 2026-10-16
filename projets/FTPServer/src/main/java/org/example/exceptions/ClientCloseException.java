package org.example.exceptions;
/**
 * Exception levée lorsqu'une connexion client doit être fermée de manière inattendue
 * ou lors d'une interruption volontaire de la session.
 */
public class ClientCloseException extends RuntimeException {
    /**
     * Construit une nouvelle exception de fermeture client.
     * @param message Le message d'erreur détaillé.
     */
    public ClientCloseException(String message) {
        super(message);
    }
}
