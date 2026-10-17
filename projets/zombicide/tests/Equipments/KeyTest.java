package Equipments;
import Cells.Door;

import static org.junit.Assert.*;
import org.junit.Test;

public class KeyTest{
    
	/**
     * Tests the effect of a key on a locked door
     * Verifies that the door becomes unlocked after using the correct key .
     * @return void
     */
    @Test
	public void testEffectUnclock(){
    	Key key = new Key(1);
    	Door door = new Door(1);
    	key.Effect(door);
    	assertTrue( door.isOpen());
	}
    
	/**
     * Tests the effect of a key on an already unlocked door.
     * verifies that the key doesn't unlock the door if it's already unlocked .
     * @return void
     */
	@Test
	public void testEffectAlreadyUnlocked(){
		Key key = new Key(2);
		Door door = new Door(2);
		door.toOpen();
		key.Effect(door); // prints "the Key doesn't match the Padlock."
	}

}
