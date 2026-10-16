package org.example.command;

/**
 * Interface fonctionnelle définissant le contrat pour l'exécution des commandes FTP.
 * <p>
 * Chaque commande implémentée (CWD, RETR, etc.) doit réaliser cette interface
 * pour permettre un traitement polymorphique des requêtes clients.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public interface CommandExecutor {
    /**
     * Exécute la logique métier associée à une commande FTP spécifique.
     * @param argument L'argument optionnel fourni avec la commande (ex: le nom d'un fichier ou d'un chemin).
     */
    void execute(String argument);
}