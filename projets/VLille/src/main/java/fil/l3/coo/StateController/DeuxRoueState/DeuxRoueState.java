package fil.l3.coo.StateController.DeuxRoueState;

import fil.l3.coo.DeuxRoues;
/**
 * Defines the interface for different states of a two-wheeled vehicle in the rental system.
 * This interface is part of the State pattern implementation, allowing for different
 * behaviors based on the current state of a vehicle.
 */
public interface DeuxRoueState {
    /**
     * Attempts to rent the vehicle in its current state.
     * @param s The DeuxRoues object representing the vehicle to be rented
     * @throws Exception if the rental operation cannot be completed in the current state
     */
    public void Louer(DeuxRoues s) throws Exception ;
    /**
     * Attempts to retrieve the vehicle from its current location in its current state.
     * @param s The DeuxRoues object representing the vehicle to be retrieved
     * @return The DeuxRoues object if the retrieval is successful
     * @throws Exception if the retrieval operation cannot be completed in the current state
     */
    public DeuxRoues Retirer(DeuxRoues s) throws Exception ;
}