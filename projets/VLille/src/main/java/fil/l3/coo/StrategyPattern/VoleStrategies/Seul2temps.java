package fil.l3.coo.StrategyPattern.VoleStrategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fil.l3.coo.Emplacement;
import fil.l3.coo.Locations;
import fil.l3.coo.Station;
/**
 * Implements the voleStrategy interface to detect potentially stolen vehicles
 * based on the "alone for 2 time periods" criterion.
 */
public class Seul2temps implements voleStrategy {
    /**
     * Retrieves a list of vehicles that are potentially stolen from all stations.
     * @param Stations List of all stations to check
     * @return List of potentially stolen vehicles
     * @throws Exception if there's an error accessing station data
     */
    public HashMap<Locations, Emplacement> getProbablyVole(List<Station> Stations)throws Exception{
        HashMap<Locations, Emplacement> s = new HashMap<>();
        for( Station station : Stations){
            Locations probably_vole = station.ProbablyLocationsVole() ;
            if( probably_vole != null){
                Emplacement emplacement = null;
                for( Emplacement e : station.getPlaces()){
                    if( e.getLocations() != null && e.getLocations().equals(probably_vole)){
                        emplacement = e ;
                    }
                }
                s.put(probably_vole, emplacement);
            }
        }
        return s ;
    }
    /**
     * Implements the strategy to detect stolen vehicles.
     * It checks for vehicles that are alone in a station for two consecutive time periods.
     * @param periode The duration of each time period in seconds
     * @param Stations List of all stations to check
     * @return List of vehicles determined to be stolen
     * @throws Exception if there's an error during the detection process
     */
    public List<Locations> getDeuxRouesVole(int periode , List<Station> Stations ) throws Exception{
        HashMap<Locations,Emplacement> s1 = getProbablyVole(Stations) ;
        Thread.sleep(1000*periode);/* Probalement vole apres le 1er temps  */
        HashMap<Locations, Emplacement> s2 = getProbablyVole(Stations) ;
        //s1.contains(s2);/* VELOs OFFICIELLEMENT VOLES */
        s1.keySet().contains(s2.keySet());
        for( Locations loc : s1.keySet()){
            Emplacement empl = s1.get(loc);
            loc.setToVole();
            empl.Retirer();
        }
        return new ArrayList<Locations>(s1.keySet());
    }
}
