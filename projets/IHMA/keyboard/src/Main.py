import sys
import os
import math
from PyQt5.QtWidgets import QApplication, QWidget, QVBoxLayout, QLineEdit
from PyQt5.QtGui import QPainter, QColor, QPen, QFont, QPolygonF
from PyQt5.QtCore import Qt, QPointF, QRectF
from Editeur import Editeur


if __name__ == "__main__":
    app = QApplication(sys.argv)
    app.setStyle("Fusion")
    
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(script_dir)
    layout_path = os.path.join(project_root, "json", "keyboard_layout.json")
    
    window = Editeur(layout_path=layout_path)
    window.show()
    sys.exit(app.exec_())
