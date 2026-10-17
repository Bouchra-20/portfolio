package card;

import static org.junit.Assert.*;

import org.junit.*;
import city.City;
import game.GameBoard;
import infection.Disease;

public class PlayerCardTest {
	
	private GameBoard gameboard;
	private PlayerCard playercard;
	private City city;
	
	@Before
	public void before() {
		this.city = new City("Paris", 1);
		this.gameboard = new GameBoard();
		this.playercard = new PlayerCard(this.city, Disease.YELLOW);
	}
	
	@Test
	public void pileTest(){
		assertTrue(this.gameboard.getPlayerPile().isEmpty());
		this.playercard.pile(this.gameboard);
		assertTrue(this.gameboard.getPlayerPile().contains(this.playercard));
	}

}
