package fil.sr1.tree;

/**
 * Exception levée lors d'erreurs critiques durant le transfert de données
 * (problèmes de socket ou flux interrompus).
 * * @author Bouchra Bouamama
 */
public class DataException extends RuntimeException {
    /**
     * @param message Description de l'erreur.
     * @param erreur Cause originelle de l'exception.
     */
    public DataException(String message, Throwable erreur) {
        super(message, erreur);
    }
}