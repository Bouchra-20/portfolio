package fil.l3.coo.DocaratorPattern;
import fil.l3.coo.Locations;
import fil.l3.coo.DocaratorPattern.*;

import java.lang.reflect.Array;

import java.util.*;

public class State {
    
    private double baseLatitude;
    private double baseLongitude;
    private Random random;
    private List<double[]> trajet = new ArrayList<double[]>();
    private boolean tracking;
    private Map<String, double[]> quartiers;
    private double[] destinationCoords;
    private String destinationName;

    public State() {
        this.random = new Random();
        // Générer une latitude entre -90 et 90 degrés
        this.baseLatitude = -90 + (180) * random.nextDouble();
        // Générer une longitude entre -180 et 180 degrés
        this.baseLongitude = -180 + (360) * random.nextDouble();
        this.tracking = false;
        this.quartiers = new HashMap<>();
        this.initializeQuartiers();
    }

    public void initializeQuartiers(){
        quartiers.put("Vieux-Lille", new double[]{50.6395, 3.0639});
        quartiers.put("Wazemmes", new double[]{50.6264, 3.0451});
        quartiers.put("Saint-Maurice Pellevoisin", new double[]{50.6469, 3.0821});
        quartiers.put("Moulins", new double[]{50.6232, 3.0621});
        quartiers.put("Fives", new double[]{50.6370, 3.0844});
        quartiers.put("Lille-Centre", new double[]{50.6292, 3.0573});
        quartiers.put("Vauban-Esquermes", new double[]{50.6316, 3.0458});
    }


    public void activateRouteTracking() {
        this.tracking = true;
        System.out.println("Suivi de trajet activé.");
    }

    public void stopRouteTracking() {
        this.tracking = false;
        System.out.println("Suivi de trajet désactivé.");
    }

    /**
     * Simule la position actuelle du vélo en générant des coordonnées proches de la position de base.
     * @return un tableau contenant la latitude et la longitude simulées.
     */
    public double[] simulatePosition(){
        double randomLatitudeOffset = (random.nextDouble() - 0.5) * 0.01; // Variation de ±0.005 (environ 500m)
        double randomLongitudeOffset = (random.nextDouble() - 0.5) * 0.01;

        double currentLatitude = baseLatitude + randomLatitudeOffset;
        double currentLongitude = baseLongitude + randomLongitudeOffset;
        String quartier = getQuartier(currentLatitude, currentLongitude);

        System.out.printf("Position actuelle : %.6f, %.6f (%s, Lille)\n", currentLatitude, currentLongitude, quartier);
        if(this.tracking){
            this.trajet.add(new double[]{currentLatitude, currentLongitude});
        }

        return new double[]{currentLatitude, currentLongitude};
    }

    /**
     * Trouve le quartier correspondant à la position donnée.
     * @param latitude La latitude de la position actuelle.
     * @param longitude La longitude de la position actuelle.
     * @return Le nom du quartier correspondant ou "Inconnu" si aucun quartier ne correspond.
     */
    private String getQuartier(double latitude, double longitude) {
        String quartier = "Inconnu";
        double minDistance = Double.MAX_VALUE;

        for (Map.Entry<String, double[]> entry : quartiers.entrySet()) {
            double[] coords = entry.getValue();
            double distance = calculateDistance(latitude, longitude, coords[0], coords[1]);
            if (distance < minDistance) {
                minDistance = distance;
                quartier = entry.getKey();
            }
        }

        return quartier;
    }

    /**
     * Calcule la distance entre deux points géographiques en utilisant la formule de la distance euclidienne.
     * @param lat1 Latitude du premier point.
     * @param lon1 Longitude du premier point.
     * @param lat2 Latitude du second point.
     * @param lon2 Longitude du second point.
     * @return La distance entre les deux points.
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2));
    }


    public List<double[]> getRouteTracking() {
        this.tracking = true;
        System.out.println("Trajet enregistré : ");
        for (double[] position : this.trajet) {
            double p1 = position[0];
            double p2 = position[1];
            String quartier = getQuartier(p1, p2);
            System.out.printf("Latitude: %.6f, Longitude: %.6f (%s, Lille)\n", p1, p2, quartier);
        }
        return this.trajet;
    }

    /**
     * Calcule et affiche la distance estimée jusqu'à la destination définie.
     */
    public void getDistanceToDestination(String destination) {
        if (quartiers.containsKey(destination)) {
            this.destinationCoords = quartiers.get(destination);
            this.destinationName = destination;

            double[] currentPosition = simulatePosition();

            double distance = calculateDistance(currentPosition[0], currentPosition[1], this.destinationCoords[0], this.destinationCoords[1]);

            System.out.printf("Distance estimée jusqu'à %s : %.2f km.\n", this.destinationName, distance * 0.32); // Conversion
        }else{
            System.out.println("Quartier inconnu : " + destination);
        }
    }

    public static void main(String[] args) {
        State s = new State();
        s.activateRouteTracking();
        for (int i = 0; i < 3; i++) {
            s.simulatePosition();
        }
        s.getDistanceToDestination("Lille-Centre");
        s.getRouteTracking();
    }
}
