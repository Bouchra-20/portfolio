import random
import os
import sys
from PyQt5.QtWidgets import QApplication
import csv
import time
from datetime import datetime
from typing import List, Tuple

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '../Technique_de_pointage/src')))

from TP1.BubbleCursor import BubbleCursor
from TP1.RopeCursor import RopeCursor
from TP1.BubbleWidget import BubbleWidget
from TP1.NormalWidget import NormalWidget        

class XPManager:
    """
    Pilote central de l'expérience. 
    Gère le passage d'une condition à l'autre, le chargement des fichiers 
    de configuration et l'enregistrement des performances (logs).
    """
    def __init__(self, user_id: int, technique: str, densities: List[int], 
                 sizes: List[int], nb_repetitions: int):
        """
        Initialise le gestionnaire d'expérience.
        
        Args:
            user_id: Numéro d'utilisateur
            technique: Technique utilisée ('Bubble', 'Rope', 'Normal')
            densities: Liste des densités (ex: [30, 60, 90])
            sizes: Liste des tailles (ex: [6, 12, 18])
            nb_repetitions: Nombre de répétitions pour chaque combinaison
        """
        self.user_id = user_id
        self.technique = technique
        self.nb_repetitions = nb_repetitions
        
        # Génération de toutes les combinaisons possibles (Produit cartésien)
        self.conditions = []
        for density in densities:
            for size in sizes:
                self.conditions.append((density, size))
        
        random.shuffle(self.conditions)
        
        # Compteurs d'état
        self.current_condition_index = 0 # Quelle combinaison Density/Size
        self.current_repetition = 1      # Quelle répétition de cette condition (1 à N)
        self.current_target_index = 0    # Quelle cible dans la séquence de 15
        
        self.error_count = 0             # Nombre de clics ratés pour la cible actuelle
        self.trial_start_time = None     # Timestamp du début de l'essai

        self.target_sequence = []
        
        self.log_file = f"logs/log_user{user_id}_{technique}.csv"
        self.init_log_file()
        
        self.load_current_condition() # Charge la première séquence de 15

        self.start_trial()
    
    def init_log_file(self):
        """Initialise le fichier de log avec les en-têtes."""
        with open(self.log_file, 'w', newline='') as f:
            writer = csv.writer(f)
            writer.writerow([
                'user_id', 'technique', 'density', 'size', 
                'repetition', 'target_number', 'timestamp', 'errors', 'duration_ms'
            ])

    def start_trial(self):
        """Démarre le chronomètre pour l'essai actuel."""
        self.trial_start_time = time.time()

    def get_trial_duration(self):
        """Retourne la durée de l'essai actuel en millisecondes."""
        if self.trial_start_time is None:
            return 0
        return int((time.time() - self.trial_start_time) * 1000)
    
    def get_current_density(self) -> int:
        """Retourne la densité courante."""
        return self.conditions[self.current_condition_index][0]
    
    def get_current_size(self) -> int:
        """Retourne la taille courante."""
        return self.conditions[self.current_condition_index][1]

    def get_nb_repetitions(self) -> int:
        """Retourne le nombre de répétitions pour la condition courante."""
        return self.nb_repetitions
    
    def get_targets_file(self) -> str:
        """Retourne le chemin du fichier de cibles pour la condition courante."""
        density = self.get_current_density()
        size = self.get_current_size()
        repetitions = self.get_nb_repetitions()
        return f"exps/Exp_{density}_{size}_{repetitions}/targets.csv"
    
    def get_sequence_file(self) -> str:
        """Retourne le chemin du fichier de séquence pour la condition courante."""
        density = self.get_current_density()
        size = self.get_current_size()
        repetitions = self.get_nb_repetitions()
        return f"exps/Exp_{density}_{size}_{repetitions}/sequence.csv"
    
    def load_current_condition(self):
        """Charge les fichiers pour la condition courante."""
        sequence_file = self.get_sequence_file()
        with open(sequence_file, 'r') as f:
            reader = csv.reader(f)
            self.target_sequence = [int(row[0]) for row in reader]
        
        self.current_target_index = 0
    
    def get_current_target_to_select(self) -> int:
        """Retourne l'index de la cible à sélectionner (basé sur la séquence)."""
        return self.target_sequence[self.current_target_index]
    
    def on_target_selected(self, is_correct: bool):
        """
        Appelé quand une cible est sélectionnée.
        
        Args:
            is_correct: True si la bonne cible a été sélectionnée
        """
        if not is_correct:
            self.error_count += 1
        else:
            duration = self.get_trial_duration()
            self.log_trial()

            self.error_count = 0
            self.next_trial()
            self.start_trial()
    
    def log_trial(self):
        """Enregistre les données de l'essai actuel dans le fichier de log."""
        timestamp = datetime.now().isoformat()
        duration = self.get_trial_duration()
        with open(self.log_file, 'a', newline='') as f:
            writer = csv.writer(f)
            writer.writerow([
                self.user_id,
                self.technique,
                self.get_current_density(),
                self.get_current_size(),
                self.current_repetition,
                self.current_target_index + 1,
                timestamp,
                self.error_count,
                duration
            ])
    
    def next_trial(self):
        """Passe au prochain essai."""
        self.current_target_index += 1
        
        if self.current_target_index >= 15:
            self.current_target_index = 0
            self.current_repetition += 1
            
            if self.current_repetition > self.nb_repetitions:
                self.current_repetition = 1
                self.current_condition_index += 1
                
                if self.current_condition_index >= len(self.conditions):
                    return True
                else:
                    self.load_current_condition()
        
        return False
    
    def is_finished(self) -> bool:
        """Vérifie si l'expérience est terminée."""
        return self.current_condition_index >= len(self.conditions)
    
    def get_progress(self) -> str:
        """Retourne une chaîne décrivant la progression."""
        total_conditions = len(self.conditions)
        total_trials = total_conditions * self.nb_repetitions * 15
        current_trial = (self.current_condition_index * self.nb_repetitions * 15 + 
                        (self.current_repetition - 1) * 15 + 
                        self.current_target_index + 1)
        
        return f"Essai {current_trial}/{total_trials} - Condition {self.current_condition_index + 1}/{total_conditions}"


                
            

                
                
        