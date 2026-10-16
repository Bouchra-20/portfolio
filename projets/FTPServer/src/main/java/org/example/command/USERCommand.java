package org.example.command;

import org.example.FTPCode.FTPCode;

import java.io.PrintWriter;

/**
 * Implémentation de la commande FTP 'USER'.
 * <p>
 * Première étape du processus d'authentification. Elle permet au client d'envoyer
 * son identifiant. Le serveur répond systématiquement par une demande de mot de passe (331)
 * pour ne pas divulguer l'existence ou non d'un compte (sécurité par obscurité).
 * </p>
 * @author Bouchra B0UAMAMA
 * @version 1.0
 */
public class USERCommand implements CommandExecutor {

    private String username;
    private PrintWriter out;

    /**
     * Initialise la commande USER.
     * @param out Le flux de sortie pour envoyer la réponse.
     */
    public USERCommand(PrintWriter out ) {
        this.out = out;
    }

    /**
     * Enregistre provisoirement le nom d'utilisateur et demande le mot de passe.
     * @param command Le nom d'utilisateur fourni par le client.
     */
    public void execute(String command) {
        this.username = command;
        this.out.println(FTPCode.C331);
    }
}
