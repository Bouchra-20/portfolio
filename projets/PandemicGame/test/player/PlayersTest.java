package player;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import action.*;
import card.Card;
import card.PlayerCard;
import city.City;
import infection.Disease;
import map.Mappemonde;
import game.GameBoard;

public class PlayersTest {
	
	private Players player1;
	private Players player2;
	private City city1;
	private City city2;
	private List<Card> hand1;
	private List<Card> hand2;
	private Card card1;
	private Card card2;
	private Disease d1;
	private Disease d2;
	
	@Before
	public void before() {
		this.player1 = new Players("tata");
		this.player2 = new Players("titi");
		this.city1 = new City("Paris",1);
		this.city2 = new City("Madrid",2);
		this.d1 = Disease.YELLOW;
		this.d2 = Disease.BLACK;
		this.card1 = new PlayerCard(city1, d1);
		this.card2 = new PlayerCard(city2, d2);
	
		this.hand1 = new ArrayList<Card>();
		this.hand2 = new ArrayList<Card>();
	}
	
	
	@Test
	public void getNameTest(){
		assertEquals(this.player1.getName(),"tata");
		assertEquals(this.player2.getName(),"titi");
		assertFalse(this.player1.getName()==null);
		assertFalse(this.player2.getName()=="tutu");
	}
	
	@Test
	public void setLocationTest() {
		this.player1.setLocation(city1);
		this.player2.setLocation(city2);
		assertEquals(this.player1.getLocation(), this.city1);
		assertEquals(this.player2.getLocation(), this.city2);
	}
	
	@Test
	public void getLocationTest(){
		assertTrue(this.player1.getLocation()==null);
		assertTrue(this.player2.getLocation()==null);
		this.player1.setLocation(city1);
		this.player2.setLocation(city2);
		assertEquals(this.player1.getLocation(), this.city1);
		assertEquals(this.player2.getLocation(), this.city2);
	}

	@Test
	public void getHandTest(){
		assertEquals(this.player1.getHand(), this.hand1);
		this.player1.addCardToHand(this.card1);
		this.player1.addCardToHand(this.card2);
		this.hand1.add(card1);
		this.hand1.add(card2);
		assertEquals(this.player1.getHand(),this.hand1);
		
		
	}
	
	@Test
	public void addCardToHandTest(){
		assertEquals(this.player1.getHand(), this.hand1);
		assertEquals(this.player2.getHand(), this.hand2);
		this.player1.addCardToHand(this.card1);
		this.player2.addCardToHand(this.card2);
		assertFalse(this.player1.getHand() == this.hand1);
		assertFalse(this.player2.getHand() == this.hand2);
		assertTrue(this.player1.getHand().contains(card1));
		assertTrue(this.player2.getHand().contains(card2));
		assertFalse(this.player2.getHand().contains(card1));
	}
	
	@Test
	public void removeCardFromHandTest(){
		assertEquals(this.player1.getHand(), this.hand1);
		assertEquals(this.player2.getHand(), this.hand2);
		this.player1.addCardToHand(this.card1);
		this.player2.addCardToHand(this.card2);
		assertTrue(this.player1.getHand().contains(card1));
		assertTrue(this.player2.getHand().contains(card2));
		this.player1.removeCardFromHand(this.card1);
		this.player2.removeCardFromHand(this.card2);
		assertEquals(this.player1.getHand(), this.hand1);
		assertEquals(this.player2.getHand(), this.hand2);
	}
	
	@Test
	public void getRoleTest() {
		GameBoard gameBoard = new GameBoard();
		Mappemonde m = new Mappemonde();
		Action role = new Medecin(this.player1, gameBoard); 
		Action role1 = new Globetrotter(this.player2,m,gameBoard);
		
		this.player1.setRole(role);
		this.player2.setRole(role1);
		assertEquals(player1.getRole(),role);
		assertEquals(this.player2.getRole(),role1);
		assertFalse(this.player2.getRole().equals(role));
		 
	}
	@Test
	public void setRoleTest() 
	{
		GameBoard gameBoard = new GameBoard();
		Mappemonde m = new Mappemonde();
		Action role = new Medecin(this.player1,gameBoard); 
		Action role1 = new Globetrotter(this.player2,m,gameBoard);
		
		assertTrue(this.player1.getRole()==null);
		this.player1.setRole(role);
		this.player2.setRole(role1);
		assertEquals(this.player1.getRole(),role);
		assertEquals(this.player2.getRole(),role1);
		
		
		 
	}

}
