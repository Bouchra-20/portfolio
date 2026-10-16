package fil.sr1.client;

/**
 * Exception spécifique levée lors d'un échec d'authentification
 * ou d'identification sur le serveur FTP.
 * * @author Bouchra Bouamama
 */
public class LoginException extends Exception {
    /**
     * @param message Message d'erreur.
     * @param cause L'exception d'origine (FTPException ou IOException).
     */
    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }
}