package city;

/**
 * @author omar_, wafa_, bouchra_, el mehdi_
 *
 */
public class ResearchStation {

	/**
	 *  the city
	 */
	private City location;
	
	/**
	 * @param location the city
	 */
	public ResearchStation() {
		
	}
	
	/**get a city
	 * @return the city
	 */
	public City getLocation() {
		return location;
	}
	
	
	/**set a city
	 * @param city the city
	 */
	
	public void setLocation(City city) {
		this.location = city;
		
	}
	
}
