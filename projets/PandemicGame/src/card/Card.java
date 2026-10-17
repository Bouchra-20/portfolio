package card;

import city.*;
import infection.*;

/**
 * @author omar_, wafa_, bouchra_, el mehdi_
 *
 */
public abstract class Card {
	

	/**
	 * the city
	 */
	protected City city;
	/**
	 * the disease
	 */
	protected Disease disease;

	/**
	 * @param c the city
	 * @param d the disease
	 */
	public Card(City c, Disease d) {
		this.city = c;
		this.disease=d;
	}
	
	/**
	 * @return the city
	 */
	public City getCity() {
		return this.city;
	}

	/**
	 * @return the disease
	 */
	public Disease getDisease() {
		return this.disease;
	}
	
}
