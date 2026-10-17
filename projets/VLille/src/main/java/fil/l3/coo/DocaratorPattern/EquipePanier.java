package fil.l3.coo.DocaratorPattern;

import java.util.*;

import fil.l3.coo.DeuxRoues;
import fil.l3.coo.Locations;
/**
 * Represents a decorator for a two-wheeled vehicle equipped with a basket.
 * This class is part of the Decorator pattern implementation, allowing
 * additional features to be added to a DeuxRoues object dynamically.
 */
public class EquipePanier extends Accessoire /*implements Locations*/ {
    // The base DeuxRoues object being decorated
    private DeuxRoues v ; 
    public List<Sac> panier ;
    /**
     * Constructs an EquipePanier object, decorating the given DeuxRoues with a basket.
     * @param v The DeuxRoues object to be equipped with a basket
     */
    public EquipePanier ( Locations v){
        super(v);
        this.panier = new ArrayList<Sac>();
    }

    public void addPanier(Sac o){
        if(o.getPoids() < 5){
            this.panier.add(o);
        }else{
            throw new IllegalArgumentException("Le sac est trop lourd");
        }
    }
    public Sac removePanier(){
        return this.panier.remove(0);
    }
    public List<Sac> getPanier(){
        return this.panier;
    }


   /*  @Override
    public void Louer(){
        /* The body must be filled 
    }


    @Override
    public Boolean estVole(){
        if(1==1){
            return true ;
        }
        return false ;
    }

    @Override
    public float getPrixLocation(){
        return this.prix_location;
    }

    @Override
    public Boolean getEtatService(){
        return this.hors_service;
    }

    @Override
    public int get_NbLocation(){
        return nb_location;
    }

    @Override
    public void set_NbLocation( int nb ){
        this.nb_location = nb ;
    }


    @Override
    public void ChangeStateService(){
        this.hors_service = !this.hors_service ;
    }*/
}
