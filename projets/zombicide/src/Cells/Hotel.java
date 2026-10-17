package Cells;
import java.util.List;

public class Hotel extends Room {
    //protected List<Object> objects; // List of specific objects in the hotel
    
    
    /**
     * Constructor of the Hotel class
     * @param x Coordinate x
     * @param y coordinate y
     */
    public Hotel(int x, int y) {
        super(x, y);
        //this.objects = objects;
    }

    /*public List<Object> getObjects() {
        return objects;
    }*/
    public String toString(){
		return "[H]";
	}
}

