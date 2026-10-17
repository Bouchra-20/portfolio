package card;

import city.City;

import game.GameBoard;
import infection.Disease;
import pile.Pile;

/**
 * @author omar_, wafa_, bouchra_, el mehdi_
 *
 */
public class PlayerCard extends Card implements CardPlayerAndEpidemic, Pile{

	/**
	 * @param c
	 * @param d
	 */
	public PlayerCard(City c, Disease d) {
		super(c, d);
		}
	
	public void pile(GameBoard gameboard) {
		gameboard.ajouterDansPilePlayer(this);
	}
}
