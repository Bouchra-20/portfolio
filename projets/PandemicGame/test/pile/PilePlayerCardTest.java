package pile;

import org.junit.Before;
import card.*;
import city.City;
import infection.Disease;
import static org.junit.Assert.assertTrue;
import org.junit.*;

public class PilePlayerCardTest {

	private PilePlayerCard pilePlayerCard;
	private PlayerCard playercard;
	private City city;
	
	@Before
	public void before() {
		this.city = new City("Paris",1);
		this.pilePlayerCard = new PilePlayerCard();
		this.playercard = new PlayerCard(this.city, Disease.YELLOW);
	}
	
	@Test
	public void getDefausseTest() {
		assertTrue(this.pilePlayerCard.getDefausse().isEmpty());
	}
	
	@Test
	public void getPileTest() {
		assertTrue(this.pilePlayerCard.getPile().isEmpty());
	}
	
	@Test
	public void defausseTest() {
		assertTrue(this.pilePlayerCard.getDefausse().isEmpty());
		this.pilePlayerCard.addDefausse(playercard);
		assertTrue(this.pilePlayerCard.getDefausse().contains(playercard));
	}
	
	@Test
	public void pileTest() {
		assertTrue(this.pilePlayerCard.getPile().isEmpty());
		this.pilePlayerCard.addPile(playercard);
		assertTrue(this.pilePlayerCard.getPile().contains(playercard));
	}

}

