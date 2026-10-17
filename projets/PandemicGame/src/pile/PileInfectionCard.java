package pile;

import java.util.ArrayList;
import java.util.List;
import card.*;

public class PileInfectionCard  implements Pile{
	/**
	 * List of discard 
	 */
	protected List<InfectionCard> defausse;
	/**
	 * List of pile cards
	 */
	protected List<InfectionCard> pile;
	
	
	public PileInfectionCard() {
		this.defausse= new ArrayList<InfectionCard>();
		this.pile=new ArrayList<InfectionCard>();

	}
	
	
//	empiler : prends une carte en parametre et defausse
	
	public List<InfectionCard> getDefausse(){
		return this.defausse;
	}
	
	public List<InfectionCard> getPile(){
		return this.pile;
	}

	/**add card to the player pile
	 * @param gameboard the gameboard
	 */
	public void pile(InfectionCard card) {
		this.pile.add(card);
	}

	/**add card to the player discard and remove it from player hand
	 * @param b the gameboard
	 * @param p the player
	 */
	public void defausse(InfectionCard card) {
		this.defausse.add(card);
	}

}