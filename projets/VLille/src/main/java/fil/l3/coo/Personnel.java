package fil.l3.coo;
/**
 * The `Personnel` class is an abstract class that represents a member of staff within the system.
 * It provides basic attributes and behavior common to all personnel, such as an ID, name, and surname.
 * Each `Personnel` object is uniquely identified by an automatically incremented ID.
 * 
 * This class implements the `Intervention` interface, indicating that it provides
 * certain behaviors related to interventions (though the implementation of these behaviors
 * is left to subclasses).
 */
public abstract class Personnel implements Intervention {
    /** Unique identifier for each personnel member.*/
    protected int ID_personnel ; 
    /** The last name of the personnel member.*/
    protected String nom ; 
    /** The first name of the personnel member.*/
    protected String prenom ; 
    /** Static field to track the current ID for personnel. This is automatically incremented
    /* with each new instance of the Personnel class.*/
    protected static int ID_actual =0;
    /**
     * Constructs a new `Personnel` object with a given first and last name.
     * Automatically assigns a unique ID to the personnel member.
     * @param Nom The last name of the personnel member.
     * @param Prenom The first name of the personnel member.
     */
    public Personnel( String Nom  , String Prenom){
            this.ID_personnel = ID_actual;
            this.nom = Nom ; 
            this.prenom = Prenom ;
            Inc_idactual();
    }
    /**
     * Increments the static `ID_actual` counter, ensuring that each personnel
     * member has a unique identifier.
     */
    private static void Inc_idactual(){
        ID_actual++;
    }

    
}
