package fil.l3.coo;

import java.util.*;

import fil.l3.coo.AccesController.AccesForReparation;
import fil.l3.coo.DocaratorPattern.Electrique;
import fil.l3.coo.NotificationController.Subscriber;
import fil.l3.coo.StrategyPattern.RedistributionStargies.HALF_RATIO_REDISTRIBUTION;
import fil.l3.coo.StrategyPattern.RedistributionStargies.REDISTRIBUTION_STRATEGY;
import fil.l3.coo.StrategyPattern.VoleStrategies.Seul2temps;
import fil.l3.coo.StrategyPattern.VoleStrategies.voleStrategy;

/**
 * The `V_lille` class is a singleton implementation of the Subscriber interface. It manages the operations of a bicycle rental system in the city of Lille.
 * The class has the following responsibilities: Maintains a list of notifications, clients, stations, vehicles, and personnel (interventions). Initializes the stations, 
 * vehicles, and personnel. Distributes the vehicles across the stations. Handles the redistribution of vehicles between stations. Manages the logic for handling intervention
 * requests. Provides methods to display the state of the stations.
 */
public class V_lille implements Subscriber { /* SINGLOTENT  */
    // A list of notifications that have been added to the system.
    private List<String> notifications  ;
    /** Retrieves the singleton instance of the `V_lille` class, which manages the bicycle rental system in the city of Lille.
     * @return the singleton instance of `V_lille`
     */
    private static V_lille unique_agence = new V_lille(); 
    // Represents the duration of a time period in the bicycle rental system, measured in units of 20 seconds.
    private final int periode = 1 ;/* 1 en unite de 20 s */
    // A list of interventions performed by the personnel of the bicycle rental system.
    private List <Intervention> personnel ;
    // Represents the strategy used for redistributing vehicles between stations in the bicycle rental system. This field is used to determine how vehicles are moved between stations to ensure an even distribution.
    private REDISTRIBUTION_STRATEGY redistribution_strategy ;
    // Strategy for managing bike thefts, here "Seul2temps" checks if a bike remains alone for 2 periods.
    private voleStrategy vole_Strategy = new Seul2temps() ;/* Vole consiste a voir les velo reste seul dans une station pendant 2 temps  */
    // List of the system's clients.    
    private List <Client> Clients ;
    // Indicates whether the system has been initialized.
    private boolean initialized = false ;
    // List of bike stations.
    private List <Station> Stations ; 
    // List of bikes (or other transportation devices) available.
    private List <Locations> vehicules ; 
    //A list of bicycles that need to be repaired.
    private List <AccesForReparation> veloToRepare ;

