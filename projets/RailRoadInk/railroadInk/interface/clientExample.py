from __future__ import division
import sys

import os

# Add the parent directory to the Python path
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


import pygame
import websocket
import time
from modules.graphicsd import *
from modules.orientationEnum import *
from rules.points import *
from rules.tile import *

class ClientExample(object):
    TILE_SIZE = (85, 85)
    GAME_RANGE = 7
    DEFAULT_IMAGE_SIZE = 700, 700
    PRESS_DELAY = 300
    WIDTH = 1000
    HEIGHT = 700
    TILE_MARGIN = 47

    def __init__(self, id, ws_url):
        # Pygame Part
        pygame.mixer.init()
        pygame.mixer.pre_init(44100, -16, 2, 2048)
        pygame.init()

        self.screen = pygame.display.set_mode((self.WIDTH, self.HEIGHT))
        self.bg_img = pygame.image.load(graphics_png['grid'])
        self.bg_img = pygame.transform.scale(self.bg_img, (self.DEFAULT_IMAGE_SIZE))

        #self.menu = pygame.display.set_mode((self.WIDTH, self.HEIGHT))
        color = (255, 255, 255)

        bar_width = 200
        bar_height = 600
        margin = 30
        bar_x_position = self.WIDTH - bar_width - margin
        bar_y_position = 50

        pygame.draw.rect(self.screen, color, pygame.Rect(bar_x_position, bar_y_position, bar_width, bar_height))
        #pygame.draw.rect(self.screen, color, pygame.Rect(self.width - 10 , 30, 70, self.height - 30))

        self.FPS = 30
        self.REFRESH = pygame.USEREVENT+1
        pygame.time.set_timer(self.REFRESH, 1000//self.FPS)

        # Reflector Part
        self.id = id
        self.ws = websocket.WebSocket()
        self.ws.settimeout(2)
        self.ws.connect(ws_url)

        self.log = []

        self.grid = [[None for i in range(self.GAME_RANGE)] for j in range(self.GAME_RANGE)]


    # def doAction(self):
    #     keys = pygame.key.get_pressed()
    #     if keys[pygame.K_o]:
    #         pygame.time.delay(self.PRESS_DELAY)
    #         self.ws.send(f"{self.id} ENTERS")
    #     if keys[pygame.K_l]:
    #         pygame.time.delay(self.PRESS_DELAY)
    #         self.ws.send(f"{self.id} LEAVES")
    #     if keys[pygame.K_t]:
    #         pygame.time.delay(self.PRESS_DELAY)
    #         self.ws.send(f"{self.id} THROWS")
    #     if keys[pygame.K_p]:
    #         pygame.time.delay(self.PRESS_DELAY)
    #         self.ws.send(f"{self.id} PLACES")
    #     if keys[pygame.K_y]:
    #         pygame.time.delay(self.PRESS_DELAY)
    #         self.ws.send(f"{self.id} YIELDS")
    #     if keys[pygame.K_s]:
    #         pygame.time.delay(self.PRESS_DELAY)
    #         self.ws.send(f"{self.id} SCORES")
    #     if keys[pygame.K_b]:
    #         pygame.time.delay(self.PRESS_DELAY)
    #         self.ws.send(f"{self.id} BLAMES")
    #     if keys[pygame.K_g]:
    #         pygame.time.delay(self.PRESS_DELAY)
    #         self.ws.send(f"{self.id} GRANTS")
    #     if keys[pygame.K_a]:
    #         pygame.time.delay(self.PRESS_DELAY)
    #         self.ws.send(f"{self.id} AGREES")
    #     if keys[pygame.K_e]:
    #         pygame.time.delay(self.PRESS_DELAY)
    #         self.ws.send(f"{self.id} ELECTS")

    #     if keys[pygame.K_q]:
    #         pygame.time.delay(self.PRESS_DELAY)
    #         self.ws.close()

    def record(self):
        try:
            i = 0
            #current_message = self.ws.recv()
            while i<5:
                try:
                    if not self.ws.connected:
                        print("La connexion WebSocket est fermée.")
                        break

                    current_message = self.ws.recv()
                    self.log.append(current_message)
                    #current_message = self.ws.recv()
                    i+=1
                    print(self.log)
                except websocket.WebSocketTimeoutException:
                    print("Aucun message reçu dans le délai imparti.")
                    break
                except Exception as e:
                    print(f"Erreur inattendue lors de la réception : {e}")
                    break
                time.sleep(0.01)
        except KeyboardInterrupt:
            print("Interruption par l'utilisateur.")
            self.ws.close()
            pygame.quit()
            sys.exit()

    def run(self):
        running = True
        iteration = 0
        while running:
            try:
                event = pygame.event.poll()
                #self.doAction()
                #self.record()

                if iteration % 10 == 0:
                    self.record()

                if event.type == pygame.QUIT:
                    running = False
                    self.ws.close() 
                    print("Connexion WebSocket fermée.")
                    pygame.quit()
                    sys.exit()

                elif event.type == self.REFRESH:
                    self.draw()

                #else:
                #    pass 
                time.sleep(0.01)
                iteration += 1
            except KeyboardInterrupt:
                print("Interruption par l'utilisateur.")
                running = False
                self.ws.close()
                pygame.quit()
                sys.exit() 
            except Exception as e:
                print(f"Erreur : {e}")
                running = False       

    def coordinates(self, x, y):
        X="ABCDEFG"
        return X[x]+str(y)

    def isAuthorised(self, path, x, y, orientation):
        return Rules.checkAction(path, x, y, orientation)

    def addTile(self, name, x, y, orientation=Orientation.STRAIGHT):
        tile = Tile.createTile(name)
        if self.isAuthorised(tile, x, y, orientation):
            self.grid[x][y] = pygame.image.load(graphics_png[tile.getName()])
            if orientation == Orientation.STRAIGHT:
                self.grid[x][y] = pygame.transform.rotate(self.grid[x][y], 0)
                self.grid[x][y] = pygame.transform.scale(self.grid[x][y], self.TILE_SIZE)
                self.ws.send(f"{self.id} PLACES {tile.getName()} straight {self.coordinates(x, y)}")
                print('STRAIGHT')
            elif orientation == Orientation.LEFT:
                self.grid[x][y] = pygame.transform.rotate(self.grid[x][y], 90)
                self.grid[x][y] = pygame.transform.scale(self.grid[x][y], self.TILE_SIZE)
                self.ws.send(f"{self.id} PLACES {tile.getName()}  left {self.coordinates(x, y)}")
                print('LEFT')
            elif orientation == Orientation.RIGHT:
                self.grid[x][y] = pygame.transform.rotate(self.grid[x][y], 270)
                self.grid[x][y] = pygame.transform.scale(self.grid[x][y], self.TILE_SIZE)
                self.ws.send(f"{self.id} PLACES {tile.getName()}  right {self.coordinates(x, y)}")
                print('RIGHT')
            elif orientation == Orientation.UPSIDE:
                self.grid[x][y] = pygame.transform.rotate(self.grid[x][y], 180)
                self.grid[x][y] = pygame.transform.scale(self.grid[x][y], self.TILE_SIZE)
                self.ws.send(f"{self.id} PLACES {tile.getName()}  upside {self.coordinates(x, y)}")
                print('UPSIDE')
            self.screen.blit(
                self.grid[x][y],
                ((x * self.TILE_SIZE[0])+self.TILE_MARGIN+(x*1.5), (y * self.TILE_SIZE[1])+self.TILE_MARGIN+(y*1.5))
            )

    # def addTileWithRotation(self, image, x, y, angle):
    #     self.grid[x][y] = image
    #     self.grid[x][y] = pygame.transform.rotate(self.grid[x][y], angle)
    #     self.screen.blit(
    #         self.grid[x][y],
    #         ((x * self.TILE_SIZE[0])+self.TILE_MARGIN+(x*1.5), (y * self.TILE_SIZE[1])+self.TILE_MARGIN+(y*1.5))
    #     )

    # def rotateTile(self, x, y, orientation):
    #     if self.grid[x][y] == None:
    #         print('Pas de tuile à l\'emplacement donné')
    #     elif orientation == Orientation.STRAIGHT:
    #         self.addTileWithRotation(self.grid[x][y], x, y, 0)
    #         print('STRAIGHT')
    #     elif orientation == Orientation.LEFT:
    #         self.addTileWithRotation(self.grid[x][y], x, y, 90)
    #         print('LEFT')
    #     elif orientation == Orientation.RIGHT:
    #         self.addTileWithRotation(self.grid[x][y], x, y, 270)
    #         print('RIGHT')
    #     elif orientation == Orientation.UPSIDE:
    #         self.addTileWithRotation(self.grid[x][y], x, y, 180)
    #         print('UPSIDE')


    def draw(self):
        self.screen.blit(self.bg_img, self.bg_img.get_rect())
        self.addTile(graphics_png['SS'], 3, 4, Orientation.FLIPPED)
        pygame.display.flip()

client = ClientExample("toto", "ws://localhost:3000")
client.run()
pygame.quit()
sys.exit()