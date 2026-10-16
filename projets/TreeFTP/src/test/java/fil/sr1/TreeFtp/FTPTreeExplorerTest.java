package fil.sr1.TreeFtp;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests des calculs protocolaires de l'explorateur.
 * @author Bouchra Bouamama
 */
public class FTPTreeExplorerTest {

    /**
     * Teste la formule de calcul du port passif : (p1 * 256) + p2.
     * Basé sur un exemple de réponse PASV standard.
     */
    @Test
    public void testPassivePortCalculation() {
        int p1 = 225;
        int p2 = 220;
        int expected = 57820;
        int result = (p1 * 256) + p2;

        assertEquals("Le calcul du port FTP passif est erroné", expected, result);
    }
}