package action;
 
import java.util.List; 

import city.City;
import game.GameBoard;
import player.Players;
import map.Mappemonde;

/**
 * @author omar_, wafa_, bouchra_, el mehdi_
 *
 */
public class Globetrotter extends Action {
	
	/**
	 * the world map
	 */
	protected Mappemonde mappemonde;
	
	/**
	 * @param p the player
	 * @param m the world map
	 */
	public Globetrotter(Players p, Mappemonde m,GameBoard g)
	{ 
		super(p,g);
		this.mappemonde=m;
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
	 * @return List of cities that are in the world map
	 */
	public List<City> choixDeplacement(){
		return this.mappemonde.getCities();
	}
	/**
	 * @param p the player
	 * @param city the city
	 */
	public void moveAnyWhere(City city) {
		this.player.setLocation(city);
	}

}
