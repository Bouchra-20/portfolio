package fil.sr1.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Gère la connexion de contrôle avec un serveur FTP distant via des sockets TCP.
 * Cette classe permet l'envoi de commandes textuelles et la réception de réponses
 * en respectant le protocole RFC 959.
 * * @author Bouchra Bouamama
 */
public class FTPConnection {

    private Socket controlSocket;
	private PrintWriter controlWriter;
	private BufferedReader controlReader;
    private boolean debug;

    /**
     * Initialise la connexion avec le serveur FTP et lit la bannière d'accueil.
     * @param ip L'adresse IP ou le nom d'hôte du serveur.
     * @param port Le port de connexion (généralement 21).
     * @param debug Active ou désactive l'affichage des logs de communication.
     * @throws FTPException Si la connexion échoue ou si le serveur ne répond pas par le code 220.
     */
	public FTPConnection(String ip, int port, boolean debug) throws FTPException {
		try {
            controlSocket = new Socket(ip , port);
			
			OutputStream out = controlSocket.getOutputStream();
			controlWriter = new PrintWriter(out, true);
			
			InputStream in = controlSocket.getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			controlReader = new BufferedReader(isr);

            String welcome = readResponse();
            if (welcome == null || !welcome.startsWith("220")) {
                throw new FTPException("Failed to connect to server: " + welcome);
            }
            if (debug) {
                this.ResponseDebug(welcome);
            }
		} catch (IOException e) {
			throw new FTPException("Cannot open the connexion with the server");
		}
        this.debug = debug;
	}

    /**
     * Effectue la procédure d'authentification sur le serveur.
     * @param user Le nom d'utilisateur.
     * @param pass Le mot de passe associé.
     * @throws LoginException Si l'utilisateur est rejeté ou si le mot de passe est incorrect.
     */
    public void login(String user, String pass)  throws LoginException {
        try {
            String resUser = executeCommand("USER " + user);
            if (!resUser.startsWith("331") && !resUser.startsWith("230")) {
                throw new LoginException("User rejected: " + resUser, null);
            }
            String resPass = executeCommand("PASS " + pass);
            if (!resPass.startsWith("230")) {
                throw new LoginException("Authentication failed: " + resPass, null);
            }
        } catch (FTPException e){
            throw new LoginException("Error while logging with username and password", e) ; 
        }
    }

    /**
     * Affiche une commande sortante dans la console de debug.
     * @param command La commande envoyée.
     */
    public void commandDebug(String command){
        System.out.println("> " + command);
    }
    /**
     * Affiche une réponse entrante dans la console de debug.
     * @param response La réponse reçue.
     */
    public void ResponseDebug(String response){
        System.out.println("< " + response);
    }

    /**
     * Lit une réponse provenant du serveur, en gérant les messages multi-lignes.
     * Conformément à la RFC 959, une réponse multi-lignes commence par "Code-"
     * et se termine par "Code ".
     * @return La réponse complète (une ou plusieurs lignes).
     * @throws IOException En cas d'erreur de lecture sur le flux.
     */
    public String readResponse() throws IOException {
        String line = controlReader.readLine();
        if (line == null) return null;

        if (line.length() >= 4 && line.charAt(3) == '-') {
            String code = line.substring(0, 3);
            StringBuilder multiLineResponse = new StringBuilder(line);
            while (true) {
                line = controlReader.readLine();
                if (line == null) break;
                multiLineResponse.append("\n").append(line);
                if (line.startsWith(code + " ")) break;
            }
            return multiLineResponse.toString();
        }
        return line;
    }

    /**
     * @return La socket de contrôle actuelle.
     */
    public Socket connectionSocket(){
        return controlSocket;
    }

    /**
     * Envoie une commande brute au serveur avec la terminaison CRLF.
     * @param command La chaîne de commande à envoyer.
     */
    public void sendCommand(String command) {
        controlWriter.print(command + "\r\n");
        controlWriter.flush();
    }

    /**
     * Change le répertoire de travail courant sur le serveur (Commande CWD).
     * @param path Le chemin du répertoire cible.
     * @throws IOException Erreur réseau.
     * @throws FTPException Si le serveur retourne un code d'erreur (différent de 250).
     */
    public void changeDirectory(String path) throws IOException, FTPException {
        sendCommand("CWD " + path);
        String response = readResponse();
        if (debug) {
            this.commandDebug("CWD " + path);
            this.ResponseDebug(response);
        }

        if (!response.startsWith("250")) {
            throw new FTPException("CWD failed: " + response);
        }
    }

    /**
     * Remonte au répertoire parent (Commande CDUP).
     * @throws IOException Erreur réseau.
     * @throws FTPException Si le serveur retourne un code d'erreur.
     */
    public void goUpDirectory() throws IOException, FTPException {
        sendCommand("CDUP");
        String response = readResponse();
        if (debug) {
            this.commandDebug("CDUP");
            this.ResponseDebug(response);
        }
        if (!response.startsWith("200") && !response.startsWith("250")) {
            throw new FTPException("CDUP failed: " + response);
        }
    }

    /**
     * Envoie une commande et attend la réponse complète du serveur.
     * @param command La commande FTP à exécuter.
     * @return La réponse du serveur.
     * @throws FTPException En cas d'absence de réponse ou d'erreur de lecture.
     */
    public String executeCommand(String command) throws FTPException {
        this.sendCommand(command);

        try {
            String response = readResponse();
            if (response == null) {
                throw new FTPException("No response from server");
            }

            if (debug) {
                this.commandDebug(command);
                this.ResponseDebug(response);
            }
            return response;
        } catch (IOException e) {
            throw new FTPException("Error reading response from server");
        }
    }

    /**
     * Envoie la commande QUIT et ferme proprement les flux et la socket.
     * @throws FTPException Si la fermeture de la socket échoue.
     */
	public void close() throws FTPException {
		try {
            sendCommand("QUIT");
            controlWriter.close();
            controlReader.close();
            controlSocket.close();

        } catch (IOException e) {
			throw new FTPException("Cannot close the socket");
		}
	}

    /**
     * @return L'état du mode debug.
     */
    public boolean isDebugging(){
        return this.debug;
    }
}