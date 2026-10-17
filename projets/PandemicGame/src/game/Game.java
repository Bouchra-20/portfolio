package game;

import java.util.*;

import action.Action;
import card.Card;
import card.CardPlayerAndEpidemic;
import card.EpidemicCard;
import card.InfectionCard;
import city.City;
import city.ResearchStation;
import infection.Disease;
import player.Players;

public class Game {

	public GameBoard gameboard;
	public ArrayList<Players> playersList ;
	public ArrayList<Action> playersRole ;
	
	public Game(GameBoard gameboard) {
		this.gameboard = gameboard;
        this.playersList = new ArrayList<>();
        this.playersRole = new ArrayList<>();

	}
	
	public void Play() {
	
    Scanner sc = new Scanner(System.in);
    while (!gameboard.gameOver()) {
        for (int k = 0; k < playersList.size(); k++) {
            Players player = playersList.get(k);
            Action playerAction = playersRole.get(k);
            
            // Phase 1 : le joueur réalise ses 4 actions
            int actionsLeft = 4;
            while (actionsLeft > 0) {
            	 if(player.getHand().size() > 0) {
                     System.out.println();
                     System.out.println("----Cartes en main : ---- ");
                     for (Card carte : player.getHand()) {
                         System.out.println("----------------------");
                    	 System.out.println("| " + carte.getCity().getName() + " | " + carte.getDisease().name());
                         System.out.println("----------------------");
                     }
            	}
                System.out.println(" ");
                System.out.println("Vous étes dans "+ player.getLocation().getName());
                System.out.println(" ");

                System.out.println(player.getName() + " - Actions restantes : " + actionsLeft);
                System.out.println("1 - Se déplacer");
                System.out.println("2 - Construire une station de recherche");
                System.out.println("3 - Découvrir un remède");
                System.out.println("4 - Traiter une maladie");
                System.out.println("5 - Passer");
                int choice;
                while (true) {
                if (sc.hasNextInt()) {
                    choice = sc.nextInt();
                    break;
                } else {
                    System.out.println("Erreur: vous devez entrer un entier.");
                    sc.next(); // vide le scanner pour éviter une boucle infinie
                	}
                }
                switch (choice) {
                    case 1:
                        System.out.println("Choisir la ville de déplacement");
                        List<City> choices = playerAction.choixDeplacement();
                        for (int i = 0; i < choices.size(); i++) {
                        	if(!choices.get(i).getDisease().isEmpty()) {
                        		for(Disease d : choices.get(i).getDisease()) {
                        			if(choices.get(i).hasResearchStation()) {
                        				System.out.print((i+1) + " - " + choices.get(i).getName() + ", La maladie :" + d.name() + ", Nombre de cubes :" + choices.get(i).getNombreCubeMaladie(d) + ", la ville a une station de recherche");
                        			}else {
                        				System.out.print((i+1) + " - " + choices.get(i).getName() + ", La maladie :" + d.name() + ", Nombre de cubes :" + choices.get(i).getNombreCubeMaladie(d) + " " );
                        			}
                        		}
                                System.out.println();
                        	}else {
                        		if(choices.get(i).hasResearchStation()) {
                    				System.out.println((i+1) + " - " + choices.get(i).getName() + ", la ville a une station de recherche");
                    			}else {                               
                    				System.out.println((i+1) + " - " + choices.get(i).getName());
                    			}
                        	}
                        }
                        System.out.println("--");
                        System.out.println("0 - Retour au choix des actions");
                        System.out.println("--");
                        int numCity;
                        while (true) {
                        if (sc.hasNextInt()) {
                        	numCity = sc.nextInt();
                            break;
                        } else {
                            System.out.println("Erreur: vous devez entrer un entier.");
                            sc.next(); // vide le scanner pour éviter une boucle infinie
                        	}
                        }
                        if(numCity == 0) {
                        	break;
                        }
                        else if(playerAction.traverser(choices.get(numCity - 1))) {
                            System.out.println("Vous êtes déplacé à " + choices.get(numCity - 1).getName());
                            actionsLeft--;
                    	}
                        break;
                    case 2:
                        if (player.getLocation().hasResearchStation()) {
                            System.out.println("Vous êtes déjà dans une ville avec une station de recherche.");
                        }else if(playerAction.peutConstruireUneStation()) {
                        	if(gameboard.stationsDisponible.isEmpty()) {
                    		System.out.println("Choisir une ville pour déplacer sa station de recherche");
                    		List<ResearchStation> choix = gameboard.stationsConstruites;
                    		for(int i = 0; i < choix.size(); i++) {
                                System.out.println((i+1) + " - " + choix.get(i).getLocation().getName());
                    		}
                            System.out.println("--");
                            System.out.println("0 - Retour au choix des actions");
                            System.out.println("--");

                            int numStation ;
                            while (true) {
                            if (sc.hasNextInt()) {
                            	numStation = sc.nextInt();
                                break;
                            } else {
                                System.out.println("Erreur: vous devez entrer un entier.");
                                sc.next(); // vide le scanner pour éviter une boucle infinie
                            	}
                            }
                            if(numStation == 0) {
                            	break;
                            }else {
                            playerAction.construireResearchStation(choix.get(numStation - 1).getLocation());;
                            System.out.println("Une station de recherche est déplacée de " + choix.get(numStation - 1).getLocation().getName() +" à " + player.getLocation().getName());
                            actionsLeft--;
                            }
                    	}else {
                            playerAction.construireResearchStation(null);
                            System.out.println("Une station de recherche est construite à " + player.getLocation().getName());
                            actionsLeft--;
                    	}
                    	}else {
                            System.out.println("Votre emplacement et les villes de votre carte ne sont pas les méme");
                    	}
                        break;
                    case 3:
                    	if(playerAction.peuxDecouvrirUnRemede()) {
                            System.out.println("Un remède est découvert à " + player.getLocation().getName());
                            actionsLeft--;
                    	}else if(!player.getLocation().hasResearchStation()) {
                            System.out.println("Vous êtes dans une ville qui n'a pas une station de recherche.");
                    	}else {
                            System.out.println("Vous n'avez pas assez de cartes .");
                    	}
                        break;
                    case 4:
                        List<Disease> diseases = player.getLocation().getDisease();
                        if(diseases.size()>0) {
                            System.out.println("Choisir la maladie à traiter");
                        for (int i = 0; i < diseases.size(); i++) {
                            System.out.println((i+1) + " - " + diseases.get(i).name());
                        }
                        System.out.println("--");
                        System.out.println("0 - Retour au choix des actions");
                        System.out.println("--");
                        int numDisease;
                        while (true) {
                        if (sc.hasNextInt()) {
                        	numDisease = sc.nextInt();
                            break;
                        } else {
                            System.out.println("Erreur: vous devez entrer un entier.");
                            sc.next(); // vide le scanner pour éviter une boucle infinie
                        	}
                        }
                        if(numDisease == 0) {
                        	break;
                        }else if(playerAction.traiterMaladie(diseases.get(numDisease - 1))) {
                            System.out.println("La maladie " + diseases.get(numDisease - 1).name() + " est traitée. Maintenant la ville a " + player.getLocation().getNombreCubeMaladie(diseases.get(numDisease - 1))+ " cubes" );
                            actionsLeft--;
                        }else {
                        	System.out.println("La maladie n'existe pas dans cette ville");
                        }
                        }else {
                            System.out.println(player.getLocation().getName() + " n'a pas des maladies à traiter");
                        }
                        break;
                    case 5:
                    	playerAction.passer();
                        System.out.println("Vous avez choisi de passer");
                        actionsLeft--;
                        break;
                    default:
                        System.out.println("Choix invalide");
                }
            }
            System.out.println(" ");
            System.out.println("--------------Piocher 2 cartes ----------------");

            int nbInfectionCards = gameboard.getTauxGlobalInfection();
            // Phase 2 : le joueur pioche 2 cartes
            for (int c = 0; c < 2; c++) {
            	if(gameboard.getPlayerPile().isEmpty()) {
                    System.out.println("La pile des cartes joueurs est vide !");
                    gameboard.gameOver();
                    break;
            	}else {
            	CardPlayerAndEpidemic card = gameboard.getPlayerPile().get(0);
            	//si la carte est une carte épidémie
            	if(card instanceof EpidemicCard) {
                    System.out.println("Une carte épidémie a été tirée !");
            		//augmenter le taux global d’infection
            		gameboard.augmenterTauxGlobalInfection();
                    System.out.println("Le taux global d'infection est : " + gameboard.getTauxGlobalInfection());
            		//déclencher une phase d’infection
                    System.out.println("Une phase d'infection est déclenchée");
                	gameboard.infectionPhase();
                	//mélanger les cartes de la défausse des cartes infection puis remises sur le dessus de la pile des cartes infection
                	Collections.shuffle(gameboard.getInfectionDefausse());
                	/*for(InfectionCard infectionCard : gameboard.getInfectionDefausse()) {
                		gameboard.getInfectionPile().add(0, infectionCard);
                		gameboard.getInfectionDefausse().remove(infectionCard);
                	}*/
                	Iterator<InfectionCard> iterator = gameboard.getInfectionDefausse().iterator();
                	while (iterator.hasNext()) {
                	    InfectionCard infectionCard = iterator.next();
                	    gameboard.getInfectionPile().add(0, infectionCard);
                	    iterator.remove();
                	}
                	//La carte épidémie est défaussée
                	gameboard.ajouterDansDefaussePlayer(card);
                	//tirer qu’une seule carte sur la pile des cartes infection
                	nbInfectionCards = 1;
            	}else {
                    System.out.println(" ");
            	    System.out.println("Une carte player a été tirée ");
                    player.addCardToHand((Card) card);
                    gameboard.getPlayerPile().remove(card);
                    if(player.getHand().size() > 7) {
                        int nbCards = player.getHand().size() - 7;
                        System.out.println("Vous avez trop de cartes en main, vous devez en défausser " + nbCards);
                        for (int l = 0; l < nbCards; l++) {
                            System.out.print("Cartes en main : ");
                            for (Card carte : player.getHand()) {
                                System.out.print(carte.getCity().getName() + " ");
                            }
                            System.out.println();
                            System.out.println("Choisissez une carte à défausser :");
                            int choice = sc.nextInt() - 1;
                            gameboard.ajouterDansDefaussePlayer((CardPlayerAndEpidemic) player.getHand().get(choice));
                            player.removeCardFromHand(player.getHand().get(choice));
                        }
                    }
            	}
            }
            }
            System.out.println(" ");

            System.out.println("--------------Infection----------------");
            
            // Phase 3 : propagation des maladies
            for (int c = 0; c < nbInfectionCards; c++) {
    			if (!gameboard.getInfectionPile().isEmpty()) {
    		        City infectionCity = gameboard.getInfectionPile().get(0).getCity();
    		        Disease disease = gameboard.getInfectionPile().get(0).getDisease();
    		        //int nbCubes = infectionCity.getNombreCubeMaladie(disease);
    		        //if (nbCubes == 3) {
    		            //System.out.println("La ville " + infectionCity.getName() + " est déjà infectée de 3 cubes !");
    		            //gameboard.gameOver();
    		        //}else {
    	            	if(gameboard.infectionPhase()) {
    	            		if(!gameboard.foyerInfection(infectionCity)) {
        	                System.out.println("Ajout d'un cube de la maladie " + disease.name() + " dans la ville " + infectionCity.getName());
    	                    System.out.println("La ville " + infectionCity.getName() + " est maintenant infectée de "+ infectionCity.getNombreCubeMaladie(disease) +" cubes !");
                            System.out.println(" ");
    	            		}else {
                                System.out.println("Le nombre de foyer d'infection : " + gameboard.getnbFoyersInfection());
    	            		}
    	            	}
    		        //}
    			}else {
    		        System.out.println("La pile d'infection est vide !");
    			}
            }
            
            // Vérification des conditions de fin de partie
            if (gameboard.gameOver()) {
		        System.out.println("La partie est terminée !");
                break;
            }
        }
    }
	}
	
}
