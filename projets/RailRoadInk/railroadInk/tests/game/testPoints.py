import unittest
import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from rules.points import Points
from railroadInk.composants.tile import *
from railroadInk.composants.board import *

class MockPlayer:
    def __init__(self):
        # Plateau 7x7 avec des tuiles connectées
        # self.board = [
        #     [None, H_Tile(), Sc_Tile(), Rc_Tile(), None, H_Tile(), None],
        #     [None, Hc_Tile(), HH_Tile(), None, None, SS_Tile(), R_Tile()],
        #     [None, None, Hc_Tile(), Hj_Tile(), S_Tile(), Rc_Tile(), None],
        #     [None, None, None, None, None, None, None],
        #     [None, None, None, None, None, None, None],
        #     [None, None, None, None, None, None, None],
        #     [None, None, None, None, None, None, None]
        # ]
        #self.board = [[None for _ in range(9)] for _ in range(9)]
        self.board = Board()
        self.board.setTile(1, 0,H_Tile())
        self.board.setTile(2, 0,Sc_Tile())
        self.board.setTile(3, 0,Rc_Tile())
        self.board.setTile(5, 0,H_Tile())
        self.board.setTile(1, 1,Hc_Tile())
        self.board.setTile(2, 1,HH_Tile())
        self.board.setTile(5, 1,SS_Tile())
        self.board.setTile(6, 1,R_Tile())
        self.board.setTile(2, 2,Hc_Tile())
        self.board.setTile(3, 2,Hj_Tile())
        self.board.setTile(4, 2,S_Tile())
        self.board.setTile(5, 2,Rc_Tile())


    def getBoard(self):
        return self.board


class TestPoints(unittest.TestCase):

    def setUp(self):
        self.points = Points()
        self.player = MockPlayer()

    def test_is_connected_to_exit(self):
        x = 1
        y = 0
        self.assertTrue(self.points.is_connected_to_exit(x, y))

    def test_is_not_connected_to_exit(self):
        x = 0
        y = 0
        self.assertFalse(self.points.is_connected_to_exit(x, y))

    def test_reseaux_points(self):
        # Test pour les points de réseaux (4 sorties connectées = 12 points)
        self.assertEqual(self.points.reseaux_points(self.player), 12)


if __name__ == '__main__':
    unittest.main()