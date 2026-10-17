import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Players.Player;
import Cells.Cell;
import Cells.Room;
import Cells.Street;
import Cells.Door;
import Board.Board;
import Actions.MoveAction;

public class MoveActionTest {
    private Player player;
    private Cell targetCell;
    private Board board;
    private MoveAction moveAction;

    @BeforeEach
    public void setUp() {
        player = new Player("Test Player", "Lucky");
        targetCell = new Cell(1, 1);
        board = new Board(5, 5);
        moveAction = new MoveAction(player, targetCell, board);
    }

    @Test
    public void testExecuteSuccessfulMoveToStreet() {
        Cell initialCell = new Cell(0, 0);
        player.setPlayerCell(initialCell);

        assertTrue(moveAction.execute());
        assertEquals(targetCell, player.getPlayerCell());
    }

    @Test
    public void testExecuteSuccessfulMoveToRoomWithOpenDoor() {
        Room room = new Room(1, 0);
        Door door = new Door(true);
        room.addDoor(door);
        board.setCell(room, 1, 0);

        Cell initialCell = new Cell(0, 0);
        player.setPlayerCell(initialCell);

        assertTrue(moveAction.execute());
        assertEquals(targetCell, player.getPlayerCell());
    }

    @Test
    public void testExecuteFailedMoveToRoomWithClosedDoor() {
        Room room = new Room(1, 0);
        Door door = new Door(false);
        room.addDoor(door);
        board.setCell(room, 1, 0);

        Cell initialCell = new Cell(0, 0);
        player.setPlayerCell(initialCell);

        assertFalse(moveAction.execute());
        assertNotEquals(targetCell, player.getPlayerCell());
    }

    @Test
    public void testExecuteFailedMoveToNonAdjacentCell() {
        Cell initialCell = new Cell(0, 0);
        player.setPlayerCell(initialCell);
        targetCell = new Cell(3, 3);

        assertFalse(moveAction.execute());
        assertNotEquals(targetCell, player.getPlayerCell());
    }

    @Test
    public void testExecuteFailedMoveToCellWithZombie() {
        targetCell.setZombie(new Zombie(ZombieType.WALKER));

        Cell initialCell = new Cell(0, 0);
        player.setPlayerCell(initialCell);

        assertFalse(moveAction.execute());
        assertNotEquals(targetCell, player.getPlayerCell());
    }
}

