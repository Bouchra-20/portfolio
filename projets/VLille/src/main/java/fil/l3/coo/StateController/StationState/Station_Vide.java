package fil.l3.coo.StateController.StationState;
import fil.l3.coo.*;
import fil.l3.coo.ExceptionsControlled.*;
import java.util.*;
/**
 * Represents the state of an empty station in the vehicle rental system.
 * This class implements the StationState interface to define behavior
 * specific to an empty station.
 */
public class Station_Vide implements StationState{
    // The list of available parking spots (Emplacement) at the station.
    private List<Emplacement> places ;
    /**
     * Constructs a Station_Vide object with a list of available spaces.
     * @param places List of available spaces in the station
     */
    public Station_Vide(List<Emplacement> places){
        this.places = places ;
    }
    /**
     * Attempts to retrieve a vehicle from an empty station.
     * @param s The station
     * @throws StationVide Always throws this exception as the station is empty
     */
    public Locations Retirer(Station s) throws Exception {
        throw new StationVide() ;
    }
    /**
     * Attempts to retrieve a specific vehicle from an empty station.
     * @param id_prod The ID of the vehicle to retrieve
     * @param s The station
     * @throws StationVide Always throws this exception as the station is empty
     */
    public Locations Retirer(int id_prod ,Station s) throws Exception{
        throw new StationVide();
    }
    /**
     * Returns the list of available spaces in the station.
     * @return List of available Emplacement objects
     */
    public List<Emplacement> placesAvailbale(){
        return this.places ;
    }
    /**
     * Attempts to process a rental operation in an empty station.
     * @param c The client attempting to rent
     * @param s The station
     * @throws StationVide Always throws this exception as the station is empty
     */
    public void Louer(Client c , Station s) throws Exception{
        throw new StationVide();
    }
    /**
     * Deposits a vehicle into the empty station.
     * @param l The vehicle to deposit
     * @param s The station
     * @throws NotPlacesAvailable if there are no available spaces
     */
    @Override
    public void Deposer( Locations l , Station s ) throws Exception{
        List <Emplacement> dispo = this.placesAvailbale() ;
        if( dispo.size()==0){
            throw new NotPlacesAvailable();
        }else{
        Emplacement e = dispo.get(0);
        e.Deposer(l);
        s.getNotifier().notify("[DEPOT]     Location : "+l.getId_prod()+" -> Station : "+s.getId_station());
        s.setPlaces_restantes(s.getPlaces_restantes()-1);
        s.SetState((StationState)new Station_EnCours(this.places));
        }
    }
 


    
}
