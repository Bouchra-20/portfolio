package Equipments;
import Players.Player;

public class HealVial extends Equipment<Player> {
    /*
	 * HealVial constructor 
	 */
	public HealVial() {
		super();
		
	}
	/**
	 * Increases the player's life level by 1 if it is below the maximum life level
	 * @param charac the player to be affected by the heal vial
	 */
	public boolean Effect(Player charac){
		int life = charac.getLifeLevel();
		int maxlifelevel = charac.getMaxLifeLevel();
		if ( life < maxlifelevel){
			charac.setLifeLevel (life+1);
			System.out.println("\u001B[31m"+charac.getRole()+" has been treated. Now he has "+charac.getLifeLevel()+" life points"+ "\u001B[0m ");
			this.use();
			return true;
        }
        else{
        	System.out.println("\u001B[36m"+"The player is already at his full lifelevel."+ "\u001B[0m ");
        	return false;
        }
	}
	/**
	 * Returns a string representation of the heal vial.
	 * @return a string representation of the heal vial
	 */
	@Override
	public String toString() {
		return "HealVial";
	}
	
	public String symbole() {
		return "🧪";
	}
}
