package Equipments;
import Actions.*;
import Board.Board;
import Cells.*;

public class Map extends Equipment<Board> implements INoiseProducer {

    /**
     * Constructs a Map
     */  
    public Map(){
        super(); 
        super.isNoisy = true;
    }
    
    /**
     * Displays the entire city and all acotrs on the board.
     * @param board the game board representing the city
     */
    public boolean Effect(Board board) {
    // Implement the logic of viewing the entire city and all actors
    	board.printBoard();
    	this.use();
    	return true;
    }

    @Override
    public void produceNoise(Cell cell) {
        if (isNoisy) {
            NoiseGenerator.addNoise(cell);
        }
    }
    
    public String symbole(){
        return "🗺️";
    }

    
    public String toString(){
        return "Map";
    }
}
