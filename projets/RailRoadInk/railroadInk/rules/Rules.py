import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))


from collections import Counter
from railroadInk.composants.tile import *


class Rules() :
    """
    Manages game rules and validation for Railroad Ink board game.
    
    Provides methods to check board state, special tile usage constraints, 
    and dice placement rules for a single player's turn.
    
    Attributes:
        __player (Player): The current player
        __diceLauncher (list): Dice rolled for the current turn
        __board (Board): Game board state
        __specialTiles (list): Special tiles available to the player
    """
    def __init__(self, player, dicesRolledByLauncher, board, specialTiles):
        self.__player = player
        self.__diceLauncher = dicesRolledByLauncher
        self.__board = board
        self.__specialTiles = specialTiles


    def isBoardEmptyAtFirst(self) :
        """
        Checks if the game board is completely empty at the start of the game.
    
        Returns:
            bool: True if no tiles are placed on the board, False otherwise.
        """
        return all(all(self.__board.getTile(i ,j) is None for i in range(0, 7)) for j in range(0, 7))
    

    def usagePerRound_SpecialTiles (self):
        """
        Checks if each special tile is used no more than once per round.
    
        Returns:
            list: A list of boolean values indicating whether each special tile 
                    has been used no more than once in the current round.
        """
        return [tile.getUsagePerRound() <= 1 for tile in self.__specialTiles]
    
    def usagePerGame_SpecialTiles (self):
        """
        Checks if each special tile is used no more than three times throughout the entire game.
    
        Returns:
            list: A list of boolean values indicating whether each special tile 
                    has been used no more than three times in the game.
        """
        return [tile.getUsagePerGame() <= 3 for tile in self.__specialTiles]
    
    def usagePerTile_SpecialTiles (self):
        """
        Checks if each special tile is used no more than once per tile placement.
    
        Returns:
            list: A list of boolean values indicating whether each special tile 
                    has been used no more than once per individual tile placement.
        """
        return [tile.getUsagePerTile() <= 1 for tile in self.__specialTiles]
    
    def specialTilesUsage(self):
        """
        Checks if special tiles are used within their allowed constraints across game, round, and tile placement.
    
        Returns:
            bool: True if special tiles comply with usage limits, False otherwise.
        """
        return self.usagePerGame_SpecialTiles and self.usagePerRound_SpecialTiles and self.usagePerTile_SpecialTiles
    
    def nbDicesEqualsToNbPlacedDices(self) :
        """
        Checks if the number of used tiles matches the number of dice rolled.
    
        Returns:
            bool: True if the count of used tiles is equal to the count of dice, False otherwise.
        """
        return Counter(self.__player.getUsedTiles()) == Counter(self.__diceLauncher)
    