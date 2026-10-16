package com.flopbox;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests de la logique de gestion FTP.
 */
public class FTPManagerTest {

    /**
     * Teste la fonction de listing récursif en simulant une arborescence.
     */
    @Test
    public void testListRecursive() throws IOException {
        FTPClient mockClient = mock(FTPClient.class);
        StringBuilder sb = new StringBuilder();

        // Simulation d'un fichier et d'un dossier à la racine
        FTPFile file1 = new FTPFile();
        file1.setName("document.txt");
        file1.setType(FTPFile.FILE_TYPE);

        FTPFile dir1 = new FTPFile();
        dir1.setName("images");
        dir1.setType(FTPFile.DIRECTORY_TYPE);

        when(mockClient.listFiles(".")).thenReturn(new FTPFile[]{file1, dir1});
        // Simulation d'un fichier vide dans le dossier images pour arrêter la récursion
        when(mockClient.listFiles("./images")).thenReturn(new FTPFile[]{});

        FTPManager.listRecursive(mockClient, ".", sb, 0);

        String result = sb.toString();
        assertTrue(result.contains("[FILE] document.txt"));
        assertTrue(result.contains("[DIR] images"));
    }

    /**
     * Vérifie que la déconnexion ne lève pas d'exception si le client est null.
     */
    @Test
    public void testDisconnect_SafeWithNull() {
        assertDoesNotThrow(() -> FTPManager.disconnect(null));
    }
}