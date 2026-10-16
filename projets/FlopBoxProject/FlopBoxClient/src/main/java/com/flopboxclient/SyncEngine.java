package com.flopboxclient;

import com.flopboxclient.model.AppConfig;
import com.flopboxclient.model.ProxyConfig;
import com.flopboxclient.service.FlopBoxServiceClient;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Moteur de synchronisation principal.
 * Gère la logique de comparaison entre les fichiers locaux et distants.
 */
public class SyncEngine {
    private final AppConfig config;
    private final Set<Path> ignoreList = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final String MANIFEST_FILE = ".sync_manifest";
    private final Map<String, PendingDelete> pendingDeletes = new ConcurrentHashMap<>();
    private final Map<String, String> hashCache = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> dirChildrenCache = new ConcurrentHashMap<>();
    private static final String DELETED_FOLDER = ".deleted";

    /**
     * Initialise le moteur de synchronisation avec la configuration fournie.
     *
     * @param config La configuration de l'application chargée depuis le fichier de config.
     */
    public SyncEngine(AppConfig config) {
        this.config = config;
    }

    /**
     * Exécute un cycle complet de synchronisation.
     *
     * Algorithme :
     * 1. Scan local : Liste les fichiers et dossiers présents sur le disque.
     * 2. Scan distant : Récupère l'arborescence du serveur via {@link #scanServerFiles}.
     * 3. Détection de renommage : Compare les éléments disparus et apparus pour identifier des déplacements.
     * 4. Comparaison :
     *    - In Local AND In Server : Fichier stable (ou mis à jour).
     *    - In Server AND NOT In Local :
     *        - Si présent dans le manifeste -> Suppression distante demandée.
     *        - Sinon -> Nouveau fichier distant à télécharger.
     *    - In Local AND NOT In Server :
     *        - Si présent dans le manifeste -> Suppression locale demandée.
     *        - Sinon -> Nouveau fichier local à uploader.
     * 5. Nettoyage : Suppression des dossiers locaux devenus vides.
     */
    public void start() {
        System.out.println("Début de la synchronisation...");
        for (ProxyConfig proxy : config.proxies) {
            FlopBoxServiceClient service = new FlopBoxServiceClient(proxy.url, proxy.user, proxy.password);
            proxy.ftpCredentials.forEach((alias, creds) -> {
                System.out.println("\n--- Sync serveur : " + alias + " ---");
                String folderName = alias.replace(":", "_");
                File aliasDir = new File(config.localRoot, folderName);
                if (!aliasDir.exists()) aliasDir.mkdirs();

                Map<String, Long> lastKnownState = loadManifestWithSizes(aliasDir);
                Set<String> lastKnownPaths = lastKnownState.keySet();

                Set<String> localFiles = new HashSet<>();
                Map<String, Long> localSizes = new HashMap<>();
                try {
                    Files.walk(aliasDir.toPath())
                            .filter(p -> !p.equals(aliasDir.toPath()))
                            .filter(p -> !p.getFileName().toString().equals(MANIFEST_FILE))
                            .forEach(p -> {
                                String rel = aliasDir.toPath().relativize(p).toString().replace("\\", "/");
                                if (Files.isDirectory(p)) {
                                    rel += "/";
                                    localFiles.add(rel);
                                    localSizes.put(rel, -1L);
                                } else {
                                    localFiles.add(rel);
                                    localSizes.put(rel, p.toFile().length());
                                    String h = computeHash(p.toFile());
                                    if (h != null) hashCache.put(alias + "::" + rel, h);
                                }
                            });
                } catch (IOException e) {
                    System.err.println("Erreur scan local : " + e.getMessage());
                }

                Set<String> serverFiles = new HashSet<>();
                Map<String, Long> serverSizes = new HashMap<>();

                localFiles.stream().filter(p -> p.endsWith("/")).forEach(dirPath -> {
                    Set<String> children = getRelativeChildren(dirPath, localFiles);
                    dirChildrenCache.put(alias + "::" + dirPath, children);
                });

                scanServerFiles(service, alias, "", creds, serverFiles, serverSizes);


                Set<String> disappearedFromServer = new HashSet<>();
                Set<String> newOnServer = new HashSet<>();

                for (String known : lastKnownPaths) {
                    if (!known.endsWith("/") && !serverFiles.contains(known) && localFiles.contains(known)) {
                        disappearedFromServer.add(known);
                    }
                }
                for (String serverPath : serverFiles) {
                    if (!serverPath.endsWith("/") && !lastKnownPaths.contains(serverPath) && !localFiles.contains(serverPath)) {
                        newOnServer.add(serverPath);
                    }
                }

                Map<String, String> remoteRenames = new HashMap<>();
                for (String oldPath : disappearedFromServer) {
                    Set<String> oldChildren = getRelativeChildren(oldPath, lastKnownState.keySet());
                    String oldParent = oldPath.contains("/") ?
                            oldPath.substring(0, oldPath.lastIndexOf("/", oldPath.length() - 2) + 1) : "";

                    for (String newPath : newOnServer) {
                        String newParent = newPath.contains("/") ?
                                newPath.substring(0, newPath.lastIndexOf("/", newPath.length() - 2) + 1) : "";
                        if (!oldParent.equals(newParent)) continue;

                        Set<String> newChildren = getRelativeChildren(newPath, serverFiles);
                        if (oldChildren.equals(newChildren)) {
                            remoteRenames.put(newPath, oldPath);
                            System.out.println(" -> Renommage dossier distant : " + newPath + " → " + oldPath);
                            break;
                        }
                    }
                }

                for (Map.Entry<String, String> rename : remoteRenames.entrySet()) {
                    String oldPath = rename.getKey().replaceAll("/$", "");
                    String newPath = rename.getValue().replaceAll("/$", "");
                    File oldFile = new File(aliasDir, oldPath);
                    File newFile = new File(aliasDir, newPath);
                    ignoreList.add(oldFile.toPath());
                    ignoreList.add(newFile.toPath());
                    try {
                        newFile.getParentFile().mkdirs();
                        Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        System.out.println(" -> Renommé localement : " + oldPath + " → " + newPath);
                        Set<String> toRemove = localFiles.stream()
                                .filter(p -> p.startsWith(oldPath + "/") || p.equals(oldPath + "/"))
                                .collect(java.util.stream.Collectors.toSet());
                        for (String old : toRemove) {
                            localFiles.remove(old);
                            localFiles.add(newPath + old.substring(oldPath.length()));
                        }
                        newOnServer.remove(rename.getValue());
                    } catch (IOException e) {
                        System.err.println("    [!] Erreur renommage local : " + e.getMessage());
                    } finally {
                        scheduleIgnoreRemoval(oldFile.toPath());
                        scheduleIgnoreRemoval(newFile.toPath());
                    }
                }

                Set<String> allPaths = new HashSet<>();
                allPaths.addAll(localFiles);
                allPaths.addAll(serverFiles);

                List<String> sortedPaths = new ArrayList<>(allPaths);
                sortedPaths.sort(Comparator.comparingInt(String::length));

                Map<String, Long> newManifest = new HashMap<>();

                for (String path : sortedPaths) {
                    boolean isDir    = path.endsWith("/");
                    boolean inLocal    = localFiles.contains(path);
                    boolean inServer   = serverFiles.contains(path);
                    boolean inManifest = lastKnownState.containsKey(path);

                    if (inLocal && inServer) {
                        newManifest.put(path, localSizes.getOrDefault(path, -1L));

                    } else if (inServer && !inLocal) {
                        if (inManifest) {
                            String dirPath = isDir ? path.substring(0, path.length() - 1) : path;
                            System.out.println(" -> Suppression distante : " + dirPath);
                            if (isDir) {
                                try {
                                    service.deleteFile(alias, dirPath, creds.user, creds.pass);
                                } catch (Exception e) {
                                    System.err.println("    [!] Erreur suppression dossier distant : " + e.getMessage());
                                    newManifest.put(path, lastKnownState.get(path));
                                }
                            } else {
                                if (!moveToDeleted(service, alias, dirPath, creds)) {
                                    newManifest.put(path, lastKnownState.get(path));
                                }
                            }
                        } else {
                            if (isDir) {
                                new File(aliasDir, path).mkdirs();
                                System.out.println(" -> Dossier créé : " + path);
                                newManifest.put(path, -1L);
                            } else {
                                File dest = new File(aliasDir, path);
                                dest.getParentFile().mkdirs();
                                ignoreList.add(dest.toPath());
                                System.out.println(" -> Téléchargement : " + path);
                                try {
                                    service.downloadFile(alias, path, dest, creds.user, creds.pass);
                                    newManifest.put(path, dest.length());
                                } catch (Exception e) {
                                    System.err.println("    [!] Erreur téléchargement : " + e.getMessage());
                                } finally {
                                    scheduleIgnoreRemoval(dest.toPath());
                                }
                            }
                        }

                    } else if (inLocal && !inServer) {
                        if (inManifest) {
                            File toDelete = new File(aliasDir, path);
                            ignoreList.add(toDelete.toPath());
                            if (isDir) {
                                String[] children = toDelete.list();
                                if (children == null || children.length == 0) {
                                    toDelete.delete();
                                    System.out.println(" -> Dossier supprimé localement : " + path);
                                }
                            } else {
                                try {
                                    Files.deleteIfExists(toDelete.toPath());
                                    System.out.println(" -> Suppression locale : " + path);
                                } catch (IOException e) {
                                    System.err.println("    [!] Erreur suppression locale : " + e.getMessage());
                                    newManifest.put(path, lastKnownState.get(path));
                                }
                            }
                            scheduleIgnoreRemoval(toDelete.toPath());
                        } else {
                            if (isDir) {
                                newManifest.put(path, -1L);
                            } else {
                                File localFile = new File(aliasDir, path);
                                ignoreList.add(localFile.toPath());
                                System.out.println(" -> Upload : " + path);
                                try {
                                    service.uploadFile(alias, path, localFile, creds.user, creds.pass);
                                    newManifest.put(path, localFile.length());
                                } catch (Exception e) {
                                    System.err.println("    [!] Erreur upload : " + e.getMessage());
                                } finally {
                                    scheduleIgnoreRemoval(localFile.toPath());
                                }
                            }
                        }
                    }
                }

                try {
                    Files.walk(aliasDir.toPath())
                            .filter(Files::isDirectory)
                            .filter(p -> !p.equals(aliasDir.toPath()))
                            .sorted(Comparator.reverseOrder())
                            .forEach(p -> {
                                String rel = aliasDir.toPath().relativize(p).toString().replace("\\", "/") + "/";
                                if (serverFiles.contains(rel)) return;

                                String[] children = p.toFile().list();
                                if (children != null && children.length == 0) {
                                    ignoreList.add(p);
                                    p.toFile().delete();
                                    System.out.println(" -> Dossier vide supprimé : " + rel);
                                    scheduleIgnoreRemoval(p);
                                }
                            });
                } catch (IOException e) {
                    System.err.println("Erreur nettoyage dossiers vides : " + e.getMessage());
                }

                saveManifestWithSizes(aliasDir, newManifest);
            });
        }
        System.out.println("\n=== Arborescence locale synchronisée ===");
        for (ProxyConfig p : config.proxies) {
            p.ftpCredentials.keySet().forEach(a -> {
                String f = a.replace(":", "_");
                File d = new File(config.localRoot, f);
                System.out.println("\n[Serveur : " + a + "]");
                printTree(d, d, "");
            });
        }
    }

