package city;

import org.junit.Before;

import infection.Disease;
import player.Players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.*;

import org.junit.*;

//import static org.junit.Assert.*;
//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.Test;
//import city.*;
//import infection.Disease;
//
//import java.util.*;

public class CityTest {

	private City city;
	private City city1;
	private City city2;
	private City city3;
	private City city4;
	private City city5;
	private Players p1 ;
	private Players p2 ;
	private ResearchStation rest1 ;
	private Disease disease;
	private Disease disease2;


	@Before
	public void before() {
		this.city = new City("Paris",1);
		this.city1 = new City("lille",2);
		this.city2 = new City("Paris",1);
		this.city3 = new City("bethune",4);
		this.city4 = new City("Lyon",2);
		this.city5 = new City("Lyon",2);
		this.p1 = new Players("mehdi");
		this.p2= new Players("habouba");
		this.city.setNeighbor(city1);
		this.city.setNeighbor(city2);
		this.city2.addPlayer(p1);
		this.city2.addPlayer(p2);
		this.disease = Disease.YELLOW;
		this.disease2 = Disease.BLACK;
		this.city.addCubeMaladie(this.disease, 3);
		this.city.addCubeMaladie(this.disease2, 2);
		this.rest1= new ResearchStation();
		this.rest1.setLocation(city);
		this.city1.setResearchStation(rest1);

		

	}
	@Test
	public void getNameTest (){
		assertEquals(this.city1.getName(),"lille");
		assertEquals(this.city2.getName(),"Paris");

	}

	@Test
	public void getNeighborsTest (){
		assertEquals(this.city.getNeighbors().size(), 2) ;
		assertEquals(this.city1.getNeighbors().size(), 0) ;
	}
	
	@Test
    public void getResearchStationTest() {
		assertEquals(this.city1.getResearchStation(),rest1);
		assertTrue(this.city2.getResearchStation()==null);
    }
	
	@Test
    public void hasResearchStationTest() {
		assertTrue(this.city1.hasResearchStation());
		assertFalse(this.city2.hasResearchStation());
	}

	@Test
    public void getPlayersTest(){
		assertEquals(this.city2.getPlayers().size(), 2) ;
		assertEquals(this.city.getPlayers().size(), 0) ;
    }
	
	@Test
	public void addPlayerTest() {
		
		assertTrue(this.city2.getPlayers().contains(p1));
		assertTrue(this.city2.getPlayers().contains(p2)); 
		assertFalse(this.city1.getPlayers().contains(p1)); 
		assertFalse(this.city3.getPlayers().contains(p1));
		
	}
	
	@Test
    public void getNombreCubeMaladieTest(){
		assertTrue(this.city.getNombreCubeMaladie(this.disease)==3);
		assertFalse(this.city.getNombreCubeMaladie(this.disease)==2);
		assertTrue(this.city.getNombreCubeMaladie(this.disease2)==2);
	}
	@Test
    public void addCubeMaladieTest() {
		assertEquals(this.city2.getNombreCubeMaladie(Disease.RED),0);
		this.city2.addCubeMaladie(Disease.RED,3);
		assertEquals(this.city2.getNombreCubeMaladie(Disease.RED),3);
	}
	
	@Test
	public void removeCubeMaladieTest() {
		assertEquals(this.city2.getNombreCubeMaladie(Disease.RED),0);
		this.city2.addCubeMaladie(Disease.RED,3);
		assertEquals(this.city2.getNombreCubeMaladie(Disease.RED),3);
		this.city2.removeCubeMaladie(Disease.RED,3);
		assertEquals(this.city2.getNombreCubeMaladie(Disease.RED),0);		
	}
    
    @Test
    public void equalsTest(){
		assertTrue(this.city4.equals(this.city5));
		assertFalse(this.city.equals(this.city4));
	}
    
    @Test
    public void getDiseaseTest() {
    	assertFalse(this.city.getDisease().isEmpty());
    	assertTrue(this.city2.getDisease().isEmpty());
    	assertTrue(this.city.getDisease().contains(Disease.YELLOW));
    	assertTrue(this.city.getDisease().contains(Disease.BLACK));
    	assertFalse(this.city.getDisease().contains(Disease.BLUE));
    }
    
    @Test
    public void setNeighborsTest() {
    	assertTrue(this.city1.getNeighbors().isEmpty());
    	List<City> voisins = new ArrayList<City>();
    	voisins.add(city2);
    	voisins.add(city3);
    	this.city1.setNeighbors(voisins);
    	assertTrue(this.city1.getNeighbors().contains(city2));
    	assertTrue(this.city1.getNeighbors().contains(city3));
    }
    
    @Test
    public void setNeighborTest() {
    	assertTrue(this.city1.getNeighbors().isEmpty());
    	this.city1.setNeighbor(city5);
    	assertTrue(this.city1.getNeighbors().contains(city5));
    }
    
    @Test
    public void toStringTest() {
    	assertEquals(this.city.toString(),"La ville s'appelle Paris");
    }

}
