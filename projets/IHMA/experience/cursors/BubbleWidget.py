from PyQt5.QtGui import QPainter
from PyQt5.QtWidgets import QWidget
import os
from typing import List

from TP1.Target import Target
from TP1.BubbleCursor import BubbleCursor
import random
import time


class BubbleWidget(QWidget):

    """
    Widget Bubble gérant l'affichage des cibles et l'interaction Bubble Cursor.
    
    Ce widget lit les données depuis un fichier CSV, stocke les objets Target,
    et coordonne le rafraîchissement visuel lors du déplacement de la souris.
    """ 
    def __init__(self, targets_path: str = None):
        """
        Initialise le widget, charge les cibles et configure le curseur.

        :param targets_path: Chemin vers le fichier CSV contenant (x, y, size). 
        """
        super().__init__()
        if targets_path is None:
            targets_path = os.path.join(os.path.dirname(__file__), "src_tp_bubble.csv")
        self.targets: List[Target] = []
        self.load_targets(targets_path)

        self.curseur = BubbleCursor(self.targets)

        self.start_time = None
        self.current_target = None
        self.pick_new_target()
        self.setMouseTracking(True)


    def load_targets(self, targets_path: str):
        """
        Lit le fichier CSV et remplit la liste des cibles.
        
        Chaque ligne du CSV doit être au format: x, y, width.
        Utilise map() pour nettoyer les espaces et convertir les chaînes en entiers.
        """
        with open(targets_path, 'r') as file:
            lines = file.readlines()
            for line in lines:
                line = line.strip()
                if not line:
                    continue
                parts = line.split(',')
                if len(parts) < 3:
                    continue
                try:
                    x, y, width = map(int, map(str.strip, parts[:3]))
                except ValueError:
                    continue
                target = Target(x, y, width)
                self.targets.append(target)


    def paintEvent(self, event):
        """
        Méthode de rendu déclenchée par Qt pour rafraîchir l'affichage.
        
        L'ordre de dessin définit la superposition :
        1. On dessine les cibles (fond).
        2. On dessine le curseur (premier plan) pour qu'il soit toujours visible.
        
        :param event: L'événement de peinture envoyé par le système.
        """
        painter = QPainter(self)
        for target in self.targets: 
            target.paint(painter)
        self.curseur.paint(painter)

    def mouseMoveEvent(self, event):
        """
        Gère le mouvement de la souris.
        
        Met à jour la position du curseur et demande au widget de se rafraîchir.
        
        :param event: L'événement de mouvement de la souris.
        """
        self.curseur.move(event.x(), event.y())
        self.update()


    def pick_new_target(self):
        """
        Sélectionne une nouvelle cible aléatoire et met à jour le chronomètre.
        """
        if self.current_target:
            self.current_target.toSelect = False

        self.current_target = random.choice(self.targets)
        self.current_target.toSelect = True
        self.start_time = time.time()


    def mousePressEvent(self, event):
        """
        Gère le clic de souris.
        
        Vérifie si la cible la plus proche est bien la cible visée.
        Si oui, calcule le temps de sélection et en choisit une nouvelle.
        
        :param event: L'événement de clic de souris.
        """
        if self.curseur.closest == self.current_target:
            end_time = time.time()
            duration_ms = int((end_time - self.start_time) * 1000)

            print(f"Temps de sélection : {duration_ms} ms")

            self.pick_new_target()
            self.update()
