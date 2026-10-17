package fil.l3.coo.DocaratorPattern;
import fil.l3.coo.*;


import java.util.*;

public class GPS extends Accessoire {
    private double baseLatitude;
    private double baseLongitude;
    private Random random;
    private List<double[]> trajet = new ArrayList<double[]>();
    private boolean tracking;
    private Map<String, double[]> neighborhoods;
    private double[] destinationCoords;
    private String destinationName;

/* private state state */
    public GPS(Locations v) {
        super(v);
        v.notifyClient("GPS ajouté à la location.");
        this.random = new Random();
        // Générer une latitude entre -90 et 90 degrés
        this.baseLatitude = -90 + (180) * random.nextDouble();
        // Générer une longitude entre -180 et 180 degrés
        this.baseLongitude = -180 + (360) * random.nextDouble();
        this.tracking = false;
        this.neighborhoods = new HashMap<>();
        this.initializeNeighborhoods();
    }

    @Override
    public void Louer() throws Exception {
        this.v.Louer();
        super.notifyClient("GPS activé.");
    }

    /*public void startGPS() {
        super.notifyClient("GPS activé.");
    }*/

    public void stopGPS() {
        v.notifyClient("GPS désactivé.");
    }

    public void saveFavoriteRoute(String routeName) {
        v.notifyClient("Itinéraire \"" + routeName + "\" sauvegardé comme favori.");
    }


    public void initializeNeighborhoods(){
        neighborhoods.put("Vieux-Lille", new double[]{50.6395, 3.0639});
        neighborhoods.put("Wazemmes", new double[]{50.6264, 3.0451});
        neighborhoods.put("Saint-Maurice Pellevoisin", new double[]{50.6469, 3.0821});
        neighborhoods.put("Moulins", new double[]{50.6232, 3.0621});
        neighborhoods.put("Fives", new double[]{50.6370, 3.0844});
        neighborhoods.put("Lille-Centre", new double[]{50.6292, 3.0573});
        neighborhoods.put("Vauban-Esquermes", new double[]{50.6316, 3.0458});
    }


    public void activateRouteTracking() {
        this.tracking = true;
        v.notifyClient("Suivi de trajet activé.");
    }

    public void stopRouteTracking() {
        this.tracking = false;
        v.notifyClient("Suivi de trajet désactivé.");
    }

    private String formatNumber(double value) {
        return String.format("%.4f", value); // Formate à 4 chiffres après le point
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

        v.notifyClient("Position actuelle : "+ formatNumber(currentLatitude) +", "+ formatNumber(currentLongitude) + " ("+ quartier +", Lille)\n");
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

        for (Map.Entry<String, double[]> entry : neighborhoods.entrySet()) {
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
        v.notifyClient("Trajet enregistré : ");
        for (double[] position : this.trajet) {
            double p1 = position[0];
            double p2 = position[1];
            String quartier = getQuartier(p1, p2);
            v.notifyClient("Latitude: "+  formatNumber(p1) +", Longitude: "+ formatNumber(p2) +" (" + quartier +", Lille)\n");
        }
        return this.trajet;
    }

    /**
     * Calcule et affiche la distance estimée jusqu'à la destination définie.
     */
    public void getDistanceToDestination(String destination) {
        if (neighborhoods.containsKey(destination)) {
            this.destinationCoords = neighborhoods.get(destination);
            this.destinationName = destination;

            double[] currentPosition = simulatePosition();

            double distance = calculateDistance(currentPosition[0], currentPosition[1], this.destinationCoords[0], this.destinationCoords[1]);

            v.notifyClient("Distance estimée jusqu'à "+ this.destinationName +" : "+ formatNumber(distance * 0.32) +" km.\n"); // Conversion
        }else{
            v.notifyClient("Quartier inconnu : " + destination);
        }
    }
    public boolean isTracking() {
        return tracking;
    }
    public Map<String, double[]>  getNeighborhoods(){
        return this.neighborhoods;
    }

}

