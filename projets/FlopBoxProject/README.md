# FlopBox

FlopBox est un projet Java composé de deux applications complémentaires : une passerelle REST qui donne accès à des serveurs FTP et un client de synchronisation qui maintient un dossier local en cohérence avec les fichiers distants. L'ensemble propose une expérience inspirée d'un service de stockage et de synchronisation de fichiers.

## Composants

- **[FlopBox](FlopBox/README.md)** — serveur HTTP REST basé sur Jersey et Grizzly. Il authentifie les utilisateurs, applique leurs autorisations sur les serveurs FTP enregistrés et expose des opérations de consultation et de gestion de fichiers.
- **[FlopBoxClient](FlopBoxClient/README.md)** — client Java qui surveille un dossier local, synchronise ses changements avec les serveurs configurés et permet de consulter, restaurer ou purger les éléments supprimés.

La communication suit ce parcours :

```text
Dossier local <-> FlopBoxClient <-> API REST FlopBox <-> Serveur(s) FTP
```

Le client utilise un fichier `client_config.json` pour définir l'URL de la passerelle, les comptes d'accès, les identifiants FTP, le dossier local et l'intervalle de synchronisation. FlopBox contrôle les utilisateurs et les serveurs auxquels ils ont accès ; il effectue ensuite les opérations FTP demandées, notamment en modes actif ou passif.

## Prérequis

- Java 17 ou supérieur
- Apache Maven 3.6 ou supérieur
- Un ou plusieurs serveurs FTP accessibles pour la synchronisation
- Facultatif : Python et `pyftpdlib` pour lancer un serveur FTP de test

## Lancer le projet

Démarrez d'abord un serveur FTP et vérifiez ses identifiants. Pour utiliser vos propres serveurs, configurez-les dans FlopBox et dans le fichier `FlopBoxClient/client_config.json`.

Dans un premier terminal, lancez la passerelle :

```bash
cd FlopBox
mvn exec:java
```

Par défaut, l'API écoute sur `http://localhost:8080`. Le serveur s'arrête lorsque vous appuyez sur Entrée dans ce terminal.

Dans un second terminal, vérifiez la configuration de `client_config.json`, puis lancez le client depuis son dossier :

```bash
cd FlopBoxClient
mvn exec:java
```

Le client crée le dossier local configuré s'il n'existe pas. Il surveille les changements locaux et effectue également des vérifications périodiques. Dans sa console, les commandes disponibles sont :

| Commande | Action |
|---|---|
| `list-deleted` | Liste les éléments supprimés conservés dans la corbeille distante |
| `restore <alias> <fichier>` | Restaure un élément pour le serveur indiqué |
| `purge` | Supprime définitivement les éléments de la corbeille |
| `exit` | Arrête le client |

## Compilation et tests

Les deux composants sont des projets Maven indépendants. Depuis le dossier de chacun :

```bash
mvn clean package
mvn test
```

Les commandes de lancement, la configuration, les détails de l'API REST et les diagrammes sont décrits dans les README de [FlopBox](FlopBox/README.md) et de [FlopBoxClient](FlopBoxClient/README.md).

## Configuration et précautions

- Adaptez `FlopBoxClient/client_config.json` à vos URL, comptes FTP et chemins locaux. Ce fichier contient des identifiants d'exemple : remplacez-les et ne publiez pas de secrets réels.
- Le fichier `FlopBox/src/main/java/server/Server.py`, fourni comme serveur FTP de test, contient un chemin local spécifique à une machine. Modifiez ce chemin avant de l'utiliser ailleurs, ou utilisez un autre serveur FTP.
- Pour le détail des commandes `curl`, des opérations REST et des limites de configuration, consultez le README de [FlopBox](FlopBox/README.md).