import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Board.Board;
import Cells.Cell;

class MapTest {

    @Test
    void testEffect() {
        Map map = new Map();
        Board board = new Board();

        assertTrue(map.Effect(board));
        assertTrue(map.isUsed());
    }

    @Test
    void testProduceNoise_Noisy() {
        Map map = new Map();
        map.use();
        Cell cell = new Cell();
        boolean noiseProduced = false;
        try {
            NoiseGenerator.addNoise(cell);
            noiseProduced = true;
        } catch (Exception e) {
            noiseProduced = false;
        }

        assertTrue(noiseProduced);
    }

    @Test
    void testProduceNoise_NotNoisy() {
        Map map = new Map();
        Cell cell = new Cell(); 
        boolean noiseProduced = false;
        try {
            NoiseGenerator.addNoise(cell);
            noiseProduced = true;
        } catch (Exception e) {
            noiseProduced = false;
        }

        assertFalse(noiseProduced);
    }

    @Test
    void testSymbol() {
        Map map = new Map();
        assertEquals("🗺️", map.symbol());
    }

    @Test
    void testToString() {
        Map map = new Map();
        assertEquals("Map", map.toString());
    }
}

