package fil.l3.coo.ExceptionsControlled;

/**
 * Represents an exception that is thrown when a requested ID is not available.
 */
public class IdNotFound extends Exception {
    /**
     * Constructs a new {@code IdNotFound} exception with the message "GIVEN ID NOT AVAILABLE !".
     */
    public IdNotFound(){
        super("GIVEN ID NOT AVAILABLE !");
    }
}
