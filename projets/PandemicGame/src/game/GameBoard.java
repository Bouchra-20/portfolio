package game;

import java.util.*;
import card.*;
import city.*;
import infection.*;
import pile.*;
import player.*;
import map.*;

/**
 * @author omar_, wafa_, bouchra_, el mehdi_
 *
 */
public class GameBoard {
	

	/**
	 * List of cured diseases
	 */
	protected List<Disease> maladiesGueries;
	protected List<Players> players;
	public List<ResearchStation> stationsDisponible;
	public List<ResearchStation> stationsConstruites;
	protected Map<Disease,Integer> cubesParMaladie;
	protected PileInfectionCard infcard;
	protected PilePlayerCard playercard;
	protected int nbFoyersInfection;
	protected int NB_TOTAL_FOYERS_INFECTION_MAX = 8;
	protected int TauxGlobalInfection;
	protected int MAX_CUBE_MALADIE = 3;
	protected Mappemonde mappemonde ;
	/**
	 * 
	 */
	public GameBoard()
	{ 

		this.maladiesGueries= new ArrayList<Disease>();
		this.stationsDisponible=new ArrayList<ResearchStation>();
		this.stationsConstruites=new ArrayList<ResearchStation>();
		this.nbFoyersInfection =0;
		this.TauxGlobalInfection =2;
		this.players= new ArrayList<Players>();
		this.cubesParMaladie=new HashMap<>();
		this.infcard= new PileInfectionCard(); 
		this.playercard= new PilePlayerCard();
		
		
	}
	
//	public void setPlayerCards(int nbOfCard)
//	{
//		
//	}
//	
//	public void setInfectionCards(int nbOfCard)
//	{
//	
//	}
//	
//	public void setEpidemicCards(int nbOfCard)
//	{
//	
//	}
	
	public void initResearchStations() 
	{ 
		for (int i=0;i<6;i++)
		{ 
			this.stationsDisponible.add(new ResearchStation());
		}
	}
	
	public void initCubesMaladie() 
	{
		Disease[] listemaladiepossible = Disease.values();
		for( Disease m :listemaladiepossible) {
			
			this.cubesParMaladie.put(m, 24);
			
		}
	}
	
	/**
	 * get number of cubes associated with this disease
	 * @param disease the disease we want to get the number of cubes
	 * @return number of cubes
	 */
	public Integer getNbCubeMaladieRestant(Disease disease) {
		return this.cubesParMaladie.get(disease);
	}
	
	
	public void prendreUneStation(City city)
	{ 
    	if(city.hasResearchStation()) {
    		city.removeResearchStation();;
    	}
	}
	
	
	/**
	 * @return List of discard
	 */
	public List<InfectionCard> getInfectionDefausse(){
		return this.infcard.getDefausse();
	}
	
	public List<CardPlayerAndEpidemic> getPlayerDefausse(){
		return this.playercard.getDefausse();
	}
	
	/**
	 * @return List of pile cards
	 */
	public List<InfectionCard> getInfectionPile(){
		return this.infcard.getPile();
	}
	
	public List<CardPlayerAndEpidemic> getPlayerPile(){
		return this.playercard.getPile();
	}
	
	/**add a card in infection discard
	 * @param carte the card
	 */
	public void ajouterDansDefausseInfection(InfectionCard card) 
	{
		this.infcard.defausse(card);
	}
	
	/**add a card in player discard
	 * @param carte the card
	 */
	public void ajouterDansDefaussePlayer(CardPlayerAndEpidemic card) 
	{
		this.playercard.addDefausse(card);
	}
	
	/**add a card in infection pile
	 * @param carte the card
	 */
	public void ajouterDansPileInfection(InfectionCard card) 
	{ 
		this.infcard.pile(card);
	}
	
	/**add a card in player pile
	 * @param carte the card
	 */
	public void ajouterDansPilePlayer(CardPlayerAndEpidemic playercard2) 
	{ 
		this.playercard.addPile(playercard2);
	}
	
	
	public List<Disease> getMaladiesGueries(){
		return this.maladiesGueries;
	}
	
	/**add a disease in the list of cured diseases 
	 * @param disease the disease
	 */
	public void addMaladiesGueries(Disease disease) 
	{  
		maladiesGueries.add(disease);  
	}  

	/*public boolean isMaladieGuerie(Disease disease) {
	    return this.getMaladiesGueries().contains(disease);
	}*/

	public void setMaladieGuerie(Disease disease) {
	    if (this.remedeTrouvee(disease)) {
	    	// We remove all the disease cubes on the map
	        for (City city : this.mappemonde.getCities()) {
	            if (city.getDisease().contains(disease)) {
	                city.removeCubeMaladie(disease, city.getNombreCubeMaladie(disease));
	                city.removeDisease(disease);
	            }
	        }
	        // Remove all the disease cubes from the supply
	        this.cubesParMaladie.put(disease, 0);
	        //this.addMaladiesGueries(disease);
	    }
	}

	/**
	 * @param d the disease
	 * @return true if the remedy found, false otherwise
	 */
	public boolean remedeTrouvee(Disease d)  
	{ 
	    return this.maladiesGueries.contains(d);
	} 

	
	public boolean foyerInfection(City city) {
	    int numCubes = 0;
	    for (Disease disease : Disease.values()) {
	        numCubes += city.getNombreCubeMaladie(disease); // compter le nombre de cubes pour chaque maladie
	        if (numCubes >= MAX_CUBE_MALADIE) {
	            return true; // s'il y a au moins une maladie avec trop de cubes, déclencher un foyer d'infection
	        }
	    }
	    return false; // sinon, pas de foyer d'infection
	}
	
