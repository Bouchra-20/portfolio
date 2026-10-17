package action;

import org.junit.Before;
import city.City;
import game.GameBoard;
import infection.Disease;
import player.Players;
import static org.junit.Assert.*;
import org.junit.*;

public class MedecinTest {
	
	private Disease disease;
	private Players player;
	private Medecin medecin;
	private GameBoard g;
	private City city;
	
	@Before
	public void before() {
		this.disease = Disease.YELLOW;
		this.player = new Players("Wafa");
		this.city = new City("Lille", 0);
		this.medecin = new Medecin(this.player, this.g);
	}

	@Test
	public void nbCubeMaladieARetirerPourTraiterTest() {
		this.player.setLocation(this.city);
		assertTrue(this.city.getNombreCubeMaladie(this.disease)==0);
		this.city.addCubeMaladie(this.disease, 2);
		assertTrue(this.medecin.nbCubeMaladieARetirerPourTraiter(this.disease)==2);
	}

	public void effetTest() {
		this.g.addMaladiesGueries(this.disease);
		this.city.addCubeMaladie(this.disease, 3);
		this.player.setLocation(this.city);
		assertFalse(this.city.getDisease().isEmpty());
		this.medecin.effet();
		assertTrue(this.city.getDisease().isEmpty());
	}
}
