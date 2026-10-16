package org.example.command;

import static org.junit.jupiter.api.Assertions.*;
import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Classe de test unitaire pour la commande FTP 'PORT' (Active Mode).
 * <p>
 * Cette classe vérifie la capacité du serveur à parser les paramètres de connexion
 * fournis par le client (IP et Port) et à les stocker pour une utilisation ultérieure
 * lors d'un transfert de données en mode actif.
 * </p>
 * * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class PORTCommandTest {

    private ClientHandler handler;
    private StringWriter sw;
    private PrintWriter out;
    private PORTCommand command;

    /**
     * Configuration du contexte de test.
     */
    @BeforeEach
    void setUp() {
        handler = new ClientHandler(null, "root", null);
        handler.setAuthentified(true);
        sw = new StringWriter();
        out = new PrintWriter(sw);
        command = new PORTCommand(out, handler);
    }

    /**
     * Teste le parsing réussi d'une commande PORT valide.
     * <p>
     * Scénario : Le client envoie "127,0,0,1,15,165".
     * Résultat attendu : Calcul correct du port (15*256 + 165 = 4005) et réponse 200 OK.
     * </p>
     */
    @Test
    public void testPortParsingSuccess() {
        String arg = "127,0,0,1,15,165";
        command.execute(arg);
        out.flush();

        assertTrue(sw.toString().contains("200"), "Le serveur doit confirmer la réception des paramètres avec 200.");
    }

    /**
     * Vérifie la gestion d'un format de commande PORT invalide.
     * <p>
     * Teste si le serveur renvoie bien le code 501 (Syntax error in parameters)
     * lorsqu'il manque des segments dans l'argument.
     * </p>
     */
    @Test
    public void testPortInvalidFormat() {
        String arg = "127,0,0,1,15"; // Manque le dernier segment
        command.execute(arg);
        out.flush();

        assertTrue(sw.toString().contains("501"), "Le serveur doit rejeter un format incomplet avec 501.");
    }

    /**
     * Vérifie la robustesse face à des valeurs non numériques.
     */
    @Test
    public void testPortWithNonNumericValues() {
        String arg = "127,0,0,1,abc,165";
        command.execute(arg);
        out.flush();

        assertTrue(sw.toString().contains("501"), "Le serveur doit rejeter les valeurs non numériques avec 501.");
    }

    /**
     * Vérifie que la commande PORT est refusée sans authentification préalable.
     */
    @Test
    public void testPortWithoutAuthentication() {
        handler.setAuthentified(false);
        command.execute("127,0,0,1,15,165");
        out.flush();

        assertTrue(sw.toString().contains("530"), "Le code 530 est attendu pour un accès non autorisé.");
    }
}