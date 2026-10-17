package fil.l3.coo.StateController.StationState;
import java.util.Iterator;
import java.util.List;

import fil.l3.coo.*;
import fil.l3.coo.ExceptionsControlled.*;;
/**
 * Represents the state of a full station in the vehicle rental system.
 * This class implements the StationState interface to define behavior
 * specific to a full station.
 */
public class Station_pleine implements StationState {
    // The list of parking spaces in the station.
    private List<Emplacement> places ;
    /**
     * Constructs a Station_pleine object with a list of occupied spaces.
     * @param liste List of occupied spaces in the station
     */
    public Station_pleine(List<Emplacement> liste){
        this.places = liste;
    }
    /**
     * Attempts to deposit a vehicle into a full station.
     * @param l The vehicle to deposit
     * @param s The station
     * @throws StationPleine Always throws this exception as the station is full
     */
    public void Deposer( Locations l , Station s) throws Exception {
        throw new StationPleine();
    }; 
    /**
     * Retrieves a vehicle from the full station.
     * @param s The station
     * @return The retrieved vehicle
     * @throws Exception if retrieval fails
     */
    public Locations Retirer(Station s) throws Exception {
        Emplacement v = places.get(0);
        Locations m = v.Retirer();
        s.getNotifier().notify("[RETRAIT]  Location : "+m.getId_prod()+" -> Station :  "+s.getId_station());
        s.setPlaces_restantes(s.getPlaces_restantes()+1);
        s.SetState((StationState)new Station_EnCours(this.places));
        return m ;
    }
    /**
     * Returns the list of available spaces in the station.
     * @return null as there are no available spaces in a full station
     */
    public List<Emplacement> placesAvailbale(){
        return null ;
    } ;
    /**
     * Processes a rental operation in a full station.
     * @param c The client renting a vehicle
     * @param s The station
     * @throws Exception if the rental operation fails
     */
    public void Louer(Client c , Station s) throws Exception{
        for( Emplacement e : this.places){
            if( e.isOccupe() && !e.getLocations().getEtatService()){
                try{
                    Locations s1 = e.Retirer();
                    c.Louer(s1);
                    s.getNotifier().notify("[LOCATION]  Location : "+s1.getId_prod()+" -> Client  : "+c.getID_client());
                    s.setPlaces_restantes(s.getPlaces_restantes()+1);
                    s.SetState((StationState)new Station_EnCours(this.places));
                    break ;
                }catch(Exception ex){
                }
             }
        }
    };

    /**
     * Retrieves a specific vehicle from the full station based on its ID.
     * @param id_prod The ID of the vehicle to retrieve
     * @param s The station
     * @return The retrieved vehicle
     * @throws IdNotFound if the specified vehicle is not found
     * @throws Exception if retrieval fails
     */
public Locations Retirer( int id_prod , Station s) throws Exception{
            Iterator<Emplacement> l = s.getPlaces().iterator();
            while( l.hasNext()){
                Emplacement element = l.next();
                if(element.isOccupe() && element.getLocations().getId_prod() == id_prod){
                    s.setPlaces_restantes(s.getPlaces_restantes()+1);;
                    Locations v = element.Retirer();
                    s.getNotifier().notify("[RETRAIT]  Location : "+v.getId_prod()+" -> Station :  "+s.getId_station());
                    s.SetState((StationState)new Station_EnCours(this.places));
                    return v  ;
                }
            }
            throw new IdNotFound();
    }
    
}
