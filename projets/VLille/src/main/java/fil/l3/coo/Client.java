package fil.l3.coo;
import fil.l3.coo.AccesController.AccesProvidertoStation;
import fil.l3.coo.ExceptionsControlled.NotEnoughMoneyException;
import fil.l3.coo.ExceptionsControlled.NotInService;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import fil.l3.coo.NotificationController.*;
/**
 * Represents a client in the vehicle rental system.
 */
public class Client {
    // Unique identifier for the client
    private int ID_client = 0 ; 
    // Last name of the client
    private String Nom ; 
    // First name of the client
    private String Prenom ;
    // Currently rented vehicle
    private Locations vehicule ;
    // Client's available money
    private double money ;
    // Start time of the rental
    private LocalDateTime debut   ;
    // End time of the rental
    private LocalDateTime fin ; 
    // Static counter for client IDs
    private static int actual_id = 0 ;
    //Instance of ClientNotifications object for handling the notifications associated with the client.
    private ClientNotifications clientNotifications;
    //Represents the notifications associated with the client.
    private List<String> notifications;
    /**
     * Constructs a new Client with given details.
     * @param Nom Last name of the client
     * @param Prenom First name of the client
     * @param money Initial amount of money the client has
     */
    public Client ( String Nom , String Prenom , double money ){
        this.ID_client = actual_id+1 ;
        this.Nom=Nom;
        this.Prenom = Prenom ;
        this.money = money ;
        incrementNbInstance();
        this.notifications = new ArrayList<>();
        this.clientNotifications = new ClientNotifications(this);
    }
    /**
     * Increments the static counter for client IDs.
     */
    private static void incrementNbInstance(){
        actual_id++;
    }
    /**
     * Rent a vehicle for the client.
     * @param v The vehicle to rent
     * @throws Exception if the client doesn't have enough money or if the rental is not possible
     */
    public void Louer( Locations v) throws Exception {
            if( this.money < v.getCaution()){
                throw new NotEnoughMoneyException(ID_client);
            }else{
                this.money = this.money - v.getCaution();
                this.vehicule = v ; 
                this.debut = LocalDateTime.now() ;
                this.vehicule.Louer();
                }

    }
    /**
     * Deposits the rented vehicle back to a station.
     * @param s The station to deposit the vehicle
     * @throws Exception if there's an issue with depositing the vehicle
     */
    public void DeposerLocationDansStation(AccesProvidertoStation s )throws Exception{
        this.fin = LocalDateTime.now(); 
        long time = this.getDuration();
        System.out.println("    Duration == " + time);
        this.Debiter(time);
        s.Deposer(vehicule);
        this.vehicule = null ;
    }
    /**
     * Calculates the duration of the rental in seconds.
     * @return The duration of the rental in seconds
     */
    public Long getDuration(){
        Duration duration = Duration.between(debut, fin);
        return duration.toSeconds();
    }
    /**
     * Debits the client's account based on the rental duration.
     * @param time The duration of the rental in seconds
     */
    public void Debiter(long time){
        Double adebiter = (time / 1) * this.vehicule.getPrixLocation();
        this.money = this.money-adebiter+this.vehicule.getCaution();
    }

    /**
     * Returns the first name of the client.
     * @return The first name of the client.
     */
    public String getNom() {
        return Nom;
    }

    /**
     * Returns the first name of the client.
     * @return The first name of the client.
     */
    public String getPrenom() {
        return Prenom;
    }

    /**
     * Returns the current value of the actual_id static variable.
     * @return The current value of the actual_id static variable.
     */
    public static int getActual_id() {
        return actual_id;
    }

    /**
     * Returns the unique identifier of the client.
     * @return The unique identifier of the client.
     */
    public int getID_client() {
        return ID_client;
    }

    /**
     * Sets the unique identifier of the client.
     * @param iD_client The new unique identifier for the client.
     */
    public void setID_client(int iD_client) {
        ID_client = iD_client;
    }

    /**
     * Returns the vehicle associated with the client.
     * @return The vehicle associated with the client.
     */
    public Locations getVehicule() {
        return vehicule;
    }

    /**
     * Sets the vehicle associated with the client.
     * @param vehicule The new vehicle to associate with the client.
     */
    public void setVehicule(Locations vehicule) {
        this.vehicule = vehicule;
    }

    /**
     * Returns the current amount of money the client has.
     * @return The current amount of money the client has.
     */
    public double getMoney() {
        return money;
    }

    /**
     * Sets the current amount of money the client has.
     * @param money The new amount of money for the client.
     */
    public void setMoney(double money) {
        this.money = money;
    }
    
    /**
     * Adds a new notification to the list of notifications for the client.
     * @param notification The notification to be added.
     */
    public void addNotif(String notification) {
        notifications.add(notification);
    }
    /**
     * Returns the list of notifications for the client.
     * @return The list of notifications for the client.
     */
    public List<String> getNotifications() {
        return notifications;
    }
    /**
     * Removes and returns the first notification from the list of notifications for the client.
     * If there are no notifications, returns null.
     * @return The first notification in the list, or null if the list is empty.
     */
    public String popNotifications() {
        if(notifications.size() > 0) {
            return notifications.remove(0);
        }
        return null;
    }
    /**
     * Displays all the notifications for the client by printing them to the console.
     */
    public void displayNotifications() {
        while (notifications.size() > 0) {
            System.out.println(popNotifications());
        }
    }
    /**
     * Returns the client's notification object.
     * @return The client's notification object.
     */
    public ClientNotifications getClientNotifications() {
        return clientNotifications;
    }
}
