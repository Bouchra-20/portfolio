package com.flopbox.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Représente un utilisateur du système FlopBox.
 * <p>
 * Cette classe est utilisée par Jackson pour mapper les données entre les objets Java
 * et le fichier de stockage {@code users.json}.
 * </p>
 */
public class User {
    /** Nom unique identifiant l'utilisateur. */
    @JsonProperty("username")
    public String username;

    /** Empreinte SHA-256 du mot de passe. */
    @JsonProperty("password")
    public String password;

    /** Sel unique utilisé pour le hachage de cet utilisateur. */
    @JsonProperty("salt")
    public String salt;

    /** Liste des alias de serveurs FTP que cet utilisateur est autorisé à consulter. */
    @JsonProperty("allowedServers")
    public List<String> allowedServers;

    /** Constructeur par défaut requis pour la désérialisation Jackson. */
    public User() {}

    /**
     * Constructeur complet pour la création d'un nouvel utilisateur.
     * @param username       Nom d'utilisateur.
     * @param password       Mot de passe haché.
     * @param salt           Sel cryptographique.
     * @param allowedServers Liste initiale des serveurs autorisés.
     */
    public User(String username, String password, String salt, List<String> allowedServers) {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.allowedServers = allowedServers;
    }
}