package fil.sr1.tree.utils;

/**
 * Énumération des différents types de documents gérés par l'explorateur.
 * @author Bouchra Bouamama
 */
public enum DocumentType {
    /** Représente un fichier régulier. */
    FILE,
    /** Représente un répertoire. */
    DIRECTORY,
    /** Représente un lien symbolique. */
    LINK;
}

