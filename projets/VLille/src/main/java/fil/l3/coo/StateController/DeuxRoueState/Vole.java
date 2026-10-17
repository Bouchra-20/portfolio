package fil.l3.coo.StateController.DeuxRoueState;

import java.time.LocalDate;

import fil.l3.coo.DeuxRoues;
import fil.l3.coo.ExceptionsControlled.NotInService;
/**
 * Represents the state of a stolen two-wheeled vehicle in the rental system.
 * This class implements the DeuxRoueState interface to define behavior
 * specific to a stolen vehicle.
 */
public class Vole implements DeuxRoueState{
    /**
     * Attempts to rent a stolen vehicle.
     * @param s The DeuxRoues object representing the vehicle
     * @throws NotInService Always throws this exception as a stolen vehicle cannot be rented
     */
    @Override
    public void Louer(DeuxRoues s) throws Exception{
        throw new NotInService(s.getIdDeuxRoues()); 
    }
    /**
     * Attempts to retrieve a stolen vehicle.
     * @param s The DeuxRoues object representing the vehicle
     * @return Never returns as it always throws an exception
     * @throws NotInService Always throws this exception as a stolen vehicle cannot be retrieved
     */
    @Override
    public DeuxRoues Retirer(DeuxRoues s) throws Exception{
        throw new NotInService(s.getIdDeuxRoues()); 
    }
    
}
