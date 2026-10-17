package Cells;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import Cells.*;

public class HotelTest {
    // Test the Constructor 
    
    @Test
    public void testConstructor(){
        Hotel hotel = new Hotel(1,3);
        //Check if the coordinates are set correctly 
        assertEquals(1, hotel.getCoordinateX());
        assertEquals(3, hotel.getCoordinateY());
    }
    
    // Checking if the toString() method returns the expected string 
    @Test
    public void StringTest(){
        Hotel h = new Hotel(1, 3);        
        asserEquals("[H]", h.toString());
    }
    
}

