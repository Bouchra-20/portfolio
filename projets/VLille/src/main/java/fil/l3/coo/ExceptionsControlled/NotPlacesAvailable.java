package fil.l3.coo.ExceptionsControlled;

/**
 * Represents an exception that is thrown when there are no available places to add.
 * This exception is used to indicate that a requested operation cannot be completed
 * because there are no available places to add.
 */
public class NotPlacesAvailable extends Exception {
    /**
     * Constructs a new `NotPlacesAvailable` exception with the message "Not places available to add !!".
     */
    public NotPlacesAvailable(){
        super("Not places available to add !!");
    }
    
}
