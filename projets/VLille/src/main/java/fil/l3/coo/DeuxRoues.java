package fil.l3.coo;
import fil.l3.coo.StateController.DeuxRoueState.*;
import fil.l3.coo.AccesController.AccesForReparation;
import fil.l3.coo.AccesController.AccesProviderForLocations;
import fil.l3.coo.NotificationController.ClientNotifications;
import fil.l3.coo.NotificationController.GestionnaireNotif;
/**
 * Abstract class representing a two-wheeled vehicle for rental.
 * Implements interfaces for location, repair access, and location access provision.
 */
public abstract class DeuxRoues implements Locations , AccesForReparation , AccesProviderForLocations  {
    /** Unique identifier for the product*/
    protected int Id_prod ; 
    /** Deposit amount*/
    protected double caution ; 
    /** Rental price*/
    protected double prix_location;
    /** Number of times the vehicle has been rented*/
    protected int nb_location =0; 
    /** Notification manager*/
    protected GestionnaireNotif notifier ;
    /** Current state of the vehicle*/
    protected DeuxRoueState state ;
    /* Booleans to define states  */  
    /** Whether the vehicle is deposited*/
    protected boolean depose ;
    /** Whether the vehicle is out of service*/
    protected boolean hors_service ;
    /** Whether the vehicle is currently rented*/
    protected boolean louee ;
    /** Whether the vehicle is stolen*/
    protected boolean isVole ;
    /*** DATA FOR IF MANAGEMENT ***/
    /** Maximum number of rentals before maintenance*/
    protected final int limite_location = 5 ; 
    /** Counter for created locations*/
    private static int  locations_created = 0 ;

    /**Notification manager for clients*/
    protected ClientNotifications notifClient ;
     /**
     * Constructor for DeuxRoues.
     * Initializes the vehicle with a unique ID and default states.
     */
    public DeuxRoues(){
        this.Id_prod = locations_created ;
        this.depose = false ; 
        this.louee = false ; 
        this.notifier = new GestionnaireNotif(); 
        IncrementeLocationId();
    }
    /**
     * Increments the static counter for created locations.
     */
    private static void IncrementeLocationId(){
        locations_created++;
    }
    /**
     * Compares this vehicle to another based on their IDs.
     * @param l The location to compare with
     * @return The difference between the IDs
     */

    /**
     * Gets the product ID of this vehicle.
     * @return The product ID
     */
    @Override
    public int getId_prod() {
        return Id_prod;
    }
    /**
     * Rent this vehicle.
     * @throws Exception if the rental is not possible
     */
    @Override
    public void Louer() throws Exception{
        this.state.Louer(this);
    }

    /**
     * Gets the number of times this vehicle has been rented.
     * @return The number of rentals
     */
    public int getNb_location() {
        return nb_location;
    }

    /**
     * Sets the number of times this two-wheeled vehicle has been rented.
     * @param nb_location The new number of rentals for this vehicle.
     */
    public void setNb_location(int nb_location) {
        this.nb_location = nb_location;
    }

    /**
     * Checks if the two-wheeled vehicle is currently rented.
     * @return true if the vehicle is rented, false otherwise
     */
    public boolean isLouee() {
        return louee;
    }

    /**
     * Sets the rented state of the two-wheeled vehicle.
     * @param louee The new rented state of the vehicle.
     */
    public void setLouee(boolean louee) {
        this.louee = louee;
    }
    /**
     * Checks if the vehicle is stolen.
     * @return true if the vehicle is stolen, false otherwise
     */
    @Override
    public boolean estVole(){
        return this.isVole ;
    }
    /**
     * Gets the rental price of the vehicle.
     * @return The rental price
     */
    @Override
    public double getPrixLocation(){
        return this.prix_location;
    }
    /**
     * Checks if the vehicle is deposited.
     * @return true if the vehicle is deposited, false otherwise
     */
    @Override
    public boolean isDepose(){
        return this.depose ;
    }
    /**
     * Sets the deposited state of the vehicle.
     * @param f The new deposited state
     */
    @Override
    public void setDeposer( boolean f){
        this.depose = f ;
    }
    /**
     * Gets the service state of the vehicle.
     * @return true if the vehicle is out of service, false otherwise
     */
    @Override
    public boolean getEtatService(){
        return this.hors_service;
    }
    /**
     * Gets the number of rentals for this two-wheeled vehicle.
     * @return The number of rentals
     */
    @Override
    public int  get_NbLocation(){
        return nb_location;
    }
    /**
     * Sets the number of rentals for this two-wheeled vehicle.
     * @param nb The new number of rentals.
     */
    @Override
    public void set_NbLocation( int nb){
        this.nb_location = nb ;
    }
    /**
     * Toggles the service state of the vehicle.
     */
    @Override
    public void ChangeStateService(){
        this.hors_service = !this.hors_service ;
    }
    /**
     * Sets the service state and updates the vehicle's state accordingly.
     */
    @Override
    public void setEtatSerice(){
        this.ChangeStateService();
        if( this.hors_service){
            this.state = new HorsService();
            }
        else{
            this.state = new OnService();
        }
    }
    /**
     * Marks the vehicle as stolen and notifies the system.
     */
    @Override
    public void setToVole(){
        this.state = new Vole();
        this.notifier.notify("[VOLE]  Velo : "+this.getIdDeuxRoues()+" a ete Vole");
        this.isVole = true ;
    }
    /**
     * Gets the deposit amount for the vehicle.
     * @return The deposit amount
     */
    @Override
    public double getCaution (){
        return this.caution ;
    }

    /**
     * Gets the GestionnaireNotif instance used by this two-wheeled vehicle.
     * @return The GestionnaireNotif instance used by this two-wheeled vehicle.
     */
    protected GestionnaireNotif getNotifier(){
        return this.notifier ; 
    }

    /**
     * Gets the unique identifier for this two-wheeled vehicle.
     * @return The unique identifier for this two-wheeled vehicle.
     */
    public int getIdDeuxRoues(){
        return this.Id_prod ;
    }

    /**
     * Checks whether the vehicle is out of service.
     * @return true if the vehicle is out of service, false otherwise.
     */
    public boolean isHors_service() {
        return this.hors_service;
    }
    /**
     * Checks if maintenance is needed and updates the state accordingly.
     * @throws Exception if there's an issue with calling for intervention
     */
    public void askForIntervention()throws Exception{
        if ( this.nb_location >= this.limite_location){
            this.hors_service=true ; 
            this.nb_location = 0 ;
            this.state = new HorsService();
            this.notifier.call_for_intervention(this);
        }
    } 
    /**
     * Retrieve the vehicle from its current location.
     * @return The retrieved vehicle
     * @throws Exception if retrieval is not possible
     */
    public Locations Retier() throws Exception{
        return this.state.Retirer(this) ;
    }
    public int getLimite_location() {
        return limite_location;
    }
    @Override
    /**
     * Sets the ClientNotifications instance to be used for notifying clients.
     * @param notif The ClientNotifications instance to be used for notifying clients.
     */
    public void setSystemeNotif(ClientNotifications notif){
        this.notifClient = notif ;
    }
    @Override
    /**
     * Notifies the client with the given notification.
     * @param notification The notification to be sent to the client.
     */
    public void notifyClient(String notification){
        if(this.notifClient != null){
            this.notifClient.notifyClient(notification);
        }
    }

}
