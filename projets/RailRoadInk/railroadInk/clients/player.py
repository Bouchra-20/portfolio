"""
Module de gestion des joueurs du jeu Railroad Ink.

Ce module contient la classe Player qui représente un joueur dans le jeu.
Elle gère l'état du jeu pour le joueur, ses tuiles, son plateau et ses interactions
avec l'interface utilisateur.
"""

from clients.client import Client
from railroadInk.composants.tile import Tile
from railroadInk.composants.board import Board
from railroadInk.interface.interface import Interface

class Player(Client):
    """
    Représente un joueur dans le jeu Railroad Ink.
    
    Cette classe gère :
    - L'état du jeu pour le joueur
    - Les tuiles disponibles et utilisées
    - Le plateau de jeu personnel
    - Les interactions avec l'interface utilisateur
    
    Attributes:
        ws_url (str): URL du serveur WebSocket
        id (str): Identifiant unique du joueur
        history (list): Historique de la partie
        launchedTiles (list): Tuiles lancées pendant la partie
        __usedTiles (list): Tuiles utilisées par le joueur
        board (Board): Plateau de jeu du joueur
        interface: Interface utilisateur pour les interactions
    """

    def __init__(self, ws_url, id, history):
        """
        Initialise un nouveau joueur.
        
        Args:
            ws_url (str): URL du serveur WebSocket
            id (str): Identifiant du joueur
            history (list): Historique de la partie
        """
        super().__init__(ws_url, id, history)
        self.launchedTiles = []
        self.__usedTiles = []
        self.board = Board()
#        self.interface = None
        self.interface = Interface()
        #self.interface.setBoard(self.board)

    def handle_message(self, message):
        """
        Traite les messages reçus par le joueur.
        
        Cette méthode est appelée automatiquement à chaque réception de message.
        Elle gère différents types de messages :
        - THROWS : Mise à jour des tuiles lancées
        - SCORES : Affichage du score
        - Autres messages spécifiques au jeu
        
        Args:
            message (str): Le message reçu à traiter
        """
        parts = message.strip().split()
        if not parts:
            return
            
        sender, command, *params = parts
        
        if command == "THROWS":
            self.launchedTiles.append(params)
        elif command == "SCORES":
            if len(params) > 0:
                self.displayScore(int(params[0]))

    def getUsedTiles(self):
        """
        Récupère la liste des tuiles utilisées par le joueur.
        
        Cette méthode met à jour la liste interne des tuiles utilisées en appelant
        la méthode getUsedTile de l'interface, puis retourne cette liste.
        
        Returns:
            list: Liste des tuiles utilisées
        """
        self.__usedTiles = self.interface.getUsedTile()
        return self.__usedTiles


    def addToUsedTiles(self, tile : Tile) :
        """
        Ajoute une tuile à la liste des tuiles utilisées.
        
        Args:
            tile (Tile): Tuile à ajouter à la liste
        """
        self.__usedTiles.append(tile)

    def removeFromUsedTiles(self):
        """
        Retire la dernière tuile de la liste des tuiles utilisées.
        
        Cette méthode est utilisée pour annuler le dernier placement de tuile.
        """
        self.__usedTiles.pop()

    def playTurn(self):
        """
        Exécute le tour de jeu du joueur.
        
        Cette méthode lance l'interface utilisateur pour permettre au joueur
        d'effectuer ses actions pendant son tour.
        """
        self.interface.run(self.ws)

    def getBoard(self):
        """
        Récupère le plateau de jeu du joueur.
        
        Returns:
            Board: Plateau de jeu du joueur
        """
        return self.board

    def setInterface(self, interface):
        """
        Configure l'interface utilisateur pour le joueur.
        
        Cette méthode initialise l'interface et configure le plateau de jeu
        pour l'affichage.
        
        Args:
            interface: Interface utilisateur à configurer
        """
        self.interface = interface
        self.interface.setBoard(self.board)

    def getInterface(self):
        return self.interface

    def displayScore(self, score):
        """
        Affiche le score du joueur.
        
        Cette méthode utilise l'interface pour afficher le score final
        du joueur à la fin de la partie.
        
        Args:
            score (int): Score du joueur à afficher
        """
        self.interface.display_score(score)