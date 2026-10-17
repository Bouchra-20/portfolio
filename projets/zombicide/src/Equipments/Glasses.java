package Equipments;
import Cells.*;
import Board.Board;
import java.util.*;

public class Glasses extends Equipment<Cell> {
    
    private Cell cellPos; // the cell position where the glasses are used from  
    protected Board board; // the board  
    
    /**
     * Glasses Constructor with a specified cell position
     * @param cell the from which the glasses are used
     * @param board board the game board
     */
    public Glasses(Board board) {
        super();
        this.board = board;
    }

    /**
     * Method to get information from the neighbouring cell
     * @param direction the direction targeted by the Nosy 
     */
    public boolean Effect(Cell cellPos) {
        
        // getting the coordinates of the cell 
        int x = cellPos.getCoordinateX();
        int y = cellPos.getCoordinateY();
        if(x-1>=0) {
        	if(y-1>=0) {
        		if(x+2<this.board.getWidth()) {
            		if(y+2<this.board.getHeight()) {
            			this.board.printBoard2(x-1,y-1,x+2,y+2);
            		}else if(y+1<this.board.getHeight()){
            			this.board.printBoard2(x-1,y-1,x+2,y+1);
            		}else {
            			this.board.printBoard2(x-1,y-1,x+2,y);
            		}
            	}else if(x+1<this.board.getWidth()) {
            		if(y+2<this.board.getHeight()) {
            			this.board.printBoard2(x-1,y-1,x+1,y+2);
            		}else if(y+1<this.board.getHeight()){
            			this.board.printBoard2(x-1,y-1,x+1,y+1);
            		}else {
            			this.board.printBoard2(x-1,y-1,x+1,y);
            		}
            	}else {
            		if(y+2<this.board.getHeight()) {
            			this.board.printBoard2(x-1,y-1,x,y+2);
            		}else if(y+1<this.board.getHeight()){
            			this.board.printBoard2(x-1,y-1,x,y+1);
            		}else {
            			this.board.printBoard2(x-1,y-1,x,y);
            		}
            	}
        	}else {
        		if(x+2<this.board.getWidth()) {
            		if(y+2<this.board.getHeight()) {
            			this.board.printBoard2(x-1,y,x+2,y+2);
            		}else if(y+1<this.board.getHeight()){
            			this.board.printBoard2(x-1,y,x+2,y+1);
            		}else {
            			this.board.printBoard2(x-1,y,x+2,y);
            		}
            	}else if(x+1<this.board.getWidth()) {
            		if(y+2<this.board.getHeight()) {
            			this.board.printBoard2(x-1,y,x+1,y+2);
            		}else if(y+1<this.board.getHeight()){
            			this.board.printBoard2(x-1,y,x+1,y+1);
            		}else {
            			this.board.printBoard2(x-1,y,x+1,y);
            		}
            	}else {
            		if(y+2<this.board.getHeight()) {
            			this.board.printBoard2(x-1,y,x,y+2);
            		}else if(y+1<this.board.getHeight()){
            			this.board.printBoard2(x-1,y,x,y+1);
            		}else {
            			this.board.printBoard2(x-1,y,x,y);
            		}
            	}
        	}
        }else {
        	if(y-1>=0) {
        		if(x+2<this.board.getWidth()) {
            		if(y+2<this.board.getHeight()) {
            			this.board.printBoard2(x,y-1,x+2,y+2);
            		}else if(y+1<this.board.getHeight()){
            			this.board.printBoard2(x,y-1,x+2,y+1);
            		}else {
            			this.board.printBoard2(x,y-1,x+2,y);
            		}
            	}else if(x+1<this.board.getWidth()) {
            		if(y+2<this.board.getHeight()) {
            			this.board.printBoard2(x,y-1,x+1,y+2);
            		}else if(y+1<this.board.getHeight()){
            			this.board.printBoard2(x,y-1,x+1,y+1);
            		}else {
            			this.board.printBoard2(x,y-1,x+1,y);
            		}
            	}else {
            		if(y+2<this.board.getHeight()) {
            			this.board.printBoard2(x,y-1,x,y+2);
            		}else if(y+1<this.board.getHeight()){
            			this.board.printBoard2(x,y-1,x,y+1);
            		}else {
            			this.board.printBoard2(x,y-1,x,y);
            		}
            	}
        	}else {
        		if(x+2<this.board.getWidth()) {
            		if(y+2<this.board.getHeight()) {
            			this.board.printBoard2(x,y,x+2,y+2);
            		}else if(y+1<this.board.getHeight()){
            			this.board.printBoard2(x,y,x+2,y+1);
            		}else {
            			this.board.printBoard2(x,y,x+2,y);
            		}
            	}else if(x+1<this.board.getWidth()) {
            		if(y+2<this.board.getHeight()) {
            			this.board.printBoard2(x,y,x+1,y+2);
            		}else if(y+1<this.board.getHeight()){
            			this.board.printBoard2(x,y,x+1,y+1);
            		}else {
            			this.board.printBoard2(x,y,x+1,y);
            		}
            	}else {
            		if(y+2<this.board.getHeight()) {
            			this.board.printBoard2(x,y,x,y+2);
            		}else if(y+1<this.board.getHeight()){
            			this.board.printBoard2(x,y,x,y+1);
            		}else {
            			this.board.printBoard2(x,y,x,y);
            		}
            	}
        	}
        }
        
        this.use();
        return true;
        
        /*if (direction.equals("right")){
        	this.seeThrough(x+1, y);
        } else if (direction.equals("left")){ 
        	this.seeThrough(x-1, y);
        }else if ( direction.equals("up")){
            this.seeThrough(x, y+1);
        }else {
        	this.seeThrough(x, y-1);
        }*/
    }

    /**
     * Displays information from the neighboring cell at the specified coordinates
     * @param x the x-- coordinate of the neighboring cell
     * @param y the y-- coordinate of the neighboring cell
     */
    /*public void seeThrough(int x, int y) {
    	System.out.print("   ");
    	if(x>this.board.getWidth()) {
    		System.out.print("|");
    	}else if(y>this.board.getHeight()){
    		System.out.print("__");
    	}else {
    		if (this.board.getBoard()[x][y] instanceof Pharmacy) {
            	System.out.print("[P] ");
            } else if (this.board.getBoard()[x][y] instanceof Hotel) {
                System.out.print("[H] ");
            } else if (this.board.getBoard()[x][y] instanceof Street) {
                Street street = (Street) this.board.getBoard()[x][y];
                if (street.hasSewer()) {
                	System.out.print("[@] ");
                } else {
                	System.out.print("[ ] ");
                }
            } else if (this.board.getBoard()[x][y] instanceof Building) {
                    System.out.print("| | ");
            } else {
                    System.out.print("   ");
            }
            System.out.print("\n");
            }
    }*/
    
    public String symbole(){
        return "🕶️";
    }
    
    public String toString(){
        return "Glasses";
    }

}