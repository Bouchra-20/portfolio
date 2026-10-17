"""
Module de gestion de l'arbitre du jeu Railroad Ink.

Ce module contient la classe Referee qui est responsable de la validation
des messages du jeu et de l'application des règles. L'arbitre vérifie que
tous les messages respectent les règles du jeu et peut blâmer les messages
qui ne les respectent pas.
"""

from clients.client import Client
from modules.messages.message import blames
import re

class Referee(Client):
    """
    Client spécialisé dans l'arbitrage du jeu Railroad Ink.
    
    L'arbitre est responsable de :
    - Charger et maintenir les règles du jeu
    - Valider les messages des joueurs
    - Appliquer les sanctions en cas de violation des règles
    
    Attributes:
        ws_url (str): URL du serveur WebSocket
        id (str): Identifiant de l'arbitre (par défaut "Referee")
        history (object): Historique des messages
        rules (dict): Règles du jeu chargées
    """
    
    def __init__(self, ws_url, id="Referee", history=None):
        """
        Initialise un nouvel arbitre.
        
        Args:
            ws_url (str): URL du serveur WebSocket
            id (str): Identifiant de l'arbitre (par défaut "Referee")
            history (object): Historique des messages
        """
        super().__init__(ws_url, id, history)
        self.rules = self.load_rules()

    def handle_message(self, message):
        """
        Traite et valide les messages reçus.
        
        Cette méthode est appelée automatiquement à chaque réception de message.
        Elle analyse le message et vérifie s'il respecte les règles du jeu.
        En cas de violation, elle applique les sanctions appropriées.
        
        Args:
            message (str): Le message reçu à valider
        """
        if not message:
            return
            
        parts = message.strip().split()
        if not parts:
            return
            
        sender, command, *params = parts
        
        # Vérification des messages de placement
        if command == "PLACES":
            if len(params) != 3:
                self.blames()
                return
                
            tile, orientation, cell = params
            if tile not in self.rules["valid_tiles"]:
                self.blames()
            if orientation not in self.rules["valid_orientations"]:
                self.blames()
            if not re.match(r"^[A-G][1-7]$", cell):
                self.blames()
        
        # Vérification des messages de lancer de dés
        elif command == "THROWS":
            for tile in params:
                if tile not in self.rules["valid_tiles"]:
                    self.blames()
                    break

        # Vérification générale des règles
        if not self.respect_rules(message):
            self.blames()

    def load_rules(self):
        """
        Charge les règles du jeu.
        
        Cette méthode charge les règles depuis un fichier de configuration
        ou une base de données.
        
        Returns:
            dict: Dictionnaire contenant les règles du jeu
        """
        pass
    
    def respect_rules(self, message):
        """
        Vérifie si un message respecte toutes les règles du jeu.
        
        Args:
            message (str): Message à vérifier
            
        Returns:
            bool: True si le message respecte les règles, False sinon
        """
        if not self.rule(message):
            return False
        return True

    def blames(self):
        """
        Applique une sanction pour un message invalide.
        
        Cette méthode envoie un message de blâme au serveur avec le rang
        du message problématique. Le rang est calculé comme le rang actuel
        moins 1 pour pointer vers le message concerné.
        """
        rank = self.history.current - 1
        blames(self.ws, self.id, rank)
