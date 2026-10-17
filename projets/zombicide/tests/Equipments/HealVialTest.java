package Equipments;
import Equipments.HealVial;
import Players.Player;

import static org.junit.Assert.*;
import org.junit.Test;

public class HealVialTest{

/**
 * tests the effect of a HealVial on a player's life level .
 * verifies that the player's life level increases after using a HealVial
 * and that it does not exceed the maximum life level.
 * @return void
 */
@Test
private static void testHealVialEffect(){
    // Create a player 
    Player player = new Player(2,5,3);
    player.setLifeLevel(5); // Define an actuall lifeLevel
    //player.setMaxLifeLevel(10); // Define the  maximum lifeLevel

    HealVial healVial = new HealVial();
    assertEquals(5, player.getLifeLevel());
    healVial.Effect(player);
    assertEquals(6, player.getLifeLevel());
    // Reintializes the LifeLevel for the next test.
    player.setLifeLevel(10);
    // Apply the effect of HealingVial on the player. 
    healVial.Effect(player);
    // Checks if the lifeLevel stays the same. 
    assertEquals(10, player.getLifeLevel());


}

}