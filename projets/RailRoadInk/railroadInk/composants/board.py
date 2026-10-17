import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from railroadInk.composants.tile import *

"""
Class representing the board of the game
In a first time, the board is a 7x7 grid with the following tiles:
- 2 highways tiles at the top and the bottom of the grid
- 2 railways tiles at the left and the right of the grid
- 4 empty tiles at the corners of the grid
The tiles which are not None are the exits of the grid.
The board is represented by a 2D list of tiles
"""
class Board:
    MAX_RANG = 8
    MIN_RANG = -1
    
    def __init__(self, board = {}):
        self.board = [[None for y in range(self.MIN_RANG, self.MAX_RANG)] for x in range(self.MIN_RANG, self.MAX_RANG)]
        for x in range(self.MIN_RANG, self.MAX_RANG):
            for y in range(self.MIN_RANG,self.MAX_RANG):
                if ((x == 1 or x == 5) and (y == -1 or y == 7)) or ((x == -1 or x == 7) and y == 3) :
                    self.board[x][y] = H_Exit()
                elif (x == 3 and (y == -1 or y == 7)) or ((y == 1 or y == 5) and (x == -1 or x == 7)) :
                    self.board[x][y] = R_Exit()
                else :
                    self.board[x][y]=None
        
        for (x, y) in board:
            self.board[x][y] = board[(x,y)]

        self.fixExitOrientation()
    
    def __repr__(self) -> str:
        """Returns a string representation of the Board object."""
        board = {(x,y): self.board[x][y] for (x, y) in zip(range(1,self.MAX_RANG), range(1,self.MAX_RANG))
                                         if self.board[x][y] != None}
        return f"Board({repr(board)})"

    def __str__(self):
        """Provides a string representation of the Board, displaying tiles in a grid format."""
        s = ""
        for x in range(self.MAX_RANG):
            for y in range(self.MAX_RANG):
                if self.board[x][y] == None:
                    s += " "
                else:
                    s += str(self.board[x][y])
            s += "\n"
        return s
    
    def getTile(self, x, y):
        """
        Retrieve the tile at the specified position on the board.

        Parameters:
        x (int): The row index.
        y (int): The column index.

        Returns:
        Tile: The tile located at position (x, y) on the board.
        """
        return self.board[x][y]


    def setTile(self, x, y, tile):
        """
        Set a tile at the specified position on the board.

        Parameters:
        x (int): The row index where the tile will be placed.
        y (int): The column index where the tile will be placed.
        tile (Tile): The tile object to place at the specified position.

        Returns:
        None
        """
        self.board[x][y] = tile




    def tiles_compatible(self, i:int, j: int, k:int, l:int) -> bool:
        """Returns True if tiles at coordinates (i,j) and (k, l)
        are adjactent and compatible
        """
        is_ok = False
        if i == k-1 and j == l:
            is_ok = Tile.is_compatible_with(self.board[i][j], self.board[k][l], Direction.RIGHT_TO_LEFT)
        elif i == k+1 and j == l :
            is_ok = Tile.is_compatible_with(self.board[i][j], self.board[k][l], Direction.LEFT_TO_RIGHT)
        elif i == k and j == l-1 :
            is_ok = Tile.is_compatible_with(self.board[i][j], self.board[k][l], Direction.BOTTOM_TO_TOP)
        elif i == k and j == l+1 :
            is_ok = Tile.is_compatible_with(self.board[i][j], self.board[k][l], Direction.TOP_TO_BOTTOM)
        return is_ok



    def compatible_in_atLeast_oneDirection(self, tile : Tile, i, j):
        """
        Check if the given tile is compatible with at least one neighboring tile in any direction.

        A tile is considered compatible in a direction if one of its sides matches the 
        corresponding side of a neighboring tile:
        - tile's top matches tile below's bottom
        - tile's bottom matches tile above's top
        - tile's left matches tile to the right's right
        - tile's right matches tile to the left's left

        Parameters:
        tile (Tile): The tile to check compatibility for.
        i (int): The row index where the tile is to be placed.
        j (int): The column index where the tile is to be placed.

        Returns:
        bool: True if the tile is compatible with at least one neighboring tile; False otherwise.
        """
        directions = [tile.getDown(), tile.getTop(), tile.getRight(), tile.getLeft()]
        neighborsDirections = [self.getTile(i-1,j).getRight(), self.getTile(i+1,j).getLeft(), self.getTile(i,j-1).getDown(), self.getTile(i,j+1).getTop()]
        res = False
        for d in directions :
            for nd in neighborsDirections :
                if d == nd :
                    return True 
        return res 
    
    def placesWithCompatibleTiles(self, tile: Tile):
        """
        Find all grid positions where the given tile is compatible with at least one neighbor.

        This method checks each position (excluding the border, from (1,1) to (7,7))
        and determines if the given tile is compatible in at least one direction
        with the surrounding tiles using `compatible_in_atLeast_oneDirection`.

        Parameters:
        tile (Tile): The tile to check for compatibility across the grid.

        Returns:
        list of tuple: A list of (i, j) positions where the tile is compatible 
                    with at least one neighboring tile.
        """
        res = []
        for i in range(1, 8):
            for j in range(1, 8):
                if self.compatible_in_atLeast_oneDirection(tile, i, j):
                    res.append((i, j))
        return res

    
    def fixExitOrientation(self):
        """
            Adjust the orientation of exit tiles on the game board.
        
            This method modifies the orientation of specific exit tiles by flipping or rotating them
            to ensure proper alignment and game setup. It targets specific board positions to 
            manipulate tile orientations.
        """
        self.board[1][-1].top_down_Flip()
        self.board[5][-1].top_down_Flip()
        self.board[3][-1].top_down_Flip()
        self.board[7][3].leftRotate()
        self.board[-1][3].rightRotate()
        self.board[-1][1].rightRotate()
        self.board[-1][5].rightRotate()
        self.board[7][1].leftRotate()
        self.board[7][5].leftRotate()
    