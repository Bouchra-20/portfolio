package Players;
import java.util.*;

public interface HealerRole {

    // A default method to heal a player, adding 1 health point
    default boolean heal(Player target) {
    	if(target.getPlayerCell().players().size()==1){
    		if(target.getLifeLevel() < target.getMaxLifeLevel()) {
    			target.setLifeLevel(target.getLifeLevel() + 1);
    			System.out.println(target.getRole()+" has been treated. Now he has "+target.getLifeLevel()+" life points");
    			return true;
    		}else {
    			System.out.println(target.getRole()+" is already at his full lifelevel.");
    			return false;
    		}
            
        }else{
            Scanner scanner = new Scanner(System.in);
            boolean fin = false;
            while(!fin){
                System.out.println("Choose the player to heal or quit to leave");
                for (Player p : target.getPlayerCell().players()) {
                    System.out.print(p.getRole()+" ");
                }
                System.out.println("");
                
                String role = scanner.nextLine();
                if(role.equals("quit")){
                    return true;
                }else{
                    for (Player p : target.getPlayerCell().players()) {
                        if(role.equals(p.getRole())){
                        	if(p.getLifeLevel() < p.getMaxLifeLevel()) {
                    			p.setLifeLevel(p.getLifeLevel() + 1);
                    			System.out.println(p.getRole()+" has been treated. Now he has "+p.getLifeLevel()+" life points");
                    			return true;
                        	}else {
                    			System.out.println("The player is already at his full lifelevel.");
                    			return false;
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
    }
    default void useHealvial(){
        
    }
}