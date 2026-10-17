package fil.l3.coo.StateController.StationState;

import java.util.List;

import fil.l3.coo.*;
/**
 * Interface representing the state of a station in a vehicle rental system.
 * This interface defines the operations that can be performed on a station
 * based on its current state.
 */
public interface StationState {
    /**
     * Deposits a vehicle into the station.
     * @param l The vehicle to be deposited
     * @param s The station where the vehicle is being deposited
     * @throws Exception if the deposit operation cannot be completed
     */
    public void Deposer( Locations l , Station s) throws Exception ; 
    /**
     * Retrieves a vehicle from the station.
     * @param s The station from which to retrieve the vehicle
     * @return The retrieved vehicle
     * @throws Exception if no vehicle can be retrieved
     */
    public Locations Retirer(Station s) throws Exception ;
    /**
     * Gets a list of available spaces in the station.
     * @return List of available Emplacement objects
     */
    public List<Emplacement> placesAvailbale() ;
    /**
     * Processes a rental operation for a client at the station.
     * @param c The client renting a vehicle
     * @param s The station where the rental is taking place
     * @throws Exception if the rental operation cannot be completed
     */
    public void Louer(Client c , Station s) throws Exception;
    /**
     * Retrieves a specific vehicle from the station based on its ID.
     * @param id_prod The ID of the vehicle to be retrieved
     * @param s The station from which to retrieve the vehicle
     * @return The retrieved vehicle
     * @throws Exception if the specified vehicle cannot be retrieved
     */
    public Locations Retirer(int id_prod , Station s) throws Exception ;

}
