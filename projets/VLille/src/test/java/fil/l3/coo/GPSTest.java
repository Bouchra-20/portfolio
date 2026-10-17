package fil.l3.coo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import fil.l3.coo.DocaratorPattern.GPS;
import static org.junit.jupiter.api.Assertions.*;

class GPSTest {

    private GPS gps;
    private MockVelo velo;

    @BeforeEach
    void setUp() {
        velo = new MockVelo(false, 50, 300);
        gps = new GPS(velo);
    }

    @Test
    void testInitializeQuartiers() {
        gps.initializeNeighborhoods();
        assertTrue(gps.getNeighborhoods().containsKey("Vieux-Lille"));
        assertTrue(gps.getNeighborhoods().containsKey("Lille-Centre"));
    }

    @Test
    void testSimulatePosition() {
        double[] position = gps.simulatePosition();
        assertEquals(2, position.length); 
    }

    @Test
    void testActivateRouteTracking() {
        gps.activateRouteTracking();
        assertTrue(gps.isTracking());
    }

    @Test
    void testStopRouteTracking() {
        gps.activateRouteTracking();
        gps.stopRouteTracking();
        assertFalse(gps.isTracking());
    }

    @Test
    void testGetRouteTracking() {
        gps.activateRouteTracking();
        gps.simulatePosition();
        List<double[]> trajet = gps.getRouteTracking();
        assertNotNull(trajet);
        assertFalse(trajet.isEmpty());
    }
}
