package fil.l3.coo;
/**
 * The `Intervention` interface defines the contract for personnel involved in handling interventions
 * within the system, such as maintenance or repair tasks. 
 */
public interface Intervention {
    /**
     * Performs an intervention action during a specified period.
     * The implementing class is responsible for defining the exact nature of the intervention.
     * @param periode_of_intervention the duration of the intervention, measured in units.
     * @throws Exception if the intervention process encounters an error.
     */
    public void Interagir( int periode_of_intervention) throws Exception;/* Doing the intervention Stuff */
}
