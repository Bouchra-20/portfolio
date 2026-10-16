package org.example;

import org.example.FTPCode.FTPCode;
import org.example.command.*;
import org.example.exceptions.ClientCloseException;
import org.example.exceptions.FTPClientThreadCreationException;
import org.example.user.UserManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Gère la session d'un client FTP individuel dans un thread séparé.
 * <p>
 * Cette classe maintient l'état du client (authentification, répertoire courant,
 * sockets de données) et assure le parsing ainsi que l'aiguillage des commandes
 * vers leurs exécuteurs respectifs.
 * </p>
 * @author Bouchra BOUMAMA
 * @version 1.0
 */
public class ClientHandler implements Runnable{

    /** Socket de contrôle pour l'échange de commandes et de réponses. */
    private Socket socket;

    /** Lecteur pour recevoir les commandes textuelles du client. */
    private BufferedReader in;

    /** Écrivain pour envoyer les codes de réponse au client. */
    private PrintWriter out;

    /** Chemin absolu du répertoire racine du serveur FTP sur la machine hôte. */
    private String directory;

    /** État de l'authentification du client (true si PASS a réussi). */
    private boolean isAuthentified = false;

    /** Référence vers le gestionnaire d'utilisateurs pour la validation USER/PASS. */
    private UserManager userManager;

    /** Flag indiquant si le serveur doit afficher des logs détaillés en console. */
    private boolean isTalking = false;

    /** Nom d'utilisateur fourni par la commande USER, en attente de validation par PASS. */
    private String currentUsername;

    /** Socket de données utilisée lors des transferts en mode Actif (PORT). */
    public Socket dataSocket;

    /** Socket serveur créée lors de l'initialisation du mode Passif (PASV). */
    public ServerSocket dataServerSocket;

    /** Chemin relatif actuel du client par rapport à la racine FTP (ex: "/docs"). */
    private String currentRelativePath = "/";

    /** Nom du fichier mémorisé lors d'une commande RNFR en attente d'un RNTO. */
    private String fileToRename;

    /** Adresse IP du client pour l'ouverture du canal de données en mode Actif. */
    private String activeClientIP;

    /** Port du client pour l'ouverture du canal de données en mode Actif. */
    private int activeClientPort;

