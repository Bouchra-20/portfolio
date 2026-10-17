package Cells; 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class RoomTest {
    
    //Test for the createDoors() method in the Room class
    @Test
    public void testCreateDoors() {
        Room room1 = new Room(0, 0);
        room1.createDoors(5, 7);
        assertEquals(2, room1.doors.size());

        Room room2 = new Room(1, 1);
        room2.createDoors(5, 7);
        assertEquals(4, room2.doors.size());

        Room room3 = new Room(2, 2);
        room3.createDoors(5, 7);
        assertEquals(4, room3.doors.size());

        Room room4 = new Room(4, 6);
        room4.createDoors(5, 7);
        assertEquals(2, room4.doors.size());
    }
}

