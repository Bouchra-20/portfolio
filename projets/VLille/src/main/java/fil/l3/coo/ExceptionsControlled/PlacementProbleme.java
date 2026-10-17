package fil.l3.coo.ExceptionsControlled;

/**
 * Represents an exception that is thrown when there is a problem with the placement of an object.
 * This exception is used to indicate that an operation cannot be performed on the current location.
 */
public class PlacementProbleme extends Exception {
    /**
     * Constructs a new {@link PlacementProbleme} exception with the message
     * "VOUS NE POUVEZ PAS EFFECTUER CETTE OPERATION SUR CET EMPLACEMENT".
     */
    public PlacementProbleme(){
        super("VOUS NE POUVEZ PAS EFFECTUER CETTE OPERATION SUR CET EMPLACEMENT");
    }
    
}
