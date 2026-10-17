package pile;

import java.util.ArrayList;
import java.util.List;
import card.*;

public class PilePlayerCard implements Pile{
	/**
	 * List of discard 
	 */
	protected List<CardPlayerAndEpidemic> defausse;
	/**
	 * List of pile cards
	 */
	protected List<CardPlayerAndEpidemic> pile;
	
	
	public PilePlayerCard(CardPlayerAndEpidemic card) {
		this.defausse= new ArrayList<CardPlayerAndEpidemic>();
		this.pile=new ArrayList<CardPlayerAndEpidemic>();

	}
	public PilePlayerCard() {
		this.defausse= new ArrayList<CardPlayerAndEpidemic>();
		this.pile=new ArrayList<CardPlayerAndEpidemic>();

	}
	
	
//	empiler : prends une carte en parametre et defausse
	
	public List<CardPlayerAndEpidemic> getDefausse(){
		return this.defausse;
	}
	
	public List<CardPlayerAndEpidemic> getPile(){
		return this.pile;
	}

	/**add card to the player pile
	 * @param gameboard the gameboard
	 */
	public void addPile(CardPlayerAndEpidemic card) {
		this.pile.add(card);
	}

	/**add card to the player discard and remove it from player hand
	 * @param b the gameboard
	 * @param p the player
	 */
	public void addDefausse(CardPlayerAndEpidemic card) {
		this.defausse.add(card);
	}




}
