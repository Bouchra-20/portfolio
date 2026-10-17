package Actions;
import Equipments.*;
import java.util.*;
import Cells.*;



public interface INoiseProducer {
    
    /**
     * Generates a noise event at the specified cell .
     * @param cell the cell where  the noise event is produced.
     */

    void produceNoise(Cell cell);
}
