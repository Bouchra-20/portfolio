package com.flopboxclient.service;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;


@WireMockTest
class FlopBoxServiceClientTest {

    private FlopBoxServiceClient serviceClient;
    private final String ALIAS = "myFtp";
    private final String FTP_USER = "ftpUser";
    private final String FTP_PASS = "ftpPass";

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        serviceClient = new FlopBoxServiceClient(wmRuntimeInfo.getHttpBaseUrl(), "proxyUser", "proxyPass");
    }

    @Test
    void testDownloadFile_Success(@TempDir Path tempDir) throws Exception {
        byte[] fileContent = "Hello World".getBytes();
        stubFor(get(urlEqualTo("/servers/myFtp/files/test.txt"))
                .withHeader("user", equalTo("proxyUser"))
                .withHeader("ftp-user", equalTo(FTP_USER))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(fileContent)));

        File destination = tempDir.resolve("downloaded.txt").toFile();

        serviceClient.downloadFile(ALIAS, "test.txt", destination, FTP_USER, FTP_PASS);

        assertTrue(destination.exists());
        assertEquals("Hello World", Files.readString(destination.toPath()));
    }

    @Test
    void testDownloadFile_Failure_ThrowsException(@TempDir Path tempDir) {
        stubFor(get(anyUrl()).willReturn(aResponse().withStatus(404)));

        File destination = tempDir.resolve("error.txt").toFile();

        assertThrows(Exception.class, () ->
                serviceClient.downloadFile(ALIAS, "notfound.txt", destination, FTP_USER, FTP_PASS)
        );
    }

    @Test
    void testGetFileList_Success() {
        String mockListing = "[DIR] folder\n[FILE] text.txt 1024";

        stubFor(get(urlPathEqualTo("/servers/myFtp/files"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(mockListing)));

        String result = serviceClient.getFileList(ALIAS, "", FTP_USER, FTP_PASS);

        assertNotNull(result);
        assertTrue(result.contains("[DIR] folder"));
    }
}