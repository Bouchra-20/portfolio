package Equipments;

import org.junit.jupiter.api.Test;

import Equipments.Weapon;
import Equipments.WeaponType;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.Transient;

public class WeaponTest {
    
    /**
     * Tests the method to set attributes of a weapon.
     * verifies that the attributes of the weapon are correctly set according to its type.
     * @return void
     */
    @Test
    public void setAttributes(){
        Weapon weapon1 = new Weapon(WeaponType.AXE);
        assertEquals(1,weapon1.getDamagePts());
        assertEquals(4,weapon1.getThreshold());
        assertEquals(1,weapon1.getDieThrow());
        assertEquals(0,weapon1.getMaxRange());
        assertEquals(0,weapon1.getMinRange());
        assertFalse(weapon1.isNoisy);
        Weapon weapon2 = new Weapon(WeaponType.CHAINSAW);
        assertEquals(3,weapon2.getDamagePts());
        assertEquals(5,weapon2.getThreshold());
        assertEquals(2,weapon2.getDieThrow());
        assertEquals(0,weapon2.getMaxRange());
        assertEquals(0,weapon2.getMinRange());
        assertTrue(weapon2.isNoisy());
        Weapon weapon3 = new Weapon(WeaponType.CROWBAR);
        assertEquals(1,weapon3.getDamagePts());
        assertEquals(4,weapon3.getThreshold());
        assertEquals(1,weapon3.getDieThrow());
        assertEquals(0,weapon3.getMaxRange());
        assertEquals(0,weapon3.getMinRange());
        assertFalse(weapon3.isNoisy());
        Weapon weapon4 = new Weapon(WeaponType.GUN);
        assertEquals(1,weapon4.getDamagePts());
        assertEquals(4,weapon4.getThreshold());
        assertEquals(1,weapon4.getDieThrow());
        assertEquals(1,weapon4.getMaxRange());
        assertEquals(0,weapon4.getMinRange());
        assertTrue(weapon4.isNoisy());
        Weapon weapon5 = new Weapon(WeaponType.SHOTGUN);
        assertEquals(1,weapon5.getDamagePts());
        assertEquals(4,weapon5.getThreshold());
        assertEquals(2,weapon5.getDieThrow());
        assertEquals(3,weapon5.getMaxRange());
        assertEquals(1,weapon5.getMinRange());
        assertTrue(weapon5.isNoisy());
    }
    
    /**
     * Tests the effect of a weapon on a zombie.
     * Verifies that the life points of the zombie are reduced after being attacked by the weapon.
     * @return void
     */
    @Test
    public void testEffect(){
        Weapon weapon = new Weapon(WeaponType.CHAINSAW);
        Zombie zombie = new Zombie(ZombieType.WALKER);
        int initialLife = zombie.getLifePts();
        weapon.Effect(zombie);
        assertTrue(initialLife >= zombie.getLifePts());
    }
    
    /**
     * tests whether a weapon is noisy or not
     * verifies that a noisy weapon returns true for isNoisy and a silent weapon returns false
     * @return void
     */
    @Test
    public void testIsNoisy(){
        Weapon noisyWeapon = new Weapon(WeaponType.GUN);
        Weapon silentWeapon = new Weapon(WeaponType.CROWBAR);
        assertTrue(noisyWeapon.isNoisy());
        assertFalse(silentWeapon.isNoisy());
        }
    /**
     * Tests the  production of noise by a weapon .
     * verifies that a weapon produces noise in a cell after an action
     * @return void
     */
    @Test
    public void testProduceNoise(){
        Weapon noisyWeapon = new Weapon(WeaponType.SHOTGUN);
        Cell cell = new Cell();
        noisyWeapon.produceNoise(cell);
        assertTrue(cell.hasNoise());
    }

}
