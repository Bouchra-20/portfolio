package Actions;
import java.util.*;

import Equipments.*;
import Players.Player;

public class TakeOnHand {

	private Player player;
     /**
     * constructs a TakeOnHand action object for a given player .
     * @param player the  player for whom the action is being  executed .
     */
    public TakeOnHand(Player player){
        this.player = player;
    }
    
    /**
     * Executes equipment operation
     * @return true on success ,false otherwise
      */
    public boolean execute(){
    	if(player.getEquipments().size()==0){
            System.out.println("\u001B[36m"+"You don't have any equipment "+ "\u001B[0m ");
            return false;
        }
    	else if(player.getEquipments().size()==1){
            Equipment equipment = player.getEquipments().get(0);
            player.getEquipments().remove(0);
            exchange(equipment);  
            return true;
        }else{
                
            Boolean exist = false;
            System.out.print("Choose the equipment to take or 'exit' to quit : ");
            while (!exist) {
                for(Equipment equipment : player.getEquipments()){
                    System.out.print(equipment.toString()+", ");
                }
                System.out.println();
                Scanner scanner = new Scanner(System.in);
                String type = scanner.nextLine();
                if(type.equals("exit")){
                    return false;
                }
                for(Equipment equipment : player.getEquipments()){
                    /*if(equipment instanceof Weapon){
                        if(type.equals(equipment.toString())) {
                            player.getEquipments().remove(equipment);
                            exchange(equipment);
                            exist = true;
                            return true;
                        }
                    }else{*/
                        if(type.equals(equipment.toString())){
                            player.getEquipments().remove(equipment);
                            exchange(equipment);
                            exist = true;
                            return true;
                        }
                    //}   
                }
                if(!exist){
                    System.out.println("Please enter a valid type of equipment or 'exit' to quit");
                    continue;
                }
            }
        }
    	return false;
    }
    
    /**
     * manages equipment exchange
     * @param equipment target equipment
     */
    public void exchange(Equipment equipment){
        if(equipment instanceof Weapon){
            if(player.getLeftHandItem() != null){
                Weapon weapon = player.getLeftHandItem();
                player.setEquipment(weapon);
                player.setLeftHandItem((Weapon) equipment);
                System.out.println("\u001B[31m"+weapon.toString() + " moves to the bag and "+ equipment.toString() +" to the left hand"+"\u001B[0m");
            }else{
                player.setLeftHandItem((Weapon) equipment);
                System.out.println("\u001B[31m"+equipment.toString() + "moves to the left hand"+"\u001B[0m");
            }
        }else{
            if(player.getRightHandItem() != null){
                Equipment eq = player.getRightHandItem();
                player.setEquipment(eq);
                player.setRightHandItem(equipment);
                System.out.println("\u001B[31m"+eq.toString() + " moves to the bag and "+ equipment.toString() +" to the right hand"+"\u001B[0m");
            }else{
                player.setRightHandItem(equipment);
                System.out.println("\u001B[31m"+equipment.toString() + " moves to the right hand"+"\u001B[0m");
            }
        } 

    }
    
}
