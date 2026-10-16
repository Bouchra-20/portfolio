package com.flopbox;

import com.flopbox.AuthService.AuthService;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

/**
 * Classe principale responsable du démarrage et de la configuration du serveur FlopBox.
 * <p>
 * Cette classe initialise le serveur HTTP Grizzly, configure le framework Jersey (JAX-RS)
 * et enregistre les composants nécessaires comme le support pour le format Multipart.
 * </p>
 * @author Votre Nom
 * @version 1.0
 */
public class Main {
    /** * URL de base sur laquelle le serveur HTTP Grizzly écoute les requêtes.
     * Par défaut : {@value #BASE_URI}
     */
    public static final String BASE_URI = "http://localhost:8080/";

    /**
     * Démarre le serveur HTTP Grizzly en exposant les ressources JAX-RS définies dans le projet.
     * La configuration inclut :
     * <ul>
     * <li>Le scan automatique du package {@code com.flopbox} pour trouver les ressources ({@code @Path}).</li>
     * <li>L'enregistrement de {@code MultiPartFeature} pour permettre l'upload de fichiers via {@code multipart/form-data}.</li>
     * </ul>
     * @return Une instance de {@link HttpServer} en cours d'exécution.
     */
    public static HttpServer startServer() {
        // Crée une configuration de ressources qui scanne le package com.flopbox
        final ResourceConfig rc = new ResourceConfig().packages("com.flopbox");

        // Enregistre le support pour les requêtes Multipart (indispensable pour l'upload FTP)
        rc.register(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
        rc.register(new AbstractBinder() {
            @Override
            protected void configure() {
                // On dit à Jersey de créer une nouvelle instance quand il trouve @Inject AuthService
                bind(AuthService.class).to(AuthService.class);
            }
        });

        // Crée et démarre une nouvelle instance de l'interface Grizzly HTTP
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Méthode d'entrée principale (Main) pour lancer l'application FlopBox.
     * <p>
     * Elle démarre le serveur, affiche l'URL de connexion dans la console et
     * maintient le processus actif jusqu'à ce que l'utilisateur appuie sur la touche Entrée.
     * </p>
     * @param args Arguments de la ligne de commande (non utilisés ici).
     * @throws Exception Si une erreur survient lors de l'initialisation du serveur.
     */
    public static void main(String[] args) throws Exception {
        final HttpServer server = startServer();
        System.out.println(String.format("FlopBox démarré sur %s\nAppuyez sur Entrée pour l'arrêter...", BASE_URI));

        // Bloque l'exécution pour empêcher le programme de se terminer immédiatement
        System.in.read();

        // Arrête proprement le serveur
        server.shutdownNow();
    }
}