package fil.l3.coo;
import java.util.* ;

import fil.l3.coo.AccesController.AccesProvidertoStation;
import fil.l3.coo.ExceptionsControlled.StationPleine;
import fil.l3.coo.NotificationController.GestionnaireNotif;
import fil.l3.coo.StateController.StationState.StationState;
import fil.l3.coo.StateController.StationState.Station_Vide;

/**
 * The `Station` class represents a bicycle rental station.
 * 
 * Each station has a unique ID, a list of available spots for bikes, and manages the state of 
 * the station (whether it is empty, full, or has some bikes). The class implements the 
 * `AccesProvidertoStation` interface, which provides access to deposit and withdraw bikes from the station.
 * 
 * This class has the following responsibilities:
 * - Manages bike spots (locations) within the station.
 * - Tracks the number of available and occupied spots.
 * - Controls the station state (full, empty, etc.).
 * - Provides notifications via a notification manager.
 * - Offers methods to rent and return bikes.
 */
public class Station implements AccesProvidertoStation{
    // Unique ID for the station.
    private int id_station ;
    // Current state of the station (empty, full, etc.).
    private StationState state;
    // List of bike spots (locations) in the station.
    private List<Emplacement> places ; 
    // Total number of spots in the station.
    private int nb_palces ;
    // Notification manager for the station.
    private GestionnaireNotif notifier=  new GestionnaireNotif() ;
    // Number of remaining available spots in the station.
    private int places_restantes ;
    // Static counter for the current station ID.
    static int current_id =0 ;

    /**
     * Constructs a new `Station` with a specified number of spots.
     * Initializes the list of bike spots (`Emplacement`) and sets the initial state to empty.
     * The station ID is automatically assigned using a static counter.
     * @param nb_places The number of spots in the station.
     */
    public Station( int nb_places ){
        this.places = new ArrayList<Emplacement>();
        for( int i = 1 ; i<= nb_places ; i++){
            Emplacement e = new Emplacement();
            this.places.add( e );
        }
        this.nb_palces = nb_places;
        this.places_restantes =nb_places;
        this.id_station = current_id ; 
        this.state = new Station_Vide(places);
        Incremente_ID();
       }
    /**
     * Increments the static ID counter for the station.
     */
    static void Incremente_ID(){
        current_id ++ ; 
    }

    /**
     * Deposits a bike or another transport device (`Locations`) at the station.
     * The deposit operation is handled by the current state of the station.
     * @param l The `Locations` object representing the bike or device to be deposited.
     * @throws Exception If the deposit operation fails (e.g., if the station is full).
     */
    @Override
    public void Deposer( Locations l) throws Exception{
            this.state.Deposer(l , this);

    }
    /**
     * Sets the current state of the station.
     * This method allows changing the station's state (e.g., from empty to full).
     * @param l The new `StationState` for the station.
     */
    public void SetState( StationState l){
        this.state = l ;
    }

    /**
     * Retrieves the notification manager (`GestionnaireNotif`) for the station.
     * @return The `GestionnaireNotif` object.
     */
    public GestionnaireNotif getNotifier() {
        return notifier;
    }

    /**
     * Sets the number of remaining available spots in the station.
     * @param places_restantes The number of remaining spots.
     */
    public void setPlaces_restantes(int places_restantes) {
        this.places_restantes = places_restantes;
    }
    /**
     * Retrieves the total number of spots in the station.
     * @return The total number of spots as an integer.
     */
    public int getNb_palces() {
        return nb_palces;
    }
    /**
     * Retrieves the current static station ID counter.
     * @return The current ID counter as an integer.
     */
    public static int getCurrent_id() {
        return current_id;
    }
    /**
     * Withdraws a bike or another transport device from the station.
     * The withdrawal operation is handled by the current state of the station.
     * @return The `Locations` object representing the withdrawn bike or device.
     * @throws Exception If the withdrawal operation fails (e.g., if the station is empty).
     */
    public Locations Retirer() throws Exception{
            return state.Retirer(this);
    }
    /**
     * Checks if the station is empty.
     * @return `true` if the station is empty, otherwise `false`.
     */
    public boolean StationVide (){
        boolean assertion = true ;
        for( Emplacement e : this.places){
            assertion = assertion && ( e.isOccupe() == false);
        };
        return assertion ;
    }

    /**
     * Retrieves the list of available (unoccupied) spots in the station.
     * The operation is managed by the current state of the station.
     * @return A list of available `Emplacement` objects.
     */
    public List<Emplacement> placesAvailbale(){
        return state.placesAvailbale();
    }
    /**
     * Checks if the station is full.
     * @return `true` if the station is full, otherwise `false`.
     */
    public boolean isStationPleine(){
        return this.places_restantes == 0 ;
    }
    /**
     * Retrieves the number of remaining available spots in the station.
     * @return The number of remaining available spots as an integer.
     */
    public int getPlaces_restantes() {
        return places_restantes;
    }
    /**
     * Retrieves the current state of the station.
     * @return The `StationState` object representing the station's state.
     */
    public StationState getState() {
        return state;
    }
    /**
     * Retrieves the list of bike spots (`Emplacement`) in the station.
     * @return A list of `Emplacement` objects.
     */
    public List<Emplacement> getPlaces() {
        return this.places;
    }
    /**
     * Retrieves the unique ID of the station.
     * @return The station ID as an integer.
     */
    public int getId_station() {
        return id_station;
    }
    /**
     * Checks if the station contains a specific bike or transport device (`Locations`).
     * @param v The `Locations` object to check.
     * @return `true` if the station contains the specified bike or device, otherwise `false`.
     */
    public boolean ContainsLocations( Locations v){
        boolean assertion  = false ; 
        for( Emplacement e : this.places){
            if( e.getLocations() != null && e.getLocations().equals(v)){
                assertion = true ;
            }
        }
        return assertion ; 
    }
    /**
     * Allows a client to rent a bike or another transport device from the station.
     * The rental operation is handled by the current state of the station.
     * @param c The `Client` object representing the client renting the bike or device.
     * @throws Exception If the rental operation fails (e.g., if no bikes are available).
     */
    public void Louer(Client c) throws Exception{
            state.Louer(c, this);
    }
    /**
     * Withdraws a bike or another transport device with a specific product ID from the station.
     * The withdrawal operation is handled by the current state of the station.
     * @param id_prod The ID of the product to withdraw.
     * @return The `Locations` object representing the withdrawn bike or device.
     * @throws Exception If the withdrawal operation fails (e.g., if the specified product is not found).
     */
    public Locations Retirer ( int id_prod) throws Exception{
            return state.Retirer(id_prod, this);
    }
    /**
     * Identifies and retrieves a bike or another transport device that might have been stolen.
     * If there is only one occupied spot in the station, this method will return the bike or device 
     * in that spot, indicating that it might be stolen.
     * @return The `Locations` object representing the potentially stolen bike or device, or `null` if not found.
     * @throws Exception If an error occurs during the process.
     */
    public Locations ProbablyLocationsVole() throws Exception{
        int placesOccupee = this.nb_palces - this.places_restantes ;
        Locations m = null ;
        if( placesOccupee == 1 ){
           for(Emplacement e : this.places){
                if( e.isOccupe()){
                    m =  e.getLocations() ;
                }
           }
        }
        return m ;
    }
    
    
}
