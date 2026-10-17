from enum import Enum

import random

class RoadType(Enum) :
    NONE = 0
    HIGHWAY = 1
    RAIL = 2

class Direction(Enum) :
    LEFT_TO_RIGHT = 0
    RIGHT_TO_LEFT = 1
    TOP_TO_BOTTOM = 2
    BOTTOM_TO_TOP = 3


class Tile:
    """
    Represents a tile in a board game with road connections and rotation capabilities.
    
    This class manages the state and behavior of a game tile.
    
    Attributes:
        __top (RoadType): Road type for the top side of the tile
        __right (RoadType): Road type for the right side of the tile
        __down (RoadType): Road type for the bottom side of the tile
        __left (RoadType): Road type for the left side of the tile
        __name (str): Identifier for the tile
        __urlPNG (str): Path to the tile's PNG graphic
        __urlSVG (str): Path to the tile's SVG graphic
        __round (int): Round in which the tile was used
        __rotation (int): Current rotation angle of the tile
    """
    def __init__(self, top : RoadType, right : RoadType,  down : RoadType, left : RoadType, name, urlPNG, urlSVG):
        self.__top = top
        self.__right = right
        self.__down = down
        self.__left = left
        self.__name = name
        self.__urlPNG = urlPNG
        self.__urlSVG = urlSVG
        self.__round = 0
        self.__rotation = 0

    def getRotation(self):
        """
        Retrieves the current rotation angle of the tile.
    
        Returns:
            int: The current rotation angle of the tile in degrees.
        """
        return self.__rotation
    
    def setRotation(self, angle):
        """Met à jour l'angle de rotation"""
        self.__rotation = angle % 360


    def getURLPNG(self):
        """
        Retrieves the URL path to the tile's PNG graphic.
    
        Returns:
            str: The file path to the tile's PNG image.
        """
        return self.__urlPNG
    
    def getURLSVG(self):
        """
        Retrieves the URL path to the tile's SVG graphic.
    
        Returns:
            str: The file path to the tile's SVG image.
        """
        return self.__urlSVG
    
    def __repr__(self):
        """
        Provides a string representation of the Tile object for debugging and logging purposes.
    
        Returns:
            str: A formatted string containing the tile's road types and name.
        """
        return f"Tile({self.__top}, {self.__right}, {self.__down}, {self.__left}, {repr(self.__name)})"

    @staticmethod
    def createTile(name):
        """
        Creates a tile instance based on the given tile name.
    
        Args:
            name (str): The name of the tile to create.
    
        Returns:
            Tile: An instance of the specified tile class.
    
        Raises:
            ValueError: If the tile name does not match any known tile class.
        """
        tile_classes = {cls.__name__.replace("_Tile", ""): cls for cls in Tile.getSubClasses()}

        tile_class = tile_classes.get(name)
        if tile_class:
            return tile_class()
        raise ValueError(f"Unknown tile type: {name}")
        
    def getName(self):
        """Retrieves the name of the tile."""
        return self.__name

    # Top part of the Tile
    def getTop(self):
        """Retrieves the top road connection of the tile."""
        return self.__top
    

    # Right part of the Tile
    def getRight(self):
        """Retrieves the right road connection of the tile."""
        return self.__right
    
    
    # Down part of the Tile
    def getDown(self):
        """Retrieves the down road connection of the tile."""
        return self.__down
    
    
    # Down part of the Tile 
    def getLeft(self):
        """Retrieves the left road connection of the tile."""
        return self.__left

    
    # Rotations
    def rightRotate(self) : 
        """Rotates the tile 90 degrees clockwise by rearranging its road connections. """
        self.__top, self.__right, self.__down, self.__left = (
            self.__left,
            self.__top,
            self.__right,
            self.__down
        )
    
    def leftRotate(self) : 
        """Rotates the tile 90 degrees counterclockwise by rearranging its road connections."""
        self.__top, self.__right, self.__down, self.__left = (
            self.__right,
            self.__down,
            self.__left,
            self.__top
        )

    # Getter and modifier of the round of the tile in which we used it
    def getRound(self):
        """Retrieves the round number in which the tile was used."""
        return self.__round
    
    def increaseRoundBy1(self) :
        """Increments the round number of the tile by 1."""
        self.__round += 1


    # Flippings
    def right_left_Flip(self) :
        self.__right, self.__left = (self.__left, self.__right)

    def top_down_Flip(self) :
        self.__top, self.__down = (self.__down, self.__top)


    @classmethod
    def getSubClasses(cls):
        """
        Recursively retrieves all subclasses of the current class.
    
        Returns:
            list: A list containing all direct and indirect subclasses of the class.
        """
        subclasses = list(cls.__subclasses__()) 
        for subclass in cls.__subclasses__():
            subclasses.extend(subclass.getSubClasses())  
        return subclasses 
    


    def is_compatible_with(self, other, direction: Direction) -> bool:
        """
        >>> R_Tile().is_compatible_with(R_Tile, TOP_TO_BOTTOM)
        True
        >>> R_Tile().is_compatible_with(R_Tile, LEFT_TO_RIGHT)
        False
        """
        is_ok = False
        if other is not None:
            if direction == Direction.RIGHT_TO_LEFT and self.getRight() == other.getLeft():
                is_ok = True
            elif direction == Direction.LEFT_TO_RIGHT and self.getLeft() == other.getRight() :
                is_ok = True
            elif direction == Direction.BOTTOM_TO_TOP and self.getDown() == other.getTop() :
                is_ok = True 
            elif self.getTop() == other.getDown() :
                is_ok = True
            
        return is_ok

    def compatible(self, other) -> bool:
        """
        Checks if the current tile is compatible with another tile by comparing road types on each side.
    
        Args:
            other: Another tile to check compatibility with.
    
        Returns:
            bool: True if any non-NONE road side of the current tile matches a road side of the other tile, False otherwise.
        """
        if (self.getRight() is not RoadType.NONE) and ((self.getRight() == other.getRight()) or (self.getRight() == other.getLeft()) or (self.getRight() == other.getTop()) or (self.getRight() == other.getDown())) :
            return True
        elif (self.getLeft() is not RoadType.NONE) and ((self.getLeft() == other.getLeft()) or (self.getLeft() == other.getTop()) or (self.getLeft() == other.getRight()) or (self.getLeft() == other.getDown())):
            return True
        elif (self.getTop() is not RoadType.NONE) and ((self.getTop() == other.getTop()) or (self.getTop() == other.getRight()) or (self.getTop() == other.getLeft()) or (self.getTop() == other.getDown())):
            return True
        elif (self.getDown() is not RoadType.NONE) and ((self.getDown() == other.getDown()) or (self.getDown() == other.getRight()) or (self.getDown() == other.getLeft()) or (self.getDown() == other.getTop())):
            return True
        else:
            return False
        
    def face_compatible(self, face, other) -> bool:
        """Vérifie si une face est compatible avec une autre tuile."""
        if (face is not RoadType.NONE) and ((face == other.getRight()) or (face == other.getLeft()) or (face == other.getTop()) or (face == other.getDown())) :
            return True
        else:
            return False