    /**
     * Private constructor to ensure the Singleton design pattern.
     * Initializes personnel, clients, notifications, stations, vehicles, and redistribution strategy.
     */
    private V_lille () { 
        this.personnel = new ArrayList<Intervention>();
        this.Clients = new ArrayList<Client>();
        this.notifications = new ArrayList<String>();
        this.Stations = new ArrayList<Station>();
        this.vehicules = new ArrayList<Locations> ();
        this.veloToRepare = new ArrayList<AccesForReparation>();
        this.redistribution_strategy = new HALF_RATIO_REDISTRIBUTION(0.5);
        initStation();
        initVelo();
        initPersonnel();
    }
    /**
     * Static method to retrieve the unique instance of `V_lille`.
     * @return The unique instance of `V_lille`.
     */
    public static V_lille getInstance(){
            return unique_agence ;
        }
    /**
     * Method to update the list of notifications with a new message.
     * @param s The notification message.
     */
    public void update(String s){
        this.notifications.add(s);
    }
    /**
     * Retrieves the current list of notifications.
     * @return The list of notifications.
     */
    public List<String> getNotifications(){
        return this.notifications ;
    }
    /**
     * Adds a new intervention to the personnel list.
     * @param i The intervention to add.
     */
    public void AddIntervention(Intervention i){
        this.personnel.add(i);
    }
    /**
     * Initializes a random number of stations with a fixed capacity.
     */
    public void initStation(){
        Random rand = new Random();
        int nb_stations = 10;
        for( int i =0  ; i< nb_stations ; i++){
            Station s = new Station(10);
            this.addStation(s);
        }
        
    }
    /**
     * Initializes a random number of bikes and adds them to the system.
     */
    public void initVelo(){
        Random rand = new Random();
        int nb_velo = 30;
        for( int i = 0 ; i < nb_velo ; i++){
            this.vehicules.add(new Velo(10 , false , 100));
        }

        int nb_electrique_velo = 10;
        for( int i = 0 ; i < nb_electrique_velo ; i++){
            int nb_battery = rand.nextInt(10)+1;
                Electrique electrique = new Electrique(new Velo(10, false, 100), nb_battery);
                this.vehicules.add(electrique);
        }
    }
    /**
     * Initializes a random number of personnel (repairers) for the system.
     */
    public void initPersonnel(){
        Random rand = new Random();
        int effectif = rand.nextInt(30) +5;
        for( int i = 0 ; i < effectif ; i++){
            Personnel p = new Reparateur("Amine", "Tifoun");
            this.personnel.add(p);
        }
    }
    /**
     * Adds a station to the system.
     * @param s The station to add.
     */
    public void addStation(Station s){
        this.Stations.add(s);
    }

    /**
     * Checks if the system is initialized.
     * @return True if the system is initialized, otherwise False.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Distributes the bikes from the `vehicules` list across the different stations.
     * @param max_in_station The maximum number of bikes per station.
     */
    public void Distribution(int max_in_station){ /* Distribution de tous les velos sur les n stations */
        int i = 0 ;
        Iterator<Station> s =this.Stations.iterator();
        Station e = s.next() ;        
        for(Locations v : this.vehicules){
            try{
                if( i < max_in_station){    
                    e.Deposer(v);
                    i++;
                }else{
                    if( s.hasNext()){
                        e = s.next();
                        e.Deposer(v);
                        i=1;
                    }else{
                        break ;
                    }
                }
            }
            catch(Exception f ){/* Exception de StationPleine */
                f.printStackTrace();
            }
        }
        this.initialized = true;
    }

    /**
     * Retrieves the list of stations requiring redistribution.
     * @param etatStation 'p' for full stations, 'v' for empty stations.
     * @return List of stations corresponding to the specified state.
     */
    public  List<Station> StationARedistribuer(char etatStation){  /* p pour chercher les pleines et v pour chercher les vide */ 
        List<Station> s = new ArrayList<Station>();
       for( Station v : this.Stations){
            if( (etatStation =='p'&& v.isStationPleine()) || (etatStation =='v' && v.StationVide())){
                s.add(v);
            }
       }
       return s ; 
    } 

