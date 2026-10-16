package org.example.command;

import java.io.PrintWriter;
import org.example.FTPCode.FTPCode;

/**
 * Implémentation de la commande FTP 'TYPE'.
 * <p>
 * Cette commande permet au client de définir la représentation des données pour le transfert.
 * Bien que ce serveur traite techniquement les données de manière binaire (8-bit),
 * il accepte les paramètres 'I' (Image/Binaire) et 'A' (ASCII) pour assurer la
 * compatibilité avec les clients FTP standards.
 * </p>
 * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class TYPECommand implements CommandExecutor {
    private PrintWriter out;

    /**
     * Initialise la commande TYPE.
     * @param out Le flux de sortie pour envoyer la réponse.
     */
    public TYPECommand(PrintWriter out) { this.out = out; }

    /**
     * Définit le mode de transfert demandé par le client.
     * @param argument 'I' pour Binaire, 'A' pour ASCII.
     */
    @Override
    public void execute(String argument) {
        if (argument.equalsIgnoreCase("I")) {
            out.println(FTPCode.C200 + " Type set to I (Binary).");
        } else if (argument.equalsIgnoreCase("A")) {
            out.println(FTPCode.C200 + " Type set to A (ASCII).");
        } else {
            out.println(FTPCode.C504);
        }
    }
}