	public void addnbFoyersInfection() {
		this.nbFoyersInfection++;
	}
	
	public int getnbFoyersInfection() {
		return this.nbFoyersInfection;
	}
	
	public boolean tousRemedesTrouvees() {
	    for (Disease disease : Disease.values()) {
	        if (!remedeTrouvee(disease)) {
	            return false;
	        }
	    }
	    return true;
	}

	public boolean gameOver() {
	    // Situation 1 : il n'y a plus de cube disponible pour une maladie
        for (Disease disease : Disease.values()) {
            if (getNbCubeMaladieRestant(disease) == 0 && !remedeTrouvee(disease)) {
                return true;
            }
        }
        // Situation 2 : le nombre de foyers d'infection atteint 8 ou un joueur ne peut pas prendre les 2 cartes joueur à son tour
	    if (getnbFoyersInfection() >= NB_TOTAL_FOYERS_INFECTION_MAX || getPlayerPile().size() == 0) {
	    	return true;
	    }
	    // Situation 3 : tous les joueurs ont gagné en éradiquant toutes les maladies
	    if(tousRemedesTrouvees()) {
	    	return true;
	    }
	    return false;
	}
	
	public int getTauxGlobalInfection() {
		return this.TauxGlobalInfection;
	}
	
	public void augmenterTauxGlobalInfection() {
	    if (TauxGlobalInfection < NB_TOTAL_FOYERS_INFECTION_MAX) {
	    	TauxGlobalInfection++;
	    }else {
	    	gameOver();
	    }
	}
	
	
	/*public void infectCity(City city, int nbCube) {
	    	List<Disease> diseases = city.getDisease();
			for(Disease disease : diseases) {
	        if (city.getNombreCubeMaladie(disease) < MAX_CUBE_MALADIE) {
	            city.addCubeMaladie(disease, nbCube);
	        } else if (!foyerInfection(city)) {
	            this.addnbFoyersInfection();
	            for (City adjacentCity : city.getNeighbors()) {
	            	propagateDisease(adjacentCity, disease);
	            }
	        }
	    }
	    this.augmenterTauxGlobalInfection();
	}*/
	
	public void infectCityDisease(City city, int nbCube, Disease d) {
	        if (city.getNombreCubeMaladie(d) <= MAX_CUBE_MALADIE) {
		        this.cubesParMaladie.replace(d, this.getNbCubeMaladieRestant(d)-nbCube);
	            city.addCubeMaladie(d, nbCube);
	        } else if (foyerInfection(city)) {
	            this.addnbFoyersInfection();
	            for (City adjacentCity : city.getNeighbors()) {
	            	propagateDisease(adjacentCity, d);
	            }
	        }
	    //this.augmenterTauxGlobalInfection();
	}

 
	public boolean infectionPhase() {
	    // Détermine le nombre de cartes infection à piocher en fonction du taux global d'infection
	    //int numInfectionCards = this.getTauxGlobalInfection();
	    
	    // Pioche les cartes infection et infecte les villes correspondantes
	   // for (int i = 0; i < numInfectionCards; i++) {
			/*if (this.getInfectionPile().isEmpty()) {
				return;
			}*/
	        InfectionCard card = this.getInfectionPile().get(0);
	        City city = card.getCity();
	        Disease disease = card.getDisease();
	        //int numCubesToAdd = 1;
	        
	        // Vérifie si la ville est déjà éradiquée
	        if (city.getNombreCubeMaladie(disease) == 0 && this.remedeTrouvee(disease)) {
	            return false;
	        }
	        
	        // Vérifie s'il reste des cubes pour les placer
	        if (getNbCubeMaladieRestant(disease) == 0 && !this.remedeTrouvee(disease)) {
	        	gameOver();
	        	return false;
	        }

	        // Prendre les cubes de la maladie du reserve vers la ville
	        this.cubesParMaladie.replace(disease, this.getNbCubeMaladieRestant(disease)-1);
	        city.addCubeMaladie(disease, 1);
	        
	        // Vérifie si la ville est déjà un foyer d'infection
	        if (foyerInfection(city)) {
	            //numCubesToAdd = 2;
	            this.addnbFoyersInfection();
                System.out.println(city.getName() + " est une foyer d'infection");
	        }
	        // Propage la maladie aux villes voisines si nécessaire
	        if (city.getNombreCubeMaladie(disease) >= 3) {
	            propagateDisease(city, disease);
	        }
	        
	        // Défausse la carte infection
	        ajouterDansDefausseInfection(card);
            getInfectionPile().remove(card);
	    //}
	    
	    // Met à jour le taux global d'infection
	    //this.augmenterTauxGlobalInfection();
	    return true;
	}

	private void propagateDisease(City outbreakCity, Disease disease) {
	    List<City> adjacentCities = outbreakCity.getNeighbors();
	    for (City city : adjacentCities) {
	        if (!this.foyerInfection(city)) {
		        this.cubesParMaladie.replace(disease, this.getNbCubeMaladieRestant(disease)-1);
	            city.addCubeMaladie(disease, 1);
                System.out.println(disease.name() + " est propagé à la "+ city.getName());
	            if (city.getNombreCubeMaladie(disease) >= 3) {
	                propagateDisease(city, disease);
	            }
	        }
	    }
	    this.addnbFoyersInfection();
	}

}
