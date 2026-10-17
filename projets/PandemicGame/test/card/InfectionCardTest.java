package card;

import infection.*;
import player.*;
import static org.junit.Assert.*;
import org.junit.*;

import city.*;
import game.*;

public class InfectionCardTest {

	private GameBoard gameboard;
	private InfectionCard infcard;
	private City city;
	private Players player;
	
	@Before
	public void before() {
		this.city = new City("Paris", 1);
		this.gameboard = new GameBoard();
		this.infcard = new InfectionCard(this.city, Disease.YELLOW);
		this.player = new Players("Wafa");
	}
	
	@Test
	public void pileTest(){
		assertTrue(this.gameboard.getInfectionPile().isEmpty());
		this.infcard.pile(this.gameboard);
		assertTrue(this.gameboard.getInfectionPile().contains(this.infcard));
	}
	
	@Test
	public void defausseTest() {
		this.player.getHand().add(this.infcard);
		assertTrue(this.player.getHand().contains(this.infcard));
		assertTrue(this.gameboard.getInfectionDefausse().isEmpty());
		this.infcard.defausse(this.gameboard, this.player);
		assertTrue(this.gameboard.getInfectionDefausse().contains(this.infcard));
		assertFalse(this.player.getHand().contains(this.infcard));
	}
}
