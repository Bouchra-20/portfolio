package com.flopbox.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour les utilitaires de sécurité.
 */
public class SecurityUtilsTest {

    /**
     * Vérifie que le hachage produit toujours le même résultat pour une entrée donnée.
     */
    @Test
    public void testHashPassword_Consistency() {
        String pass = "monMotDePasse123";
        String salt = "xyz123";

        String hash1 = SecurityUtils.hashPassword(pass, salt);
        String hash2 = SecurityUtils.hashPassword(pass, salt);

        assertNotNull(hash1);
        assertEquals(hash1, hash2, "Le hachage doit être déterministe");
    }

    /**
     * Vérifie que deux sels générés successivement sont différents.
     */
    @Test
    public void testGenerateSalt_Uniqueness() {
        String salt1 = SecurityUtils.generateSalt();
        String salt2 = SecurityUtils.generateSalt();

        assertNotEquals(salt1, salt2, "Deux sels ne devraient pas être identiques");
        assertEquals(24, salt1.length(), "Le sel Base64 de 16 octets doit faire 24 caractères");
    }
}