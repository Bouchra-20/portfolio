package players;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.beans.Transient;

import Players.Zombie;
import Players.ZombieType;
public class ZombieTest {

   /**
     * Tests the method that set attributes of zombies.
     * checks that  the properties of zombies are correctly defined
     * according to their type
     * @return void
   */
    @Test
   @Test
   public void setZombie(){
    Zombie zombi1 = new Zombie(ZombieType.ABOMINATION);
    assertEquals(3,zombi1.getDamagePts());
    assertEquals(6,zombi1.getLifePts());
    Zombie zombi2 = new Zombie(ZombieType.RUNNER);
    assertEquals(1,zombi2.getDamagePts());
    assertEquals(2,zombi1.getLifePts());
    Zombie zombi3 = new Zombie(ZombieType.STRONG);
    assertEquals(2,zombi1.getDamagePts());
    assertEquals(4,zombi1.getLifePts());
    Zombie zombi4 = new Zombie(ZombieType.WALKER);
    assertEquals(1,zombi1.getDamagePts());
    assertEquals(1,zombi1.getLifePts());
   } 

   /**
     * tests a zombie's ability to attack a player
     * verifies that the player's life is appropriately reduced
     * after a zombie attack 
     * @return void
     */
   @Test
   public void testAttack(){
      Zombie zombie = new Zombie(ZombieType.RUNNER);
      Player player = new Player();
      int initialLife = player.getLifeLevel();
      zombie.attack(player);
      assertEquals(initialLife - zombie.getDamagePts(), player.getLifeLevel());
   }
   
}
