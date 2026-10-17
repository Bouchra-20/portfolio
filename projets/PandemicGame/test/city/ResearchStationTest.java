package city;

import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.*;

public class ResearchStationTest {

	private ResearchStation rs;
	private City city;
	
	@Before
	public void before() {
		this.rs = new ResearchStation();
		this.city = new City("Paris", 1);
	}
	
	@Test
	public void getLocationTest() {
		this.rs.setLocation(this.city);
		assertEquals(this.rs.getLocation(), this.city);
	}
	
	@Test
	public void setLocationTest() {
		this.rs.setLocation(this.city);
		assertEquals(this.rs.getLocation(), this.city);
	}

}
