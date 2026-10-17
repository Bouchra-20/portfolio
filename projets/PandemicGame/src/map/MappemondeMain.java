package map;

import java.io.FileNotFoundException;

import city.City;

/** cette classe initialise une mappemonde, la remplie avec des villes à partir d'un fichier JSon. La liaison entre les villes et les voisines est également faites à partir du fichier json */

public class MappemondeMain {

	public static void main(String[] args) throws FileNotFoundException {
		Mappemonde m = new Mappemonde(); 
		m.initialiserVillesDepuisJson(args[0]);
		System.out.println("Voici la liste des villes de la mappemonde : ");
		 for (City v : m.getCities())
		    {
		    	System.out.println(v.getName());
		    	System.out.println("Qui a pour voisines :");
		    	for (City n : v.getNeighbors() )
		    	{
		    		System.out.println(n.getName());
		    	}
		    	System.out.println("\n");
		    } 

	}

}
