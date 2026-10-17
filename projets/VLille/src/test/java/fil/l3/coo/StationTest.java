package fil.l3.coo;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import fil.l3.coo.ExceptionsControlled.IdNotFound;
import fil.l3.coo.ExceptionsControlled.NotPlacesAvailable;
import fil.l3.coo.ExceptionsControlled.StationVide;
import fil.l3.coo.StateController.StationState.StationState;
import fil.l3.coo.StateController.StationState.Station_pleine;

public class StationTest {
    private static final int  Nb_place_notOk =0 ;
    private static final int  Nb_place_Ok =10 ;
    private MockVelo v; 

    @BeforeEach
    public void setUp() {
        v = new MockVelo(false, Nb_place_notOk, Nb_place_Ok) ; 
    }

    @Test
    void TestStationInitialization() {
        Station s = new Station(5);
        assertEquals(5, s.getNb_palces());
        assertEquals(5, s.getPlaces_restantes());
        assertTrue(s.StationVide());
    }

    @Test
    void TestIdStationOk(){
        Station s1 = new Station(Nb_place_Ok);
        Station s2 = new Station(Nb_place_Ok);
            assertFalse(s1.getId_station() == s2.getId_station());
    }

    @Test 
    void testAvailablePlaces(){
        Station sOk=  new Station(Nb_place_Ok);
        assertEquals(Nb_place_Ok , sOk.getPlaces().size());
    }

    @Test  
    public void TestAjoutOK() throws Exception{
        Station s = new Station(Nb_place_Ok);
        System.out.println(s.getPlaces());
        s.Deposer(v);
        assertEquals(s.getPlaces_restantes(), Nb_place_Ok-1);
        assertTrue(s.ContainsLocations(v));
    }


    @Test
    public void TestAJOUTNotOK(){
        Station s = new Station(0);
        assertThrows(NotPlacesAvailable.class,()->{
            s.Deposer(v); 
        });
        assertEquals(s.getPlaces_restantes(),Nb_place_notOk);
        assertFalse(s.ContainsLocations(v));
    }


    @Test 
    void RetiterOk()throws Exception{
        Station s = new Station(Nb_place_Ok);
        int nb_place_before = s.getPlaces_restantes();
        s.Deposer(v);
        Locations l  = s.Retirer(v.getId_prod());
        assertEquals(nb_place_before, s.getPlaces_restantes());
        assertFalse(s.ContainsLocations(v));    
    }

    @Test
    void RetirerNotOK(){
        Station s = new Station(Nb_place_Ok);
        assertThrows(StationVide.class, ()->{
            s.Retirer(145);
        });
        assertThrows(IdNotFound.class, ()->{
            s.Deposer(v);
            Locations m = s.Retirer(v.getId_prod()+1);
        });
    }

    @Test
    public void TestLouerOK() throws Exception{
        Client c = new MockClient(4000);
        Station s = new Station(Nb_place_Ok);
        s.Deposer(v);
        s.Louer(c);
        assertEquals(s.getPlaces_restantes(),Nb_place_Ok);
    }    

    @Test
    public void ProbablyLocationsVoleTest_WITH_STATIONOK()throws Exception{
        Station s  = new Station(2);
        s.Deposer(v);
        assertEquals(v, s.ProbablyLocationsVole());
    }

    @Test
    public void ProbablyLocationsVoleTest_WITH_STATION_NOTOK()throws Exception{
        Station s  = new Station(2);
        /* s.Deposer(v); */ /* La station reste vide */
        assertEquals(null, s.ProbablyLocationsVole());
    }
    @Test
    void TestSetState() {
        Station s = new Station(5);
        StationState newState = new Station_pleine(s.getPlaces());
        s.SetState(newState);
        assertEquals(newState, s.getState());
    }

    @Test
    void TestContainsLocationsWhenEmpty() {
        Station s = new Station(5);
        assertFalse(s.ContainsLocations(v));
    }

    @Test
    void TestLouerFromEmptyStation() {
        Client c = new MockClient(4000);
        Station s = new Station(Nb_place_notOk);
        assertThrows(StationVide.class, () -> {
            s.Louer(c);
        });
    }

    @Test
    void TestGetPlacesAvailable() throws Exception {
        Station s = new Station(3);
        s.Deposer(v);
        assertEquals(2, s.placesAvailbale().size());
    }
}
    