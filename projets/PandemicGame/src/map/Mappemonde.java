package map;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import city.City;

public class Mappemonde {	
	
	
	/**
	 * list of the cities
	 */
	protected List<City> cities;
	
	public Mappemonde() {
		this.cities=new ArrayList<City>();
		
	}
	
	/**
	 * @return the list of cities in Mappemonde
	 */
	public List<City> getCities(){
		return this.cities;
	}
	 
	/**
	 * @param cities create a list of cities
	 */
	public void setCities(List<City> cities)
	{ 
		this.cities=cities;
	}
	
	
	/**
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public void initialiserVillesDepuisJson (String filename) throws FileNotFoundException { 
			
			List<City> listc = new ArrayList<City>();
			Map<String,City> mapcity= new HashMap<String, City>();
			FileReader reader = new FileReader(filename); 
			//Mappemonde m= new Mappemonde();
			
		    JSONObject map = new JSONObject(new JSONTokener(reader));
	
			    String cities = "cities";//entries.next();
			    JSONObject entry = map.getJSONObject(cities);
			    Iterator<String> datakeys = entry.keys();
			    while (datakeys.hasNext()) {
			    	String cityName = datakeys.next();
			    	int maladie = entry.getInt(cityName);
				    City c = new City(cityName,maladie);  
				    mapcity.put(cityName,c);
				    
			    }
			    
			    String neighbors = "neighbors";
			    JSONObject a = map.getJSONObject(neighbors);
			    Iterator<String> datakeys2 = a.keys();
			    while (datakeys2.hasNext()) {
			    	String cityName = datakeys2.next();
			    	JSONArray voisines = a.getJSONArray(cityName); 
			    	
			    	for (Object voisineName : voisines) { 
			    		
			    		mapcity.get(cityName).setNeighbor(mapcity.get(voisineName));
			    		}
			    }
			    	
		    
		    Collection<City> collectionvilles =  mapcity.values();
	
	    
	    for (City ca : collectionvilles)
	    {
	    	 listc.add(ca);
	    }
	    this.setCities(listc); 
    
		}
		

}
