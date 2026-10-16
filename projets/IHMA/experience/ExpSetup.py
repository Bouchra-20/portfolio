import os
import sys

from PyQt5.QtGui import QIntValidator
from PyQt5.QtWidgets import QDialog, QLabel, QSpinBox, QComboBox, QGridLayout, QPushButton, QHBoxLayout, QVBoxLayout

class ExpSetup(QDialog):
    """
    Boîte de dialogue permettant de configurer les paramètres d'une expérience IHM.
    
    Elle permet de définir l'identifiant utilisateur, la technique testée, 
    la densité des cibles, leur taille et le nombre de répétitions.
    """

    def __init__(self):
        """
        Initialise l'interface de configuration avec des layouts imbriqués.
        """
        super().__init__()
        self.setWindowTitle("Configuration de l'expérience")

        # Numéro d'utilisateur (Numérique)
        self.userBox = QSpinBox()
        self.userBox.setMinimum(1)

        # Choix de la technique (Liste déroulante)
        self.techniqueBox = QComboBox()
        self.techniqueBox.addItems(["Normal", "Bubble", "Rope"])

        # Densité (Nombre de cibles : 30, 60 ou  90)
        self.densityBox = QComboBox()
        self.densityBox.addItems(["30", "60", "90"])

        # Taille des cibles en pixels (6, 12 ou 18)
        self.sizeBox = QComboBox()
        self.sizeBox.addItems(["6", "12", "18"])

        # Nombre de répétitions (Minimum 2 pour l'analyse statistique)
        self.repeatBox = QSpinBox()
        self.repeatBox.setMinimum(2)
        self.repeatBox.setValue(3)

        # Bouton de validation
        self.validateBtn = QPushButton("Lancer l'expérience")

        self.validateBtn.clicked.connect(self.accept)

        # Layout principal
        layout = QVBoxLayout()

        # Ajout des lignes de saisie via la méthode utilitaire _row
        layout.addLayout(self._row("Utilisateur", self.userBox))
        layout.addLayout(self._row("Technique", self.techniqueBox))
        layout.addLayout(self._row("Densité", self.densityBox))
        layout.addLayout(self._row("Taille cible", self.sizeBox))
        layout.addLayout(self._row("Répétitions", self.repeatBox))

        layout.addWidget(self.validateBtn)

        self.setLayout(layout)


    def _row(self, label, widget):
        """
        Crée une ligne horizontale (QHBoxLayout) contenant un label et un widget.
        
        :param label_text: Texte à afficher à gauche.
        :param widget: Le composant interactif (SpinBox, ComboBox, etc.) à droite.
        :return: Un objet QHBoxLayout prêt à être ajouté au layout principal.
        """
        row = QHBoxLayout()
        row.addWidget(QLabel(label))
        row.addWidget(widget)
        return row

    def get_values(self):
        """
        Récupère l'ensemble des données saisies dans l'interface.
        
        :return: Un dictionnaire contenant les paramètres de l'expérience.
        """
        return {
            "user": self.userBox.value(),
            "technique": self.techniqueBox.currentText(),
            "density": int(self.densityBox.currentText()),
            "size": int(self.sizeBox.currentText()),
            "repetitions": self.repeatBox.value()
        }
        
    def get_user_id(self):
        """Retourne le numéro d'utilisateur."""
        return self.userBox.value()
    
    def get_technique(self):
        """Retourne la technique sélectionnée."""
        return self.techniqueBox.currentText()
    
    def get_densities(self):
        """Retourne la liste des densités (toutes les valeurs possibles)."""
        return [30, 60, 90]
    
    def get_sizes(self):
        """Retourne la liste des tailles (toutes les valeurs possibles)."""
        return [6, 12, 18]
    
    def get_nb_repetitions(self):
        """Retourne le nombre de répétitions."""
        return self.repeatBox.value()
    
    def get_current_density(self):
        """Retourne la densité actuellement sélectionnée."""
        return int(self.densityBox.currentText())
    
    def get_current_size(self):
        """Retourne la taille actuellement sélectionnée."""
        return int(self.sizeBox.currentText())