# Highway tiles
class Highway_Tile(Tile) :
    """
    Represents a highway tile in the Railroad Ink game, inheriting from the base Tile class.
    
    Args:
        top (RoadType): Road type for the top side of the tile
        right (RoadType): Road type for the right side of the tile
        down (RoadType): Road type for the bottom side of the tile
        left (RoadType): Road type for the left side of the tile
        name (str): Identifier name for the highway tile
        urlPNG (str): File path to the PNG graphic representation of the tile
        urlSVG (str): File path to the SVG graphic representation of the tile
    """
    def __init__(self, top, right, down, left, name, urlPNG, urlSVG):
        super().__init__(top, right, down, left, name, urlPNG, urlSVG)

class H_Tile(Highway_Tile) :
    """Represents a straight highway tile in the Railroad Ink game with highway connections on the top and bottom sides."""
    def __init__(self):
        super().__init__(
            top = RoadType.HIGHWAY,
            right = RoadType.NONE, 
            left = RoadType.NONE, 
            down = RoadType.HIGHWAY,
            name = "H",
            urlPNG = './graphics/highway-straight.png',
            urlSVG = './graphics/highway-straight.svg'
        )

class Hj_Tile(Highway_Tile) :
    """Represents a highway junction tile in the Railroad Ink game with highway connections on the top, right, and bottom sides."""
    def __init__(self):
        super().__init__(
            top = RoadType.HIGHWAY,
            right = RoadType.HIGHWAY,
            down = RoadType.HIGHWAY,
            left = RoadType.NONE,
            name = "Hj",
            urlPNG = "./graphics/highway-junction.png",
            urlSVG = "./graphics/highway-junction.svg"
        )

