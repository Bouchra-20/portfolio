package com.flopbox.AuthService;

import com.flopbox.FTPManager;
import com.flopbox.exceptions.FtpConnectionException;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FtpSessionFactoryTest {

    @Test
    @DisplayName("Devrait retourner un FTPClient quand la connexion réussit")
    void testCreateFtpClientSuccess() throws Exception {
        // Mock de l'objet FTPClient
        FTPClient mockClient = mock(FTPClient.class);

        // Mock de la méthode statique FTPManager.getConnectedClient
        try (MockedStatic<FTPManager> mockedManager = mockStatic(FTPManager.class)) {
            mockedManager.when(() -> FTPManager.getConnectedClient(anyString(), anyString(), anyString(), anyInt(), anyBoolean()))
                    .thenReturn(mockClient);

            FTPClient result = FtpSessionFactory.createFtpClient("localhost:2121", "user", "pass", "passive");

            assertNotNull(result);
            assertEquals(mockClient, result);
        }
    }

    @Test
    @DisplayName("Devrait jeter FtpConnectionException si le manager lève une IOException")
    void testCreateFtpClientFailure() {
        try (MockedStatic<FTPManager> mockedManager = mockStatic(FTPManager.class)) {
            // On simule une erreur réseau (IOException)
            mockedManager.when(() -> FTPManager.getConnectedClient(anyString(), anyString(), anyString(), anyInt(), anyBoolean()))
                    .thenThrow(new IOException("Connexion impossible"));

            assertThrows(FtpConnectionException.class, () -> {
                FtpSessionFactory.createFtpClient("localhost:2121", "user", "pass", "passive");
            });
        }
    }
}