package fil.l3.coo;
import java.util.* ;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class V_lilleTest {
    private V_lille v = V_lille.getInstance();
    @Test
    void testOfUnicity(){
        V_lille v1 = V_lille.getInstance();
        V_lille v2 = V_lille.getInstance();
        assertEquals(v1, v2);
    }

    @Test 
     void TestMethodeDistribution()throws Exception{
        DistributeVeloInStation(10);
        assertTrue(isLoaded());
     }

     /* Methode à utiliser dans le test */
     public boolean isLoaded(){
        List<Station> s = v.getStations();
        boolean loaded = false ;
        for( Station e : s){
            loaded = loaded ||(!e.StationVide()); 
        }
        return loaded ;
     }
     /************************************** */


     public void DistributeVeloInStation(int max )throws Exception{
        if( ! v.isInitialized()){
            v.Distribution(max);
        }
     }

    /*Rajouter test NonOK de Redestribution */
    @Test 
    void testNeedToRestribute()throws Exception {
        DistributeVeloInStation(10);
        List<List<Station>> s = v.NeedToDistribute(2);
        assertTrue(s.size()==2);
        for( int i = 0 ; i< s.size() ; i++ ){
            Iterator<Station> s1 = s.get(i).iterator() ;
            while(s1.hasNext()){
                Station e = s1.next();
                if( i == 0){
                    assertTrue(e.isStationPleine());
                }
                if( i == 1 ){
                    assertTrue(e.StationVide());
                }
            }
        }
    }

    @Test
    void TestREDISTRIBUTION_METHODE_CLASSIQUE()throws Exception{
        DistributeVeloInStation(10);
        Station pleine = new Station(10);
        for( int i = 0 ; i <10 ; i++ ){
            pleine.Deposer(new Velo(10,false,100));
        }
        Station vide = new Station(10);
        List<Station> s = new ArrayList<Station>();
        s.add(pleine);
        List<Station> s1 = new ArrayList<Station>();
        s1.add(vide);
        v.REDISTRIBUTION(s , s1);  
        assertTrue(pleine.getPlaces_restantes() == vide.getPlaces_restantes());
    }
 
    @Test
    void TestGetDeuxRouesVole() throws Exception{
       DistributeVeloInStation(1);
       List<Locations> d = this.v.getDeuxRouesVole();/* Return a list of Stolen DeuxRoues thank to Seul2times strategy */
       for( Locations loc : d){
        assertEquals(true, loc.estVole());
       }

    }
}
