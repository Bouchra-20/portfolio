"""
Module de gestion des clients du jeu Railroad Ink.

Ce module contient la classe Client qui sert de base pour tous les clients du jeu.
Chaque client est connecté au serveur via une WebSocket et peut envoyer/recevoir des messages.
"""

import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


from modules.messages.websocketCo import WebSocketClient


class Client():
    """
    Classe de base pour tous les clients du jeu Railroad Ink.
    
    Cette classe gère la connexion WebSocket et les communications de base.
    Elle sert de point d'entrée pour tous les types de clients (joueurs, arbitre, etc.).
    
    Attributes:
        ws (WebSocketClient): Gestionnaire de connexion WebSocket
        id (str): Identifiant unique du client
        history (optional): Historique optionnel associé au client
    """

    def __init__(self, ws_url, id, history=None):
        """
        Initialise un nouveau client.
        
        Args:
            ws_url (str): URL du serveur WebSocket
            id (str): Identifiant unique du client
            history (optional): Historique des messages (optionnel)
        """
        self.id = id
        self.history = history
        self.ws = WebSocketClient(ws_url, id)
        self.ws.set_message_callback(self._dispatch_message)
        self.ws.connect()

    def _dispatch_message(self, _client_id, message):
        self.handle_message(message)

    def handle_message(self, message):
        """
        Méthode de base pour gérer les messages reçus.
        
        Cette méthode doit être surchargée par les classes filles pour implémenter
        leur propre logique de traitement des messages.
        
        Args:
            message (str): Message reçu du serveur
        """
        pass