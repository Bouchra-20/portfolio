package Equipments;
import Equipments.*;
import Players.* ;
import org.junit.Test;
import static org.junit.Assert.*;

class FirstAidKitTest {

	private FirstAidKit kit;
	private Player p;
    
	/**
     * Sets up the initial state for testing .
     * @throws Exception if an exception occurs during setup
     */
	@Before
	public void setUp() throws  Exception {
		kit = new FirstAidKit(3);
		p = new Player(1,5,10);
	}

    /**
     * Tests the effect of a FirstAidKit on a player's life level.
     * Verifies that the player's life level decreases after using a FirstAidKit.
     * @return void
     */
	@Test
	public void testEffect() {
		kit.Effect(p);
		assertEquals(8, p.getLifeLevel());     
	}
    
	/**
     * Tests the use of a potion from a FirstAidKit
     * verifies that a potion is correctly used, increasing the player's life level
     * and reducing the number of potions in the kit.
     * @return void
     */
	@Test
	public void testUsePotion(){
		try {
			kit.usePotion(p);
			assertEquals(6, p.getLifeLevel());
		    assertEquals(2, kit.getNumbersofPotions());

		} catch (MaxLifeLevelException e){
			fail("Exception lifted");
		}
	}

	/**
     * tests the use of a potion from a FirstAidKit when the player's life level is at maximum
     * verifies that an exception is thrown and the number of potions remains unchanged 
     * @return void
     */
	@Test
	public void testUsePotionWhenLifeAtMax(){
		p.setLifeLevel(p.MAX_LIFE_LEVEL  );
		try {
			kit.usePotion(p);
			fail("Exception lifted");
		} catch (MaxLifeLevelException e){
			assertTrue(e.getMessage().contains("maximum"));
			assertEquals(3, kit.getNumbersofPotions());
		}
	}
    

}



