package fil.l3.coo.NotificationController;

import fil.l3.coo.Client;

/**
 * Manages notifications for a client.
 * Provides methods to set the client and notify the client of a message.
 */
public class ClientNotifications {
    
    /**
     * The client associated with this notification manager.
     */
    private Client client;

    /**
     * Constructs a new instance of the ClientNotifications class.
     */
    public ClientNotifications(Client client) {
        this.client = client;
    }
    /**
     * Notifies the client associated with this notification manager of the given notification.
     * If the client is not null, the notification is added to the client's notification list.
     * @param notification The notification message to be sent to the client.
     */
    public void notifyClient(String notification) {
        if(this.client != null){
            this.client.addNotif(notification);

        }
    }
    
}
