package Cells;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import Board.*;
import Players.*;
import Cells.*;

public class CellTest {
    
    //Test method for the getCoordinateX method
    @Test
    public void testGetCoordinateX() {

        Cell cell = new Cell(3, 5);
        int result = cell.getCoordinateX();
        assertEquals(3, result);
    }
    
    //Test method for the getCoordinateX method
    @Test
    public void testGetCoordinateY() {
        Cell cell = new Cell(3, 5);
        int result = cell.getCoordinateY();
        assertEquals(5, result);
    }
 
    /** 
    //Test method for the Setters and Getters of Zombie
    @Test
    public void testZombieSetter(){
        Cell cell = new Cell(2, 3);
        Zombie zombie = new Zombie(ZombieType.ABOMINATION);
        //Set the zombie in the cell
        cell.setZombie(zombie);
        //Checking if the zombie is in the cell
        assertTrue(cell.hasZombie());
    }

    @Test
    public void testZombieGetter(){
        Cell cell = new Cell(2,3);
        Zombie zombie = new Zombie(ZombieType.ABOMINATION);
        cell.setZombie(zombie);
        asserEquals(zombie, cell.getZombie());

    }
    // Test methods for the Player getters and setters 
    @Test
    public void testPlayerSetter(){
        Cell cell = new Cell(2,3);
        Player player = new Player(4, 3, 4);
        //Setting the player in the cell
        cell.setPlayer(player);
        //:Checking if the player is in the Cell
        assertTrue(cell.hasPlayer());
    }

    @Test
    public void testPlayerGetter(){
        Cell cell = new Cell(3,5);
        Player player = new Player(4, 3, 4);
        cell.setPlayer(player);
        assertEquals(player, cell.getPlayer());
    }
    */

}
