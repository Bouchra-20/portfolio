package fil.l3.coo.NotificationController;

import fil.l3.coo.AccesController.AccesForReparation;
/**
 * Defines the interface for subscribers in the notification system of the rental service.
 * This interface is part of the Observer pattern implementation, allowing objects to receive
 * updates and react to specific events in the system.
 */
public interface Subscriber {
    /**
     * Receives and processes updates from the system.
     * @param s A string containing the update or notification message
     */
    void update(String s);
    /**
     * Requests an intervention for a vehicle that needs repair or maintenance.
     * @param i An object implementing AccesForReparation, representing the vehicle needing intervention
     * @throws Exception if the intervention request cannot be processed
     */
    public void askForIntervention(AccesForReparation i)throws Exception ;

}
