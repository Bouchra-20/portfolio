import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from varianteEnum import *

def throws(ws, id, tiles):
    """
    Envoie une commande pour lancer des tuiles.
    
    :param ws: WebSocket utilisé pour envoyer le message.
    :param id: Identifiant de l'auteur.
    :param tiles: Liste des tuiles lancées.
    
    Message envoyé : "THROWS <tiles_liste>"
    """
    tiles_liste = ""
    for tile in tiles:
        tiles_liste += tile
        tiles_liste  += ""
    ws.send(f"{id} THROWS {tiles_liste}")

def places(ws, id, tile, orientation, cell):
    """
    Envoie une commande pour qu’un joueur place une tuile sur le plateau.
    
    :param ws: WebSocket utilisé pour envoyer le message.
    :param id: Identifiant de l'auteur.
    :param tile: Tuile placée.
    :param orientation: Orientation de la tuile.
    :param cell: Emplacement sur le plateau.
    
    Message envoyé : "<id> PLACES <tile> <orientation> <cell>"
    """
    ws.send(f"{id} PLACES {tile} {orientation} {cell}")

def yields(ws, id):
    """
    Envoie une commande pour qu’un joueur passe son tour.
    
    :param ws: WebSocket utilisé pour envoyer le message.
    :param id: Identifiant de l'auteur.
    
    Message envoyé : "<id> YIELDS"
    """
    ws.send(f"{id} YIELDS")

def scores(ws, id, rank, score, points):
    """
    Envoie le score d’un joueur à la fin d’une manche ou d’une partie.
    
    :param ws: WebSocket utilisé pour envoyer le message.
    :param id: Identifiant de l'auteur.
    :param rank: Classement du joueur.
    :param score: Titre d'attribution du score: réseaux/sorties connectées (N),<br> 
                                                plus longue autoroute (H),<br>
                                                plus longue ligne de chemin de fer (R),<br>
                                                occupation du carré central (C),<br>
                                                pénalité d'adjacence (X),<br>
                                                points d'extension (E).<br>
    :param points: Points détaillés.
    
    Message envoyé : "<id> SCORES <rank> <score> <points>"
    """
    ws.send(f"{id} SCORES {rank} {score} {points}")

def blames(ws, id, rank):
    """
    Conteste un message.
    
    :param ws: WebSocket utilisé pour envoyer le message.
    :param id: Identifiant de l'auteur.
    :param rank: Rang du message contesté.
    
    Message envoyé : "<id> BLAMES <rank>"
    """
    ws.send(f"{id} BLAMES {rank}")

def grants(ws, id, player, message):
    """
    Accorde le droit à un autre joueur d'émettre un message spécifique.
    
    :param ws: WebSocket utilisé pour envoyer le message.
    :param id: Identifiant de l'auteur.
    :param player: Joueur qui reçoit l’autorisation.
    :param message: Message faisant l'objet d'une autorisation.
    
    Message envoyé : "<id> GRANTS <player.id> <message>"
    """
    ws.send(f"{id} GRANTS {player.id} {message}")

def agrees(ws, id, variante):
    """
    Indique le choix d'une variante du jeu.
    
    :param ws: WebSocket utilisé pour envoyer le message.
    :param id: Identifiant de l'auteur.
    :param variante: Variante choisie.
    
    Message envoyé : "<id> AGREES <varianteStr[variante]>"
    """
    ws.send(f"{id} AGREES {varianteStr[variante]}")
    
def elects(ws, id, players):
    """
    Envoie une commande établissant la liste des participants.
    
    :param ws: WebSocket utilisé pour envoyer le message.
    :param id: Identifiant de l'auteur.
    :param players: Liste des joueurs participants.
    
    Message envoyé : "<id> ELECTS <player_liste>"
    """
    player_liste = ""
    for player in players:
        player_liste += player.name
        player_liste += " "
    ws.send(f"{id} ELECTS {player_liste}")