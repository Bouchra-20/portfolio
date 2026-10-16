package fil.sr1.tree.utils;

/**
 * Représente un document distant (fichier, répertoire ou lien) récupéré via FTP.
 * Cette classe est responsable du parsing des lignes retournées par la commande LIST.
 * * @author Bouchra Bouamama
 */
public class Document {

    /** Le nom du document (fichier ou dossier). */
    protected String name;
    /** Le type du document défini par l'énumération DocumentType. */
    protected DocumentType type;

    /**
     * @param name le nom du fichier ou dossier.
     * @param type Le type du document (FILE, DIRECTORY, LINK).
     */
    public Document(String name, DocumentType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Parse une ligne brute renvoyée par le serveur (format UNIX ls -l) pour créer un Document.
     * Détermine le type via le premier caractère et extrait le nom en fin de ligne.
     * @param data La ligne de texte brute provenant du flux de données FTP.
     * @return Une instance de Document initialisée.
     */
    public static Document getDocumentFromData(String data) {
        DocumentType type;
        switch (data.charAt(0)) {
            case ('d') :
                type = DocumentType.DIRECTORY;
                break;
            case ('l') :
                type = DocumentType.LINK;
                break;
            default :
                type = DocumentType.FILE;
        }
        String[] dataArray = data.split("\\s+");
        String name = "";
        if (type == DocumentType.LINK) {
            name = dataArray[dataArray.length - 3] + " -> ";
        }
        name += dataArray[dataArray.length - 1];
        return new Document(name , type);
    }

    /**
     * @return Le type du document.
     */
    public DocumentType type(){
        return this.type;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
