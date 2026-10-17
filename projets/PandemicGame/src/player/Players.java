package player;
import card.*;
import city.*;
import java.util.*;

import action.Action; 

/**
 * @author omar_, wafa_, bouchra_, el mehdi_
 *
 */
public class Players {
	
	/**
	 * the player name
	 */
	private String name;
	/**
	 * List of hand cards
	 */
	private List<Card> hand;
	/**
	 * the location city
	 */
	private City location;
	/**
	 * the card role
	 */
	private Action role;
	
	/**
	 * @param name the player name
	 */
	public Players(String name) {
		this.name = name;
		this.hand = new ArrayList<>();
	}

	/**get player name
	 * @return the player name
	 */
	public String getName() {
		return this.name;
	}
	
	/**get hand cards
	 * @return list of hand cards
	 */
	public List<Card> getHand(){
		return this.hand;
	}
	
	/**add a card to hand
	 * @param card the card
	 */
	public void addCardToHand(Card card) {
		this.hand.add(card);
	}
	
	/**remove a card from player hand
	 * @param card the card
	 */
	public void removeCardFromHand(Card card) {
        this.hand.remove(card);
    }
  
    /**get the location city
     * @return the city
     */
    public City getLocation() {
        return this.location;
    }
  
    /**set a location 
     * @param city the city
     */
    public void setLocation(City city) {
        this.location = city;
    }

	/**
	 * @return the card role
	 */
	public Action getRole() {
		return this.role;
	}

	/**set a card role
	 * @param role the card role
	 */
	public void setRole(Action role) {
		this.role = role;
	}
}
