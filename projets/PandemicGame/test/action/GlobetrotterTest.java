package action;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;
import city.City;
import game.GameBoard;
import map.*;
import player.Players;

public class GlobetrotterTest {
	
	private City city1;
	private City city2;
	private Players player;
	private Globetrotter globetrotteur;
	private Mappemonde m;
	private GameBoard g;
	
	@Before
	public void before() {
		this.city1 = new City("Lille", 1);
		this.city2 = new City("Paris", 2);
		this.player = new Players("Wafa");
		this.m = new Mappemonde();
		this.g = new GameBoard();
		this.globetrotteur = new Globetrotter(this.player, this.m, this.g);
	}

	@Test
	public void moveAnyWhereTest() {
		this.player.setLocation(this.city1);
		assertEquals(this.player.getLocation(), this.city1);
		this.globetrotteur.moveAnyWhere(this.city2);
		assertEquals(this.player.getLocation(), this.city2);
		assertFalse(this.player.getLocation()==this.city1);
	}

	@Test
	public void choixDeplacementTest() {
		List<City> l1 = this.m.getCities();
		assertEquals(l1, this.globetrotteur.choixDeplacement());
	}
}
