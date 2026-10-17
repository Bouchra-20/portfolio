package card;

import city.City;
import infection.Disease;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class CardTest {
	
	private City c1;
	private City c2;
	private Card card1;
	private Card card2;
	private Disease d1;
	private Disease d2;
	
	@Before
	public void before()
	{ 
		this.c1 = new City("Paris", 0);
		this.c2 = new City("Madrid",1);
		this.d1 = Disease.BLACK;
		this.d2 = Disease.YELLOW;
		this.card1 = new PlayerCard(this.c1, this.d1);
		this.card2 = new InfectionCard(this.c2, this.d2);
		new PlayerCard(this.c1, this.d1);
		new InfectionCard(this.c1, this.d1);
	}	
	
	@Test 
	public void getCityTest(){
		assertEquals(this.card1.getCity(), this.c1);
		assertEquals(this.card2.getCity(), this.c2);
		assertFalse(this.card1.getCity()==null);
		assertFalse(this.card2.getCity()==null);
		assertFalse(this.card2.getCity().equals(this.c1));
	}
	
	public void getDiseaseTest() {
		assertEquals(this.card1.getDisease(), this.d1);
		assertEquals(this.card2.getDisease(), this.d2);
		assertFalse(this.card1.getDisease()==null);
		assertFalse(this.card2.getDisease()==null);
		assertFalse(this.card2.getDisease().equals(d1));
	}
}