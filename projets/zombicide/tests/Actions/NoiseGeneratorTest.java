import static org.junit.Assert.*;
import org.junit.Test;
import Cell.*;
import Board.*;
import Actions.*;
import Players.*;
import Equipment.*;


public class NoiseGeneratorTest {

    // tests the addNoise method by adding noise to a cell and checking if the noise level increases . 
    @Test
    public void testAddNoise(){
        Cell cell = new Cell(0, 0);
        NoiseGenerator.addNoise(cell);
        assertTrue(NoiseGenerator.getMaxNoiseEvent().isPresent());
        assertEquals(1, NoiseGenerator.getMaxNoiseEvent().get().noiseLevel);
    }

    // Tests the resetNoise method by adding noise to a cell, resetting the noise, and checking if noise levels are cleared.
    @Test
    public void testResetNoise(){
        Cell cell = new Cell(0, 0);
        NoiseGenerator.addNoise(cell);
        NoiseGenerator.resetNoise();
        assertFalse(NoiseGenerator.getMaxNoiseEvent().isPresent());
    }

    // tests the getMaxNoiseEvent method by adding noise to multiple cells and checking if the max noise event is returned correctly .
    @Test
    public void testGetMaxNoiseEvent(){
        Cell cell1 = new Cell(0, 0);
        Cell cell2 = new Cell(1, 1);
        NoiseGenerator.addNoise(cell1);
        NoiseGenerator.addNoise(cell1);
        NoiseGenerator.addNoise(cell2);
        NoiseGenerator.addNoise(cell2);
        NoiseGenerator.addNoise(cell2);
        assertTrue(NoiseGenerator.getMaxNoiseEvent().isPresent());
        assertEquals(3, NoiseGenerator.getMaxNoiseEvent().get().noiseLevel);
    }
}
