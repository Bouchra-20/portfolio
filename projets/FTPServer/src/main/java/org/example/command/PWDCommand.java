package org.example.command;

import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;

import java.io.PrintWriter;

/**
 * Implémentation de la commande FTP 'PWD' (Print Working Directory).
 * <p>
 * Cette commande renvoie au client le chemin relatif actuel dans l'arborescence
 * virtuelle du serveur. Ce chemin est toujours relatif à la racine du serveur FTP
 * pour ne pas exposer la structure réelle du disque hôte.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class PWDCommand implements CommandExecutor{

    private PrintWriter out;
    private ClientHandler clientHandler;

    /**
     * Construit une commande PWD.
     * @param out Le flux de sortie vers le client.
     * @param clientHandler La session client contenant le chemin relatif actuel.
     */
    public PWDCommand(PrintWriter out, ClientHandler clientHandler) {
        this.out = out;
        this.clientHandler = clientHandler;
    }

    /**
     * Envoie le chemin actuel au format RFC 959.
     * <p>
     * La réponse doit être envoyée avec le code 257 et le chemin doit être
     * impérativement entouré de guillemets doubles.
     * </p>
     * @param command L'argument de la commande (généralement ignoré pour PWD).
     */
    @Override
    public void execute(String command) {
        String current = clientHandler.getCurrentRelativePath();
        out.println(FTPCode.C257 + " \"" + current + "\" is current directory.");
    }
}
