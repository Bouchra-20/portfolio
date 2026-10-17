package Cells;

import static org.junit.Assert.*;
import org.junit.Test;

public class StreetTest {
    
    // Test method for the Street class constructor.
    @Test
    public void testConstructor() {
        Street street = new Street(3,5);
        assertFalse(street.hasSewer());
    }
    
    // Test method for the addSewer() method in the Street class.
    @Test
    public void testAddSewer() {
        Street street = new Street(3, 5);
        assertFalse(street.hasSewer());
        street.addSewer();
        assertTrue(street.hasSewer());
    }
    
    // Test method for the hasSewer() method in the Street class
    @Test
    public void testHasSewer() {
        Street street = new Street(3,5);
        assertFalse(street.hasSewer());
        street.addSewer();
        assertTrue(street.hasSewer());
    }
}
