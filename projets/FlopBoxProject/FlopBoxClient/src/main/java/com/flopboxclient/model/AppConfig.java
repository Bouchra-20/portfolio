package com.flopboxclient.model;

import java.util.List;

/**
 * Représente la configuration globale de l'application FlopBox.
 * Contient les paramètres de synchronisation et la liste des proxys.
 */
public class AppConfig {
    /** Liste des configurations de serveurs proxys enregistrés. */
    public List<ProxyConfig> proxies;

    /** Chemin vers le dossier racine local utilisé pour la synchronisation. */
    public String localRoot;

    /** Intervalle de temps en secondes entre chaque cycle de synchronisation automatique. */
    public int intervalSeconds;
}