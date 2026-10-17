/**
package action;

import org.junit.Before;
import org.junit.Test;

import city.City;
import city.ResearchStation;
import player.Players;

public class ActionTest {
	
	private City c1;
	private City c2;
	private ResearchStation s1;
	@Before
	public void before() {
		new Players("Toto");
		new Players("Rina");
		this.c1 = new City("Paris",1);
		this.c2 = new City("lille",2);
		this.s1 = new ResearchStation();
		new ResearchStation();
	}
	
	

	@Test
	public void peuxDecouvrirUnRemedeTest() {
		this.c1.setNeighbor(c2);
		this.s1.setLocation(c1);
		this.c1.setResearchStation(s1);
	}

}
**/
