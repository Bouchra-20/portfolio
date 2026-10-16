package fil.sr1.tree;

import fil.sr1.client.FTPConnection;
import fil.sr1.client.FTPException;
import fil.sr1.client.LoginException;
import fil.sr1.tree.printers.BFSPrintStrategy;
import fil.sr1.tree.printers.DFSPrintStrategy;
import fil.sr1.tree.printers.JSONPrintStrategy;

import java.io.IOException;

/**
 * Orchestrateur principal de l'application TreeFTP.
 * Cette classe gère le cycle de vie de l'application : analyse des arguments,
 * établissement de la connexion, lancement de l'exploration et rendu final.
 * * @author Bouchra Bouamama
 */
public class TreeFTP {

    private String server;
    private String user;
    private String password;
    private int port;
    private boolean printJSON;
    private boolean printBFS;
    private int maxDepth;
    private boolean debug;

    private FTPConnection ftpConnection;
    private FTPTreeExplorer ftpTreeExplorer;
    private Tree tree;

    /**
     * Construit l'orchestrateur en analysant les arguments de la ligne de commande.
     * @param args Les arguments passés au programme.
     */
    public TreeFTP(String[] args){
        parseArguments(args);
    }

    /**
     * Initialise la connexion de contrôle avec le serveur FTP.
     * @throws FTPException Si la connexion réseau échoue.
     */
    public void startConnexion() throws FTPException{
        this.ftpConnection = new FTPConnection(this.server, this.port, this.debug);
        this.ftpTreeExplorer = new FTPTreeExplorer(this.ftpConnection);
    }

    /**
     * Gère la phase d'authentification USER/PASS.
     */
    public void login() {
        try {
            if (this.debug) {
                System.out.println("Connecting to " + server + "...");
            }
            this.ftpConnection.login(this.user, this.password);
            if (this.debug) {
                System.out.println("Logging in as " + user + "...");
                System.out.println("Login successful. Ready to list tree.");
            }

        }catch (LoginException e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Déclenche l'exploration de l'arborescence et affiche les résultats selon les options choisies.
     * Par défaut, l'affichage est en profondeur (DFS).
     * @throws FTPException En cas d'erreur protocolaire.
     * @throws IOException En cas d'erreur d'entrée/sortie.
     */
    public void printTrees() throws FTPException, IOException {
        this.tree = this.ftpTreeExplorer.getTree(maxDepth);

        System.out.println("\nAffichage parcours en profondeur:\n");
        System.out.println(".");
        new DFSPrintStrategy().printTree(this.tree);
        if(this.printJSON){
            JSONPrintStrategy jsonPrintStrategy = new JSONPrintStrategy();
            String json = jsonPrintStrategy.treeToString(this.tree);
            System.out.println("\nAffichage JSON :\n" + json);
            jsonPrintStrategy.writeJsonToFile(json, "output.json");
        }
        if(this.printBFS){
            System.out.println("\nAffichage parcours en largeur:\n");
            new BFSPrintStrategy().printTree(this.tree);
        }

        this.ftpConnection.close();
    }

    /**
     * Analyseur robuste de la ligne de commande gérant les options et les paramètres positionnels.
     * @param args Tableau des arguments bruts.
     */
    private void parseArguments(String[] args) {
        this.server = args[0];
        this.user = "anonymous";
        this.password = "guest";
        this.port = 21;
        this.maxDepth = -1;

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--json": this.printJSON = true; break;
                case "--largeur": this.printBFS = true; break;
                case "--debug": this.debug = true; break;
                case "--depth":
                    if (i + 1 < args.length) this.maxDepth = Integer.parseInt(args[++i]);
                    break;
                case "--port":
                    if (i + 1 < args.length) this.port = Integer.parseInt(args[++i]);
                    break;
                default:
                    if (this.user.equals("anonymous")) this.user = args[i];
                    else if (this.password.equals("guest")) this.password = args[i];
                    break;
            }
        }
    }
}
