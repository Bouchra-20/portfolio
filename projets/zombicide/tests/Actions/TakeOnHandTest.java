package Actions;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import Equipments.*;
import Players.Player;

public class TakeOnHandTest{
    // Tests the behavior of the execute method when the player has no equipment.
    // It verifies that the  method returns false.
    @Test
    public void testExecuteNoEquipment(){
        Player player=new Player(1,5,3); 
        TakeOnHand takeOnHand=new TakeOnHand(player) ;
        assertFalse(takeOnHand.execute());
    }

    // tests the behavior of the execute method when the player has only one equipment .
    // It verifies that the method returns false .
    @Test
    public void testExecuteOneEquipment(){
        List<Equipment> equipments = new ArrayList<>();
        equipments.add(Weapon.AXE);
        Player player = new Player(1, 5, 3, equipments); 
        TakeOnHand takeOnHand = new TakeOnHand(player);
        assertFalse(takeOnHand.execute());
    }

    // Tests the behavior of the execute() method when the player has multiple equipment .
    // It verifies that the method returns true after selecting one equipment .
    @Test
    public void testExecuteMultipleEquipment() {
        List<Equipment> equipments = new ArrayList<>();
        equipments.add(Weapon.AXE);
        equipments.add(new Glasses());
        Player player = new Player(1, 5,3, equipments) ; 
        TakeOnHand takeOnHand = new TakeOnHand(player);
        assertTrue(takeOnHand.execute());
    }

    // tests the exchange() method when the equipment to exchange is a weapon .
    // it verifies that the weapon is moved to the left hand .
    @Test
    public void testExchangeWeapon(){
        Player player = new Player(1, 5, 3); // Player constructor: expertiseLevel, lifeLevel, actionPoints
        TakeOnHand takeOnHand = new TakeOnHand(player);
        Weapon weapon = Weapon.AXE;
        takeOnHand.exchange(weapon);
        assertEquals(weapon, player.getLeftHandItem());
    }

    // Tests the exchange method when the equipment to exchange is not a weapon.
    // It verifies that the equipment is moved to the right hand.
    @Test
    public void testExchangeEquipment(){
        Player player = new Player(1,5,3) ; 
        TakeOnHand takeOnHand = new TakeOnHand(player);
        Equipment equipment = new Glasses() ;
        takeOnHand.exchange(equipment);
        assertEquals(equipment, player.getRightHandItem());
    }
}
