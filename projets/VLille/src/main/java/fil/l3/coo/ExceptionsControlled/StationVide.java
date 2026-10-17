package fil.l3.coo.ExceptionsControlled;

/**
 * Represents an exception that is thrown when a station is empty.
 */
public class StationVide extends Exception{
    /**
     * Constructs a new {@code StationVide} exception with the message "EMPTY STATION !!".
     */
    public StationVide(){
        super("EMPTY STATION !! ");
    }
    
}
