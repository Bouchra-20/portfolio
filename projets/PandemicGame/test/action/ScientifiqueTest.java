package action;

import org.junit.Before;
import card.PlayerCard;
import city.City;
import city.ResearchStation;
import game.GameBoard;
import infection.Disease;
import player.Players;
import static org.junit.Assert.*;
import org.junit.*;

public class ScientifiqueTest {
	
	private Players player;
	private GameBoard g;
	private City city;
	private Scientifique scientifique;
	private PlayerCard pc1;
	private PlayerCard pc2;
	private PlayerCard pc3;
	private PlayerCard pc4;
	private PlayerCard pc5;
	private Disease d;
	private Disease d2;
	private ResearchStation rs;
	
	@Before
	public void before() {
		this.player = new Players("Wafa");
		this.g = new GameBoard();
		this.city = new City("Lille", 1);
		this.d = Disease.BLACK;
		this.d2 = Disease.YELLOW;
		this.pc1 = new PlayerCard(this.city, this.d);
		this.pc2 = new PlayerCard(this.city, this.d);
		this.pc3 = new PlayerCard(this.city, this.d);
		this.pc4 = new PlayerCard(this.city, this.d);
		this.pc5 = new PlayerCard(this.city, this.d2);
		this.scientifique = new Scientifique(this.player, this.g);
		this.rs = new ResearchStation();
		this.player.setLocation(this.city);
	}
	
	@Test
	public void peuxDecouvrirUnRemedeTest() {
		assertFalse(this.scientifique.peuxDecouvrirUnRemede());
		this.city.addCubeMaladie(this.d, 5);
		this.player.addCardToHand(this.pc1);
		this.player.addCardToHand(this.pc2);
		this.player.addCardToHand(this.pc3);
		assertFalse(this.scientifique.peuxDecouvrirUnRemede());
		this.player.addCardToHand(this.pc5);
		assertFalse(this.scientifique.peuxDecouvrirUnRemede());
		this.player.addCardToHand(this.pc4);
		assertFalse(this.scientifique.peuxDecouvrirUnRemede());
		this.city.setResearchStation(this.rs);
		assertTrue(this.scientifique.peuxDecouvrirUnRemede());
	}

}
