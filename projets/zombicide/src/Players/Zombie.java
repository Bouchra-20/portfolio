package Players;
import Cells.*;
public class Zombie {
    
    private ZombieType type;
    private int lifePts;
    private int damagePts;
    private Cell cellposition;
    /**
    *Constructor of Zombie class
    @param type the type of zombie to be created
    */
    public Zombie(ZombieType type){
    	this.type=type;
    	setAttributes(type);
    }
    /**
     * method that gives the zombie life points and damage points according to his type
     * @param type the type of Zombie to be created
     */
    public void setAttributes(ZombieType type){
        if(type==ZombieType.WALKER){
            this.lifePts=1;
            this.damagePts=1;
        }
        if(type==ZombieType.RUNNER){
            this.lifePts=2;
            this.damagePts=1;
        }
        if(type==ZombieType.ABOMINATION){
            this.lifePts=6;
            this.damagePts=3;
        }
        if(type==ZombieType.STRONG){
            this.lifePts=4;
            this.damagePts=2;
        }
    }
    /**
    *getter method for attribute lifpts
    *@return int
    */
    public int getLifePts(){
    	return this.lifePts;
    }
    /**
     *getter method for zombie's type
     *@return ZombieType
     */
     public ZombieType getType(){
     	return this.type;
     }
    /**
     *setter method for attribute lifpts
     *@return int
     */
     public void setLifePts(int LifePts){
     	this.lifePts=LifePts;
     }
     /**
    *getter method for attribute damagepts
    *@return int
    */
    public int getDamagePts(){
    	return this.damagePts;
    }
        
    /**
     *setter method for attribute damagepts
     *@param points the number of damage points the weapon can afflict
     *@return int
     */
     public int setDamagePts(int points){
     	return this.damagePts = points;
     }
     /**
      * Method that returns true when a zombie attacks the player
      reducing player'life points
      * @param P player to be attacked by zombie
      @return boolean
      */
     public boolean attack(Player P){
        P.setLifeLevel(P.getLifeLevel()-damagePts);
        return true;
     }
    
    public void move(Zombie zombie ){
        

    }
    /**
     * a method that returns the position of a zombie 
     * @return 
     */
    public Cell getCellPosition(){
         return this.cellposition;
    }

    /**
     * a method that sets a zombie on a certain cell 
     * @param 
     * @param
     */
    public void setCellPosition(Cell cell){
        this.cellposition = cell;


    }
    
    public String symbole(){
        if(this.lifePts == 1){
            return "🧟¹";
        }else if(this.lifePts == 2){
            return "🧟²";
        }else if(this.lifePts == 3){
            return "🧟³";
        }else if(this.lifePts == 4){
            return "🧟⁴";
        }else if(this.lifePts == 5){
            return "🧟⁵";
        }else if(this.lifePts == 6){
            return "🧟⁶";
        }
        return "🧟";
    }
}
