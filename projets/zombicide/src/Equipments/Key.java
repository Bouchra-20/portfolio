package Equipments;
import Cells.*;

public class Key extends Equipment<Door> {
   
    protected int keytoPad;
    
    /**
     * Constructs a key a specified type key.
     * @param typekey the type of key associated with the key 
     */
    public Key() {
        super();
    }

    /**
     * Unlocks the specified door if the key matches the door's padlock
     * @param door the door to be unlocked 
     */
    public boolean Effect(Door door) {
    	if (!door.isOpen()){
    		System.out.println("\u001B[31m"+"Unlock the door."+ "\u001B[0m ");
            door.toOpen();
            this.use();
            return true;
        }else{
            System.out.println("\u001B[36m"+"The door is already unlocked."+ "\u001B[0m ");
            return false;
        }
    }
    
    public String symbole(){
        return "🔑";
    }
    
    public String toString(){
        return "Key";
    }
    

}
