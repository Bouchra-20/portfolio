import sys
from PyQt5.QtWidgets import QApplication, QMainWindow

from TP1.BubbleWidget import BubbleWidget
from TP1.NormalWidget import NormalWidget
from TP1.RopeWidget import RopeWidget
	
def main():
	"""
    Point d'entrée principal de l'application.
    
    Initialise l'environnement Qt, configure la fenêtre principale et
    permet de basculer entre les différentes techniques d'interaction
    (Normal, Bubble, Rope).
    """
	app = QApplication(sys.argv)
	window = QMainWindow()
	window.resize(1024, 800)
	window.setWindowTitle("Évaluation des techniques de pointage")

	bubble = BubbleWidget()

	normal = NormalWidget(bubble.targets)

	rope = RopeWidget()

	window.setCentralWidget(normal)

	window.show()

	sys.exit(app.exec_())


if __name__ == "__main__":
	main()

