from PyQt5.QtGui import QColor, QPainter, QPen
from TP1.BubbleCursor import BubbleCursor

class RopeCursor(BubbleCursor):
    
    """
    Variante du Bubble Cursor utilisant un retour visuel par ligne (Rope).
    
    Au lieu d'une bulle, ce curseur trace un segment entre la pointe de 
    la souris et le centre de la cible la plus proche.
    """
    # Couleur du segment (Noir par défaut)
    ropeCol = QColor(0, 0, 0)

    def __init__(self, targets):
        """
        Initialise le RopeCursor en réutilisant la logique du BubbleCursor.
        
        :param targets: Liste des cibles pour le calcul de proximité.
        """
        super().__init__(targets)


    def paint(self, painter: QPainter):
        """
        Dessine un segment (ligne) reliant le curseur à la cible 'closest'.
        
        Si aucune cible n'est identifiée comme proche (ex: liste vide), 
        la méthode ne dessine rien.
        
        :param painter: L'objet QPainter utilisé pour le rendu.
        """
        if self.closest is None:
            return

        pen = QPen(self.ropeCol)
        pen.setWidth(2)
        painter.setPen(pen)

        painter.drawLine(
            self.x,
            self.y,
            self.closest.x,
            self.closest.y
        )
        painter.setPen(QPen())
