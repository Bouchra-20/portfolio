package Players;
import Cells.*;
import java.util.*;

import Equipments.*;
import Equipments.Map;

public class Player implements NosyRole, HealerRole, LuckyRole, FighterRole{
	private int actionPoints;
	private int expertiseLevel;
	protected int lifeLevel;
	private List<Equipment> equipments;
	protected Equipment righthandItem;
	protected Weapon lefthandItem;
	protected Cell cell;
	private String role;
	public static final int MAX_LIFE_LEVEL = 5;
	protected int total ;
	
	/**
	 *  constrctor of Player  
	 * @param lifeLevel player's life
	 * @param actionPoints actionpoints
	 * @param expertiseLevel player 's exp
	 */ 
	public Player(int expertiseLevel, int lifeLevel, int actionPoints) {
		this.lifeLevel= lifeLevel;
		this.actionPoints = actionPoints;
		this.expertiseLevel = expertiseLevel;
    	this.equipments = new ArrayList<>(); 
		//Adds a healing potion to the player's hand
		this.righthandItem = null;
		this.lefthandItem = new Weapon(WeaponType.GUN);
		//Add a map to the backpack
		//this.equipments.add(new Map());
		this.role=null;
		this.total = actionPoints;
	}
	
	/**
	 * A method that gets the actionPoints
	 * @return actionPoints  
	 * */ 
	public int getActionPoints() {
		return actionPoints;
	}
    
	/**
	 * A method that sets actionPoints
	 * @param actionPoints 
	 */
	public void setActionPoints(int actionPoints) {
		this.actionPoints = actionPoints;
		this.total = this.actionPoints;
	}
    
	public void decreaseActionPoints(){
		this.actionPoints--;
	}
    
	public void resetActionPoints(){
		if(this.actionPoints == 0){
			this.actionPoints = this.total;
		}
	}
	
	/**
	 * A method that gets TheExpertiseLevel 
	 * @return expertiseLevel
	 */
	public int getExpertiseLevel() {
		return expertiseLevel;
	}
	/**
	 * A method that rsets the expertiseLevel
	 * @param expertiseLevel   player EXP
	 */

	public void setExpertiseLevel(int expertiseLevel) {
		this.expertiseLevel = expertiseLevel;
	}
	
	public void increaseExpertiseLevel(){
		this.expertiseLevel++;
		if(this.expertiseLevel == 3 || this.expertiseLevel == 7 || this.expertiseLevel == 11){
			this.total++;
		}
	}
    /**
	 * A method that get LifeLevel
	 * @return lifeLevel
	 */
	public int getLifeLevel() {
		return lifeLevel;
	}
    /**
	 * A method that sets the LifeLevel
	 * @param lifeLevel player's lifepoints
	 */
	public void setLifeLevel(int lifeLevel) {
		this.lifeLevel = lifeLevel;
	}
    
	/**
	 * A method that gets the Equipments
	 * @return equipments 
	 */
	public List<Equipment> getEquipments() {
		return equipments;
	}
    
    /**
	 * A method that sets the Equipments 
	 * @param equipment equipement
	 */
	public void setEquipment(Equipment equipment) {
		this.equipments.add(equipment);
	}

	/**
	 * A method that get the item  to be held by the player.
	 * @return The equipment currently held in the player's hand.
	 */
	public Equipment getRightHandItem() {
		return righthandItem;
	}

	/**
	 * A method that sets the item to be held by the player.
	 * @param righthandItem The equipment to be held in the player's hand.
	 */
	public void setRightHandItem(Equipment righthandItem) {
		this.righthandItem = righthandItem;
	}
    /**
	 * A method that get the item  to be held by the player.
	 * @return The equipment currently held in the player's left hand.
	 */
	public Weapon getLeftHandItem() {
		return lefthandItem;
	}

	/**
	 * A method that sets the item to be held by the player.
	 * @param lefthandItem The equipment to be held in the player's left hand.
	 */
	public void setLeftHandItem(Weapon lefthandItem) {
		this.lefthandItem = lefthandItem;
	}
    
	/**
	 * a method that gets the max life level
	 * @return
	 */
	public int getMaxLifeLevel() {
		return MAX_LIFE_LEVEL;
	}

	 /**
	 * A method that sets the player in the cell 
	 * @param cell the cell 
	 */
	public void setCell(Cell cell) {
		this.cell = cell;
	}

	/**
	* A method that get the player cell
	* @return The cell occupied by the player
	*/
	public Cell getPlayerCell() {
		return this.cell;
	}

