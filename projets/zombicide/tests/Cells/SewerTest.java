package Cells;
import org.junit.*;
import static org.junit.Assert.*;

public class SewerTest {

    @Test
    public void testSewer() {
        Sewer sewer = new Sewer(10, 20);

        //The initial number of zombies in Egout should be 10
        assertEquals(10, sewer.getNumbrZomb());

        //The initial number of zombies that can come out is 20
        assertEquals(20, sewer.getSewerZombout());

        //The initial Number of zombies that have passed should be 0
        assertEquals(0, sewer.getZombPasses());

        sewer.setNumbrZomb(15);
        //Number of zombies in Egout should be updated to 15
        assertEquals(15, sewer.getNumbrZomb());

        sewer.setSewerZombout(25);
        //Number of zombies that can come out should be updated to 25
        assertEquals(25, sewer.getSewerZombout());

        //Should allow passage for 5 zombies
        assertEquals(true, sewer.letZombiePass(5));

        //Total zombies passed should be updated to 5
        assertEquals(5, sewer.getZombPasses());

        //Should refuse passage for 20 zombies as it exceeds capacity
        assertEquals(false, sewer.letZombiePass(30));

        //Total zombies passed should remain at 5
        assertEquals(5,sewer.getZombPasses());
    }

    /**
     * Tests the letZombiePass method when the number of zombies trying to pass is within the limit .
     * It verifies that the method returns true and updates the number of zombies that have passed .
     */
    @Test
    public void testLetZombiePassWithinLimit(){
        Sewer sewer=new Sewer();
        sewer.setSewerZombout(5) ; 
        int initialZombiesPassed=sewer.getZombPasses();
        boolean result=sewer.letZombiePass(3); 
        assertTrue(result) ; 
        assertEquals(initialZombiesPassed+3,sewer.getZombPasses()); 
    }
    
    /**
     * tests the letZombiePass method when the number of zombies trying to pass exceeds the limit .
     * it verifies that the method returns false and does not update the number of zombies that 
     * have passed .
     */
    @Test
    public void testLetZombiePassExceedLimit(){
        Sewer sewer = new Sewer() ;
        sewer.setSewerZombout(5) ;
        sewer.letZombiePass(4) ; 
        int initialZombiesPassed = sewer.getZombPasses();
        boolean result = sewer.letZombiePass(3) ; 
        assertFalse(result); 
        assertEquals(initialZombiesPassed,sewer.getZombPasses()); 
    }

    /**
     * tests the addZombie method by adding a zombie to the sewer.
     * it verifies that the zombie is successfully added to the sewer .
     */
    @Test
    public void testAddZombie(){
        Sewer sewer=new Sewer();
        Zombie zombie = new Zombie();
        sewer.addZombie(zombie);
        assertEquals(1,sewer.getZombie().size()); 
    }

}

