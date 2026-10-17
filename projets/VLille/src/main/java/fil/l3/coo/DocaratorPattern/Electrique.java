package fil.l3.coo.DocaratorPattern;

import java.time.Duration;
import java.time.LocalDateTime;
import fil.l3.coo.DeuxRoues;

/**
 * The `Electrique` class represents an electric vehicle that implements the `DeuxRoues` interface.
 * It has a private `DeuxRoues` object that it uses to manage the vehicle's properties and behavior.
 */
public class Electrique extends Accessoire {
    // A private field that holds a reference to a `DeuxRoues` object, which represents the electric vehicle.
    private LocalDateTime retrait ; 
    private int Batterie ;
    private LocalDateTime depot ;


    public Electrique( DeuxRoues v , int batterie){
        super(v);
        this.Batterie = batterie ;
        if( this.Batterie == 0){
            this.v.setEtatSerice();
        } 
    }


    @Override
    public void Louer() throws Exception{
        this.v.Louer();
        this.retrait =LocalDateTime.now(); 
    }


    @Override
    public void setDeposer(boolean f){
        super.setDeposer(f);
        this.depot = LocalDateTime.now();
        if(this.retrait == null){
            this.retrait = this.depot;
        }
        this.EstimationNiveauBATTERIE();
    }

    public void EstimationNiveauBATTERIE(){
        int conso = 10 ; /* 10% chaque 1 periode de temps  */
        int duration = (int) Duration.between(this.retrait , this.depot).toSeconds() / 1; /* DU MOMENT DE QUE LA PERIODE EST FIXE A 1 SECONDE DEVISE PAR 1 */
        this.Batterie = this.Batterie - conso * duration ;
    }

    @Override
    public double getPrixLocation(){
        return this.v.getPrixLocation() * 2 ;
    }

    @Override
    public double getCaution(){
        return super.getCaution()*1.5;
    }


    public int getBatterie() {
        return Batterie;
    }

    public String toString(){
        return "🛵";
    }

}
