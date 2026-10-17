import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Players.Player;
import Players.FighterRole;
import Players.Zombie;
import Players.ZombieType;
import Cells.Cell;
import Board.Board;
import Equipments.Weapon;
import Equipments.WeaponType;

public class AttackActionTest {
    private Player player;
    private Zombie zombie;
    private Weapon weapon;
    private AttackAction attackAction;

    @BeforeEach
    public void setUp() {
        player = new Player("Test Player", "Lucky");
        zombie = new Zombie(ZombieType.WALKER);
        weapon = new Weapon(WeaponType.GUN);
        attackAction = new AttackAction(player, zombie, weapon);
    }

    @Test
    public void testExecuteSuccessfulAttack() {
        assertTrue(attackAction.execute());
    }

    @Test
    public void testExecuteFailedAttackOutOfRange() {
        // Setting up the player and zombie positions to be out of weapon range
        Cell playerCell = new Cell(0, 0);
        Cell zombieCell = new Cell(3, 3); // Assuming weapon range is 1
        player.setPlayerCell(playerCell);
        zombie.setCellPosition(zombieCell);

        assertFalse(attackAction.execute());
    }

    @Test
    public void testExecuteFailedAttackInHotel() {
        // Setting player's position to be in a hotel
        player.setPlayerCell(new Hotel(0, 0));

        assertFalse(attackAction.execute());
    }

    @Test
    public void testIsTargetInRangeTrue() {
        // Assuming the weapon range is 1 and the player and zombie are within range
        player.setPlayerCell(new Cell(0, 0));
        zombie.setCellPosition(new Cell(0, 1));

        assertTrue(attackAction.isTargetInRange());
    }

    @Test
    public void testIsTargetInRangeFalse() {
        // Assuming the weapon range is 1 and the player and zombie are out of range
        player.setPlayerCell(new Cell(0, 0));
        zombie.setCellPosition(new Cell(3, 3));

        assertFalse(attackAction.isTargetInRange());
    }
}

