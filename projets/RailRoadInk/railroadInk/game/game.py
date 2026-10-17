import os
import sys

# Ajouter le projet et son dossier parent au chemin d'import
project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(project_root)
sys.path.append(os.path.dirname(project_root))

from clients.animator import Animator
from clients.dicelauncher import DiceLauncher
from clients.history import History
from clients.player import Player
from clients.referee import Referee
from rules.Rules import Rules
from rules.points import Points
from interface.interface import Interface

class Game:
    """
    Manages the game logic for Railroad Ink, handling players, turns, scoring, and game interactions.
    
    Attributes:
        DEFAULT_PORT (int): Default network port for game communication.
        NBPARTICIPANTS (int): Maximum number of players allowed.
        NB_TURNS (int): Number of turns in a game.
    """
    DEFAULT_PORT = 10000
    NBPARTICIPANTS = 4
    NB_TURNS = 1

    def __init__(self, port=DEFAULT_PORT):
        self.players = []
        self.port = port
        #self.ws_url = "ws://localhost:"+str(self.DEFAULT_PORT)
        self.ws_url = "ws://localhost:"+str(self.port)
        #self.board = Board()
        self.rules = None
        self.nb_participants = self.NBPARTICIPANTS
        self.history = History(self.ws_url, "hist")
        self.animator = Animator(self.ws_url, "anim", self.history)
        self.dicelauncher = DiceLauncher(self.ws_url, "diceLn", self.history)
        self.referee = Referee(self.ws_url, "ref", self.history)
        self.interface = Interface()
        #self.interface = None
        self.points = Points()

    def addPlayer(self, id):
        """
        Adds a new player to the game.

        Args:
            id (int): The unique identifier for the player.
        """
        newPlayer = Player(self.ws_url, id, self.history)
        self.players.append(newPlayer)
        newPlayer.setInterface(self.interface)

    def play(self):
        """
        Executes the game play sequence for all players across a predefined number of turns.
    
        This method manages the game flow by:
        - Iterating through a fixed number of turns
        - Launching dice for each player
        - Adding dice tiles to the interface
        - Initializing game rules for each player's turn
        - Allowing each player to play their turn
         """
        for turn in range(self.NB_TURNS):
            for player in self.players:
                interface = player.getInterface()
                dices = self.dicelauncher.launch()
                for d in dices:
                    interface.addTileBar(d)

                self.rules = Rules(self.players, dices, player.getBoard(), interface.getSpecialTile())
                player.playTurn()

    def score(self, player):
        """
        Calculates and displays the score for a given player.
        
        Args:
            player (Player): The player whose score is to be calculated.
    
        Returns:
            int: The player's final score, guaranteed to be non-negative.
        """
        score = self.points.count_points(player)
        if score < 0:
            score = 0
            
        player.displayScore(score)
        return score


def main():
    game = Game(3000)
    game.addPlayer(1)
    #game.addPlayer(2)
    game.play()

    # Count points of each player
    for player in game.players: 
        print("Player", player.id, "scored", game.score(player), "points") # voir comment le gérer dans l'interface

if __name__ == "__main__":
    main()