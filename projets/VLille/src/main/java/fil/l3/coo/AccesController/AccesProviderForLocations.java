package fil.l3.coo.AccesController;
import fil.l3.coo.Locations;
/**
 * Defines an interface for providing access to essential operations and information
 * for rental locations (vehicles or items) in the system.
 * This interface acts as a contract for classes that represent rentable items,
 * ensuring they provide necessary methods for the rental process.
 */
public interface AccesProviderForLocations {
   /**
     * Retrieves the unique identifier of the two-wheeled vehicle.
     * @return The ID of the vehicle
     */
   public int  getIdDeuxRoues();
   /**
     * Marks the vehicle as stolen.
     */
   public void setToVole();
   /**
     * Retrieves the deposit amount required for renting the vehicle.
     * @return The deposit amount
     */
   public double  getCaution();
   /**
     * Checks if the vehicle is currently in service.
     * @return true if the vehicle is in service, false otherwise
     */
   public boolean getEtatService();
   /**
     * Initiates the rental process for this vehicle.
     * @throws Exception if the rental operation cannot be completed
     */
   public void Louer() throws Exception;
   /**
     * Retrieves the vehicle from its current location.
     * @return The Locations object representing the retrieved vehicle
     * @throws Exception if the retrieval operation cannot be completed
     */
   public Locations Retier() throws Exception;
   /**
     * Gets the rental price for this vehicle.
     * @return The rental price
     */
   public double getPrixLocation();

    
}
