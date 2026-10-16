package org.example.command;

import org.example.ClientHandler;
import java.io.PrintWriter;

/**
 * Implémentation de la commande FTP 'CDUP' (Change to Parent Directory).
 * <p>
 * Cette classe est une spécialisation de la commande {@link CWDCommand}.
 * Elle permet de remonter d'un niveau dans l'arborescence des fichiers.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class CDUPCommand implements CommandExecutor {

    private PrintWriter out;
    private ClientHandler clientHandler;

    /**
     * Construit une commande CDUP.
     * @param out Le flux de sortie vers le client.
     * @param clientHandler L'état de la session client.
     */
    public CDUPCommand(PrintWriter out, ClientHandler clientHandler) {
        this.out = out;
        this.clientHandler = clientHandler;
    }

    /**
     * Exécute la commande en déléguant la logique à {@link CWDCommand} avec l'argument "..".
     * @param argument Ignoré pour cette commande (toujours "..").
     */
    @Override
    public void execute(String argument) {
        new CWDCommand(out, clientHandler).execute("..");
    }
}