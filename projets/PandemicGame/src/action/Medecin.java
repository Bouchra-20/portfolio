package action;

import java.util.ListIterator;

import game.GameBoard;
import player.*;
import infection.Disease;

/**
 * @author omar_, wafa_, bouchra_, el mehdi_
 *
 */
public class Medecin extends Action {
	

//	/**
//	 * @return List of possible actions
//	 */
//	@Override
//	public List<Action> actionPossible() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	

	public Medecin(Players player,GameBoard g) {
		super(player,g);
	}

	/**
	 * @param d the disease
	 * @return number of disease cubes to remove to treat disease
	 */
	public int nbCubeMaladieARetirerPourTraiter(Disease d)
	{	
		return this.player.getLocation().getNombreCubeMaladie(d);
	}
	

	public void effet()
	{ 
		//ici les effets supplémentaires impliquées par le rôle sans utiliser d'actions.


		ListIterator<Disease> i = this.gameBoard.getMaladiesGueries().listIterator();
		while(i.hasNext())
		{ 
			Disease d = i.next(); 
			int nbcubeasupp=this.player.getLocation().getNombreCubeMaladie(d);
			
			this.player.getLocation().removeCubeMaladie(d, nbcubeasupp);
		}
		this.player.getLocation().removeCubeMaladie(null, 0);
	}


}
