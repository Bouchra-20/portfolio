package org.example.command;

import static org.junit.jupiter.api.Assertions.*;
import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;
import org.example.user.UserManager;
import org.example.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Classe de test unitaire pour la commande FTP 'PASS' (Password).
 * <p>
 * Cette classe valide le mécanisme d'authentification du serveur en vérifiant
 * les interactions entre le {@link UserManager} et le {@link ClientHandler}.
 * Elle couvre les succès de connexion, les échecs de mot de passe et la gestion
 * des utilisateurs inconnus.
 * </p>
 * * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class PASSCommandTest {

    private UserManager userManager;
    private ClientHandler handler;
    private StringWriter sw;
    private PrintWriter out;

    /**
     * Prépare le gestionnaire d'utilisateurs et le contexte client avant chaque test.
     * Ajoute un utilisateur de test "admin" avec le mot de passe "123".
     */
    @BeforeEach
    void setUp() {
        User user = new User("admin", "123");
        userManager = new UserManager();
        userManager.addUser(user);

        handler = new ClientHandler(null, "root", userManager);
        sw = new StringWriter();
        out = new PrintWriter(sw);
    }

    /**
     * Teste l'authentification avec des identifiants valides.
     * <p>
     * Résultat attendu : Code 230 et l'état de l'utilisateur passe à 'authentifié'.
     * </p>
     */
    @Test
    public void testLoginSuccess() {
        PASSCommand command = new PASSCommand("admin", "123", userManager, false, out, handler);

        command.execute("root");
        out.flush();

        assertTrue(sw.toString().contains("230"), "Le serveur doit confirmer la connexion (230).");
        assertTrue(handler.isAuthentified(), "Le client devrait être authentifié.");
    }

    /**
     * Teste l'authentification avec un mot de passe incorrect.
     * <p>
     * Résultat attendu : Code 530 et accès refusé.
     * </p>
     */
    @Test
    public void testLoginFailureWrongPassword() {
        PASSCommand command = new PASSCommand("admin", "mauvais_pass", userManager, false, out, handler);

        command.execute("root");
        out.flush();

        assertTrue(sw.toString().contains("530"), "Le serveur doit rejeter le mot de passe (530).");
        assertFalse(handler.isAuthentified(), "Le client ne devrait pas être authentifié.");
    }

    /**
     * Teste la tentative de connexion avec un nom d'utilisateur inexistant.
     */
    @Test
    public void testLoginFailureUnknownUser() {
        PASSCommand command = new PASSCommand("user_inexistant", "123", userManager, false, out, handler);

        command.execute("root");
        out.flush();

        assertTrue(sw.toString().contains("530"), "L'utilisateur inconnu doit être rejeté.");
        assertFalse(handler.isAuthentified());
    }

    /**
     * Teste la robustesse de la commande si le nom d'utilisateur est nul.
     * <p>
     * Ce cas survient généralement si la commande PASS est envoyée avant la commande USER.
     * </p>
     */
    @Test
    public void testLoginNullUsername() {
        PASSCommand command = new PASSCommand(null, "123", userManager, false, out, handler);

        command.execute("root");
        out.flush();

        assertTrue(sw.toString().contains("530"), "Le serveur doit échouer si aucun utilisateur n'est défini.");
        assertFalse(handler.isAuthentified());
    }
}