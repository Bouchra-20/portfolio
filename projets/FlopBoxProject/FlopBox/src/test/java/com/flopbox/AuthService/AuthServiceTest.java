package com.flopbox.AuthService;

import com.flopbox.exceptions.UnauthorizedException;
import com.flopbox.exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthServiceTest {

    private static AuthService authService;
    private static final String TEST_FILE = "users-test.json";

    @BeforeAll
    static void setup() {
        authService = new AuthService(TEST_FILE);
    }

    @BeforeEach
    void cleanFile() {
        // Supprime le fichier avant chaque test pour repartir de zéro
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterAll
    static void tearDown() {
        new File(TEST_FILE).delete();
    }

    @Test
    @Order(1)
    @DisplayName("Devrait enregistrer un utilisateur avec succès")
    void testRegisterUser() throws Exception {
        assertDoesNotThrow(() -> authService.registerUser("leo", "password123"));
        assertTrue(authService.checkUserOnly("leo", "password123"));
    }

    @Test
    @Order(2)
    @DisplayName("Devrait lever une exception si l'utilisateur existe déjà")
    void testRegisterDuplicateUser() throws Exception {
        authService.registerUser("gab", "pass");

        assertThrows(UserAlreadyExistsException.class, () -> {
            authService.registerUser("gab", "autre_pass");
        });
    }

    @Test
    @Order(3)
    @DisplayName("Devrait valider l'accès à un serveur autorisé")
    void testValidateAccess() throws Exception {
        authService.registerUser("bob", "secret");
        authService.addServer("bob", "ftp.monserveur.com");

        assertTrue(authService.validateAccess("bob", "secret", "ftp.monserveur.com"),
                "L'accès devrait être autorisé pour ce serveur.");

        assertFalse(authService.validateAccess("bob", "secret", "inconnu.com"),
                "L'accès devrait être refusé pour un serveur non listé.");
    }

    @Test
    @Order(4)
    @DisplayName("Devrait gérer correctement l'ajout et la suppression de serveurs")
    void testAddRemoveServer() throws Exception {
        authService.registerUser("charlie", "123");

        authService.addServer("charlie", "srv1");
        List<String> servers = authService.getUserServers("charlie", "123");
        assertEquals(1, servers.size());
        assertTrue(servers.contains("srv1"));

        authService.removeServer("charlie", "srv1");
        servers = authService.getUserServers("charlie", "123");
        assertEquals(0, servers.size());
    }

    @Test
    @Order(5)
    @DisplayName("Devrait lever UnauthorizedException pour un mauvais mot de passe")
    void testAuthFailures() throws Exception {
        authService.registerUser("userTest", "correctPass");

        // Test mauvais mot de passe
        assertThrows(UnauthorizedException.class, () -> {
            authService.getUserServers("userTest", "mauvaisPass");
        });

        // Test utilisateur inexistant
        assertThrows(UnauthorizedException.class, () -> {
            authService.getUserServers("fantome", "pass");
        });
    }
}