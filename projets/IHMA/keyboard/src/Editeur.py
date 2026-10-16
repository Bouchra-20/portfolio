import sys
import math
from PyQt5.QtWidgets import QApplication, QWidget, QVBoxLayout, QLineEdit
from PyQt5.QtGui import QPainter, QColor, QPen, QFont, QPolygonF
from PyQt5.QtCore import Qt, QPointF, QRectF
from KeyboardWidget import KeyboardWidget
from Dictionnaire import Dictionnaire

class Editeur(QWidget):
    """Fenêtre principale héritant de QWidget.
    Layout : QVBoxLayout > QLineEdit + KeyboardWidget.
    """

    def __init__(self, layout_path: str = "keyboard_layout.json"):
        """
        Initialise l'interface, configure le layout, instancie le clavier 
        et charge le dictionnaire de mots.
        
        Args:
            layout_path: Chemin vers le fichier JSON de configuration du clavier.
        """
        super().__init__()
        self.setWindowTitle("Éditeur — Saisie gestuelle")
        self.setStyleSheet("background-color: #ECEFF1;")

        layout = QVBoxLayout(self)
        layout.setContentsMargins(16, 16, 16, 16)
        layout.setSpacing(12)

        self.line_edit = QLineEdit()
        self.line_edit.setPlaceholderText("Le texte saisi apparaîtra ici…")
        self.line_edit.setFont(QFont("Arial", 16))
        self.line_edit.setStyleSheet("""
            QLineEdit {
                border: 2px solid #90CAF9;
                border-radius: 8px;
                padding: 8px 12px;
                background: white;
                color: #212121;
            }
            QLineEdit:focus { border-color: #1565C0; }
        """)

        self.keyboard = KeyboardWidget(self.line_edit, layout_path)

        layout.addWidget(self.line_edit)
        layout.addWidget(self.keyboard)
        layout.addStretch()
        self.adjustSize()

        self.keyboard = KeyboardWidget(self.line_edit, layout_path)
        self.keyboard.newletter.connect(self.on_new_letter)

        self.dictionnaire = Dictionnaire()
        self.dictionnaire.load("mots.txt", self.keyboard, d=20)

        self.keyboard.newword.connect(self.on_new_word)

    def on_new_letter(self, letter: str):
        """
        Slot appelé lors de l'émission du signal 'newletter' (clic simple).
        Insère un caractère unique à la position actuelle du curseur.
        
        Args:
            letter: Le caractère à insérer.
        """       
        cursor_pos = self.line_edit.cursorPosition()
        current_text = self.line_edit.text()

        new_text = current_text[:cursor_pos] + letter + current_text[cursor_pos:]
        self.line_edit.setText(new_text)
        self.line_edit.setCursorPosition(cursor_pos + len(letter))

    def on_new_word(self, word: str):
        """
        Slot appelé lors de l'émission du signal 'newword' (saisie gestuelle).
        Insère un mot complet suivi d'un espace pour faciliter la saisie rapide.
        
        Args:
            word: Le mot validé depuis la barre de suggestions.
        """
        pos = self.line_edit.cursorPosition()
        txt = self.line_edit.text()
        insertion = word + " "
        self.line_edit.setText(txt[:pos] + insertion + txt[pos:])
        self.line_edit.setCursorPosition(pos + len(insertion))

    