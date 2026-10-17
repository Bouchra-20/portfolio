package city;
import java.util.*; 
import infection.*;
import player.*;

/**
 * @author omar_, wafa_, bouchra_, el mehdi_
 *
 */
public class City {
	
	/**
	 * the name of the city
	 */
	protected String name;
	/**
	 * A list of city neighbors
	 */
	protected List<City> neighbors;
	/**
	 * the research station
	 */
	protected ResearchStation researchStation;
	/**
	 * A list of the players
	 */
	protected List<Players> players;
	/**
	 * city diseases
	 */
	protected Map<Disease,Integer> maladiesDeLaVille;
	/**
	 * the sector
	 */
	protected int secteur;
	

	
    /**
     * @param name the city name
     * @param secteur the sector
     */
    public City(String name, int secteur){
        this.name = name;
        this.neighbors = new ArrayList<City>();
        this.players = new ArrayList<Players>();
        this.maladiesDeLaVille = new HashMap<Disease,Integer>();    
        this.secteur=secteur;
    }
    
    /**
     * @return the name of the city
     */
    public String getName() {
    	return this.name;
    }
    /**
     * @return the list of the neighbors
     */
    public List<City> getNeighbors() {
    	return this.neighbors;
    }
    /**
     * @return the research station
     */
    public ResearchStation getResearchStation() {
    	return this.researchStation;
    }
    
    public void setResearchStation(ResearchStation s)
    { 
    	this.researchStation=s;
    	s.setLocation(this);
    }
    public void removeResearchStation()
    { 
    	this.researchStation= null;
    }
    /**
     * tests if the city has a research station or not
     * @param city the city
     * @return true if the city has a research station, false otherwise
     */
    public boolean hasResearchStation() {
        return this.getResearchStation() != null;
    }
    
    /**
     * @return the players
     */
    public List<Players> getPlayers(){
    	return this.players;
    }
    /**
     * @param p the player 
     * add players
     */
    public void addPlayer(Players p) {
    	this.players.add(p);
    }
    /**
     * @param m the disease
     * @return number of the city diseases
     */
    public int getNombreCubeMaladie(Disease m){
    	if(this.maladiesDeLaVille.containsKey(m))
    	{ 
    		return this.maladiesDeLaVille.get(m); 
    	}
    	else
    	{ 
    		return 0;
    	}
    	
    }
   
    /**
     * @param neighbors add neighbors
     */
    public void setNeighbors(List<City> neighbors) {
    	this.neighbors.addAll(neighbors);
    }
    /**
     * @param neighbor add a neighbor
     */
    public void setNeighbor(City neighbor) {
    	this.neighbors.add(neighbor);
    }
    
    /**
     * @param infection the infection
     * @param cubeMaladie a cube maladie
     * @return adds a disease cube to a city
     */
    public void addCubeMaladie(Disease infection, int cubeMaladie) {
    	int nb_avant=0;
    	
    	if(maladiesDeLaVille.containsKey(infection) ) {
    		nb_avant=this.maladiesDeLaVille.get(infection);
    	} 
    	 if(nb_avant+cubeMaladie >= 3) {
    		 	this.maladiesDeLaVille.put(infection, 3);
    	 }else {
    	    	this.maladiesDeLaVille.put(infection, nb_avant+cubeMaladie);
    	 }
    }
   
    public void removeCubeMaladie(Disease infection, int cubeMaladie) {
    	int nb_avant=0;
    	
    	if(maladiesDeLaVille.containsKey(infection) ) {
    		nb_avant=this.maladiesDeLaVille.get(infection);
    		if(nb_avant < cubeMaladie)
    		{ 
    			this.maladiesDeLaVille.put(infection, 0);
    		}
    		else
    		{ 
    			this.maladiesDeLaVille.put(infection, nb_avant-cubeMaladie);
    		}
    		
    	} 
    	 
    	
    }
    
    
    /**
     * @param o an object
     * @return true if the cities are equals, false otherwise
     */
    public boolean equals(Object o) {
    	if (! (o instanceof City)) {
    		return false;
    	}
    	City other = (City) o;
    	return (this.getName() == other.getName()) && (this.getNeighbors().equals(other.getNeighbors()));
    }    
    
    /**
     * @return List of Diseases of this city
     */
    public List<Disease> getDisease() {
    	List<Disease> disease = new ArrayList<Disease>();
    	for(Disease i : maladiesDeLaVille.keySet()) {
    		disease.add(i);
    	}
		return disease;
	}
    
    public void removeDisease(Disease d) {
    		maladiesDeLaVille.remove(d);
    }
    
    public String toString() {
		return "La ville s'appelle " + this.name ;
    }
    
}

