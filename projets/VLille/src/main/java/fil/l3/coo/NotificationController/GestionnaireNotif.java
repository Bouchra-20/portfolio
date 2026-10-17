package fil.l3.coo.NotificationController;

import fil.l3.coo.V_lille;
import fil.l3.coo.AccesController.AccesForReparation;
/**
 * Manages notifications and intervention requests in the V_lille rental system.
 * This class acts as a central point for sending notifications and requesting interventions,
 * interfacing with the V_lille singleton instance.
 */
public class GestionnaireNotif {
    /**
     * Notifies the V_lille system of an event.
     * @param event A string describing the type of event
     * @param content Detailed content or description of the event
     */
    public void notify(String content){
    /* L'abonnée a cette notification est V Lille mais suite a un probleme de recusrion interminable j'ai du appelé getInstance */
        V_lille.getInstance().update(content);
    }
    /**
     * Requests an intervention for a vehicle that needs repair or maintenance.
     * @param i An object implementing AccesForReparation, representing the vehicle needing intervention
     * @throws Exception if the intervention request cannot be processed
     */
    public void call_for_intervention(AccesForReparation i) throws Exception{
        V_lille.getInstance().askForIntervention(i);
    }
    
}
