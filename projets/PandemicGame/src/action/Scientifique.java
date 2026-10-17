package action;

import game.GameBoard;
import player.Players;
 
/**
 * @author omar_, wafa_, bouchra_, el mehdi_
 *
 */
public class Scientifique extends Action { 
	

//	/**
//	 * @return List of possible actions
//	 */
//	@Override
//	public List<Action> actionPossible() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	public Scientifique(Players player,GameBoard g) {
		super(player,g);
		this.NbCard=4;
	}

	/**
	 * @return true if can discover a cure, false otherwise
	 */
	public boolean peuxDecouvrirUnRemede()
	{ 	// modification of the NbCard variable to 4
		
		return super.peuxDecouvrirUnRemede();
	}

}
