package fil.sr1.TreeFtp;

import static org.junit.Assert.*;

import fil.sr1.tree.utils.DocumentType;
import fil.sr1.tree.utils.Document;
import org.junit.Test;

/**
 * Classe de test pour la classe Document.
 * Vérifie le bon parsing des chaînes de caractères retournées par la commande LIST.
 * @author Bouchra Bouamama
 */
public class DocumentTest {

    /**
     * Teste la création d'un document de type fichier simple.
     * Vérifie que le nom et le type FILE sont correctement extraits.
     */
    @Test
    public void testGetDocumentFromFile() {
        String fileData = "-rw-r--r--    1 ftp      ftp           545 Oct 01 12:00 index.html";
        Document doc = Document.getDocumentFromData(fileData);

        assertNotNull("Le document ne devrait pas être null", doc);
        assertEquals("Le nom extrait est incorrect", "index.html", doc.toString());
        assertEquals("Le type devrait être FILE", DocumentType.FILE, doc.type());
    }

    /**
     * Teste la création d'un document de type répertoire.
     * Vérifie que le premier caractère 'd' est bien interprété comme DIRECTORY.
     */
    @Test
    public void testGetDocumentFromDirectory() {
        String dirData = "drwxr-xr-x    2 ftp      ftp          4096 Jan 15 10:00 debian";
        Document doc = Document.getDocumentFromData(dirData);

        assertEquals("Le nom du dossier est incorrect", "debian", doc.toString());
        assertEquals("Le type devrait être DIRECTORY", DocumentType.DIRECTORY, doc.type());
    }

    /**
     * Teste la gestion des liens symboliques.
     * Vérifie que la flèche '->' est traitée sans faire planter le parsing.
     */
    @Test
    public void testGetDocumentFromLink() {
        String linkData = "lrwxrwxrwx    1 ftp      ftp             7 Mar 12 15:30 stable -> debian";
        Document doc = Document.getDocumentFromData(linkData);

        assertEquals("Le type devrait être LINK", DocumentType.LINK, doc.type());
        assertTrue("Le nom devrait contenir la flèche du lien", doc.toString().contains("->"));
    }
}