package fil.l3.coo.StateController.DeuxRoueState;

import fil.l3.coo.DeuxRoues;
import fil.l3.coo.V_lille;
import fil.l3.coo.ExceptionsControlled.NotInService;
/**
 * Represents the state of a two-wheeled vehicle that is in service and available for rental.
 * This class implements the DeuxRoueState interface to define behavior
 * specific to an operational vehicle.
 */
public class OnService implements DeuxRoueState {
    /**
     * Rent the vehicle.
     * @param s The DeuxRoues object representing the vehicle
     * @throws NotInService if the vehicle is already rented
     * @throws Exception for any other unexpected errors
     */
    @Override
    public void Louer(DeuxRoues s) throws Exception{
        if(! s.isLouee()){
            if(s.getNb_location() < s.getLimite_location()){
                s.setNb_location(s.getNb_location()+1);
                s.setLouee(true); 
            }else{
                s.askForIntervention();
                throw new NotInService(s.getId_prod());
            }
        }else{
            throw new NotInService(s.getId_prod());
        } 
    }
    /**
     * Retrieves the vehicle from its current location.
     * @param s The DeuxRoues object representing the vehicle
     * @return The same DeuxRoues object, indicating successful retrieval
     * @throws Exception for any unexpected errors during retrieval
     */
    @Override
    public DeuxRoues Retirer(DeuxRoues s) throws Exception{
        return s ; 
    }
}
