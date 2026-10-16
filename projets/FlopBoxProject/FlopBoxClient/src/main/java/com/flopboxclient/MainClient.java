package com.flopboxclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flopboxclient.model.AppConfig;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Classe d'entrée principale de l'application client FlopBox.
 */
public class MainClient {
    private static AppConfig config;

    /**
     * Point d'entrée du programme.
     * Initialise les services et lance le moteur de synchronisation.
     *
     * @param args Arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        System.out.println("=== FlopBox Sync Agent Starting ===");

        try {
            ObjectMapper mapper = new ObjectMapper();
            config = mapper.readValue(new File("client_config.json"), AppConfig.class);
            System.out.println("Config chargée. Dossier local : " + config.localRoot);

            Files.createDirectories(Paths.get(config.localRoot));

            SyncEngine engine = new SyncEngine(config);
            engine.start();

            Thread watcherThread = new Thread(() -> {
                System.out.println("[Thread-Watcher] Démarrage de la surveillance locale...");
                engine.startWatcher();
            });

            watcherThread.setDaemon(true);
            watcherThread.start();

            Thread pollingThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(config.intervalSeconds * 1000L);
                        System.out.println("\n[Polling] Vérification des mises à jour...");
                        engine.start();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            pollingThread.setDaemon(true);
            pollingThread.start();


            System.out.println("=== Agent en cours d'exécution (Appuyez sur Ctrl+C pour arrêter) ===");

            System.out.println("\n=== Agent en cours d'exécution ===");
            System.out.println("Commandes : list-deleted | restore <alias> <fichier> | purge | exit");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("\n> ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) continue;

                if (input.equals("list-deleted")) {
                    engine.listDeleted();

                } else if (input.startsWith("restore ")) {
                    String[] parts = input.split(" ", 3);
                    if (parts.length == 3) {
                        engine.restoreDeleted(parts[1], parts[2]);
                    } else {
                        System.out.println("Usage : restore <alias> <fichier>");
                    }

                } else if (input.equals("purge")) {
                    engine.purgeDeleted();

                } else if (input.equals("exit")) {
                    System.out.println("Arrêt de l'agent.");
                    break;

                } else {
                    System.out.println("Commande inconnue. Commandes disponibles :");
                    System.out.println("  list-deleted");
                    System.out.println("  restore <alias> <fichier>");
                    System.out.println("  purge");
                    System.out.println("  exit");
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur fatale au démarrage : " + e.getMessage());
            e.printStackTrace();
        }
    }
}