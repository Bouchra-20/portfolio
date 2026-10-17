package fil.l3.coo.AccesController;

import fil.l3.coo.Locations;

/**
 * Provides an interface for depositing a location with an access provider.
 */
public interface AccesProvidertoStation {
    /**
     * Deposits in the given location with the access provider.
     * @param l The location to deposit in with the access provider.
     * @throws Exception If there is an error depositing in the location.
     */
    void Deposer(Locations l) throws Exception;
}
