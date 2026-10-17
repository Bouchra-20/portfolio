package fil.l3.coo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import fil.l3.coo.ExceptionsControlled.PlacementProbleme;


public class EmplacementTest {
    private Emplacement s;
    private Locations v;

    @BeforeEach
    public void setUp() {
        s = new Emplacement();
        v = new Velo(400, false, 700);
    }
    @Test
    void testGetID(){
        Emplacement e = new Emplacement();
        Emplacement e1 = new Emplacement();
        assertEquals(e.getID()+1, e1.getID());
    }

    @Test
    void RetirerOk() throws Exception {
        s.Deposer(v);
        Locations e = s.Retirer();
        assertEquals(v, e);
        assertFalse(s.isOccupe());
        
    }


    @Test
    void DeposerOk()throws Exception {
        s.Deposer(v);
        assertEquals(s.getLocations(),v );
        assertTrue(s.isOccupe());
    }


    @Test
    void testGetIDUnique() {
        Emplacement e1 = new Emplacement();
        Emplacement e2 = new Emplacement();
        assertNotEquals(e1.getID(), e2.getID());
    }

    @Test
    void testRetirerThrowsExceptionWhenEmpty() throws Exception {
        assertThrows(PlacementProbleme.class, () -> {
            s.Retirer();
        });
    }

    @Test
    void testDeposerThrowsExceptionWhenOccupied() throws Exception {
        s.Deposer(v);
       assertThrows(PlacementProbleme.class, () -> {
            s.Deposer(new Velo(300, true, 500));
        });
    }
    
}
