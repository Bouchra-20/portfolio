package org.example;

import org.example.ClientHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test dédiée à la sécurité et au confinement des accès (Sandboxing).
 * <p>
 * L'objectif principal est de valider la méthode {@code resolveSafePath} du
 * {@link ClientHandler}, qui garantit qu'aucune commande FTP ne peut accéder
 * à des fichiers en dehors du répertoire racine assigné à l'utilisateur.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
class SecurityTest {

    private ClientHandler handler;

    /** Répertoire temporaire servant de racine sécurisée pour les tests. */
    @TempDir
    Path tempDir;

    /**
     * Initialise un environnement de test isolé avant chaque méthode.
     */
    @BeforeEach
    void setUp() {
        handler = new ClientHandler(null, tempDir.toString(), null);
    }

    /**
     * Vérifie que les chemins relatifs situés à l'intérieur de la racine sont autorisés.
     * <p>
     * Le chemin résolu doit être absolu, normalisé et commencer par le chemin de la racine.
     * </p>
     */
    @Test
    void testResolveSafePath_InsideRoot() {
        assertDoesNotThrow(() -> {
            Path path = handler.resolveSafePath("test.txt");
            assertTrue(path.startsWith(tempDir.toAbsolutePath().normalize()),
                    "Le chemin résolu doit se situer à l'intérieur du répertoire racine.");
        });
    }

    /**
     * Teste la protection contre les attaques par remontée de répertoire (Path Traversal).
     * <p>
     * Scénario : Tentative d'accès à un fichier via "../".
     * Résultat attendu : Une {@link SecurityException} doit être levée.
     * </p>
     */
    @Test
    void testResolveSafePath_OutsideRoot_ShouldThrowException() {
        assertThrows(SecurityException.class, () -> {
            handler.resolveSafePath("../forbidden_file.txt");
        }, "L'accès en dehors de la racine via '../' doit être bloqué par une SecurityException.");
    }

    /**
     * Teste la protection contre l'usage de chemins absolus système.
     * <p>
     * Scénario : Tentative d'accès direct à un dossier système Windows (C:\Windows).
     * Résultat attendu : Une {@link SecurityException} doit être levée.
     * </p>
     */
    @Test
    void testResolveSafePath_PathTraversal_ShouldThrowException() {
        String dangerousPath = "../../etc/passwd";

        assertThrows(SecurityException.class, () -> {
            handler.resolveSafePath(dangerousPath);
        }, "Le serveur doit interdire de remonter au-delà du répertoire racine (Path Traversal).");
    }
}