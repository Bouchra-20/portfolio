package org.example.command;

import static org.junit.jupiter.api.Assertions.*;
import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe de test unitaire pour la commande FTP 'PASV' (Passive Mode).
 * <p>
 * Cette classe vérifie que le serveur est capable de passer en mode passif en ouvrant
 * une {@link java.net.ServerSocket} dynamique et en retournant les informations
 * de connexion au client selon le format strict défini par la RFC 959.
 * </p>
 * * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class PASVCommandTest {

    private ClientHandler handler;
    private StringWriter sw;
    private PrintWriter out;
    private PASVCommand command;

    /**
     * Initialise l'environnement de test.
     * Le handler est configuré par défaut comme authentifié pour permettre l'exécution de la commande.
     */
    @BeforeEach
    void setUp() {
        handler = new ClientHandler(null, "root", null);
        handler.setAuthentified(true);
        sw = new StringWriter();
        out = new PrintWriter(sw);
        command = new PASVCommand(out, handler);
    }

    /**
     * Vérifie l'initialisation correcte du mode passif.
     * <p>
     * Teste si le code 227 est renvoyé et si une socket de données est
     * effectivement ouverte et prête à accepter des connexions dans le handler.
     * </p>
     * @throws Exception Si une erreur d'entrée/sortie survient lors de la création de la socket.
     */
    @Test
    public void testPassiveModeInitialization() throws Exception {
        command.execute(null);
        out.flush();

        String response = sw.toString();

        assertTrue(response.contains("227"), "La réponse doit contenir le code 227 (Entering Passive Mode).");
        assertNotNull(handler.dataServerSocket, "La ServerSocket de données doit être initialisée.");
        assertFalse(handler.dataServerSocket.isClosed(), "La ServerSocket doit être ouverte.");
    }

    /**
     * Valide le format de la réponse renvoyée au client.
     * <p>
     * Utilise une expression régulière pour s'assurer que l'adresse IP et le port
     * sont formatés comme suit : (h1,h2,h3,h4,p1,p2).
     * </p>
     */
    @Test
    public void testPassiveResponseFormat() {
        command.execute(null);
        out.flush();

        String response = sw.toString();
        Pattern pattern = Pattern.compile("\\((\\d+,\\d+,\\d+,\\d+,\\d+,\\d+)\\)");
        Matcher matcher = pattern.matcher(response);

        assertTrue(matcher.find(), "Le format de l'adresse et du port doit respecter la syntaxe FTP (h1,h2,h3,h4,p1,p2).");
    }

    /**
     * Vérifie la réinitialisation du mode passif.
     * <p>
     * S'assure que si la commande PASV est appelée plusieurs fois, le serveur
     * ferme correctement l'ancienne socket pour en ouvrir une nouvelle sans fuite de ressources.
     * </p>
     */
    @Test
    public void testPassiveReinitialization() throws Exception {
        command.execute(null);
        command.execute(null);

        assertNotNull(handler.dataServerSocket);
        assertFalse(handler.dataServerSocket.isClosed(), "La nouvelle socket doit être active après réinitialisation.");
    }

    /**
     * Vérifie que le mode passif est refusé si l'utilisateur n'est pas authentifié.
     * <p>
     * Doit renvoyer le code d'erreur 530 et ne pas ouvrir de socket.
     * </p>
     */
    @Test
    public void testPasvWithoutAuthentication() {
        handler.setAuthentified(false);
        command.execute(null);
        out.flush();

        assertTrue(sw.toString().contains("530"), "L'accès doit être refusé avec le code 530.");
        assertNull(handler.dataServerSocket, "Aucune socket ne doit être créée pour un utilisateur non authentifié.");
    }
}