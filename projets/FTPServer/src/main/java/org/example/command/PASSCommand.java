package org.example.command;

import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;
import org.example.user.UserManager;

import java.io.File;
import java.io.PrintWriter;

import static java.lang.System.out;


/**
 * Implémentation de la commande FTP 'PASS' (Password).
 * <p>
 * Cette classe finalise le processus d'authentification initié par la commande USER.
 * Elle vérifie la correspondance entre le nom d'utilisateur mémorisé et le mot de passe fourni
 * en interrogeant le {@link UserManager}.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class PASSCommand implements CommandExecutor {
    private String username;
    private String password;
    private UserManager userManager;
    private boolean talk;
    private PrintWriter out;
    private ClientHandler clientHandler;

    /**
     * Construit une nouvelle commande PASS.
     * @param username Le nom d'utilisateur précédemment fourni via la commande USER.
     * @param password Le mot de passe fourni par le client.
     * @param userManager Le gestionnaire de la base de données utilisateurs.
     * @param talk Mode verbeux pour les logs serveur.
     * @param out Le flux de sortie pour le canal de contrôle.
     * @param clientHandler Le gestionnaire de session pour mettre à jour l'état d'authentification.
     */
    public PASSCommand(String username, String password, UserManager userManager, boolean talk, PrintWriter out, ClientHandler clientHandler) {
        this.username = username;
        this.password = password;
        this.userManager = userManager;
        this.talk = talk;
        this.out = out;
        this.clientHandler = clientHandler;
    }
    /**
     * Exécute la vérification des identifiants.
     * <p>
     * Si les identifiants sont corrects, l'état du {@link ClientHandler} est passé à authentifié
     * et le code 230 est renvoyé. Sinon, le code 530 est envoyé.
     * </p>
     * @param homeDirectory Chemin racine pour le répertoire personnel de l'utilisateur.
     */
    @Override
    public void execute(String homeDirectory) {
        if(this.username != null && userManager.isUser(this.username) && userManager.getPassword(this.username).equals(password)) {
            this.clientHandler.setAuthentified(true);
            this.out.println(FTPCode.C230);
        } else {
            this.out.println(FTPCode.C530);
        }
    }

    /**
     * Méthode utilitaire pour initialiser l'espace de stockage d'un client.
     * <p>
     * Vérifie si un répertoire au nom de l'utilisateur existe dans le homeDirectory,
     * et le crée si nécessaire.
     * </p>
     * @param homeDirectory Le chemin racine du serveur.
     * @return Le code FTP de résultat (230 succès, 451 erreur système).
     */
    private String acceptClient(String homeDirectory) {
        File dir = new File(homeDirectory+'/'+this.username);
        homeDirectory += '/'+this.username;
        if (!dir.exists() || !dir.isDirectory()){
            if(dir.mkdir()){
                if(talk)
                    System.out.println("Repertoire client crée");
                return FTPCode.C230;
            } else {
                if(talk)
                    System.out.println("Echec de la création du repertoire client.");
                return FTPCode.C451;
            }
        }
        return FTPCode.C230;
    }

}
