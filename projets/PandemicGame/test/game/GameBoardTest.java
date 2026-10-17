package game;

import org.junit.Before;
import infection.Disease;
import player.Players;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import city.*;
import card.*;
import org.junit.*;

public class GameBoardTest {
	
	private Disease disease1;
	private Disease disease2;
	private GameBoard g;
	private City city1;
	private City city2;
	private Players player;
	private PlayerCard pc1;
	private PlayerCard pc2;
	private InfectionCard infcard;
	private CardPlayerAndEpidemic pe1;
	
	@Before
	public void before() {
		this.disease1 = Disease.YELLOW;
		this.disease2 = Disease.BLACK;
		this.city1 = new City("Paris", 1);
		this.city2 = new City("Lille", 2);
		this.player = new Players("Wafa");
		this.pc1 = new PlayerCard(this.city1, this.disease1);
		this.pc2 = new PlayerCard(this.city2, this.disease2);
		this.infcard = new InfectionCard(this.city1, this.disease1);
		this.g = new GameBoard();
		this.g.NB_TOTAL_FOYERS_INFECTION_MAX=2;
		this.g.TauxGlobalInfection=1;
	}
	
	@Test
	public void ajouterDansDefaussePlayerTest() {
		assertFalse(this.g.getPlayerDefausse().contains(pe1));
		g.ajouterDansDefaussePlayer(this.pe1);
		assertTrue(this.g.getPlayerDefausse().contains(pe1));
	}
	
	@Test
	public void ajouterDansPilePlayerTest() {
		assertFalse(this.g.getPlayerPile().contains(pe1));
		g.ajouterDansPilePlayer(this.pe1);
		assertTrue(this.g.getPlayerPile().contains(pe1));		
	}
	
	@Test
	public void getPlayerDefausseTest() {
		assertFalse(this.g.getPlayerDefausse().contains(pe1));
		this.g.ajouterDansDefaussePlayer(pe1);
		assertTrue(this.g.getPlayerDefausse().contains(pe1));
	}
	
	@Test
	public void getPlayerPileTest() {
		assertFalse(this.g.getPlayerPile().contains(pe1));
		this.g.ajouterDansPilePlayer(pe1);
		assertTrue(this.g.getPlayerPile().contains(pe1));
	}
	
	@Test
	public void ajouterDansDefausseInfectionTest() {
		assertFalse(this.g.getInfectionDefausse().contains(this.infcard));
		g.ajouterDansDefausseInfection(this.infcard);
		assertTrue(this.g.getInfectionDefausse().contains(this.infcard));
	}
	
	@Test
	public void ajouterDansPileInfectionTest() {
		assertFalse(this.g.getInfectionPile().contains(this.infcard));
		g.ajouterDansPileInfection(this.infcard);
		assertTrue(this.g.getInfectionPile().contains(this.infcard));		
	}
	
	@Test
	public void getInfectionDefausseTest() {
		assertFalse(this.g.getInfectionDefausse().contains(this.infcard));
		this.g.ajouterDansDefausseInfection(this.infcard);
		assertTrue(this.g.getInfectionDefausse().contains(this.infcard));
	}
	
	@Test
	public void getInfectionPileTest() {
		assertFalse(this.g.getInfectionPile().contains(this.infcard));
		this.g.ajouterDansPileInfection(this.infcard);
		assertTrue(this.g.getInfectionPile().contains(this.infcard));
	}
	
	@Test
	public void addMaladiesGueries() {
		assertFalse(this.g.getMaladiesGueries().contains(this.disease1));
		this.g.addMaladiesGueries(this.disease1);
		assertTrue(this.g.getMaladiesGueries().contains(this.disease1));
	}
	
	@Test
	public void getMaladiesGueriesTest() {
		assertFalse(this.g.getMaladiesGueries().contains(this.disease1));
		this.g.addMaladiesGueries(this.disease1);
		assertTrue(this.g.getMaladiesGueries().contains(this.disease1));
	}
	
	@Test
	public void addnbFoyersInfectionTest() {
		assertFalse(this.g.getnbFoyersInfection()==1);
		this.g.addnbFoyersInfection();
		assertTrue(this.g.getnbFoyersInfection()==1);
	}
	
	@Test
	public void getnbFoyersInfectionTest() {
		assertFalse(this.g.getnbFoyersInfection()==1);
		this.g.addnbFoyersInfection();
		assertTrue(this.g.getnbFoyersInfection()==1);
		assertFalse(this.g.getnbFoyersInfection()==2);
		this.g.addnbFoyersInfection();
		assertTrue(this.g.getnbFoyersInfection()==2);
	}
	
	@Test
	public void augmenterTauxGlobalInfectionTest() {
		assertFalse(this.g.getTauxGlobalInfection()==2);
		assertTrue(this.g.getTauxGlobalInfection()==1);
		this.g.augmenterTauxGlobalInfection();
		assertTrue(this.g.getTauxGlobalInfection()==2);
	}
	
	@Test
	public void getTauxGlobalInfectionTest() {
		assertFalse(this.g.getTauxGlobalInfection()==2);
		assertTrue(this.g.getTauxGlobalInfection()==1);
		this.g.augmenterTauxGlobalInfection();
		assertTrue(this.g.getTauxGlobalInfection()==2);
		assertFalse(this.g.getTauxGlobalInfection()==3);
	}
	
	@Test
	public void remedeTrouveeTest() {
		assertFalse(this.g.remedeTrouvee(this.disease1));
		this.g.addMaladiesGueries(this.disease1);
		assertTrue(this.g.remedeTrouvee(this.disease1));
	}
	
//	@Test
//	public void tousRemedesTrouveesTest() {
//		assertFalse(this.g.tousRemedesTrouvees());
//		this.city1.getDisease().
//		this.g.addMaladiesGueries(this.disease1);
//		this.g.addMaladiesGueries(this.disease2);
//		assertTrue(this.g.tousRemedesTrouvees());
//	}	
	
	/**
	@Test
	public void getNbCubeMaladieRestantTest() {
		assertTrue(this.city1.getDisease().isEmpty());
		this.city1.addCubeMaladie(this.disease1, 3);
		assertFalse(this.city1.getDisease().isEmpty());
		assertTrue(this.g.getNbCubeMaladieRestant(this.disease1)==3);
	}
	**/

}
