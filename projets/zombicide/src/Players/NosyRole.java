package Players;
import java.util.*;
import Equipments.*;

public interface NosyRole {
    
    // Task that  the task
    //public void dothetask();

    /**
     * search in the Room method 
     * 
     * 
     *  */
    public default void search() {
    	//
    }
    /**
     * 
     *  
     * 
     * */
    
    
    //public void useEquipment(Equipment equip);
    
    //public void useGlasses() {
    //
    //}
    
    /**
     * @return a new list
     * */
    public default List<Equipment> searchEquipments(){
         // code de recherche 
         return new ArrayList<>();
    }

    

}