    /**
     * Identifies stations requiring redistribution after a specified number of periods.
     * @param numberOfTime The number of periods to wait.
     * @return A nested list containing full and empty stations.
     * @throws Exception In case of an error during the wait.
     */
    public List<List<Station>> NeedToDistribute(int numberOfTime) throws Exception {/* POSITION 0 LES STATIONS PLEINES POSITION 1 LES STATIONS VIDES */
        /* NUMBER OF TIME MUST BE MINIMUM 2 */
        if( numberOfTime <2){
            return null ;
        }
        List<List<Station>> s = new ArrayList<>();
        for (int i = 0; i < numberOfTime; i++) {
            Thread.sleep(this.periode * 1000); 
            s.add(new ArrayList<>(this.StationARedistribuer('p'))); 
            s.add(new ArrayList<>(this.StationARedistribuer('v')));
        }
        s.set(0, Intersection(s.get(0), s.get(2)));
        s.set(1, Intersection(s.get(1), s.get(3)));
        s.subList(2, s.size()).clear(); 
        return s;
    }
    /**
     * Identifies the intersection between two lists of stations.
     * @param s1 The first list of stations.
     * @param s2 The second list of stations.
     * @return List of stations present in both lists.
     */
    private List<Station> Intersection(List<Station> s1, List<Station> s2) {
        List<Station> result = new ArrayList<>(s1);
        result.retainAll(s2);
        return result;
    }
    /**
     * Removes and returns the last intervention from the personnel list.
     * @return The last intervention object in the personnel list.
     */
    private Intervention popPersonnel(){
        return this.personnel.remove(this.personnel.size()-1);
    }
    /**
     * Retrieves and returns the last notification added to the system.
     * @return The last notification as a `String`.
     */
    public String popNotification(){
        return this.getNotifications().get(this.getNotifications().size()-1);
    }
    /**
     * Handles an intervention request for a product in need of maintenance.
     * 
     * This method logs a notification about the product that requires immediate intervention.
     * It also assigns a personnel member to interact with the product and changes the service state.
     * 
     * @param i The `AccesForReparation` object representing the product needing intervention.
     * @throws Exception If an error occurs during the intervention process.
     */
    public void askForIntervention(AccesForReparation i)throws Exception{
        /* Logique d'intervention */
        //this.notifications.add("The Product with [ id = "+ i.getId_prod()+"] is Seeking for Intervention IMMEDIALTLY !");
        Intervention p = popPersonnel();
        p.Interagir(this.periode);
        i.setEtatSerice();
        this.notifications.add("[INTERVENTION] Repair completed for location : "+ i.getId_prod());
    }
    /**
     * Retrieves the list of stations in the system.
     * @return A list of `Station` objects.
     */
    public List<Station> getStations(){
        return this.Stations ;
    }
    /**
     * Sets the list of stations in the system to a new list.
     * @param s A list of `Station` objects to set.
     */
    public void setStations(List<Station> s){
        this.Stations = s ;
    }
    /**
     * Redistributes bikes between stations using the current redistribution strategy.
     * This method applies a strategy to move bikes from source stations to destination stations.
     * @param Source The list of source stations (stations with excess bikes).
     * @param dest The list of destination stations (stations with space for more bikes).
     * @throws Exception If an error occurs during the redistribution.
     */
    public void REDISTRIBUTION(List<Station> Source , List<Station> dest)throws Exception{
        this.redistribution_strategy.REDISTRIBUTION(Source, dest);
    }
    /**
     * Identifies the bikes that have been flagged as potentially stolen.
    
     * This method uses a theft detection strategy to identify bikes that might have been stolen 
     * if they remain alone in a station for a specific period.
     * @return A list of potentially stolen bikes (`Locations` objects).
     * @throws Exception If an error occurs during the theft detection process.
     */
    public List<Locations> getDeuxRouesVole() throws Exception{
        List<Locations> l = this.vole_Strategy.getDeuxRouesVole(periode, this.Stations);
        return l ;
    }

    /**
     * This method provides access to the list of bicycles that have been flagged as needing repair.
     * @return A list of `AccesForReparation` objects representing the bicycles that need repair.
     */
    public List<AccesForReparation> getVeloToRepare(){
        return this.veloToRepare ;
    }

    /**
     * Adds a bicycle that needs repair to the list of bicycles requiring repair.
     * @param l The `AccesForReparation` object representing the bicycle that needs repair.
     */
    public void addNeedRepairer(AccesForReparation l){
        this.notifications.add("[REPARATION] Location : "+ l.getId_prod()+" is Seeking for Intervention ! ");
        this.veloToRepare.add(l);
    }

    /**
     * Adds a new client to the list of clients.
     * @param c The `Client` object to be added.
     */
    public void addClient(Client c){
        this.Clients.add(c);
    }

    /**
     * Gets the list of clients registered with the system.
     * @return The list of `Client` objects representing the clients.
     */
    public List<Client> getClients(){
        return this.Clients ;
    }
}
