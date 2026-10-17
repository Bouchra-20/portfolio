package action;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import card.*;
import city.*;
import game.GameBoard;
import infection.Disease;
import player.*;

public abstract class Action {

	protected Players player;
	protected int NbCard;
	protected GameBoard gameBoard;
	
	public Action(Players player, GameBoard gameBoard) {
		this.player = player;
		this.NbCard = 5;
		this.gameBoard = gameBoard;
	}

	/**
     * Discover a cure for a given disease if the player has the necessary cards.
     * @return true if the cure has been discovered, false otherwise
     */
	public boolean peuxDecouvrirUnRemede(){
		// tests if the city has a research station
		if(!this.player.getLocation().hasResearchStation()) {
			return false;
		}
		if(this.player.getLocation().getDisease().isEmpty()) {
			return false;
		}
		List<Card> playerHand=this.player.getHand();
		List<PlayerCard> matchingCards = new ArrayList<>();
		// tests if the disease is among the diseases of the city, if yes it adds it to the matchingCards
	        for (Card card : playerHand) {
	            if ((card instanceof PlayerCard) &&  this.player.getLocation().getDisease().contains(card.getDisease())) {
	                matchingCards.add((PlayerCard) card);
	                
	            }
	            
	        }
	        //if there are enough cards to discover the cure and the cure for this disease has not yet been found
	        if (matchingCards.size() >= this.NbCard && !this.gameBoard.remedeTrouvee(matchingCards.get(0).getDisease())) {
		        //Discard matching cards
		        for (PlayerCard card : matchingCards) {
		            playerHand.remove(card);
		            this.gameBoard.ajouterDansDefaussePlayer(card);
		        }
		        
		        // Discover the cure
		        this.gameBoard.addMaladiesGueries(matchingCards.get(0).getDisease());
		        return true ;
	        }
	        return false ;
	}
	
	
	/**
	 * @return the list of the neighbors
	 */
	public List<City> choixDeplacement(){
		return this.player.getLocation().getNeighbors();
	}
	
	/**
	 * @return true if can build a research station, false otherwise
	 */
	public boolean peutConstruireUneStation()
	{ 
		if(this.necessiteCartePourConstruireStationRecherche()) {
			List<Card> main=this.player.getHand();
			ListIterator<Card> i = main.listIterator();
			while(i.hasNext())
			{
				//tests if the city of the card and that of the player are the same and does not have a research station
				if(i.next().getCity().equals(this.player.getLocation()) && !this.player.getLocation().hasResearchStation())
				{ 
					return true;
				}
						
			}
		}else if(!this.player.getLocation().hasResearchStation()) {
			return true;
		}

		return false;
		
	}
	
	/**
	 * @param d the disease
	 * @return number of disease cubes to remove to treat disease
	 */
	public int nbCubeMaladieARetirerPourTraiter(Disease d)
	{
		if(this.gameBoard.remedeTrouvee(d))
		{ 
			return this.player.getLocation().getNombreCubeMaladie(d);
		}
		else
		{
			return 1;
		}
		
	}	
	
	
	/**
	 * @return true when need a card to build research station
	 */
	public boolean necessiteCartePourConstruireStationRecherche()
	{ 
		return true;
	}
	
	public void effet() {
		// ne fait rien, sauf pour médecin
	}
	
	/**
	 * move the player to an adjacent city
	 * @param city the city where the player wants to go
	 */
	public boolean traverser(City city) {
	    if (this.choixDeplacement().contains(city)) {
	        this.player.setLocation(city);
	        return true;
	    }
	    return false;
	}

	/**
	 * move the player to the city by one card from their hand
	 * @param city the city where the player wants to go
	 */
	/*public void volDirect(City city) {
		List<Card> playerHand = this.player.getHand();
		for(Card card : playerHand) {
			if (card.getCity().equals(city)) {
		    	this.player.removeCardFromHand(card);
		    	this.player.setLocation(city);
		    	this.gameBoard.ajouterDansDefaussePlayer(card);
		    	break;
			}
		}
	}*/
	
	/**
	 * move the player to any city if they have a card matching the current location
	 * @param city the city where the player wants to go
	 */
	/*public void volCharter(City city) {
		List<Card> playerHand = this.player.getHand();
		for(Card card : playerHand) {
		    if (card.getCity().equals(this.player.getLocation())){
		    	this.player.removeCardFromHand(card);
		    	this.player.setLocation(city);
		    	this.gameBoard.ajouterDansDefaussePlayer(card);
		    	break;
		    }
		}
	}*/
	
	/**
	 * move to any city that has a research station if the player is in a city that has a research station
	 * @param city the city where the player wants to go
	 */
	/*public void volNavette(City city) {
	    if (this.player.getLocation().getResearchStation() != null && city.getResearchStation() != null) {
	    	this.player.setLocation(city);
	    }
	}*/
	

	public void passer() {
	    // Do nothing
	}

	/**
	 * build a Research Station 
	 * @param city the city we want to move the station if not null
	 */
	public void construireResearchStation(City city) {
	    // Vérifier si le joueur peut construire une station de recherche
		if(this.peutConstruireUneStation()) {
	        // Si la ville est nulle et s'il reste des stations disponibles, construire une station sur la ville actuelle
			if( city == null && this.gameBoard.stationsDisponible.size() > 0) {
	            // Ajouter une station de recherche à la ville actuelle
				this.player.getLocation().setResearchStation(this.gameBoard.stationsDisponible.get(0));
	            // Ajouter la station construite à la liste des stations construites
				this.gameBoard.stationsConstruites.add(this.gameBoard.stationsDisponible.get(0));
	            // Retirer la station disponible de la liste des stations disponibles
				this.gameBoard.stationsDisponible.remove(0);
	            // Chercher la carte de la ville actuelle dans la main du joueur et la défausser
				List<Card> playerHand = this.player.getHand();
				for(Card card : playerHand) {
					if (card.getCity().equals(this.player.getLocation())) {
				    	this.player.removeCardFromHand(card);
				    	this.gameBoard.ajouterDansDefaussePlayer((CardPlayerAndEpidemic) card);
	                    break; // Sortir de la boucle for si on a trouvé et défaussé la carte
					}
				}
			}else {
	            // S'il n'y a plus de stations disponibles ou si une ville est spécifiée, déplacer une station de recherche
				this.gameBoard.prendreUneStation(city);
	            // Ajouter une station de recherche à la ville actuelle
				this.player.getLocation().setResearchStation(this.gameBoard.stationsConstruites.get(0));
			}
		}
	}
	
	/**
	 * remove a disease cube in the city the player is in. if a cure has been discovered, all cubes are removed.
	 * @param disease the disease we want to treat
	 * @return true if remove the necessary disease cubes, false otherwise
	 */
	public boolean traiterMaladie(Disease disease) {
		//tests if there are no cubes of this disease in the current city
		if(this.nbCubeMaladieARetirerPourTraiter(disease) == 0) {
			return false;
		}else {
			// Remove the necessary disease cubes from the current city
			this.player.getLocation().removeCubeMaladie(disease, this.nbCubeMaladieARetirerPourTraiter(disease));
			// Tests if a disease are eradicate 
		    if(this.player.getLocation().getNombreCubeMaladie(disease) == 0) {
		    	this.player.getLocation().removeDisease(disease);
		    }else if(this.gameBoard.getNbCubeMaladieRestant(disease) == 0 && this.gameBoard.remedeTrouvee(disease)) {
		        this.gameBoard.setMaladieGuerie(disease);		    	
		    }
			return true ;
		}
	}

}
