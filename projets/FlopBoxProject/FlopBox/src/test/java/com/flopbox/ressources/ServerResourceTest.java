package com.flopbox.ressources;

import com.flopbox.AuthService.AuthService;
import com.flopbox.exceptions.ForbiddenException;
import com.flopbox.exceptions.UnauthorizedException;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test pro utilisant le MockedConstruction pour intercepter "new AuthService()"
 */
public class ServerResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig(ServerResource.class);
        config.register(MultiPartFeature.class);
        return config;
    }

    /**
     * Test de refus (403) : On force le mock à lever une ForbiddenException
     */
    @Test
    public void testGetFiles_Forbidden() {
        try (MockedConstruction<AuthService> mockedAuth = mockConstruction(AuthService.class,
                (mock, context) -> {
                    doThrow(new ForbiddenException("Interdit"))
                            .when(mock).validateAccess(anyString(), anyString(), anyString());
                })) {

            Response response = target("/servers/alias1/files")
                    .request()
                    .header("user", "admin")
                    .header("password", "admin")
                    .get();

            assertEquals(403, response.getStatus(), "Devrait retourner 403 Forbidden");
        }
    }

    /**
     * Test d'erreur d'auth (401) : On force le mock à lever une UnauthorizedException
     */
    @Test
    public void testGetFiles_Unauthorized() {
        try (MockedConstruction<AuthService> mockedAuth = mockConstruction(AuthService.class,
                (mock, context) -> {
                    when(mock.checkUserOnly(anyString(), anyString())).thenReturn(false);
                    doThrow(new UnauthorizedException("Mauvais pass"))
                            .when(mock).validateAccess(anyString(), anyString(), anyString());
                })) {

            Response response = target("/servers/alias1/files")
                    .request()
                    .header("user", "mauvais")
                    .header("password", "faux")
                    .get();

            assertEquals(401, response.getStatus(), "Devrait retourner 401 Unauthorized");
        }
    }
}