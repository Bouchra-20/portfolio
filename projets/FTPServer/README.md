# Serveur FTP Multi-threadé

Implémentation d'un serveur FTP en Java

## Introduction

Ce logiciel est une implémentation complète d'un serveur de transfert de fichiers (FTP), il permet la gestion multi-utilisateurs grâce à une architecture threadée où chaque connexion cliente est isolée. Le système supporte la navigation, le transfert de fichiers (RETR, STOR) ainsi que les modes de transfert actif et passif. 

### Instructions de build et d’exécution

Le projet utilise l'outil Apache Maven pour la gestion des dépendances et la compilation.

Pour empaqueter le projet, suivez les instructions suivantes :
`````Bash
mvn clean package
`````

Le JAR généré se trouve dans le dossier target/.

Pour lancer le projet, exécutez la commande suivante :

```bash
FTPServer> java -jar target/sr1-1.0-SNAPSHOT.jar
```

Pour lancer les testes, exécutez la commande suivante : 

```bash
mvn test
```

Pour générer le javadoc, exécutez la commande suivante : 

```bash
mvn javadoc:javadoc   
```

Pour se connecter avec FileZila, il faut saisir le nom d'utilisateur et le mot de passe passés en paramètres dans le main :
- Host : 127.0.0.1
- Username : Bouchra
- Password : Bouchra
- Port : 2121

Vous pouvez aussi modifier le répertoire racine avec celui souhaité : 


````Java
public static void main(String[] args) {

    User user = new User("Bouchra", "Bouchra"); // Nom d'utilisateur et mot de passe
    UserManager userManager = new UserManager();
    userManager.addUser(user);

    FTPServer ftpServer = new FTPServer(2121, "/home/m1eserv/bouchra.bouamama.etu/Documents", userManager);  // Répertoire racine

    try {
        ftpServer.start();
    }catch (Exception e){
        e.printStackTrace();
    }
}
````

### Architecture

L'architecture logicielle est conçue autour de la séparation des responsabilités et de l'extensibilité :

- *Interfaces et Classes Abstraites* : L'interface ``CommandExecutor`` définit le contrat unique pour tous les exécuteurs de commandes FTP.

- *Polymorphisme* : Chaque commande (RETR, STOR, LIST, CWD, etc.) implémente la méthode ``execute()``. Le serveur délègue le traitement sans connaître l'implémentation spécifique.

- *Gestion des sessions* : La classe ``ClientHandler`` maintient l'état du client (authentification, répertoire courant) et assure le parsing du protocole.

- *Gestion des messages* : La classe final ``FTPCode`` gère les message du serveur en respectant le standard FTP, et en facilitant leurs modifications ultérieures.

- *Exceptions personnalisées* : Des classes d’exception dédiées pour chaque erreur identifiée.

- *Gestion de la sécurité* : Une méthode de résolution de chemin sécurisée (resolveSafePath) normalise et valide chaque accès au système de fichiers local.

### Fonctionnalités implémentées

- **Connexion et Authentification** :
    Gestion des commandes USER et PASS.
    Gestionnaire d'utilisateurs (UserManager) en mémoire.

- **Navigation dans le système de fichiers** :
    Support de PWD, CWD, CDUP, LIST.
    Création (MKD) et suppression (DELE, RMD) de dossiers/fichiers.

- **Transfert de données** :
    Support complet du mode PASV (Passif) et PORT (Actif).
    Téléchargement (RETR) et téléversement (STOR).
    Support des types binaire (I) et ASCII (A) via la commande TYPE.

- **Gestion de session** :
    Renommage de fichiers en deux étapes (RNFR/RNTO).
    Commande de maintien de session NOOP.
    Commande de déconnexion QUIT.

- **Sécurité** :
    Interdiction d'aller au delà du répertoire racine défini au démarrage du serveur.
    Interdiction des commandes sans authentification préalable (code 530).

### Exemple des sorties

Exemple des réponses du serveur sur FileZilla :

