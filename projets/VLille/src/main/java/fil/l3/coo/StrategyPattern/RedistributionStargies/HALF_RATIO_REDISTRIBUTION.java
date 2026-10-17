package fil.l3.coo.StrategyPattern.RedistributionStargies;

import java.util.*;

import fil.l3.coo.Emplacement;
import fil.l3.coo.Locations;
import fil.l3.coo.Station;
import fil.l3.coo.V_lille;
import fil.l3.coo.ExceptionsControlled.StationVide;
/**
 * Implements a redistribution strategy based on a half-ratio principle.
 * This strategy aims to redistribute vehicles between stations to maintain a certain balance.
 */
public class HALF_RATIO_REDISTRIBUTION implements REDISTRIBUTION_STRATEGY {
    // The ratio used for the half-ratio redistribution strategy.
    private double ratio ;/* GENERALEMENT MIS A 50% */

    /**
     * Constructs a new HALF_RATIO_REDISTRIBUTION with a specified ratio.
     * @param Ratio The target ratio for redistribution (typically 0.5 for 50%)
     */
    public HALF_RATIO_REDISTRIBUTION(double Ratio){
        this.ratio = Ratio ;
    }

    /**
     * Implements the redistribution strategy.
     * @param source List of source stations (typically full or near-full)
     * @param dest List of destination stations (typically empty or near-empty)
     * @throws Exception if there's an error during redistribution
     */
    @Override
    public void REDISTRIBUTION(List<Station> source , List<Station> dest) throws Exception {
        this.REDISTRIBUTION_METHODE_CLASSIQUE(source, dest);
    }

    /**
     * Redistributes vehicles from a single source station to multiple destination stations.
     * @param source The source station
     * @param dest List of potential destination stations
     * @return Updated list of destination stations that still need vehicles
     * @throws Exception if there's an error during redistribution
     */
    public List<Station> redistribution_ratio(Station source, List<Station> dest) throws Exception {
        Iterator<Station> vide = dest.iterator();
        double rapport = (double) source.getPlaces_restantes() / source.getNb_palces(); 
        if (!vide.hasNext()) {
            throw new StationVide();
        }
        Station e = vide.next();
        double taux_vide = (double)(e.getPlaces_restantes()/e.getNb_palces());
        while (!source.StationVide() && (rapport < this.ratio)) {
            Locations m = source.Retirer();
            if (((taux_vide)<= this.ratio)) {/* On rempli jusqu'a 50% */
                if (vide.hasNext()) {
                    e = vide.next();
                }else {
                    source.Deposer(m);
                    break;
                }
            }
            // Déposer le vélo dans la station actuelle
            e.Deposer(m);
    
            // Recalculer le rapport pour voir si la condition est toujours satisfaite
            rapport = (double) source.getPlaces_restantes() / source.getNb_palces();
            taux_vide = (double) e.getPlaces_restantes() / e.getNb_palces();
        }
        return dest.subList(dest.indexOf(e), dest.size());            
    }
    
    /**
     * Implements the classic redistribution method.
     * Redistributes vehicles from full stations to empty stations.
     * @param remplies List of full or near-full stations
     * @param vide List of empty or near-empty stations
     * @throws Exception if there's an error during redistribution
     */
    public void REDISTRIBUTION_METHODE_CLASSIQUE(List<Station> remplies , List<Station> vide ) throws Exception{
        
        if( remplies != null && vide != null){
            List<Station> Pleines = remplies;
            List<Station> Vides = vide;
            for ( Station v : Pleines){
                Vides = redistribution_ratio(v, Vides); 
                if( Vides == null){
                    break ;
                }
            }
        }
    }   
    
}
