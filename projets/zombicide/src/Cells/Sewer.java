package Cells;
import java.util.*;
import Players.*;

public class Sewer {
    protected int numbrZomb; // Number of zombies in the sewers.
    protected int sewerZombout; // Number of zombies that can come out of this sewer.
    protected int zombPasses; // Number of zombies that have already passed.
    protected List<Zombie> zombie;
    protected Cell cell;

    /**
     * Constructor of the Sewer class.
     * @param numbrZomb   Number of zombies in the sewers.
     * @param sewerZombout Number of zombies that can come out of this sewer.
     */

    public Sewer() {
        //this.numbrZomb = numbrZomb;
        //this.sewerZombout = sewerZombout;
    	this.zombPasses = 0;
        this.zombie = new ArrayList<>();
    }

    /**
    *Get the number of zombies in the sewers
    *@return numbrZomb
     */ 
    public int getNumbrZomb() {
        return numbrZomb;
    }

    /**
    * Set the number of zombies in the sewers
    * @param numbrZomb  Number of zombies in the sewers.
    */
    public void setNumbrZomb(int numbrZomb) {
        if (numbrZomb >= 0) {
            this.numbrZomb = numbrZomb;
        }
    }

    /** 
    * Get the number of zombies that can come out of this sewer.
    * @return egoutZombout 
    */
    public int getSewerZombout() {
        return sewerZombout;
    }

    /** 
     * Set the number of zombies that can come out of this sewer.
     * @param sewerZombout Number of zombies that can come out of this sewer.
     */
    public void setSewerZombout(int sewerZombout) {
        if (sewerZombout >= 0) {
            this.sewerZombout = sewerZombout;
        }
    }

    
    /**
     * Allow a specified number of zombies to pass through.
     * @param nombreDeZombies   Number of zombies in the sewers.
     * @return boolean
     */
    public boolean letZombiePass(int nombreDeZombies) {
    	if (zombPasses+ nombreDeZombies <= sewerZombout){
    		zombPasses += nombreDeZombies;
    		return true; // Allow zombies to pass
    	}else {
    		return false; // Refuse the passage of more zombies
    	}
    }

    /**
     * Get the number of zombies that passed.
     * @return zombPasses
     */
    public int getZombPasses() {
    	return this.zombie.size();
    }

    public String toString(){
		return "[Sw]";
	}
    
    /**
     * 
     * @param z
     */
    public void addZombie(Zombie z) {
    	this.zombie.add(z);
    }
    
    /**
     * 
     * @param cell
     */
    public void setCell(Cell cell){
        this.cell=cell;
    }
    /**
     * 
     * @return
     */
    public Cell getCell(){
        return this.cell;
    }

    
}
