import unittest
import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from composants.board import Board
from railroadInk.composants.tile import H_Tile, H_Exit, R_Tile, R_Exit, Tile


class TestBoard(unittest.TestCase):

    def setUp(self):
        self.board = Board()

    def test_init(self):
        self.assertIsInstance(self.board.getTile(1, -1), H_Exit)
        self.assertIsInstance(self.board.getTile(5, -1), H_Exit)
        self.assertIsInstance(self.board.getTile(-1, 3), H_Exit)
        self.assertIsInstance(self.board.getTile(7, 3), H_Exit)
        self.assertIsInstance(self.board.getTile(3, -1), R_Exit)
        self.assertIsInstance(self.board.getTile(3, 7), R_Exit)
        self.assertIsInstance(self.board.getTile(-1, 1), R_Exit)
        self.assertIsInstance(self.board.getTile(7, 1), R_Exit)
        self.assertIsNone(self.board.getTile(2, 2))

    def test_repr(self):
        board_repr = repr(self.board)
        self.assertTrue(board_repr.startswith("Board({"))
        self.assertTrue(board_repr.endswith("})"))

    def test_str(self):
        board_str = str(self.board)
        self.assertEqual(len(board_str.split('\n')), 9)


    def test_setTile(self):
        new_tile = H_Tile()
        self.board.setTile(2, 2, new_tile)
        self.assertEqual(self.board.getTile(2, 2), new_tile)

    def test_tiles_compatible(self):
        self.board.setTile(1, 1, H_Tile())
        self.assertFalse(self.board.tiles_compatible(1, 1, -1, 1))

if __name__ == '__main__':
    unittest.main()