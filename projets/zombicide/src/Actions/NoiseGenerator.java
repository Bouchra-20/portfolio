package Actions;

import Equipments.*;
import java.util.*;
import Cells.*;

public class NoiseGenerator {
    private static class NoiseInfo {
        int x, y, noiseLevel;

        NoiseInfo(int x, int y, int noiseLevel) {
            this.x = x;
            this.y = y;
            this.noiseLevel = noiseLevel;
        }
    }

    private static final List<NoiseInfo> noiseEvents = new ArrayList<>();


    /**
     * Increments the noise level for the specified cell by one.
     * @param cell The cell where the noise event occurs.
     */
    public static void addNoise(Cell cell) {
        // Find the noise info for the cell if it exists
        Optional<NoiseInfo> existingNoise = noiseEvents.stream()
                .filter(n -> n.x == cell.getCoordinateX() && n.y == cell.getCoordinateY())
                .findFirst();

        // If there's existing noise info, increment its noise level
        if (existingNoise.isPresent()) {
            NoiseInfo noiseInfo = existingNoise.get();
            noiseInfo.noiseLevel++;
            System.out.println("\u001B[31m"+"Noise incremented at [" + cell.getCoordinateX() + "," + cell.getCoordinateY() + "] to level " + noiseInfo.noiseLevel+"\u001B[0m");
        } else {
            // Otherwise, add a new noise info with a level of 1
            noiseEvents.add(new NoiseInfo(cell.getCoordinateX(), cell.getCoordinateY(), 1));
            System.out.println("\u001B[31m"+"Noise started at [" + cell.getCoordinateX() + "," + cell.getCoordinateY() + "] with level 1"+"\u001B[0m");
        }
    }

    /**
     * Resets all noise events, clearing the list and effectively setting the game's noise level to 0.
     */
    public static void resetNoise() {
        noiseEvents.clear();
        System.out.println("\u001B[31m"+"Noise levels reset."+"\u001B[0m");
    }

    /**
     * Gets the noise event with the highest noise level.
     * @return An Optional containing the NoiseInfo of the event with the highest noise level, if any exist.
     */
    public static Optional<NoiseInfo> getMaxNoiseEvent() {
        return noiseEvents.stream().max(Comparator.comparingInt(n -> n.noiseLevel));
    }

    /**
     * Prints the details of the noise event with the highest noise level.
     */
    public static void printMaxNoiseEvent() {
        Optional<NoiseInfo> maxEvent = getMaxNoiseEvent();
        maxEvent.ifPresent(event -> System.out.println("\u001B[31m"+"Max noise at [" + event.x + "," + event.y + "] with level " + event.noiseLevel+"\u001B[0m"));
    }


    
}