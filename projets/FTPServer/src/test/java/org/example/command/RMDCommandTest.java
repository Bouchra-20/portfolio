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
 * Tests unitaires pour la commande FTP RMD (Remove Directory).
 * Cette classe valide la suppression sécurisée de répertoires.
 * @author Bouchra BOUAMAMA
 */
public class RMDCommandTest {

    @TempDir
    Path tempDir; // Crée un environnement de fichiers isolé pour chaque test

    private ClientHandler handler;
    private StringWriter sw;
    private PrintWriter out;
    private RMDCommand command;

    @BeforeEach
    void setUp() {
        // Initialisation du handler avec le dossier temporaire comme racine FTP
        handler = new ClientHandler(null, tempDir.toString(), null);
        handler.setAuthentified(true);

        sw = new StringWriter();
        out = new PrintWriter(sw);
        command = new RMDCommand(out, handler);
    }

    /**
     * Teste la suppression réussie d'un répertoire vide.
     */
    @Test
    public void testRemoveEmptyDirectorySuccess() throws Exception {
        Path subDir = tempDir.resolve("a_supprimer");
        Files.createDirectory(subDir);

        command.execute("a_supprimer");
        out.flush();

        assertTrue(sw.toString().contains("250"), "Le serveur doit répondre 250 en cas de succès");
        assertFalse(Files.exists(subDir), "Le dossier physique ne devrait plus exister");
    }

    /**
     * Teste l'échec de la suppression si le dossier contient des fichiers.
     * En FTP, RMD ne doit pas être récursif pour éviter les pertes de données accidentelles.
     */
    @Test
    public void testRemoveNotEmptyDirectoryFail() throws Exception {
        Path subDir = tempDir.resolve("pas_vide");
        Files.createDirectory(subDir);
        Files.createFile(subDir.resolve("image.jpg"));

        command.execute("pas_vide");
        out.flush();

        assertTrue(sw.toString().contains("550"), "Le serveur doit répondre 550 car le dossier n'est pas vide");
        assertTrue(Files.exists(subDir), "Le dossier ne doit pas avoir été supprimé");
    }

    /**
     * Vérifie que la commande échoue si on tente de supprimer un fichier simple avec RMD.
     */
    @Test
    public void testRemoveFileInsteadOfDirectory() throws Exception {
        Path file = tempDir.resolve("simple_fichier.txt");
        Files.createFile(file);

        command.execute("simple_fichier.txt");
        out.flush();

        assertTrue(sw.toString().contains("550"), "Doit renvoyer 550 : ce n'est pas un dossier");
        assertTrue(Files.exists(file), "Le fichier ne doit pas être supprimé par RMD");
    }

    /**
     * Test de sécurité : empêche la suppression d'un dossier en dehors de la racine.
     */
    @Test
    public void testSecurityPathTraversal() {
        // Tentative de supprimer un dossier parent imaginaire
        command.execute("../../etc");
        out.flush();

        assertTrue(sw.toString().contains("550"), "Le serveur doit interdire l'accès hors racine");
        assertTrue(sw.toString().contains("Access denied"), "Le message doit être explicite sur le refus");
    }
}