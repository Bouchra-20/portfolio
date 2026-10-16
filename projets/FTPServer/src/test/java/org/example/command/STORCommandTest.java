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
 * Tests unitaires pour la commande STOR (Upload de fichier).
 * @author Bouchra BOUAMAMA
 */
public class STORCommandTest {

    @TempDir
    Path tempDir;

    private ClientHandler handler;
    private StringWriter controlOutput;
    private PrintWriter out;
    private STORCommand command;

    @BeforeEach
    void setUp() {
        handler = new ClientHandler(null, tempDir.toString(), null);
        handler.setAuthentified(true);
        controlOutput = new StringWriter();
        out = new PrintWriter(controlOutput);
        command = new STORCommand(out, handler);
    }

    /**
     * Teste l'upload réussi d'un petit fichier texte.
     */
    @Test
    public void testStorSuccess() throws Exception {
        String fileName = "upload.txt";
        String contentToSend = "Données envoyées par le client";

        // 1. Simuler un port client prêt (Mode Actif)
        try (ServerSocket fakeClientSocket = new ServerSocket(0)) {
            handler.setActivePortInfo("127.0.0.1", fakeClientSocket.getLocalPort());

            // 2. Lancer STOR dans un thread séparé
            Thread serverThread = new Thread(() -> command.execute(fileName));
            serverThread.start();

            // 3. Le "Client" accepte la connexion et envoie les données
            try (Socket dataSocket = fakeClientSocket.accept();
                 OutputStream os = dataSocket.getOutputStream()) {
                os.write(contentToSend.getBytes());
                os.flush();
            }
            serverThread.join();
        }

        // 4. Vérifications
        Path uploadedFile = tempDir.resolve(fileName);
        assertTrue(Files.exists(uploadedFile), "Le fichier devrait avoir été créé sur le serveur");
        assertEquals(contentToSend, Files.readString(uploadedFile), "Le contenu sauvegardé doit correspondre à l'envoi");

        String logs = controlOutput.toString();
        assertTrue(logs.contains("150"), "Doit envoyer le code 150");
        assertTrue(logs.contains("226"), "Doit envoyer le code 226 à la fin");
    }

    /**
     * Vérifie que STOR échoue si aucun canal de données n'est préparé.
     */
    @Test
    public void testStorFailNoConnection() {
        command.execute("fail.txt");
        out.flush();

        assertTrue(controlOutput.toString().contains("425"), "Doit renvoyer 425 si PASV/PORT manque");
    }

    /**
     * Test de sécurité : empêche d'écrire en dehors de la racine.
     */
    @Test
    public void testStorSecurityPathTraversal() {
        assertThrows(SecurityException.class, () -> {
            command.execute("../malveillant.exe");
        });
    }

}