import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from railroadInk.composants.tile import *
from random import *


class Dice:
    """
    A dice class for randomly selecting and generating tile names.
    
    Attributes:
        tilesList (list): A list of tile classes to choose from when rolling the dice.
    """
    def __init__(self, l ):
        self.tilesList = l


    def roll(self):
        """
        Randomly selects and returns the name of a tile from the predefined list of tile classes.
    
        Returns:
            str: The name of the randomly chosen tile.
        """
        chosenClass = choice(self.tilesList)
        chosenTile = chosenClass()
        return chosenTile.getName()


class Road_Rail_Dice(Dice):
    """A specialized dice class for rolling Highway and Rail tiles."""
    def __init__(self):
        super().__init__(
            l = Highway_Tile.getSubClasses() + Rail_Tile.getSubClasses()
        )


class Connection_Dice(Dice):
    """A specialized dice class for rolling Connection tiles."""
    def __init__(self):
        super().__init__(
            l = Connection_Tile.getSubClasses() + Connection_Tile.getSubClasses()
        )



def main():
    dice = Road_Rail_Dice()
    print(dice.roll())

    dice2 = Connection_Dice()
    print(dice2.roll())





if __name__ == "__main__":
    main()