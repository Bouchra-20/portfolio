package Equipments;

public class MaxLifeLevelException extends Exception{

    public MaxLifeLevelException(){
        super("The player is already at his full lifelevel.");

    }

}