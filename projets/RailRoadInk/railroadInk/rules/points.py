import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

import random
from railroadInk.composants.board import Board
from railroadInk.composants.dice import Dice
from railroadInk.modules.graphes import *
from railroadInk.composants.tile import *

"""
Class Points contains the methods to calculate the points of a player    
"""
class Points :
    def __init__(self):
        self.dices = []



    def longest_road(self, player, road_type : Tile) :
        """
        Method to calculate the points of a player for the longer road
        @param player : the player for which we calculate the points
        @param road_type : the type of road we want to calculate the points for
        @return the number of points for the longest road
        """
        graph = Graphes()
        graph.create_graphe(player, road_type)
        longest_path = graph.longest_path()
        return len(longest_path)
    

    
    def is_connected_to_exit(self, x, y):
        """
        Method to check if a tile at (x, y) is connected to an exit.
        @param x: x-coordinate of the tile
        @param y: y-coordinate of the tile
        @return True if the tile is connected to an exit, False otherwise
        """
        exits = [(1, 0), (3, 0), (5, 0), (1, 6), (3, 6), (5, 6), (0, 1), (0, 3), (0, 5), (6, 1), (6, 3), (6, 5)]
        return (x, y) in exits

    
    def reseaux_points(self, player) :
        """"
        Method to calculate the points of a player for the number of exits connected.
        The number of points is calculated as follows:
        2 exits connected = 4 points / 3 exits connected = 8 points / 4 exits connected = 12 points / 5 exits connected = 16 points
        6 exits connected = 20 points / 7 exits connected = 24 points / 8 exits connected = 28 points / 9 exits connected = 32 points
        10 exits connected = 36 points / 11 exits connected = 40 points / 12 exits connected = 45 points
        @param player : the player for which we calculate the points
        @return the number of points for the number of exits connected
        """
        points = 0
        exits = 0
        board = player.getBoard()

        for x in range(-1, Board.MAX_RANG):
            for y in range(-1, Board.MAX_RANG):
                if board.getTile(x, y) is not None and self.is_connected_to_exit(x, y):
                    exits += 1

        if exits == 2:
            points = 4 
        elif exits == 3:
            points = 8
        elif exits == 4:
            points = 12 
        elif exits == 5:
            points = 16
        elif exits == 6:
            points = 20
        elif exits == 7:
            points = 24
        elif exits == 8:
            points = 28
        elif exits == 9:
            points = 32
        elif exits == 10:
            points = 36
        elif exits == 11:
            points = 40
        elif exits == 12:
            points = 45
        return points
        
    
    
    def road_points(self, player) :
        """
        Method to calculate the points of a player for the longer road
        @param player : the player for which we calculate the points
        @return the number of points for the longest road
        """
        return self.longest_road(player, Highway_Tile)
    

    
    def rail_points(self, player) :
        """
        Method to calculate the points of a player for the longer rail
        @param player : the player for which we calculate the points
        @return the number of points for the longest rail
        """
        return self.longest_road(player, Rail_Tile)
    
    
    def center_points(self, player):
        """
        Method to calculate the points of a player for the center of the grid.
        The player gets 1 point for each tile he has placed in the center of the grid
        @param player : the player for which we calculate the points
        @return the number of points for the center of the grid
        """
    
        points = 0
        for i in range(2, 5): # we check the center of the grid
            for j in range(2, 4):  
                if player.getBoard().getTile(i, j) is not None: # if the player has placed a tile on the center of the grid 
                    points += 1 # he gets 1 point
        return points

    
    
    def bad_connection_points(self, player) :
        """
        Method to calculate the points of a player for the bad connections. 
        The player loses 1 point for each bad connection
        @param player : the player for which we calculate the points
        @return the number of points for the bad connections
        """
        points = 0
        for i in range(7):
            for j in range(7):
                if player.getBoard().getTile(i, j) is not None:
                    if player.getBoard().getTile(i, j).is_compatible_with(player.getBoard().getTile(i+1, j), Direction.RIGHT_TO_LEFT) == False:
                        points -= 1
                    if player.getBoard().getTile(i, j).is_compatible_with(player.getBoard().getTile(i- 1, j), Direction.LEFT_TO_RIGHT) == False:
                        points -= 1
                    if player.getBoard().getTile(i, j).is_compatible_with(player.getBoard().getTile(i, j+1), Direction.BOTTOM_TO_TOP) == False:
                        points -= 1
                    if player.getBoard().getTile(i, j).is_compatible_with(player.getBoard().getTile(i, j-1), Direction.TOP_TO_BOTTOM) == False:
                        points -= 1
        return points
    

    
    def count_points(self, player) :
        """
        Method to calculate the points of a player.
        @param player : the player for which we calculate the points
        @return the number of points of the player
        """
        return self.reseaux_points(player) + self.road_points(player) + self.rail_points(player) + self.center_points(player) + self.bad_connection_points(player)
