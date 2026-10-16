package org.example.command;

import static org.junit.jupiter.api.Assertions.*;
import org.example.ClientHandler;
import org.example.FTPCode.FTPCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Classe de test unitaire pour la commande FTP 'LIST'.
 * <p>
 * Cette classe valide la capacité du serveur à énumérer les fichiers et dossiers
 * d'un répertoire et à transmettre ces informations via un canal de données secondaire.
 * </p>
 * <p>
 * Étant donné que le protocole FTP sépare les commandes des données, ces tests
 * simulent un "faux client" (ServerSocket) pour intercepter le flux de données
 * envoyé par le serveur.
 * </p>
 * * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class LISTCommandTest {

    /** Répertoire racine virtuel utilisé pour simuler le système de fichiers du serveur. */
    @TempDir
    Path tempDir;

    private ClientHandler handler;
    private StringWriter sw;
    private PrintWriter out;
    private LISTCommand command;

    /**
     * Initialise l'environnement de test avant chaque méthode.
     * Configure le handler en mode authentifié et prépare les flux de capture
     * pour le canal de contrôle.
     */
    @BeforeEach
    void setUp() {
        handler = new ClientHandler(null, tempDir.toString(), null);
        handler.setAuthentified(true);

        sw = new StringWriter();
        out = new PrintWriter(sw);
        command = new LISTCommand(handler, out);
    }

    /**
     * Teste le succès de l'énumération d'un répertoire contenant des éléments.
     * <p>
     * Scénario :
     * 1. Création d'un fichier et d'un dossier physiques.
     * 2. Simulation d'une négociation de port (Mode Actif).
     * 3. Exécution de LIST dans un thread séparé (pour éviter le blocage I/O).
     * 4. Interception et lecture des données sur la socket de test.
     * </p>
     * * @throws Exception Si une erreur réseau ou de manipulation de fichiers survient.
     */
    @Test
    public void testListSuccess() throws Exception {
        Files.createFile(tempDir.resolve("fichier1.txt"));
        Files.createDirectory(tempDir.resolve("dossier1"));

        try (ServerSocket fakeClientDataServer = new ServerSocket(0)) {
            int port = fakeClientDataServer.getLocalPort();
            handler.setActivePortInfo("127.0.0.1", port);

            Thread executionThread = new Thread(() -> command.execute(null));
            executionThread.start();

            try (Socket dataSocket = fakeClientDataServer.accept();
                 BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()))) {

                String line1 = dataReader.readLine();
                String line2 = dataReader.readLine();

                assertNotNull(line1, "Le listing doit contenir au moins un élément.");
                String fullListing = line1 + (line2 != null ? line2 : "");
                assertTrue(fullListing.contains("fichier1.txt"), "Le fichier doit apparaître dans le listing.");
                assertTrue(fullListing.contains("dossier1"), "Le dossier doit apparaître dans le listing.");
            }

            executionThread.join();
        }

        String controlResponse = sw.toString();
        assertTrue(controlResponse.contains("150"), "Le serveur doit annoncer l'ouverture du canal (150).");
        assertTrue(controlResponse.contains("226"), "Le serveur doit confirmer la fin du transfert (226).");
    }

    /**
     * Teste le comportement du serveur lors du listage d'un répertoire vide.
     * <p>
     * Résultat attendu : Le serveur doit ouvrir la connexion, n'envoyer aucune donnée
     * (flux vide) et clôturer proprement avec le code 226.
     * </p>
     * * @throws Exception Si une erreur de socket survient.
     */
    @Test
    public void testListEmptyDirectory() throws Exception {
        try (ServerSocket fakeClientDataServer = new ServerSocket(0)) {
            handler.setActivePortInfo("127.0.0.1", fakeClientDataServer.getLocalPort());

            Thread t = new Thread(() -> command.execute(null));
            t.start();

            try (Socket s = fakeClientDataServer.accept()) {
                assertEquals(-1, s.getInputStream().read(), "Le flux de données doit être vide pour un dossier vide.");
            }
            t.join();
        }
        assertTrue(sw.toString().contains("226"), "Le serveur doit clore la session avec succès.");
    }

    /**
     * Teste l'échec de la commande LIST lorsqu'aucune connexion de données n'est établie.
     * <p>
     * Scénario : Appel de LIST sans avoir exécuté PASV ou PORT au préalable.
     * Résultat attendu : Code d'erreur 425 (Can't open data connection).
     * </p>
     */
    @Test
    public void testListWithoutConnection() {
        command.execute(null);
        out.flush();

        assertTrue(sw.toString().contains("425"), "Le serveur doit refuser si aucun canal n'est préparé.");
    }
}