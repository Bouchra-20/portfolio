package Cells;
import Players.*;
import java.util.*;
import Actions.*;
import Equipments.*;

public class Cell {
	protected int x;
	protected int y;
	protected List<Player> players;
	protected List<Zombie> zombies;
	protected List<Door> doors;
	protected List<Equipment> equipments;
	protected int noiseLevel; 
	/**Constructor of class Cell
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
		this.noiseLevel = noiseLevel;
		this.players = new ArrayList<>();
		this.zombies = new ArrayList<>();
		this.doors = new ArrayList<Door>();
		this.equipments = new ArrayList<>();
	}
	
	/**Get the x coordinate
	 * @return x coordination
	 */
	public int getCoordinateX() {
		return this.x;
	}
	
	/**Get the y coordinate
	 * @return y coordination
	 */
	public int getCoordinateY() {
		return this.y;
	}
	
	/**
	 * A method that sets the player in the cell 
	 * @param player the player 
	 */
	public void setPlayer(Player player) {
		this.players.add(player);
	}

	/**
	* A method that get the player cell
	* @return The cell occupied by the player
	*/
	public Player getPlayer(Player p) {
		int i = this.players.indexOf(p);
		return players.get(i) ;
	}
	
	public void removePlayer(Player p){
		this.players.remove(p);
	}
	/**
	 * A method that sets the Zombie in the cell 
	 * @param zombie the zombie
	 */
	public void setZombie(Zombie zombie) {
		this.zombies.add(zombie);
	}
	
	public List<Player> players() {
		return players ;
	}
	
	public void removeZombie(Zombie zombie){
	        this.zombies.remove(zombie);
	}

	/**
	* A method that get the Zombie cell
	* @return The cell occupied by the Zombie
	*/
	public List<Zombie> getZombies() {
		return this.zombies;
	}
	public boolean hasZombie(){
	/*if(this.zombie==null){
		return false;
	}else{
		return true;
	}*/
		return !this.zombies.isEmpty();
	}
	public boolean hasPlayer(){
		/*if(this.player==null){
			return false;
		}else{
			return true;
		}*/
		return !this.players.isEmpty();
	}
	
	public List<Door> doors() {
		return doors;
	}

	public int getnoiseLevel(){
		return noiseLevel;
	}
    
	public void setnoiseLevel(int noise){
		this.noiseLevel = noise;

	}
	
	public List<Equipment> equipments(){
		return this.equipments;
	}
	public void addEquipment(Equipment equipment) {
		this.equipments.add(equipment);
	}
	public void takeEquipment(Equipment e){
		this.equipments.remove(e);
	}
	public boolean hasEquipment(){
		return !this.equipments.isEmpty();
	}

    
	
	
	public String toString(){
		return " ";
	}
}
