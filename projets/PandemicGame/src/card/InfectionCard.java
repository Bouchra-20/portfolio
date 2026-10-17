package card;

import city.City;

import game.GameBoard;
import player.Players;
import infection.Disease;

/**
 * @author omar_, wafa_, bouchra_, el mehdi_
 *
 */
public class InfectionCard extends Card{

	/**
	 * @param c the city
	 * @param d the disease
	 */
	public InfectionCard(City c,Disease d) {
		super(c, d);
		}

	/**
	 * add this card to the infection pile
	 */
	public void pile(GameBoard gameboard) {
		gameboard.ajouterDansPileInfection(this);
	}

	/**
	 * add this card to the infection discard and remove it from player hand
	 */
	public void defausse(GameBoard b, Players p) {
		b.ajouterDansDefausseInfection(this);
		p.removeCardFromHand(this);
	}

}
