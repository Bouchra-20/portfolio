package Cells;
import java.util.*;

public class Door {
	
    private int doornbr;
    private boolean open;
    // door direction
    private Dirc dirc;
    protected Room room;
    //private boolean locked;
    /** 
    * Door constructor 
    @param doornbr the door's number
    @param room the room
    */ 
    public Door(int doornbr, Room room, Dirc dirc) {
        this.open = false; // the door is initially closed
        //this.locked = true;
        this.doornbr = doornbr;
        this.room = room;
        this.dirc = dirc;
    }
    public Door(int doornbr, Room room ) {
        this.open = false; // the door is initially closed
        //this.locked = true;
        this.doornbr = doornbr;
        this.room = room;
    }

    /**
     * sets the door direction
     * @param dirc
     */
    public void setDoordirc(Dirc dirc){
        this.dirc = dirc;
    }
    /**
     * get the door direction
     * @param dirc
     * @return the door direction 
     */
    public Dirc getDoordirc(Dirc dirc){
        return this.dirc = dirc;
    }
    public Door() {
        this.open = false; // the door is initially closed//this.locked = true;
    }


    // opening door method 
    public void toOpen() {
        open = true;
    }
    // closing door Method 
    public void toClose() {
        open = false;
    }
    // Method to check if the door is open".
    public boolean isOpen() {
        return open;
    }
    /**
     * setter method to attribute a number to a door
     * @param nbr the door's number
     */
    public void setDoorNbr(int nbr){
        this.doornbr=nbr;
    }
    /**
     * method that returns the door's number
     * @return door number
     */
    public int getDoorNbr(){
        return this.doornbr;
    }
    public Room getRoom(){
        return this.room;
    }

    /**
     * method that sets the doors Cadna 
     * @param cad the cadna 
     */
    /** 
    public void setCadna(String cad){
         this.cadna = cad;
    }
    */
    /**
     * method that returns the door's Cadna
     * @return cadna 
     */
    /** 
    public String getCadna(){
        return this.cadna;

    }
    */
    
    /**
     * method that verifies if the door is locked or not 
     * @return boolean 
     */
    /*public boolean isLocked(){
        return this.locked == true;    
    }

    /**
     * method that unlocks the door 
     */
    /*public void unlock(){
        this.locked = false;    
    }

    */
    
}

