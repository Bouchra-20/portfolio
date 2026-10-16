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
 * Classe de test unitaire pour la commande FTP 'CDUP' (Change to Parent Directory).
 * <p>
 * Cette classe vérifie que le serveur permet à l'utilisateur de remonter correctement
 * vers le répertoire parent tout en respectant les limites de la racine du serveur.
 * </p>
 * * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class CDUPCommandTest {

    /** Répertoire temporaire isolé créé pour chaque exécution de test. */
    @TempDir
    Path tempDir;

    private ClientHandler handler;
    private StringWriter sw;
    private PrintWriter out;

    /**
     * Initialise l'environnement de test avant chaque méthode.
     * Configure un ClientHandler authentifié pointant vers le répertoire temporaire.
     */
    @BeforeEach
    void setUp() {
        handler = new ClientHandler(null, tempDir.toString(), null);
        handler.setAuthentified(true);
        sw = new StringWriter();
        out = new PrintWriter(sw);
    }

    /**
     * Teste le succès de la remontée vers le répertoire parent.
     * <p>
     * Scénario : L'utilisateur est dans "/subdir" et appelle CDUP.
     * Résultat attendu : Le chemin relatif doit devenir "/" et le code 250 doit être renvoyé.
     * </p>
     */
    @Test
    public void testCDUPSuccess() {
        // Préparation du dossier cible
        tempDir.resolve("subdir").toFile().mkdir();
        handler.setCurrentRelativePath("/subdir");

        CDUPCommand cdup = new CDUPCommand(out, handler);
        cdup.execute(null);
        out.flush();

        // Assertions
        assertEquals("/", handler.getCurrentRelativePath(), "Le chemin devrait être revenu à la racine.");
        assertTrue(sw.toString().contains("250"), "Le code de réponse FTP doit être 250 (Directory changed).");
    }
}