package fil.l3.coo ;




public class MockVelo extends Velo{
    private boolean etat ;

    public MockVelo(boolean etat , float prix_location , float caution){
        super(prix_location , false , caution);
        this.etat = etat ;

    }

    @Override
    public boolean getEtatService(){
        return etat ; 
    } 




    void setEtat(boolean bool){
        this.etat = bool ;  
    }

    @Override
    public void askForIntervention()throws Exception{
        super.askForIntervention();
    }
    
    
    public void Deposer(){
        this.louee = false;
    }




}
