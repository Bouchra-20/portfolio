package fil.l3.coo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import fil.l3.coo.DocaratorPattern.Electrique;
import fil.l3.coo.ExceptionsControlled.NotInService;

public class TestElectrique {
     @Test
    public void testLouerElectriqueOK(){
        Client c = new Client ( "Tifoun" ,  "Mohamed" , 1000 );
        Velo v = new Velo ( 5 , false  , 100 );
        Electrique el = new Electrique(v , 100  );
        try{
            int nb_location = el.get_NbLocation();
            c.Louer(el); 
            assertEquals(850,(short) c.getMoney() );    
            assertEquals(1 , el.get_NbLocation()- nb_location ); 
            assertEquals(c.getVehicule(), el);
        }catch( Exception e){
            e.printStackTrace();
        }
    }


     @Test 
    public void LouerAvecBatterieFaible()throws Exception{
        Client c = new Client ( "Tifoun" ,  "Mohamed" , 10000 );
        Velo l = new Velo(10 , false , 100);
        Electrique h = new Electrique(l, 0);
        assertThrows(NotInService.class, ()->{
            c.Louer(l);
        });
           
    }


    @Test
    public void CheckIfBatterieDecreasing() throws Exception{
        Client c = new Client ( "Tifoun" ,  "Mohamed" , 10000 );
        Velo l = new Velo(10 , false , 100);
        Electrique h = new Electrique(l, 10);
        c.Louer(h);
        Thread.sleep(1000);/* SIMULATION D'UNE PERIODE DE TEMPS */
        /* THE FOLLOWING METHODE IS CALLED INSIDE   deposerLocationdansStation in Client Object*/
        h.setDeposer(true);
        assertEquals(0, h.getBatterie());
    }
}
