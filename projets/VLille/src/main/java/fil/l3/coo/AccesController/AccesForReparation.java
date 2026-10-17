package fil.l3.coo.AccesController;

/**
 * Provides an interface for accessing and managing the state of a service.
 * This interface is used to interact with the service's state, such as setting the service's state.
 * The interface also provides a method to retrieve the ID of the associated product.
 */
public interface AccesForReparation {
    /**
     * Sets or updates the service state of the item.
     * This method is likely used to toggle between in-service and out-of-service states.
     */
    void setEtatSerice();
    /**
     * Retrieves the unique identifier of the product (rental item).
     * @return The ID of the product
     */
    int getId_prod();
}