````
Connexion à 127.0.0.1:2121…
Statut :	Connexion établie, attente du message d’accueil…
Statut :	Serveur non sécurisé, celui‐ci ne prend pas en charge FTP sur TLS.
Statut :	Le serveur ne prend pas en charge pas les caractères non ASCII.
Statut :	Connecté
Commande :  PASV
Réponse :	227 Entering Passive Mode (192,168,1,27,228,145)
Commande :	STOR background2.jpg
Réponse :	150 File status okay; about to open data connection.
Réponse :	150 File status okay; about to open data connection.
Statut :	Démarrage de l’envoi de C:\Users\bouam\m1\test\background2.jpg
Statut :	Récupération du contenu du dossier « / »…
Commande :	PASV
Réponse :	227 Entering Passive Mode (192,168,1,27,228,147)
Commande :	LIST
Réponse :	150 File status okay; about to open data connection.
Réponse :	226 Closing data connection. Transfer complete.
Statut :	Récupération du contenu du dossier « / »…
Statut :	Contenu du dossier « / » affiché avec succès
````

Exemple des réponses sur le terminal :
````
AUTH TLS
AUTH SSL
USER Bouchra
PASS Bouchra
PWD
TYPE I
PASV
LIST
CWD /AP
PASV
LIST
CWD /AP
PASV
LIST
CWD /
TYPE I
PASV
RETR background2.jpg
PASV
STOR background2.jpg
PASV
````

### Code Samples

#### Pattern Command (Polymorphisme)

L'aiguillage des commandes utilise une structure permettant d'isoler la logique de chaque instruction FTP dans des classes dédiées.

````Java

// La commande est extraite et l'exécuteur correspondant est instancié dynamiquement
switch (command) {
case "RETR":
new RETRCommand(out, this).execute(arg); // Appel de la méthode polymorphique
break;
case "STOR":
new STORCommand(out, this).execute(arg);
break;
...
}
````
    
#### Algorithme de Sandboxing (Sécurité)

Cette méthode garantit qu'aucun client ne peut accéder à des fichiers en dehors du répertoire racine autorisé par normalisation des chemins.

````Java

public Path resolveSafePath(String requestedPath) {
Path root = Paths.get(this.directory).toAbsolutePath().normalize();
...
// Combinaison de la racine et du chemin demandé
Path resolved = root.resolve(requestedPath).toAbsolutePath().normalize();

if (!resolved.startsWith(root)) { // Vérification stricte de l'étanchéité
throw new SecurityException("Exit root directory is forbidden");
}
return resolved;
}
````

#### Gestion du mode passif et actif

La socket de données est récupérée dynamiquement selon le mode négocié au préalable par le client (PASV ou PORT).

````Java

public Socket getDataSocket() throws IOException {
if (dataServerSocket != null) {
Socket s = dataServerSocket.accept(); // Mode Passif
dataServerSocket.close();
dataServerSocket = null;
return s;
} else if (activeClientIP != null) {
    Socket s = new Socket(activeClientIP, activeClientPort);  // Mode Actif
    activeClientIP = null;
    activeClientPort = 0;
    return s;
}
return null;
}
````

#### Transfert de fichiers (Flux Binaires)

Le transfert utilise des buffers d'octets pour optimiser les performances de lecture/écriture sur le réseau.

````Java

try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
byte[] buffer = new byte[8192]; // Buffer de 8 Ko
int bytesRead;
while ((bytesRead = bis.read(buffer)) != -1) {
dataOut.write(buffer, 0, bytesRead); // Envoi par blocs binaires
}
dataOut.flush();
}
````
    
#### Multithreading et cycle de vie

Le serveur accepte les connexions en boucle et délègue chaque client à un nouveau thread pour assurer la disponibilité du service.

````Java
while(true) {
Socket clientSocket = serverSocket.accept();
// Chaque handler est encapsulé dans un Thread indépendant
new Thread(new ClientHandler(clientSocket, directory, userManager)).start();
}
````

### Architecture du Projet (UML)

1. **Diagramme des classes principales**

![diagramme_classes](/projets/FTPServer/doc/diagramClasses.png)

2. **Diagramme de Séquence : Authentification (USER/PASS)**

![diagramme_sequence](doc/Authentification(USER,PASS).png)

3. **Diagramme de Séquence : Transfert de données (Mode Actif - PORT + RETR)**

![diagramme_sequence](doc/TransfertDonnées(ModeActif,PORT,RETR).png)

