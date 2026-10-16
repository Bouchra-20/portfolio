package org.example.FTPCode;

/**
 * Classe utilitaire centralisant les codes de réponse du protocole FTP.
 * <p>
 * Cette classe contient les constantes de chaînes de caractères correspondant
 * aux réponses standards définies par la RFC 959, facilitant la maintenance
 * et la cohérence des messages envoyés au client.
 * </p>
 * * @author Bouchra BOUMAMA
 * @version 1.0
 */
public final class FTPCode {

    /** 125 : Canal de données déjà ouvert ; début du transfert. */
    public static final String C125 = "125 Data channel already open; transfer has begun.";

    /** 150 : État du fichier OK ; ouverture prochaine de la connexion de données. */
    public static final String C150 = "150 File status okay; about to open data connection.";

    /** 200 : Commande réussie. */
    public static final String C200 = "200 Command successful.";

    /** 215 : Information système (Type UNIX avec des octets de 8 bits). */
    public static final String C215 = "215 UNIX Type: L8";

    /** 220 : Service prêt pour un nouvel utilisateur (Message d'accueil). */
    public static final String C220 = "220 Welcome to My FTP Server";

    /** 221 : Fermeture de la connexion de contrôle (Déconnexion). */
    public static final String C221 = "221 Goodbye";

    /** 226 : Fermeture de la connexion de données. Transfert réussi. */
    public static final String C226 = "226 Closing data connection. Transfer complete.";

    /** 227 : Passage en mode passif (h1,h2,h3,h4,p1,p2). */
    public static final String C227 = "227 Entering Passive Mode";

    /** 250 : Action sur le fichier demandée accomplie avec succès. */
    public static final String C250 = "250 Success";

    /** 257 : Chemin créé ou affiché (utilisé par PWD). */
    public static final String C257 = "257 / is current directory ";

    /** 230 : Utilisateur connecté, fin du processus d'authentification. */
    public static final String C230 = "230 Open session.";

    /** 331 : Nom d'utilisateur OK, mot de passe requis. */
    public static final String C331 = "331 User name okay, need password";

    /** 350 : Action sur le fichier en attente d'informations complémentaires (RNFR vers RNTO). */
    public static final String C350 = "350 File exists, ready for destination name.d";

    /** 425 : Impossible d'ouvrir la connexion de données. */
    public static final String C425 = "425 Can't open data connection";

    /** 451 : Action interrompue : erreur locale dans le traitement. */
    public static final String C451 = "451 Service interrupted. Local processing error.";

    /** 501 : Erreur de syntaxe dans les paramètres ou les arguments. */
    public static final String C501 = "501 Syntax error in parameters or arguments.";

    /** 502 : Commande non implémentée par ce serveur. */
    public static final String C502 = "502 Commande not implemented";

    /** 503 : Mauvaise séquence de commandes (ex: RNTO avant RNFR). */
    public static final String C503 = "503 Bad sequence of commands.";

    /** 504 : Commande non implémentée pour ce paramètre spécifique (utilisé par TYPE). */
    public static final String C504 = "504 Command not implemented for that parameter.";

    /** 530 : Non connecté (Nécessite USER et PASS). */
    public static final String C530 = "530 Please login with USER and PASS";

    /** 550 : Action non exécutée (Fichier non trouvé, pas d'accès, etc.). */
    public static final String C550 = "550 Erreur";

}