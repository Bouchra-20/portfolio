# Encre, Routes & Rails

## Introduction
Le projet consiste à la création d'un jeu afin d'être utilisé lors d'un tournois d'algorithmie. Le jeu est [Railroad Ink](https://horribleguild.com/eu/product/railroad-ink-deep-blue/). Nous nous sommes basés sur les [règles officielle](https://www.dropbox.com/scl/fi/i93ybxqc8f8230y0i9puj/RRI_BLUE_Rulebook-FR_Web.pdf?rlkey=vyeh09o5dbxwgfoavdyswg7ci&e=1&st=1llaknu8&dl=0) afin d'être le plus fidèle possible du jeu de société.

## Conception
### Clients
Ce package contient les différentes classes qui représente des clients connectés au reflecteur, ceux-ci héritent tous de la super-classe `Client`. Cette classe établit les attributs communs à tous les clients, à savoir, *ws_url* qui correspond à l'url de connexion à la websocket, *id* qui correspond à l'identifiant du client sur le reflecteur et *history* qui correspond au client de la classe `History` auquel se réfère le client pour récuperer l'historique de la partie. 

#### Classe animator
La classe `Animator` manipule les messages **elects** et **grants** au reflecteur. Le message **elects** permet à l'animateur d'établir la liste des participants de la partie. Le message **grants** permet d'autoriser à un client particulier à émettre un mesage spécifique.

#### Classe diceLauncher
La classe `DiceLauncher` correspond au client qui lance les dés indiquant les tuiles que les joueurs doivent utiliser. Il dispose d'une méthode **launchNormal** qui retourne un résultat de la classe `Tile` correspondant à une tuile normale et d'une méthode **launchConnection** qui retourne cette fois-ci une tuile spéciale (tuile de connexion). La méthode **launch** réalise trois lancers de dés normaux et lancer de dés spécial et envoie dans le reflecteur le résultat des lancers avec une commande **throws**.

#### Classe history
La classe `History` représente le client qui enregistre les messages et leur rang dans une variable de classe grâce à la fonction **record**. Le rang dans le dictionnaire *HISTORYD* commence à 2 afin de prendre en compte le message d'entrée de l'historique. Ainsi, pour que les rangs aient du sens, le client `History` doit absolument être le premier client à se connecter au reflecteur.

#### Classe player
La classe `Player` est une ébauche de client joueur, en plus des attributs de bases, elle possède une liste des tuiles désignées par les dés (*launchedTiles*), une liste des tuiles utilisés (*usedTiles*) et un plateau de jeu (*board*). Enfin la fonction **playTurn** contiendra l'algorithme de jeu pour chaque tour.

#### Classe referee
La classe `Referee` est la classe d'arbitrage du jeu. Elle récupère les règles de la classe `Rules` et s'assure que les joueurs les respectent. Dans le cas contraire, l'arbitre émet un message **blames** sur le réflecteur avec le rang du message comme argument. Cette classe n'est pas encore aboutie et son lien avec la classe `Rules` n'est pas encore correctement établi.

### Interface
Dans ce package, nous avons une classe qui permet d'afficher le jeu.

### Modules
Le package `Modules` regroupe les classes nécessaire pour le bon fonctionnement d'autres classes. Comme par exemple, `graphes` dont on a besoin pour calculer les points des joueurs ou encore `WebSocketClient` qui gère la connexion au réflecteur. La classe `WebSocketClient` et le module `Message` sont également définis en langage *JavaScript* pour donner aux participants le choix de développer leur clients de jeu en *Python* ou en *JS*.

#### Classe WebSocketClient  
La classe `WebSocketClient` gère la communication avec un serveur WebSocket. Son constructeur établit les attributs suivants : *ws_url* qui représente l’URL du serveur WebSocket, *client_id* qui identifie le client, *ws* qui stocke la connexion WebSocket, *running* qui indique si la connexion est active, et *message_callback* qui est une fonction de rappel optionnelle pour traiter les messages reçus.  

Les fonctions de la classe sont :  
- **connect** qui établit la connexion WebSocket, envoie un message d'entrée et démarre un thread dédié à l'écoute des messages du serveur.  
- **send** qui permet d'envoyer un message via la connexion WebSocket.  
- **set_message_callback** qui définit une fonction de rappel pour traiter les messages entrants.  
- **_listen** qui tourne en boucle pour recevoir les messages du serveur et les transmettre au callback s’il est défini.  
- **close** qui met fin à la connexion WebSocket proprement et arrête l’écoute des messages.

#### Module Message
Ce module fournit des fonctions permettant d’envoyer différentes commandes via une WebSocket à destination du réflecteur. Chaque fonction construit un message spécifique au format attendu avant de l’envoyer au serveur.  

Les fonctions du module sont :  
- **throws** qui envoie une commande pour lancer des tuiles en précisant l’identifiant du lanceur et la liste des tuiles lancées.  
- **places** qui indique qu’un joueur place une tuile sur le plateau avec une orientation et une position précises.  
- **yields** qui permet à un joueur de passer son tour.  
- **scores** qui transmet le score d’un joueur avec son classement et les détails des points obtenus.  
- **blames** qui permet de contester un message en indiquant son rang.  
- **grants** qui accorde le droit à un autre joueur d’émettre un message spécifique.  
- **agrees** qui envoie le choix d’une variante de jeu.  
- **elects** qui établit la liste des participants en précisant les noms des joueurs sélectionnés.  

Chaque fonction prend en paramètre une instance de WebSocket pour envoyer les messages, l’identifiant du joueur initiateur, ainsi que les informations spécifiques à l’action réalisée.

### Rules
Le package `Rules` permet de gérer les règles du jeu et le comptage des points. On y trouve la classe `rules`, cette classe est appelée par l'arbitre afin de vérifier les actions des joueurs et ainsi s'assurer qu'il n'y a pas de triches. Il y a aussi la classe `points`, qui permet de calculer le score des joueurs à la fin de la partie en se basant sur les règles du jeu.

#### Classe points
La classe `points` permet de calculer les points d'un joueur conformément aux règles de calcul du score du jeu de base. Pour cela, nous avons défini une classe nommée `graphes` qui permet de représenter le plateau d'un joueur sous forme de graphe. Ainsi, nous pouvons déterminer le chemin le plus long en prenant en compte le type de route (autoroute ou chemin de fer).

### Game
Le package `Game` regroupe les classes nécessaire à la gestion d'une partie ainsi que la boucle de jeu.

#### Classe game
La classe `Game` gère la boucle globale du jeu. Son constructeur établit les attributs suivants : *players* qui représente la liste des clients joueurs, *port* qui correspond au port sur lequel est connecté le réflecteur, *ws_url* pour l'url de la websocket, *board* pour la grille de jeu, *rules* pour les règles, *nb_participants* pour le nombre de clients joueurs. Ensuite, on ajoute les clients indispensables au bon fonctionnement de la partie : *history* pour l'historique (toujours le premier client), *animator* pour l'animator qui pourra autoriser au *dicelauncher* l'émission du message **throws** et au *referee* l'émission du message **blames**.
Les trois fonctions pour le moment présentes sont **addPlayer** qui permet d'ajouter un joueur à la partie en prenant en argument l'id souhaité (la fonction ne gère pas encore l'unicité des id ou encore le nombre maximum de participants), la fonction principale **play** qui prend en paramètre le nombre de tours de la partie et qui, pour chaque tour, pour chaque joueur, appelle la fonction de jeu de celui-ci et enfin la fonction **score** qui compte les points de chaque joueur.

#### Classe board
Cette classe permet de créer le plateau du joueur. Ce plateau est présenté sous la forme d'un tableau en 9 par 9, les cases exterieures représentent les sorties, certaines sont donc initialisée avec une autoroute ou un chemin de fer. Les autres cases sont donc vide afin de permettre au joueur d'y placer des tuiles durant la partie conformement aux règles du jeu.

#### Classe tile
Cette classe permet de définir les différentes tuiles que peuvent utiliser les joueurs.

#### Classe dice
Cette classe permet de définir les dés à partir des tuiles définient dans la classe `tile`.
