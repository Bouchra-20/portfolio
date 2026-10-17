package fil.l3.coo.ExceptionsControlled;
/**
 * Represents an exception that is thrown when a client does not have enough money.
 * This exception is used to indicate that a financial transaction cannot be completed
 * due to insufficient funds.
 */
public class NotEnoughMoneyException extends Exception {
    /**
     * Constructs a new {@code NotEnoughMoneyException} with the specified client ID.
     * @param id_client the ID of the client who does not have enough money
     */
    public NotEnoughMoneyException (int id_client){
        super("The Client :"+id_client+"does not have enough sold !");
    }
}
