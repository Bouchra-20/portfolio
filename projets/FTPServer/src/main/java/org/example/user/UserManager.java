package org.example.user;

import java.util.HashMap;
/**
 * Gère la base de données des utilisateurs en mémoire.
 * @author Bouchra BOUAMAMA
 */
public class UserManager {
    /** Map associant un login à un objet {@link User}. */
    private HashMap<String,User> users;
    /**
     * Manage les utilisateurs
     */
    public UserManager() {
        this.users = new HashMap<String, User>();
    }

    /**
     * @return the users
     */
    public HashMap<String, User> getUsers() {
        return users;
    }

    /**
     * Ajoute un user à la map des utilisateurs.
     * @param user L'objet utilisateur contenant le login et le mot de passe.
     */
    public void addUser(User user) {
        users.put(user.getName(),user);
    }

    /**
     * Supprime un user à la map des utilisateurs.
     * @param user L'objet utilisateur contenant le login et le mot de passe.
     */
    public void removeUser(User user) {
        users.remove(user.getName(),user);
    }

    /**
     * Verifie que l'utilisateur existe dans la liste
     * @param user le nom d'uitlisateur
     * @return true si oui sinon false
     */
    public boolean isUser(String user) {
        return users.containsKey(user);
    }

    /**
     * Renvoie le mot de passe de l'utilisateur
     * @param userName l'userName de l'user conercnée.
     * @return Le mot de passe sous forme de chaîne, ou {@code null} si l'utilisateur est inconnu.
     */
    public String getPassword(String userName) {
        User u = users.get(userName);
        return (u != null) ? u.getPassword() : null;
    }

}
