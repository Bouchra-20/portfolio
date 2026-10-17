package fil.l3.coo.StateController.DeuxRoueState;

import fil.l3.coo.DeuxRoues;
import fil.l3.coo.ExceptionsControlled.NotInService;
/**
 * Represents the state of a two-wheeled vehicle that is out of service in the rental system.
 * This class implements the DeuxRoueState interface to define behavior
 * specific to a vehicle that is currently unavailable for use.
 */
public class HorsService implements DeuxRoueState{
    /**
     * Attempts to rent a vehicle that is out of service.
     * @param s The DeuxRoues object representing the vehicle
     * @throws NotInService Always throws this exception as an out-of-service vehicle cannot be rented
     */
    @Override
    public void Louer(DeuxRoues s) throws Exception{
        throw new NotInService(s.getIdDeuxRoues()); 
    }
    /**
     * Attempts to retrieve a vehicle that is out of service.
     * @param s The DeuxRoues object representing the vehicle
     * @return Never returns as it always throws an exception
     * @throws NotInService Always throws this exception as an out-of-service vehicle cannot be retrieved
     */
    @Override
    public DeuxRoues Retirer(DeuxRoues s) throws Exception{
        throw new NotInService(s.getIdDeuxRoues()); 
    }
}
