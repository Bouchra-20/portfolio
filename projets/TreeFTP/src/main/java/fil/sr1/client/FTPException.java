package fil.sr1.client;

/**
 * Exception générique pour les erreurs liées au protocole FTP.
 * * @author Bouchra Bouamama
 */
public class FTPException extends Exception {
    /**
     * @param message Message décrivant l'erreur.
     */
    public FTPException(String message) {
        super(message);
    }
}
