package org.example.command;

import static org.junit.jupiter.api.Assertions.*;
import org.example.ClientHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

/**
 * Classe de test unitaire pour la commande FTP 'DELE' (Delete).
 * <p>
 * Cette classe valide la suppression de fichiers sur le système de fichiers du serveur
 * et la gestion des erreurs liées aux fichiers inexistants.
 * </p>
 * * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class DELECommandTest {

    /** Répertoire racine virtuel pour simuler le stockage du serveur FTP. */
    @TempDir
    Path tempDir;

    private ClientHandler handler;
    private StringWriter sw;
    private PrintWriter out;

    /**
     * Configuration initiale du contexte de test.
     */
    @BeforeEach
    void setUp() {
        handler = new ClientHandler(null, tempDir.toString(), null);
        handler.setAuthentified(true);
        sw = new StringWriter();
        out = new PrintWriter(sw);
    }

    /**
     * Teste la suppression effective d'un fichier existant.
     * <p>
     * Vérifie que le fichier est supprimé physiquement du disque et que
     * le serveur confirme l'action avec le code 250.
     * </p>
     * @throws Exception en cas d'erreur de création de fichier temporaire.
     */
    @Test
    public void testDeleteFileSuccess() throws Exception {
        // Création d'un fichier réel pour le test
        File file = tempDir.resolve("test.txt").toFile();
        file.createNewFile();

        DELECommand command = new DELECommand(out, handler);
        command.execute("test.txt");
        out.flush();

        // Assertions
        assertTrue(sw.toString().contains("250"), "Le serveur doit confirmer la suppression (250).");
        assertFalse(file.exists(), "Le fichier physique doit avoir été supprimé du disque.");
    }

    /**
     * Teste la tentative de suppression d'un fichier qui n'existe pas.
     * <p>
     * Le serveur doit répondre avec un code d'erreur 550 (File not found).
     * </p>
     */
    @Test
    public void testDeleteNonExistingFile() {
        DELECommand command = new DELECommand(out, handler);
        command.execute("fantome.txt");
        out.flush();

        // Assertions
        assertTrue(sw.toString().contains("550"), "Le serveur doit renvoyer une erreur 550.");
    }
}