package org.example.exceptions;
/**
 * Exception levée en cas d'échec critique lors de l'instanciation d'un nouveau
 * thread de gestion de client (ClientHandler).
 */
public class FTPClientThreadCreationException extends RuntimeException {
    /**
     * Construit une exception liée à l'échec de création du thread client.
     * @param message Le message d'erreur détaillé.
     */
    public FTPClientThreadCreationException(String message) {
        super(message);
    }
}
