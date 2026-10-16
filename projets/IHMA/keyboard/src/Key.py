import sys
import json
from PyQt5.QtWidgets import QApplication, QWidget, QVBoxLayout, QLineEdit
from PyQt5.QtGui import QPainter, QColor, QPen, QFont
from PyQt5.QtCore import Qt, QPointF, QRect


class Key:
    """Représente une touche du clavier.

    Attributs
    ---------
    symbol   : str   — caractère ou label affiché (ex. "A", "⌫", " ")
    row      : int   — ligne dans le layout (1 = première ligne de lettres)
    col      : int   — colonne de départ dans le layout
    col_span : int   — nombre de colonnes occupées (1 par défaut)
    rect     : QRect — coordonnées et dimensions calculées en pixels
    """

    def __init__(self, symbol: str, row: int, col: int, col_span: int = 1):
        """
        :param label: Le caractère ou symbole affiché sur la touche.
        :param row: L'index de la ligne (commence à 1, la ligne 0 étant les suggestions).
        :param col: L'index de la colonne.
        :param col_span: Nombre de colonnes occupées par la touche (défaut 1).
        """
        self.symbol = symbol
        self.row = row
        self.col = col
        self.col_span = col_span
        self.rect: QRect = QRect()

    def compute_rect(self, params: dict):
        """Calcule et stocke le QRect de la touche à partir des paramètres
        du layout : key_width, key_height, margin_x, margin_y,
        padding_x, padding_y, row_offset.

        La ligne 0 est réservée aux suggestions ; les touches commencent
        à row=1, d'où le décalage vertical de (key_height + margin_y).
        """
        kw  = params["key_width"]
        kh  = params["key_height"]
        mx  = params["margin_x"]
        my  = params["margin_y"]
        px  = params["padding_x"]
        py  = params["padding_y"]

        suggestion_band = kh + my

        x = px + self.col * (kw + mx) 
        y = py + suggestion_band + (self.row - 1) * (kh + my)

        width = self.col_span * kw + (self.col_span - 1) * mx

        self.rect = QRect(int(x), int(y), int(width), int(kh))

    def isOver(self, point: QPointF) -> bool:
        """Renvoie True si le QPointF est dans le rect de la touche."""
        return self.rect.contains(int(point.x()), int(point.y()))