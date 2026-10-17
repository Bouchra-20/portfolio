package Actions;

import Players.*;
import Cells.*;
import Board.*;
import Equipments.*;
import java.util.*;

public class AttackAction {
    private Player player;
    private Zombie target;
    private Weapon weapon;
    
   /**
    * Constructs an AttackAction object.
    * @param player The player initiating the attack.
    * @param target The target zombie of the attack.
    * @param weapon The weapon used for the attack.
    */
    public AttackAction(Player player, Zombie target, Weapon weapon) {
        this.player = player;
        this.target = target;
        this.weapon = weapon;
    }
    
    /**
     * gets the target zombie of the attack .
     * @return The target zombie.
     */
    public Zombie getTarget(){
        return this.target;
    }
    
    /**
     * executes the attack action .
     * @return true if the attack  is successful ,false otherwise .
     */
    public boolean execute() {
    	if(player.getPlayerCell() instanceof Hotel){
            System.out.println("\u001B[36m"+"You cannot attack in a hotel."+ "\u001B[0m ");
            return false;
        }
        // Check if the target is within the range of the weapon
        if (!isTargetInRange()) {
            // Perform the attack roll
            System.out.println("\u001B[36m"+"The zombie is not within range of the weapon."+ "\u001B[0m ");
            return false;
        }
        if(weapon.getDamagePts() == 1) {
        	if(target.getType() == ZombieType.STRONG || target.getType() == ZombieType.ABOMINATION) {
        		System.out.println("\u001B[31m"+"Miss! The attack did not hit "+target.getType()+"\u001B[0m");
        		return true;
        	}
        }

        // Initialize attack roll with base roll
        //int attackRoll = player.rollDice();
        int attackRoll = 0;
        int nbrThrows = weapon.getDieThrow();
        // If the player is lucky, roll an extra die
        if (player.getRole().equals("Lucky")) {
        	nbrThrows += 1;
        }

        // Roll additional dice based on the weapon's die throw count
        for (int i = 0; i < nbrThrows; i++) {
            attackRoll += player.rollDice();
            if (player.getRole().equals("Fighter")) {
                attackRoll += ((FighterRole) player).getAttackRollBonus() * weapon.getDieThrow();
            }
            // Check if the attack roll meets or exceeds the threshold
            if (attackRoll >= weapon.getThreshold()) {
                int damageDealt = weapon.getDamagePts();
                target.setLifePts(target.getLifePts() - damageDealt);
                System.out.println("\u001B[31m"+"Hit! You dealt " + damageDealt + " damage to "+target.getType()+"\u001B[0m");

                if (target.getLifePts() <= 0) {
                    System.out.println("\u001B[31m"+target.getType()+" has been defeated!"+"\u001B[0m");
                    player.increaseExpertiseLevel();
                    System.out.println("\u001B[32m"+player.getRole()+" expertise level is "+ player.getExpertiseLevel()+ "\u001B[0m ");
                    player.getPlayerCell().getZombies().remove(target);
                    break;
                }

                // Produce noise if the weapon is noisy
                if (weapon.isNoisy()) {
                    weapon.produceNoise(player.getPlayerCell());
                }
                //return true;
            } else {
                System.out.println("\u001B[31m"+"Miss! The attack did not hit "+target.getType()+"\u001B[0m");
                //return false;
            }
        }
        return true;
        // If the player is a fighter, add bonus to each die roll
        
        
    }
    
    /**
     * Checks if the target zombie is within the  range of the weapon .
     * @return true if the target zombie is in range , false otherwise .
     */
    private boolean isTargetInRange() {
        Cell playerCell = player.getPlayerCell();
        Cell zombieCell = target.getCellPosition();

        int distanceX = Math.abs(playerCell.getCoordinateX() - zombieCell.getCoordinateX());
        int distanceY = Math.abs(playerCell.getCoordinateY() - zombieCell.getCoordinateY());
        double distance = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

        return distance >= weapon.getMinRange() && distance <= weapon.getMaxRange();
    }
    
