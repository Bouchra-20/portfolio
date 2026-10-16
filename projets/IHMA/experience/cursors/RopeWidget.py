from TP1.BubbleWidget import BubbleWidget
from TP1.RopeCursor import RopeCursor

class RopeWidget(BubbleWidget):
    """
    Widget spécifique pour l'interaction Rope Cursor.
    Hérite de BubbleWidget pour bénéficier du chargement CSV et du chronomètre.
    """

    def __init__(self, targets_path: str):
        super().__init__(targets_path)
        self.curseur = RopeCursor(self.targets)
        self.setMouseTracking(True)
