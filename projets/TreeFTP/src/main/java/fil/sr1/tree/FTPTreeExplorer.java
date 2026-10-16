package fil.sr1.tree;

import fil.sr1.client.FTPConnection;
import fil.sr1.client.FTPException;
import fil.sr1.tree.utils.Document;
import fil.sr1.tree.utils.DocumentType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Responsable de l'exploration récursive de l'arborescence du serveur FTP.
 * Utilise le mode passif (PASV) pour ouvrir des canaux de données et récupérer
 * la liste des fichiers.
 * @author Bouchra Bouamama
 */
public class FTPTreeExplorer {

    private FTPConnection ftp;


    private Socket dataSocket;
    private BufferedReader dataReader;

    private Tree tree;
    private int max;

    /**
     * @param ftp La connexion de contrôle active à utiliser.
     */
    public FTPTreeExplorer(FTPConnection ftp) {
        this.ftp = ftp;
    }

    /**
     * Initialise et retourne l'arbre représentant l'arborescence distante.
     * @param max La profondeur maximale d'exploration (-1 pour illimité).
     * @return L'objet Tree complété.
     * @throws IOException En cas d'erreur de flux.
     * @throws FTPException En cas d'erreur protocolaire.
     */
    public Tree getTree(int max) throws IOException, FTPException {
        if (this.tree == null) {
            this.tree = new Tree();
            this.max = max;
            exploreRecursively(this.tree.getRoot(), 0);

        }
        return this.tree;
    }

/**
 * Calcule le port de données à partir de la réponse PASV du serveur.
 * Formule : (p1 * 256) + p2.
 * @param data La réponse brute du serveur (ex: 227 Entering Passive Mode (h1,h2,h3,h4,p1,p2)).
 * @return Le port TCP calculé pour le canal de données.
 */
    private int calculatePort(String data){
        String inside = data.substring(data.indexOf("(") + 1, data.indexOf(")"));
        String[] parts = inside.split(",");

        int p1 = Integer.parseInt(parts[4]);
        int p2 = Integer.parseInt(parts[5]);
        int port = (p1 * 256) + p2;
        return port;
    }

    /**
     * Envoie la commande PASV et ouvre une nouvelle socket de données sur le port fourni par le serveur.
     * @throws FTPException Si le serveur refuse le mode passif.
     */
    private void enterPassiveMode() throws FTPException {
        String response = this.ftp.executeCommand("PASV");
        if (!response.startsWith("227")) {
            throw new FTPException("PASV failed: " + response);
        }

        String host = this.ftp.connectionSocket().getInetAddress().getHostAddress();

        try {
            dataSocket = new Socket(host, this.calculatePort(response));
            dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
        } catch (IOException e) {
            throw new DataException("Erreur opening data channel", e);
        }
    }

    /**
     * Récupère la liste brute des fichiers du répertoire courant via le canal de données.
     * @return Une liste de chaînes de caractères (une par fichier/dossier).
     * @throws FTPException Si la commande LIST est refusée ou interrompue.
     */
    public ArrayList<String> list() throws FTPException {
        enterPassiveMode();
        try (Socket s = this.dataSocket;
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()))) {

            String response = this.ftp.executeCommand("LIST");
            if (!(response.startsWith("150") || response.startsWith("125"))) {
                throw new FTPException("LIST refused: " + response);
            }

            ArrayList<String> files = new ArrayList<>();

            String line;
            while ((line = dataReader.readLine()) != null) {
                files.add(line);
            }

            String res226 = this.ftp.readResponse();
            if (this.ftp.isDebugging()){
                this.ftp.ResponseDebug(res226);
            }

            if (!res226.startsWith("226")) {
                throw new FTPException("LIST not completed: " + res226);
            }

            return files;

        } catch (IOException e) {
            throw new DataException("Error during LIST", e);
        }
    }

    /**
     * Algorithme récursif de parcours en profondeur de l'arborescence.
     * Se déplace dans les dossiers via CWD et remonte via CDUP.
     * @param node Le nœud actuel dans l'arbre local.
     * @param level La profondeur actuelle de récursion.
     * @throws FTPException En cas d'erreur de commande FTP.
     * @throws IOException En cas d'erreur réseau.
     */
    public void exploreRecursively(Node node, int level)
            throws FTPException, IOException {
        if(this.max != -1 && level >= this.max){
            return;
        }
        ArrayList<String> files;
        try {
            files = list();
        } catch (FTPException e) {
            System.err.println("Skipping directory (LIST failed): " + node.getName());
            return;
        }
        for (String file : files) {
            Document doc = Document.getDocumentFromData(file);
            Node child = new Node(doc.toString());
            node.addChild(child);

            if (doc.type() == DocumentType.DIRECTORY &&
                    !doc.toString().equals(".") &&
                    !doc.toString().equals("..")) {

                try {
                    ftp.changeDirectory(doc.toString());
                    exploreRecursively(child, level + 1);
                    ftp.goUpDirectory();
                } catch (FTPException e) {
                    System.err.println("Permission denied or error for: " + doc.toString());
                }
            }
        }
    }


}