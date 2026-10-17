package fil.l3.coo;

import fil.l3.coo.StateController.DeuxRoueState.HorsService;
import fil.l3.coo.StateController.DeuxRoueState.OnService;

/**
 * Represents a bicycle with a rental price, service status, and deposit.
 * The bicycle's state is determined by whether it is in service or out of service.
 */
public class Velo extends DeuxRoues {
    /**
     * Constructs a new Velo object with the specified rental price, service status, and deposit.
     *
     * @param prix_location the rental price of the bicycle
     * @param hors_service true if the bicycle is out of service, false if it is in service
     * @param caution the deposit amount for the bicycle
     */
    public Velo ( double prix_location , Boolean hors_service , double caution ){
        super();
        this.prix_location = prix_location ;
        this.hors_service = hors_service ; 
        this.caution = caution ;
        if( this.hors_service){
            this.state = new HorsService();
        }else{
            this.state = new OnService();
        }    
    }

    public String toString(){
        return "🚲";
    }
}