class Hc_Tile(Highway_Tile) :
    """Represents a highway curve tile in the Railroad Ink game with highway connections on the top and right sides."""
    def __init__(self):
        super().__init__(
            top = RoadType.HIGHWAY,
            right = RoadType.HIGHWAY,
            down = RoadType.NONE,
            left = RoadType.NONE,
            name = "Hc",
            urlPNG = './graphics/highway-curve.png',
            urlSVG = './graphics/highway-curve.svg'
        )

class H_Exit(Highway_Tile):
    """Represents a highway exit tile in the Railroad Ink game with a highway connection only on the top side."""
    def __init__(self):
        super().__init__(
            top = RoadType.HIGHWAY,
            right = RoadType.NONE,
            down = RoadType.NONE,
            left = RoadType.NONE,
            name = "H",
            urlPNG = None,
            urlSVG = None
        )

# Rail tiles
class Rail_Tile(Tile):
    """Represents a rail tile in the Railroad Ink game, defining the road connections and graphical representation for rail-based tiles."""
    def __init__(self, top, right, down, left, name, urlPNG, urlSVG):
        super().__init__(top, right, down, left, name, urlPNG, urlSVG)

class R_Tile(Rail_Tile) :
    """Represents a railway straight tile in the Railroad Ink game with railway connections on the top and bottom sides."""
    def __init__(self):
        super().__init__(
            top = RoadType.RAIL,
            right = RoadType.NONE, 
            left = RoadType.NONE, 
            down = RoadType.RAIL,
            name = "R",
            urlPNG = './graphics/railway-straight.png',
            urlSVG = './graphics/railway-straight.svg'
        )

class Rj_Tile(Rail_Tile) :
    """Represents a railway junction tile in the Railroad Ink game with railway connections on the top, right, and bottom sides."""
    def __init__(self):
        super().__init__(
            top = RoadType.RAIL,
            right = RoadType.RAIL,
            down = RoadType.RAIL,
            left = RoadType.NONE,
            name = "Rj", 
            urlPNG = './graphics/railway-junction.png',
            urlSVG = './graphics/railway-junction.svg'
        )

class Rc_Tile(Rail_Tile) :
    """Represents a railway curve tile in the Railroad Ink game with railway connections on the top and right sides."""
    def __init__(self):
        super().__init__(
            top = RoadType.RAIL,
            right = RoadType.RAIL,
            down = RoadType.NONE,
            left = RoadType.NONE,
            name = "Rc",
            urlPNG = './graphics/railway-curve.png',
            urlSVG = './graphics/railway-curve.svg'
        )

class R_Exit(Rail_Tile) :
    """Represents a railway exit tile in the Railroad Ink game with a railway connection only on the top side."""
    def __init__(self):
        super().__init__(
            top = RoadType.RAIL,
            right = RoadType.NONE, 
            left = RoadType.NONE, 
            down = RoadType.NONE,
            name = "R",
            urlPNG = None,
            urlSVG = None
        )

# Connection tiles
class Connection_Tile(Tile) :
    """
    Represents a connection tile in the Railroad Ink game, inheriting from the base Tile class.
    
    This class initializes a tile with specific road connections and graphical representations.
    
    Args:
        top (RoadType): Road type for the top connection
        right (RoadType): Road type for the right connection
        down (RoadType): Road type for the bottom connection
        left (RoadType): Road type for the left connection
        name (str): Identifier name for the tile
        urlPNG (str): File path to the PNG graphic representation
        urlSVG (str): File path to the SVG graphic representation
    """
    def __init__(self, top, right, down, left, name, urlPNG, urlSVG):
        super().__init__(top, right, down, left, name, urlPNG, urlSVG)

class S_Tile(Connection_Tile) :
    def __init__(self):
        super().__init__(
            top = RoadType.HIGHWAY,
            right = RoadType.NONE,
            down = RoadType.RAIL,
            left = RoadType.NONE,
            name = "S",
            urlPNG = './graphics/station-straight.png',
            urlSVG = './graphics/station-straight.svg'
        )

class Sc_Tile(Connection_Tile) :
    def __init__(self):
        super().__init__(
            top = RoadType.HIGHWAY,
            right = RoadType.RAIL,
            down = RoadType.NONE,
            left = RoadType.NONE,
            name = "Sc",
            urlPNG = './graphics/station-curve.png',
            urlSVG = './graphics/station-curve.svg'
        )

class Hr_Tile(Connection_Tile) :
    def __init__(self):
        super().__init__(
            top = RoadType.HIGHWAY,
            right = RoadType.RAIL, 
            down = RoadType.HIGHWAY,
            left = RoadType.RAIL,
            name = "Hr",
            urlPNG = './graphics/overpass.png',
            urlSVG = './graphics/overpass.svg'
        )