	/** Override the method from the NosyRole interface
	* @return a list of equipements 
	*/@Override
	public List<Equipment> searchEquipments() {
		// Simulate finding a HealVial and a Weapon
		List<Equipment> found = new ArrayList<>();
		found.add(new HealVial());
		WeaponType randomWeaponType = getRandomWeaponType();
		found.add(new Weapon(randomWeaponType));
		return found;
	}
	private static WeaponType getRandomWeaponType() {
		WeaponType[] weaponTypes = WeaponType.values();

		Random random = new Random();
		int randomIndex = random.nextInt(weaponTypes.length);

		return weaponTypes[randomIndex];
	}

	// HealerRole implementation
	/**
	 * Heals the specified target player by increasing their life level by 1, not exceeding the MAX_LIFE_LEVEL.
	 * Implements the HealerRole interface.
	 * @param target The player to be healed.
	 */
	@Override
	public boolean heal(Player target) {
		
		return HealerRole.super.heal(target);
		/*if (target != null && target.lifeLevel < MAX_LIFE_LEVEL) {
			target.lifeLevel++; // Heal the target by increasing their life level by 1
			System.out.println("Player healed, new life level: " + target.lifeLevel);
			return true;
		}
		return false;*/
	}

	// Implementation for LuckyRole interface
	/**
	 * Rolls an extra die as part of the LuckyRole implementation.
	 * @return The result of rolling an additional six-sided die.
	 */
	@Override
	public int throwExtraDie() {
		// Return the result of rolling an additional six-sided die
		return (int) (Math.random() * 6) + 1;
	}

	// FighterRole implementation
	/**
	 * Simulates an attack action, incorporating a bonus for the attack roll.
	 * Implements the FighterRole interface.
	 */
	@Override
	public void attack() {
		// Perform an attack with an additional bonus from the FighterRole
		int attackRoll = rollDice() + 1; // Add bonus of 1 to each dice roll
		// Implement the attack logic using the attackRoll value
	}
	/**
	 * method that returns true if the player can use the weapon to attack successfully
	 * and false if the attack fails
	 * @param W weapon
	 * @param Z zombie
	 * @return boolean
	 */
	public boolean useWeapon(Weapon W,Zombie Z){
		int diceSum=0;
		if(W.getDieThrow()==2){
			diceSum+=this.rollDice();
			diceSum+=this.rollDice();
		}else{
			diceSum+=this.rollDice();
		}
		if(W.getThreshold()==diceSum){
			if(this.attackZombie(Z,W)) {
				Z.setLifePts(Z.getLifePts()-W.getDamagePts());
			}else {
				System.out.println("Attack failed!");
				return false;
			}
		}else{
		return false;
	}
		return false;
}
	public boolean attackZombie(Zombie Z,Weapon W) {
		if((Z.getType()==ZombieType.ABOMINATION || Z.getType()==ZombieType.STRONG)&&(W.getDamagePts()==1)) {
			return false;
		}else {
			return true;
		}
	}
	/**
	 * Simulates rolling a six-sided die.
	 * @return a random number between 1 and 6.
	 */
	public int rollDice() {
		// Simulates rolling a six-sided die
		return (int) (Math.random() * 6) + 1;
	}
	/*public String toString(){
		return "in right hand : "+this.getRightHandItem().toString() +" in bag : "+this.getEquipments().get(0).toString();
	}*/
	/**
	 * method that prints the player stats
	 */
	public void PlayerStats(){
		System.out.println("\u001B[35m"+"The role of " +this.getRole()+ "\u001B[0m ");
		System.out.println("\u001B[35m"+this.getRole()+" life points : "+this.getLifeLevel()+ "\u001B[0m ");
		System.out.println("\u001B[35m"+this.getRole()+" EXP : "+this.getExpertiseLevel()+ "\u001B[0m ");
		System.out.println("\u001B[35m"+this.getRole()+" remaining Action points : "+this.getActionPoints()+ "\u001B[0m ");
	}
	public boolean isBetween1And6(int value){
		return value >= 1 && value <=6 ; 
	}
	public void assignedRole(int number){
		 switch(number){
			case 1 :
			    this.role="Nosy";
				break;
			case 2 :
			this.role="Fighter";
				break;
			case 3 :
			this.role="Healer";
				break;
			case 4 :
			this.role="Lucky";
				break;
		 }
	}
	/**
	 * method that adds equipement to player's bag
	 * @param E
	 */
	public void PickUp(Equipment E){
		this.equipments.add(E);
		System.out.println("\u001B[31m"+this.role+" has picked up "+E.toString()+"\u001B[0m");

	}
	public String getRole(){
		return this.role;
	}
	
	public String symbole(){
		if(this.lifeLevel == 1){
            return "🚶¹";
        }else if(this.lifeLevel == 2){
            return "🚶²";
        }else if(this.lifeLevel == 3){
            return "🚶³";
        }else if(this.lifeLevel == 4){
            return "🚶⁴";
        }else if(this.lifeLevel == 5){
            return "🚶⁵";
        }
		return "🚶";
	}
}