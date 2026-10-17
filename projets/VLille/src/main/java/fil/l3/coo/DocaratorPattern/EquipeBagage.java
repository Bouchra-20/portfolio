package fil.l3.coo.DocaratorPattern;

import java.util.*;

import fil.l3.coo.DeuxRoues;
import fil.l3.coo.Locations;
/**
 * Represents a decorator for a two-wheeled vehicle equipped with luggage storage.
 * This class is part of the Decorator pattern implementation, allowing
 * additional features to be added to a DeuxRoues object dynamically.
 */
public class EquipeBagage extends Accessoire /*implements Locations */{
    // The base DeuxRoues object being decorated
    private DeuxRoues v ; 
    public List<Sac> baggage ;
    /**
     * Constructs an EquipeBagage object, decorating the given DeuxRoues with luggage storage.
     * @param v The DeuxRoues object to be equipped with luggage storage
     */
    public EquipeBagage ( Locations v){
        super(v);
        this.baggage = new ArrayList<Sac>();
    }

    public void addBaggage(Sac o){
        if(o.getPoids() < 10){
            this.baggage.add(o);
        }else{
            throw new IllegalArgumentException("Le sac est trop lourd");
        }
    }
    public Sac removeBaggage(){
        return this.baggage.remove(0);
    }

    public List<Sac> getBaggage(){
        return this.baggage;
    }

/*
    @Override
    public void Louer( ){
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
    }

    */
}
