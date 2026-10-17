package Equipments;

import Players.Player;
import java.util.*;

public class FirstAidKit extends Equipment<Player> {

    private int healingPotions;
    
    /**
     * Constructs a first aid kit with a specified number of healing 
     * @param healingPotions healingPotions the number of healing potions contained  
     */
    public FirstAidKit() {
        super();
    }
    
    /**
     * A method that heals completely the player by using 
     * all the potions contained in the FirstAidKit.
     * @param charac the player 
     */
    public boolean Effect(Player player) {
    	if(player.getPlayerCell().players().size()==1){
    		System.out.println("\u001B[36m"+"No player around"+ "\u001B[0m ");
    		return false;    
        }else{
            Scanner scanner = new Scanner(System.in);
            boolean fin = false;
            while(!fin){
                System.out.println("Choose the player to heal or quit to leave");
                for (Player p : player.getPlayerCell().players()) {
                    if(!p.equals(player)){
                        System.out.print(p.getRole()+" ");
                    }
                }
                System.out.println("");
                
                String role = scanner.nextLine();
                if(role.equals("quit")){
                    return true;
                }else{
                    for (Player p : player.getPlayerCell().players()) {
                        if(!p.equals(player)){
                            if(role.equals(p.getRole())){
                                if(p.getLifeLevel() < p.getMaxLifeLevel()) {
                                    p.setLifeLevel(p.getLifeLevel() + 1);
                                    System.out.println("\u001B[31m"+p.getRole()+" has been treated. Now he has "+p.getLifeLevel()+" life points"+ "\u001B[0m ");
                                    return true;
                                }else {
                                	System.out.println("\u001B[36m"+"The player is already at his full lifelevel."+ "\u001B[0m ");
                                    return false;
                                }                            
                            }
                        }
                    }
                    if(!fin){
                        System.out.println("Wrong input! Please try again.");
                        continue;
                    }
                }
            }
            return false;
        }
    	
    	/*
    	// Implement treatment logic
        int life = charac.getLifeLevel();
        int maxlifelevel = charac.getMaxLifeLevel();
        int potionsNeeded = (maxlifelevel - life);
        if ((potionsNeeded ) <= healingPotions){
            charac.setLifeLevel(life + potionsNeeded);
        } else{
            charac.setLifeLevel(life + healingPotions);
        }
        //charac.setLifeLevel();
        System.out.println("Healing a survivor by 1 health point.");
        //Involves selecting survivors and modifying their health points
        this.use();
        return true;*/
    }
    
    /**
     * Retrieves the number of healing potions contained in the first aid kit.
     * @return the umber of healing potions 
     */
    public int getNumbersofPotions(){
        return this.healingPotions;    }
    
    /**
     * Sets the number of healing potions 
     * @param n the number of potions to set
     */    
    public void setNumberofPotions(int n ){
        this.healingPotions = n; 
    }
    
    /**
     * The player uses only a Potion from the FirstAidKit 
     * @param charac the player using the potion
     * @throws MaxLifeLevelException if the player's life level is already at maximum
     */
    public void usePotion(Player charac)throws MaxLifeLevelException{
        int life = charac.getLifeLevel();
        int maxlifelevel = charac.getMaxLifeLevel();
        int numb = this.getNumbersofPotions();
        if ( this.getNumbersofPotions() != 0 ){
            if ( life < maxlifelevel){
            charac.setLifeLevel (life+1);
            this.setNumberofPotions(numb-1);
            }
            else{
                throw new MaxLifeLevelException();
            }
        }

    }
    
    public String symbole(){
        return "🧰";
    }
    
    public String toString(){
        return "First aid kit";
    }

}
