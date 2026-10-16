package com.flopbox.ressources;

import com.flopbox.AuthService.AuthService;
import com.flopbox.exceptions.UserAlreadyExistsException;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests d'intégration pour la ressource de gestion des comptes (ServerManagerRessource).
 * Utilise Jersey Test Framework et Mockito pour simuler la couche service.
 */
public class ServerManagerRessourceTest extends JerseyTest {

    private AuthService authService;

    private AutoCloseable closeable;

    @Override
    protected Application configure() {
        authService = mock(AuthService.class);

        ResourceConfig config = new ResourceConfig(ServerManagerRessource.class);

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(authService).to(AuthService.class);
            }
        });
        return config;
    }

    /**
     * Teste la création d'un compte utilisateur avec succès (201 Created).
     */
    @Test
    public void testRegisterUser_Success() throws Exception {
        doNothing().when(authService).registerUser(anyString(), anyString());

        Response response = target("/manage-servers/register")
                .request()
                .header("new-user", "testUser")
                .header("new-password", "testPass")
                .post(Entity.text(""));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(authService).registerUser("testUser", "testPass");
    }

    /**
     * Teste l'échec de création si l'utilisateur existe déjà (409 Conflict).
     */
    @Test
    public void testRegisterUser_AlreadyExists() throws Exception {
        doThrow(new UserAlreadyExistsException("Déjà là"))
                .when(authService).registerUser(eq("testUser"), anyString());

        Response response = target("/manage-servers/register")
                .request()
                .header("new-user", "testUser")
                .header("new-password", "testPass")
                .post(Entity.text(""));

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    /**
     * Teste l'ajout d'un serveur à un compte (201 Created).
     */
    @Test
    public void testAddServer_Success() throws Exception {
        when(authService.checkUserOnly(anyString(), anyString())).thenReturn(true);

        Response response = target("/manage-servers/my-ftp")
                .request()
                .header("user", "bouchra")
                .header("password", "bouchra")
                .post(Entity.text(""));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }
}