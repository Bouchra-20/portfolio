package com.flopboxclient;


import java.nio.file.Path;
import java.util.Set;

/**
 * Objet de transfert stockant temporairement l'état d'un élément supprimé localement.
 *
 * Cette classe est la clé de la détection de mouvement (Move/Rename) :
 * - Pour un fichier : On stocke son 'hash' MD5.
 * - Pour un dossier : On stocke son 'children' (empreinte structurelle).
 *
 * Si un élément identique réapparaît ailleurs durant la "période de grâce",
 * cet objet fournit les métadonnées nécessaires pour transformer un Upload en Rename.
 */
public class PendingDelete {
    final Path path;
    final String hash;
    final Set<String> children;
    final String alias;
    final String remotePath;
    final boolean isDir;

    PendingDelete(Path path, String hash, String alias, String remotePath) {
        this.path = path; this.hash = hash; this.alias = alias;
        this.remotePath = remotePath; this.isDir = false; this.children = null;
    }

    PendingDelete(Path path, Set<String> children, String alias, String remotePath) {
        this.path = path; this.children = children; this.alias = alias;
        this.remotePath = remotePath; this.isDir = true; this.hash = null;
    }
}
