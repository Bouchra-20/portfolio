package Equipments;
import Players.*;
import java.util.Random;
import Actions.*;
import Cells.*;

public class Weapon extends Equipment<Zombie> implements INoiseProducer{
	
    private WeaponType type;
    private int damagePts;
    private int minRange;
    private int maxRange;
    private int threshold;
    private int dieThrow;
    protected boolean isNoisy;
    protected String symbole;
    
    private static Random random = new Random();
    
    /**
     * Class Constructor
     * @param type the type of the weapon to be created
     */
    public Weapon(WeaponType type){
        this.type=type;
        setAttributes(type);
    }
    /**
     * method that gives the weapon damage points according to his type
     * @param type the type of the weapon
     */
    public void setAttributes(WeaponType type){
        if(type==WeaponType.AXE){
            this.minRange=0;
            this.maxRange=0;
            this.damagePts=1;
            this.threshold=4;
            this.dieThrow=1;
            this.isNoisy = false;
            this.symbole = "🪓";
        }
        if(type==WeaponType.GUN){
            this.maxRange=1;
            this.minRange=0;
            this.damagePts=1;
            this.threshold=4;
            this.dieThrow=1;
            this.isNoisy = true;
            this.symbole = "🔫";
        }
        if(type==WeaponType.SHOTGUN){
            this.maxRange=3;
            this.minRange=1;
            this.damagePts=1;
            this.threshold=4;
            this.dieThrow=2;
            this.isNoisy = true;
            this.symbole = "╾━╤デ╦︻";
        }
        if(type==WeaponType.CHAINSAW){
            this.maxRange=0;
            this.minRange=0;
            this.damagePts=3;
            this.threshold=5;
            this.dieThrow=2;
            this.isNoisy = true;
            this.symbole = "🪚";
        }
        if(type==WeaponType.CROWBAR){
            this.maxRange=0;
            this.minRange=0;
            this.damagePts=1;
            this.threshold=4;
            this.dieThrow=1;
            this.isNoisy = false;
            this.symbole ="⛏️";
        }
    }
    /**
     * @param zombie Zombie
     */
    public boolean Effect(Zombie zombie){
    	//Throw a die
    	int dieResult = 0;
    	for(int i = 0; i<dieThrow ; i++) {
    		dieResult = dieResult + oneDieThrow();
    		//zombie damaged
    		if(dieResult >= threshold) {
    			int d = zombie.getDamagePts() - damagePts;
    			zombie.setDamagePts(d);
    		}
    	}
    	return true;
    }
    /**
     * getter method for attribute Maxrange
     * @return the Maxrange for which the weapon is effective
     */
    public int getMaxRange(){
    	return this.maxRange;
    }
    /**
     * getter method for attribute Minrange
     * @return int  minimum range that weapon is effective
     */
    public int getMinRange(){
    	return this.minRange;
    }
    /**
     * getter method for attribute damagepts
     * @return the number of damage points
     */
    public int getDamagePts(){
    	return this.damagePts;
    }
    /**
     * 
     * @return getter method for attribute threshold
     */
    public int getThreshold(){
    	return this.threshold;
    }
    /**
     * getter method for attribute Diethrow
     * @return die throw numbers 
     */
    public int getDieThrow(){
    	return this.dieThrow;
    }
    
    /** @return random result of a 1d6 throw*/
    private int oneDieThrow() {
        return Weapon.random.nextInt(6)+ 1;
    }
    @Override
    public String toString() {
        return "" + type;
    }
    
    @Override
    public void produceNoise(Cell cell) {
        if (isNoisy) {
            NoiseGenerator.addNoise(cell);
        }
    }

    public boolean isNoisy() {
        return this.isNoisy;
    }
    
    public String symbole(){
        return this.symbole;
    }

    public WeaponType type(){
        return this.type;
    }
}
