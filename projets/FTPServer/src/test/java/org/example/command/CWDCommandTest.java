package org.example.command;

import static org.junit.jupiter.api.Assertions.*;
import org.example.ClientHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Classe de test unitaire pour la commande FTP 'CWD' (Change Working Directory).
 * <p>
 * Cette classe vérifie la capacité du serveur à naviguer dans les sous-répertoires,
 * à gérer les dossiers inexistants et à appliquer les restrictions de sécurité
 * pour empêcher l'accès en dehors de la racine du serveur.
 * </p>
 * * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class CWDCommandTest {

    /** * Répertoire temporaire agissant comme racine isolée pour le système de fichiers FTP.
     * JUnit 5 se charge de créer et supprimer ce dossier automatiquement.
     */
    @TempDir
    Path tempDir;

    private ClientHandler handler;
    private StringWriter sw;
    private PrintWriter out;
    private CWDCommand command;

    /**
     * Initialise le contexte de test avant chaque exécution.
     * <p>
     * Configure un {@link ClientHandler} authentifié dont la racine est fixée
     * sur le répertoire temporaire pour garantir l'isolation des tests.
     * </p>
     * @throws Exception Si une erreur survient lors de l'initialisation du handler.
     */
    @BeforeEach
    void setUp() throws Exception {
        handler = new ClientHandler(null, tempDir.toString(), null);
        handler.setAuthentified(true);

        sw = new StringWriter();
        out = new PrintWriter(sw);
        command = new CWDCommand(out, handler);
    }

    /**
     * Teste le changement de répertoire vers un sous-dossier valide et existant.
     * <p>
     * Scénario : Création physique d'un dossier "photos" puis exécution de "CWD photos".
     * Résultat attendu : Code 250 et mise à jour de l'état interne du chemin relatif.
     * </p>
     * @throws Exception Si la création du dossier physique échoue.
     */
    @Test
    public void testChangeToValidSubdirectory() throws Exception {
        Path subDir = tempDir.resolve("photos");
        Files.createDirectory(subDir);

        command.execute("photos");
        out.flush();

        assertTrue(sw.toString().contains("250"), "Le code de succès 250 doit être renvoyé.");
        assertEquals("/photos", handler.getCurrentRelativePath(), "Le chemin relatif doit être mis à jour.");
    }

    /**
     * Teste le retour à la racine en utilisant le raccourci ".".
     * <p>
     * Résultat attendu : Le serveur doit rester ou revenir à la racine "/" avec un code 250.
     * </p>
     */
    @Test
    public void testChangeToRootUsingDot() {
        command.execute(".");
        out.flush();

        assertTrue(sw.toString().contains("250"), "Le code 250 doit être renvoyé pour le répertoire courant.");
        assertEquals("/", handler.getCurrentRelativePath(), "Le chemin relatif doit être la racine.");
    }

    /**
     * Teste la tentative de navigation vers un répertoire qui n'existe pas.
     * <p>
     * Résultat attendu : Code d'erreur 550 et conservation du chemin actuel.
     * </p>
     */
    @Test
    public void testChangeToNonExistentDirectory() {
        command.execute("dossier_fantome");
        out.flush();

        assertTrue(sw.toString().contains("550"), "Le serveur doit renvoyer une erreur 550.");
        assertNotEquals("/dossier_fantome", handler.getCurrentRelativePath(), "Le chemin ne doit pas être mis à jour.");
    }

    /**
     * Test de sécurité critique vérifiant la protection contre le "Path Traversal".
     * <p>
     * Scénario : Tentative d'accès à un dossier système (ex: ../../Windows) via des chemins relatifs.
     * Résultat attendu : Code 550 avec un message de refus d'accès. Le serveur ne doit pas sortir de tempDir.
     * </p>
     */
    @Test
    public void testPathTraversalSecurity() {
        command.execute("../../Windows");
        out.flush();

        assertTrue(sw.toString().contains("550"), "Le serveur doit bloquer l'accès avec une erreur 550.");
        assertTrue(sw.toString().contains("Access denied"), "Le message doit indiquer explicitement un refus d'accès.");
        assertEquals("/", handler.getCurrentRelativePath(), "Le chemin relatif doit rester inchangé à la racine.");
    }

    /**
     * Vérifie qu'il est impossible de définir un fichier régulier comme répertoire de travail.
     * <p>
     * Scénario : Création d'un fichier "test.txt" et tentative de faire "CWD test.txt".
     * Résultat attendu : Code 550 car la cible n'est pas un répertoire.
     * </p>
     * @throws Exception Si la création du fichier physique échoue.
     */
    @Test
    public void testCWDToFileInsteadOfDirectory() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.createFile(file);

        command.execute("test.txt");
        out.flush();

        assertTrue(sw.toString().contains("550"), "Le serveur doit refuser le CWD vers un fichier.");
        assertTrue(sw.toString().contains("Not a directory"), "Le message doit être explicite.");
    }
}