package fil.l3.coo.ExceptionsControlled;

/**
 * Represents an exception that is thrown when a device is not in service.
 * This exception is used to indicate that a requested operation cannot be performed
 * because the device with the specified ID is not currently in service.
 */
public class NotInService extends Exception {
    /**
     * Constructs a new {@code NotInService} exception with the specified device ID.
     * @param id the ID of the device that is not in service
     */
    public NotInService(int id){
        super("The Device with ID ="+id+" is not in service .");
    }
}
