package org.example.command;

import static org.junit.jupiter.api.Assertions.*;
import org.example.ClientHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

/**
 * Classe de test unitaire pour la commande FTP 'MKD' (Make Directory).
 * <p>
 * Cette classe vérifie la capacité du serveur à créer de nouveaux répertoires
 * sur le disque et à gérer les collisions de noms (dossiers déjà existants)
 * conformément à la RFC 959.
 * </p>
 * * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class MKDCommandTest {

    /** Répertoire racine virtuel pour les tests, isolé du système de fichiers réel. */
    @TempDir
    Path tempDir;

    private ClientHandler handler;
    private StringWriter sw;
    private PrintWriter out;

    /**
     * Configure l'environnement de test avant chaque exécution.
     * Initialise un contexte client authentifié.
     */
    @BeforeEach
    void setUp() {
        handler = new ClientHandler(null, tempDir.toString(), null);
        handler.setAuthentified(true);
        sw = new StringWriter();
        out = new PrintWriter(sw);
    }

    /**
     * Teste la création réussie d'un nouveau répertoire.
     * <p>
     * Vérifie que le code de succès 257 est renvoyé et que le dossier
     * est effectivement créé sur le système de fichiers.
     * </p>
     */
    @Test
    public void testCreateDirectorySuccess() {
        MKDCommand command = new MKDCommand(out, handler);
        command.execute("nouveauDossier");
        out.flush();

        assertTrue(sw.toString().contains("257"), "Le serveur doit renvoyer 257 (Pathname created).");
        assertTrue(tempDir.resolve("nouveauDossier").toFile().exists(), "Le dossier doit exister physiquement.");
    }

    /**
     * Teste le comportement lors d'une tentative de création d'un dossier déjà existant.
     * <p>
     * Le serveur doit refuser l'opération avec un code d'erreur 550.
     * </p>
     */
    @Test
    public void testCreateExistingDirectory() {
        // Pré-création du dossier
        tempDir.resolve("existant").toFile().mkdir();

        MKDCommand command = new MKDCommand(out, handler);
        command.execute("existant");
        out.flush();

        assertTrue(sw.toString().contains("550"), "Le serveur doit renvoyer 550 (File exists).");
    }
}