    /**
     * finds a target zombie on a given board at specified coordinates. 
     * @param board The game board.
     * @param x  the x-coordinate of the cell to search for the target.
     * @param y  the y-coordinate of the cell to search for the target.
     * @return true if a target zombie is found and attacked, false otherwise.
     */
    public boolean findTarget(Board board, int x, int y){
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);
        if(board.getBoard()[x][y] instanceof Hotel){
            return false;
        }
        if(board.getBoard()[x][y].getZombies().size()>1){
        	List<Zombie> zombiesOfCell = board.getBoard()[x][y].getZombies();
        	System.out.println("Choose a zombie with his lifePoints or 0 to quit : ");
        	boolean exit = false;
        	while(!exit) {
        		for (int z=0 ;z<zombiesOfCell.size();z++){
                    System.out.print(zombiesOfCell.get(z).symbole());
                }
                System.out.println("");
                if(scanner.hasNextInt()){
               	 	int points = scanner.nextInt();
               	 	if(points == 0) {
               	 		exit = true;
               	 		return false;
               	 	}
                    for(Zombie z : zombiesOfCell){
                        if(z.getLifePts() == points){
                            this.target = z;
                            if(this.execute()) {
                            	exit = true;
                           	 	return true;
                            }
                        }
                    }
                    System.out.println("Invalid input");
                  	continue;
                }else {
               	 	System.out.println("Invalid input");
               	 	continue;
                }
        	}
              
            /*int id = random.nextInt(board.getBoard()[x][y].getZombies().size());
            Zombie zombie = board.getBoard()[x][y].getZombies().get(id);
            this.target = zombie;
            this.execute();
            return true;*/
        }else if(board.getBoard()[x][y].getZombies().size()==1){
            Zombie zombie = board.getBoard()[x][y].getZombies().get(0);
            this.target = zombie;
            this.execute();
            return true;
        }
           return false;
    }

    /**
     * Initiates a distant attack by the player on zombies within weapon range .
     * @param board The game board.
     * @return true if a target is found and attacked, false otherwise.
     */
    public boolean distantAttack(Board board){
        if(weapon.getMaxRange()>0){
        	
            int x = player.getPlayerCell().getCoordinateX();
            int y = player.getPlayerCell().getCoordinateY();
            if(player.getPlayerCell().getZombies().size()!=0) {
        		if(this.findTarget(board, x,y)){
                    return true;
                }
        	}else {
        		if(!(player.getPlayerCell() instanceof Street)){
    	            for(int i=weapon.getMinRange(); i<weapon.getMaxRange()+1;i++){
    	                if(x-i>=0){
    	                    Door door = board.getDoor(x, y, "left");
    	                    if(door != null && door.isOpen()){
    	                        if(this.findTarget(board, x-i,y)){
    	                            return true;
    	                        }
    	                    }
    	                }
    	                if(x+i<board.getWidth()){
    	                    Door door = board.getDoor(x, y, "right");
    	                    if(door != null && door.isOpen()){
    	                        if(this.findTarget(board, x+i,y)){
    	                            return true;
    	                        }
    	                    }
    	                }
    	                if(y-i>=board.getHeight()){
    	                    Door door = board.getDoor(x, y, "up");
    	                    if(door != null && door.isOpen()){
    	                        if(this.findTarget(board, x,y-i)){
    	                            return true;
    	                        }
    	                    }
    	                }
    	                if(y+i<board.getHeight()){
    	                    Door door = board.getDoor(x, y, "down");
    	                    if(door != null && door.isOpen()){
    	                        if(this.findTarget(board, x,y+i)){
    	                            return true;
    	                        }
    	                    }
    	                }
    	                
    	            }
                }else{
                	for(int i=weapon.getMinRange(); i<weapon.getMaxRange()+1;i++){
                        if(x-i>=0){
                            if(this.findTarget(board, x-i,y)){
                                return true;
                            }
                        }
                        if(x+i<board.getWidth()){
                            if(this.findTarget(board, x+i,y)){
                                return true;
                            }
                        }
                        if(y-i>=board.getHeight()){
                            if(this.findTarget(board, x,y-i)){
                                return true;
                            }
                        }
                        if(y+i<board.getHeight()){
                            if(this.findTarget(board, x,y+i)){
                                return true;
                            }
                        }
                	}
                }
        	}
            
            
            
        }
        System.out.println("No zombie arround");
        return false;
    }
    
}

