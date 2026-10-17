import unittest
from unittest.mock import patch, MagicMock

import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from clients.referee import Referee

class TestReferee(unittest.TestCase):

    def setUp(self):
        self.mock_history = MagicMock()
        self.mock_history.current = 1
        self.mock_ws = MagicMock()
        self.referee = Referee("ws://test_url", history=self.mock_history)
        self.referee.ws = self.mock_ws
        self.referee.rules = {
            "valid_tiles": ["HH", "SH", "Hj"],
            "valid_orientations": ["S", "L", "R", "U"]
        }

    def test_validate_message_places_valid(self):
        message = "player1 PLACES HH S A1"
        self.referee.validate_message(message)
        self.mock_ws.send.assert_not_called()

    def test_validate_message_places_invalid_tile(self):
        message = "player1 PLACES Z9 L A1"
        with patch.object(self.referee, 'blames') as mock_blames:
            self.referee.validate_message(message)
            mock_blames.assert_called_once()

    def test_validate_message_places_invalid_orientation(self):
        message = "player1 PLACES SH X A1"
        with patch.object(self.referee, 'blames') as mock_blames:
            self.referee.validate_message(message)
            mock_blames.assert_called_once()

    def test_validate_message_places_invalid_cell(self):
        message = "player1 PLACES SH R Z9"
        with patch.object(self.referee, 'blames') as mock_blames:
            self.referee.validate_message(message)
            mock_blames.assert_called_once()

    def test_validate_message_throws_valid(self):
        message = "player1 THROWS Hj HH"
        self.referee.validate_message(message)
        self.mock_ws.send.assert_not_called()

    def test_validate_message_throws_invalid_tile(self):
        message = "player1 THROWS Z9"
        with patch.object(self.referee, 'blames') as mock_blames:
            self.referee.validate_message(message)
            mock_blames.assert_called_once()

    def test_respect_rules(self):
        with patch.object(self.referee, 'rule', return_value=True):
            self.assertTrue(self.referee.respect_rules("test message"))
        with patch.object(self.referee, 'rule', return_value=False):
            self.assertFalse(self.referee.respect_rules("test message"))

if __name__ == '__main__':
    unittest.main()