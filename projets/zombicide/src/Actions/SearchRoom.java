package Actions;

import java.util.*;

import Cells.*;
import Equipments.*;
import Players.Player;

public class SearchRoom {

	private Player player;
	
    /**
     * constructs a SearchRoom object associated with a specific player .
     * @param player the player performing the search action.
     */
    public SearchRoom(Player player){
        this.player = player;
    }
	
    /**
     * executes the search action in the  current room .
     * @return true if the search action is successful and equipment is found, false otherwise.
     */
    public boolean execute(){
        Cell cell = player.getPlayerCell();
        if(cell.equipments().size()==1){
            Equipment equipment = cell.equipments().get(0);
            for (Equipment playerEquipment : player.getEquipments()) {
                if (playerEquipment.toString().equals(equipment.toString()) ) {
                	System.out.println("\u001B[36m"+"You already have this equipment."+ "\u001B[0m "); 
                    return false;
                }
            }
            if((player.getLeftHandItem()!= null && player.getLeftHandItem().equals(equipment)) || (player.getRightHandItem()!= null && player.getRightHandItem().equals(equipment))){
                System.out.println("\u001B[36m"+"You already have this equipment."+ "\u001B[0m "); 
                return false;
            }else{
                canPickUp(equipment); 
                return true;
            }     
        }else{
            Boolean exist = false;
            System.out.print("Choose the equipment to take or 'exit' to quit: ");
            while (!exist) {
                for(Equipment equipment : cell.equipments()){
                    System.out.print(equipment.toString()+" ");
                }
                System.out.println();
                Scanner scanner = new Scanner(System.in);
                String type = scanner.nextLine();
                if(type.equals("exit")) {
                	return false;
                }
                for(Equipment equipment : cell.equipments()){
                    //if(equipment instanceof Weapon){
                        if(type.equals(equipment.toString())) {
                        	for (Equipment playerEquipment : player.getEquipments()) {
                                if (playerEquipment.equals(equipment) ) {
                                	System.out.println("\u001B[36m"+"You already have this equipment."+ "\u001B[0m "); 
                                    return false;
                                }
                            }
                            if((player.getLeftHandItem() != null && player.getLeftHandItem().toString().equals(equipment.toString())) || (player.getRightHandItem() != null && player.getRightHandItem().toString().equals(equipment.toString()))){
                                System.out.println("\u001B[36m"+"You already have this equipment."+ "\u001B[0m "); 
                                return false;
                            }else{
                                canPickUp(equipment);
                                exist = true;
                                return true;
                            }
                        }
                    /*}else{
                        if(type.equals(equipment.toString())) {
                        	for (Equipment playerEquipment : player.getEquipments()) {
                                if (playerEquipment.equals(equipment) ) {
                                	System.out.println("You already have this equipment."); 
                                    return false;
                                }
                            }
                            if(player.getLeftHandItem().equals(equipment) || player.getRightHandItem().equals(equipment)){
                                System.out.println("You already have this equipment."); 
                                return false;
                            }else{
                                canPickUp(equipment);
                                exist = true;
                                return true;
                            }
                        }   
                    }*/
                }
                if(!exist){
                    System.out.println("Please enter a valid type of equipment or 'exit' to quit");
                    continue;
                }
            }
            return false; 
        }
    }
     
    /**
     * Handles the process of picking up equipment from the room and adding it to the player's inventory.
     * @param equipment The equipment to be picked up.
     */
	public void canPickUp(Equipment equipment){
        Cell cell = player.getPlayerCell();
        if(player.getEquipments().size()<5){
            player.PickUp(equipment);
            cell.takeEquipment(equipment);
        }else{
            System.out.println("Your bag is full, choose the equipment to throw away :");
            Boolean exist = false;
            while (!exist) {
                for(Equipment equip : player.getEquipments()){
                    System.out.print(equip.toString()+" ");
                }
                System.out.println();
                Scanner scanner = new Scanner(System.in);
                String type = scanner.nextLine();
                for(Equipment equip : player.getEquipments()){
                    if(equipment instanceof Weapon){
                        if(type.equals(equip.toString())) {
                            cell.takeEquipment(equipment);
                            cell.addEquipment(equip);
                            player.getEquipments().remove(equip);
                            player.PickUp(equipment);
                            exist = true;
                            break;
                        }
                    }else{
                        if(type.equals(equip.toString())){
                            cell.takeEquipment(equipment);
                            cell.addEquipment(equip);
                            player.getEquipments().remove(equip);
                            player.PickUp(equipment);
                            exist = true;
                            break;
                        }
                    }   
                }
                if(!exist){
                    System.out.println("Please enter a valid type of equipment");
                    continue;
                }
            }
        }
    }
}
