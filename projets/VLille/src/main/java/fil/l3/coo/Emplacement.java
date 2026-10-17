package fil.l3.coo ;

import fil.l3.coo.ExceptionsControlled.PlacementProbleme;
/**
 * The `Emplacement` class represents a specific spot in a station where a vehicle (e.g., a bicycle) can be placed or removed.
 * Each `Emplacement` has a unique ID and can be occupied or empty. It manages the assignment and retrieval of vehicles
 * and ensures that operations are properly handled based on the current status (occupied or empty).
 * Implements the `Comparable` interface to allow comparisons between different `Emplacement` objects based on their ID.
 */
public class Emplacement implements Comparable<Emplacement> {
    // The vehicle or item that is currently placed at this `Emplacement`.
    private Locations v  ;
    // Indicates whether the `Emplacement` is currently occupied.
    private boolean occupe ; 
    // A unique identifier for the `Emplacement`.
    private  int ID  ;
    // A static variable to keep track of the number of `Emplacement` objects created.
    private static int incrementer = 0 ;
    /**
     * Constructs an empty `Emplacement` and assigns it a unique ID.
     * Initially, the `Emplacement` is empty (not occupied) and does not contain any vehicle.
     */
    public Emplacement(){
        this.occupe = false ; 
        this.v = null;
        this.ID = incrementer;
        incrementID();
    }
    /**
     * Increments the static ID counter for the `Emplacement` class.
     * This method ensures that each `Emplacement` receives a unique ID.
     */
    static void incrementID(){
        incrementer++ ;
    }
    /**
     * Compares this `Emplacement` to another based on their unique IDs.
     * @param other the other `Emplacement` to be compared
     * @return a negative integer, zero, or a positive integer as this ID is less than, equal to, or greater than the specified object's ID
     */
    @Override
    public int compareTo(Emplacement other) {
        return Integer.compare(this.getID(), other.getID());
    }
    /**
     * Returns the unique ID of the `Emplacement`.
     * @return the unique identifier of the `Emplacement`
     */
    public int getID(){
        return this.ID ;
    }
    /**
     * Places a vehicle (`Locations` object) in this `Emplacement` if it is currently empty.
     * @param v the vehicle to be placed
     * @throws Exception if the `Emplacement` is already occupied
     */
    public void Deposer(Locations v) throws Exception{
        if(! this.occupe){
            this.v = v;
            this.v.setDeposer(true);
            this.occupe = true ;
            v.setLouee(false);
        }else{
            throw new PlacementProbleme();
        }
    }
    /**
     * Removes and returns the vehicle currently placed in this `Emplacement`, if it is occupied.
     * @return the vehicle (`Locations` object) that was placed in this `Emplacement`
     * @throws Exception if the `Emplacement` is empty
     */
    public Locations Retirer() throws Exception{
        if(this.occupe){
            if(this.v.estVole()){
                this.v.setDeposer(false);
                this.v = null;
                this.occupe = false ;
                return null ;
            }else{
                Locations e = this.v.Retier() ;
                e.setDeposer(false);
                this.v = null;
                this.occupe = false ;
                return e ;
            }
        }else{
            throw new PlacementProbleme();
        }
    }
    /**
     * Checks if the `Emplacement` is currently occupied.
     * @return `true` if the `Emplacement` is occupied; `false` otherwise
     */
    public boolean isOccupe(){
        return this.occupe ;
        }
    /**
     * Get the vehicle currently placed in this `Emplacement`.
     * @return the vehicle (`Locations` object) in this `Emplacement`, or `null` if empty
     */
    public Locations getLocations(){
        return this.v ;
    }
    /**
     * Provides a string representation of the `Emplacement`, including its ID and occupation status.
     * @return a formatted string with the ID and whether the `Emplacement` is occupied
     */
    public String toString() {
        return String.format("Emplacement ID: %d, Occupé: %s", this.ID, (this.occupe ? "Oui" : "Non"));
    }
}
