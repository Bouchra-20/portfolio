package fil.l3.coo;

import java.util.*;

import fil.l3.coo.AccesController.AccesForReparation;
import fil.l3.coo.DocaratorPattern.*;

/**
 * The `main` class is the entry point of the application that demonstrates the functionality
 * of the `V_lille` class, which manages the bicycle rental system in the city of Lille.
 * It initializes the system, distributes bicycles across stations, and displays the state of all stations.
 */
public class main {
    /**
     * The `main` method is the starting point of the application.
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        V_lille v = V_lille.getInstance();
        BikeRentalSystem bikeRentalSystem = new BikeRentalSystem();
        
        v.Distribution(10);

        

        Client client1 = new Client("Tifoun", "Amine", 45000);
        Client client2 = new Client("John", "Doe", 2500);


        try{
            Locations locationVole = new Velo(100,false, 300);
            v.getStations().get(9).Deposer(locationVole);
            bikeRentalSystem.afficherPlusieursStations();

            Random rand = new Random();
            int n = 0;

            /*A normal client rent a bike */
            Station station1 = bikeRentalSystem.getRandomNotEmptyStaion();
            station1.Louer(client1);

            Locations location1 = client1.getVehicule();

            /*Client has a bag to add in the bike basket */
            EquipePanier accessoire = new EquipePanier(location1);
            Sac sac = new Sac(2);
            accessoire.addPanier(sac);
            
            /**Client deposit the bike at a station after removing his bag */
            n = rand.nextInt(v.getStations().size());
            Station station2 = bikeRentalSystem.getRandomNotFullStation();
            accessoire.removePanier();
            station2.Deposer(client1.getVehicule());

            /**Create multiple clients */
            bikeRentalSystem.createClients(v.getStations().size());

            System.out.println(v.getClients().size() +" clients rent and deposit bikes in many stations");
            for (Client client : v.getClients()) {
                Station rentalStation = bikeRentalSystem.getRandomNotEmptyStaion();
                if(rentalStation == null){
                    System.out.println("Client with id "+ client.getID_client() +" can't rent a bike because all stations are empty");
                    break;
                }else{
                    rentalStation.Louer(client);
                    
                }
            }

            for (Client client : v.getClients()) {
                if (client.getVehicule() != null) {
                    Station Station = bikeRentalSystem.getRandomNotFullStation();
                    if(Station == null){
                        System.out.println("Client with id "+ client.getID_client() +" can't deposit the bike because all stations are full");
                        break;
                    }else{
                        Station.Deposer(client.getVehicule());
                    }
                }
            }

            Station station = new Station(10);
            
            Locations location = new Velo(100,false, 300);
            
            System.out.println("****** Id velo test for asking reparation : "+location.getIdDeuxRoues() +" ********");
            for(int i =0; i<5; i++ ){
                Client client = new Client("Tifoun","Amine", 3000);
                client.Louer(location);
                station.Deposer(location);

            }
            try{
                Client client = new Client("Failed","rent", 3000);
                System.err.println("Now the client with id "+ client.getID_client() +" is traying to rent bike witch is not available because of nb location > limite location");
                client.Louer(location);
            }catch(Exception e){
                System.out.println("Client id :"+ Client.getActual_id() +" can't rent a bike because bike not available");
            }

            

            List<Locations> veloVole = v.getDeuxRouesVole();
            if(veloVole.size() == 0){
                System.out.println("No bikes stolen");
            }else{
                System.out.println(veloVole.size() + " bikes stolen");
            }
            
            bikeRentalSystem.afficherNotificationsParColonnes(2, 0);

            System.out.println("Stations before redistribution");
            bikeRentalSystem.afficherPlusieursStations();

            

            List<List<Station>> destribution = bikeRentalSystem.startRedistribution();
            if(destribution == null){
                System.out.println("No stations to redistribute");
            }else{
                System.out.println("After redistribution");
                bikeRentalSystem.afficherPlusieursStations();
            }


            Station station3 = bikeRentalSystem.getRandomNotEmptyStaion();
            station3.Louer(client2);

            Locations location2 = client2.getVehicule();
            if(location2 == null){
                System.out.println("No bike to rent");
            }
            GPS gps = new GPS(location2);
            gps.activateRouteTracking();
            gps.simulatePosition();
            gps.getRouteTracking();
            gps.getDistanceToDestination("Lille-Centre");
            gps.stopRouteTracking();

            client2.displayNotifications();


        }catch(Exception e){
            e.printStackTrace();;
        }
    }
}
    