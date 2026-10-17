"""
Module de gestion du lanceur de dés du jeu Railroad Ink.

Ce module contient la classe DiceLauncher qui est responsable du lancement des dés
pour déterminer les tuiles que les joueurs devront utiliser pendant leur tour.
Le lanceur de dés gère deux types de dés : les dés normaux (routes et rails)
et les dés de connexion (tuiles spéciales).
"""

import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from clients.client import Client
from modules.messages.message import throws
from random import *

from composants.dice import Connection_Dice, Road_Rail_Dice

class DiceLauncher(Client):
    """
    Client spécialisé dans le lancement des dés du jeu Railroad Ink.
    
    Ce client est responsable de :
    - Lancer les dés normaux (routes et rails)
    - Lancer les dés de connexion (tuiles spéciales)
    - Communiquer les résultats des lancers aux joueurs
    
    Attributes:
        ws_url (str): URL du serveur WebSocket
        id (str): Identifiant du lanceur de dés (par défaut "LanceurDeDes")
        history (optional): Historique des messages
    """
    
    def __init__(self, ws_url, id="LanceurDeDes", history=None):
        """
        Initialise un nouveau lanceur de dés.
        
        Args:
            ws_url (str): URL du serveur WebSocket
            id (str): Identifiant du lanceur de dés (par défaut "LanceurDeDes")
            history (optional): Historique des messages
        """
        super().__init__(ws_url, id, history)

    def launchNormal(self):
        """
        Lance un dé normal (routes et rails).
        
        Cette méthode crée et lance un dé normal qui détermine une tuile
        de route ou de rail que le joueur pourra utiliser.
        
        Returns:
            str: Identifiant de la tuile obtenue
        """
        dice = Road_Rail_Dice()
        return dice.roll()

    def launchConnection(self):
        """
        Lance un dé de connexion (tuile spéciale).
        
        Cette méthode crée et lance un dé spécial qui détermine une tuile
        de connexion que le joueur pourra utiliser.
        
        Returns:
            str: Identifiant de la tuile spéciale obtenue
        """
        dice = Connection_Dice()
        return dice.roll()

    def launch(self):
        """
        Lance tous les dés nécessaires pour un tour de jeu.
        
        Cette méthode lance trois dés normaux et un dé de connexion,
        puis envoie les résultats aux joueurs via le serveur.
        
        Returns:
            list: Liste des tuiles obtenues [3 tuiles normales, 1 tuile spéciale]
        """
        dices = [self.launchNormal(), self.launchNormal(), self.launchNormal(), self.launchConnection()]
        throws(self.ws, self.id, dices)
        return dices
