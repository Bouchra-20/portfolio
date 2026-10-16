from PyQt5.QtWidgets import QApplication, QMainWindow, QMessageBox

import sys
from ExpSetup import ExpSetup
from TP1.NormalWidget import NormalWidget
from TP1.BubbleWidget import BubbleWidget
from TP1.RopeWidget import RopeWidget
from XPManager import XPManager

LARGEUR = 1024
HAUTEUR = 768

class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()

        self.setWindowTitle("Expérience")
        self.resize(LARGEUR, HAUTEUR)

        self.xp_manager = None
        self.current_widget = None
        
        self.setup_exp = ExpSetup()

        self.setup_exp.validateBtn.clicked.connect(self.start_experiment)
        self.setup_exp.exec_()

    
    def start_experiment(self):
        """Démarre l'expérience avec les paramètres du dialogue."""
        user_id = self.setup_exp.get_user_id()
        technique = self.setup_exp.get_technique()
        densities = self.setup_exp.get_densities()
        sizes = self.setup_exp.get_sizes()
        nb_repetitions = self.setup_exp.get_nb_repetitions()
        
        self.setup_exp.accept()
        
        self.xp_manager = XPManager(
            user_id=user_id,
            technique=technique,
            densities=densities,
            sizes=sizes,
            nb_repetitions=nb_repetitions
        )
        
        self.create_widget(technique)
        
        self.show()
    
    def create_widget(self, technique: str):
        """Crée et configure le widget approprié pour la technique."""
        targets_file = self.xp_manager.get_targets_file()
        
        if technique == "Bubble":
            self.current_widget = BubbleWidget(targets_path=targets_file)
        elif technique == "Rope":
            self.current_widget = RopeWidget(targets_path=targets_file)
        elif technique == "Normal":
            self.current_widget = NormalWidget(targets_path=targets_file)
        else:
            raise ValueError(f"Technique inconnue: {technique}")
        
        self.setup_widget_for_experiment()
        
        self.setCentralWidget(self.current_widget)
        
        self.select_target_from_sequence()
        
        self.update_title()
    
    def setup_widget_for_experiment(self):
        """Configure le widget pour l'expérience contrôlée."""
        original_mouse_press = self.current_widget.mousePressEvent
        
        def new_mouse_press(event):
            self.current_widget.curseur.move(event.x(), event.y())
            self.current_widget.update()

        
            target_index = self.xp_manager.get_current_target_to_select()
            selected_target = self.current_widget.curseur.closest
            
            if selected_target is None:
                print("  Aucune cible proche du curseur")
                return
            
            try:
                selected_index = self.current_widget.targets.index(selected_target)
            except ValueError:
                return
            
            is_correct = (selected_index == target_index)

            if is_correct:
                selected_target.selected = True
                self.current_widget.update()
                self.xp_manager.on_target_selected(is_correct=True)
                
                if self.xp_manager.is_finished():
                    self.end_experiment()
                else:
                    if self.xp_manager.current_target_index == 0:
                        self.reload_condition()
                    else:
                        self.select_target_from_sequence()
                        self.update_title()
            else:
                self.xp_manager.on_target_selected(is_correct=False)
        
        self.current_widget.mousePressEvent = new_mouse_press
    
    def select_target_from_sequence(self):
        """Sélectionne la cible à trouver selon la séquence."""
        for target in self.current_widget.targets:
            target.toSelect = False
            target.selected = False
        
        target_index = self.xp_manager.get_current_target_to_select()
        if target_index < len(self.current_widget.targets):
            self.current_widget.targets[target_index].toSelect = True
            self.current_widget.update()
    
    def reload_condition(self):
        """Recharge les cibles pour une nouvelle condition."""
        technique = self.xp_manager.technique
        
        self.create_widget(technique)
    
    def update_title(self):
        """Met à jour le titre de la fenêtre avec la progression."""
        progress = self.xp_manager.get_progress()
        density = self.xp_manager.get_current_density()
        size = self.xp_manager.get_current_size()
        
        title = (f"Expérience - {self.xp_manager.technique} - "
                f"Densité: {density}, Taille: {size} - {progress}")
        self.setWindowTitle(title)
    
    def end_experiment(self):
        """Termine l'expérience."""
        QMessageBox.information(
            self,
            "Expérience terminée",
            f"Merci d'avoir participé à l'expérience !\n"
            f"Les résultats ont été sauvegardés dans {self.xp_manager.log_file}"
        )
        self.close()



if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    sys.exit(app.exec_())