# Special tiles
class Special_Tile(Tile):
    """
    A special tile class that extends the base Tile class with usage tracking functionality.
    
    Tracks usage metrics for special tiles across different scopes:
    - Usage per round
    - Usage per game
    - Usage per tile
    
    Provides getter and setter methods to manage and retrieve usage statistics.
    """
    def __init__(self, top, right, down, left, name, urlPNG, urlSVG):
        super().__init__(top, right, down, left, name, urlPNG, urlSVG)
        self.__usagePerRound = 0
        self.__usagePerGame = 0
        self.__usagePerTile = 0

    # Usage Per Round
    def getUsagePerRound(self) :
        return self.__usagePerRound
    
    def modifyUsagePerRound(self, modifiedValue) :
        self.__usagePerRound = modifiedValue

    # Usage Per Game
    def getUsagePerGame(self) :
        return self.__usagePerGame
    
    def modifyUsagePerGame(self, modifiedValue) :
        self.__usagePerGame = modifiedValue
    
    # Usage Per Tile
    def getUsagePerTile(self) :
        return self.__usagePerTile
    
    def modifyUsagePerTile(self, modifiedValue) :
        self.__usagePerTile = modifiedValue
        

class HH_Tile(Special_Tile) :
    def __init__(self):
        super().__init__(
            top = RoadType.HIGHWAY,
            right = RoadType.HIGHWAY,
            down = RoadType.HIGHWAY,
            left = RoadType.HIGHWAY,
            name = "HH",
            urlPNG = './graphics/highway-cross.png',
            urlSVG = './graphics/highway-cross.svg'
        )

class SH_Tile(Special_Tile) :
    def __init__(self):
        super().__init__(
            top = RoadType.HIGHWAY,
            right = RoadType.HIGHWAY,
            down = RoadType.HIGHWAY,
            left = RoadType.RAIL,
            name = "SH",
            urlPNG = './graphics/station-highway-junction.png',
            urlSVG = './graphics/station-highway-junction.svg'
        )

class SHR_Tile(Special_Tile) :
    def __init__(self):
        super().__init__(
            top = RoadType.HIGHWAY,
            right = RoadType.RAIL,
            down = RoadType.HIGHWAY,
            left = RoadType.RAIL,
            name = "SHR",
            urlPNG = './graphics/station-cross.png',
            urlSVG = './graphics/station-cross.svg'
        )

class RR_Tile(Special_Tile) :
    def __init__(self):
        super().__init__(
            top = RoadType.RAIL,
            right = RoadType.RAIL,
            down = RoadType.RAIL,
            left = RoadType.RAIL,
            name = "RR",
            urlPNG = './graphics/railway-cross.png',
            urlSVG = './graphics/railway-cross.svg'
        )

class SR_Tile(Special_Tile) :
    def __init__(self):
        super().__init__(
            top = RoadType.RAIL,
            right = RoadType.RAIL,
            down = RoadType.RAIL,
            left = RoadType.HIGHWAY,
            name = "SR", 
            urlPNG = './graphics/station-railway-junction.png',
            urlSVG = './graphics/station-railway-junction.svg'
        )

class SS_Tile(Special_Tile) :
    def __init__(self):
        super().__init__(
            top = RoadType.HIGHWAY,
            right = RoadType.HIGHWAY,
            down = RoadType.RAIL,
            left = RoadType.RAIL,
            name = "SS",
            urlPNG = './graphics/station-reflect.png',
            urlSVG = './graphics/station-reflect.svg'
        )




def main():
    tile = H_Tile()
    print(tile.getTop())
    tile.rightRotate()
    print(tile.getTop())
    print(Tile.getSubClasses())
    sbcls = Highway_Tile.getSubClasses()
    print(sbcls)
    sbcls2 = Rail_Tile.getSubClasses()
    print(sbcls2)
    print(sbcls + sbcls2)
    print(len(sbcls + sbcls2))
    chosenClass = random.choice(sbcls+ sbcls2)
    chosenTile = chosenClass()
    print(chosenTile)
    print(chosenTile.getName())

    tile1 = Highway_Tile.createTile("H")
    print(tile1.getName())
    print(tile1.getRight())
    print(tile1.getTop())
    print(tile1.getURLPNG())
    print(tile1.getRound())
    tile1.increaseRoundBy1()
    tile1.increaseRoundBy1()
    print(tile1.getRound())







if __name__ == "__main__":
    main()


