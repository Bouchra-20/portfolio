from PyQt5.QtGui import QPainter
from PyQt5.QtWidgets import QWidget
from typing import List

from TP1.Target import Target
from TP1.NormalCursor import NormalCursor
import random
import time


class NormalWidget(QWidget):

    def __init__(self, targets_path: List[Target]):
        """
        Initialise le widget, charge les cibles et configure le curseur.

        :param targets_path: Chemin vers le fichier CSV contenant (x, y, size). 
        """
        super().__init__()
        self.targets = []
        self.load_targets(targets_path) 

        self.curseur = NormalCursor(self.targets)

        self.start_time = None
        self.current_target = None
        self.pick_new_target()

        self.setMouseTracking(True)


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

    def mouseMoveEvent(self, event):
        """
        Capture le mouvement pour mettre à jour l'état 'highlighted' des cibles.
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
        Gère la validation de la sélection.
        
        Si la cible cliquée est la 'current_target', on calcule le temps de sélection
        et on passe à la suivante.
        """
        if self.curseur.closest == self.current_target:
            end_time = time.time()
            duration_ms = int((end_time - self.start_time) * 1000)

            print(f"Temps de sélection : {duration_ms} ms")

            self.pick_new_target()
            self.update()

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

