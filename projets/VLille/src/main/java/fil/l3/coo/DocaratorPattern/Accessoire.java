package fil.l3.coo.DocaratorPattern;

import fil.l3.coo.Locations;
import fil.l3.coo.NotificationController.ClientNotifications;

/**
 * Represents a base decorator for adding accessories to a rental item.
 * This class is part of the Decorator pattern implementation, allowing
 * additional features to be added to a Locations object dynamically.
 */
public abstract class Accessoire implements Locations  {
    // The base Locations object being decorated
    protected Locations  v ; 

    /**
     * Represents a notification client for the Accessoire class.
     * This field is used to handle notifications related to the rental
     * of the accessory.
     */
    protected ClientNotifications notifClient;
    /**
     * Constructs an Accessoire object, decorating the given Locations object.
     * @param v The Locations object to be decorated with an accessory
     */
    public Accessoire(Locations v){
        this.v = v  ;
    }

    /**
     * Rent the vehicle in the specified location.
     * @throws Exception if the rental operation cannot be completed
     */
    public void Louer() throws Exception{
        this.v.Louer();
    }


    public void askForIntervention() throws Exception{
        this.v.askForIntervention();
    } ;
    public int getId_prod(){
        return this.v.getId_prod();
    }
    public boolean isHors_service(){
        return this.v.isHors_service();
    }
    public void setToVole(){
        this.v.setToVole();
    }
    public void setDeposer(boolean f){
        this.v.setDeposer(f);
    }
    public int getIdDeuxRoues(){
        return v.getIdDeuxRoues();
    }
    public boolean isDepose(){
        return this.v.isDepose();
    }
    public double getCaution(){
        return this.v.getCaution();
    }

    public void ChangeStateService(){
        this.v.ChangeStateService();
    }

    public int get_NbLocation(){
        return this.v.get_NbLocation() ;
    }

    public boolean estVole(){
        return this.v.estVole();
    }

    public boolean getEtatService(){
       return this.v.getEtatService();
    }
    public void set_NbLocation(int nb){
        this.v.set_NbLocation(nb);
    }
    public boolean isLouee(){
        return this.isLouee();
    }
    public  Locations Retier() throws Exception{
        return this.v.Retier();
    }
    public void setEtatSerice(){
        this.v.setEtatSerice();
    }
    
    public double getPrixLocation(){
        return this.v.getPrixLocation();
    }
    
    public void setSystemeNotif(ClientNotifications notifClient){
        this.notifClient = notifClient;
    }

    public void notifyClient(String notification){
        this.notifClient.notifyClient(notification);
    }

    public void setLouee(boolean f){
        this.v.setLouee(f);
    }
}
