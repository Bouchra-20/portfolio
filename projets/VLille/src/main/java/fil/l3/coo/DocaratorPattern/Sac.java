package fil.l3.coo.DocaratorPattern;

/**
 * Represents a bag or sack with a specified weight.
 */
public class Sac {
    /**
     * The weight of the bag or sack.
     */
    private int poids;
    
    /**
     * Constructs a new Sac instance with the specified weight.
     * @param poids the weight of the bag or sack
     */
    public Sac(int poids) {
        this.poids = poids;
    }

    /**
     * Gets the weight of the bag or sack.
     * @return the weight of the bag or sack
     */
    public int getPoids() {
        return poids;
    }
}
