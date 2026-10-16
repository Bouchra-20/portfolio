from PyQt5.QtGui import QColor

class Target:
    """
    Représente une cible circulaire dans l'environnement du Bubble Cursor.
    
    Cette classe gère l'état d'affichage (couleur) et le dessin de la cible
    en fonction de son statut (sélectionnée, survolée, ou cible à atteindre).
    """
    # Couleurs de classe (statiques)
    defaultCol = QColor(255, 0, 0)    # Rouge : état normal
    highlightCol = QColor(0, 0, 0)    # Noir : survolée par le Bubble Cursor
    toSelectCol = QColor(0, 255, 0)   # Vert : la cible que l'utilisateur doit cliquer
    selectedCol = QColor(0, 0, 255)  # Bleu : cible déjà validée

    def __init__(self, x, y, size):
        """
        Initialise une nouvelle cible.

        :param x: Coordonnée X du centre de la cible.
        :param y: Coordonnée Y du centre de la cible.
        :param size: Diamètre du disque en pixels.
        """
        self.x: int = x
        self.y: int = y
        self.size: int = size
        self.selected: bool = False
        self.highlighted: bool = False
        self.toSelect: bool = False

    def paint(self, painter):
        """
        Dessine la cible sur le widget en utilisant l'objet QPainter fourni.
        
        La couleur du pinceau (Brush) change dynamiquement selon l'état de la cible.
        Le dessin utilise (x, y) comme centre du disque.
        
        :param painter: L'instance de QPainter active (généralement dans paintEvent).
        """
        if self.selected:
            painter.setBrush(self.selectedCol)
        elif self.toSelect:
            painter.setBrush(self.toSelectCol)
        elif self.highlighted:
            painter.setBrush(self.highlightCol)
        else:
            painter.setBrush(self.defaultCol)

        painter.drawEllipse(self.x - self.size // 2, self.y - self.size // 2, self.size, self.size)

