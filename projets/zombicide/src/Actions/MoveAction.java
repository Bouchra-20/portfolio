package Actions;

import Players.*;
import Cells.*;
import Board.*;



public class MoveAction {

    private Player player;
    private Cell targetCell;

    private Board board;
    
    /**
     * Constructs a MoveAction object .
     * @param player  the moving player.
     * @param targetCell the destination cell.
     * @param board  The game board .
     */
    public MoveAction(Player player, Cell targetCell, Board board) {
        this.player = player;
        this.targetCell = targetCell;
        this.board = board;
    }
    
    /**
     * Executes the move action .
     * @return true if move is successful, false otherwise.
     */
    public boolean execute() {
        // Check if the target cell is adjacent to the player's current cell.
        if (isAdjacent(player.getPlayerCell(), targetCell)) {
            // Check if the target cell is a street or a room with an open door
            if (targetCell instanceof Street || (targetCell instanceof Room && hasOpenDoor((Room) targetCell))) {
                // Move the player to the target cell if there is no zombie
                if (!targetCell.hasZombie()) {
                    player.getPlayerCell().setPlayer(null);  // Remove player from current cell
                    targetCell.setPlayer(player);            // Set player to target cell
                    player.setCell(targetCell);              // Update player's current cell
                    System.out.println("Player moved to cell (" + targetCell.getCoordinateX() + ", " + targetCell.getCoordinateY() + ")");
                    return true;
                } else {
                    System.out.println("Cannot move: zombie present in target cell.");
                }
            } else {
                System.out.println("Cannot move: no open door or the cell is not a street.");
            }
        } else {
            System.out.println("Cannot move: target cell is not adjacent.");
        }
        return false;
    }
    
    /**
     * Checks if two cells are adjacent.
     * @param currentCell the current cell of the player.
     * @param targetCell  the target cell where the player intends to move.
     * @return true if the target cell is adjacent to the current cell, false otherwise.
   */
    private boolean isAdjacent(Cell currentCell, Cell targetCell) {
        //to check if cells are adjacent
        int currentX = currentCell.getCoordinateX();
        int currentY = currentCell.getCoordinateY();
        int targetX = targetCell.getCoordinateX();
        int targetY = targetCell.getCoordinateY();

        return (currentX == targetX && Math.abs(currentY - targetY) == 1) ||
                (currentY == targetY && Math.abs(currentX - targetX) == 1);
    }
    
    /**
     * Checks if a room has an open door.
     * @param room the room to check for open doors.
     * @return true if the room has an open door, false otherwise.
     */
    public boolean hasOpenDoor(Room room) {
        for (Door door : room.doors()) {
            if (door.isOpen()) {
                return true;
            }
        }
        return false;
    }
}


