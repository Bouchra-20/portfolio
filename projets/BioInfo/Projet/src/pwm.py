from Bio import motifs
from Bio.Seq import Seq

import os
import sys

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from src.utils import *

def pwm2pssm(frequency_matrix, pseudocounts=0.1):
    """
    Convertit une matrice de fréquence JASPAR (FrequencyPositionMatrix) en une PSSM (PositionSpecificScoringMatrix).
    
    :param frequency_matrix: Objet FrequencyPositionMatrix (Biopython)
    :param pseudocounts: Valeur du pseudo-poids (défaut 0.1)
    :return: Objet PositionSpecificScoringMatrix (PSSM)
    """
    return frequency_matrix.counts.normalize(pseudocounts=pseudocounts).log_odds()

def scan_sequence(pssm, sequence, threshold=-2.0):
    """
    Recherche les occurrences d'une PSSM dans une séquence.
    
    :param pssm: Objet PositionSpecificScoringMatrix
    :param sequence: Objet Seq (Biopython)
    :param threshold: Seuil de score (défaut -2.0)
    :return: Liste de couples (position, score) où le score dépasse le seuil
    """
    occurrences = []
    for pos, score in pssm.search(sequence, threshold=threshold, both=False):
        position = pos-len(sequence)-1
        occurrences.append((int(position), float(score)))
    
    return occurrences

def scan_all_sequences(pssm, list_seq, threshold):
    """
    Scanne toutes les séquences avec une PSSM pour trouver les positions et scores des occurrences.
    :param pssm: PSSM (PositionSpecificScoringMatrix)
    :param list_seq: Liste des séquences (objets Bio.Seq)
    :param threshold: Seuil de score pour accepter une occurrence
    :return: Liste des résultats de scan (chaque élément est une liste de (position, score))
    """
    scan_results = []
    for seq in list_seq:
        occurrences = scan_sequence(pssm, seq, threshold)
        scan_results.extend(occurrences)  
    return scan_results

def score_window(res_scan, coord_start, coord_stop):
    """
    Calcule le score total des occurrences dans la fenêtre spécifiée.

    :param res_scan: Liste des résultats de `scan_all_sequences`
    :param coord_start: Position de début de la fenêtre
    :param coord_stop: Position de fin de la fenêtre
    :return: Score total de la fenêtre (somme des scores des occurrences dans la fenêtre)
    """
    total_score = 0
    for pos, score in res_scan:
        if coord_start <= pos <= coord_stop:
            total_score += score  
    return total_score

def best_window(res_scan, window_size):
    """
    Recherche la meilleure fenêtre de scores dans les résultats du scan,
    sans inclure l'indice de la séquence dans le résultat.
    
    :param res_scan: Résultats du scan (obtenus avec scan_all_sequences), liste de listes de tuples (position, score)
    :param window_size: Taille de la fenêtre à tester
    :return: Une liste avec les coordonnées de la meilleure fenêtre et son score
    """
    if not res_scan:
        return (None, float('-inf'))
    
    best_pos = None
    best_score = float('-inf')

    positions = sorted([pos for pos, _ in res_scan])

    for i in range(len(positions)):
        start = positions[i]
        end = start + window_size

        window_score = score_window(res_scan, start, end)

        if window_score > best_score:
            best_score = window_score
            best_pos = (start, end)

    return best_pos, best_score


def main():
    Entrez.email = "bouchra.bouamama.etu@univ-lille.fr"
    length = 100
    gene_list = ["NM_007389"]
    sequences = []
    fichier = "./data/MA0114.jaspar"
    threshold = -20

    for gene_id in gene_list:
        g_id = mrna_to_gene(gene_id)
        seq = upstream_gene_seq(g_id, length)
        sequences.append(seq)

    with open(fichier) as handle:
        matrix = motifs.read(handle, "jaspar")
        pssm = pwm2pssm(matrix, 0.01)
        
        results = scan_all_sequences(pssm, sequences, threshold)
        print("Résultats du scan:", results)

        score_result = score_window(results, -100, -50)
        print(f"Score de la fenêtre: {score_result}")
        
        best_window_result = best_window(results, window_size=3)
        print("Meilleure fenêtre trouvée:", best_window_result)

if __name__ == "__main__":
    main()
