"""
Module de gestion de l'animateur du jeu Railroad Ink.

Ce module contient la classe Animator qui est responsable de la gestion des joueurs
et de leurs permissions dans le jeu. L'animateur peut établir la liste des joueurs
participants et leur accorder des permissions spécifiques pour émettre certains messages.
"""

import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from clients.client import Client
from modules.messages.message import elects, grants

class Animator(Client):
    """
    Client spécialisé dans la gestion des joueurs et de leurs permissions.
    
    L'animateur est responsable de :
    - Établir la liste des joueurs participants
    - Gérer les permissions des joueurs pour émettre des messages spécifiques
    
    Attributes:
        ws (WebSocketClient): Gestionnaire de connexion WebSocket
        id (str): Identifiant de l'animateur (par défaut "Animateur")
        history (optional): Historique des messages
    """

    def __init__(self, ws_url, id="Animateur", history=None):
        """
        Initialise un nouvel animateur.
        
        Args:
            ws_url (str): URL du serveur WebSocket
            id (str): Identifiant de l'animateur (par défaut "Animateur")
            history (optional): Historique des messages
        """
        super().__init__(ws_url, id, history)
    
    def elects(self, *players):
        """
        Établit la liste des joueurs participants à la partie.
        
        Cette méthode envoie un message 'elects' au serveur avec la liste des joueurs
        qui seront autorisés à participer à la partie.
        
        Args:
            *players: Liste variable des identifiants des joueurs participants
        """
        elects(self.ws, self.id, players)
    
    def grants(self, player, message):
        """
        Accorde à un joueur la permission d'émettre un message spécifique.
        
        Cette méthode envoie un message 'grants' au serveur pour autoriser un joueur
        particulier à émettre un type de message spécifique.
        
        Args:
            player (str): Identifiant du joueur
            message (str): Type de message autorisé
        """
        grants(self.ws, self.id, player, message)