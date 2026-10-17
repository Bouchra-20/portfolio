import java.io.FileNotFoundException;
import java.util.*;

import city.*;
import game.*;
import map.*;
import player.*;
import card.*;
import infection.*;
import action.*;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		
		Mappemonde mappemonde = new Mappemonde(); 
		mappemonde.initialiserVillesDepuisJson(args[0]);
		
	    final int NUM_Pile = 4;
	    final int NUM_Player ;

		
		GameBoard gameboard = new GameBoard();
		Game game = new Game(gameboard);
		
		/*for(City city : mappemonde.getCities()) {
	        System.out.println(city);
		}*/
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrez le nombre de joueurs ");

        int nbPlayer ;
        while (true) {
            if (scanner.hasNextInt()) {
            	nbPlayer = scanner.nextInt();
                break;
            } else {
                System.out.println("Erreur: vous devez entrer un entier.");
                scanner.next(); // vide le scanner pour éviter une boucle infinie
            }
        }
        NUM_Player = nbPlayer ;
		/*
		Players player1 = new Players("Wafa");
		Players player2 = new Players("Omar");
		Players player3 = new Players("Mehdi");
		Players player4 = new Players("Bouchra");
		
		// Mettre les players dans une liste
        ArrayList<Players> playersList = new ArrayList<>();
        playersList.add(player1);
        playersList.add(player2);
        playersList.add(player3);
        playersList.add(player4);
		*/
		
		// Mettre les players dans une liste
        for (int i = 0; i < NUM_Player; i++) {
            Players player = new Players("Player " + (i+1));
            game.playersList.add(player);
        }
                
		// Mettre les players dans une ville
		//City c1 = new City("Paris", 1);
		
        // Génération d'un nombre aléatoire entre 0 et la taille de la liste
        Random rand = new Random();
        int index = rand.nextInt(mappemonde.getCities().size());
        
        // Récupération de l'élément aléatoire
        City c1 = mappemonde.getCities().get(index);
        
        for (Players player : game.playersList) {
    		player.setLocation(c1);
        }
		
        System.out.println("Les players sont dans la ville de "+ c1.getName());
        System.out.println(" ");

        /*
        player1.setLocation(c1);
		player2.setLocation(c1);
		player3.setLocation(c1);
		player4.setLocation(c1);
		*/
		
        // Construction de la station de recherche sur la ville de départ
		ResearchStation researchstation = new ResearchStation();
        c1.setResearchStation(researchstation);
        System.out.println(c1.getName() + " a une station de recherche");
        System.out.println(" ");

        //Distribuer les roles à chaque joueur
        Scientifique scientifique = new Scientifique(game.playersList.get(0),gameboard);
        System.out.println(game.playersList.get(0).getName() + " a le role scientifique");
        game.playersRole.add(scientifique);
        
        Globetrotter globetrotter = new Globetrotter(game.playersList.get(1), mappemonde,gameboard);
        System.out.println(game.playersList.get(1).getName() + " a le role globetrotter");
        game.playersRole.add(globetrotter);
        
        if(game.playersList.size() >= 3) {
            Expert expert = new Expert(game.playersList.get(2),gameboard);
            System.out.println(game.playersList.get(2).getName() + " a le role expert");
            game.playersRole.add(expert);
        }

        if(game.playersList.size() == 4) {
            Medecin medecin = new Medecin(game.playersList.get(3),gameboard);
            System.out.println(game.playersList.get(3).getName() + " a le role medecin");
            game.playersRole.add(medecin);
        }
        
        /*List<String> availableRoles = new ArrayList<String>();
        availableRoles.add("scientifique");
        availableRoles.add("globetrotter");
        availableRoles.add("expert");
        availableRoles.add("medecin");

        System.out.println("Roles disponibles: ");
        for (int i = 0; i < availableRoles.size(); i++) {
            System.out.println(i + 1 + " - " + availableRoles.get(i));
        }
        System.out.print("Choisissez votre role en entrant le numero correspondant: ");
        int roleIndex = scanner.nextInt() - 1;
        switch (roleIndex) {
        case 0:
            Scientifique scientifique = new Scientifique(game.playersList.get(0));
            game.playersRole.add(scientifique);
            availableRoles.remove(roleIndex);
            System.out.println(game.playersList.get(0).getName() + " a le role scientifique");
            break;
        case 1:
            Globetrotter globetrotter = new Globetrotter(game.playersList.get(1), mappemonde);
            game.playersRole.add(globetrotter);            
            availableRoles.remove(roleIndex);
            System.out.println(game.playersList.get(1).getName() + " a le role globetrotter");
            break;
        case 2:
            Expert expert = new Expert(game.playersList.get(2));
            game.playersRole.add(expert);
            availableRoles.remove(roleIndex);
            System.out.println(game.playersList.get(2).getName() + " a le role expert");
            break;
        case 3:
            Medecin medecin = new Medecin(game.playersList.get(3));
            game.playersRole.add(medecin);
            availableRoles.remove(roleIndex);
            System.out.println(game.playersList.get(3).getName() + " a le role medecin");
            break;
    }*/
		
		// Création de la liste des maladies
        List<Disease> diseases = Arrays.asList(Disease.BLUE, Disease.RED, Disease.YELLOW, Disease.BLACK);

        // Initialise player cards
        
        // Associer aléatoirement une maladie à une playerCard
        Collections.shuffle(diseases);
        ArrayList<PlayerCard> playerCards = new ArrayList<>();
        int i= 0;
        for (City city : mappemonde.getCities()) {
            Disease disease = diseases.get(i % diseases.size());
            playerCards.add(new PlayerCard(city, disease));
            i++;
        }
        Collections.shuffle(playerCards);
        
        System.out.println("------------------------------------- ");
        System.out.println("------------------------------------- ");
        // On distribue les cartes joueur aux joueurs
        int numCardsPerPlayer = (game.playersList.size() == 2) ? 4 : (game.playersList.size() == 3) ? 3 : 2;
        for (Players player : game.playersList) {
            System.out.println(" ");
            System.out.println(player.getName() + " a les cartes suivantes : ");
            System.out.println(" ");

            for (int j = 0; j < numCardsPerPlayer; j++) {
                player.addCardToHand(playerCards.get(0));
                System.out.println("----------------------------------------------- ");
                System.out.println("La carte " + (j+1) + " : " + playerCards.get(0).getCity().getName() + " maladie : " + playerCards.get(0).getDisease().name());
                System.out.println("----------------------------------------------- ");
                playerCards.remove(0);
            }
        }
        
        
        // On prépare les piles de cartes joueur pour le jeu
        int numCardsPerStack = playerCards.size() / NUM_Pile;
        for (int p = 0; p < NUM_Pile; p++) {
            List<CardPlayerAndEpidemic> playerDeck = new ArrayList<>();
            for (int j = 0; j < numCardsPerStack; j++) {
            	playerDeck.add(playerCards.get(0));
                playerCards.remove(0);
            }
            //ajout d'une carte epidemic
            playerDeck.add((CardPlayerAndEpidemic) new EpidemicCard());
            Collections.shuffle(playerDeck);
            Collections.shuffle(playerDeck);

            //ajout des cartes players dans PilePlayer
            for(CardPlayerAndEpidemic playercard : playerDeck) {
                gameboard.ajouterDansPilePlayer(playercard);
            }
        }
        
        // Initialise infection cards

        // Associer aléatoirement une maladie à chaque ville et les mettre dans pileInfectionCard
        Collections.shuffle(diseases);
        ArrayList<InfectionCard> infectionCards = new ArrayList<>();
        int j = 0;
        for (City city : mappemonde.getCities()) {
            Disease disease = diseases.get(j % diseases.size());
            infectionCards.add(new InfectionCard(city, disease));
            j++;
        }
        //Melange des cartes infection
        Collections.shuffle(infectionCards);
        //Ajout des cartes infection dans pileInfection
        for(InfectionCard infectionCard : infectionCards) {
            gameboard.ajouterDansPileInfection(infectionCard);
        }

        gameboard.initCubesMaladie();

        System.out.println("---------------------------------- ");

        // Infecter les villes initiales
        for (int h = 0; h < 3; h++) {
            InfectionCard card = gameboard.getInfectionPile().get(0);
            gameboard.getInfectionPile().remove(0);
            Disease dis = card.getDisease();
            City city = card.getCity();
            gameboard.infectCityDisease(city, 3,dis);
            gameboard.ajouterDansDefausseInfection(card);
         
            
            System.out.println(city.getName() + " est infectée par 3 cubes de la maladie " + dis.name());
        }
        System.out.println(" ");

        for (int h = 0; h < 3; h++) {
            InfectionCard card = gameboard.getInfectionPile().get(0);
            gameboard.getInfectionPile().remove(0);
            Disease dis = card.getDisease();
            City city = card.getCity();
            gameboard.infectCityDisease(city, 2,dis);
            gameboard.ajouterDansDefausseInfection(card);
            
            System.out.println(city.getName() + " est infectée par 2 cubes de la maladie " + dis.name());

        }
        System.out.println(" ");

        for (int h = 0; h < 3; h++) {
            InfectionCard card = gameboard.getInfectionPile().get(0);
            gameboard.getInfectionPile().remove(0);
            Disease dis = card.getDisease();
            City city = card.getCity();
            gameboard.infectCityDisease(city, 1,dis);
            gameboard.ajouterDansDefausseInfection(card);
            
            System.out.println(city.getName() + " est infectée par 1 cube de la maladie " + dis.name());

        }
        System.out.println(" ");

        gameboard.initResearchStations();

        

        game.Play();

     // Affichage du résultat
        for (Disease disease : Disease.values()) {
            if (gameboard.getNbCubeMaladieRestant(disease) == 0 && !gameboard.remedeTrouvee(disease)) {
                System.out.println("Partie perdue : plus de cube disponible pour une maladie");
                break;
            }
        }
        if (gameboard.getnbFoyersInfection() >= 8) {
            System.out.println("Partie perdue : trop de foyers d'infection");
        } else if (gameboard.getPlayerPile().size() == 0) {
            System.out.println("Partie perdue : plus de cartes joueur");
        } else {
            System.out.println("Partie gagnée !");
        }
	}
}
