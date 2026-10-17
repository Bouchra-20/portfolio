package fil.l3.coo;

import java.util.*;

import fil.l3.coo.DocaratorPattern.*;

public class BikeRentalSystem {
    V_lille vLille = V_lille.getInstance();
    public BikeRentalSystem() {

    }

    /**
     * Retrieves a random station that is not empty.
     * @return a random station that is not empty, or `null` if all stations are empty
     */
    public Station getRandomNotEmptyStaion(){
        int n = 0;
        Station station = vLille.getStations().get(n);
        return station;
    }

    /**
     * Retrieves a random station that is not full.
     * @return a random station that is not full, or `null` if all stations are full
     */
    public Station getRandomNotFullStation(){
        Random rand = new Random();
        int stationSize = vLille.getStations().size();
        int n = 0;
        Station station = vLille.getStations().get(n);
        while(station.isStationPleine()){
            n = n+ 1;
            station = vLille.getStations().get(n);
            if(vLille.StationARedistribuer('p').size() == stationSize ){
                return null;
            }
        }
        return station;
    }

    /**
     * Creates a specified number of clients with random first and last names, and a random amount of money.
     * The created clients are added to the V_lille instance.
     * @param nbClient the number of clients to create
     */
    public void createClients(int nbClient) {
        Random rand = new Random();
        for (int i = 1; i <= nbClient; i++) {
            String firstName = "Client" + i;
            String lastName = "Lastname" + i;
            double money = rand.nextInt(2000) + 200;
            Client client = new Client(lastName, firstName, money);
            vLille.addClient(client);
        }
    }

    /**
     * Starts the redistribution of bikes between stations.
     * @return a list of two lists, where the first list contains the source stations 
     * and the second list contains the destination stations for the redistribution, 
     * or `null` if there are no stations that need to be redistributed.
     * @throws Exception if an error occurs during the redistribution process.
     */
    public List<List<Station>> startRedistribution() throws Exception {
        List<List<Station>> list = vLille.NeedToDistribute(2);
        if(list.get(0).isEmpty() || list.get(1).isEmpty()){
            return null;
        }else{
            List<Station> source = new ArrayList<>();
            List<Station> destination = new ArrayList<>();

            List<Station> st1 = list.get(0);
            List<Station> st2 = list.get(1);
            int[] sizesSt1 = new int[st1.size()];
            int c = 0;
            for(Station s : st1){
                sizesSt1[c] = s.getPlaces_restantes();
                c++;
            }
            int[] sizesSt2 = new int[st2.size()];
            c = 0;
            for(Station s : st2){
                sizesSt2[c] = s.getPlaces_restantes();
                c++;
            }
            vLille.REDISTRIBUTION(list.get(0), list.get(1));
            c = 0;
            for(Station s : st1){
                if(sizesSt1[c] != s.getPlaces_restantes()){
                    source.add(s);
                }
                c++;
            }
            c = 0;
            for(Station s : st2){
                if(sizesSt2[c] != s.getPlaces_restantes()){
                    destination.add(s);
                }
                c++;
            }
            return List.of(source, destination);
        }
        
    }

    /**
     * Displays a single station's status in a grid format.
     * 
     * Each spot in the grid is represented by:
     * - "🚲" if a bike is present in that location.
     * - "⬜" if the location is empty.
     * 
     * @param station The `Station` object to display.
     */
    public void displayOneStation(Station station) {
        int count = 0;
        System.out.println("===== Station " + station.getId_station() + " =====");
        for (Emplacement e : station.getPlaces()) {
            String symbole = "⬜";
            if( e.isOccupe()){
                Locations loc = e.getLocations();
                symbole = loc.toString();
            }
            //e.isOccupe() ? "🚲" : "⬜";
            System.out.printf(" %s ", symbole);
            count++;
            if (count % 5 == 0) { // 5 emplacements par ligne
                System.out.println(); // Saut de ligne
            }
        }
        System.out.println();
    }

    /**
     * Returns a formatted header string for a given station.
     *
     * @param station The `Station` object to get the header for.
     * @return A formatted string in the format "===== Station {stationId} =====" where {stationId} is the ID of the station.
     */
    public String getStationHeader(Station station) {
        return String.format("===== Station %d =====", station.getId_station());
    }
    
