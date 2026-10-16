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
 * Classe de test unitaire pour la commande FTP 'RETR' (Retrieve).
 * <p>
 * Cette classe valide le transfert de fichiers du serveur vers le client.
 * Elle simule l'ouverture d'une socket de données et vérifie l'intégrité
 * des données transmises ainsi que la conformité des codes de contrôle.
 * </p>
 * * @author Bouchra BOUAMAMA
 * @version 1.0
 */
public class RETRCommandTest {

    /** Racine virtuelle du serveur pour l'isolation des tests de transfert. */
    @TempDir
    Path tempDir;

    private ClientHandler handler;
    private StringWriter controlOutput;
    private PrintWriter out;
    private RETRCommand command;

    /**
     * Préparation du handler et des flux de contrôle.
     */
    @BeforeEach
    void setUp() {
        handler = new ClientHandler(null, tempDir.toString(), null);
        handler.setAuthentified(true);
        controlOutput = new StringWriter();
        out = new PrintWriter(controlOutput);
        command = new RETRCommand(out, handler);
    }

    /**
     * Teste le téléchargement réussi d'un fichier.
     * <p>
     * Ce test utilise un thread séparé pour l'exécution de la commande afin
     * de ne pas bloquer le thread principal lors de l'attente de connexion
     * sur la socket de données simulée.
     * </p>
     * @throws Exception Si une erreur réseau survient durant la simulation du client.
     */
    @Test
    public void testRetrSuccess() throws Exception {
        String content = "Contenu secret du serveur FTP";
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, content);

        try (ServerSocket fakeClientSocket = new ServerSocket(0)) {
            handler.setActivePortInfo("127.0.0.1", fakeClientSocket.getLocalPort());

            Thread serverThread = new Thread(() -> command.execute("test.txt"));
            serverThread.start();

            try (Socket dataSocket = fakeClientSocket.accept();
                 BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()))) {

                String receivedContent = dataReader.readLine();
                assertEquals(content, receivedContent, "Les données reçues par le client doivent correspondre au fichier.");
            }
            serverThread.join();
        }

        String logs = controlOutput.toString();
        assertTrue(logs.contains("150"), "Le serveur doit envoyer 150 (File status okay).");
        assertTrue(logs.contains("226"), "Le serveur doit envoyer 226 (Closing data connection).");
    }

    /**
     * Vérifie le comportement lors de la demande d'un fichier inexistant.
     * <p>
     * Résultat attendu : Code 550 (Action not taken).
     * </p>
     */
    @Test
    public void testRetrFileNotFound() {
        command.execute("fichier_inexistant.txt");
        out.flush();

        assertTrue(controlOutput.toString().contains("550"), "Le serveur doit répondre 550 pour un fichier manquant.");
    }

    /**
     * Teste l'erreur lorsque aucune connexion de données n'est négociée au préalable.
     * <p>
     * Résultat attendu : Code 425 (Can't open data connection).
     * </p>
     * @throws IOException Si la création du fichier témoin échoue.
     */
    @Test
    public void testRetrNoConnectionNegotiated() throws IOException {
        Path file = tempDir.resolve("exist.txt");
        Files.writeString(file, "content");

        command.execute("exist.txt");
        out.flush();

        assertTrue(controlOutput.toString().contains("425"), "Le serveur doit échouer avec le code 425.");
    }

    /**
     * Vérifie la sécurité contre les tentatives de sortie du répertoire racine.
     * <p>
     * S'assure que la méthode lève une {@link SecurityException} lors de l'usage
     * de chemins malveillants (Path Traversal).
     */
    @Test
    public void testRetrPathTraversalAttempt() {
        assertThrows(SecurityException.class, () -> {
            command.execute("../../../etc/passwd");
        }, "Une SecurityException doit être levée pour protéger la racine du serveur.");
    }
}