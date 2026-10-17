package fil.l3.coo.StrategyPattern.RedistributionStargies;
import fil.l3.coo.* ;
import java.util.*;

/**
 * Defines a strategy interface for redistributing stations between two lists.
 * Implementations of this interface should provide a specific algorithm for redistributing the stations.
 */
public interface REDISTRIBUTION_STRATEGY {
    /**
     * Redistributes the stations from the source list to the destination list.
     * 
     * @param source The list of stations to redistribute from.
     * @param dest The list of stations to redistribute to.
     * @throws Exception If an error occurs during the redistribution process.
     */
    void REDISTRIBUTION(List<Station> source , List<Station> dest) throws Exception ;
}
