from typing import List
from PyQt5.QtGui import QPainter, QColor
from math import sqrt    

from TP1.Target import Target

class BubbleCursor:
    """
    Gère la logique du Bubble Cursor : calcule la cible la plus proche
    et adapte dynamiquement son rayon pour atteindre cette cible.
    """

    # Couleur semi-transparente pour voir les cibles à travers la bulle
    defaultCol = QColor(200, 200, 255)

    def __init__(self, targets: List[Target]):
        """
        Initialise le Bubble Cursor.
        
        :param targets: Référence vers la liste des cibles présentes dans le widget.
        """
        self.size: int = 10
        self.x: int = 0
        self.y: int = 0
        self.targets: List[Target] = targets
        self.closest = None
    
    def paint(self, painter: QPainter):
        """
        Dessine la bulle du curseur. 
        Note: Le diamètre est size * 2 pour que le rayon corresponde à la distance calculée.
        """
        painter.setBrush(self.defaultCol)
        painter.drawEllipse(self.x - self.size, self.y - self.size, self.size * 2, self.size * 2)

    def move(self, x: int, y: int):
        """
        Met à jour la position du curseur et déclenche le recalcul de la cible la plus proche.
        
        :param x: Nouvelle coordonnée X de la souris.
        :param y: Nouvelle coordonnée Y de la souris.
        """
        self.x = x
        self.y = y
        self.update_closest_target()


    def update_closest_target(self):
        """
        Algorithme de détection : parcourt toutes les cibles pour trouver celle 
        dont le bord est le plus proche de la pointe du curseur.
        Met à jour le rayon de la bulle pour qu'il touche précisément ce bord.
        """
        min_distance = float('inf')
        closest_target = None

        for target in self.targets:
            target.highlighted = False

            distance = abs(sqrt((target.x - self.x)**2 + (target.y - self.y)**2) - target.size/2)
            if distance < min_distance:
                min_distance = distance
                closest_target = target

        self.closest = closest_target
        if self.closest:
            self.closest.selected = True
            self.size = max(5, int(min_distance))