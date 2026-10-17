package Cells;
import org.junit.Test;
import static org.junit.Assert.*;

public class DoorTest {

    //Test method to check if the door is initially closed
    @Test
    public void testDoorInitiallyClosed() {
        Door door = new Door();
        assertFalse(door.isOpen());
    }

    // Test method to check if the door can be opened and closed
    @Test
    public void testOpenAndCloseDoor() {
        // Create a new Door object
        Door door = new Door();
  
        // Check if the door is initially closed
        assertFalse("Door should be initially closed", door.isOpen());
        // Open the door and check if it is open
        door.toOpen();
        assertTrue("Door should be open after calling toOpen()", door.isOpen());
  
        // Close the door and check if it is closed
        door.toClose();
        assertFalse("Door should be closed after calling toClose()", door.isOpen());
    }
    
    // Test to check if the door can be closed after being opened
    @Test
    public void testDoorNumber() {
        Door door = new Door();
        door.setDoorNbr(3);
        assertEquals(3, door.getDoorNbr());
    }
    
}
