package fil.l3.coo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import fil.l3.coo.ExceptionsControlled.NotInService; 
import fil.l3.coo.StateController.DeuxRoueState.*;



public class DeuxRouesTest {
    Client c;
    Client pauvre;

    private MockDeuxRoues deuxRoues;

    @BeforeEach
    void setUp() {
        c = new Client("COO", "L3", 45000);
        pauvre = new Client("COO" , "L3 MIAGE" , 50);
        deuxRoues = new MockDeuxRoues();
    }

    @Test
    public void testRetirOk() throws Exception{
        Velo v = new Velo(10, false, 100);
        assertEquals(v, v.Retier());
    }

    @Test
    public void testRetirerNotOk() throws Exception{
        Velo v = new Velo(10, true, 100);
        assertThrows(NotInService.class, ()->{
            v.Retier() ;
        });

    }

    @Test
    public void testLouerNotOk() throws Exception{
        Velo v = new Velo(10, false, 100);
        this.c.Louer(v);/*LOCATION FAITE  */
        assertThrows(NotInService.class, ()->{
            this.c.Louer(v);/*LOCATION IMPOSSIBLE CAR LE DEUX ROUES EST DEJA LOUE */
        });
    }

    @Test
    public void testSetEtatServiceToHorsService() {
        assertFalse(deuxRoues.isHors_service());
        
        deuxRoues.setEtatSerice();
        
        assertTrue(deuxRoues.isHors_service());
        assertTrue(deuxRoues.state instanceof HorsService);
    }
    
    @Test
    public void testSetEtatServiceToOnService() {
        deuxRoues.setEtatSerice();
        assertTrue(deuxRoues.isHors_service());
        
        deuxRoues.setEtatSerice();
        
        assertFalse(deuxRoues.isHors_service());
        assertTrue(deuxRoues.state instanceof OnService);
    }

    @Test
    public void testAskForInterventionWhenUnderLimit() throws Exception {
        deuxRoues.setNb_location(3);
        
        deuxRoues.askForIntervention();
        
        assertFalse(deuxRoues.isHors_service());
        assertFalse(deuxRoues.state instanceof HorsService);
    }

    @Test
    public void testSetToVoleOk() {
        assertFalse(deuxRoues.estVole());
        
        deuxRoues.setToVole();
        
        assertTrue(deuxRoues.estVole());
        assertTrue(deuxRoues.state instanceof Vole);
    }
}
