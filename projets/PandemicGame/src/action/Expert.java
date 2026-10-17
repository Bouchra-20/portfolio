package action;

import game.GameBoard;
import player.Players;


/**
 * @author omar_, wafa_, bouchra_, el mehdi_
 *
 */
public class Expert extends Action {
	
	protected Players player;
	
	public Expert(Players p,GameBoard g) {
	super(p,g);
	}

	  
//	/**
//	 * @return List of possible actions
//	 */
//	@Override
//	public List<Action> actionPossible() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	/**
	 * @return false when don't need map to build research station
	 */
	public boolean necessiteCartePourConstruireStationRecherche()
	{ 
		return false;
	}

}