    /**
     * Analyse la réponse textuelle du serveur pour reconstruire l'arborescence distante.
     *
     * @param service Client de service FlopBox.
     * @param alias Identifiant du serveur FTP.
     * @param remotePath Chemin racine à scanner.
     * @param creds Identifiants FTP.
     * @param serverFiles [Sortie] Set complété avec les chemins relatifs (les dossiers finissent par '/').
     * @param serverSizes [Sortie] Map complétée avec les tailles de fichiers pour la détection de modification.
     *
     * Note technique : Utilise une Stack et un calcul d'indentation dynamique pour transformer
     * un affichage visuel plat en structure de chemins hiérarchiques.
     */
    public void scanServerFiles(FlopBoxServiceClient service, String alias, String remotePath,
                                 ProxyConfig.FtpCredentials creds, Set<String> serverFiles, Map<String, Long> serverSizes) {
        try {
            String content = service.getFileList(alias, remotePath, creds.user, creds.pass);
            String[] lines = content.split("\n");

            Stack<String> currentPath = new Stack<>();
            int indentUnit = -1;

            for (String line : lines) {
                if (line.contains("Arborescence") || line.trim().isEmpty()) continue;

                int indentation = line.indexOf("[");
                if (indentation == -1) continue;

                if (indentation > 0 && indentUnit == -1) indentUnit = indentation;

                int level = (indentUnit > 0 && indentation > 0) ? (indentation / indentUnit) : 0;
                while (currentPath.size() > level) currentPath.pop();

                if (line.contains("[DIR]")) {
                    String dirName = line.substring(line.indexOf("[DIR]") + 5).trim();
                    currentPath.push(dirName);
                    String dirPath = String.join("/", currentPath);
                    if (dirPath.equals(DELETED_FOLDER) || dirPath.startsWith(DELETED_FOLDER + "/")) continue;
                    serverFiles.add(dirPath + "/");
                } else if (line.contains("[FILE]")) {
                    String fileName = line.substring(line.indexOf("[FILE]") + 6).trim();
                    if (fileName.equals(MANIFEST_FILE)) continue;
                    String prefix = String.join("/", currentPath);
                    String fullPath = prefix.isEmpty() ? fileName : prefix + "/" + fileName;
                    serverFiles.add(fullPath);

                    try {
                        String[] parts = fileName.split("\\s+");
                        if (parts.length >= 2) {
                            serverSizes.put(fullPath, Long.parseLong(parts[parts.length - 1]));
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur scan serveur : " + e.getMessage());
        }
    }

    /**
     * Supprime un chemin de la liste d'exclusion après un délai de garde.
     * Nécessaire pour éviter les "boucles d'écho" : quand le client écrit un fichier
     * téléchargé sur le disque, cela génère un événement système que le Watcher
     * pourrait interpréter comme une modification manuelle de l'utilisateur.
     */
    private void scheduleIgnoreRemoval(Path path) {
        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            ignoreList.remove(path);
        }).start();
    }

    /**
     * Initialise et lance la boucle infinie de surveillance du système de fichiers local.
     *
     * Fonctionnement :
     * 1. Enregistre récursivement tous les sous-dossiers auprès du WatchService.
     * 2. Intercepte les événements CREATE, MODIFY et DELETE.
     * 3. Logique de filtrage : Ignore le fichier manifeste, la corbeille (.deleted)
     *    et les fichiers présents dans la 'ignoreList'.
     * 4. Gestion du renommage : Si un dossier est créé, le moteur cherche dans
     *    'pendingDeletes' si une structure identique vient d'être supprimée pour
     *    déclencher un RENAME distant plutôt qu'un DELETE+UPLOAD.
     */
    public void startWatcher() {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path rootPath = Paths.get(config.localRoot);

            registerAll(rootPath, watcher);

            System.out.println("Surveillance active (Upload auto) sur : " + rootPath.toAbsolutePath());

            while (true) {
                WatchKey key = watcher.take();
                Path dir = (Path) key.watchable();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == OVERFLOW) continue;

                    Path fileName = (Path) event.context();
                    Path fullPath = dir.resolve(fileName);

                    if (ignoreList.contains(fullPath)) continue;

                    if (fullPath.getFileName().toString().equals(MANIFEST_FILE)) continue;

                    Path relCheck = Paths.get(config.localRoot).relativize(fullPath);
                    if (relCheck.getNameCount() >= 2 && relCheck.getName(1).toString().equals(DELETED_FOLDER)) continue;

                    if (kind == ENTRY_DELETE) {
                        handleLocalDeletion(fullPath);
                    }
                    else {
                        if (Files.isDirectory(fullPath)) {
                            if (kind == ENTRY_CREATE) {
                                registerAll(fullPath, watcher);

                                Set<String> newChildren = new HashSet<>();
                                try {
                                    Files.walk(fullPath)
                                            .filter(p -> !p.equals(fullPath))
                                            .forEach(p -> newChildren.add(
                                                    fullPath.relativize(p).toString().replace("\\", "/")
                                            ));
                                } catch (IOException e) { e.printStackTrace(); }

                                Path relNew = Paths.get(config.localRoot).relativize(fullPath);
                                if (relNew.getNameCount() >= 2) {
                                    String alias = relNew.getName(0).toString().replace("_", ":");
                                    String newRemotePath = relNew.subpath(1, relNew.getNameCount())
                                            .toString().replace("\\", "/");

                                    PendingDelete dirMatch = null;
                                    String dirMatchKey = null;
                                    for (Map.Entry<String, PendingDelete> entry : pendingDeletes.entrySet()) {
                                        PendingDelete pd = entry.getValue();
                                        if (pd.isDir && pd.alias.equals(alias) && pd.children != null
                                                && pd.children.equals(newChildren)) {
                                            dirMatch = pd;
                                            dirMatchKey = entry.getKey();
                                            break;
                                        }
                                    }

                                    if (dirMatch != null) {
                                        pendingDeletes.remove(dirMatchKey);
                                        String oldRemotePath = dirMatch.remotePath;
                                        System.out.println(">>> Renommage dossier : " + oldRemotePath + " → " + newRemotePath);

                                        for (ProxyConfig proxy : config.proxies) {
                                            if (!proxy.ftpCredentials.containsKey(alias)) continue;
                                            ProxyConfig.FtpCredentials creds = proxy.ftpCredentials.get(alias);
                                            FlopBoxServiceClient service = new FlopBoxServiceClient(proxy.url, proxy.user, proxy.password);
                                            try {
                                                service.renameFile(alias, oldRemotePath, newRemotePath, creds.user, creds.pass);
                                                System.out.println("Dossier renommé sur serveur : " + oldRemotePath + " → " + newRemotePath);
                                                dirChildrenCache.put(alias + "::" + newRemotePath + "/", newChildren);
                                            } catch (Exception e) {
                                                System.err.println("Erreur renommage dossier distant : " + e.getMessage());

                                                try {
                                                    Files.walk(fullPath).filter(Files::isRegularFile)
                                                            .forEach(this::handleLocalChange);
                                                    service.deleteFile(alias, oldRemotePath, creds.user, creds.pass);
                                                } catch (Exception e2) {
                                                    System.err.println("Erreur fallback renommage dossier : " + e2.getMessage());
                                                }
                                            }
                                        }
                                    } else {
                                        dirChildrenCache.put(alias + "::" + newRemotePath + "/", newChildren);
                                        Files.walk(fullPath).filter(Files::isRegularFile).forEach(this::handleLocalChange);
                                    }
                                }
                            }
                        } else {
                            handleLocalChange(fullPath);
                        }
                    }
                }
                if (!key.reset()) break;
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Enregistre récursivement un dossier et tous ses sous-dossiers auprès du service de surveillance.
     *
     * Crucial pour le WatchService Java qui, par défaut, n'est pas récursif.
     * Sans cette méthode, les fichiers créés dans des sous-répertoires profonds
     * ne déclencheraient aucun événement d'upload automatique.
     *
     * @param start Chemin racine à partir duquel commencer l'enregistrement.
     * @param watcher Le service WatchService actif.
     * @throws IOException En cas d'impossibilité d'accéder à un répertoire.
     */
    private void registerAll(Path start, WatchService watcher) throws IOException {
        Files.walk(start).filter(Files::isDirectory).forEach(path -> {
            try {
                path.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            } catch (IOException e) { e.printStackTrace(); }
        });
    }

    /**
     * Traite les créations et modifications de fichiers individuels.
     *
     * Algorithme de détection de mouvement :
     * 1. Calcule le hash (empreinte) du fichier modifié.
     * 2. Parcourt les suppressions récentes.
     * 3. Si un fichier supprimé possède le même hash et le même alias :
     *    - Commande une opération RENAME sur le serveur.
     * 4. Sinon :
     *    - Commande un UPLOAD classique vers le serveur.
     *
     * @param localFile Chemin absolu du fichier modifié localement.
     */
    private void handleLocalChange(Path localFile) {
        if (!Files.exists(localFile) || ignoreList.contains(localFile)) return;
        try { if (Files.size(localFile) == 0) return; } catch (IOException e) { return; }

        Path relativeToRoot = Paths.get(config.localRoot).relativize(localFile);
        if (relativeToRoot.getNameCount() < 2) return;

        String aliasFolder = relativeToRoot.getName(0).toString();
        String alias = aliasFolder.replace("_", ":");
        String newRemotePath = relativeToRoot.subpath(1, relativeToRoot.getNameCount())
                .toString().replace("\\", "/");

        String newHash = computeHash(localFile.toFile());

        PendingDelete match = null;
        String matchKey = null;
        for (Map.Entry<String, PendingDelete> entry : pendingDeletes.entrySet()) {
            PendingDelete pd = entry.getValue();
            if (!pd.alias.equals(alias)) continue;
            if (newHash != null && newHash.equals(pd.hash)) {
                match = pd;
                matchKey = entry.getKey();
                break;
            }
        }

        for (ProxyConfig proxy : config.proxies) {
            if (!proxy.ftpCredentials.containsKey(alias)) continue;
            ProxyConfig.FtpCredentials creds = proxy.ftpCredentials.get(alias);
            FlopBoxServiceClient service = new FlopBoxServiceClient(proxy.url, proxy.user, proxy.password);

            if (match != null) {
                pendingDeletes.remove(matchKey);
                String oldRemotePath = match.remotePath;
                System.out.println(">>> Renommage détecté : " + oldRemotePath + " → " + newRemotePath);
                try {
                    service.renameFile(alias, oldRemotePath, newRemotePath, creds.user, creds.pass);
                    System.out.println("Renommé sur serveur : " + oldRemotePath + " → " + newRemotePath);
                } catch (Exception e) {
                    System.err.println("Erreur renommage distant : " + e.getMessage());
                    try {
                        service.uploadFile(alias, newRemotePath, localFile.toFile(), creds.user, creds.pass);
                        service.deleteFile(alias, oldRemotePath, creds.user, creds.pass);
                    } catch (Exception e2) {
                        System.err.println("Erreur fallback renommage : " + e2.getMessage());
                    }
                }
            } else {
                System.out.println(">>> Upload : [" + alias + "] " + newRemotePath);
                ignoreList.add(localFile);
                try {
                    service.uploadFile(alias, newRemotePath, localFile.toFile(), creds.user, creds.pass);
                    System.out.println("Uploadé : " + newRemotePath);
                    hashCache.put(alias + "::" + newRemotePath, newHash);
                } catch (Exception e) {
                    System.err.println("Erreur upload : " + e.getMessage());
                } finally {
                    scheduleIgnoreRemoval(localFile);
                }
            }
        }
    }

    /**
     * Gère la suppression d'un fichier ou d'un dossier détectée localement.
     *
     * Algorithme de "Suppression Différée" :
     * 1. Identifie si l'élément supprimé était un fichier (via hashCache) ou un dossier (via dirChildrenCache).
     * 2. Place l'élément dans 'pendingDeletes' pendant 2000ms.
     * 3. Si pendant ce délai, un événement CREATE est détecté avec un contenu identique (hash ou enfants),
     *    l'élément est retiré de 'pendingDeletes' pour effectuer un RENAME (voir handleLocalChange).
     * 4. Si le délai expire, la suppression est confirmée et répercutée sur le serveur FTP :
     *    - Dossier : Suppression physique.
     *    - Fichier : Déplacement vers le dossier corbeille distant (.deleted/).
     */
    private void handleLocalDeletion(Path localFileDeleted) {
        Path relativeToRoot = Paths.get(config.localRoot).relativize(localFileDeleted);
        if (relativeToRoot.getNameCount() < 2) return;

        String aliasFolder = relativeToRoot.getName(0).toString();
        String alias = aliasFolder.replace("_", ":");
        String remotePath = relativeToRoot.subpath(1, relativeToRoot.getNameCount()).toString().replace("\\", "/");

        String hashKey = alias + "::" + remotePath;
        Set<String> cachedChildren = dirChildrenCache.remove(hashKey);
        boolean wasDir = cachedChildren != null;

        if (wasDir) {
            pendingDeletes.put(hashKey, new PendingDelete(localFileDeleted, cachedChildren, alias, remotePath));
        } else {
            String cachedHash = hashCache.remove(alias + "::" + remotePath);
            pendingDeletes.put(hashKey, new PendingDelete(localFileDeleted, cachedHash, alias, remotePath));
        }
        new Thread(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            PendingDelete pending = pendingDeletes.remove(hashKey);
            if (pending != null) {
                System.out.println(">>> Suppression confirmée -> FTP [" + alias + "] : " + remotePath);
                for (ProxyConfig proxy : config.proxies) {
                    if (proxy.ftpCredentials.containsKey(alias)) {
                        ProxyConfig.FtpCredentials creds = proxy.ftpCredentials.get(alias);
                        FlopBoxServiceClient service = new FlopBoxServiceClient(proxy.url, proxy.user, proxy.password);
                        if (pending.isDir) {
                            try {
                                service.deleteFile(alias, remotePath, creds.user, creds.pass);
                            } catch (Exception e) {
                                System.err.println("Erreur suppression dossier : " + e.getMessage());
                            }
                        } else {
                            moveToDeleted(service, alias, remotePath, creds);
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * Calcule l'empreinte MD5 d'un fichier.
     *
     * Utilisé pour :
     * - Identifier de manière unique le contenu d'un fichier indépendamment de son nom.
     * - Détecter les renommages de fichiers (Move) en comparant les empreintes.
     *
     * @param file Le fichier à analyser.
     * @return Une chaîne hexadécimale représentant le hash, ou null en cas d'erreur de lecture.
     */
    public String computeHash(File file) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            try (java.io.InputStream is = Files.newInputStream(file.toPath())) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = is.read(buf)) != -1) md.update(buf, 0, n);
            }
            StringBuilder sb = new StringBuilder();
            for (byte b : md.digest()) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Charge l'état de synchronisation précédent à partir du fichier manifeste local.
     *
     * Format du fichier : "chemin/relatif::taille_en_octets"
     * - Une taille de -1 indique un dossier ou une taille inconnue.
     * - Le manifeste sert de "point de vérité" pour distinguer ce qui est nouveau
     *   sur le serveur de ce qui a été supprimé localement.
     *
     * @param aliasDir Le dossier racine local correspondant au serveur (alias).
     * @return Une Map associant les chemins relatifs à leur dernière taille connue.
     */
    private Map<String, Long> loadManifestWithSizes(File aliasDir) {
        File manifest = new File(aliasDir, MANIFEST_FILE);
        Map<String, Long> state = new HashMap<>();
        if (!manifest.exists()) return state;
        try {
            Files.lines(manifest.toPath())
                    .map(String::trim)
                    .filter(l -> !l.isEmpty())
                    .forEach(line -> {
                        String[] parts = line.split("::", 2);
                        if (parts.length == 2) {
                            try { state.put(parts[0], Long.parseLong(parts[1])); }
                            catch (NumberFormatException ignored) { state.put(parts[0], -1L); }
                        } else {
                            state.put(parts[0], -1L);
                        }
                    });
        } catch (IOException e) {
            System.err.println("Erreur lecture manifeste : " + e.getMessage());
        }
        return state;
    }

    /**
     * Sérialise l'état actuel de la synchronisation dans le fichier manifeste.
     *
     * Le manifeste permet au prochain cycle de synchronisation de savoir si une absence
     * de fichier est due à une suppression (présent dans le manifeste, absent sur le disque)
     * ou à un nouvel élément (absent du manifeste, présent sur le serveur).
     *
     * @param aliasDir Dossier racine local du serveur concerné.
     * @param pathsWithSizes Map contenant les chemins relatifs et leurs tailles respectives.
     */
    private void saveManifestWithSizes(File aliasDir, Map<String, Long> pathsWithSizes) {
        File manifest = new File(aliasDir, MANIFEST_FILE);
        List<String> lines = new ArrayList<>();
        pathsWithSizes.forEach((path, size) -> lines.add(path + "::" + size));
        try {
            Files.write(manifest.toPath(), lines);
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde manifeste : " + e.getMessage());
        }
    }

    /**
     * Extrait la structure relative des descendants d'un dossier donné.
     *
     * Cette méthode est fondamentale pour la détection de renommage de dossiers.
     * Elle permet de comparer si un nouveau dossier possède exactement la même
     * "signature" (contenu relatif) qu'un dossier qui vient de disparaître.
     *
     * @param dirPath Chemin du dossier parent.
     * @param allPaths Set global de tous les chemins connus.
     * @return Un Set de chemins relatifs par rapport à dirPath.
     */
    public Set<String> getRelativeChildren(String dirPath, Set<String> allPaths) {
        Set<String> result = new HashSet<>();
        String prefix = dirPath.endsWith("/") ? dirPath : dirPath + "/";
        for (String p : allPaths) {
            if (p.startsWith(prefix) && !p.equals(prefix)) {
                result.add(p.substring(prefix.length()));
            }
        }
        return result;
    }

    /**
     * Affiche une représentation visuelle "en arbre" de l'arborescence locale.
     *
     * Utilise les caractères de dessin de boîte (├──, └──) pour une lisibilité console
     * similaire à la commande 'tree' d'Unix/Windows. Les dossiers sont affichés
     * prioritairement aux fichiers.
     */
    private void printTree(File root, File current, String indent) {
        File[] children = current.listFiles();
        if (children == null) return;

        Arrays.sort(children, (a, b) -> {
            if (a.isDirectory() && !b.isDirectory()) return -1;
            if (!a.isDirectory() && b.isDirectory()) return 1;
            return a.getName().compareTo(b.getName());
        });

        for (int i = 0; i < children.length; i++) {
            File child = children[i];
            if (child.getName().equals(MANIFEST_FILE)) continue;

            boolean isLast = (i == children.length - 1);
            long visibleCount = Arrays.stream(children)
                    .filter(f -> !f.getName().equals(MANIFEST_FILE))
                    .count();
            long visibleIndex = Arrays.stream(children)
                    .filter(f -> !f.getName().equals(MANIFEST_FILE))
                    .sorted((a, b) -> {
                        if (a.isDirectory() && !b.isDirectory()) return -1;
                        if (!a.isDirectory() && b.isDirectory()) return 1;
                        return a.getName().compareTo(b.getName());
                    })
                    .collect(java.util.stream.Collectors.toList())
                    .indexOf(child);
            isLast = (visibleIndex == visibleCount - 1);

            String connector = isLast ? "└── " : "├── ";
            String childIndent = isLast ? indent + "    " : indent + "│   ";

            if (child.isDirectory()) {
                System.out.println(indent + connector + "[" + child.getName() + "/]");
                printTree(root, child, childIndent);
            } else {
                System.out.println(indent + connector + child.getName());
            }
        }
    }

    /**
     * Implémente une suppression sécurisée en déplaçant les fichiers vers un dossier .deleted/.
     *
     * Mécanisme :
     * 1. Le chemin d'origine est encodé (remplacement des "/" par "__") pour aplatir la structure.
     * 2. Le fichier est téléchargé temporairement puis ré-uploadé dans le dossier corbeille.
     * 3. En cas d'échec du mécanisme de corbeille (ex: quota plein), le moteur bascule
     *    sur une suppression définitive (fallback) pour garantir la synchronisation.
     *
     * @return true si le fichier a été traité (déplacé ou supprimé), false sinon.
     */
    private boolean moveToDeleted(FlopBoxServiceClient service, String alias,
                                  String remotePath, ProxyConfig.FtpCredentials creds) {
        if (remotePath.equals(MANIFEST_FILE) || remotePath.startsWith(DELETED_FOLDER + "/")) {
            try {
                service.deleteFile(alias, remotePath, creds.user, creds.pass);
                return true;
            } catch (Exception e) {
                return false;
            }
        }


        String timestamp = String.valueOf(System.currentTimeMillis());
        String encodedPath = remotePath.replace("/", "__");
        String deletedPath = DELETED_FOLDER + "/" + encodedPath;

        File tempFile = new File(System.getProperty("java.io.tmpdir"), "flopbox_tmp_" + timestamp);

        try {
            service.downloadFile(alias, remotePath, tempFile, creds.user, creds.pass);

            service.uploadFile(alias, deletedPath, tempFile, creds.user, creds.pass);

            service.deleteFile(alias, remotePath, creds.user, creds.pass);

            System.out.println("   [Corbeille] Déplacé vers : " + deletedPath);
            return true;

        } catch (Exception e) {
            System.err.println("   [!] Erreur moveToDeleted : " + e.getMessage());
            try {
                service.deleteFile(alias, remotePath, creds.user, creds.pass);
                System.out.println("   [!] Fallback : suppression directe de " + remotePath);
                return true;
            } catch (Exception e2) {
                System.err.println("   [!] Erreur suppression fallback : " + e2.getMessage());
                return false;
            }
        } finally {
            if (tempFile.exists()) tempFile.delete();
        }
    }

    /**
     * Récupère et affiche la liste des fichiers stockés dans la corbeille distante (.deleted/).
     *
     * Pour chaque serveur configuré, la méthode :
     * 1. Scanne l'arborescence pour isoler les fichiers du dossier .deleted/.
     * 2. Décode les noms de fichiers pour afficher leur emplacement d'origine.
     * 3. Affiche un index permettant à l'utilisateur de choisir un fichier à restaurer.
     */
    public void listDeleted() {
        System.out.println("\n=== Fichiers supprimés (.deleted/) ===");
        for (ProxyConfig proxy : config.proxies) {
            FlopBoxServiceClient service = new FlopBoxServiceClient(proxy.url, proxy.user, proxy.password);
            proxy.ftpCredentials.forEach((alias, creds) -> {
                System.out.println("\n[Serveur : " + alias + "]");
                List<String> deletedFiles = getDeletedFiles(service, alias, creds);
                if (deletedFiles.isEmpty()) {
                    System.out.println(" (aucun fichier supprimé)");
                } else {
                    int i = 1;
                    for (String fileName : deletedFiles) {
                        if (fileName.equals(MANIFEST_FILE)) continue;
                        String original = decodeDeletedName(fileName);
                        System.out.printf("  [%d] %s%n       -> original : %s%n", i++, fileName, original);
                    }
                }
            });
        }
    }

    /**
     * Restaure un fichier depuis la corbeille vers son emplacement d'origine sur le serveur.
     *
     * Mécanisme :
     * 1. Téléchargement du fichier depuis .deleted/ vers un répertoire temporaire local.
     * 2. Upload du fichier vers le chemin original (décodé via decodeDeletedName).
     * 3. Suppression de la copie dans la corbeille après confirmation de l'upload.
     *
     * @param alias Identifiant du serveur FTP.
     * @param deletedFileName Nom encodé du fichier tel qu'il apparaît dans .deleted/.
     */
    public void restoreDeleted(String alias, String deletedFileName) {
        for (ProxyConfig proxy : config.proxies) {
            if (!proxy.ftpCredentials.containsKey(alias)) continue;
            ProxyConfig.FtpCredentials creds = proxy.ftpCredentials.get(alias);
            FlopBoxServiceClient service = new FlopBoxServiceClient(proxy.url, proxy.user, proxy.password);

            String deletedPath = DELETED_FOLDER + "/" + deletedFileName;
            String originalPath = decodeDeletedName(deletedFileName);
            File tempFile = new File(System.getProperty("java.io.tmpdir"), "flopbox_restore_" + System.currentTimeMillis());

            try {
                service.downloadFile(alias, deletedPath, tempFile, creds.user, creds.pass);
                service.uploadFile(alias, originalPath, tempFile, creds.user, creds.pass);
                service.deleteFile(alias, deletedPath, creds.user, creds.pass);
                System.out.println(" Restauré : " + deletedFileName + " → " + originalPath);
            } catch (Exception e) {
                System.err.println("Erreur restauration : " + e.getMessage());
            } finally {
                if (tempFile.exists()) tempFile.delete();
            }
        }
    }

    /**
     * Supprime définitivement tous les fichiers contenus dans la corbeille distante.
     *
     * Cette opération est irréversible. Elle tente de supprimer chaque fichier
     * individuellement, puis essaie de supprimer le dossier .deleted/ lui-même
     * pour laisser le serveur propre.
     */
    public void purgeDeleted() {
        System.out.println("\n=== Purge de .deleted/ ===");
        for (ProxyConfig proxy : config.proxies) {
            FlopBoxServiceClient service = new FlopBoxServiceClient(proxy.url, proxy.user, proxy.password);
            proxy.ftpCredentials.forEach((alias, creds) -> {
                System.out.println("[" + alias + "]");
                List<String> deletedFiles = getDeletedFiles(service, alias, creds);

                if (deletedFiles.isEmpty()) {
                    System.out.println(" (rien à purger)");
                    return;
                }

                int count = 0;
                for (String fileName : deletedFiles) {
                    if (fileName.equals(MANIFEST_FILE)) continue;
                    String fullPath = DELETED_FOLDER + "/" + fileName;
                    try {
                        service.deleteFile(alias, fullPath, creds.user, creds.pass);
                        System.out.println("  Supprimé définitivement : " + fileName);
                        count++;
                    } catch (Exception e) {
                        System.err.println(" [!] Erreur suppression de " + fileName + " : " + e.getMessage());
                    }
                }

                try {
                    service.deleteFile(alias, DELETED_FOLDER, creds.user, creds.pass);
                    System.out.println(" Dossier .deleted/ supprimé (" + count + " fichier(s) purgé(s))");
                } catch (Exception e) {
                    System.out.println(" (dossier .deleted/ non supprimé : " + e.getMessage() + ")");
                }
            });
        }
    }

    /**
     * Reconstruit le chemin d'accès original à partir d'un nom de fichier archivé.
     *
     * Algorithme :
     * 1. Ignore le préfixe de timestamp/index.
     * 2. Remplace les doubles underscores (__) par des slashs (/) pour
     *    recréer la hiérarchie des dossiers originale.
     *
     * @param deletedFileName Nom du fichier dans la corbeille (ex: "171545_docs__cv.pdf").
     * @return Le chemin original (ex: "docs/cv.pdf").
     */
    public String decodeDeletedName(String deletedFileName) {
        int firstUnderscore = deletedFileName.indexOf("_");
        if (firstUnderscore == -1) return deletedFileName;
        String encoded = deletedFileName.substring(firstUnderscore + 1);
        return encoded.replace("__", "/");
    }

    /**
     * Interroge le serveur pour lister spécifiquement les fichiers archivés.
     *
     * La méthode utilise désormais scanServerFiles avec le point d'entrée
     * DELETED_FOLDER, ce qui limite le trafic réseau en ne récupérant que
     * l'arborescence de la corbeille au lieu du serveur complet.
     *
     * @return Une liste des noms de fichiers (format encodé) prêts pour la restauration.
     */
    private List<String> getDeletedFiles(FlopBoxServiceClient service, String alias,
                                         ProxyConfig.FtpCredentials creds) {
        List<String> result = new ArrayList<>();
        Set<String> allFiles = new HashSet<>();
        Map<String, Long> sizes = new HashMap<>();
        scanServerFiles(service, alias, DELETED_FOLDER, creds, allFiles, sizes);

        for (String path : allFiles) {
            if (path.startsWith(DELETED_FOLDER + "/") && !path.endsWith("/")) {
                String fileName = path.substring((DELETED_FOLDER + "/").length());
                result.add(fileName);
            }
        }
        return result;
    }
}