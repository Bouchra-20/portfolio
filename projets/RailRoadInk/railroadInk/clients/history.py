"""
Module de gestion de l'historique des messages du jeu Railroad Ink.

Ce module contient la classe History qui maintient un historique chronologique
de tous les messages échangés pendant la partie. Chaque message est associé
à un rang unique qui permet de le référencer.
"""

from railroadInk.clients.client import Client

class History(Client):
    """
    Client spécialisé dans le suivi de l'historique des messages.
    
    Cette classe maintient un dictionnaire global (HISTORYD) qui stocke tous les messages
    échangés pendant la partie, indexés par leur rang. Le rang commence à 2 pour prendre
    en compte le message d'initialisation.
    
    Attributes:
        HISTORYD (dict): Dictionnaire global stockant l'historique des messages
        current (int): Rang actuel du dernier message enregistré
    """
    
    # Dictionnaire global pour stocker l'historique des messages
    HISTORYD = {}

    def __init__(self, ws_url, id="Historique"):
        """
        Initialise un nouveau client d'historique.
        
        Args:
            ws_url (str): URL du serveur WebSocket
            id (str): Identifiant du client (par défaut "Historique")
        """
        self.current = 2
        super().__init__(ws_url, id, None)
        self.current = 2  # Le rang commence à 2 pour le message d'initialisation
        
    def handle_message(self, message):
        """
        Traite les messages reçus et les enregistre dans l'historique.
        
        Cette méthode est appelée automatiquement à chaque réception de message.
        Elle enregistre le message dans le dictionnaire HISTORYD avec le rang actuel
        comme clé, puis incrémente le rang pour le prochain message.
        
        Args:
            message (str): Le message à enregistrer
        """
        self.HISTORYD[self.current] = message
        self.current += 1