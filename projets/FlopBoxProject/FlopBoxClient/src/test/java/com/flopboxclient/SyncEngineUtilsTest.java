package com.flopboxclient;

import com.flopboxclient.model.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SyncEngineUtilsTest {

    private SyncEngine syncEngine;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        AppConfig config = new AppConfig();
        config.localRoot = tempDir.toString();
        syncEngine = new SyncEngine(config);
    }

    @Test
    void testComputeHash_Consistency() throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Contenu de test");

        String hash1 = syncEngine.computeHash(file.toFile());
        String hash2 = syncEngine.computeHash(file.toFile());

        assertNotNull(hash1);
        assertEquals(hash1, hash2, "Le hash doit être identique pour le même contenu.");
    }

    @Test
    void testGetRelativeChildren() {
        Set<String> allPaths = new HashSet<>();
        allPaths.add("A/file1.txt");
        allPaths.add("A/sub/file2.txt");
        allPaths.add("B/file3.txt");

        Set<String> result = syncEngine.getRelativeChildren("A/", allPaths);

        assertEquals(2, result.size());
        assertTrue(result.contains("file1.txt"));
        assertTrue(result.contains("sub/file2.txt"));
    }

    @Test
    void testDecodeDeletedName() {
        String encoded = "1715456789_docs__secret__data.pdf";
        String decoded = syncEngine.decodeDeletedName(encoded);

        assertEquals("docs/secret/data.pdf", decoded);
    }
}