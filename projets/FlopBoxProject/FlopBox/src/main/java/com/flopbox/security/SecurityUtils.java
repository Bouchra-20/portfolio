package com.flopbox.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Fournit des utilitaires pour la sécurisation des données utilisateurs.
 * <p>
 * Cette classe gère la génération de sels aléatoires et le hachage cryptographique
 * des mots de passe en utilisant l'algorithme SHA-256 pour garantir la confidentialité
 * des informations stockées.
 * </p>
 */
public class SecurityUtils {
    /**
     * Génère un sel (salt) aléatoire pour renforcer le hachage des mots de passe.
     * <p>
     * Utilise {@link SecureRandom} pour une génération cryptographiquement forte
     * et encode le résultat en Base64.
     * </p>
     * @return Une chaîne de caractères représentant le sel généré.
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Calcule l'empreinte numérique (hash) d'un mot de passe combiné à un sel.
     * <p>
     * L'algorithme utilisé est SHA-256. Le résultat est retourné sous forme
     * de chaîne hexadécimale.
     * </p>
     * @param password Le mot de passe en clair.
     * @param salt     Le sel à concaténer au mot de passe avant le hachage.
     * @return Le hash résultant en format hexadécimal.
     * @throws RuntimeException Si l'algorithme SHA-256 n'est pas disponible sur le système.
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String combined = password + salt;
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}