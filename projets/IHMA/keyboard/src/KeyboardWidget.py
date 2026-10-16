import sys
import math
import json
from Key import Key
from Reconnaisseur import Reconnaisseur
from PyQt5.QtWidgets import QApplication, QWidget, QVBoxLayout, QLineEdit
from PyQt5.QtGui import QPainter, QColor, QPen, QFont, QPolygonF
from PyQt5.QtCore import Qt, QPointF, QRectF, QRect, QSize, pyqtSignal
from Dictionnaire import Dictionnaire

class KeyboardWidget(QWidget):
    """
    Composant de clavier virtuel supportant la saisie par clic et par geste (SharkKey).
    
    Ce widget gère l'affichage des touches à partir d'un layout JSON, capture les 
    mouvements de la souris pour la reconnaissance de tracés, et affiche une barre 
    de suggestions pour la complétion de mots.
    """

    newletter = pyqtSignal(str)
    newword   = pyqtSignal(str)
    
    def __init__(self, line_edit: QLineEdit,
                 layout_path: str = "keyboard_layout.json",
                 parent=None):
        """
        Initialise le clavier, charge le fichier de configuration JSON et 
        calcule la géométrie de chaque touche.
        
        Args:
            line_edit: Le champ de texte où les caractères seront insérés.
            layout_path: Chemin vers le fichier JSON définissant le clavier.
        """
        super().__init__(parent)
        self.line_edit = line_edit

        with open(layout_path, "r", encoding="utf-8") as f:
            layout = json.load(f)

        self.params: dict = layout["params"]

        self.keys: list[Key] = []
        for k in layout["keys"]:
            key = Key(
                symbol=k["symbol"],
                row=k["row"],
                col=k["col"],
                col_span=k.get("col_span", 1),
            )
            key.compute_rect(self.params)
            self.keys.append(key)


        self._suggestions: list[str] = [] 

        max_x = max(k.rect.right()  for k in self.keys) + self.params["padding_x"]
        max_y = max(k.rect.bottom() for k in self.keys) + self.params["padding_y"]
        self.setMinimumSize(int(max_x), int(max_y))

        self.setMouseTracking(True)

        self._pressed_key = None
        self._hovered_key = None
        self._visited_keys: set[Key] = set()
        self._path: list[tuple[int, int]] = []
        self._resampled: list[tuple[int, int]] = []
        self._reconnaisseur = Reconnaisseur()
        self.nb_reco = self.params.get("nbReco", 3)
        self._dictionnaire = Dictionnaire()


    def paintEvent(self, event):
        """
        Gère le rendu graphique du widget. Dessine successivement le fond, 
        la barre de suggestions, les touches et le tracé utilisateur.
        """
        painter = QPainter(self)
        painter.setRenderHint(QPainter.Antialiasing)
 
        painter.fillRect(self.rect(), QColor("#ECEFF1"))
 
        self._draw_suggestions(painter)
 
        painter.setFont(QFont("Arial", 14, QFont.Bold))
        for key in self.keys:
            self._draw_key(painter, key)
 
        self._draw_path(painter)
        self._draw_resampled(painter)
 
        painter.end()

    def _draw_suggestions(self, painter: QPainter):
        """
        Dessine la zone rectangulaire en haut du clavier dédiée à l'affichage 
        des mots suggérés par le moteur de reconnaissance.
        """
        band_rect = QRect(
            self.params["padding_x"],
            self.params["padding_y"],
            self.width() - 2 * self.params["padding_x"],
            self.params["key_height"]
        )

        painter.setBrush(QColor("#E3F2FD"))
        painter.setPen(QPen(QColor("#90CAF9"), 1.5))
        painter.drawRoundedRect(band_rect, 6, 6)

        painter.setPen(QColor("#1565C0"))
        text = "   |   ".join(self._suggestions) if self._suggestions else "Suggestions…"
        painter.drawText(band_rect, Qt.AlignCenter, text)
        
    def _draw_key(self, painter: QPainter, key: Key):
        """
        Dessine une touche individuelle en appliquant un style visuel différent 
        selon son état (normale, survolée ou visitée par un geste).
        """
        if key is self._hovered_key:
            bg, fg = QColor("#1565C0"), QColor("white")
        elif key in self._visited_keys:
            bg, fg = QColor("#42A5F5"), QColor("white")
        else:
            bg, fg = QColor("#FFFFFF"), QColor("#212121")

        shadow_rect = key.rect.translated(2, 2)
        painter.setPen(Qt.NoPen)
        painter.setBrush(QColor(0, 0, 0, 30))
        painter.drawRoundedRect(shadow_rect, 8, 8)

        painter.setBrush(bg)
        painter.setPen(QPen(QColor("#B0BEC5"), 1.5))
        painter.drawRoundedRect(key.rect, 8, 8)

        painter.setPen(fg)
        painter.drawText(key.rect, Qt.AlignCenter, key.symbol)

    def _draw_path(self, painter: QPainter):
        """
        Dessine le tracé brut (ligne rouge) représentant le mouvement continu 
        de la souris effectué par l'utilisateur.
        """
        if len(self._path) < 2:
            return

        pen = QPen(QColor("#E53935"), 3, Qt.SolidLine, Qt.RoundCap, Qt.RoundJoin)
        painter.setPen(pen)
        painter.setBrush(Qt.NoBrush)

        for i in range(1, len(self._path)):
            x1, y1 = self._path[i - 1]
            x2, y2 = self._path[i]
            painter.drawLine(QPointF(x1, y1), QPointF(x2, y2))

        painter.setBrush(QColor("#E53935"))
        painter.setPen(Qt.NoPen)

    def sizeHint(self) -> QSize:
        """
        Calcule la taille idéale du widget en pixels en fonction du nombre 
        de colonnes, de lignes et des marges définies dans les paramètres.
        """
        padding_x = self.params["padding_x"]
        padding_y = self.params["padding_y"]
        key_width = self.params["key_width"]
        key_height = self.params["key_height"]
        margin_x = self.params["margin_x"]
        margin_y = self.params["margin_y"]
        
        n_cols = max(k.col + k.col_span for k in self.keys)

        largeur = 2 * padding_x + n_cols * key_width + (n_cols - 1) * margin_x
 
        n_rows     = max(k.row for k in self.keys)
        total_rows = n_rows + 1

        hauteur = 2 * padding_y + total_rows * key_height + (total_rows - 1) * margin_y

        return QSize(int(largeur), int(hauteur))

    def minimumSizeHint(self) -> QSize:
        return self.sizeHint()


    def mousePressEvent(self, event):
        """
        Détecte le début d'une interaction. Gère le clic sur une suggestion 
        ou initialise un nouveau tracé si l'utilisateur appuie sur une touche.
        """
        if event.button() != Qt.LeftButton:
            return
        
        band_rect = self._get_band_rect()
        if band_rect.contains(event.pos()):
            self._handle_suggestion_click(event.pos().x())
            return
        
        self._pressed_key = self._key_at(QPointF(event.pos()))
        self._path = [(event.pos().x(), event.pos().y())]  

    def mouseReleaseEvent(self, event):
        """
        Finalise l'interaction. Si le mouvement est court, valide la lettre pressée. 
        Si c'est un geste long, lance la recherche de mots dans le dictionnaire.
        """
        if event.button() != Qt.LeftButton:
            return

        released_key = self._key_at(QPointF(event.pos()))

        if released_key is not None and released_key is self._pressed_key:
            self.newletter.emit(released_key.symbol)
        else:
            if len(self._path) >= 2:
                resampled = self._reconnaisseur.resample(self._path, d=20)
                suggestions = self._dictionnaire.recherche(
                    resampled, self._reconnaisseur,
                    self.nb_reco, self
                )
                self._suggestions = suggestions
                self.update()

        self._path = [] 

        self._pressed_key = None
        self.update()

    def mouseMoveEvent(self, event):
        """
        Enregistre les points du mouvement de la souris et met à jour l'état 
        visuel des touches survolées. Calcule également le rééchantillonnage en temps réel.
        """
        point = QPointF(event.pos())
        hovered = self._key_at(point)

        if hovered is not self._hovered_key:
            self._hovered_key = hovered
            self.update()   
        
        if event.buttons() & Qt.LeftButton:
            self._path.append((event.pos().x(), event.pos().y()))
        
            if len(self._path) >= 2:
                self._resampled = self._reconnaisseur.resample(self._path, d=20)

        self.update()

    def _key_at(self, point: QPointF) -> Key | None:
        """
        Parcourt la liste des touches pour trouver celle qui contient le 
        point (x, y) spécifié. Retourne None si aucune touche n'est trouvée.
        """
        for key in self.keys:
            if key.isOver(point):
                return key
        return None


    def _draw_resampled(self, painter: QPainter):
        """
        Affiche les points de contrôle après rééchantillonnage (points bleus) 
        pour donner un retour visuel sur la normalisation du tracé.
        """
        if not self._resampled:
            return

        painter.setBrush(QColor("#1565C0"))
        painter.setPen(QPen(QColor("white"), 1.5))

        for (x, y) in self._resampled:
            painter.drawEllipse(QPointF(x, y), 5, 5)

        if len(self._resampled) >= 2:
            dist = math.dist(self._resampled[0], self._resampled[1])
            print(f"Distance entre points rééchantillonnés : {dist:.1f}px")


    def wordToStroke(self, word: str, d: int) -> list:
        """
        Convertit un mot textuel en une suite de points géométriques en 
        reliant le centre des touches correspondantes, puis rééchantillonne le tout.
        
        Args:
            word: Le mot à convertir.
            d: La distance d'espacement entre les points pour le rééchantillonnage.
        """
        key_map = {key.symbol.upper(): key for key in self.keys}

        raw_stroke = []
        for letter in word.upper():
            if letter not in key_map:
                return []

            key  = key_map[letter]
            rect = key.rect
            cx = rect.x() + rect.width()  / 2
            cy = rect.y() + rect.height() / 2

            raw_stroke.append((cx, cy))

        if len(raw_stroke) < 2:
            return raw_stroke

        return self._reconnaisseur.resample(raw_stroke, d)

    def _key_at_tuple(self, point: tuple) -> Key | None:
        return self._key_at(QPointF(point[0], point[1]))

    def _get_band_rect(self):
        px = self.params["padding_x"]
        py = self.params["padding_y"]
        kh = self.params["key_height"]
        return QRect(int(px), int(py), int(self.width() - 2*px), int(kh))

    def _handle_suggestion_click(self, click_x: int):
        """
        Détermine quel mot de la barre de suggestions a été cliqué en divisant 
        la largeur de la barre par le nombre de mots affichés.
        """
        if not self._suggestions:
            return
        band_rect = self._get_band_rect()
        slot_width = band_rect.width() // len(self._suggestions)

        for i, word in enumerate(self._suggestions):
            slot_x = band_rect.x() + i * slot_width
            if slot_x <= click_x <= slot_x + slot_width:
                self.newword.emit(word)
                self._suggestions = []
                self.update()
                return