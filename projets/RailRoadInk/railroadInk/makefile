# Définitions des commandes Python
PYTHON := python
PYTHONPATH ?= .:clients

# Définitions des scripts
GAME_SCRIPT := game/game.py
TEST_BOARD := tests/game/testBoard.py
TEST_POINTS := tests/game/testPoints.py

# Lancer le jeu
game:
	env PYTHONPATH=$(PYTHONPATH) $(PYTHON) $(GAME_SCRIPT)

# Exécution des tests
test-board:
	$(PYTHON) $(TEST_BOARD)

test-points:
	$(PYTHON) $(TEST_POINTS)

test-rules: 
	$(PYTHON) tests/game/testRules.py

#test-dice:
#	$(PYTHON) tests/game/testDice.py

#test-tile:
#	$(PYTHON) tests/game/testTile.py

test-referee:
	$(PYTHON) tests/clients/test_Referee.py

test-websocket:
	$(PYTHON) tests/modules/test_WebsocketCo.py

# Exécution de tous les tests
test-all: test-board test-points test-rules test-referee test-websocket


.PHONY: game

# Suppression des fichiers temporaires
clean:
	find . -name "__pycache__" -type d -exec rm -r {} +
