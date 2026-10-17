package players;
import org.junit.Test;
import static org.junit.Assert.*;

import java.beans.Transient;

import Players.*;
import Equipments.*;
import Cells.*;

public class PlayerTest{
    
    /**
     * Tests the method to set action points for a player
     * verifies that the action points are correctly set and retrieved 
     * @return void
     */
    @Test
    public void testSetActionPoints(){
        Player player = new Player(3, 3, 10);
        player.setActionPoints(2);
        assertEquals(2, player. getActionPoints());
        assertNotEquals(2, player.getActionPoints());
    }
    
    
    /**
     * Tests the method to set the expertise level for a player
     * verifies that the expertise level is correctly set and retrieved
     * @return void
     */
    @Test
    public void testExpertiseLevel(){
        Player player = new Player(3, 3, 10);
        player.setExpertiseLevel(4);
        assertEquals(4, player.getExpertiseLevel());
        asserNotEquals(6, player.getExpertiseLevel());
        }
    
    /**
     * Tests the method to retrieve the maximum life level for a player
     * verifies that the maximum life level is correctly retrieved
     * @return void
     */
    @Test
    public void testGetMaxLifeLevel(){
        // creating the player 
        Player player = new Player(3, 3, 10);
        system.out.println(" The player should have MaximumLifeLevel of 5");
        assertEquals(5, player.getMaxLifeLevel());
        }
    
    /**
     * Tests the method to set the life level for  a player
     * verifies that  the life level is correctly set and retrieved
     * @return void
     */
    @Test
    public void testSetLifeLevel(){
        Player player = new Player(3, 3, 10);
        player.setLifeLevel(4);
        assertEquals(4, player.getLifeLevel());

      
    }
    
    /**
     * Tests the method to roll a dice for a player
     * verifies that  the result of the dice roll is within the expected range 
     * @return void
     */
    @Test
    public void testRollDice(){
        int result = player.rollDice();
        assertTrue(result >=1 && result <= 6);

    }
    //public void testSearchEquipments(){
    //    List<Equipment> foundEquipments = player.searchEquipments();
    



        //public void testUseWeapon(){
    //    Weapon weapon = new weapon( WeaponType.Gun);
   //     boolean result = player.useWeapon(weapon);

    }



    


    
