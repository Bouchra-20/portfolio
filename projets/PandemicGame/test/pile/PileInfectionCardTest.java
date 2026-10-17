package pile;

import org.junit.Before;

import card.InfectionCard;
import city.City;
import infection.Disease;
import static org.junit.Assert.assertTrue;
import org.junit.*;

public class PileInfectionCardTest {

	private PileInfectionCard pileInfCard;
	private InfectionCard infcard;
	private City city;
	
	@Before
	public void before() {
		this.city = new City("Paris",1);
		this.pileInfCard = new PileInfectionCard();
		this.infcard = new InfectionCard(this.city, Disease.YELLOW);
	}
	
	@Test
	public void getDefausseTest() {
		assertTrue(this.pileInfCard.getDefausse().isEmpty());
	}
	
	@Test
	public void getPileTest() {
		assertTrue(this.pileInfCard.getPile().isEmpty());
	}
	
	@Test
	public void defausseTest() {
		assertTrue(this.pileInfCard.getDefausse().isEmpty());
		this.pileInfCard.defausse(infcard);
		assertTrue(this.pileInfCard.getDefausse().contains(infcard));
	}
	
	@Test
	public void pileTest() {
		assertTrue(this.pileInfCard.getPile().isEmpty());
		this.pileInfCard.pile(infcard);
		assertTrue(this.pileInfCard.getPile().contains(infcard));
	}

}
