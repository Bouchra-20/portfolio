package fil.sr1;

import fil.sr1.client.FTPException;
import fil.sr1.tree.TreeFTP;
import java.io.IOException;

/**
 * Point d'entrée principal de l'application TreeFTP.
 * Initialise l'orchestrateur et traite les exceptions globales.
 * * @author Bouchra Bouamama
 */
public class Main {
    /**
     * Méthode principale exécutée au lancement du JAR.
     * @param args Arguments de la ligne de commande (Server obligatoire, User, Pass optionnels).
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar TreeFtp-1.0-SNAPSHOT.jar <server> [user] [password]");
            System.exit(1);
        }

        try {

            TreeFTP treeftp = new TreeFTP(args);
            treeftp.startConnexion();
            treeftp.login();
            treeftp.printTrees();

        } catch (FTPException | IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}