package fil.l3.coo.StateController.StationState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fil.l3.coo.*;
import fil.l3.coo.ExceptionsControlled.IdNotFound;
import fil.l3.coo.ExceptionsControlled.NotPlacesAvailable;

/**
 * Represents the state of a station that is neither empty nor full in the vehicle rental system.
 * This class implements the StationState interface to define behavior
 * specific to a station in an intermediate state.
 */
public class Station_EnCours implements StationState{
    // The list of available parking spots (Emplacement) at the station.
    private List<Emplacement> places ; 
    /**
     * Constructs a Station_EnCours object with a list of spaces.
     * @param places List of spaces in the station
     */
    public Station_EnCours(List<Emplacement> places){
        this.places = places ;
    }

    /**
     * Deposits a vehicle into the station.
     * @param l The vehicle to deposit
     * @param s The station
     * @throws Exception if the deposit operation fails
     */
    public void Deposer( Locations l , Station s) throws Exception{
        //System.out.println("*******************EN COURS*********************");
        List <Emplacement> dispo = this.placesAvailbale() ;
        //System.out.println(dispo);
        Emplacement e = dispo.get(0);
        e.Deposer(l);
        s.getNotifier().notify("[DEPOT]     Location : "+l.getId_prod()+" -> Station : "+s.getId_station());
        s.setPlaces_restantes(s.getPlaces_restantes()-1);
        if(s.isStationPleine()){
            s.SetState((StationState)new Station_pleine(this.places));
        }
    }
    /**
     * Returns the list of available spaces in the station.
     * @return List of available Emplacement objects
     */
       public List<Emplacement> placesAvailbale(){
        List<Emplacement> l = new ArrayList<Emplacement>();
        for( Emplacement e : this.places){
            if( !e.isOccupe()){
                l.add(e);
            }
        }
        return l ; 
    }
    /**
     * Retrieves a vehicle from the station.
     * @param s The station
     * @return The retrieved vehicle
     * @throws Exception if retrieval fails
     */
    public Locations Retirer(Station s) throws Exception {
        for(Emplacement v : this.places){
            if( v.isOccupe()){
                Locations m = v.Retirer();
                s.getNotifier().notify("[RETRAIT]  Location : "+m.getId_prod()+" -> Station :  "+s.getId_station());
                s.setPlaces_restantes(s.getPlaces_restantes()+1);
                if( s.StationVide())
                 {s.SetState((StationState)new Station_Vide(this.places));}
                return m ;
            }
        }
        throw new Exception("THERE IS A STATION ISSUE");
    }
    /**
     * Processes a rental operation in the station.
     * @param c The client renting a vehicle
     * @param s The station
     * @throws Exception if the rental operation fails
     */
    public void Louer(Client c ,Station s) throws Exception{
        for( Emplacement e : this.places){
            if( e.isOccupe() && !e.getLocations().getEtatService()){
                try{
                    Locations s1 = e.Retirer();
                    c.Louer(s1);
                    s.getNotifier().notify("[LOCATION]  Location : "+s1.getId_prod()+" -> Client  : "+c.getID_client());
                    s.setPlaces_restantes(s.getPlaces_restantes()+1);
                    if( s.StationVide()){s.SetState((StationState)new Station_Vide(this.places));}
                    break ;
                }catch(Exception ex){
                }
            }
        }
    }
    /**
     * Retrieves a specific vehicle from the station based on its ID.
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
                    s.getNotifier().notify("[RETRAIT]  Location :  "+v.getId_prod()+" -> Station : "+s.getId_station());
                    if( s.StationVide()){s.SetState((StationState)new Station_Vide(this.places));}
                    return v  ;
                }
            }
            throw new IdNotFound();
    }
}


    

