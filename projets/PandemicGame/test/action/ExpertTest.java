package action;

import static org.junit.Assert.*;
import org.junit.Test;
import player.Players;
import game.GameBoard;

public class ExpertTest {
	GameBoard gameBoard = new GameBoard();
	Players p1 = new Players("Toto");
	Expert e1 = new Expert(p1, gameBoard);
	
	@Test
	public void necessiteCartePourConstruireStationRechercheTest() {
		assertFalse(e1.necessiteCartePourConstruireStationRecherche());
	}
}
