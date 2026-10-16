package org.example;

import org.example.user.User;
import org.example.user.UserManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test unitaire pour le gestionnaire d'utilisateurs {@link UserManager}.
 * <p>
 * Ce test vérifie le bon fonctionnement du stockage en mémoire des utilisateurs,
 * la détection des comptes existants et la récupération sécurisée des mots de passe.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
class UserManagerTest {

    /**
     * Teste le processus d'authentification et de gestion des données utilisateurs.
     * <p>
     * Scénario :
     * 1. Ajout d'un utilisateur "admin" avec le mot de passe "1234".
     * 2. Vérification que l'utilisateur est bien reconnu par le système.
     * 3. Vérification qu'un utilisateur non inscrit est rejeté.
     * 4. Validation de la correspondance du mot de passe stocké.
     * </p>
     */
    @Test
    void testUserAuthentication() {
        UserManager um = new UserManager();
        um.addUser(new User("admin", "1234"));

        assertTrue(um.isUser("admin"), "L'utilisateur 'admin' devrait être reconnu.");
        assertFalse(um.isUser("hacker"), "L'utilisateur 'hacker' ne devrait pas exister.");
        assertEquals("1234", um.getPassword("admin"), "Le mot de passe récupéré doit correspondre à celui défini.");
    }
}