    /**
     * Generates a grid representation of the available spaces in a given station.
     * 
     * Each line in the grid represents 5 spaces, with the following symbols:
     * - "🚲" if a standard bike is present in that space
     * - "🛵" if an electric bike is present in that space
     * - "⬜" if the space is empty
     * 
     * @param station The `Station` object to generate the grid for.
     * @return A list of strings, where each string represents a line in the grid.
     */
    public List<String> getStationGrid(Station station) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        int count = 0;
        
        for (Emplacement e : station.getPlaces()) {
            String symbole = "⬜";
            if (e.isOccupe()) {
                Locations loc = e.getLocations();
                symbole = loc.toString();
            }
            currentLine.append(String.format(" %s ", symbole));
            count++;
            
            if (count % 5 == 0) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
            }
        }
        
        return lines;
    }
    
    /**
     * Displays multiple stations in a formatted grid layout.
     * This method prints the header for each station, followed by a grid representation of the available spaces in each station.
     * The stations are displayed in groups of 3 per line, with the headers and grids aligned.
     */
    public void afficherPlusieursStations() {
        int stationsParLigne = 3;
        int stationsSize = vLille.getStations().size();
        for (int i = 0; i < stationsSize; i += stationsParLigne) {
            for (int j = 0; j < stationsParLigne && i + j < stationsSize; j++) {
                System.out.print(getStationHeader(vLille.getStations().get(i + j)));
                if (j < stationsParLigne - 1 && i + j < stationsSize - 1) {
                    System.out.print("     ");
                }
            }
            System.out.println();

            List<List<String>> stationGrids = new ArrayList<>();
            for (int j = 0; j < stationsParLigne && i + j < stationsSize; j++) {
                stationGrids.add(getStationGrid(vLille.getStations().get(i + j)));
            }
            
            int maxLines = stationGrids.stream()
                                     .mapToInt(List::size)
                                     .max()
                                     .orElse(0);
                                     
            for (int line = 0; line < maxLines; line++) {
                for (int j = 0; j < stationGrids.size(); j++) {
                    if (line < stationGrids.get(j).size()) {
                        System.out.print(stationGrids.get(j).get(line));
                    } else {
                        System.out.print("        ");
                    }
                    if (j < stationGrids.size() - 1) {
                        System.out.print("       ");
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    /**
     * Displays multiple notifications per row for compact visualization.
     * Notifications are grouped into columns and rows.
     * @param notifications List of notifications to display.
     * @param columns Number of notifications per row.
     */
    public void afficherNotificationsParColonnes(int columns, int debut) {
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m";
        final String YELLOW = "\u001B[33m";
        final String RED = "\u001B[31m";
        final String CYAN = "\u001B[36m";
        final String MAGENTA = "\u001B[35m";
        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("║                                        \u001B[5m\u001B[1mNOTIFICATIONS \u001B[0m                                               ║");

        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════");
        
        for (int i = debut; i < vLille.getNotifications().size(); i++) {
            String notification = vLille.getNotifications().get(i);
            String color = "";
            String highlightedNotification = notification;

            // Définir la couleur en fonction du type de notification
            if (notification.contains("[DEPOT]")) {
                color = GREEN;
            } else if (notification.contains("[VOLE]")) {
                color = RED;
            } else if (notification.contains("[INTERVENTION]")) {
                color = YELLOW;
            }else if(notification.contains("[REPARATION]")){
                color = MAGENTA;
            }else{
                color = CYAN;
            }
            
            int startIndex = notification.indexOf("[");
            int endIndex = notification.indexOf("]") + 1; // Inclure le caractère ']'
            if (startIndex != -1 && endIndex != -1) {
                String before = notification.substring(0, startIndex);
                String inside = notification.substring(startIndex, endIndex);
                String after = notification.substring(endIndex);
                highlightedNotification = before + color + inside + RESET + after;
            }
            
            if(notification.length() > 50){
                System.out.println(" "+ highlightedNotification);
                System.out.println("----------------------------------------------------------------------------------------------------");

            }else{
                System.out.print(" "+ highlightedNotification);
                if (i == vLille.getNotifications().size() - 1) {
                    System.out.println();    
                } else if((i + 1) % columns == 0){
                    System.out.println();
                    System.out.println("----------------------------------------------------------------------------------------------------");
                } else {
                    System.out.print(" | ");
                }
            }
        }
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════════════════");
    }
}
