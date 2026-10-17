package Cells;
import java.util.*;
import Equipments.HealVial;
import Cells.Room;

/**
 * Constructor of the Pharmacie class
 */
public class Pharmacy extends Room {
    //protected List<Medication> medic; // Liste des médicaments dans la pharmacie

    
    
    /**
     * 
     * @param x Coordinate X of the pharmacy
     * @param y Cootdinate Y of the pharmacy
     */
    public Pharmacy(int x, int y) {
        super(x,y);//pour appeler le constructeur de la classe parente et passer les paramètres reçus à celui-ci
        //this.medic = medic;
    }
    
    /**
     * refresh the meds in  the pharmacy
     */
    public void regenerateMed() {
        // Implement med regeneration logic
        // This could involve updating the quantity or status of medications in the medication list
    	this.addEquipment(new HealVial());
    }
    public String toString(){
		return "[Ph]";
	}

    // Additional Methods for Managing Medications
}
