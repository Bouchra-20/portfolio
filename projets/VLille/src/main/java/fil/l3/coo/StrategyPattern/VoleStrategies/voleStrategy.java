package fil.l3.coo.StrategyPattern.VoleStrategies;

import java.util.List;

import fil.l3.coo.Locations;
import fil.l3.coo.Station;

/**
 * Defines a strategy interface for retrieving a list of locations for stolen vehicles based on a given time period and a list of stations.
 */
public interface voleStrategy {
    /**
     * Retrieves a list of locations for stolen vehicles based on the given time period and list of stations.
     *
     * @param periode The time period to retrieve stolen vehicle locations for.
     * @param Stations The list of stations to retrieve stolen vehicle locations from.
     * @return A list of locations where stolen vehicles were reported.
     * @throws Exception If there is an error retrieving the stolen vehicle locations.
     */
    public List<Locations> getDeuxRouesVole(int periode , List<Station> Stations) throws Exception ;
}
