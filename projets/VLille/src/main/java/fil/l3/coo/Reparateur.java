package fil.l3.coo;
/**
 * The `Reparateur` class represents a repair technician in charge of maintaining equipment.
 * 
 * This class extends the `Personnel` class, which means that it inherits basic attributes 
 * and behavior from the personnel class, such as the name and surname of the technician.
 * The `Reparateur` class provides methods for repairing equipment, simulating the 
 * repair process with a delay.
 */
public class Reparateur extends Personnel{
    /**
     * Constructs a new `Reparateur` (repair technician) with a given first and last name.
     * @param Nom The last name of the repair technician.
     * @param Prenom The first name of the repair technician.
     */
    public Reparateur(String Nom , String Prenom){
        super(Nom , Prenom);
    }
    /**
     * Interacts with the equipment by performing a repair operation.
     * This method calls the `Reparer` method to simulate the repair process.
     * @param periode_of_intervention The duration of the repair operation in arbitrary time units.
     * @throws Exception If an error occurs during the repair process.
     */
    public void Interagir(int periode_of_intervention) throws Exception{
        this.Reparer(periode_of_intervention);
    }
    /**
     * Repairs the equipment, simulating the repair process with a delay.
     * This method simulates a repair process by printing a message indicating that the 
     * repair is in progress, and then pausing the execution for a specified period.
     * @param periode The duration of the repair in arbitrary time units. 
     *                Each unit corresponds to a 20-second pause in real time.
     * @throws Exception If an error occurs during the delay simulation.
     */
    public void Reparer(int periode)throws Exception{
        Thread.sleep(periode*1000);/*  simulation */
    }
    
}
