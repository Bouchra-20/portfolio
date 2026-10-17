package map;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import city.City;

public class MappemondeTest {
	
	private Mappemonde m;
	private Mappemonde m2;
	private Mappemonde m3;
	private City v1;
	private City v2;

	@Before
	public void before() {
		this.m = new Mappemonde();
		this.m2 = new Mappemonde();
		this.m3 = new Mappemonde();
		this.v1 = new City("Paris",1);
		this.v2 = new City("Londre",2);
	}
	
	@Test
	public void getCitiesTest() {
		
		List<City> villes= new ArrayList<City>();
		villes.add(v1);
		m.setCities(villes);
		assertTrue(m.getCities().contains(v1));
		assertFalse(m.getCities().contains(v2));
	}
	
	@Test
	public void setCitiesTest() {
		
		assertFalse(m.getCities().contains(v2));
		List<City> villes2= new ArrayList<City>();
		villes2.add(this.v2);
		m.setCities(villes2);
		assertTrue(m.getCities().contains(v2));
		
	}
	
	@Test
	public void inititTest() throws FileNotFoundException
	{
		assertTrue(m2.getCities().size()==0);
		m2.initialiserVillesDepuisJson("ville.json");
		assertTrue(m2.getCities().size()>0);
	}
	@Test(expected=FileNotFoundException.class )
	public void inititExceptionTest() throws FileNotFoundException
	{
		assertTrue(m3.getCities().size()==0);
		m3.initialiserVillesDepuisJson("fichierInexistant.json");		
	}

}
