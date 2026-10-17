package Players;
import Equipments.Weapon;

public interface FighterRole {

    // This default method adds 1 to the attack roll whenever it's called.
    default int getAttackRollBonus() {
        return 1; // Returns the bonus added to the attack roll.
    }

    // Method that defines the attack action
    void attack();

    // Existing methods
    // ...
}
