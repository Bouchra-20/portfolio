import unittest
from unittest.mock import patch, Mock
from Bio import motifs
from Bio.Seq import Seq
import sys
import os

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from src.pwm import pwm2pssm, scan_all_sequences, scan_sequence, score_window, best_window, main

class TestPWM(unittest.TestCase):

    def setUp(self):
        self.jaspar_content = """
>JASPAR MA0001.1 TEST
A  [10   2   5]
C  [ 2   8   3]
G  [ 3   5   7]
T  [ 5   5   5]
"""
        self.matrix = motifs.parse(self.jaspar_content.splitlines(), "jaspar")[0]
        
        self.sequence = Seq("ATGCAGCT")
        self.sequences = [self.sequence, Seq("GCTAGCTA")]

        self.scan_results = [(-7, 2.5), (-5, 3.0), (-3, 1.5)]

    def test_pwm2pssm(self):
        """Teste la conversion d'une matrice de fréquence en PSSM."""
        pssm = pwm2pssm(self.matrix, pseudocounts=0.1)
        self.assertEqual(len(pssm[0]), 3)
        
    def test_scan_sequence(self):
        """Teste la recherche d'occurrences dans une séquence."""
        pssm = pwm2pssm(self.matrix, pseudocounts=0.1)
        occurrences = scan_sequence(pssm, self.sequence, threshold=0.0)
        self.assertIsInstance(occurrences, list)
        self.assertTrue(all(isinstance(item, tuple) and len(item) == 2 for item in occurrences))
        self.assertTrue(all(pos < 0 for pos, score in occurrences))

    def test_scan_all_sequences(self):
        """Teste le scan de plusieurs séquences."""
        pssm = pwm2pssm(self.matrix, pseudocounts=0.1)
        results = scan_all_sequences(pssm, self.sequences, threshold=0.0)
        self.assertIsInstance(results, list)
        self.assertTrue(len(results) > 0)
        self.assertTrue(all(isinstance(item, tuple) and len(item) == 2 for item in results))

    def test_score_window(self):
        """Teste le calcul du score dans une fenêtre."""
        score = score_window(self.scan_results, -6, -4)
        self.assertEqual(score, 3.0)
        score = score_window(self.scan_results, -2, 0)
        self.assertEqual(score, 0)

    def test_best_window(self):
        """Teste la recherche de la meilleure fenêtre."""
        window, score = best_window(self.scan_results, window_size=2)
        self.assertEqual(window, (-7, -5))
        self.assertEqual(score, 5.5)

        # Test avec une fenêtre vide
        empty_results = []
        result = best_window(empty_results, window_size=2)
        self.assertEqual(result, (None, float('-inf')))

if __name__ == "__main__":
    unittest.main()