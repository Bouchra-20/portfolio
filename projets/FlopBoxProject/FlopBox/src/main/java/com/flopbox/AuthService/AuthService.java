package com.flopbox.AuthService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flopbox.exceptions.FlopBoxException;
import com.flopbox.exceptions.UnauthorizedException;
import com.flopbox.exceptions.UserAlreadyExistsException;
import com.flopbox.security.SecurityUtils;
import com.flopbox.user.User;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service gérant l'authentification des utilisateurs, la persistance des données
 * et les autorisations d'accès aux serveurs FTP.
 * <p>
 * Ce service utilise un fichier JSON local pour stocker les informations des utilisateurs,
 * incluant leurs identifiants hachés et leurs alias de serveurs autorisés.
 * </p>
 *
 * @author Bouchra Bouamama
 * @version 1.0
 */
public class AuthService {
    /** Chemin vers le fichier de stockage des utilisateurs. */
    private String usersFile = "users.json";

    /** Instance de Jackson pour la sérialisation/désérialisation JSON. */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructeur par défaut utilisant le fichier de stockage standard.
     */
    public AuthService() {}

    /**
     * Constructeur permettant de spécifier un fichier de stockage personnalisé.
     * Utile pour isoler les tests unitaires de la production.
     * @param customFilePath Chemin vers le fichier JSON de test.
     */
    public AuthService(String customFilePath) {
        this.usersFile = customFilePath;
    }
    /**
     * Charge la liste complète des utilisateurs depuis le fichier JSON.
     * @return Une liste d'objets {@link User}. Retourne une liste vide si le fichier n'existe pas.
     * @throws IOException Si une erreur survient lors de la lecture du fichier.
     */
    private List<User> loadAllUsers() throws IOException {
        File file = new File(this.usersFile);
        if (!file.exists()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(mapper.readValue(file, User[].class)));
    }

    /**
     * Enregistre un nouvel utilisateur dans le système FlopBox.
     * Le mot de passe est haché avec un sel unique avant d'être stocké.
     *
     * @param username    Le nom d'utilisateur choisi.
     * @param rawPassword Le mot de passe en clair à hacher.
     * @throws IOException Si une erreur survient lors de l'écriture sur le disque.
     * @throws FlopBoxException Si l'utilisateur existe déjà ({@link UserAlreadyExistsException}).
     */
    public void registerUser(String username, String rawPassword) throws IOException, FlopBoxException {
        List<User> allUsers = loadAllUsers();

        for (User u : allUsers) {
            if (u.username.equalsIgnoreCase(username)) {
                throw new UserAlreadyExistsException("L'utilisateur " + username + " existe déjà.");
            }
        }

        String salt = SecurityUtils.generateSalt();
        String hashedPassword = SecurityUtils.hashPassword(rawPassword, salt);

        User newUser = new User();
        newUser.username = username;
        newUser.password = hashedPassword;
        newUser.salt = salt;
        newUser.allowedServers = new ArrayList<>();

        allUsers.add(newUser);

        saveAllUsers(allUsers);
    }

    /**
     * Valide les identifiants d'un utilisateur et vérifie s'il possède les droits
     * d'accès sur un serveur FTP spécifique.
     *
     * @param inputUser      Le nom d'utilisateur FlopBox.
     * @param inputPass      Le mot de passe FlopBox associé.
     * @param requestedAlias L'alias du serveur FTP visé.
     * @return {@code true} si l'authentification réussit et que l'accès est autorisé, {@code false} sinon.
     * @throws IOException Si une erreur de lecture du fichier survient.
     */
    public boolean validateAccess(String inputUser, String inputPass, String requestedAlias) throws IOException {
        List<User> allUsers = loadAllUsers();

        for (User u : allUsers) {
            if (u.username.equals(inputUser)) {
                String hashedInput = SecurityUtils.hashPassword(inputPass, u.salt);

                if (u.password.equals(hashedInput)) {
                    return u.allowedServers.contains(requestedAlias);
                }
            }
        }
        return false;
    }

    /**
     * Ajoute un alias de serveur à la liste des serveurs autorisés pour un utilisateur.
     *
     * @param username Le nom de l'utilisateur.
     * @param newAlias L'alias du serveur à ajouter.
     * @throws IOException Si une erreur survient lors de la mise à jour du fichier.
     */
    public void addServer(String username, String newAlias) throws IOException {
        List<User> allUsers = loadAllUsers();

        for (User u : allUsers) {
            if (u.username.equals(username)){
                if(!u.allowedServers.contains(newAlias)){
                    u.allowedServers.add(newAlias);
                }
                break;
            }
        }
        saveAllUsers(allUsers);
    }

    /**
     * Supprime un alias de serveur de la liste des serveurs autorisés d'un utilisateur.
     *
     * @param username      Le nom de l'utilisateur.
     * @param aliasToRemove L'alias du serveur à retirer.
     * @throws IOException Si une erreur survient lors de la mise à jour du fichier.
     */
    public void removeServer(String username, String aliasToRemove) throws IOException {
        List<User> allUsers = loadAllUsers();

        for (User u : allUsers) {
            if (u.username.equals(username)){
                u.allowedServers.remove(aliasToRemove);
                break;
            }
        }
        saveAllUsers(allUsers);
    }

    /**
     * Vérifie uniquement la validité des identifiants (Login/Password) sans vérifier les serveurs.
     *
     * @param user     Le nom d'utilisateur.
     * @param password Le mot de passe.
     * @return {@code true} si les identifiants sont corrects, {@code false} sinon.
     * @throws IOException Si une erreur survient lors de la lecture.
     */
    public boolean checkUserOnly(String user, String password) throws IOException {
        List<User> allUsers = loadAllUsers();

        for (User u : allUsers) {
            if (u.username.equals(user)) {
                String hashedInput = SecurityUtils.hashPassword(password, u.salt);

                if (u.password.equals(hashedInput)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Récupère la liste de tous les alias de serveurs autorisés pour un utilisateur donné.
     *
     * @param username Le nom de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     * @return Une liste de chaînes de caractères contenant les alias autorisés.
     * @throws IOException           Si une erreur survient lors de la lecture.
     * @throws UnauthorizedException Si l'utilisateur n'existe pas ou si le mot de passe est incorrect.
     */
    public List<String> getUserServers(String username, String password) throws IOException, UnauthorizedException {
        List<User> allUsers = loadAllUsers();
        for (User u : allUsers) {
            if (u.username.equals(username)) {
                String hashedInput = SecurityUtils.hashPassword(password, u.salt);

                if (u.password.equals(hashedInput)) {
                    return u.allowedServers;
                }
                throw new UnauthorizedException("Mot de passe incorrect.");
            }
        }
        throw new UnauthorizedException("Utilisateur non trouvé.");
    }

    /**
     * Sauvegarde la liste des utilisateurs dans le fichier JSON avec un formatage lisible.
     *
     * @param users La liste des utilisateurs à sauvegarder.
     * @throws IOException Si une erreur survient lors de l'écriture du fichier.
     */
    private void saveAllUsers(List<User> users) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(this.usersFile), users);
    }
}