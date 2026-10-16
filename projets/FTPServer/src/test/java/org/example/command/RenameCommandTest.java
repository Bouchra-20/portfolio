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
 * Classe de test unitaire pour la séquence de renommage FTP (RNFR et RNTO).
 * <p>
 * Le renommage en FTP est une opération atomique en deux étapes :
 * 1. RNFR (Rename From) : Spécifie le fichier à renommer.
 * 2. RNTO (Rename To) : Spécifie le nouveau nom et exécute l'action.
 * </p>
 * <p>
 * Cette classe vérifie le respect de cet enchaînement et la gestion des erreurs
 * de séquence (code 503).
 * </p>
 * * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class RenameCommandTest {

    /** Répertoire racine virtuel pour simuler l'arborescence du serveur. */
    @TempDir
    Path tempDir;

    private ClientHandler handler;
    private StringWriter sw;
    private PrintWriter out;

    /**
     * Configuration du contexte de test avant chaque exécution.
     */
    @BeforeEach
    void setUp() {
        handler = new ClientHandler(null, tempDir.toString(), null);
        handler.setAuthentified(true);
        sw = new StringWriter();
        out = new PrintWriter(sw);
    }

    /**
     * Teste le succès complet d'une séquence de renommage.
     * <p>
     * Vérifie que le fichier source est identifié (350), que le renommage physique
     * a lieu (250) et que le système de fichiers reflète le changement.
     * </p>
     * @throws Exception Si la création du fichier de test échoue.
     */
    @Test
    public void testRenameSequenceSuccess() throws Exception {
        tempDir.resolve("ancien.txt").toFile().createNewFile();

        // Étape 1 : RNFR
        new RNFRCommand(out, handler).execute("ancien.txt");
        assertTrue(sw.toString().contains("350"), "RNFR doit répondre 350 (Requested file action pending).");
        assertEquals("ancien.txt", handler.getFileToRename(), "Le nom source doit être mémorisé dans le handler.");

        // Étape 2 : RNTO
        new RNTOCommand(out, handler).execute("nouveau.txt");
        assertTrue(sw.toString().contains("250"), "RNTO doit répondre 250 (Requested file action okay).");
        assertTrue(tempDir.resolve("nouveau.txt").toFile().exists(), "Le nouveau fichier doit exister.");
        assertFalse(tempDir.resolve("ancien.txt").toFile().exists(), "L'ancien fichier ne doit plus exister.");
    }

    /**
     * Teste la violation de la séquence de renommage.
     * <p>
     * Vérifie que l'appel de RNTO sans appel préalable de RNFR provoque une erreur 503.
     * </p>
     */
    @Test
    public void testRNTOWithoutRNFR() {
        new RNTOCommand(out, handler).execute("nouveau.txt");
        assertTrue(sw.toString().contains("503"), "Le serveur doit répondre 503 (Bad sequence of commands).");
    }
}