import pygame
import websocket

import sys
import time
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from railroadInk.composants.dice import Dice
from railroadInk.composants.board import *
from railroadInk.composants.tile import *

class Interface :
    """
    Manages the graphical interface for a tile-based board game using Pygame.
    
    Handles game state, tile placement, board rendering, and user interactions. 
    Supports special and bare tiles, round management, and score display.
    
    Attributes:
        TILE_SIZE (tuple): Dimensions of each tile
        GAME_RANGE (int): Size of the game board grid
        WIDTH (int): Total window width
        HEIGHT (int): Total window height
        BAR_MAX (int): Maximum number of bare tiles
        SPECIAL_tile_MAX (int): Maximum number of special tiles
    """
    TILE_SIZE = (85, 85)
    GAME_RANGE = 8
    DEFAULT_IMAGE_SIZE = 700, 700
    PRESS_DELAY = 300
    WIDTH = 1000
    HEIGHT = 700
    TILE_MARGIN = 47
    BAR_WIDTH = 200
    BAR_HEIGHT = 600
    BAR_MARGIN = 30
    BAR_COLOR = (255, 255, 255)
    BAR_Y_POSITION = 50
    HIGHLIGHT_COLOR = (0, 255, 0, 128)
    BAR_MAX = 4 
    SPECIAL_tile_MAX = 6
    Board_IMG = './graphics/grid.png'
    def __init__(self):
        pygame.mixer.init()
        pygame.mixer.pre_init(44100, -16, 2, 2048)
        pygame.init()

        self.board = None

        self.screen = pygame.display.set_mode((self.WIDTH, self.HEIGHT))
        self.bg_img = pygame.image.load(self.Board_IMG)
        self.bg_img = pygame.transform.scale(self.bg_img, (self.DEFAULT_IMAGE_SIZE))

        color = self.BAR_COLOR
        self.bar_x_position = self.WIDTH - self.BAR_WIDTH - self.BAR_MARGIN
        self.bar_y_position = self.BAR_Y_POSITION

        pygame.draw.rect(self.screen, color, pygame.Rect(self.bar_x_position,self.bar_y_position, self.BAR_WIDTH, self.BAR_HEIGHT))

        pygame.display.set_caption("Carcassonne")

        self.end_round_button = pygame.Rect(
            self.bar_x_position + (0 * self.TILE_SIZE[0]),
            self.bar_y_position + (5 * self.TILE_SIZE[0]),
            self.TILE_SIZE[0],
            self.TILE_SIZE[1]
        )


        self.font = pygame.font.Font(None, 20)
        self.current_round = 1

        self.bareTile = [None for x in range(self.BAR_MAX)]
        self.next_bare_index = 0
        self.special_tile = [None for x in range(self.SPECIAL_tile_MAX)]
        self.next_special_tile_index = 0
        self.placed_tiles = []
        self.placed_from_bare = 0

        self.draw_end_round_button()

        self.FPS = 30
        self.REFRESH = pygame.USEREVENT+1
        pygame.time.set_timer(self.REFRESH, 1000//self.FPS)

        self.log = []

        self.screen.blit(self.bg_img, self.bg_img.get_rect())
        pygame.display.flip()


        self.selected_tile = None
        self.selected_tile_type = None
        self.compatible_positions = {}
        self.all_compatible_positions = {}
        self.locked_tiles = set() 
   
        self.add_Special_Tile("HH")
        self.add_Special_Tile("SH")
        self.add_Special_Tile("SHR")
        self.add_Special_Tile("RR")
        self.add_Special_Tile("SR")
        self.add_Special_Tile("SS")

    
    def setBoard(self, board):
        self.board = board

    def getUsedTile(self):
        return self.placed_tiles

    def highlight_compatible_cases(self, road_type):        
        """
        Highlights compatible board positions for a given road type.
    
        Scans the game board to find empty adjacent positions where a tile with the specified
        road type can be legally placed. Visually highlights these compatible positions
        on the game screen and updates internal tracking of compatible positions.
    
        Args:
            road_type (str): The type of road to check for compatibility.
        """
        self.compatible_positions.clear()
        MIN = 0
        MAX = self.GAME_RANGE - 1

        for i in range(-1, self.GAME_RANGE):
            for j in range(-1, self.GAME_RANGE):
                case = self.board.getTile(i, j)

                if case is not None:
                    if case.compatible(road_type):
                        if MIN <= i-1 < MAX and MIN <= j < MAX and self.board.getTile(i-1, j) is None:
                            if case.face_compatible(case.getLeft(), road_type):
                                self.compatible_positions[(i-1, j)] = case
                        if MIN <= j-1 < MAX and MIN <= i < MAX and self.board.getTile(i, j-1) is None:
                            if case.face_compatible(case.getTop(),road_type):
                                self.compatible_positions[(i, j-1)] = case
                        if MIN <= i+1 < MAX and MIN <= j < MAX and self.board.getTile(i+1, j) is None:
                            if case.face_compatible(case.getRight(), road_type):
                                self.compatible_positions[(i+1, j)] = case
                        if MIN <= j+1 < MAX and MIN <= i < MAX and self.board.getTile(i, j+1) is None:
                            if case.face_compatible(case.getDown(), road_type):
                                self.compatible_positions[(i, j+1)] = case
                        
        self.redraw_board()
        for (i, j), adj_case in self.compatible_positions.items():
            pos_x = (i * self.TILE_SIZE[0]) + self.TILE_MARGIN + (i * 1.5)
            pos_y = (j * self.TILE_SIZE[1]) + self.TILE_MARGIN + (j * 1.5)
            rect = pygame.Rect(pos_x, pos_y, self.TILE_SIZE[0], self.TILE_SIZE[1])
            pygame.draw.rect(self.screen, self.HIGHLIGHT_COLOR, rect, 3)
        
        pygame.display.flip()
        self.all_compatible_positions.update(self.compatible_positions)


    def is_click_in_bar(self, mouse_x, mouse_y):
        """
        Determines if a mouse click is within the game's tile selection bar.
    
        Args:
            mouse_x (int): The x-coordinate of the mouse click.
            mouse_y (int): The y-coordinate of the mouse click.
    
        Returns:
            bool: True if the click is within the bar's boundaries, False otherwise.
        """
        return (self.bar_x_position <= mouse_x <= self.bar_x_position + self.BAR_WIDTH and
                self.bar_y_position <= mouse_y <= self.bar_y_position + self.BAR_HEIGHT)

    def get_bar_tile_position(self, mouse_x, mouse_y):
        """
        Calculates the position of a tile within the game's tile selection bar.
    
        Args:
            mouse_x (int): The x-coordinate of the mouse click.
            mouse_y (int): The y-coordinate of the mouse click.
    
        Returns:
            tuple or None: A tuple (tile_x, tile_y) representing the tile's position in the bar,
            or None if the click is outside the bar.
        """
        if not self.is_click_in_bar(mouse_x, mouse_y):
            return None
        
        relative_x = mouse_x - self.bar_x_position
        relative_y = mouse_y - self.bar_y_position
        
        tile_x = int(relative_x / self.TILE_SIZE[0])
        tile_y = int(relative_y / self.TILE_SIZE[1])
        
        return (tile_x, tile_y)

    def get_grid_position(self, mouse_x, mouse_y):
        """
        Converts mouse click coordinates to a grid position within the game board.
    
        Args:
            mouse_x (int): The x-coordinate of the mouse click.
            mouse_y (int): The y-coordinate of the mouse click.
    
        Returns:
            tuple or None: A tuple (grid_x, grid_y) representing the grid position,
            or None if the click is outside the game board's range.
        """
        grid_x = int((mouse_x - self.TILE_MARGIN) / (self.TILE_SIZE[0] + 1.5))
        grid_y = int((mouse_y - self.TILE_MARGIN) / (self.TILE_SIZE[1] + 1.5))
        
        if 0 <= grid_x < self.GAME_RANGE and 0 <= grid_y < self.GAME_RANGE:
            return (grid_x, grid_y)
        return None

    def run(self, ws):
        """
        Main game event loop that handles user interactions, tile selection, placement, and game state management.
    
        Args:
            ws (WebSocket): WebSocket connection for potential network interactions.
    
        Manages game flow by processing mouse events, including:
        - Tile selection from the tile bar
        - Tile placement on the game board
        - Tile rotation
        - Handling end of round
        - Handling game exit conditions
    
        Handles various exceptions and ensures clean game termination.
        """
        running = True
        iteration = 0
        
        while running:
            try:
                event = pygame.event.poll()
  
                if event.type == pygame.MOUSEBUTTONDOWN:
                    mouse_x, mouse_y = pygame.mouse.get_pos()
                    
                    if self.is_click_in_bar(mouse_x, mouse_y):
                        tile_pos = self.get_bar_tile_position(mouse_x, mouse_y)
                        
                        if tile_pos[0] == 0 and tile_pos[1] < self.BAR_MAX:
                            self.selected_tile_type = self.bareTile[tile_pos[1]]
                        elif tile_pos[0] == 1 and tile_pos[1] < self.SPECIAL_tile_MAX:
                            self.selected_tile_type = self.special_tile[tile_pos[1]]
                        if self.selected_tile_type is not None and self.selected_tile_type not in self.placed_tiles and tile_pos:
                            self.selected_tile = tile_pos
                            
                            self.selected_tile_img = self.screen.subsurface(
                                pygame.Rect(
                                    self.bar_x_position + (tile_pos[0] * self.TILE_SIZE[0]),
                                    self.bar_y_position + (tile_pos[1] * self.TILE_SIZE[1]),
                                    self.TILE_SIZE[0],
                                    self.TILE_SIZE[1]
                                )
                            ).copy()

                            if self.selected_tile_type:
                                self.screen.blit(self.bg_img, self.bg_img.get_rect())
                                self.highlight_compatible_cases(self.selected_tile_type)
                        elif self.handle_end_round_button_click((mouse_x, mouse_y)) :
                            running = False

                    elif event.button == 3:  # Clic droit
                        grid_pos = self.get_grid_position(mouse_x, mouse_y)
                        if grid_pos:
                            self.remove_tile(grid_pos[0], grid_pos[1])
                    elif self.get_grid_position(mouse_x, mouse_y) is not None:
                        grid_pos = self.get_grid_position(mouse_x, mouse_y)
                        if self.board.getTile(grid_pos[0], grid_pos[1]) is None:
                            if grid_pos and self.selected_tile and self.selected_tile_type:
                                x, y = grid_pos

                                if (x,y) in self.compatible_positions:
                                    self.rotatForPlace(x,y, self.selected_tile_type)
                                    self.board.setTile(x,y, self.selected_tile_type)
                                    self.placed_from_bare += 1
  
                                    self.placed_tiles.append(self.selected_tile_type)

                                    self.redraw_board()

                                    self.redraw_tile_bar()

                                    self.selected_tile = None
                                    self.selected_tile_type = None
                                    self.selected_tile_img = None
                        else:
                            if self.all_compatible_positions.get(grid_pos) is not None:
                                tile = self.board.getTile(grid_pos[0], grid_pos[1])
                                x, y = grid_pos
                                self.rotate(x, y, tile)
                                self.board.setTile(grid_pos[0], grid_pos[1], tile)
                                
                                self.place_tile(grid_pos[0], grid_pos[1], tile)

                if event.type == pygame.QUIT:
                    running = False
                    ws.close() 
                    print("Connexion WebSocket fermée.")
                    pygame.quit()
                    sys.exit()

                time.sleep(0.01)
                iteration += 1
            except KeyboardInterrupt:
                print("Interruption par l'utilisateur.")
                running = False
                ws.close()
                pygame.quit()
                sys.exit() 
            except Exception as e:
                print(f"Erreur : {e}")
                running = False     

    def addTileBar(self, tile_name):
        """
        Add a tile to the game's tile bar for selection.
    
        Creates a tile object from the given tile name, scales its image, and displays
        it in the tile selection bar if there is space available. Updates the bar's
        tile tracking and visual representation.
    
        Args:
            tile_name (str): The name of the tile to be added to the tile bar.
        """
        tile_obj = Tile.createTile(tile_name)
        tile_img = pygame.image.load(tile_obj.getURLPNG())
        tile = pygame.transform.scale(tile_img, self.TILE_SIZE)

        y = 0
        pos_x = self.bar_x_position + (y * self.TILE_SIZE[0])
        pos_y = self.bar_y_position + (self.next_bare_index * self.TILE_SIZE[1])

        if self.next_bare_index < self.BAR_MAX:
            self.bareTile[self.next_bare_index] = tile_obj
            self.next_bare_index += 1

            self.screen.blit(tile, (pos_x, pos_y))
            pygame.display.flip()

    def add_Special_Tile(self, tile_name):
        """
        Add a special tile to the game's special tile bar for selection.
    
        Creates a special tile object from the given tile name, scales its image, and displays
        it in the special tile selection bar if there is space available. Updates the special
        tile bar's tile tracking and visual representation.
    
        Args:
            tile_name (str): The name of the special tile to be added to the special tile bar.
        """
        tile_obj = Tile.createTile(tile_name)
        tile_img = pygame.image.load(tile_obj.getURLPNG())
        tile = pygame.transform.scale(tile_img, self.TILE_SIZE)

        pos_x = self.bar_x_position + (1 * self.TILE_SIZE[0])
        pos_y = self.bar_y_position + (self.next_special_tile_index * self.TILE_SIZE[1])
        
        if self.next_special_tile_index < self.SPECIAL_tile_MAX:
            self.special_tile[self.next_special_tile_index] = tile_obj
            self.next_special_tile_index += 1

            self.screen.blit(tile, (pos_x, pos_y))
            pygame.display.flip()
    
    def getSpecialTile(self):
        """
        Retrieve the list of special tiles currently in the game's special tile bar.
    
        Returns:
            list: A list of special tile objects currently available in the special tile bar.
        """
        return self.special_tile

    def place_tile(self, x, y, tile):
        """
        Place a tile on the game board at the specified coordinates.
    
        Calculates the pixel position for the tile, loads and scales its image,
        applies any rotation, sets the tile on the board, and updates the screen.
    
        Args:
            x (int): The x-coordinate on the game board where the tile will be placed.
            y (int): The y-coordinate on the game board where the tile will be placed.
            tile (Tile): The tile object to be placed on the board.
        """
        pos_x = (x * self.TILE_SIZE[0]) + self.TILE_MARGIN + (x * 1.5)
        pos_y = (y * self.TILE_SIZE[1]) + self.TILE_MARGIN + (y * 1.5)

        tile_img = pygame.image.load(tile.getURLPNG())
        tile_img = pygame.transform.scale(tile_img, self.TILE_SIZE)

        rotation_angle = tile.getRotation()
        tile_img = pygame.transform.rotate(tile_img, rotation_angle)

      
        self.board.setTile(x,y, tile)
        
        self.screen.blit(tile_img, (pos_x, pos_y))
        pygame.display.flip()


    def redraw_tile_bar(self):
        """
        Redraws the tile bar by rendering both regular and special tiles.
    
        Updates the screen to display available tiles in the game's tile bar, including
        loading tile images, positioning them, and applying a semi-transparent overlay
        for tiles that have already been placed. Also draws the end round button and
        updates the display.
        """
        self.screen.fill(self.BAR_COLOR, (self.bar_x_position, self.bar_y_position, self.BAR_WIDTH, self.BAR_HEIGHT))
        for pos_x in range(self.BAR_MAX):
            tile_way = self.bareTile[pos_x]
            if tile_way is not None :
                t = pygame.image.load(tile_way.getURLPNG())
                tile_img = pygame.transform.scale(t, self.TILE_SIZE)
                
                pos_y = 0
                self.screen.blit(tile_img, (self.bar_x_position + (pos_y * self.TILE_SIZE[0]), 
                                          self.bar_y_position + (pos_x * self.TILE_SIZE[1])))
                
                if tile_way in self.placed_tiles:
                    overlay = pygame.Surface(self.TILE_SIZE, pygame.SRCALPHA)
                    overlay.fill((255, 255, 255, 128))
                    self.screen.blit(overlay, (self.bar_x_position + (pos_y * self.TILE_SIZE[0]), 
                                             self.bar_y_position + (pos_x * self.TILE_SIZE[1])))
        
        for pos_x in range(self.SPECIAL_tile_MAX):
            tile_special = self.special_tile[pos_x]
            if tile_special is not None :
                t = pygame.image.load(tile_special.getURLPNG())
                tile_img = pygame.transform.scale(t, self.TILE_SIZE)

                pos_y = 1
                self.screen.blit(tile_img, (self.bar_x_position + (pos_y * self.TILE_SIZE[0]), 
                                          self.bar_y_position + (pos_x * self.TILE_SIZE[1])))

                if tile_special in self.placed_tiles:
                    overlay = pygame.Surface(self.TILE_SIZE, pygame.SRCALPHA)
                    overlay.fill((255, 255, 255, 128))
                    self.screen.blit(overlay, (self.bar_x_position + (pos_y * self.TILE_SIZE[0]), 
                                             self.bar_y_position + (pos_x * self.TILE_SIZE[1])))

        self.draw_end_round_button()
        
        pygame.display.flip()


    def redraw_board(self):
        """Redessiner le plateau avec toutes les tuiles placées."""
        self.screen.blit(self.bg_img, self.bg_img.get_rect())

        for i in range(self.GAME_RANGE - 1):
            for j in range(self.GAME_RANGE - 1):
                tile = self.board.getTile(i, j)
                if tile is not None:
                    self.place_tile(i, j, tile)

        pygame.display.flip()

    def rotatForPlace(self, x, y, tile):
        """
        Rotate a tile to match the orientation of an adjacent tile on the game board.

        Checks the surrounding board positions and rotates the given tile until its 
        connection points align with the adjacent tile's connection points.

        Args:
            x (int): X-coordinate of the tile's position
            y (int): Y-coordinate of the tile's position
            tile (Tile): The tile to be rotated
        """
        adjacent_case = self.compatible_positions.get((x, y)) 

        if adjacent_case: 
            if self.board.getTile(x+1, y) == adjacent_case :
                if tile.getRight() != adjacent_case.getLeft():

                    while tile.getRight() != adjacent_case.getLeft():
                        tile.rightRotate()
                        tile.setRotation(tile.getRotation() - 90)
            elif self.board.getTile(x-1, y) == adjacent_case :
                if tile.getLeft() != adjacent_case.getRight():

                    while tile.getLeft() != adjacent_case.getRight():
                        tile.rightRotate()
                        tile.setRotation(tile.getRotation() - 90)
            elif self.board.getTile(x, y+1) == adjacent_case :
                if tile.getDown() != adjacent_case.getTop():
                    while tile.getDown() != adjacent_case.getTop():
                        tile.rightRotate()
                        tile.setRotation(tile.getRotation() - 90)
            elif self.board.getTile(x, y-1) == adjacent_case :
                if tile.getTop() != adjacent_case.getDown():
                    while tile.getTop() != adjacent_case.getDown():
                        tile.rightRotate()
                        tile.setRotation(tile.getRotation() - 90)

    def rotate(self, x, y, tile):
        """
        Rotate a tile to align with an adjacent tile based on its position.
    
        Automatically rotates the given tile to match connection points with 
        an adjacent tile in the game board. Checks tiles in all four cardinal 
        directions and adjusts the tile's rotation accordingly.
    
        Args:
            x (int): X-coordinate of the tile's position
            y (int): Y-coordinate of the tile's position
            tile (Tile): The tile to be rotated
        """
        adjacent_case = self.all_compatible_positions.get((x, y)) 

        if adjacent_case: 
            if self.board.getTile(x+1, y) == adjacent_case :
                rotation = True
                while rotation:
                    tile.rightRotate()
                    tile.setRotation(tile.getRotation() - 90)
                    rotation = tile.getRight() != adjacent_case.getLeft()
            elif self.board.getTile(x-1, y) == adjacent_case :
                rotation = True
                while rotation:
                    tile.rightRotate()
                    tile.setRotation(tile.getRotation() - 90)
                    rotation = tile.getLeft() != adjacent_case.getRight()
            elif self.board.getTile(x, y+1) == adjacent_case :
                rotation = True
                while rotation:
                    tile.rightRotate()
                    tile.setRotation(tile.getRotation() - 90)
                    rotation = tile.getDown() != adjacent_case.getTop()
            elif self.board.getTile(x, y-1) == adjacent_case :
                rotation = True
                while rotation:
                    tile.rightRotate()
                    tile.setRotation(tile.getRotation() - 90)
                    rotation = tile.getTop() != adjacent_case.getDown()


    def remove_tile(self, x, y):
        """Supprime une tuile du plateau à la position (x, y)"""
        removed_tile = self.board.getTile(x, y)
        if removed_tile is not None:
            if (x, y) in self.locked_tiles:
                return
            
            self.board.setTile(x, y, None)
            self.placed_tiles.remove(removed_tile)
            self.placed_from_bare -= 1
            self.redraw_board()
            self.draw_end_round_button()
            self.redraw_tile_bar()
            
            
    def draw_end_round_button(self):
        """
        Draws the 'End Round' button on the game screen.
    
        Renders the button with different colors based on whether 4 tiles have been placed.
        Displays the button text and current round number with appropriate color and positioning.
        """
        button_surface = pygame.Surface((self.end_round_button.width, self.end_round_button.height), pygame.SRCALPHA)
        if self.placed_from_bare == 4:
            button_color = (255, 255, 255)
            text_color = (0, 0, 0)
        else:
            button_color = (255, 255, 255)
            text_color = (128, 128, 128)
        button_surface.fill(button_color)
        
        self.screen.blit(button_surface, self.end_round_button)
        
        pygame.draw.rect(self.screen, (0, 0, 0), self.end_round_button, 2)
        
        text = self.font.render(f"End round", True, text_color)
        text_rect = text.get_rect(center=self.end_round_button.center)
        self.screen.blit(text, text_rect)
        
        tiles_text = self.font.render(f"#{self.current_round}", True, text_color)
        tiles_rect = tiles_text.get_rect(center=(self.end_round_button.centerx, self.end_round_button.bottom - 20))
        self.screen.blit(tiles_text, tiles_rect)

    def handle_end_round_button_click(self, pos):
        """
        Handles the end of a round when the 'End Round' button is clicked.
    
        @param pos: The mouse click position
        @return: True if the end round action is successful, False otherwise
        """
        if self.end_round_button.collidepoint(pos) and self.placed_from_bare == 4:
            for x in range(self.GAME_RANGE):
                for y in range(self.GAME_RANGE):
                    tile = self.board.getTile(x, y)
                    if tile is not None:
                        self.locked_tiles.add((x, y))
                        
            self.current_round += 1
            self.bareTile = [None for x in range(self.BAR_MAX)]
            self.next_bare_index = 0

            self.selected_tile = None
            self.selected_tile_type = None
            self.selected_tile_img = None
            self.placed_from_bare = 0
            self.compatible_positions.clear()
            self.all_compatible_positions.clear()

            self.draw_end_round_button()

            return True
        return False
            

    def display_score(self, score):
        """
        Affiche les scores des joueurs à la fin de la partie
        @param scores : dictionnaire avec les joueurs et leurs scores {player: score}
        """
        # Créer une surface semi-transparente pour le fond
        overlay = pygame.Surface((500, 300), pygame.SRCALPHA)
        overlay.fill((0, 0, 0, 180))
        
        # Positionner au centre de l'écran
        overlay_x = (self.WIDTH - 800) // 2
        overlay_y = (self.HEIGHT - 300) // 2
        
        # Afficher le fond
        self.screen.blit(overlay, (overlay_x, overlay_y))
        
        # Créer une police plus grande pour le titre
        title_font = pygame.font.Font(None, 48)
        text_font = pygame.font.Font(None, 36)
        
        # Afficher le titre
        title = title_font.render("Scores", True, (255, 255, 255))
        title_rect = title.get_rect(centerx=self.WIDTH//2 - 190, top=overlay_y + 20)
        self.screen.blit(title, title_rect)
        
        # Afficher les scores
        y_offset = overlay_y + 150
        player_text = text_font.render(f" {score} points", True, (255, 255, 255))
        text_rect = player_text.get_rect(centerx=self.WIDTH//2 - 190, top=y_offset)
        self.screen.blit(player_text, text_rect)
        y_offset += 40
        
        # Afficher un message pour continuer
        continue_text = text_font.render("Cliquez pour quitter", True, (200, 200, 200))
        continue_rect = continue_text.get_rect(centerx=self.WIDTH//2 - 190, bottom=overlay_y + 280)
        self.screen.blit(continue_text, continue_rect)
        
        pygame.display.flip()
        
        # Attendre un clic
        waiting = True
        while waiting:
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    pygame.quit()
                    sys.exit()
                elif event.type == pygame.MOUSEBUTTONDOWN:
                    waiting = False
