package Board;

import Cells.*;
import Board.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class BoardTest {


    // Test method that verifies the height of the board 
    @Test
    public void testGetHeight() {
        Board board = new Board(3, 3);
        assertEquals(3, board.getHeight());
    }
    
    // Test method that verifies the width of the board 
    @Test
    public void testGetWidth() {
        Board board = new Board(3, 3);
        assertEquals(3, board.getWidth());
    }
    
    // Test method that verifies if the board was correctly initialized .
    @Test
    public void testGenerate(){
        // Creating a 7x7 board 
        Board board = new Board(7,7);
        // Generating the board 
        board.generate();
        // Verify if the board was correctly generated 
        Cell[][] cells = board.getBoard();
        assertNotNull(cells);
        assertEquals(7, cells.length);
        assertEquals(7, cells[0].length);
        // Verifies if the cell was initialized correctly 
        for (Cell[] row : cells){
            for (Cell cell : row){
                assertNotNull(cell);
            }
        }
    }
    
    // Test method that verfies if the buildings were intialized .
    @Test
    public void testFillWithBuildings(){
        Board board = new Board(10,10);
        board.generate();
        board.fillWithBuildings(0,0);
        Cell[][] cells = board.getBoard();
        // Verify if the building were correctly initialized .
        for (Cell[] row : cells){
            for(Cell cell : row ){
                if (!(cell instanceof Street)){
                    assertTrue(cell instanceof Room);
                }
            }
        }
    }
    
    // Test method that verifies if the special rooms were placed on the board .
    @Test
    public void testPlaceSpecialRooms(){
        Board board = new Board(10, 10);
        board.generate();
        // Verify if the room were actually placed . 
        board.placeSpecialRooms();
        Cell[][] cells = board.getBoard();
        boolean pharmacyInitialized = false;
        boolean hotelInitialized = false;
        for ( Cell[] row : cells){
            for (Cell cell : row){
                if ( cell instanceof Pharmacy){
                    pharmacyInitialized = true;
                }
                if ( cell instanceof Hotel){
                    hotelInitialized = true;
                }
            }
        }
        assertTrue(pharmacyInitialized);
        assertTrue(hotelInitialized);
    }
    
    // Test method that verifies if the doors were placed .
    @Test 
    public void testAddDoor(){
        Board board = new Board(10,10);
        board.generate();
        board.fillWithBuildings(0, 0);
        board.addDoor();
        Cell[][] cells = board.getBoard();
        for ( Cell[] row : cells){
            for( Cell cell : row ){
                if (!(cell instanceof Street )){
                    Room room = (Room) cell;
                    assertTrue(room.doors().size()>0);
                }
            }
        }
    }
    
    // Test method that verifies if the carrefour was placed .
    @Test
    public void testFindCarrefour(){
        Board board = new Board(10, 10);
        board.generate();
        // Verify if a carrefour is found
        Cell carrefour = board.findCarrefour();
        assertNotNull(carrefour);
        assertTrue(carrefour instanceof Street);
        assertTrue(board.isCarrefour(carrefour.getCoordinateY(), carrefour.getCoordinateY()));
    }

    @Test
    public void testGetDoor(){
        Board board = new Board(10, 10);
        board.generate();
        // Testing with a valid direction 
        Door door = board.getDoor(5, 5, "right");
        assertNotNull(door);
        Dooe soorInvalid = board.getDoor(9, 5, "right");
        assertNull(doorInvalid);
    } 

    @Test
    public void testGetDirectionDoor(){
    Board board = new Board(10, 10);

    Door doorSouth = new Door();
    doorSouth.setDoorNbr(2);
    Room roomSouth = new Room(5,6);
    doorSouth.setRoom(roomSouth);
    assertEquals("S", board.getDirectionDoor(doorSouth));
    Door doorWest = new Door();
    doorWest.setDoorNbr(3);
    Room roomWest = new Room(4, 5);
    door
}

    @Test

}
