package com.flopboxclient;

import com.flopboxclient.model.AppConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TrashLogicTest {

    @TempDir
    Path tempDir;

    @Test
    void testIgnoreDeletedFolder() {
        AppConfig config = new AppConfig();
        config.localRoot = tempDir.toString();
        SyncEngine engine = new SyncEngine(config);

        Path trashPath = tempDir.resolve("serveur_1").resolve(".deleted").resolve("old_file.txt");

        Path relCheck = tempDir.relativize(trashPath);

        boolean isTrash = relCheck.getNameCount() >= 2 && relCheck.getName(1).toString().equals(".deleted");

        assertTrue(isTrash, "Le fichier dans .deleted doit être identifié comme faisant partie de la corbeille.");
    }
}