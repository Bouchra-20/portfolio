package map;

import city.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener; 




public class Mapmain {
	
	
	
	
	public static void main(String args[]) throws FileNotFoundException { 
		
		ArrayList<City> listc = new ArrayList<City>();
		String filename = args[0];
		Map<String,City> mapcity= new HashMap<String, City>();
		FileReader reader = new FileReader(filename); 
		Mappemonde m= new Mappemonde();
		
	    JSONObject map = new JSONObject(new JSONTokener(reader));
	    
	    {
		    
		    Iterator<String> entries = map.keys();
		    System.out.println("Affichage des clés");
		    while (entries.hasNext()) {
		    	System.out.println(entries.next());
		    }
	    }
	    
	    {
		    

		    System.out.println("Affichage des villes et de ses Maladies");
		    
		    String cities = "cities";//entries.next();
		    System.out.println(cities);
		    JSONObject entry = map.getJSONObject(cities);
		    Iterator<String> datakeys = entry.keys();
		    while (datakeys.hasNext()) {
		    	String cityName = datakeys.next();
		    	int maladie = entry.getInt(cityName);
			    System.out.println(cityName+ " "+maladie); 
			    City c = new City(cityName,maladie);  
			    mapcity.put(cityName,c);
			    
			    
		    }
		    
		    System.out.println("Affichage des voisines de chaque villes");
		    
		    String neighbors = "neighbors";
		    System.out.println(neighbors);
		    JSONObject a = map.getJSONObject(neighbors);
		    Iterator<String> datakeys2 = a.keys();
		    while (datakeys2.hasNext()) {
		    	String cityName = datakeys2.next();
		    	JSONArray voisines = a.getJSONArray(cityName); 
		    	
		    	for (Object voisineName : voisines) { 
		    		
		    		mapcity.get(cityName).setNeighbor(mapcity.get(voisineName));
		    		}
			    System.out.println(cityName+ " "+voisines);
		    }
		    	
		    
	    }
	   
	     
	    
	    Collection<City> collectionvilles =  mapcity.values();

	   
	    
  for (City c3 : collectionvilles)
  { 
	  listc.add(c3);
	  
  }
   
    m.setCities(listc); 
    

	    System.out.println("resultat final");
	  //  System.out.println(m.getCities());
	    
	    for (City v : m.getCities())
	    {
	    	System.out.println(v.getName());
	    	for (City n : v.getNeighbors() )
	    	{
	    		System.out.println(n.toString());
	    	}
	    	
	    	
	    } 
	    
	    
	}
}
