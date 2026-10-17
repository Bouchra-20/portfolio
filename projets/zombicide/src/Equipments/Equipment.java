package Equipments;

public abstract class Equipment<P> {

	protected boolean isUsed ;
	protected boolean isNoisy;
	
	/**
	 * Constructs a new Equipment object which is not in use and not noisy.
	 */
	public Equipment() {
		this.isUsed = false;
		this.isNoisy = false;
	}

	/**
	 * Applies the effect of the equipment with the given parameter.
	 * @param parameter the object  upon which a certain effect is applied 
	 */
	public abstract boolean Effect(P parameter) ;
	
	/**
	 * Cheks if the quipment is currently in used
	 * @return true if the equipment is in the use, false if it's not 
	 */
	public boolean isUsed() {
		return isUsed;
	}

	/**
	 * Sets the quipment to be in use.
	 */
	public void use() {
		this.isUsed = true;
	}
	public String toString(){
		return "Equipment";
	}
	
	public String symbole(){
		return "";
	}
}
