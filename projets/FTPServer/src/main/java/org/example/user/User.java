package org.example.user;
/**
 * Représente un compte utilisateur sur le serveur FTP.
 * @author Bouchra BOUAMAMA
 */
public class User {
    /** Identifiant de l'utilisateur. */
    private String name;
    /** Mot de passe associé au compte. */
    private String password;

    /**
     * @param name Identifiant.
     * @param password Mot de passe.
     */
    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
    /** @return Le name de l'utilisateur. */
    public String getName() {
        return name;
    }
    /** @return Le mot de passe de l'utilisateur. */
    public String getPassword() {
        return password;
    }
}