    /**
     * Constructeur initialisant les flux de contrôle et le contexte de travail.
     * @param socket Socket de contrôle établie par le serveur.
     * @param directory   Répertoire racine alloué au serveur.
     * @param userManager Gestionnaire des comptes utilisateurs.
     * @throws FTPClientThreadCreationException Si l'initialisation des flux d'entrée/sortie échoue.
     */
    public ClientHandler(Socket socket, String directory, UserManager userManager) {
        this.socket = socket;
        this.directory = directory;
        this.userManager = userManager;

        if (socket != null) {
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new PrintWriter(socket.getOutputStream(), true);
            } catch (Exception e) {
                throw new FTPClientThreadCreationException(e.getMessage());
            }
        }
    }

    /**
     * Point d'entrée du thread. Envoie le message d'accueil et écoute
     * les commandes entrantes en boucle jusqu'à la déconnexion.
     */
    public void run(){
        out.println(FTPCode.C220);
        try{
            String line;
            while (true){
                line = in.readLine();
                if (line == null){
                    System.out.println("Client thread interrupted");
                    break;
                }
                else {
                    System.out.println(line);
                    parseReponse(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            this.close();
        }
    }

    /**
     * Découpe la ligne de commande reçue en un couple Commande/Argument.
     * @param line La ligne brute lue sur le flux d'entrée.
     */
    public void parseReponse(String line){
        String[] tokens = line.split(" ", 2);
        String command = tokens[0].toUpperCase();
        String arg = (tokens.length > 1) ? tokens[1] : null;

        chooseAction(command, arg);

    }

    /**
     * Établit le canal de données en fonction du mode négocié (Passif ou Actif).
     * @return Une {@link Socket} de données prête pour le transfert.
     * @throws IOException En cas d'échec de connexion ou d'acceptation.
     */
    public Socket getDataSocket() throws IOException {
        if (dataServerSocket != null) {
            Socket s = dataServerSocket.accept();
            dataServerSocket.close();
            dataServerSocket = null;
            return s;
        }
        else if (activeClientIP != null) {
            Socket s = new Socket(activeClientIP, activeClientPort);
            activeClientIP = null;
            activeClientPort = 0;
            return s;
        }
        return null;
    }

    /**
     * Enregistre les informations réseau transmises par le client pour le mode Actif.
     * Ferme également toute socket passive existante pour assurer la cohérence.
     * @param ip L'adresse IP cible du client.
     * @param port Le port cible du client.
     */
    public void setActivePortInfo(String ip, int port) {
        this.activeClientIP = ip;
        this.activeClientPort = port;
        if (dataServerSocket != null) {
            try { dataServerSocket.close(); } catch (IOException ignored) {}
            dataServerSocket = null;
        }
    }

    /**
     * Logique d'aiguillage (Dispatching) des commandes.
     * <p>
     * Vérifie si l'utilisateur est authentifié avant d'autoriser l'accès aux
     * commandes de manipulation de fichiers.
     * </p>
     * @param command Le code de la commande (ex: "RETR").
     * @param arg L'argument associé (ex: "image.png").
     */
    private void chooseAction(String command, String arg){
        if (command.equals("USER")) {
            this.currentUsername = arg;
            new USERCommand(out).execute(arg);
            return;
        }
        if (command.equals("PASS")) {
            new PASSCommand(this.currentUsername, arg, this.userManager, this.isTalking, out, this).execute(directory);
            return;
        }
        if (command.equals("QUIT")) {
            out.println(FTPCode.C221);
            this.close();
            return;
        }

        if (!isAuthentified) {
            out.println(FTPCode.C530);
            return;
        }

        switch (command) {
            case "PWD":
                new PWDCommand(out, this).execute(arg);
                break;
            case "TYPE":
                new TYPECommand(out).execute(arg);
                break;
            case "SYST":
                out.println(FTPCode.C215);
                break;
            case "PASV":
                new PASVCommand(out, this).execute(arg);
                break;
            case "LIST":
                new LISTCommand(this, out).execute(arg);
                break;
            case "FEAT":
                out.println("211-Features:\r\n PASV\r\n LIST\r\n211 End");
                break;
            case "PORT":
                new PORTCommand(out, this).execute(arg);
                break;
            case "RETR":
                new RETRCommand(out, this).execute(arg);
                break;
            case "STOR":
                new STORCommand(out, this).execute(arg);
                break;
            case "CWD":
                new CWDCommand(out, this).execute(arg);
                break;
            case "CDUP":
                new CDUPCommand(out, this).execute(null);
                break;
            case "MKD":
                new MKDCommand(out, this).execute(arg);
                break;
            case "DELE":
                new DELECommand(out, this).execute(arg);
                break;
            case "RMD":
                new RMDCommand(out, this).execute(arg);
                break;
            case "RNFR":
                new RNFRCommand(out, this).execute(arg);
                break;
            case "RNTO":
                new RNTOCommand(out, this).execute(arg);
                break;
            case "NOOP":
                out.println("200 NOOP ok.");
                break;
            default:
                out.println(FTPCode.C502 + command);
                break;
        }
    }

    /**
     * Résout un chemin demandé par le client en un chemin système absolu et sécurisé.
     * <p>
     * <b>Mécanisme de Sandboxing :</b> Cette méthode normalise le chemin et vérifie
     * impérativement qu'il commence par la racine du serveur. Si un client tente
     * de sortir (ex: ../../), une {@link SecurityException} est levée.
     * </p>
     * @param requestedPath Le chemin fourni par l'utilisateur dans sa commande.
     * @return Un {@link Path} sécurisé et pointant vers le système de fichiers réel.
     * @throws SecurityException Si le chemin résolu se situe hors de la racine autorisée.
     */
    public Path resolveSafePath(String requestedPath) throws SecurityException {
        Path root = Paths.get(this.directory).toAbsolutePath().normalize();

        Path base;
        if (requestedPath.startsWith("/")) {
            base = root;
            requestedPath = requestedPath.substring(1);
        } else {
            String rel = currentRelativePath.startsWith("/") ? currentRelativePath.substring(1) : currentRelativePath;
            base = root.resolve(rel).toAbsolutePath().normalize();
        }

        Path resolved = base.resolve(requestedPath).toAbsolutePath().normalize();

        if (!resolved.startsWith(root)) {
            throw new SecurityException("Exit root directory is forbidden");
        }

        return resolved;
    }

    /** @return Le chemin relatif actuel du client (ex: "/"). */
    public String getCurrentRelativePath() { return currentRelativePath; }

    /** @param path Définit le nouveau chemin relatif actuel du client. */
    public void setCurrentRelativePath(String path) { this.currentRelativePath = path; }

    /** @return Le chemin physique de la racine du serveur. */
    public String getDirectory(){ return directory; }

    /** @return Le nom du fichier source en attente de renommage. */
    public String getFileToRename() { return fileToRename; }

    /** @param fileToRename Définit le nom du fichier source pour la transaction de renommage. */
    public void setFileToRename(String fileToRename) { this.fileToRename = fileToRename; }

    /** @param isAuthentified Définit l'état d'authentification de la session. */
    public void setAuthentified(boolean isAuthentified) { this.isAuthentified = isAuthentified; }

    /** @return true si le client est authentifié, false sinon. */
    public boolean isAuthentified() { return this.isAuthentified; }

    /** Active le mode verbeux pour l'affichage des logs en console. */
    public void makeTalking(){ this.isTalking = true; }

     /**
     * Ferme proprement la socket de contrôle et les flux associés.
     * @throws ClientCloseException En cas d'erreur technique lors de la fermeture des ressources.
     */
    public void close(){
        try {
            this.socket.close();
            this.in.close();
            this.out.close();
        }catch (Exception e){
            throw new ClientCloseException(e.getMessage());
        }
    }


}
