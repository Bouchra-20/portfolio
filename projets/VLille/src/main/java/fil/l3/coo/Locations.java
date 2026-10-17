package fil.l3.coo;

import fil.l3.coo.NotificationController.ClientNotifications;

/**
 * The `Locations` interface defines the contract for objects that represent rentable locations,
 * specifically for a bicycle rental system. The implementing classes are expected to manage the
 * rental, service state, deposit status, and other attributes related to the rentable item.
 */
public interface Locations{
     /**
     * Rents the item represented by this instance.
     * Throws an exception if the rental operation fails.
     * @throws Exception if the rental operation cannot be performed.
     */
     public void Louer() throws Exception;
     /**
     * Retrieves the item from its current location.
     * Throws an exception if the retrieval operation fails.
     * @return the `Locations` instance that was retrieved.
     * @throws Exception if the retrieval operation cannot be performed.
     */
     public Locations Retier() throws Exception;
     /**
     * Checks if the item is currently rented.
     * @return `true` if the item is rented, `false` otherwise.
     */
     public boolean isLouee();
     /**
     * Gets the rental price for this item.
     * @return the rental price as a `double`.
     */
     public double getPrixLocation();
     /**
     * Checks if the item is in service.
     * @return `true` if the item is in service, `false` otherwise.
     */
     public boolean getEtatService();
     /**
     * Gets the number of times this item has been rented.
     * @return the number of rentals as an integer.
     */
     public int get_NbLocation();
     /**
     * Sets the number of times this item has been rented.
     * @param nb the number of rentals to be set.
     */
     public void set_NbLocation(int nb);
     /**
     * Changes the service state of this item, marking it as in or out of service.
     */
     public void ChangeStateService();
     /**
      * Gets the caution or deposit amount required for renting this item.
      * @return the caution amount as a `double`.
      */
     public double getCaution();
     /**
     * Gets the unique identifier for this rentable item.
     * @return the unique ID as an integer.
     */
     public int getIdDeuxRoues();
     /**
     * Checks if the item has been deposited in a station.
     * @return `true` if the item is deposited, `false` otherwise.
     */
     public boolean isDepose();
     /**
     * Sets the deposit status of the item.
     * @param f `true` if the item is deposited, `false` otherwise.
     */
     public void setDeposer(boolean f); 
     /**
     * Checks if the item is marked as stolen.
     * @return `true` if the item is stolen, `false` otherwise.
     */
     public boolean estVole();
     /**
     * Marks the item as stolen.
     */
     public void setToVole();
     /**
     * Checks if the item is out of service.
     * @return `true` if the item is out of service, `false` otherwise.
     */
     public boolean isHors_service();
     /**
     * Compares this item to another `Locations` item.
     * @param other the other `Locations` instance to be compared.
     * @return a negative integer, zero, or a positive integer as this item is less than,
     * equal to, or greater than the specified item.
     */
    
     public int getId_prod();
     /**
     * Requests an intervention for the item (e.g., for repair).
     * @throws Exception if the intervention request cannot be performed.
     */
     public void askForIntervention() throws Exception ;


     /**
      * Sets the service state of the item.
      */
     public void setEtatSerice() ; 

     /**
      * Sets the notification system for this Locations object.
      * @param notif the ClientNotifications instance to use for notifications.
      */
     public void setSystemeNotif(ClientNotifications notif);

     /**
      * Notifies the client with the given notification message.
      * @param notification the message to be sent to the client.
      */
     public void notifyClient(String notification);

     public void setLouee(boolean f);
}
