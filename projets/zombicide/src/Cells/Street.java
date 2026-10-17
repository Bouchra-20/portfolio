package Cells;
public class Street extends Cell{
	
	private Sewer Hole;
	
	private boolean hasSewer;
	/**
	 * Constructor of Rue which is an empty case
	 * @param x coordinate Y
	 * @param y Coordinate X
	 */
	public Street(int x,int y) {
		super(x,y);
		this.hasSewer=false;
	}

	/**
 	* method that adds Egout to Rue
 	*/
	public void addSewer() {
		this.hasSewer=true;
		this.Hole=new Sewer();
	}

	/**method that returns true if Rue has Egout in it and false otherwise
	 * @return hasEgout boolean
	 */
	public boolean hasSewer(){
		return this.hasSewer;
	}
	
	// method that returns a string 
	public String toString(){
		return "[St]";
	}
	
	/**
	 * method that gets the sewer .
	 * @return
	 */
	public Sewer getSewer(){
		return this.Hole;
	}
}
