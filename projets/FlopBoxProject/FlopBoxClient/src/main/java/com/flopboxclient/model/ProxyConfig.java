package com.flopboxclient.model;
import java.util.Map;

/**
 * Configuration spécifique pour la connexion à un proxy FlopBox.
 */
public class ProxyConfig {
    /** URL de base de l'API du proxy. */
    public String url;

    /** Nom d'utilisateur pour s'authentifier sur le proxy. */
    public String user;

    /** Mot de passe pour s'authentifier sur le proxy. */
    public String password;

    /** Map associant un alias de serveur FTP à ses identifiants de connexion. */
    public Map<String, FtpCredentials> ftpCredentials;

    /**
     * Classe interne stockant les identifiants d'accès à un serveur FTP distant.
     */
    public static class FtpCredentials {
        /** Nom d'utilisateur FTP. */
        public String user;

        /** Mot de passe FTP. */
        public String pass;
    }
}