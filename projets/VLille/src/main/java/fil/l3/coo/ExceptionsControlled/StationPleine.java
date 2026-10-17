package fil.l3.coo.ExceptionsControlled;

/**
 * Represents an exception that is thrown when a station is full and cannot accept any more items.
 */
public class StationPleine extends Exception{
    /**
     * Constructs a new {@code StationPleine} exception with the message "FULL STATION !!".
     */
    public StationPleine(){
        super("FULL STATION !! ");
    }
    
}
