package com.flopbox.AuthService;

import com.flopbox.FTPManager;
import com.flopbox.exceptions.FlopBoxException;
import com.flopbox.exceptions.FtpConnectionException;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

/**
 * Factory responsable de la création et de l'initialisation des sessions FTP.
 * <p>
 * Cette classe extrait les informations de connexion (hôte, port) à partir d'un alias
 * et configure le mode de transfert (Actif/Passif) avant de solliciter le {@link FTPManager}.
 * </p>
 * @author Bouchra Bouamama
 * @version 1.0
 */
public class FtpSessionFactory {

    /**
     * Crée, configure et connecte un client FTP basé sur les paramètres fournis.
     * @param alias L'adresse du serveur (format "host:port" ou "host").
     * Si le port est omis, le port par défaut 2121 est utilisé.
     * @param fU    Le nom d'utilisateur pour la session FTP.
     * @param fP    Le mot de passe pour la session FTP.
     * @param mode  Le mode de connexion ("active" ou "passive").
     * Par défaut, le mode passif est activé si le paramètre est nul ou différent de "active".
     * @return Une instance de {@link FTPClient} connectée et authentifiée.
     * @throws FlopBoxException Si la connexion ou l'authentification échoue ({@link FtpConnectionException}).
     */
    public static FTPClient createFtpClient(String alias, String fU, String fP, String mode) throws FlopBoxException {
        try {
            String host = alias.contains(":") ? alias.split(":")[0] : alias;
            int port = alias.contains(":") ? Integer.parseInt(alias.split(":")[1].trim()) : 2121;

            boolean isPassive = !(mode != null && mode.equalsIgnoreCase("active"));

            return FTPManager.getConnectedClient(fU, fP, host, port, isPassive);
        } catch (IOException e) {
            throw new FtpConnectionException("Erreur de connexion au serveur FTP : " + e.getMessage());
        }
    }
}