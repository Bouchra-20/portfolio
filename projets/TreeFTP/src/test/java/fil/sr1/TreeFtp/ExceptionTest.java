package fil.sr1.TreeFtp;

import fil.sr1.client.FTPException;
import fil.sr1.client.LoginException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Vérifie la bonne levée des exceptions personnalisées.
 * @author Bouchra Bouamama
 */
public class ExceptionTest {

    /**
     * Teste que LoginException est bien levée lors d'un échec simulé.
     * @throws LoginException l'exception attendue.
     */
    @Test(expected = LoginException.class)
    public void testLoginExceptionTrigger() throws LoginException {
        throw new LoginException("Échec authentification", new Exception());
    }

    /**
     * Teste la levée d'exception sans utiliser l'annotation 'expected'.
     * Utilise la méthode fail() pour s'assurer que le catch est atteint.
     */
    @Test
    public void testExceptionAlternativeWay() {
        try {
            if (true) throw new FTPException("Erreur protocole");
            fail("L'exception n'a pas été levée");
        } catch (FTPException e) {
            assertEquals("Erreur protocole", e.getMessage());
        }
    }
}